package com.luisangel.calculadoramedicamentos

import android.app.Application
import com.luisangel.calculadoramedicamentos.data.AppDatabase
import com.luisangel.calculadoramedicamentos.data.AppPreferences
import com.luisangel.calculadoramedicamentos.data.MedicationRepository
import com.luisangel.calculadoramedicamentos.io.ExcelService

class CalculatorApplication : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { MedicationRepository(database.medicationDao()) }
    val preferences by lazy { AppPreferences(this) }
    fun createExcelService(): ExcelService = ExcelService()
}
