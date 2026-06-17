package com.luisangel.calculadoramedicamentos.growth

import android.content.Context
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

enum class GrowthSex(val label: String) {
    FEMALE("Femenino"),
    MALE("Masculino")
}

enum class MeasurementMode(val label: String) {
    LENGTH("Longitud acostado"),
    HEIGHT("Talla de pie")
}

enum class GrowthIndicator(val label: String, val unit: String) {
    WEIGHT_FOR_AGE("Peso para la edad", "kg"),
    HEIGHT_FOR_AGE("Longitud/talla para la edad", "cm"),
    BMI_FOR_AGE("IMC para la edad", "kg/m²"),
    WEIGHT_FOR_HEIGHT("Peso para la longitud/talla", "kg")
}

data class ChartPoint(val x: Double, val y: Double)

data class PercentileCurve(
    val label: String,
    val percentile: Double,
    val points: List<ChartPoint>
)

data class GrowthChart(
    val title: String,
    val xLabel: String,
    val yLabel: String,
    val curves: List<PercentileCurve>,
    val patientPoint: ChartPoint,
    val xFormatter: (Double) -> String
)

data class GrowthResult(
    val indicator: GrowthIndicator,
    val measuredValue: Double,
    val zScore: Double,
    val percentile: Double,
    val interpretation: String,
    val chart: GrowthChart
)

enum class NutritionStatus {
    LOW_WEIGHT,
    EXPECTED,
    OVERWEIGHT,
    OBESITY
}

data class NutritionSummary(
    val status: NutritionStatus,
    val label: String,
    val bmiPercentile: Double,
    val overweightFromKg: Double,
    val obesityFromKg: Double,
    val reference: String
)

data class GrowthAssessment(
    val ageDays: Long,
    val ageMonths: Double,
    val ageText: String,
    val adjustedHeightCm: Double,
    val measurementAdjustment: String?,
    val bmi: Double,
    val nutritionSummary: NutritionSummary,
    val results: List<GrowthResult>,
    val warnings: List<String>
)

private data class LmsPoint(
    val x: Double,
    val l: Double,
    val m: Double,
    val s: Double
)

class GrowthEngine(context: Context) {
    private val appContext = context.applicationContext

    private val ageTables: Map<Pair<GrowthSex, GrowthIndicator>, List<LmsPoint>> by lazy {
        loadAgeTables()
    }

    private val weightHeightTables: Map<Pair<GrowthSex, MeasurementMode>, List<LmsPoint>> by lazy {
        loadWeightHeightTables()
    }

