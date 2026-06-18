package com.luisangel.calculadoramedicamentos.renal

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

enum class RenalSex(val label: String) {
    MALE("Hombre"),
    FEMALE("Mujer")
}

enum class CreatinineUnit(val label: String) {
    MG_DL("mg/dL"),
    UMOL_L("µmol/L")
}

enum class AcrUnit(val label: String) {
    MG_G("mg/g"),
    MG_MMOL("mg/mmol")
}

enum class RenalMethod(
    val label: String,
    val shortLabel: String,
    val description: String,
    val requiresCreatinine: Boolean,
    val requiresCystatinC: Boolean,
    val requiresWeight: Boolean,
    val requiresHeight: Boolean,
    val isIndexedGfr: Boolean
) {
    CKD_EPI_2021_CREATININE(
        label = "CKD-EPI 2021 · creatinina",
        shortLabel = "CKD-EPI 2021 Cr",
        description = "Ecuación actual sin coeficiente racial para adultos.",
        requiresCreatinine = true,
        requiresCystatinC = false,
        requiresWeight = false,
        requiresHeight = false,
        isIndexedGfr = true
    ),
    CKD_EPI_2021_CREATININE_CYSTATIN(
        label = "CKD-EPI 2021 · creatinina + cistatina C",
        shortLabel = "CKD-EPI 2021 Cr-Cys",
        description = "Estimación combinada, generalmente más precisa cuando la cistatina C está disponible.",
        requiresCreatinine = true,
        requiresCystatinC = true,
        requiresWeight = false,
        requiresHeight = false,
        isIndexedGfr = true
    ),
    CKD_EPI_2012_CYSTATIN(
        label = "CKD-EPI 2012 · cistatina C",
        shortLabel = "CKD-EPI 2012 Cys",
        description = "Estimación basada únicamente en cistatina C.",
        requiresCreatinine = false,
        requiresCystatinC = true,
        requiresWeight = false,
        requiresHeight = false,
        isIndexedGfr = true
    ),
    MDRD_4_IDMS(
        label = "MDRD-4 IDMS · legado",
        shortLabel = "MDRD-4",
        description = "Ecuación histórica con creatinina estandarizada; se muestra para comparación.",
        requiresCreatinine = true,
        requiresCystatinC = false,
        requiresWeight = false,
        requiresHeight = false,
        isIndexedGfr = true
    ),
    COCKCROFT_GAULT(
        label = "Cockcroft-Gault · depuración de creatinina",
        shortLabel = "Cockcroft-Gault",
        description = "Estima depuración de creatinina. La app también la indexa a 1.73 m² para ubicarla de forma orientativa en categorías G.",
        requiresCreatinine = true,
        requiresCystatinC = false,
        requiresWeight = true,
        requiresHeight = true,
        isIndexedGfr = false
    )
}

enum class GfrCategory(
    val label: String,
    val rangeLabel: String,
    val description: String
) {
    G1("G1", "≥90", "Normal o alta"),
    G2("G2", "60–89", "Levemente disminuida"),
    G3A("G3a", "45–59", "Leve a moderadamente disminuida"),
    G3B("G3b", "30–44", "Moderada a gravemente disminuida"),
    G4("G4", "15–29", "Gravemente disminuida"),
    G5("G5", "<15", "Falla renal")
}

enum class AlbuminuriaCategory(
    val label: String,
    val rangeLabel: String,
    val description: String
) {
    A1("A1", "<30 mg/g · <3 mg/mmol", "Normal a levemente aumentada"),
    A2("A2", "30–300 mg/g · 3–30 mg/mmol", "Moderadamente aumentada"),
    A3("A3", ">300 mg/g · >30 mg/mmol", "Gravemente aumentada")
}

enum class CkdRiskLevel(val label: String) {
    LOW("Riesgo bajo"),
    MODERATE("Riesgo moderadamente aumentado"),
    HIGH("Riesgo alto"),
    VERY_HIGH("Riesgo muy alto")
}

