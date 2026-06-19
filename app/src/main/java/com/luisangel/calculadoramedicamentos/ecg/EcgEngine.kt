package com.luisangel.calculadoramedicamentos.ecg

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

enum class EcgSex(val label: String) {
    MALE("Hombre"),
    FEMALE("Mujer")
}

enum class EcgPaperSpeed(val label: String, val mmPerSecond: Double) {
    SPEED_25("25 mm/s", 25.0),
    SPEED_50("50 mm/s", 50.0)
}

enum class EcgAxisMethod(val label: String) {
    LEAD_I_AVF("DI + aVF"),
    LEAD_I_III("DI + DIII")
}

enum class EcgLeadGroup(val label: String) {
    V2_V3("V2-V3"),
    OTHER_CONTIGUOUS("Otras derivaciones contiguas")
}

data class EcgRateResult(
    val bpm: Double,
    val rrMs: Double,
    val interpretation: String
)

data class QtcResult(
    val bazettMs: Double,
    val fridericiaMs: Double,
    val framinghamMs: Double,
    val hodgesMs: Double,
    val rrSeconds: Double,
    val interpretation: String,
    val warning: String?
)

data class AxisResult(
    val degrees: Double,
    val category: String,
    val note: String
)

data class LvhResult(
    val sokolowLyonMm: Double?,
    val sokolowPositive: Boolean?,
    val cornellVoltageMm: Double?,
    val cornellVoltagePositive: Boolean?,
    val cornellProduct: Double?,
    val cornellProductPositive: Boolean?,
    val interpretation: String
)

data class StElevationResult(
    val thresholdMm: Double,
    val meetsCriteria: Boolean,
    val interpretation: String
)

data class EcgSummaryInput(
    val ageYears: Int?,
    val sex: EcgSex,
    val heartRateBpm: Double?,
    val prMs: Double?,
    val qrsMs: Double?,
    val qtMs: Double?,
    val axisDegrees: Double?,
    val rhythmSinus: Boolean,
    val rhythmRegular: Boolean,
    val stElevationResult: StElevationResult?,
    val lvhResult: LvhResult?
)

object EcgCalculator {
    fun smallSquareMs(paperSpeed: EcgPaperSpeed): Double =
        1000.0 / paperSpeed.mmPerSecond

    fun largeSquareMs(paperSpeed: EcgPaperSpeed): Double =
        smallSquareMs(paperSpeed) * 5.0

    fun millisecondsFromSmallSquares(
        smallSquares: Double,
        paperSpeed: EcgPaperSpeed = EcgPaperSpeed.SPEED_25
    ): Double = smallSquares * smallSquareMs(paperSpeed)

    fun millisecondsFromLargeSquares(
        largeSquares: Double,
        paperSpeed: EcgPaperSpeed = EcgPaperSpeed.SPEED_25
    ): Double = largeSquares * largeSquareMs(paperSpeed)

    fun rateFromRrMs(rrMs: Double): Result<EcgRateResult> = runCatching {
        require(rrMs > 0.0) { "El intervalo RR debe ser mayor de 0 ms." }
        rateFromBpm(60000.0 / rrMs)
    }

    fun rateFromRrSeconds(rrSeconds: Double): Result<EcgRateResult> =
        rateFromRrMs(rrSeconds * 1000.0)

    fun rateFromLargeSquares(
        largeSquares: Double,
        paperSpeed: EcgPaperSpeed = EcgPaperSpeed.SPEED_25
    ): Result<EcgRateResult> = runCatching {
        require(largeSquares > 0.0) { "Los cuadros grandes deben ser mayores de 0." }
        val rrMs = millisecondsFromLargeSquares(largeSquares, paperSpeed)
        rateFromRrMs(rrMs).getOrThrow()
    }

    fun rateFromSmallSquares(
        smallSquares: Double,
        paperSpeed: EcgPaperSpeed = EcgPaperSpeed.SPEED_25
    ): Result<EcgRateResult> = runCatching {
        require(smallSquares > 0.0) { "Los cuadros pequeños deben ser mayores de 0." }
        val rrMs = millisecondsFromSmallSquares(smallSquares, paperSpeed)
        rateFromRrMs(rrMs).getOrThrow()
    }

