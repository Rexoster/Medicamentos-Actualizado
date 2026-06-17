package com.luisangel.calculadoramedicamentos.ui

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.luisangel.calculadoramedicamentos.data.AppPreferences
import com.luisangel.calculadoramedicamentos.data.MedicalSpecialties
import com.luisangel.calculadoramedicamentos.data.MedicationRepository
import com.luisangel.calculadoramedicamentos.data.exampleMedications
import com.luisangel.calculadoramedicamentos.io.ExcelService
import com.luisangel.calculadoramedicamentos.io.ExportRequest
import com.luisangel.calculadoramedicamentos.io.ImportBundle
import com.luisangel.calculadoramedicamentos.model.FilterState
import com.luisangel.calculadoramedicamentos.model.MedicationDraft
import com.luisangel.calculadoramedicamentos.model.MedicationRecord
import com.luisangel.calculadoramedicamentos.model.MedicationType
import com.luisangel.calculadoramedicamentos.model.SortOption
import com.luisangel.calculadoramedicamentos.model.TypeFilter
import com.luisangel.calculadoramedicamentos.model.fingerprint
import com.luisangel.calculadoramedicamentos.model.toRecord
import com.luisangel.calculadoramedicamentos.model.validationError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


data class ImportPreview(
    val bundle: ImportBundle,
    val uniqueRecords: List<MedicationRecord>,
    val duplicatesInsideFile: Int,
    val duplicatesWithCurrent: Int,
    val newRecords: List<MedicationRecord>
)

