package com.luisangel.calculadoramedicamentos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors

@Database(
    entities = [MedicationEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(
                database: SupportSQLiteDatabase
            ) {
                database.execSQL(
                    "ALTER TABLE medications " +
                        "ADD COLUMN isInteractiveDose " +
                        "INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE medications " +
                        "ADD COLUMN dosePerKgMin REAL"
                )
                database.execSQL(
                    "ALTER TABLE medications " +
                        "ADD COLUMN dosePerKgMax REAL"
                )
                database.execSQL(
                    "ALTER TABLE medications " +
                        "ADD COLUMN dosePerKgStep REAL"
                )

                database.execSQL(
                    "UPDATE medications SET " +
                        "isInteractiveDose = 1, " +
                        "dosePerKgMin = 0.1, " +
                        "dosePerKgMax = 0.7, " +
                        "dosePerKgStep = 0.1 " +
                        "WHERE id = " +
                        "'example-insulin-basal-adult'"
                )
                database.execSQL(
                    "UPDATE medications SET " +
                        "isInteractiveDose = 1, " +
                        "dosePerKgMin = 10.0, " +
                        "dosePerKgMax = 15.0, " +
                        "dosePerKgStep = 2.5 " +
                        "WHERE id = " +
                        "'example-paracetamol-pediatric'"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "medications.db"
            )
                .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                .setQueryExecutor(Executors.newFixedThreadPool(2))
                .setTransactionExecutor(Executors.newSingleThreadExecutor())
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { instance = it }
        }
    }
}