    fun rateFromTenSecondStrip(qrsCount: Int): Result<EcgRateResult> = runCatching {
        require(qrsCount > 0) { "El número de complejos QRS debe ser mayor de 0." }
        rateFromBpm(qrsCount * 6.0)
    }

    fun rrMsFromHeartRate(bpm: Double): Result<Double> = runCatching {
        require(bpm > 0.0) { "La frecuencia cardiaca debe ser mayor de 0." }
        60000.0 / bpm
    }

    fun qtcFromHeartRate(qtMs: Double, heartRateBpm: Double): Result<QtcResult> = runCatching {
        require(qtMs > 0.0) { "El QT debe ser mayor de 0 ms." }
        require(heartRateBpm > 0.0) { "La frecuencia cardiaca debe ser mayor de 0." }
        val rrSeconds = 60.0 / heartRateBpm
        qtc(qtMs, rrSeconds, heartRateBpm)
    }

    fun qtcFromRrMs(qtMs: Double, rrMs: Double): Result<QtcResult> = runCatching {
        require(qtMs > 0.0) { "El QT debe ser mayor de 0 ms." }
        require(rrMs > 0.0) { "El RR debe ser mayor de 0 ms." }
        val rrSeconds = rrMs / 1000.0
        val heartRate = 60.0 / rrSeconds
        qtc(qtMs, rrSeconds, heartRate)
    }

    fun axisFromLeadIAndAvf(leadI: Double, avf: Double): AxisResult =
        axisFromComponents(leadI, avf, "Cálculo vectorial con DI como eje X y aVF como eje Y.")

    fun axisFromLeadIAndIII(leadI: Double, leadIII: Double): AxisResult {
        val y = (leadI + (2.0 * leadIII)) / sqrt(3.0)
        return axisFromComponents(
            x = leadI,
            y = y,
            note = "Cálculo aproximado por derivaciones de Einthoven usando DI y DIII."
        )
    }

    fun calculateAxis(
        method: EcgAxisMethod,
        firstAmplitude: Double,
        secondAmplitude: Double
    ): AxisResult = when (method) {
        EcgAxisMethod.LEAD_I_AVF -> axisFromLeadIAndAvf(firstAmplitude, secondAmplitude)
        EcgAxisMethod.LEAD_I_III -> axisFromLeadIAndIII(firstAmplitude, secondAmplitude)
    }

    fun lvh(
        sex: EcgSex,
        sV1Mm: Double?,
        rV5Mm: Double?,
        rV6Mm: Double?,
        rAvlMm: Double?,
        sV3Mm: Double?,
        qrsDurationMs: Double?
    ): LvhResult {
        val sokolow = if (sV1Mm != null && (rV5Mm != null || rV6Mm != null)) {
            sV1Mm + max(rV5Mm ?: 0.0, rV6Mm ?: 0.0)
        } else {
            null
        }
        val sokolowPositive = sokolow?.let { it >= 35.0 }

        val cornell = if (rAvlMm != null && sV3Mm != null) rAvlMm + sV3Mm else null
        val cornellThreshold = if (sex == EcgSex.MALE) 28.0 else 20.0
        val cornellPositive = cornell?.let { it > cornellThreshold }

        val product = if (cornell != null && qrsDurationMs != null) {
            val adjustedCornell = cornell + if (sex == EcgSex.FEMALE) 6.0 else 0.0
            adjustedCornell * qrsDurationMs
        } else {
            null
        }
        val productPositive = product?.let { it > 2440.0 }

        val positives = listOfNotNull(
            sokolowPositive?.takeIf { it }?.let { "Sokolow-Lyon" },
            cornellPositive?.takeIf { it }?.let { "Cornell voltaje" },
            productPositive?.takeIf { it }?.let { "Cornell producto" }
        )

        val interpretation = when {
            positives.isEmpty() && listOf(sokolow, cornell, product).all { it == null } ->
                "Completa los voltajes para valorar criterios eléctricos de HVI."
            positives.isEmpty() ->
                "Sin criterios eléctricos de HVI por los datos capturados."
            else ->
                "Criterios eléctricos de HVI positivos: ${positives.joinToString()} . Correlacionar con clínica e imagen."
        }

        return LvhResult(
            sokolowLyonMm = sokolow,
            sokolowPositive = sokolowPositive,
            cornellVoltageMm = cornell,
            cornellVoltagePositive = cornellPositive,
            cornellProduct = product,
            cornellProductPositive = productPositive,
            interpretation = interpretation.replace(" }", "}").replace(" .", ".")
        )
    }

