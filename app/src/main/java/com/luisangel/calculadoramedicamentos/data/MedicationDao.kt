package com.luisangel.calculadoramedicamentos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications")
    suspend fun getAll(): List<MedicationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MedicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<MedicationEntity>)

    @Query("DELETE FROM medications WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM medications")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM medications")
    suspend fun count(): Int
}