class MainViewModel(
    private val repository: MedicationRepository,
    private val preferences: AppPreferences,
    private val excelServiceProvider: () -> ExcelService
) : ViewModel() {
    private val excelService: ExcelService by lazy(
        LazyThreadSafetyMode.NONE,
        excelServiceProvider
    )
    val allMedications: StateFlow<List<MedicationRecord>> = repository.medications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val darkTheme = preferences.darkTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _filters = MutableStateFlow(FilterState())
    val filters: StateFlow<FilterState> = _filters

    private val _selectedTab = MutableStateFlow(MedicationType.ADULT)
    val selectedTab: StateFlow<MedicationType> = _selectedTab

    private val _adultWeight = MutableStateFlow("")
    val adultWeight: StateFlow<String> = _adultWeight

    private val _pediatricWeight = MutableStateFlow("")
    val pediatricWeight: StateFlow<String> = _pediatricWeight

    private val _pendingImport = MutableStateFlow<ImportPreview?>(null)
    val pendingImport: StateFlow<ImportPreview?> = _pendingImport

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val messages = _messages.asSharedFlow()

    val filteredMedications: StateFlow<List<MedicationRecord>> = combine(
        allMedications,
        filters
    ) { records, filter -> applyFilters(records, filter) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val availableFamilies: StateFlow<List<String>> = allMedications
        .map { list ->
            list.map { it.family.trim() }
                .filter(String::isNotBlank)
                .distinctBy { it.lowercase(Locale.ROOT) }
                .sortedBy { it.lowercase(Locale.ROOT) }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val availableSubgroups: StateFlow<List<String>> = allMedications
        .map { list ->
            list.map { it.subgroup.trim() }
                .filter(String::isNotBlank)
                .distinctBy { it.lowercase(Locale.ROOT) }
                .sortedBy { it.lowercase(Locale.ROOT) }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val availableFrequencies: StateFlow<List<String>> = allMedications
        .map { list ->
            list.map { it.frequencyPerDay.trim() }
                .filter(String::isNotBlank)
                .distinctBy { it.lowercase(Locale.ROOT) }
                .sortedBy { it.lowercase(Locale.ROOT) }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val availableSpecialties: StateFlow<List<String>> = allMedications
        .map { list ->
            (MedicalSpecialties + list.flatMap { it.specialties })
                .distinct()
                .sorted()
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MedicalSpecialties)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Darle primero un respiro al primer frame de Compose.
            // Sembrar ejemplos al instante no es una emergencia nacional.
            delay(450)

            val alreadySeeded = preferences.examplesSeeded.first()
            if (!alreadySeeded) {
                if (repository.count() == 0) {
                    repository.upsertAll(exampleMedications())
                }
                preferences.setExamplesSeeded(true)
            }
        }
    }

    fun setSelectedTab(type: MedicationType) { _selectedTab.value = type }
    fun setAdultWeight(value: String) { _adultWeight.value = numericInput(value) }
    fun setPediatricWeight(value: String) { _pediatricWeight.value = numericInput(value) }
    fun setFilters(value: FilterState) { _filters.value = value }
    fun clearFilters() { _filters.value = FilterState() }
    fun toggleTheme() = viewModelScope.launch { preferences.setDarkTheme(!darkTheme.value) }

    fun saveMedication(draft: MedicationDraft, editing: MedicationRecord?) {
        viewModelScope.launch {
            val record = draft.toRecord(
                id = editing?.id ?: java.util.UUID.randomUUID().toString(),
                createdAt = editing?.createdAt ?: System.currentTimeMillis()
            )
            val error = record.validationError()
            if (error != null) {
                _messages.emit(error)
                return@launch
            }
            val duplicate = allMedications.value.any {
                it.id != record.id && it.fingerprint() == record.fingerprint()
            }
            if (duplicate) {
                _messages.emit("Ya existe un medicamento con todos esos datos.")
                return@launch
            }
            repository.upsert(record)
            _messages.emit(if (editing == null) "Medicamento guardado." else "Medicamento actualizado.")
        }
    }

    fun deleteMedication(record: MedicationRecord) = viewModelScope.launch {
        repository.delete(record.id)
        _messages.emit("Medicamento eliminado.")
    }

    fun clearAll() = viewModelScope.launch {
        repository.deleteAll()
        _messages.emit("Se eliminaron todos los medicamentos.")
    }

    fun importFromUri(contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            _busy.value = true
            try {
                val name = queryDisplayName(contentResolver, uri) ?: "archivo.xlsx"
                val bundle = withContext(Dispatchers.IO) {
                    contentResolver.openInputStream(uri)?.use { excelService.import(it, name) }
                        ?: error("No se pudo abrir el archivo.")
                }
                if (bundle.records.isEmpty()) {
                    error("No se encontraron medicamentos ni columnas reconocibles.")
                }
                val seen = linkedSetOf<String>()
                val unique = bundle.records.filter { seen.add(it.fingerprint()) }
                val duplicatesInside = bundle.records.size - unique.size
                val currentFingerprints = allMedications.value.map { it.fingerprint() }.toSet()
                val newRecords = unique.filterNot { it.fingerprint() in currentFingerprints }
                _pendingImport.value = ImportPreview(
                    bundle = bundle,
                    uniqueRecords = unique,
                    duplicatesInsideFile = duplicatesInside,
                    duplicatesWithCurrent = unique.size - newRecords.size,
                    newRecords = newRecords
                )
            } catch (error: Exception) {
                _messages.emit("No se pudo importar: ${error.message ?: "formato no compatible"}")
            } finally {
                _busy.value = false
            }
        }
    }

    fun cancelImport() { _pendingImport.value = null }

    fun finishImport(replace: Boolean) = viewModelScope.launch {
        val preview = _pendingImport.value ?: return@launch
        _busy.value = true
        try {
            if (replace) {
                repository.deleteAll()
                repository.upsertAll(preview.uniqueRecords.ensureUniqueIds())
            } else {
                repository.upsertAll(preview.newRecords.ensureUniqueIds(allMedications.value.map { it.id }.toSet()))
            }
            preview.bundle.filters?.let { _filters.value = it }
            preview.bundle.darkTheme?.let { preferences.setDarkTheme(it) }
            val imported = if (replace) preview.uniqueRecords.size else preview.newRecords.size
            val skipped = preview.duplicatesInsideFile + if (replace) 0 else preview.duplicatesWithCurrent
            _messages.emit("Importados: $imported. Repetidos omitidos: $skipped.")
            _pendingImport.value = null
        } finally {
            _busy.value = false
        }
    }

    fun exportToUri(contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            _busy.value = true
            try {
                val all = allMedications.value
                val visible = filteredMedications.value
                val request = ExportRequest(
                    all = all,
                    visibleAdults = visible.filter { it.type == MedicationType.ADULT },
                    visiblePediatrics = visible.filter { it.type == MedicationType.PEDIATRIC },
                    filters = filters.value,
                    darkTheme = darkTheme.value,
                    adultWeight = adultWeight.value.replace(',', '.').toDoubleOrNull(),
                    pediatricWeight = pediatricWeight.value.replace(',', '.').toDoubleOrNull()
                )
                withContext(Dispatchers.IO) {
                    contentResolver.openOutputStream(uri, "w")?.use { excelService.export(request, it) }
                        ?: error("No se pudo crear el archivo.")
                }
                _messages.emit("Excel exportado correctamente.")
            } catch (error: Exception) {
                _messages.emit("No se pudo exportar: ${error.message ?: "error desconocido"}")
            } finally {
                _busy.value = false
            }
        }
    }

    private fun applyFilters(records: List<MedicationRecord>, filter: FilterState): List<MedicationRecord> {
        val query = filter.search.trim().lowercase(Locale.ROOT)
        val filtered = records.filter { record ->
            val textMatch = if (query.isBlank()) {
                true
            } else {
                record.name.contains(query, ignoreCase = true) ||
                    record.presentation.contains(query, ignoreCase = true) ||
                    record.dose.contains(query, ignoreCase = true) ||
                    record.family.contains(query, ignoreCase = true) ||
                    record.subgroup.contains(query, ignoreCase = true) ||
                    record.notes.contains(query, ignoreCase = true) ||
                    record.specialties.any {
                        it.contains(query, ignoreCase = true)
                    }
            }
            val familyMatch = filter.family.isBlank() || record.family == filter.family
            val subgroupMatch = filter.subgroup.isBlank() || record.subgroup == filter.subgroup
            val specialtyMatch = filter.specialties.isEmpty() || record.specialties.any(filter.specialties::contains)
            val typeMatch = when (filter.type) {
                TypeFilter.BOTH -> true
                TypeFilter.ADULT -> record.type == MedicationType.ADULT
                TypeFilter.SPECIAL_ADULT -> record.type == MedicationType.ADULT && record.isSpecialAdult
                TypeFilter.PEDIATRIC -> record.type == MedicationType.PEDIATRIC
            }
            textMatch && familyMatch && subgroupMatch && specialtyMatch && typeMatch
        }
        val comparator = when (filter.sort) {
            SortOption.NAME -> compareBy<MedicationRecord> { it.name.lowercase(Locale.ROOT) }
            SortOption.CREATED_AT -> compareBy { it.createdAt }
            SortOption.FAMILY -> compareBy<MedicationRecord> { it.family.lowercase(Locale.ROOT) }.thenBy { it.name.lowercase(Locale.ROOT) }
        }
        return if (filter.ascending) filtered.sortedWith(comparator) else filtered.sortedWith(comparator.reversed())
    }

    private fun numericInput(value: String): String {
        val normalized = value.replace(',', '.')
        val builder = StringBuilder()
        var dot = false
        normalized.forEach { char ->
            when {
                char.isDigit() -> builder.append(char)
                char == '.' && !dot -> { builder.append(char); dot = true }
            }
        }
        return builder.toString()
    }

    private fun queryDisplayName(contentResolver: ContentResolver, uri: Uri): String? {
        return contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }
    }

    private fun List<MedicationRecord>.ensureUniqueIds(reserved: Set<String> = emptySet()): List<MedicationRecord> {
        val used = reserved.toMutableSet()
        return map { record ->
            val id = record.id.takeIf { it.isNotBlank() && it !in used } ?: java.util.UUID.randomUUID().toString()
            used += id
            record.copy(id = id, updatedAt = System.currentTimeMillis())
        }
    }

    companion object {
        fun factory(
            repository: MedicationRepository,
            preferences: AppPreferences,
            excelServiceProvider: () -> ExcelService
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                MainViewModel(
                    repository,
                    preferences,
                    excelServiceProvider
                ) as T
        }
    }
}