    fun stElevationCriteria(
        ageYears: Int,
        sex: EcgSex,
        leadGroup: EcgLeadGroup,
        elevationMm: Double,
        contiguousLeads: Boolean
    ): Result<StElevationResult> = runCatching {
        require(ageYears >= 0) { "La edad no puede ser negativa." }
        require(elevationMm >= 0.0) { "La elevación del ST no puede ser negativa." }
        val threshold = when (leadGroup) {
            EcgLeadGroup.OTHER_CONTIGUOUS -> 1.0
            EcgLeadGroup.V2_V3 -> when (sex) {
                EcgSex.FEMALE -> 1.5
                EcgSex.MALE -> if (ageYears < 40) 2.5 else 2.0
            }
        }
        val positive = contiguousLeads && elevationMm >= threshold
        StElevationResult(
            thresholdMm = threshold,
            meetsCriteria = positive,
            interpretation = when {
                !contiguousLeads -> "No cumple criterio porque se requieren al menos 2 derivaciones contiguas."
                positive -> "Cumple umbral electrocardiográfico de elevación del ST para el grupo seleccionado. Valorar contexto clínico, cambios recíprocos y ruta de SCA/IAMCEST."
                else -> "No alcanza el umbral de elevación del ST para el grupo seleccionado."
            }
        )
    }

    fun intervalInterpretation(prMs: Double?, qrsMs: Double?): List<String> = buildList {
        prMs?.let {
            add(
                when {
                    it < 120.0 -> "PR corto (<120 ms)."
                    it <= 200.0 -> "PR dentro de rango adulto habitual (120-200 ms)."
                    else -> "PR prolongado (>200 ms), compatible con bloqueo AV de primer grado si cada P conduce."
                }
            )
        }
        qrsMs?.let {
            add(
                when {
                    it < 110.0 -> "QRS estrecho."
                    it < 120.0 -> "QRS limítrofe."
                    else -> "QRS ancho (≥120 ms), valorar bloqueo de rama, ritmo ventricular o preexcitación según morfología."
                }
            )
        }
    }

    fun buildSummary(input: EcgSummaryInput): String {
        val lines = mutableListOf<String>()
        lines += "Interpretación preliminar de ECG"
        input.ageYears?.let { lines += "Paciente: ${it} años, ${input.sex.label.lowercase()}." }
        input.heartRateBpm?.let { bpm ->
            val rr = rrMsFromHeartRate(bpm).getOrNull()
            lines += "Frecuencia cardiaca: ${round(bpm)} lpm${rr?.let { " · RR ${round(it)} ms" } ?: ""}. ${rateInterpretation(bpm)}"
        }
        lines += when {
            input.rhythmSinus && input.rhythmRegular -> "Ritmo: sinusal regular por datos capturados."
            input.rhythmSinus -> "Ritmo: sinusal, con irregularidad reportada."
            input.rhythmRegular -> "Ritmo: regular, no confirmado como sinusal."
            else -> "Ritmo: no sinusal o irregular según datos capturados."
        }
        intervalInterpretation(input.prMs, input.qrsMs).forEach { lines += it }
        if (input.qtMs != null && input.heartRateBpm != null) {
            qtcFromHeartRate(input.qtMs, input.heartRateBpm).getOrNull()?.let { qtc ->
                lines += "QTc: Bazett ${round(qtc.bazettMs)} ms, Fridericia ${round(qtc.fridericiaMs)} ms, Framingham ${round(qtc.framinghamMs)} ms, Hodges ${round(qtc.hodgesMs)} ms. ${qtc.interpretation}"
            }
        }
        input.axisDegrees?.let { axis ->
            val normalized = normalizeAxis(axis)
            lines += "Eje eléctrico: ${round(normalized)}° · ${axisCategory(normalized)}."
        }
        input.lvhResult?.let { lvh ->
            if (lvh.sokolowLyonMm != null || lvh.cornellVoltageMm != null || lvh.cornellProduct != null) {
                lines += "HVI: ${lvh.interpretation}"
            }
        }
        input.stElevationResult?.let { st ->
            lines += "ST: ${st.interpretation}"
        }
        lines += "Nota: herramienta de apoyo. No sustituye interpretación médica ni correlación clínica."
        return lines.joinToString("\n")
    }