    fun assess(
        sex: GrowthSex,
        birthDate: LocalDate,
        measurementDate: LocalDate,
        weightKg: Double,
        heightCm: Double,
        measurementMode: MeasurementMode
    ): Result<GrowthAssessment> = runCatching {
        require(!measurementDate.isBefore(birthDate)) {
            "La fecha de medición no puede ser anterior al nacimiento."
        }
        require(weightKg in 0.5..300.0) { "Captura un peso válido." }
        require(heightCm in 30.0..230.0) { "Captura una longitud o talla válida." }

        val ageDays = ChronoUnit.DAYS.between(birthDate, measurementDate)
        val ageMonths = ageDays / DAYS_PER_MONTH
        require(ageMonths <= 228.0) {
            "Los estándares incluidos abarcan desde el nacimiento hasta los 19 años."
        }

        val shouldUseLength = ageDays <= 730L
        val adjustedHeight = when {
            shouldUseLength && measurementMode == MeasurementMode.HEIGHT -> heightCm + 0.7
            !shouldUseLength && ageMonths <= 60.0 && measurementMode == MeasurementMode.LENGTH -> heightCm - 0.7
            else -> heightCm
        }

        val adjustment = when {
            shouldUseLength && measurementMode == MeasurementMode.HEIGHT ->
                "Se añadieron 0.7 cm porque antes de los 24 meses el estándar usa longitud acostado."
            !shouldUseLength && ageMonths <= 60.0 && measurementMode == MeasurementMode.LENGTH ->
                "Se restaron 0.7 cm porque desde los 24 meses el estándar usa talla de pie."
            else -> null
        }

        val bmi = weightKg / (adjustedHeight / 100.0).pow(2)
        val warnings = mutableListOf<String>()
        val results = mutableListOf<GrowthResult>()

        resultForAge(
            sex = sex,
            indicator = GrowthIndicator.HEIGHT_FOR_AGE,
            ageMonths = ageMonths,
            value = adjustedHeight
        )?.let(results::add)

        if (ageMonths <= 120.0) {
            resultForAge(
                sex = sex,
                indicator = GrowthIndicator.WEIGHT_FOR_AGE,
                ageMonths = ageMonths,
                value = weightKg
            )?.let(results::add)
        } else {
            warnings += "Peso para la edad no se presenta después de los 10 años porque no distingue talla de masa corporal durante el crecimiento puberal."
        }

        resultForAge(
            sex = sex,
            indicator = GrowthIndicator.BMI_FOR_AGE,
            ageMonths = ageMonths,
            value = bmi
        )?.let(results::add)

        if (ageMonths <= 60.0) {
            val standardMode = if (shouldUseLength) MeasurementMode.LENGTH else MeasurementMode.HEIGHT
            resultForWeightHeight(
                sex = sex,
                mode = standardMode,
                measurementCm = adjustedHeight,
                weightKg = weightKg
            )?.let(results::add)
        } else {
            warnings += "Peso para la longitud/talla se calcula únicamente hasta los 5 años; después se utiliza IMC para la edad."
        }

        require(results.isNotEmpty()) {
            "No fue posible calcular percentiles con los datos capturados."
        }

        val nutritionSummary = nutritionSummary(
            sex = sex,
            ageMonths = ageMonths,
            adjustedHeightCm = adjustedHeight,
            weightKg = weightKg
        ) ?: error("No fue posible calcular la clasificación nutricional.")

        GrowthAssessment(
            ageDays = ageDays,
            ageMonths = ageMonths,
            ageText = formatAge(ageDays, birthDate, measurementDate),
            adjustedHeightCm = adjustedHeight,
            measurementAdjustment = adjustment,
            bmi = bmi,
            nutritionSummary = nutritionSummary,
            results = results,
            warnings = warnings
        )
    }


    private fun nutritionSummary(
        sex: GrowthSex,
        ageMonths: Double,
        adjustedHeightCm: Double,
        weightKg: Double
    ): NutritionSummary? {
        val table = ageTables[sex to GrowthIndicator.BMI_FOR_AGE].orEmpty()
        val lms = interpolate(table, ageMonths) ?: return null
        val heightMeters = adjustedHeightCm / 100.0
        val bmi = weightKg / heightMeters.pow(2)
        val percentile = normalCdf(zScore(bmi, lms)) * 100.0

        val overweightBmi = valueAtZ(lms, Z_P85)
        val obesityBmi = valueAtZ(lms, Z_P97)

        val status = when {
            percentile < 3.0 -> NutritionStatus.LOW_WEIGHT
            percentile < 85.0 -> NutritionStatus.EXPECTED
            percentile < 97.0 -> NutritionStatus.OVERWEIGHT
            else -> NutritionStatus.OBESITY
        }

        val label = when (status) {
            NutritionStatus.LOW_WEIGHT -> "Bajo peso"
            NutritionStatus.EXPECTED -> "Peso dentro del intervalo esperado"
            NutritionStatus.OVERWEIGHT -> "Sobrepeso"
            NutritionStatus.OBESITY -> "Obesidad"
        }

        return NutritionSummary(
            status = status,
            label = label,
            bmiPercentile = percentile,
            overweightFromKg = overweightBmi * heightMeters.pow(2),
            obesityFromKg = obesityBmi * heightMeters.pow(2),
            reference = "IMC para la edad · límites P85 y P97"
        )
    }

    private fun resultForAge(
        sex: GrowthSex,
        indicator: GrowthIndicator,
        ageMonths: Double,
        value: Double
    ): GrowthResult? {
        val table = ageTables[sex to indicator].orEmpty()
        val lms = interpolate(table, ageMonths) ?: return null
        val z = zScore(value, lms)
        val percentile = normalCdf(z) * 100.0
        val maxAge = when (indicator) {
            GrowthIndicator.WEIGHT_FOR_AGE -> if (ageMonths <= 60.0) 60.0 else 120.0
            GrowthIndicator.HEIGHT_FOR_AGE,
            GrowthIndicator.BMI_FOR_AGE -> if (ageMonths <= 60.0) 60.0 else 228.0
            GrowthIndicator.WEIGHT_FOR_HEIGHT -> 60.0
        }
        val minAge = if (ageMonths <= 60.0) 0.0 else 61.0
        val selected = table.filter { it.x in minAge..maxAge }
        val curves = standardCurves(selected, xTransform = { it / 12.0 })
        val chart = GrowthChart(
            title = indicator.label,
            xLabel = "Edad (años)",
            yLabel = indicator.unit,
            curves = curves,
            patientPoint = ChartPoint(ageMonths / 12.0, value),
            xFormatter = { formatAxisYears(it) }
        )
        return GrowthResult(
            indicator = indicator,
            measuredValue = value,
            zScore = z,
            percentile = percentile,
            interpretation = interpret(indicator, z, ageMonths),
            chart = chart
        )
    }

