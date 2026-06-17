package com.luisangel.calculadoramedicamentos.obstetrics

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

enum class UltrasoundTrimester(val label: String, val shortLabel: String) {
    FIRST("Primer trimestre", "1T"),
    SECOND("Segundo trimestre", "2T"),
    THIRD("Tercer trimestre", "3T")
}

data class LmpComparison(
    val lmpGestationalAgeDays: Int,
    val differenceDays: Int,
    val redatingThresholdDays: Int,
    val exceedsThreshold: Boolean,
    val message: String
)

data class UltrasoundDatingResult(
    val trimester: UltrasoundTrimester,
    val gestationalAgeDays: Int,
    val estimatedDueDate: LocalDate,
    val method: String,
    val measurementSummary: String,
    val expectedAccuracy: String,
    val warnings: List<String>,
    val lmpComparison: LmpComparison?
) {
    val weeks: Int get() = gestationalAgeDays / 7
    val days: Int get() = gestationalAgeDays % 7
    val gestationalAgeLabel: String get() = "$weeks semanas + $days días"
}

object UltrasoundDatingCalculator {
    fun fromCrl(
        scanDate: LocalDate,
        crlMm: Double,
        lmpDate: LocalDate? = null
    ): Result<UltrasoundDatingResult> = runCatching {
        require(crlMm.isFinite()) { "La LCC/CRL debe ser un número válido." }
        require(crlMm in 5.0..84.0) { "La LCC/CRL debe estar entre 5 y 84 mm." }
        val gestationalDays = (8.052 * sqrt(1.037 * crlMm) + 23.73).roundToInt()
        val warnings = mutableListOf<String>()
        if (crlMm > 80.0 || gestationalDays > 97) {
            warnings += "La medición está en el límite de transición a 14 semanas. Confirma la datación con biometría del segundo trimestre."
        }
        buildResult(
            trimester = UltrasoundTrimester.FIRST,
            scanDate = scanDate,
            gestationalAgeDays = gestationalDays,
            method = "LCC/CRL · Robinson-Fleming corregida",
            measurementSummary = "LCC/CRL ${formatMeasurement(crlMm)} mm",
            expectedAccuracy = "Aproximadamente ±5–7 días",
            warnings = warnings,
            lmpDate = lmpDate
        )
    }

    fun fromBiometry(
        scanDate: LocalDate,
        trimester: UltrasoundTrimester,
        bpdMm: Double? = null,
        hcMm: Double? = null,
        acMm: Double? = null,
        flMm: Double? = null,
        lmpDate: LocalDate? = null
    ): Result<UltrasoundDatingResult> = runCatching {
        require(trimester != UltrasoundTrimester.FIRST) { "Para el primer trimestre utiliza LCC/CRL." }
        validateMeasurement("DBP/BPD", bpdMm, 15.0..110.0)
        validateMeasurement("CC/HC", hcMm, 70.0..420.0)
        validateMeasurement("CA/AC", acMm, 70.0..500.0)
        validateMeasurement("LF/FL", flMm, 8.0..100.0)
        val bpd = bpdMm?.div(10.0)
        val hc = hcMm?.div(10.0)
        val ac = acMm?.div(10.0)
        val fl = flMm?.div(10.0)
        val (menstrualAgeWeeks, method) = hadlockMenstrualAgeWeeks(bpd, hc, ac, fl)
        val gestationalDays = (menstrualAgeWeeks * 7.0).roundToInt()
        require(gestationalDays in 84..294) { "La biometría produjo una edad fuera del intervalo de 12 a 42 semanas. Revisa unidades y mediciones." }
        val measurements = listOfNotNull(
            bpdMm?.let { "DBP ${formatMeasurement(it)} mm" },
            hcMm?.let { "CC ${formatMeasurement(it)} mm" },
            acMm?.let { "CA ${formatMeasurement(it)} mm" },
            flMm?.let { "LF ${formatMeasurement(it)} mm" }
        )
        val warnings = mutableListOf<String>()
        if (measurements.size == 1) warnings += "Se utilizó un solo parámetro. La estimación compuesta con varias biometrías suele ser más estable."
        when (trimester) {
            UltrasoundTrimester.SECOND -> if (gestationalDays !in 98..195) {
                warnings += "La edad calculada no corresponde al intervalo habitual del segundo trimestre (14+0 a 27+6 semanas)."
            }
            UltrasoundTrimester.THIRD -> {
                if (gestationalDays < 196) warnings += "La edad calculada es menor de 28 semanas y no corresponde al tercer trimestre seleccionado."
                warnings += "La datación del tercer trimestre es la menos precisa. No debe reemplazar una FPP establecida por un ultrasonido temprano sin valorar el contexto clínico y el crecimiento fetal."
            }
            UltrasoundTrimester.FIRST -> Unit
        }
        buildResult(
            trimester = trimester,
            scanDate = scanDate,
            gestationalAgeDays = gestationalDays,
            method = method,
            measurementSummary = measurements.joinToString(" · "),
            expectedAccuracy = accuracyLabel(gestationalDays),
            warnings = warnings,
            lmpDate = lmpDate
        )
    }