    fun rateInterpretation(bpm: Double): String = when {
        bpm < 60.0 -> "Bradicardia orientativa."
        bpm <= 100.0 -> "Frecuencia dentro de rango adulto habitual."
        else -> "Taquicardia orientativa."
    }

    private fun rateFromBpm(bpm: Double): EcgRateResult =
        EcgRateResult(
            bpm = bpm,
            rrMs = 60000.0 / bpm,
            interpretation = rateInterpretation(bpm)
        )

    private fun qtc(qtMs: Double, rrSeconds: Double, heartRateBpm: Double): QtcResult {
        val qtSeconds = qtMs / 1000.0
        val bazett = (qtSeconds / sqrt(rrSeconds)) * 1000.0
        val fridericia = (qtSeconds / rrSeconds.pow(1.0 / 3.0)) * 1000.0
        val framingham = (qtSeconds + 0.154 * (1.0 - rrSeconds)) * 1000.0
        val hodges = qtMs + 1.75 * (heartRateBpm - 60.0)
        val preferred = when {
            heartRateBpm < 60.0 || heartRateBpm > 100.0 -> fridericia
            else -> bazett
        }
        return QtcResult(
            bazettMs = bazett,
            fridericiaMs = fridericia,
            framinghamMs = framingham,
            hodgesMs = hodges,
            rrSeconds = rrSeconds,
            interpretation = qtcInterpretation(preferred),
            warning = if (heartRateBpm < 60.0 || heartRateBpm > 100.0) {
                "Bazett puede sobrecorregir con FC alta y subcorregir con FC baja; revisa Fridericia/Framingham."
            } else {
                null
            }
        )
    }

    private fun qtcInterpretation(qtcMs: Double): String = when {
        qtcMs >= 500.0 -> "QTc prolongado de alto riesgo orientativo (≥500 ms)."
        qtcMs > 460.0 -> "QTc prolongado orientativo."
        qtcMs >= 440.0 -> "QTc limítrofe según sexo y contexto clínico."
        else -> "QTc dentro de rango habitual orientativo."
    }

    private fun axisFromComponents(x: Double, y: Double, note: String): AxisResult {
        val degrees = normalizeAxis(atan2(y, x) * 180.0 / PI)
        return AxisResult(
            degrees = degrees,
            category = axisCategory(degrees),
            note = note
        )
    }

    private fun normalizeAxis(value: Double): Double {
        var angle = value
        while (angle > 180.0) angle -= 360.0
        while (angle <= -180.0) angle += 360.0
        return angle
    }

    private fun axisCategory(degrees: Double): String = when {
        degrees >= -30.0 && degrees <= 90.0 -> "Eje normal"
        degrees < -30.0 && degrees >= -90.0 -> "Desviación izquierda"
        degrees > 90.0 && degrees <= 180.0 -> "Desviación derecha"
        else -> "Eje extremo"
    }

    private fun round(value: Double): Int = value.roundToInt()
}
