package com.luisangel.calculadoramedicamentos.model

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.text.Normalizer
import java.util.Locale
import java.util.UUID

@Serializable
data class MedicationRecord(
    val id: String = UUID.randomUUID().toString(),
    val type: MedicationType = MedicationType.ADULT,
    val isSpecialAdult: Boolean = false,
    val name: String = "",
    val presentation: String = "",
    val dose: String = "",
    val dosePerKg: Double? = null,
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

@Serializable
enum class MedicationType {
    ADULT,
    PEDIATRIC
}

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

@Serializable
enum class TypeFilter {
    BOTH,
    ADULT,
    SPECIAL_ADULT,
    PEDIATRIC
}

@Serializable
enum class SortOption {
    NAME,
    CREATED_AT,
    FAMILY
}

data class MedicationDraft(
    val type: MedicationType = MedicationType.ADULT,
    val isSpecialAdult: Boolean = false,
    val name: String = "",
    val presentation: String = "",
    val dose: String = "",
    val dosePerKg: String = "",
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
    return MedicationRecord(
        id = id,
        type = type,
        isSpecialAdult = type == MedicationType.ADULT && isSpecialAdult,
        name = name.trim(),
        presentation = presentation.trim(),
        dose = dose.trim(),
        dosePerKg = dosePerKg.replace(',', '.').toDoubleOrNull(),
        doseUnit = doseUnit.trim(),
        frequencyPerDay = frequencyPerDay.trim(),
        durationDays = durationDays.toIntOrNull()?.coerceAtLeast(1) ?: 1,
        family = family.trim(),
        subgroup = subgroup.trim(),
        specialties = specialties.map(String::trim).filter(String::isNotBlank).distinct().sorted(),
        notes = notes.trim(),
        createdAt = createdAt,
        updatedAt = now
    )
}

fun MedicationRecord.validationError(): String? = when {
    name.isBlank() -> "Captura el nombre del medicamento."
    presentation.isBlank() -> "Captura la presentación."
    family.isBlank() -> "Captura la familia del medicamento."
    specialties.isEmpty() -> "Selecciona al menos una especialidad."
    type == MedicationType.ADULT && !isSpecialAdult && dose.isBlank() -> "Captura la dosis fija del medicamento adulto."
    (type == MedicationType.PEDIATRIC || isSpecialAdult) && (dosePerKg == null || dosePerKg <= 0.0) -> "Captura una dosis válida por kilogramo."
    frequencyPerDay.isBlank() -> "Captura la frecuencia o tiempo de uso por día."
    durationDays <= 0 -> "La duración debe ser mayor que cero."
    else -> null
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
    normalizeText(doseUnit),
    normalizeText(frequencyPerDay),
    durationDays.toString(),
    normalizeText(family),
    normalizeText(subgroup),
    specialties.map(::normalizeText).sorted().joinToString("|"),
    normalizeText(notes)
).joinToString("§")

fun MedicationRecord.calculatedDose(weight: Double?): String {
    if (weight == null || weight <= 0 || dosePerKg == null || dosePerKg <= 0) {
        return if (type == MedicationType.ADULT && !isSpecialAdult) "No aplica" else "Captura el peso"
    }
    val result = BigDecimal.valueOf(weight * dosePerKg)
        .stripTrailingZeros()
        .toPlainString()
    return "$result $doseUnit"
}
