package com.luisangel.calculadoramedicamentos.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.preferencesDataStore by preferencesDataStore(name = "calculator_preferences")

class AppPreferences(private val context: Context) {
    private object Keys {
        val darkTheme = booleanPreferencesKey("dark_theme")
        val examplesSeeded = booleanPreferencesKey("examples_seeded")
    }

    val darkTheme: Flow<Boolean> = context.preferencesDataStore.data.map { preferences ->
        preferences[Keys.darkTheme] ?: false
    }

    val examplesSeeded: Flow<Boolean> = context.preferencesDataStore.data.map { preferences ->
        preferences[Keys.examplesSeeded] ?: false
    }

    suspend fun setDarkTheme(value: Boolean) {
        context.preferencesDataStore.edit { it[Keys.darkTheme] = value }
    }

    suspend fun setExamplesSeeded(value: Boolean) {
        context.preferencesDataStore.edit { it[Keys.examplesSeeded] = value }
    }
}
