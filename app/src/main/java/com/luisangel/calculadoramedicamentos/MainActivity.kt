package com.luisangel.calculadoramedicamentos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.luisangel.calculadoramedicamentos.ui.CalculatorApp
import com.luisangel.calculadoramedicamentos.ui.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        val app = application as CalculatorApplication
        MainViewModel.factory(
            repository = app.repository,
            preferences = app.preferences,
            excelServiceProvider = app::createExcelService
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorApp(viewModel)
        }
    }
}