    private fun resultForWeightHeight(
        sex: GrowthSex,
        mode: MeasurementMode,
        measurementCm: Double,
        weightKg: Double
    ): GrowthResult? {
        val table = weightHeightTables[sex to mode].orEmpty()
        val lms = interpolate(table, measurementCm) ?: return null
        val z = zScore(weightKg, lms)
        val percentile = normalCdf(z) * 100.0
        val chart = GrowthChart(
            title = GrowthIndicator.WEIGHT_FOR_HEIGHT.label,
            xLabel = if (mode == MeasurementMode.LENGTH) "Longitud (cm)" else "Talla (cm)",
            yLabel = "kg",
            curves = standardCurves(table, xTransform = { it }),
            patientPoint = ChartPoint(measurementCm, weightKg),
            xFormatter = { String.format(Locale.US, "%.0f", it) }
        )
        return GrowthResult(
            indicator = GrowthIndicator.WEIGHT_FOR_HEIGHT,
            measuredValue = weightKg,
            zScore = z,
            percentile = percentile,
            interpretation = interpret(GrowthIndicator.WEIGHT_FOR_HEIGHT, z, 0.0),
            chart = chart
        )
    }

    private fun standardCurves(
        table: List<LmsPoint>,
        xTransform: (Double) -> Double
    ): List<PercentileCurve> {
        return CURVE_Z.map { curve ->
            PercentileCurve(
                label = curve.first,
                percentile = curve.second,
                points = table.map { point ->
                    ChartPoint(
                        x = xTransform(point.x),
                        y = valueAtZ(point, curve.third)
                    )
                }
            )
        }
    }

    private fun interpolate(table: List<LmsPoint>, x: Double): LmsPoint? {
        if (table.isEmpty() || x < table.first().x || x > table.last().x) return null
        val exact = table.binarySearchBy(x) { it.x }
        if (exact >= 0) return table[exact]
        val insertion = -exact - 1
        if (insertion <= 0 || insertion >= table.size) return null
        val left = table[insertion - 1]
        val right = table[insertion]
        val ratio = if (abs(right.x - left.x) < 1e-9) 0.0 else (x - left.x) / (right.x - left.x)
        return LmsPoint(
            x = x,
            l = left.l + (right.l - left.l) * ratio,
            m = left.m + (right.m - left.m) * ratio,
            s = left.s + (right.s - left.s) * ratio
        )
    }

    private fun zScore(value: Double, lms: LmsPoint): Double {
        return if (abs(lms.l) < 1e-9) {
            ln(value / lms.m) / lms.s
        } else {
            ((value / lms.m).pow(lms.l) - 1.0) / (lms.l * lms.s)
        }
    }

    private fun valueAtZ(lms: LmsPoint, z: Double): Double {
        return if (abs(lms.l) < 1e-9) {
            lms.m * exp(lms.s * z)
        } else {
            lms.m * (1.0 + lms.l * lms.s * z).pow(1.0 / lms.l)
        }
    }

    private fun normalCdf(z: Double): Double {
        val sign = if (z < 0) -1 else 1
        val x = abs(z) / sqrt(2.0)
        val t = 1.0 / (1.0 + 0.3275911 * x)
        val erf = 1.0 - (((((1.061405429 * t - 1.453152027) * t) + 1.421413741) * t - 0.284496736) * t + 0.254829592) * t * exp(-x * x)
        return 0.5 * (1.0 + sign * erf)
    }

