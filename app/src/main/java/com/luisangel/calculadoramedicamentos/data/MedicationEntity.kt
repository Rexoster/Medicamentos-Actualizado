package com.luisangel.calculadoramedicamentos.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.luisangel.calculadoramedicamentos.model.MedicationRecord
import com.luisangel.calculadoramedicamentos.model.MedicationType
import com.luisangel.calculadoramedicamentos.model.fingerprint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

@Entity(
    tableName = "medications",
    indices = [Index(value = ["fingerprint"])]
)
data class MedicationEntity(
    @PrimaryKey val id: String,
    val type: String,
    val isSpecialAdult: Boolean,
    val name: String,
    val presentation: String,
    val dose: String,
    val dosePerKg: Double?,
    @ColumnInfo(defaultValue = "0")
    val isInteractiveDose: Boolean,
    val dosePerKgMin: Double?,
    val dosePerKgMax: Double?,
    val dosePerKgStep: Double?,
    val doseUnit: String,
    val frequencyPerDay: String,
    val durationDays: Int,
    val family: String,
    val subgroup: String,
    val specialtiesJson: String,
    val notes: String,
    val fingerprint: String,
    val createdAt: Long,
    val updatedAt: Long
)

fun MedicationRecord.toEntity() = MedicationEntity(
    id = id,
    type = type.name,
    isSpecialAdult = isSpecialAdult,
    name = name,
    presentation = presentation,
    dose = dose,
    dosePerKg = dosePerKg,
    isInteractiveDose = isInteractiveDose,
    dosePerKgMin = dosePerKgMin,
    dosePerKgMax = dosePerKgMax,
    dosePerKgStep = dosePerKgStep,
    doseUnit = doseUnit,
    frequencyPerDay = frequencyPerDay,
    durationDays = durationDays,
    family = family,
    subgroup = subgroup,
    specialtiesJson = json.encodeToString(specialties),
    notes = notes,
    fingerprint = fingerprint(),
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun MedicationEntity.toRecord() = MedicationRecord(
    id = id,
    type = runCatching { MedicationType.valueOf(type) }.getOrDefault(MedicationType.ADULT),
    isSpecialAdult = isSpecialAdult,
    name = name,
    presentation = presentation,
    dose = dose,
    dosePerKg = dosePerKg,
    isInteractiveDose = isInteractiveDose,
    dosePerKgMin = dosePerKgMin,
    dosePerKgMax = dosePerKgMax,
    dosePerKgStep = dosePerKgStep,
    doseUnit = doseUnit,
    frequencyPerDay = frequencyPerDay,
    durationDays = durationDays,
    family = family,
    subgroup = subgroup,
    specialties = runCatching { json.decodeFromString<List<String>>(specialtiesJson) }.getOrDefault(emptyList()),
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)
