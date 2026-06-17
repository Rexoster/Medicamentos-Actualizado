package com.luisangel.calculadoramedicamentos.ui

import android.app.DatePickerDialog
import android.graphics.Paint as AndroidPaint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luisangel.calculadoramedicamentos.growth.GrowthAssessment
import com.luisangel.calculadoramedicamentos.growth.GrowthChart
import com.luisangel.calculadoramedicamentos.growth.GrowthEngine
import com.luisangel.calculadoramedicamentos.growth.GrowthIndicator
import com.luisangel.calculadoramedicamentos.growth.GrowthResult
import com.luisangel.calculadoramedicamentos.growth.GrowthSex
import com.luisangel.calculadoramedicamentos.growth.MeasurementMode
import com.luisangel.calculadoramedicamentos.model.FilterState
import com.luisangel.calculadoramedicamentos.model.MedicationDraft
import com.luisangel.calculadoramedicamentos.model.MedicationRecord
import com.luisangel.calculadoramedicamentos.model.MedicationType
import com.luisangel.calculadoramedicamentos.model.SortOption
import com.luisangel.calculadoramedicamentos.model.TypeFilter
import com.luisangel.calculadoramedicamentos.model.calculatedDose
import com.luisangel.calculadoramedicamentos.model.fingerprint
import com.luisangel.calculadoramedicamentos.model.toDraft
import com.luisangel.calculadoramedicamentos.model.toRecord
import com.luisangel.calculadoramedicamentos.model.validationError
import com.luisangel.calculadoramedicamentos.ui.theme.CalculatorTheme
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

private enum class MainSection(val label: String) {
    MEDICATIONS("Medicamentos"),
    PERCENTILES("Percentiles")
}