data class RenalInput(
    val method: RenalMethod,
    val ageYears: Int,
    val sex: RenalSex,
    val creatinine: Double? = null,
    val creatinineUnit: CreatinineUnit = CreatinineUnit.MG_DL,
    val cystatinCmgL: Double? = null,
    val weightKg: Double? = null,
    val heightCm: Double? = null,
    val acr: Double? = null,
    val acrUnit: AcrUnit = AcrUnit.MG_G
)

data class RenalResult(
    val method: RenalMethod,
    val primaryLabel: String,
    val primaryValue: Double,
    val primaryUnit: String,
    val indexedValue: Double?,
    val bodySurfaceArea: Double?,
    val gfrCategory: GfrCategory?,
    val albuminuriaCategory: AlbuminuriaCategory?,
    val riskLevel: CkdRiskLevel?,
    val warnings: List<String>
)

object RenalCalculator {
    fun calculate(input: RenalInput): Result<RenalResult> = runCatching {
        require(input.ageYears in 18..120) {
            "La calculadora renal de adultos requiere una edad entre 18 y 120 años."
        }

        val creatinineMgDl = input.creatinine?.let {
            require(it.isFinite() && it > 0.0) { "La creatinina debe ser mayor que cero." }
            when (input.creatinineUnit) {
                CreatinineUnit.MG_DL -> it
                CreatinineUnit.UMOL_L -> it / 88.4
            }
        }

        val cystatin = input.cystatinCmgL?.also {
            require(it.isFinite() && it > 0.0) { "La cistatina C debe ser mayor que cero." }
        }

        val rawValue: Double
        val indexedValue: Double?
        val bsa: Double?
        val primaryLabel: String
        val primaryUnit: String
        val warnings = mutableListOf<String>()

        when (input.method) {
            RenalMethod.CKD_EPI_2021_CREATININE -> {
                rawValue = ckdEpi2021Creatinine(
                    age = input.ageYears,
                    sex = input.sex,
                    creatinineMgDl = requireNotNull(creatinineMgDl) { "Captura la creatinina." }
                )
                indexedValue = rawValue
                bsa = null
                primaryLabel = "TFGe CKD-EPI 2021"
                primaryUnit = "mL/min/1.73 m²"
            }

            RenalMethod.CKD_EPI_2021_CREATININE_CYSTATIN -> {
                rawValue = ckdEpi2021CreatinineCystatin(
                    age = input.ageYears,
                    sex = input.sex,
                    creatinineMgDl = requireNotNull(creatinineMgDl) { "Captura la creatinina." },
                    cystatinCmgL = requireNotNull(cystatin) { "Captura la cistatina C." }
                )
                indexedValue = rawValue
                bsa = null
                primaryLabel = "TFGe CKD-EPI 2021 Cr-Cys"
                primaryUnit = "mL/min/1.73 m²"
            }

            RenalMethod.CKD_EPI_2012_CYSTATIN -> {
                rawValue = ckdEpi2012Cystatin(
                    age = input.ageYears,
                    sex = input.sex,
                    cystatinCmgL = requireNotNull(cystatin) { "Captura la cistatina C." }
                )
                indexedValue = rawValue
                bsa = null
                primaryLabel = "TFGe CKD-EPI 2012 Cys"
                primaryUnit = "mL/min/1.73 m²"
            }

            RenalMethod.MDRD_4_IDMS -> {
                rawValue = mdrd4Idms(
                    age = input.ageYears,
                    sex = input.sex,
                    creatinineMgDl = requireNotNull(creatinineMgDl) { "Captura la creatinina." }
                )
                indexedValue = rawValue
                bsa = null
                primaryLabel = "TFGe MDRD-4 IDMS"
                primaryUnit = "mL/min/1.73 m²"
                warnings += "MDRD-4 es una ecuación de legado y suele ser menos precisa a filtrados altos."
            }

            RenalMethod.COCKCROFT_GAULT -> {
                val weight = requireNotNull(input.weightKg) { "Captura el peso." }
                val height = requireNotNull(input.heightCm) { "Captura la talla." }
                require(weight.isFinite() && weight in 20.0..350.0) { "El peso debe estar entre 20 y 350 kg." }
                require(height.isFinite() && height in 100.0..230.0) { "La talla debe estar entre 100 y 230 cm." }
                val scr = requireNotNull(creatinineMgDl) { "Captura la creatinina." }
                rawValue = cockcroftGault(
                    age = input.ageYears,
                    sex = input.sex,
                    creatinineMgDl = scr,
                    weightKg = weight
                )
                bsa = mostellerBsa(heightCm = height, weightKg = weight)
                indexedValue = rawValue * 1.73 / bsa
                primaryLabel = "Depuración estimada de creatinina"
                primaryUnit = "mL/min"
                warnings += "Cockcroft-Gault estima depuración de creatinina, no TFGe. La categoría G usa aquí el valor indexado a 1.73 m² solo como orientación."
            }
        }

        require(rawValue.isFinite() && rawValue > 0.0) {
            "No fue posible obtener un resultado válido. Revisa los datos."
        }

        val classificationValue = requireNotNull(indexedValue) {
            "No fue posible indexar el resultado para clasificación."
        }
        val gCategory = classifyGfr(classificationValue)
        val albuminuriaCategory = input.acr?.let { value ->
            require(value.isFinite() && value >= 0.0) { "El cociente albúmina/creatinina no puede ser negativo." }
            classifyAlbuminuria(value, input.acrUnit)
        }
        val risk = if (albuminuriaCategory != null) {
            combinedRisk(gCategory, albuminuriaCategory)
        } else {
            null
        }

        warnings += "Un resultado aislado no demuestra enfermedad renal crónica; la cronicidad requiere al menos 3 meses o evidencia clínica equivalente."
        if (albuminuriaCategory == null) {
            warnings += "Sin ACR solo se muestra la categoría G; no se calcula el riesgo combinado G-A."
        }
        if (
            gCategory in setOf(GfrCategory.G1, GfrCategory.G2) &&
            (albuminuriaCategory == null || albuminuriaCategory == AlbuminuriaCategory.A1)
        ) {
            warnings += "G1 o G2 no establecen ERC por sí solos si no existen otros marcadores de daño renal."
        }

        RenalResult(
            method = input.method,
            primaryLabel = primaryLabel,
            primaryValue = rawValue,
            primaryUnit = primaryUnit,
            indexedValue = indexedValue,
            bodySurfaceArea = bsa,
            gfrCategory = gCategory,
            albuminuriaCategory = albuminuriaCategory,
            riskLevel = risk,
            warnings = warnings
        )
    }

