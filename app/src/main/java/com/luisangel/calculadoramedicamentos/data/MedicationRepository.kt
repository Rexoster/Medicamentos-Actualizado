package com.luisangel.calculadoramedicamentos.data

import com.luisangel.calculadoramedicamentos.model.MedicationRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class MedicationRepository(private val dao: MedicationDao) {
    val medications: Flow<List<MedicationRecord>> = dao.observeAll()
        .map { list ->
            list.map(MedicationEntity::toRecord)
        }
        .flowOn(Dispatchers.Default)

    suspend fun getAll(): List<MedicationRecord> = dao.getAll().map(MedicationEntity::toRecord)

    suspend fun upsert(record: MedicationRecord) = dao.upsert(record.toEntity())

    suspend fun upsertAll(records: List<MedicationRecord>) = dao.upsertAll(records.map(MedicationRecord::toEntity))

    suspend fun delete(id: String) = dao.deleteById(id)

    suspend fun deleteAll() = dao.deleteAll()

    suspend fun count(): Int = dao.count()
}