@Composable
fun CalculatorApp(viewModel: MainViewModel) {
    val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()
    CalculatorTheme(darkTheme) {
        ApplicationShell(viewModel, darkTheme)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApplicationShell(viewModel: MainViewModel, darkTheme: Boolean) {
    var section by rememberSaveable { mutableStateOf(MainSection.MEDICATIONS) }
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.messages.collectLatest { snackbarHost.showSnackbar(it) }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Herramientas clínicas",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "Aplicación nativa · datos locales",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = viewModel::toggleTheme) {
                            Icon(
                                imageVector = if (darkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = if (darkTheme) "Cambiar a tema claro" else "Cambiar a tema oscuro"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                TabRow(selectedTabIndex = section.ordinal) {
                    MainSection.entries.forEach { item ->
                        Tab(
                            selected = section == item,
                            onClick = { section = item },
                            text = { Text(item.label, fontWeight = if (section == item) FontWeight.Bold else FontWeight.Medium) }
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        when (section) {
            MainSection.MEDICATIONS -> MedicationCalculatorScreen(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            MainSection.PERCENTILES -> PercentilesScreen(
                modifier = Modifier.fillMaxSize().padding(padding)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MedicationCalculatorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val all by viewModel.allMedications.collectAsStateWithLifecycle()
    val filtered by viewModel.filteredMedications.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val adultWeight by viewModel.adultWeight.collectAsStateWithLifecycle()
    val pediatricWeight by viewModel.pediatricWeight.collectAsStateWithLifecycle()
    val families by viewModel.availableFamilies.collectAsStateWithLifecycle()
    val subgroups by viewModel.availableSubgroups.collectAsStateWithLifecycle()
    val frequencies by viewModel.availableFrequencies.collectAsStateWithLifecycle()
    val specialties by viewModel.availableSpecialties.collectAsStateWithLifecycle()
    val pendingImport by viewModel.pendingImport.collectAsStateWithLifecycle()
    val busy by viewModel.busy.collectAsStateWithLifecycle()

    var showEditor by rememberSaveable { mutableStateOf(false) }
    var editorTarget by remember { mutableStateOf<MedicationRecord?>(null) }
    var deleteTarget by remember { mutableStateOf<MedicationRecord?>(null) }
    var showFilters by rememberSaveable { mutableStateOf(false) }
    var showClearConfirmation by rememberSaveable { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
    ) { uri: Uri? ->
        uri?.let { viewModel.exportToUri(context.contentResolver, it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.importFromUri(context.contentResolver, it) }
    }

    val adultCount = all.count { it.type == MedicationType.ADULT }
    val pediatricCount = all.count { it.type == MedicationType.PEDIATRIC }
    val visibleForTab = filtered.filter { it.type == selectedTab }

    BoxWithConstraints(modifier) {
        val availableScreenWidth = maxWidth
        val contentMaxWidth = when {
            availableScreenWidth >= 1400.dp -> 1360.dp
            availableScreenWidth >= 840.dp -> availableScreenWidth - 32.dp
            else -> availableScreenWidth
        }

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier.width(contentMaxWidth).fillMaxHeight(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { ClinicalNotice() }
                item { SummarySection(total = all.size, adults = adultCount, pediatrics = pediatricCount) }
                item {
                    WeightSection(
                        adultWeight = adultWeight,
                        pediatricWeight = pediatricWeight,
                        onAdultWeight = viewModel::setAdultWeight,
                        onPediatricWeight = viewModel::setPediatricWeight
                    )
                }
                item {
                    ActionSection(
                        onAdd = { editorTarget = null; showEditor = true },
                        onFilter = { showFilters = true },
                        onImport = {
                            importLauncher.launch(
                                arrayOf(
                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                    "application/vnd.ms-excel",
                                    "text/csv",
                                    "application/json",
                                    "text/plain"
                                )
                            )
                        },
                        onExport = {
                            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale("es", "MX")).format(Date())
                            exportLauncher.launch("calculadora_medicamentos_$timestamp.xlsx")
                        },
                        onClear = { showClearConfirmation = true },
                        enabled = !busy
                    )
                }
                item { FilterSummary(filters = filters) }
                item {
                    TabRow(selectedTabIndex = if (selectedTab == MedicationType.ADULT) 0 else 1) {
                        Tab(
                            selected = selectedTab == MedicationType.ADULT,
                            onClick = { viewModel.setSelectedTab(MedicationType.ADULT) },
                            text = { Text("Adultos ($adultCount)") }
                        )
                        Tab(
                            selected = selectedTab == MedicationType.PEDIATRIC,
                            onClick = { viewModel.setSelectedTab(MedicationType.PEDIATRIC) },
                            text = { Text("Pediátricos ($pediatricCount)") }
                        )
                    }
                }
                if (visibleForTab.isEmpty()) {
                    item { EmptyState() }
                } else {
                    item {
                        MedicationTable(
                            records = visibleForTab,
                            adultWeight = adultWeight.toDoubleOrNull(),
                            pediatricWeight = pediatricWeight.toDoubleOrNull(),
                            availableWidth = availableScreenWidth,
                            onEdit = { record -> editorTarget = record; showEditor = true },
                            onDelete = { deleteTarget = it }
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = { editorTarget = null; showEditor = true },
                modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar medicamento")
            }

            if (busy) {
                Surface(
                    color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.28f),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                }
            }
        }
    }

    if (showEditor) {
        MedicationEditorDialog(
            target = editorTarget,
            allRecords = all,
            families = families,
            subgroups = subgroups,
            frequencies = frequencies,
            specialties = specialties,
            onDismiss = { showEditor = false },
            onSave = { draft ->
                viewModel.saveMedication(draft, editorTarget)
                showEditor = false
            }
        )
    }

    if (showFilters) {
        FilterSheet(
            current = filters,
            families = families,
            subgroups = subgroups,
            specialties = specialties,
            onDismiss = { showFilters = false },
            onApply = { viewModel.setFilters(it); showFilters = false },
            onClear = { viewModel.clearFilters(); showFilters = false }
        )
    }

    deleteTarget?.let { record ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = { Text("Eliminar fármaco") },
            text = { Text("¿Eliminar ${record.name} (${record.presentation})? Esta acción no se puede deshacer salvo que tengas un Excel de respaldo.") },
            confirmButton = {
                Button(onClick = { viewModel.deleteMedication(record); deleteTarget = null }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancelar") } }
        )
    }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            icon = { Icon(Icons.Default.WarningAmber, contentDescription = null) },
            title = { Text("Borrar todos los medicamentos") },
            text = { Text("Se eliminará toda la base local de la aplicación. Los pesos temporales no forman parte de la base.") },
            confirmButton = {
                Button(onClick = { viewModel.clearAll(); showClearConfirmation = false }) { Text("Borrar todo") }
            },
            dismissButton = { TextButton(onClick = { showClearConfirmation = false }) { Text("Cancelar") } }
        )
    }

    pendingImport?.let { preview ->
        ImportPreviewDialog(
            preview = preview,
            onReplace = { viewModel.finishImport(true) },
            onCombine = { viewModel.finishImport(false) },
            onCancel = viewModel::cancelImport
        )
    }
}

@Composable
private fun ClinicalNotice() {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
        )
    ) {
        Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Text(
                "La aplicación almacena únicamente medicamentos en una base local privada. Los pesos sirven para calcular dosis durante la sesión y no se guardan. Los ejemplos no sustituyen guías clínicas ni valoración profesional.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SummarySection(total: Int, adults: Int, pediatrics: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        SummaryCard("Total", total.toString(), Modifier.weight(1f))
        SummaryCard("Adultos", adults.toString(), Modifier.weight(1f))
        SummaryCard("Pediátricos", pediatrics.toString(), Modifier.weight(1f))
    }
}

@Composable
private fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WeightSection(
    adultWeight: String,
    pediatricWeight: String,
    onAdultWeight: (String) -> Unit,
    onPediatricWeight: (String) -> Unit
) {
    BoxWithConstraints {
        if (maxWidth < 650.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                WeightCard("Peso temporal adulto", adultWeight, onAdultWeight)
                WeightCard("Peso temporal pediátrico", pediatricWeight, onPediatricWeight)
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                WeightCard("Peso temporal adulto", adultWeight, onAdultWeight, Modifier.weight(1f))
                WeightCard("Peso temporal pediátrico", pediatricWeight, onPediatricWeight, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WeightCard(label: String, value: String, onValue: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedCard(modifier) {
        Column(Modifier.padding(14.dp)) {
            OutlinedTextField(
                value = value,
                onValueChange = { onValue(decimalText(it)) },
                label = { Text(label) },
                suffix = { Text("kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "No se guarda al cerrar la aplicación.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActionSection(
    onAdd: () -> Unit,
    onFilter: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    onClear: () -> Unit,
    enabled: Boolean
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onAdd, enabled = enabled) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(6.dp)); Text("Agregar") }
        OutlinedButton(onClick = onFilter, enabled = enabled) { Icon(Icons.Default.FilterAlt, null); Spacer(Modifier.width(6.dp)); Text("Filtros") }
        OutlinedButton(onClick = onImport, enabled = enabled) { Icon(Icons.Default.UploadFile, null); Spacer(Modifier.width(6.dp)); Text("Importar") }
        OutlinedButton(onClick = onExport, enabled = enabled) { Icon(Icons.Default.Download, null); Spacer(Modifier.width(6.dp)); Text("Exportar Excel") }
        OutlinedButton(onClick = onClear, enabled = enabled) { Icon(Icons.Default.ClearAll, null); Spacer(Modifier.width(6.dp)); Text("Borrar todo") }
    }
}

@Composable
private fun FilterSummary(filters: FilterState) {
    val labels = buildList {
        if (filters.search.isNotBlank()) add("Búsqueda: ${filters.search}")
        if (filters.family.isNotBlank()) add("Familia: ${filters.family}")
        if (filters.subgroup.isNotBlank()) add("Subgrupo: ${filters.subgroup}")
        if (filters.specialties.isNotEmpty()) add("Especialidades: ${filters.specialties.size}")
        if (filters.type != TypeFilter.BOTH) add("Apartado: ${filters.type.label()}")
        add("Orden: ${filters.sort.label()} ${if (filters.ascending) "↑" else "↓"}")
    }
    Text(
        labels.joinToString(" · "),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun EmptyState() {
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(42.dp), tint = MaterialTheme.colorScheme.primary)
            Text("No hay medicamentos para mostrar", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 10.dp))
            Text("Revisa los filtros o agrega un medicamento.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private data class MedicationColumn(val title: String, val width: Dp)

private val medicationColumns = listOf(
    MedicationColumn("Medicamento", 190.dp),
    MedicationColumn("Presentación", 220.dp),
    MedicationColumn("Dosis", 170.dp),
    MedicationColumn("Dosis calculada", 170.dp),
    MedicationColumn("Uso por día", 190.dp),
    MedicationColumn("Días", 90.dp),
    MedicationColumn("Familia", 190.dp),
    MedicationColumn("Subgrupo", 170.dp),
    MedicationColumn("Especialidades", 300.dp),
    MedicationColumn("Notas", 330.dp),
    MedicationColumn("Opciones", 90.dp)
)

@Composable
private fun MedicationTable(
    records: List<MedicationRecord>,
    adultWeight: Double?,
    pediatricWeight: Double?,
    availableWidth: Dp,
    onEdit: (MedicationRecord) -> Unit,
    onDelete: (MedicationRecord) -> Unit
) {
    val scrollState = rememberScrollState()
    val naturalWidth = medicationColumns.fold(0.dp) { total, column -> total + column.width }
    val tableWidth = if (availableWidth > naturalWidth) availableWidth else naturalWidth

    OutlinedCard(Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Tabla de medicamentos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "Desliza horizontalmente para consultar todas las columnas.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text("${records.size} registros", style = MaterialTheme.typography.labelMedium)
            }
            HorizontalDivider()
            Box(Modifier.fillMaxWidth().horizontalScroll(scrollState)) {
                Column(Modifier.width(tableWidth)) {
                    MedicationHeaderRow()
                    records.forEachIndexed { index, record ->
                        MedicationDataRow(
                            record = record,
                            weight = if (record.type == MedicationType.ADULT) adultWeight else pediatricWeight,
                            alternate = index % 2 == 1,
                            onEdit = { onEdit(record) },
                            onDelete = { onDelete(record) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicationHeaderRow() {
    Row(
        Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .height(58.dp)
    ) {
        medicationColumns.forEach { column ->
            Box(
                modifier = Modifier
                    .width(column.width)
                    .fillMaxHeight()
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    column.title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MedicationDataRow(
    record: MedicationRecord,
    weight: Double?,
    alternate: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    var showNotes by remember { mutableStateOf(false) }
    val rowColor = if (alternate) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.34f) else MaterialTheme.colorScheme.surface
    val dose = if (record.type == MedicationType.ADULT && !record.isSpecialAdult) {
        record.dose
    } else {
        "${record.dosePerKg ?: ""} ${record.doseUnit}/kg"
    }

    Row(
        Modifier
            .background(rowColor)
            .combinedClickable(onClick = {}, onLongClick = { menuOpen = true })
            .height(112.dp)
    ) {
        TableCell(medicationColumns[0].width) {
            Column {
                Text(record.name, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (record.isSpecialAdult) {
                    Text("Adulto especial", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
        TableTextCell(record.presentation, medicationColumns[1].width)
        TableTextCell(dose, medicationColumns[2].width)
        TableTextCell(record.calculatedDose(weight), medicationColumns[3].width, fontWeight = FontWeight.SemiBold)
        TableTextCell(record.frequencyPerDay, medicationColumns[4].width)
        TableTextCell(record.durationDays.toString(), medicationColumns[5].width)
        TableTextCell(record.family, medicationColumns[6].width)
        TableTextCell(record.subgroup.ifBlank { "—" }, medicationColumns[7].width)
        TableTextCell(record.specialties.joinToString(", ").ifBlank { "—" }, medicationColumns[8].width, maxLines = 3)
        TableCell(medicationColumns[9].width) {
            Column {
                Text(
                    record.notes.ifBlank { "—" },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
                if (record.notes.length > 95) {
                    TextButton(onClick = { showNotes = true }, contentPadding = PaddingValues(0.dp)) {
                        Text("+ Ver más")
                    }
                }
            }
        }
        TableCell(medicationColumns[10].width, contentAlignment = Alignment.Center) {
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("Editar fármaco") },
                        leadingIcon = { Icon(Icons.Default.Edit, null) },
                        onClick = { menuOpen = false; onEdit() }
                    )
                    DropdownMenuItem(
                        text = { Text("Eliminar fármaco") },
                        leadingIcon = { Icon(Icons.Default.Delete, null) },
                        onClick = { menuOpen = false; onDelete() }
                    )
                }
            }
        }
    }
    HorizontalDivider()

    if (showNotes) {
        AlertDialog(
            onDismissRequest = { showNotes = false },
            title = { Text("Notas de ${record.name}") },
            text = { Text(record.notes) },
            confirmButton = { TextButton(onClick = { showNotes = false }) { Text("Cerrar") } }
        )
    }
}

@Composable
private fun TableCell(
    width: Dp,
    contentAlignment: Alignment = Alignment.CenterStart,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

@Composable
private fun TableTextCell(
    text: String,
    width: Dp,
    maxLines: Int = 3,
    fontWeight: FontWeight? = null
) {
    TableCell(width) {
        Text(
            text.ifBlank { "—" },
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = fontWeight
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PercentilesScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val engine = remember { GrowthEngine(context) }
    var sex by rememberSaveable { mutableStateOf(GrowthSex.FEMALE) }
    var measurementMode by rememberSaveable { mutableStateOf(MeasurementMode.HEIGHT) }
    var birthDate by rememberSaveable { mutableStateOf(LocalDate.now().minusYears(2).toString()) }
    var measurementDate by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var weightText by rememberSaveable { mutableStateOf("") }
    var heightText by rememberSaveable { mutableStateOf("") }
    var assessment by remember { mutableStateOf<GrowthAssessment?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val birth = remember(birthDate) { runCatching { LocalDate.parse(birthDate) }.getOrElse { LocalDate.now().minusYears(2) } }
    val measured = remember(measurementDate) { runCatching { LocalDate.parse(measurementDate) }.getOrElse { LocalDate.now() } }

    BoxWithConstraints(modifier) {
        val expanded = maxWidth >= 840.dp
        val calculate: () -> Unit = {
            val weight = weightText.replace(',', '.').toDoubleOrNull()
            val height = heightText.replace(',', '.').toDoubleOrNull()

            if (weight == null || height == null) {
                error = "Captura peso y talla con valores numéricos válidos."
                assessment = null
            } else {
                engine.assess(
                    sex = sex,
                    birthDate = birth,
                    measurementDate = measured,
                    weightKg = weight,
                    heightCm = height,
                    measurementMode = measurementMode
                ).onSuccess {
                    assessment = it
                    error = null
                }.onFailure {
                    assessment = null
                    error = it.message ?: "No fue posible calcular los percentiles."
                }

                Unit
            }
        }

        if (expanded) {
            Row(
                Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    Modifier.widthIn(min = 340.dp, max = 430.dp).fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PercentileInformationCard()
                    PercentileForm(
                        sex = sex,
                        onSex = { sex = it },
                        birthDate = birth,
                        onBirthDate = { birthDate = it.toString() },
                        measurementDate = measured,
                        onMeasurementDate = { measurementDate = it.toString() },
                        weight = weightText,
                        onWeight = { weightText = decimalText(it) },
                        height = heightText,
                        onHeight = { heightText = decimalText(it) },
                        measurementMode = measurementMode,
                        onMeasurementMode = { measurementMode = it },
                        onCalculate = calculate,
                        error = error
                    )
                    Spacer(Modifier.height(20.dp))
                }
                PercentileResults(
                    assessment = assessment,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp, 12.dp, 12.dp, 40.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { PercentileInformationCard() }
                item {
                    PercentileForm(
                        sex = sex,
                        onSex = { sex = it },
                        birthDate = birth,
                        onBirthDate = { birthDate = it.toString() },
                        measurementDate = measured,
                        onMeasurementDate = { measurementDate = it.toString() },
                        weight = weightText,
                        onWeight = { weightText = decimalText(it) },
                        height = heightText,
                        onHeight = { heightText = decimalText(it) },
                        measurementMode = measurementMode,
                        onMeasurementMode = { measurementMode = it },
                        onCalculate = calculate,
                        error = error
                    )
                }
                assessment?.let { result ->
                    item { PercentileAssessmentSummary(result) }
                    result.results.forEach { growthResult ->
                        item(key = growthResult.indicator.name) { GrowthResultCard(growthResult) }
                    }
                    if (result.warnings.isNotEmpty()) {
                        item { GrowthWarnings(result.warnings) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PercentileInformationCard() {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text("Percentiles pediátricos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Calcula peso y talla para la edad, IMC para la edad y, hasta los 5 años, peso para la longitud o talla. Utiliza estándares OMS 2006 y referencias OMS 2007.",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Los datos introducidos no se guardan. El resultado es orientativo y debe interpretarse junto con la evolución clínica y mediciones previas.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PercentileForm(
    sex: GrowthSex,
    onSex: (GrowthSex) -> Unit,
    birthDate: LocalDate,
    onBirthDate: (LocalDate) -> Unit,
    measurementDate: LocalDate,
    onMeasurementDate: (LocalDate) -> Unit,
    weight: String,
    onWeight: (String) -> Unit,
    height: String,
    onHeight: (String) -> Unit,
    measurementMode: MeasurementMode,
    onMeasurementMode: (MeasurementMode) -> Unit,
    onCalculate: () -> Unit,
    error: String?
) {
    OutlinedCard {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Datos de la medición", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Sexo", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GrowthSex.entries.forEach { value ->
                    FilterChip(
                        selected = sex == value,
                        onClick = { onSex(value) },
                        label = { Text(value.label) }
                    )
                }
            }
            DateField("Fecha de nacimiento", birthDate, onBirthDate, maxDate = measurementDate)
            DateField("Fecha de medición", measurementDate, onMeasurementDate, minDate = birthDate, maxDate = LocalDate.now())
            OutlinedTextField(
                value = weight,
                onValueChange = onWeight,
                label = { Text("Peso") },
                suffix = { Text("kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = height,
                onValueChange = onHeight,
                label = { Text("Longitud o talla") },
                suffix = { Text("cm") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text("Forma de medición", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MeasurementMode.entries.forEach { value ->
                    FilterChip(
                        selected = measurementMode == value,
                        onClick = { onMeasurementMode(value) },
                        label = { Text(value.label) }
                    )
                }
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            Button(onClick = onCalculate, modifier = Modifier.fillMaxWidth()) {
                Text("Calcular percentiles")
            }
        }
    }
}

@Composable
private fun DateField(
    label: String,
    date: LocalDate,
    onDate: (LocalDate) -> Unit,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null
) {
    val context = LocalContext.current
    val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    OutlinedButton(
        onClick = {
            DatePickerDialog(
                context,
                { _, year, month, day -> onDate(LocalDate.of(year, month + 1, day)) },
                date.year,
                date.monthValue - 1,
                date.dayOfMonth
            ).apply {
                minDate?.let { datePicker.minDate = it.toEpochDay() * 86_400_000L }
                maxDate?.let { datePicker.maxDate = it.toEpochDay() * 86_400_000L }
            }.show()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(date.format(formatter), fontWeight = FontWeight.SemiBold)
        }
        Text("▾")
    }
}

@Composable
private fun PercentileResults(
    assessment: GrowthAssessment?,
    modifier: Modifier = Modifier
) {
    if (assessment == null) {
        Box(modifier, contentAlignment = Alignment.Center) {
            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Captura los datos y calcula", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Aquí aparecerán los percentiles y sus gráficas.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { PercentileAssessmentSummary(assessment) }
        assessment.results.forEach { result ->
            item(key = result.indicator.name) { GrowthResultCard(result) }
        }
        if (assessment.warnings.isNotEmpty()) {
            item { GrowthWarnings(assessment.warnings) }
        }
    }
}

@Composable
private fun PercentileAssessmentSummary(assessment: GrowthAssessment) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("Resumen", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Edad exacta: ${assessment.ageText}")
            Text("IMC: ${formatDecimal(assessment.bmi, 2)} kg/m²")
            assessment.measurementAdjustment?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
private fun GrowthResultCard(result: GrowthResult) {
    OutlinedCard {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(result.indicator.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ResultPill("Medición", "${formatDecimal(result.measuredValue, 2)} ${result.indicator.unit}")
                ResultPill("Percentil", formatPercentile(result.percentile))
                ResultPill("Puntaje Z", formatSigned(result.zScore))
            }
            Text(result.interpretation, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            GrowthChartView(result.chart)
        }
    }
}

@Composable
private fun ResultPill(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GrowthChartView(chart: GrowthChart) {
    val curveColors = listOf(
        Color(0xFF6A5ACD),
        Color(0xFF1E88E5),
        Color(0xFF00897B),
        Color(0xFFF9A825),
        Color(0xFFD81B60)
    )
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val patientColor = MaterialTheme.colorScheme.error

    val allPoints = remember(chart) { chart.curves.flatMap { it.points } + chart.patientPoint }
    val minX = allPoints.minOfOrNull { it.x } ?: 0.0
    val maxX = allPoints.maxOfOrNull { it.x } ?: 1.0
    val rawMinY = allPoints.minOfOrNull { it.y } ?: 0.0
    val rawMaxY = allPoints.maxOfOrNull { it.y } ?: 1.0
    val yPadding = max((rawMaxY - rawMinY) * 0.08, 0.5)
    val minY = max(0.0, rawMinY - yPadding)
    val maxY = rawMaxY + yPadding

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(chart.title, style = MaterialTheme.typography.labelLarge)
        Canvas(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 250.dp, max = 360.dp)
                .aspectRatio(1.55f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, gridColor, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            val left = 52.dp.toPx()
            val right = 14.dp.toPx()
            val top = 18.dp.toPx()
            val bottom = 38.dp.toPx()
            val plotWidth = size.width - left - right
            val plotHeight = size.height - top - bottom

            fun xToPx(value: Double): Float {
                val ratio = if (maxX == minX) 0.0 else (value - minX) / (maxX - minX)
                return left + (ratio * plotWidth).toFloat()
            }
            fun yToPx(value: Double): Float {
                val ratio = if (maxY == minY) 0.0 else (value - minY) / (maxY - minY)
                return top + ((1.0 - ratio) * plotHeight).toFloat()
            }

            repeat(5) { index ->
                val ratio = index / 4f
                val y = top + ratio * plotHeight
                drawLine(gridColor, Offset(left, y), Offset(left + plotWidth, y), strokeWidth = 1.dp.toPx())
                val value = maxY - ratio * (maxY - minY)
                val paint = AndroidPaint().apply {
                    color = axisColor.toArgb()
                    textSize = 10.sp.toPx()
                    textAlign = AndroidPaint.Align.RIGHT
                    isAntiAlias = true
                }
                drawContext.canvas.nativeCanvas.drawText(formatDecimal(value, 1), left - 6.dp.toPx(), y + 4.dp.toPx(), paint)
            }

            repeat(5) { index ->
                val ratio = index / 4f
                val x = left + ratio * plotWidth
                drawLine(gridColor, Offset(x, top), Offset(x, top + plotHeight), strokeWidth = 1.dp.toPx())
                val value = minX + ratio * (maxX - minX)
                val paint = AndroidPaint().apply {
                    color = axisColor.toArgb()
                    textSize = 10.sp.toPx()
                    textAlign = AndroidPaint.Align.CENTER
                    isAntiAlias = true
                }
                drawContext.canvas.nativeCanvas.drawText(chart.xFormatter(value), x, top + plotHeight + 18.dp.toPx(), paint)
            }

            drawLine(axisColor, Offset(left, top), Offset(left, top + plotHeight), strokeWidth = 1.5.dp.toPx())
            drawLine(axisColor, Offset(left, top + plotHeight), Offset(left + plotWidth, top + plotHeight), strokeWidth = 1.5.dp.toPx())

            chart.curves.forEachIndexed { index, curve ->
                val path = Path()
                curve.points.forEachIndexed { pointIndex, point ->
                    val x = xToPx(point.x)
                    val y = yToPx(point.y)
                    if (pointIndex == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(
                    path = path,
                    color = curveColors[index % curveColors.size],
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            val patientX = xToPx(chart.patientPoint.x)
            val patientY = yToPx(chart.patientPoint.y)
            drawCircle(Color.White, radius = 7.dp.toPx(), center = Offset(patientX, patientY))
            drawCircle(patientColor, radius = 5.dp.toPx(), center = Offset(patientX, patientY))
        }
        Text(
            "Eje X: ${chart.xLabel} · Eje Y: ${chart.yLabel}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            chart.curves.forEachIndexed { index, curve ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(Modifier.size(10.dp).background(curveColors[index % curveColors.size], RoundedCornerShape(50)))
                    Text(curve.label, style = MaterialTheme.typography.labelSmall)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.size(10.dp).background(patientColor, RoundedCornerShape(50)))
                Text("Medición", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun GrowthWarnings(warnings: List<String>) {
    OutlinedCard(colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f))) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Consideraciones", fontWeight = FontWeight.Bold)
            warnings.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
        }
    }
}

private fun formatDecimal(value: Double, decimals: Int): String =
    String.format(Locale("es", "MX"), "%.${decimals}f", value)

private fun formatSigned(value: Double): String =
    String.format(Locale("es", "MX"), "%+.2f", value)

private fun formatPercentile(value: Double): String = when {
    value < 0.1 -> "< P0.1"
    value > 99.9 -> "> P99.9"
    else -> "P${String.format(Locale("es", "MX"), "%.1f", value)}"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun MedicationEditorDialog(
    target: MedicationRecord?,
    allRecords: List<MedicationRecord>,
    families: List<String>,
    subgroups: List<String>,
    frequencies: List<String>,
    specialties: List<String>,
    onDismiss: () -> Unit,
    onSave: (MedicationDraft) -> Unit
) {
    var draft by remember(target?.id) { mutableStateOf(target?.toDraft() ?: MedicationDraft()) }
    var formError by remember { mutableStateOf<String?>(null) }
    var showSpecialties by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(if (target == null) "Agregar medicamento" else "Editar medicamento") },
                        navigationIcon = { IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Cerrar") } },
                        actions = {
                            IconButton(onClick = {
                                val candidate = draft.toRecord(id = target?.id ?: "preview", createdAt = target?.createdAt ?: System.currentTimeMillis())
                                val error = candidate.validationError()
                                val duplicate = allRecords.any { it.id != target?.id && it.fingerprint() == candidate.fingerprint() }
                                formError = when {
                                    error != null -> error
                                    duplicate -> "Ya existe un medicamento con todos esos datos."
                                    else -> null
                                }
                                if (formError == null) onSave(draft)
                            }) { Icon(Icons.Default.Save, "Guardar") }
                        }
                    )
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).imePadding(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("Tipo de paciente", style = MaterialTheme.typography.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = draft.type == MedicationType.ADULT,
                                onClick = { draft = draft.copy(type = MedicationType.ADULT) },
                                label = { Text("Adulto") }
                            )
                            FilterChip(
                                selected = draft.type == MedicationType.PEDIATRIC,
                                onClick = { draft = draft.copy(type = MedicationType.PEDIATRIC, isSpecialAdult = false) },
                                label = { Text("Pediátrico") }
                            )
                        }
                    }
                    if (draft.type == MedicationType.ADULT) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = draft.isSpecialAdult, onCheckedChange = { draft = draft.copy(isSpecialAdult = it) })
                                Column {
                                    Text("Medicamento adulto especial")
                                    Text("Activa el cálculo por kg de peso.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                    item { FormTextField("Medicamento", draft.name, { draft = draft.copy(name = it) }) }
                    item { FormTextField("Presentación", draft.presentation, { draft = draft.copy(presentation = it) }) }
                    if (draft.type == MedicationType.PEDIATRIC || draft.isSpecialAdult) {
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FormTextField(
                                    "Dosis por kg",
                                    draft.dosePerKg,
                                    { draft = draft.copy(dosePerKg = decimalText(it)) },
                                    Modifier.weight(1f),
                                    KeyboardType.Decimal
                                )
                                FormTextField("Unidad", draft.doseUnit, { draft = draft.copy(doseUnit = it) }, Modifier.weight(1f))
                            }
                        }
                        item { FormTextField("Descripción de dosis", draft.dose, { draft = draft.copy(dose = it) }, placeholder = "Ej. Dosis calculada por peso") }
                    } else {
                        item { FormTextField("Dosis", draft.dose, { draft = draft.copy(dose = it) }, placeholder = "Ej. 1 tableta de 500 mg") }
                        item { FormTextField("Unidad", draft.doseUnit, { draft = draft.copy(doseUnit = it) }) }
                    }
                    item {
                        EditableSuggestionField(
                            label = "Tiempo de uso por día",
                            value = draft.frequencyPerDay,
                            options = frequencies,
                            onValue = { draft = draft.copy(frequencyPerDay = it) },
                            placeholder = "Ej. Cada 8 horas"
                        )
                    }
                    item {
                        FormTextField(
                            "Tiempo de uso por días",
                            draft.durationDays,
                            { draft = draft.copy(durationDays = it.filter(Char::isDigit)) },
                            keyboardType = KeyboardType.Number
                        )
                    }
                    item {
                        EditableSuggestionField(
                            label = "Familia",
                            value = draft.family,
                            options = families,
                            onValue = { draft = draft.copy(family = it) },
                            placeholder = "Ej. Analgésico"
                        )
                    }
                    item {
                        EditableSuggestionField(
                            label = "Subgrupo (opcional)",
                            value = draft.subgroup,
                            options = subgroups,
                            onValue = { draft = draft.copy(subgroup = it) },
                            placeholder = "Ej. AINE"
                        )
                    }
                    item {
                        Text("Especialidades", style = MaterialTheme.typography.titleSmall)
                        OutlinedButton(onClick = { showSpecialties = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(if (draft.specialties.isEmpty()) "Seleccionar especialidades" else "${draft.specialties.size} especialidades seleccionadas")
                        }
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 8.dp)) {
                            draft.specialties.sorted().forEach { AssistChip(onClick = {}, label = { Text(it) }) }
                        }
                    }
                    item { FormTextField("Notas", draft.notes, { draft = draft.copy(notes = it) }, singleLine = false, minLines = 4) }
                    formError?.let { error ->
                        item { Text(error, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
                    }
                    item {
                        Button(
                            onClick = {
                                val candidate = draft.toRecord(id = target?.id ?: "preview", createdAt = target?.createdAt ?: System.currentTimeMillis())
                                val error = candidate.validationError()
                                val duplicate = allRecords.any { it.id != target?.id && it.fingerprint() == candidate.fingerprint() }
                                formError = when {
                                    error != null -> error
                                    duplicate -> "Ya existe un medicamento con todos esos datos."
                                    else -> null
                                }
                                if (formError == null) onSave(draft)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Icon(Icons.Default.Save, null); Spacer(Modifier.width(8.dp)); Text("Guardar medicamento") }
                    }
                }
            }
        }
    }

    if (showSpecialties) {
        SpecialtySelectionDialog(
            title = "Especialidades del medicamento",
            all = specialties,
            selected = draft.specialties,
            onDismiss = { showSpecialties = false },
            onApply = { draft = draft.copy(specialties = it); showSpecialties = false }
        )
    }
}

@Composable
private fun EditableSuggestionField(
    label: String,
    value: String,
    options: List<String>,
    onValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    var expanded by remember { mutableStateOf(false) }

    val cleanOptions = remember(options) {
        options
            .map(String::trim)
            .filter(String::isNotBlank)
            .distinctBy { it.lowercase(Locale.ROOT) }
            .sortedBy { it.lowercase(Locale.ROOT) }
    }

    val visibleOptions = remember(value, cleanOptions) {
        val query = value.trim()
        cleanOptions
            .filter {
                query.isBlank() ||
                    it.contains(query, ignoreCase = true)
            }
            .sortedWith(
                compareBy<String> {
                    if (it.equals(query, ignoreCase = true)) 0 else 1
                }.thenBy { it.lowercase(Locale.ROOT) }
            )
            .take(12)
    }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValue(it)
                expanded = cleanOptions.isNotEmpty()
            },
            label = { Text(label) },
            placeholder = {
                if (placeholder.isNotBlank()) {
                    Text(placeholder)
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = !expanded && cleanOptions.isNotEmpty()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Mostrar opciones anteriores",
                        tint = if (cleanOptions.isEmpty()) {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            },
            supportingText = {
                Text(
                    if (cleanOptions.isEmpty()) {
                        "Puedes escribir una opción nueva."
                    } else {
                        "${cleanOptions.size} opciones usadas anteriormente · también puedes escribir una nueva"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded && cleanOptions.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .widthIn(min = 280.dp, max = 520.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            if (visibleOptions.isEmpty()) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Text(
                        "Sin coincidencias",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Continúa escribiendo para guardar una opción nueva.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                visibleOptions.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    option,
                                    fontWeight = if (
                                        option.equals(value.trim(), ignoreCase = true)
                                    ) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.SemiBold
                                    }
                                )
                                Text(
                                    "Usado anteriormente",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        leadingIcon = {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(7.dp).size(18.dp)
                                )
                            }
                        },
                        onClick = {
                            onValue(option)
                            expanded = false
                        }
                    )

                    if (index < visibleOptions.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        placeholder = { if (placeholder.isNotBlank()) Text(placeholder) },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterSheet(
    current: FilterState,
    families: List<String>,
    subgroups: List<String>,
    specialties: List<String>,
    onDismiss: () -> Unit,
    onApply: (FilterState) -> Unit,
    onClear: () -> Unit
) {
    var draft by remember(current) { mutableStateOf(current) }
    var showSpecialties by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier.fillMaxHeight(0.9f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Filtros y orden", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = draft.search,
                onValueChange = { draft = draft.copy(search = it) },
                label = { Text("Buscar medicamento") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            DropdownField("Familia", draft.family, listOf("") + families, { draft = draft.copy(family = it) }, emptyLabel = "Todas las familias")
            DropdownField("Subgrupo", draft.subgroup, listOf("") + subgroups, { draft = draft.copy(subgroup = it) }, emptyLabel = "Todos los subgrupos")
            OutlinedButton(onClick = { showSpecialties = true }, modifier = Modifier.fillMaxWidth()) {
                Text(if (draft.specialties.isEmpty()) "Todas las especialidades" else "Especialidades seleccionadas: ${draft.specialties.size}")
            }
            Text("Apartado", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TypeFilter.entries.forEach { value ->
                    FilterChip(selected = draft.type == value, onClick = { draft = draft.copy(type = value) }, label = { Text(value.label()) })
                }
            }
            Text("Ordenar por", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SortOption.entries.forEach { value ->
                    FilterChip(selected = draft.sort == value, onClick = { draft = draft.copy(sort = value) }, label = { Text(value.label()) })
                }
                FilterChip(selected = draft.ascending, onClick = { draft = draft.copy(ascending = true) }, label = { Text("Ascendente") })
                FilterChip(selected = !draft.ascending, onClick = { draft = draft.copy(ascending = false) }, label = { Text("Descendente") })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) { Text("Limpiar") }
                Button(onClick = { onApply(draft) }, modifier = Modifier.weight(1f)) { Text("Aplicar") }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showSpecialties) {
        SpecialtySelectionDialog(
            title = "Filtrar por especialidad",
            all = specialties,
            selected = draft.specialties,
            onDismiss = { showSpecialties = false },
            onApply = { draft = draft.copy(specialties = it); showSpecialties = false }
        )
    }
}

@Composable
private fun DropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    emptyLabel: String
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                Text(label, style = MaterialTheme.typography.labelSmall)
                Text(selected.ifBlank { emptyLabel }, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text("▾")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.ifBlank { emptyLabel }) },
                    onClick = { onSelected(option); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun SpecialtySelectionDialog(
    title: String,
    all: List<String>,
    selected: Set<String>,
    onDismiss: () -> Unit,
    onApply: (Set<String>) -> Unit
) {
    var search by rememberSaveable { mutableStateOf("") }
    var working by remember(selected) { mutableStateOf(selected) }
    val visible = remember(all, search) {
        all.filter { it.contains(search, ignoreCase = true) }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(Modifier.height(500.dp)) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar especialidad") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                LazyColumn(Modifier.padding(top = 8.dp)) {
                    visible.forEach { specialty ->
                        item(key = specialty) {
                            Row(
                                Modifier.fillMaxWidth().clickable {
                                    working = if (specialty in working) working - specialty else working + specialty
                                }.padding(vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = specialty in working,
                                    onCheckedChange = { checked -> working = if (checked) working + specialty else working - specialty }
                                )
                                Text(specialty, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onApply(working) }) { Text("Aceptar (${working.size})") } },
        dismissButton = {
            Row {
                TextButton(onClick = { working = emptySet() }) { Text("Limpiar") }
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        }
    )
}

@Composable
private fun ImportPreviewDialog(
    preview: ImportPreview,
    onReplace: () -> Unit,
    onCombine: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Importar medicamentos") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Registros válidos: ${preview.uniqueRecords.size}")
                Text("Repetidos exactos dentro del archivo: ${preview.duplicatesInsideFile}")
                Text("Repetidos exactos con la aplicación: ${preview.duplicatesWithCurrent}")
                HorizontalDivider(Modifier.padding(vertical = 6.dp))
                Text("Solo se considera repetido cuando coinciden tipo, presentación, dosis, frecuencia, duración, familia, subgrupo, especialidades y notas.")
            }
        },
        confirmButton = { Button(onClick = onCombine) { Text("Combinar y omitir repetidos") } },
        dismissButton = {
            Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = onReplace) { Text("Reemplazar datos actuales") }
                TextButton(onClick = onCancel) { Text("Cancelar") }
            }
        }
    )
}

private fun TypeFilter.label(): String = when (this) {
    TypeFilter.BOTH -> "Adultos y pediátricos"
    TypeFilter.ADULT -> "Adultos"
    TypeFilter.SPECIAL_ADULT -> "Adultos especiales"
    TypeFilter.PEDIATRIC -> "Pediátricos"
}

private fun SortOption.label(): String = when (this) {
    SortOption.NAME -> "Nombre"
    SortOption.CREATED_AT -> "Fecha de alta"
    SortOption.FAMILY -> "Familia"
}

private fun decimalText(value: String): String {
    val builder = StringBuilder()
    var dot = false
    value.replace(',', '.').forEach { char ->
        when {
            char.isDigit() -> builder.append(char)
            char == '.' && !dot -> { builder.append(char); dot = true }
        }
    }
    return builder.toString()
}