    fun ckdEpi2021Creatinine(
        age: Int,
        sex: RenalSex,
        creatinineMgDl: Double
    ): Double {
        val kappa = if (sex == RenalSex.FEMALE) 0.7 else 0.9
        val alpha = if (sex == RenalSex.FEMALE) -0.241 else -0.302
        val ratio = creatinineMgDl / kappa
        val femaleFactor = if (sex == RenalSex.FEMALE) 1.012 else 1.0
        return 142.0 *
            min(ratio, 1.0).pow(alpha) *
            max(ratio, 1.0).pow(-1.200) *
            0.9938.pow(age.toDouble()) *
            femaleFactor
    }

    fun ckdEpi2021CreatinineCystatin(
        age: Int,
        sex: RenalSex,
        creatinineMgDl: Double,
        cystatinCmgL: Double
    ): Double {
        val kappa = if (sex == RenalSex.FEMALE) 0.7 else 0.9
        val alpha = if (sex == RenalSex.FEMALE) -0.219 else -0.144
        val creatinineRatio = creatinineMgDl / kappa
        val cystatinRatio = cystatinCmgL / 0.8
        val femaleFactor = if (sex == RenalSex.FEMALE) 0.963 else 1.0
        return 135.0 *
            min(creatinineRatio, 1.0).pow(alpha) *
            max(creatinineRatio, 1.0).pow(-0.544) *
            min(cystatinRatio, 1.0).pow(-0.323) *
            max(cystatinRatio, 1.0).pow(-0.778) *
            0.9961.pow(age.toDouble()) *
            femaleFactor
    }