    private fun buildResult(
        trimester: UltrasoundTrimester,
        scanDate: LocalDate,
        gestationalAgeDays: Int,
        method: String,
        measurementSummary: String,
        expectedAccuracy: String,
        warnings: List<String>,
        lmpDate: LocalDate?
    ): UltrasoundDatingResult {
        require(!scanDate.isBefore(LocalDate.of(1900, 1, 1))) { "La fecha del ultrasonido no es válida." }
        return UltrasoundDatingResult(
            trimester = trimester,
            gestationalAgeDays = gestationalAgeDays,
            estimatedDueDate = scanDate.plusDays((280 - gestationalAgeDays).toLong()),
            method = method,
            measurementSummary = measurementSummary,
            expectedAccuracy = expectedAccuracy,
            warnings = warnings,
            lmpComparison = compareWithLmp(scanDate, gestationalAgeDays, lmpDate)
        )
    }

    private fun compareWithLmp(scanDate: LocalDate, gestationalAgeDays: Int, lmpDate: LocalDate?): LmpComparison? {
        if (lmpDate == null) return null
        require(!lmpDate.isAfter(scanDate)) { "La FUM no puede ser posterior al ultrasonido." }
        val lmpDays = ChronoUnit.DAYS.between(lmpDate, scanDate).toInt()
        val difference = abs(lmpDays - gestationalAgeDays)
        val threshold = redatingThresholdDays(lmpDays)
        val exceeds = difference > threshold
        return LmpComparison(
            lmpGestationalAgeDays = lmpDays,
            differenceDays = difference,
            redatingThresholdDays = threshold,
            exceedsThreshold = exceeds,
            message = if (exceeds) {
                "La diferencia supera el umbral orientativo de >$threshold días para este intervalo."
            } else {
                "La diferencia no supera el umbral orientativo de >$threshold días para este intervalo."
            }
        )
    }

    private fun redatingThresholdDays(days: Int): Int = when {
        days <= 62 -> 5
        days <= 97 -> 7
        days <= 111 -> 7
        days <= 153 -> 10
        days <= 195 -> 14
        else -> 21
    }

    private fun accuracyLabel(days: Int): String = when {
        days <= 97 -> "Aproximadamente ±5–7 días"
        days <= 153 -> "Aproximadamente ±7–10 días"
        days <= 195 -> "Aproximadamente ±10–14 días"
        else -> "Aproximadamente ±21–30 días"
    }

    private fun validateMeasurement(label: String, value: Double?, range: ClosedFloatingPointRange<Double>) {
        if (value == null) return
        require(value.isFinite()) { "$label debe ser un número válido." }
        require(value in range) { "$label está fuera del intervalo admitido. Revisa que esté expresado en milímetros." }
    }

    private fun hadlockMenstrualAgeWeeks(bpd: Double?, hc: Double?, ac: Double?, fl: Double?): Pair<Double, String> {
        require(listOf(bpd, hc, ac, fl).any { it != null }) { "Captura al menos una biometría fetal." }
        return when {
            bpd != null && hc != null && ac != null && fl != null ->
                (10.85 + 0.060 * hc * fl + 0.6700 * bpd + 0.1680 * ac) to "Hadlock 1984 · DBP + CC + CA + LF"
            hc != null && ac != null && fl != null ->
                (10.33 + 0.031 * hc * fl + 0.3610 * hc + 0.0298 * ac * fl) to "Hadlock 1984 · CC + CA + LF"
            bpd != null && ac != null && fl != null ->
                (10.61 + 0.175 * bpd * fl + 0.2970 * ac + 0.7100 * fl) to "Hadlock 1984 · DBP + CA + LF"
            bpd != null && hc != null && fl != null ->
                (11.38 + 0.070 * hc * fl + 0.9800 * bpd) to "Hadlock 1984 · DBP + CC + LF"
            bpd != null && hc != null && ac != null ->
                (10.58 + 0.005 * hc.pow(2) + 0.3635 * ac + 0.02864 * bpd * ac) to "Hadlock 1984 · DBP + CC + CA"
            hc != null && fl != null ->
                (11.19 + 0.070 * hc * fl + 0.2630 * hc) to "Hadlock 1984 · CC + LF"
            bpd != null && fl != null ->
                (10.50 + 0.197 * bpd * fl + 0.9500 * fl + 0.7300 * bpd) to "Hadlock 1984 · DBP + LF"
            ac != null && fl != null ->
                (10.47 + 0.442 * ac + 0.3140 * fl.pow(2) - 0.0121 * fl.pow(3)) to "Hadlock 1984 · CA + LF"
            hc != null && ac != null ->
                (10.31 + 0.012 * hc.pow(2) + 0.3850 * ac) to "Hadlock 1984 · CC + CA"
            bpd != null && hc != null ->
                (10.32 + 0.009 * hc.pow(2) + 1.3200 * bpd + 0.00012 * hc.pow(3)) to "Hadlock 1984 · DBP + CC"
            bpd != null && ac != null ->
                (9.57 + 0.524 * ac + 0.1220 * bpd.pow(2)) to "Hadlock 1984 · DBP + CA"
            bpd != null ->
                (9.54 + 1.482 * bpd + 0.1676 * bpd.pow(2)) to "Hadlock 1984 · DBP"
            hc != null ->
                (8.96 + 0.540 * hc + 0.0003 * hc.pow(3)) to "Hadlock 1984 · CC"
            ac != null ->
                (8.14 + 0.753 * ac + 0.0036 * ac.pow(2)) to "Hadlock 1984 · CA"
            fl != null ->
                (10.35 + 2.460 * fl + 0.170 * fl.pow(2)) to "Hadlock 1984 · LF"
            else -> error("No fue posible seleccionar una ecuación de biometría.")
        }
    }

    private fun formatMeasurement(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString()
        else String.format(java.util.Locale.US, "%.1f", value)
}
