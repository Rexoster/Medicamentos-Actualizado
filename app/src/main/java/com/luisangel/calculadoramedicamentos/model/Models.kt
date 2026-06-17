package com.luisangel.calculadoramedicamentos.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.text.Normalizer
import java.util.Locale
import java.util.UUID

@Immutable
@Serializable
data class MedicationRecord(
    val id: String = UUID.randomUUID().toString(),
    val type: MedicationType = MedicationType.ADULT,
    val isSpecialAdult: Boolean = false,
    val name: String = "",
    val presentation: String = "",
    val dose: String = "",
    val dosePerKg: Double? = null,
    val isInteractiveDose: Boolean = false,
    val dosePerKgMin: Double? = null,
    val dosePerKgMax: Double? = null,
    val dosePerKgStep: Double? = null,
    val doseUnit: String = "mg",
    val frequencyPerDay: String = "",
    val durationDays: Int = 1,
    val family: String = "",
    val subgroup: String = "",
    val specialties: List<String> = emptyList(),
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Immutable
@Serializable
enum class MedicationType {
    ADULT,
    PEDIATRIC
}

@Immutable
@Serializable
data class FilterState(
    val search: String = "",
    val family: String = "",
    val subgroup: String = "",
    val specialties: Set<String> = emptySet(),
    val type: TypeFilter = TypeFilter.BOTH,
    val sort: SortOption = SortOption.NAME,
    val ascending: Boolean = true
)

@Immutable
@Serializable
enum class TypeFilter {
    BOTH,
    ADULT,
    SPECIAL_ADULT,
    PEDIATRIC
}

@Immutable
@Serializable
enum class SortOption {
    NAME,
    CREATED_AT,
    FAMILY
}

@Immutable
data class MedicationDraft(
    val type: MedicationType = MedicationType.ADULT,
    val isSpecialAdult: Boolean = false,
    val name: String = "",
    val presentation: String = "",
    val dose: String = "",
    val dosePerKg: String = "",
    val isInteractiveDose: Boolean = false,
    val dosePerKgMin: String = "",
    val dosePerKgMax: String = "",
    val dosePerKgStep: String = "0.1",
    val doseUnit: String = "mg",
    val frequencyPerDay: String = "",
    val durationDays: String = "1",
    val family: String = "",
    val subgroup: String = "",
    val specialties: Set<String> = emptySet(),
    val notes: String = ""
)

fun MedicationRecord.toDraft() = MedicationDraft(
    type = type,
    isSpecialAdult = isSpecialAdult,
    name = name,
    presentation = presentation,
    dose = dose,
    dosePerKg = dosePerKg?.toString().orEmpty(),
    isInteractiveDose = isInteractiveDose,
    dosePerKgMin = dosePerKgMin?.toString().orEmpty(),
    dosePerKgMax = dosePerKgMax?.toString().orEmpty(),
    dosePerKgStep = dosePerKgStep?.toString().orEmpty(),
    doseUnit = doseUnit,
    frequencyPerDay = frequencyPerDay,
    durationDays = durationDays.toString(),
    family = family,
    subgroup = subgroup,
    specialties = specialties.toSet(),
    notes = notes
)

fun MedicationDraft.toRecord(
    id: String = UUID.randomUUID().toString(),
    createdAt: Long = System.currentTimeMillis()
): MedicationRecord {
    val now = System.currentTimeMillis()
    val supportsWeightDose = (
        type == MedicationType.PEDIATRIC ||
            (type == MedicationType.ADULT && isSpecialAdult)
        )
    val interactive = supportsWeightDose && isInteractiveDose
    val parsedDose = dosePerKg
        .replace(',', '.')
        .toDoubleOrNull()
    val parsedMinimum = dosePerKgMin
        .replace(',', '.')
        .toDoubleOrNull()
    val parsedMaximum = dosePerKgMax
        .replace(',', '.')
        .toDoubleOrNull()
    val parsedStep = dosePerKgStep
        .replace(',', '.')
        .toDoubleOrNull()
        ?: 0.1

    val normalizedDose = if (interactive) {
        parsedDose ?: parsedMinimum
    } else {
        parsedDose
    }

    return MedicationRecord(
        id = id,
        type = type,
        isSpecialAdult = type == MedicationType.ADULT &&
            isSpecialAdult,
        name = name.trim(),
        presentation = presentation.trim(),
        dose = dose.trim(),
        dosePerKg = normalizedDose,
        isInteractiveDose = interactive,
        dosePerKgMin = if (interactive) {
            parsedMinimum
        } else {
            null
        },
        dosePerKgMax = if (interactive) {
            parsedMaximum
        } else {
            null
        },
        dosePerKgStep = if (interactive) {
            parsedStep
        } else {
            null
        },
        doseUnit = doseUnit.trim(),
        frequencyPerDay = frequencyPerDay.trim(),
        durationDays = durationDays.toIntOrNull()
            ?.coerceAtLeast(1)
            ?: 1,
        family = family.trim(),
        subgroup = subgroup.trim(),
        specialties = specialties
            .map(String::trim)
            .filter(String::isNotBlank)
            .distinct()
            .sorted(),
        notes = notes.trim(),
        createdAt = createdAt,
        updatedAt = now
    )
}

fun MedicationRecord.validationError(): String? {
    val supportsWeightDose = (
        type == MedicationType.PEDIATRIC ||
            isSpecialAdult
        )

    return when {
        name.isBlank() ->
            "Captura el nombre del medicamento."
        presentation.isBlank() ->
            "Captura la presentación."
        family.isBlank() ->
            "Captura la familia del medicamento."
        specialties.isEmpty() ->
            "Selecciona al menos una especialidad."
        type == MedicationType.ADULT &&
            !isSpecialAdult &&
            dose.isBlank() ->
            "Captura la dosis fija del medicamento adulto."
        isInteractiveDose && !supportsWeightDose ->
            "La dosis interactiva solo está disponible para medicamentos especiales o pediátricos."
        supportsWeightDose &&
            !isInteractiveDose &&
            (dosePerKg == null || dosePerKg <= 0.0) ->
            "Captura una dosis válida por kilogramo."
        isInteractiveDose &&
            (dosePerKgMin == null || dosePerKgMin <= 0.0) ->
            "Captura una dosis mínima válida."
        isInteractiveDose &&
            (dosePerKgMax == null || dosePerKgMax <= 0.0) ->
            "Captura una dosis máxima válida."
        isInteractiveDose &&
            dosePerKgMin != null &&
            dosePerKgMax != null &&
            dosePerKgMax <= dosePerKgMin ->
            "La dosis máxima debe ser mayor que la mínima."
        isInteractiveDose &&
            (dosePerKgStep == null || dosePerKgStep <= 0.0) ->
            "Captura un incremento válido para la dosis."
        isInteractiveDose &&
            dosePerKgMin != null &&
            dosePerKgMax != null &&
            dosePerKgStep != null &&
            dosePerKgStep > dosePerKgMax - dosePerKgMin ->
            "El incremento no puede ser mayor que todo el rango."
        isInteractiveDose &&
            dosePerKg != null &&
            dosePerKgMin != null &&
            dosePerKg < dosePerKgMin ->
            "La dosis inicial no puede ser menor que la dosis mínima."
        isInteractiveDose &&
            dosePerKg != null &&
            dosePerKgMax != null &&
            dosePerKg > dosePerKgMax ->
            "La dosis inicial no puede ser mayor que la dosis máxima."
        frequencyPerDay.isBlank() ->
            "Captura la frecuencia o tiempo de uso por día."
        durationDays <= 0 ->
            "La duración debe ser mayor que cero."
        else ->
            null
    }
}

private fun normalizeText(value: String): String = Normalizer
    .normalize(value.trim(), Normalizer.Form.NFD)
    .replace("\\p{M}+".toRegex(), "")
    .lowercase(Locale.ROOT)
    .replace("\\s+".toRegex(), " ")

private fun normalizeNumber(value: Double?): String = value?.let {
    BigDecimal.valueOf(it).stripTrailingZeros().toPlainString()
}.orEmpty()

fun MedicationRecord.fingerprint(): String = listOf(
    type.name,
    isSpecialAdult.toString(),
    normalizeText(name),
    normalizeText(presentation),
    normalizeText(dose),
    normalizeNumber(dosePerKg),
    isInteractiveDose.toString(),
    normalizeNumber(dosePerKgMin),
    normalizeNumber(dosePerKgMax),
    normalizeNumber(dosePerKgStep),
    normalizeText(doseUnit),
    normalizeText(frequencyPerDay),
    durationDays.toString(),
    normalizeText(family),
    normalizeText(subgroup),
    specialties.map(::normalizeText).sorted().joinToString("|"),
    normalizeText(notes)
).joinToString("§")

fun MedicationRecord.calculatedDose(
    weight: Double?,
    selectedDosePerKg: Double? = null
): String {
    val activeDose = selectedDosePerKg
        ?: dosePerKg

    if (
        weight == null ||
        weight <= 0 ||
        activeDose == null ||
        activeDose <= 0
    ) {
        return if (
            type == MedicationType.ADULT &&
            !isSpecialAdult
        ) {
            "No aplica"
        } else {
            "Captura el peso"
        }
    }

    val result = BigDecimal.valueOf(
        weight * activeDose
    )
        .stripTrailingZeros()
        .toPlainString()

    return "$result $doseUnit"
}

fun MedicationRecord.interactiveDoseStart(): Double? {
    if (!isInteractiveDose) {
        return dosePerKg
    }

    val minimum = dosePerKgMin ?: return dosePerKg
    val maximum = dosePerKgMax ?: return dosePerKg
    val start = dosePerKg ?: minimum

    return start.coerceIn(minimum, maximum)
}

fun MedicationRecord.hasValidInteractiveRange(): Boolean =
    isInteractiveDose &&
        dosePerKgMin != null &&
        dosePerKgMax != null &&
        dosePerKgStep != null &&
        dosePerKgMin > 0.0 &&
        dosePerKgMax > dosePerKgMin &&
        dosePerKgStep > 0.0 &&
        dosePerKgStep <=
            dosePerKgMax - dosePerKgMin