    fun ckdEpi2012Cystatin(
        age: Int,
        sex: RenalSex,
        cystatinCmgL: Double
    ): Double {
        val ratio = cystatinCmgL / 0.8
        val femaleFactor = if (sex == RenalSex.FEMALE) 0.932 else 1.0
        return 133.0 *
            min(ratio, 1.0).pow(-0.499) *
            max(ratio, 1.0).pow(-1.328) *
            0.996.pow(age.toDouble()) *
            femaleFactor
    }

    fun mdrd4Idms(
        age: Int,
        sex: RenalSex,
        creatinineMgDl: Double
    ): Double {
        val femaleFactor = if (sex == RenalSex.FEMALE) 0.742 else 1.0
        return 175.0 *
            creatinineMgDl.pow(-1.154) *
            age.toDouble().pow(-0.203) *
            femaleFactor
    }

    fun cockcroftGault(
        age: Int,
        sex: RenalSex,
        creatinineMgDl: Double,
        weightKg: Double
    ): Double {
        val femaleFactor = if (sex == RenalSex.FEMALE) 0.85 else 1.0
        return ((140.0 - age) * weightKg) /
            (72.0 * creatinineMgDl) *
            femaleFactor
    }

    fun mostellerBsa(heightCm: Double, weightKg: Double): Double =
        sqrt(heightCm * weightKg / 3600.0)

    fun classifyGfr(value: Double): GfrCategory = when {
        value >= 90.0 -> GfrCategory.G1
        value >= 60.0 -> GfrCategory.G2
        value >= 45.0 -> GfrCategory.G3A
        value >= 30.0 -> GfrCategory.G3B
        value >= 15.0 -> GfrCategory.G4
        else -> GfrCategory.G5
    }

    fun classifyAlbuminuria(
        value: Double,
        unit: AcrUnit
    ): AlbuminuriaCategory = when (unit) {
        AcrUnit.MG_G -> when {
            value < 30.0 -> AlbuminuriaCategory.A1
            value <= 300.0 -> AlbuminuriaCategory.A2
            else -> AlbuminuriaCategory.A3
        }
        AcrUnit.MG_MMOL -> when {
            value < 3.0 -> AlbuminuriaCategory.A1
            value <= 30.0 -> AlbuminuriaCategory.A2
            else -> AlbuminuriaCategory.A3
        }
    }

    fun combinedRisk(
        gfr: GfrCategory,
        albuminuria: AlbuminuriaCategory
    ): CkdRiskLevel = when (gfr) {
        GfrCategory.G1,
        GfrCategory.G2 -> when (albuminuria) {
            AlbuminuriaCategory.A1 -> CkdRiskLevel.LOW
            AlbuminuriaCategory.A2 -> CkdRiskLevel.MODERATE
            AlbuminuriaCategory.A3 -> CkdRiskLevel.HIGH
        }
        GfrCategory.G3A -> when (albuminuria) {
            AlbuminuriaCategory.A1 -> CkdRiskLevel.MODERATE
            AlbuminuriaCategory.A2 -> CkdRiskLevel.HIGH
            AlbuminuriaCategory.A3 -> CkdRiskLevel.VERY_HIGH
        }
        GfrCategory.G3B -> when (albuminuria) {
            AlbuminuriaCategory.A1 -> CkdRiskLevel.HIGH
            AlbuminuriaCategory.A2,
            AlbuminuriaCategory.A3 -> CkdRiskLevel.VERY_HIGH
        }
        GfrCategory.G4,
        GfrCategory.G5 -> CkdRiskLevel.VERY_HIGH
    }
}