    private fun interpret(indicator: GrowthIndicator, z: Double, ageMonths: Double): String {
        return when (indicator) {
            GrowthIndicator.HEIGHT_FOR_AGE -> when {
                z < -3 -> "Talla muy baja para la edad"
                z < -2 -> "Talla baja para la edad"
                z > 3 -> "Talla muy alta para la edad"
                z > 2 -> "Talla alta para la edad"
                else -> "Dentro del intervalo esperado"
            }
            GrowthIndicator.WEIGHT_FOR_AGE -> when {
                z < -3 -> "Peso muy bajo para la edad"
                z < -2 -> "Peso bajo para la edad"
                z > 2 -> "Peso alto para la edad; interpretar junto con talla e IMC"
                else -> "Dentro del intervalo esperado"
            }
            GrowthIndicator.BMI_FOR_AGE,
            GrowthIndicator.WEIGHT_FOR_HEIGHT -> when {
                z < -3 -> "Déficit ponderal grave"
                z < -1.8807936081512509 -> "Bajo peso"
                z < Z_P85 -> "Dentro del intervalo esperado"
                z < Z_P97 -> "Sobrepeso"
                else -> "Obesidad"
            }
        }
    }

    private fun formatAge(ageDays: Long, birthDate: LocalDate, measurementDate: LocalDate): String {
        val years = ChronoUnit.YEARS.between(birthDate, measurementDate)
        val afterYears = birthDate.plusYears(years)
        val months = ChronoUnit.MONTHS.between(afterYears, measurementDate)
        val afterMonths = afterYears.plusMonths(months)
        val days = ChronoUnit.DAYS.between(afterMonths, measurementDate)
        return buildList {
            if (years > 0) add("$years ${if (years == 1L) "año" else "años"}")
            if (months > 0 || years > 0) add("$months ${if (months == 1L) "mes" else "meses"}")
            add("$days ${if (days == 1L) "día" else "días"}")
        }.joinToString(" · ") + " ($ageDays días)"
    }

    private fun formatAxisYears(value: Double): String {
        return if (value < 1.0) {
            "${(value * 12).toInt()} m"
        } else if (abs(value - value.toInt()) < 0.05) {
            "${value.toInt()} a"
        } else {
            String.format(Locale.US, "%.1f a", value)
        }
    }

    private fun loadAgeTables(): Map<Pair<GrowthSex, GrowthIndicator>, List<LmsPoint>> {
        val output = mutableMapOf<Pair<GrowthSex, GrowthIndicator>, MutableList<LmsPoint>>()
        appContext.assets.open("who_age_lms.csv").bufferedReader().useLines { lines ->
            lines.drop(1).forEach { line ->
                val values = line.split(',')
                if (values.size < 6) return@forEach
                val point = LmsPoint(
                    x = values[0].toDouble(),
                    l = values[3].toDouble(),
                    m = values[4].toDouble(),
                    s = values[5].toDouble()
                )
                val sex = GrowthSex.valueOf(values[1])
                val indicator = GrowthIndicator.valueOf(values[2])
                output.getOrPut(sex to indicator) { mutableListOf() }.add(point)
            }
        }
        return output.mapValues { (_, points) -> points.sortedBy { it.x } }
    }

    private fun loadWeightHeightTables(): Map<Pair<GrowthSex, MeasurementMode>, List<LmsPoint>> {
        val output = mutableMapOf<Pair<GrowthSex, MeasurementMode>, MutableList<LmsPoint>>()
        appContext.assets.open("who_weight_height_lms.csv").bufferedReader().useLines { lines ->
            lines.drop(1).forEach { line ->
                val values = line.split(',')
                if (values.size < 6) return@forEach
                val point = LmsPoint(
                    x = values[0].toDouble(),
                    l = values[3].toDouble(),
                    m = values[4].toDouble(),
                    s = values[5].toDouble()
                )
                val sex = GrowthSex.valueOf(values[1])
                val mode = MeasurementMode.valueOf(values[2])
                output.getOrPut(sex to mode) { mutableListOf() }.add(point)
            }
        }
        return output.mapValues { (_, points) -> points.sortedBy { it.x } }
    }

    companion object {
        private const val DAYS_PER_MONTH = 365.25 / 12.0
        private const val Z_P85 = 1.0364333894937896
        private const val Z_P97 = 1.8807936081512509

        private val CURVE_Z = listOf(
            Triple("P3", 3.0, -1.8807936081512509),
            Triple("P15", 15.0, -1.0364333894937896),
            Triple("P50", 50.0, 0.0),
            Triple("P85", 85.0, 1.0364333894937896),
            Triple("P97", 97.0, 1.8807936081512509)
        )
    }
}
