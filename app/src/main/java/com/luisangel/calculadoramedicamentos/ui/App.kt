package com.luisangel.calculadoramedicamentos.ui

import android.graphics.Paint as AndroidPaint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
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
import com.luisangel.calculadoramedicamentos.growth.NutritionStatus
import com.luisangel.calculadoramedicamentos.growth.NutritionSummary
import com.luisangel.calculadoramedicamentos.obstetrics.UltrasoundDatingCalculator
import com.luisangel.calculadoramedicamentos.obstetrics.UltrasoundTrimester
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

private enum class MainSection(val label: String) {
    MEDICATIONS("Medicamentos"),
    PERCENTILES("Percentiles"),
    OBSTETRICS("Gineco-OB")
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
            MainSection.OBSTETRICS -> ObstetricsScreen(
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

    val adultCount by remember(all) {
        derivedStateOf {
            all.count { it.type == MedicationType.ADULT }
        }
    }
    val pediatricCount by remember(all) {
        derivedStateOf {
            all.count { it.type == MedicationType.PEDIATRIC }
        }
    }
    val visibleForTab by remember(filtered, selectedTab) {
        derivedStateOf {
            filtered.filter { it.type == selectedTab }
        }
    }

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
            val maximumVisibleRows = if (
                availableWidth < 700.dp
            ) {
                4
            } else {
                7
            }
            val visibleRows = min(
                records.size,
                maximumVisibleRows
            ).coerceAtLeast(1)
            val viewportHeight = 58.dp +
                (112.dp * visibleRows)

            Box(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .width(tableWidth)
                        .height(viewportHeight),
                    userScrollEnabled = records.size > visibleRows
                ) {
                    item(key = "medication-header") {
                        MedicationHeaderRow()
                    }
                    itemsIndexed(
                        items = records,
                        key = { _, record -> record.id }
                    ) { index, record ->
                        MedicationDataRow(
                            record = record,
                            weight = if (
                                record.type == MedicationType.ADULT
                            ) {
                                adultWeight
                            } else {
                                pediatricWeight
                            },
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


private enum class ObstetricCalculator(
    val label: String,
    val shortLabel: String,
    val description: String
) {
    GESTATIONAL_AGE(
        "Edad gestacional y FPP",
        "EG/FPP",
        "Calcula edad gestacional por FUM y fecha probable de parto."
    ),
    ULTRASOUND_DATING(
        "Edad gestacional por ultrasonido",
        "EG por USG",
        "Primer trimestre por LCC/CRL y segundo o tercero por biometría Hadlock."
    ),
    HADLOCK_EFW(
        "Peso fetal estimado",
        "Hadlock",
        "Estimación por biometría fetal con fórmula Hadlock de cuatro parámetros."
    ),
    TWIN_DISCORDANCE(
        "Discordancia gemelar",
        "Gemelos",
        "Calcula la diferencia porcentual de peso entre dos fetos."
    ),
    CPR(
        "Relación cerebroplacentaria",
        "CPR",
        "Calcula CPR con IP de arteria cerebral media e IP umbilical."
    ),
    DOPPLER_QUICK(
        "Doppler rápido",
        "Doppler",
        "Promedios e índices básicos: arterias uterinas, TEI y ductus."
    ),
    CDH_LHR(
        "Hernia diafragmática",
        "LHR",
        "Calcula LHR por método de diámetros pulmonares."
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ObstetricsScreen(modifier: Modifier = Modifier) {
    var selected by rememberSaveable { mutableStateOf(ObstetricCalculator.GESTATIONAL_AGE) }

    BoxWithConstraints(modifier) {
        val expanded = maxWidth >= 840.dp

        if (expanded) {
            Row(
                Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ObstetricCalculatorMenu(
                    selected = selected,
                    onSelected = { selected = it },
                    modifier = Modifier.widthIn(min = 260.dp, max = 330.dp).fillMaxHeight()
                )
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item { ObstetricHeaderCard() }
                    item { ObstetricCalculatorContent(selected) }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(12.dp, 12.dp, 12.dp, 40.dp)
            ) {
                item { ObstetricHeaderCard() }
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ObstetricCalculator.entries.forEach { calculator ->
                            FilterChip(
                                selected = selected == calculator,
                                onClick = { selected = calculator },
                                label = { Text(calculator.shortLabel) }
                            )
                        }
                    }
                }
                item { ObstetricCalculatorContent(selected) }
            }
        }
    }
}

@Composable
private fun ObstetricHeaderCard() {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)
        )
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(
                "Calculadoras gineco-obstétricas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Módulo nativo inspirado en la estructura de calculadoras de Medicina Fetal Barcelona: edad gestacional, biometría, gemelos, Doppler y LHR. No abre páginas externas y no guarda datos capturados.",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Algunos algoritmos clínicos propietarios o dependientes de tablas específicas, como riesgo 1T de preeclampsia o clasificación completa de RCIU, no se reproducen como cálculo diagnóstico automático.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ObstetricCalculatorMenu(
    selected: ObstetricCalculator,
    onSelected: (ObstetricCalculator) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier) {
        Column(
            Modifier.fillMaxSize().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Menú",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            )
            ObstetricCalculator.entries.forEach { calculator ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (selected == calculator) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (selected == calculator) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelected(calculator) }
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            calculator.label,
                            fontWeight = FontWeight.Bold,
                            color = if (selected == calculator) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                        Text(
                            calculator.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ObstetricCalculatorContent(calculator: ObstetricCalculator) {
    when (calculator) {
        ObstetricCalculator.GESTATIONAL_AGE -> GestationalAgeCalculator()
        ObstetricCalculator.ULTRASOUND_DATING -> UltrasoundGestationalAgeCalculator()
        ObstetricCalculator.HADLOCK_EFW -> HadlockCalculator()
        ObstetricCalculator.TWIN_DISCORDANCE -> TwinDiscordanceCalculator()
        ObstetricCalculator.CPR -> CprCalculator()
        ObstetricCalculator.DOPPLER_QUICK -> DopplerQuickCalculator()
        ObstetricCalculator.CDH_LHR -> LhrCalculator()
    }
}

@Composable
private fun GestationalAgeCalculator() {
    var lmp by rememberSaveable { mutableStateOf(LocalDate.now().minusWeeks(20).toString()) }
    var reference by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }

    val lmpDate = remember(lmp) { runCatching { LocalDate.parse(lmp) }.getOrElse { LocalDate.now().minusWeeks(20) } }
    val referenceDate = remember(reference) { runCatching { LocalDate.parse(reference) }.getOrElse { LocalDate.now() } }
    val totalDays = ChronoUnit.DAYS.between(lmpDate, referenceDate).coerceAtLeast(0)
    val weeks = totalDays / 7
    val days = totalDays % 7
    val dueDate = lmpDate.plusDays(280)

    CalculatorCard(
        title = "Edad gestacional por FUM",
        note = "Basada en FUM y regla obstétrica de 280 días. Verifica con ultrasonido cuando corresponda."
    ) {
        DateField("Fecha de última menstruación", lmpDate, { lmp = it.toString() }, maxDate = referenceDate)
        DateField("Fecha de evaluación", referenceDate, { reference = it.toString() }, minDate = lmpDate, maxDate = LocalDate.now().plusDays(1))
        ResultBlock(
            title = "Resultado",
            rows = listOf(
                "Edad gestacional" to "${weeks} semanas + ${days} días",
                "Días de gestación" to "$totalDays días",
                "Fecha probable de parto" to dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UltrasoundGestationalAgeCalculator() {
    var trimester by rememberSaveable { mutableStateOf(UltrasoundTrimester.FIRST) }
    var scanDateText by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var compareWithLmp by rememberSaveable { mutableStateOf(false) }
    var lmpText by rememberSaveable { mutableStateOf(LocalDate.now().minusWeeks(12).toString()) }
    var crlText by rememberSaveable { mutableStateOf("") }
    var bpdText by rememberSaveable { mutableStateOf("") }
    var hcText by rememberSaveable { mutableStateOf("") }
    var acText by rememberSaveable { mutableStateOf("") }
    var flText by rememberSaveable { mutableStateOf("") }

    val scanDate = remember(scanDateText) { runCatching { LocalDate.parse(scanDateText) }.getOrElse { LocalDate.now() } }
    val lmpDate = remember(lmpText, compareWithLmp) {
        if (!compareWithLmp) null else runCatching { LocalDate.parse(lmpText) }.getOrNull()
    }
    val hasInput = when (trimester) {
        UltrasoundTrimester.FIRST -> crlText.isNotBlank()
        UltrasoundTrimester.SECOND, UltrasoundTrimester.THIRD ->
            listOf(bpdText, hcText, acText, flText).any(String::isNotBlank)
    }
    val calculation = remember(trimester, scanDate, lmpDate, crlText, bpdText, hcText, acText, flText, hasInput) {
        if (!hasInput) null else when (trimester) {
            UltrasoundTrimester.FIRST -> UltrasoundDatingCalculator.fromCrl(
                scanDate = scanDate,
                crlMm = crlText.replace(',', '.').toDoubleOrNull() ?: Double.NaN,
                lmpDate = lmpDate
            )
            UltrasoundTrimester.SECOND, UltrasoundTrimester.THIRD -> UltrasoundDatingCalculator.fromBiometry(
                scanDate = scanDate,
                trimester = trimester,
                bpdMm = bpdText.replace(',', '.').toDoubleOrNull(),
                hcMm = hcText.replace(',', '.').toDoubleOrNull(),
                acMm = acText.replace(',', '.').toDoubleOrNull(),
                flMm = flText.replace(',', '.').toDoubleOrNull(),
                lmpDate = lmpDate
            )
        }
    }
    val result = calculation?.getOrNull()
    val error = calculation?.exceptionOrNull()?.message

    CalculatorCard(
        title = "Edad gestacional por ultrasonido",
        note = "Selecciona el trimestre. En 1T se utiliza LCC/CRL; en 2T y 3T se aplican ecuaciones Hadlock con las biometrías disponibles."
    ) {
        Text("Trimestre del estudio", fontWeight = FontWeight.Bold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            UltrasoundTrimester.entries.forEach { option ->
                FilterChip(selected = trimester == option, onClick = { trimester = option }, label = { Text(option.label) })
            }
        }
        DateField("Fecha del ultrasonido", scanDate, { scanDateText = it.toString() }, maxDate = LocalDate.now().plusDays(1))
        Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceContainer, modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = compareWithLmp, onCheckedChange = { compareWithLmp = it })
                Column(Modifier.weight(1f)) {
                    Text("Comparar con FUM", fontWeight = FontWeight.SemiBold)
                    Text("Muestra la diferencia y el umbral ACOG orientativo.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        if (compareWithLmp) {
            DateField("Fecha de última menstruación", lmpDate ?: scanDate.minusWeeks(12), { lmpText = it.toString() }, maxDate = scanDate)
        }
        when (trimester) {
            UltrasoundTrimester.FIRST -> {
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.48f), modifier = Modifier.fillMaxWidth()) {
                    Text("Primer trimestre: captura LCC/CRL en milímetros. Intervalo admitido: 5–84 mm.", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                FormTextField("LCC / CRL", crlText, { crlText = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "mm", placeholder = "Ej. 50")
            }
            UltrasoundTrimester.SECOND, UltrasoundTrimester.THIRD -> {
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.48f), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        if (trimester == UltrasoundTrimester.SECOND) "Segundo trimestre: biometría compuesta de 14+0 a 27+6 semanas. Captura una o varias medidas."
                        else "Tercer trimestre: biometría desde 28+0 semanas. La precisión para datación es menor.",
                        modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                FormTextField("DBP / BPD", bpdText, { bpdText = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "mm")
                FormTextField("CC / HC", hcText, { hcText = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "mm")
                FormTextField("CA / AC", acText, { acText = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "mm")
                FormTextField("LF / FL", flText, { flText = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "mm")
            }
        }
        if (error != null) {
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.errorContainer, modifier = Modifier.fillMaxWidth()) {
                Text(error, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodySmall)
            }
        }
        result?.let { dating ->
            ResultBlock(
                title = "Resultado por ultrasonido",
                rows = listOf(
                    "Edad gestacional" to dating.gestationalAgeLabel,
                    "Fecha probable de parto" to dating.estimatedDueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    "Método" to dating.method,
                    "Mediciones utilizadas" to dating.measurementSummary,
                    "Precisión aproximada" to dating.expectedAccuracy
                )
            )
            dating.lmpComparison?.let { comparison ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (comparison.exceedsThreshold) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("Comparación contra FUM", fontWeight = FontWeight.Bold)
                        Text("Diferencia: ${comparison.differenceDays} días", style = MaterialTheme.typography.bodyMedium)
                        Text(comparison.message, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            dating.warnings.forEach { warning ->
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.tertiaryContainer, modifier = Modifier.fillMaxWidth()) {
                    Text(warning, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onTertiaryContainer, style = MaterialTheme.typography.bodySmall)
                }
            }
            Text("La edad calculada es una estimación ultrasonográfica y no sustituye la fecha obstétrica final documentada ni la valoración clínica.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun HadlockCalculator() {
    var bpdText by rememberSaveable { mutableStateOf("") }
    var hcText by rememberSaveable { mutableStateOf("") }
    var acText by rememberSaveable { mutableStateOf("") }
    var flText by rememberSaveable { mutableStateOf("") }

    val bpd = bpdText.replace(',', '.').toDoubleOrNull()
    val hc = hcText.replace(',', '.').toDoubleOrNull()
    val ac = acText.replace(',', '.').toDoubleOrNull()
    val fl = flText.replace(',', '.').toDoubleOrNull()

    val efw = if (bpd != null && hc != null && ac != null && fl != null) {
        val log10Weight = 1.3596 - (0.00386 * ac * fl) + (0.0064 * hc) + (0.00061 * bpd * ac) + (0.0424 * ac) + (0.174 * fl)
        10.0.pow(log10Weight)
    } else null

    CalculatorCard(
        title = "Peso fetal estimado · Hadlock",
        note = "Introduce biometría en milímetros. La app calcula EFW, no percentil fetal automático."
    ) {
        FormTextField("DBP / BPD", bpdText, { bpdText = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "mm")
        FormTextField("CC / HC", hcText, { hcText = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "mm")
        FormTextField("CA / AC", acText, { acText = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "mm")
        FormTextField("LF / FL", flText, { flText = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "mm")
        ResultBlock(
            title = "Resultado",
            rows = listOf(
                "Peso fetal estimado" to (efw?.let { "${formatDecimal(it, 0)} g" } ?: "Pendiente"),
                "Fórmula" to "Hadlock 4 parámetros"
            )
        )
    }
}

@Composable
private fun TwinDiscordanceCalculator() {
    var twinA by rememberSaveable { mutableStateOf("") }
    var twinB by rememberSaveable { mutableStateOf("") }

    val a = twinA.replace(',', '.').toDoubleOrNull()
    val b = twinB.replace(',', '.').toDoubleOrNull()
    val discordance = if (a != null && b != null && max(a, b) > 0.0) {
        abs(a - b) / max(a, b) * 100.0
    } else null

    CalculatorCard(
        title = "Discordancia de peso gemelar",
        note = "Cálculo porcentual entre el feto mayor y el menor. No sustituye evaluación corionicidad/Doppler."
    ) {
        FormTextField("Peso feto A", twinA, { twinA = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "g")
        FormTextField("Peso feto B", twinB, { twinB = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "g")
        ResultBlock(
            title = "Resultado",
            rows = listOf(
                "Discordancia" to (discordance?.let { "${formatDecimal(it, 1)} %" } ?: "Pendiente"),
                "Interpretación orientativa" to when {
                    discordance == null -> "Captura ambos pesos"
                    discordance >= 25.0 -> "Discordancia importante"
                    discordance >= 20.0 -> "Vigilancia estrecha"
                    else -> "Sin discordancia marcada"
                }
            )
        )
    }
}

@Composable
private fun CprCalculator() {
    var mcaText by rememberSaveable { mutableStateOf("") }
    var uaText by rememberSaveable { mutableStateOf("") }

    val mca = mcaText.replace(',', '.').toDoubleOrNull()
    val ua = uaText.replace(',', '.').toDoubleOrNull()
    val cpr = if (mca != null && ua != null && ua > 0.0) mca / ua else null

    CalculatorCard(
        title = "Relación cerebroplacentaria",
        note = "Calcula CPR = IP ACM / IP AU. El percentil requiere tablas por edad gestacional."
    ) {
        FormTextField("IP arteria cerebral media", mcaText, { mcaText = decimalText(it) }, keyboardType = KeyboardType.Decimal)
        FormTextField("IP arteria umbilical", uaText, { uaText = decimalText(it) }, keyboardType = KeyboardType.Decimal)
        ResultBlock(
            title = "Resultado",
            rows = listOf(
                "CPR" to (cpr?.let { formatDecimal(it, 2) } ?: "Pendiente"),
                "Lectura rápida" to when {
                    cpr == null -> "Captura ambos IP"
                    cpr < 1.0 -> "CPR bajo de forma orientativa"
                    else -> "CPR no bajo por corte simple"
                }
            )
        )
    }
}

@Composable
private fun DopplerQuickCalculator() {
    var rightUt by rememberSaveable { mutableStateOf("") }
    var leftUt by rememberSaveable { mutableStateOf("") }
    var ictText by rememberSaveable { mutableStateOf("") }
    var irtText by rememberSaveable { mutableStateOf("") }
    var etText by rememberSaveable { mutableStateOf("") }

    val r = rightUt.replace(',', '.').toDoubleOrNull()
    val l = leftUt.replace(',', '.').toDoubleOrNull()
    val meanUt = if (r != null && l != null) (r + l) / 2.0 else null

    val ict = ictText.replace(',', '.').toDoubleOrNull()
    val irt = irtText.replace(',', '.').toDoubleOrNull()
    val et = etText.replace(',', '.').toDoubleOrNull()
    val tei = if (ict != null && irt != null && et != null && et > 0.0) (ict + irt) / et else null

    CalculatorCard(
        title = "Doppler rápido",
        note = "Agrupa cálculos aritméticos usados en Doppler. Los percentiles dependen de edad gestacional y tablas."
    ) {
        FormTextField("IP uterina derecha", rightUt, { rightUt = decimalText(it) }, keyboardType = KeyboardType.Decimal)
        FormTextField("IP uterina izquierda", leftUt, { leftUt = decimalText(it) }, keyboardType = KeyboardType.Decimal)
        HorizontalDivider()
        FormTextField("Tiempo contracción isovolumétrica", ictText, { ictText = decimalText(it) }, keyboardType = KeyboardType.Decimal)
        FormTextField("Tiempo relajación isovolumétrica", irtText, { irtText = decimalText(it) }, keyboardType = KeyboardType.Decimal)
        FormTextField("Tiempo de eyección", etText, { etText = decimalText(it) }, keyboardType = KeyboardType.Decimal)
        ResultBlock(
            title = "Resultado",
            rows = listOf(
                "IP uterina media" to (meanUt?.let { formatDecimal(it, 2) } ?: "Pendiente"),
                "Índice TEI" to (tei?.let { formatDecimal(it, 2) } ?: "Pendiente")
            )
        )
    }
}

@Composable
private fun LhrCalculator() {
    var longText by rememberSaveable { mutableStateOf("") }
    var transText by rememberSaveable { mutableStateOf("") }
    var hcText by rememberSaveable { mutableStateOf("") }

    val longitudinal = longText.replace(',', '.').toDoubleOrNull()
    val transverse = transText.replace(',', '.').toDoubleOrNull()
    val hc = hcText.replace(',', '.').toDoubleOrNull()
    val area = if (longitudinal != null && transverse != null) longitudinal * transverse else null
    val lhr = if (area != null && hc != null && hc > 0.0) area / hc else null

    CalculatorCard(
        title = "LHR · hernia diafragmática",
        note = "Método de diámetros: área pulmonar = diámetro longitudinal × transversal; LHR = área / CC."
    ) {
        FormTextField("Diámetro longitudinal pulmonar", longText, { longText = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "mm")
        FormTextField("Diámetro transversal pulmonar", transText, { transText = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "mm")
        FormTextField("Circunferencia cefálica", hcText, { hcText = decimalText(it) }, keyboardType = KeyboardType.Decimal, suffix = "mm")
        ResultBlock(
            title = "Resultado",
            rows = listOf(
                "Área pulmonar" to (area?.let { "${formatDecimal(it, 1)} mm²" } ?: "Pendiente"),
                "LHR" to (lhr?.let { formatDecimal(it, 2) } ?: "Pendiente"),
                "o/e LHR" to "Requiere tablas esperadas por edad gestacional"
            )
        )
    }
}

@Composable
private fun CalculatorCard(
    title: String,
    note: String,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard {
        Column(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}

@Composable
private fun ResultBlock(
    title: String,
    rows: List<Pair<String, String>>
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            rows.forEach { (label, value) ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PercentilesScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val engine = remember(context) { GrowthEngine(context) }
    val calculationScope = rememberCoroutineScope()
    var calculating by remember { mutableStateOf(false) }
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
            } else if (!calculating) {
                calculating = true
                error = null

                calculationScope.launch {
                    val result = withContext(Dispatchers.Default) {
                        engine.assess(
                            sex = sex,
                            birthDate = birth,
                            measurementDate = measured,
                            weightKg = weight,
                            heightCm = height,
                            measurementMode = measurementMode
                        )
                    }

                    result.onSuccess {
                        assessment = it
                        error = null
                    }.onFailure {
                        assessment = null
                        error = it.message
                            ?: "No fue posible calcular los percentiles."
                    }

                    calculating = false
                }
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
                        calculating = calculating,
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
                        calculating = calculating,
                        error = error
                    )
                }
                assessment?.let { result ->
                    item { PercentileAssessmentSummary(result) }
                    item { NutritionStatusCard(result.nutritionSummary) }
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
                "Calcula peso y talla para la edad, IMC para la edad y, hasta los 5 años, peso para la longitud o talla. También muestra la situación nutricional y los pesos aproximados desde los que corresponden sobrepeso y obesidad.",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Los datos introducidos no se guardan. El resultado es orientativo y debe interpretarse junto con la evolución clínica y mediciones previas.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(11.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Cotejo OMS/CDC",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "La app mantiene OMS como referencia principal: estándares 0-5 años y referencia 5-19 años. CDC queda documentado como referencia alternativa, especialmente para población de EE. UU. y BMI extendido 2-20 años. No se mezclan curvas en un mismo cálculo.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
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
    calculating: Boolean,
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
            Button(
                onClick = onCalculate,
                enabled = !calculating,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (calculating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Calculando...")
                } else {
                    Text("Calcular percentiles")
                }
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
    var showCalendar by rememberSaveable {
        mutableStateOf(false)
    }
    val formatter = remember {
        DateTimeFormatter.ofPattern(
            "EEEE, d 'de' MMMM 'de' yyyy",
            Locale("es", "MX")
        )
    }
    val monthFormatter = remember {
        DateTimeFormatter.ofPattern(
            "MMM",
            Locale("es", "MX")
        )
    }

    Surface(
        onClick = { showCalendar = true },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Column(
                    modifier = Modifier
                        .width(62.dp)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        monthFormatter.format(date).uppercase(
                            Locale("es", "MX")
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Column(Modifier.weight(1f)) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    formatter.format(date).replaceFirstChar {
                        if (it.isLowerCase()) {
                            it.titlecase(Locale("es", "MX"))
                        } else {
                            it.toString()
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
            }

            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Abrir calendario",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showCalendar) {
        ClinicalCalendarDialog(
            title = label,
            initialDate = date,
            minDate = minDate,
            maxDate = maxDate,
            onDismiss = { showCalendar = false },
            onConfirm = {
                onDate(it)
                showCalendar = false
            }
        )
    }
}


private data class CalendarPartOption<T>(
    val value: T,
    val label: String,
    val enabled: Boolean = true
)

@Composable
private fun <T> CalendarDatePartSelector(
    label: String,
    selectedValue: T,
    selectedLabel: String,
    options: List<CalendarPartOption<T>>,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (expanded) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
            ),
            tonalElevation = if (expanded) 4.dp else 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(
                    horizontal = 11.dp,
                    vertical = 9.dp
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        selectedLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Elegir $label",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 340.dp)
                .widthIn(min = 150.dp)
        ) {
            options.forEach { option ->
                val selected = option.value == selectedValue

                DropdownMenuItem(
                    text = {
                        Text(
                            option.label,
                            fontWeight = if (selected) {
                                FontWeight.Black
                            } else {
                                FontWeight.Medium
                            },
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    trailingIcon = {
                        if (selected) {
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    "✓",
                                    modifier = Modifier.padding(
                                        horizontal = 7.dp,
                                        vertical = 2.dp
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    },
                    enabled = option.enabled,
                    onClick = {
                        onSelected(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun clampCalendarDate(
    date: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?
): LocalDate {
    var result = date

    if (minDate != null && result.isBefore(minDate)) {
        result = minDate
    }
    if (maxDate != null && result.isAfter(maxDate)) {
        result = maxDate
    }

    return result
}

@Composable
private fun ClinicalCalendarDialog(
    title: String,
    initialDate: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    var selectedDateText by rememberSaveable(initialDate) {
        mutableStateOf(initialDate.toString())
    }
    var visibleMonthText by rememberSaveable(initialDate) {
        mutableStateOf(
            YearMonth.from(initialDate).toString()
        )
    }

    val selectedDate = remember(selectedDateText) {
        LocalDate.parse(selectedDateText)
    }
    val visibleMonth = remember(visibleMonthText) {
        YearMonth.parse(visibleMonthText)
    }
    val today = remember { LocalDate.now() }
    val monthTitleFormatter = remember {
        DateTimeFormatter.ofPattern(
            "MMMM yyyy",
            Locale("es", "MX")
        )
    }

    val minimumMonth = minDate?.let(YearMonth::from)
    val maximumMonth = maxDate?.let(YearMonth::from)
    val canGoPrevious = minimumMonth == null ||
        visibleMonth.isAfter(minimumMonth)
    val canGoNext = maximumMonth == null ||
        visibleMonth.isBefore(maximumMonth)

    val minimumYear = minDate?.year ?: 1900
    val maximumYear = maxDate?.year ?: (today.year + 10)

    val yearOptions = remember(
        minimumYear,
        maximumYear
    ) {
        (minimumYear..maximumYear)
            .reversed()
            .map {
                CalendarPartOption(
                    value = it,
                    label = it.toString()
                )
            }
    }

    val monthFormatter = remember {
        DateTimeFormatter.ofPattern(
            "MMMM",
            Locale("es", "MX")
        )
    }

    val monthOptions = remember(
        selectedDate.year,
        minDate,
        maxDate
    ) {
        (1..12).map { monthNumber ->
            val candidateMonth = YearMonth.of(
                selectedDate.year,
                monthNumber
            )
            val monthStart = candidateMonth.atDay(1)
            val monthEnd = candidateMonth.atEndOfMonth()
            val enabled = (
                (minDate == null ||
                    !monthEnd.isBefore(minDate)) &&
                    (maxDate == null ||
                        !monthStart.isAfter(maxDate))
                )

            CalendarPartOption(
                value = monthNumber,
                label = candidateMonth.format(
                    monthFormatter
                ).replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(Locale("es", "MX"))
                    } else {
                        it.toString()
                    }
                },
                enabled = enabled
            )
        }
    }

    val dayOptions = remember(
        selectedDate.year,
        selectedDate.monthValue,
        minDate,
        maxDate
    ) {
        val selectedMonth = YearMonth.of(
            selectedDate.year,
            selectedDate.monthValue
        )

        (1..selectedMonth.lengthOfMonth()).map { day ->
            val candidate = selectedMonth.atDay(day)
            val enabled = (
                (minDate == null ||
                    !candidate.isBefore(minDate)) &&
                    (maxDate == null ||
                        !candidate.isAfter(maxDate))
                )

            CalendarPartOption(
                value = day,
                label = day.toString(),
                enabled = enabled
            )
        }
    }

    fun updateDateParts(
        year: Int = selectedDate.year,
        month: Int = selectedDate.monthValue,
        day: Int = selectedDate.dayOfMonth
    ) {
        val targetMonth = YearMonth.of(year, month)
        val safeDay = day.coerceIn(
            1,
            targetMonth.lengthOfMonth()
        )
        val candidate = targetMonth.atDay(safeDay)
        val clamped = clampCalendarDate(
            candidate,
            minDate,
            maxDate
        )

        selectedDateText = clamped.toString()
        visibleMonthText = YearMonth.from(clamped)
            .toString()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .widthIn(max = 520.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 10.dp,
            shadowElevation = 14.dp
        ) {
            Column {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            selectedDate.format(
                                DateTimeFormatter.ofPattern(
                                    "d 'de' MMMM 'de' yyyy",
                                    Locale("es", "MX")
                                )
                            ).replaceFirstChar {
                                if (it.isLowerCase()) {
                                    it.titlecase(Locale("es", "MX"))
                                } else {
                                    it.toString()
                                }
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Column(
                    Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(
                            alpha = 0.48f
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(9.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(7.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        "Ir directamente a una fecha",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        "Elige año, mes y día sin recorrer el calendario.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                            alpha = 0.78f
                                        )
                                    )
                                }
                            }

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CalendarDatePartSelector(
                                    label = "Día",
                                    selectedValue = selectedDate.dayOfMonth,
                                    selectedLabel = selectedDate.dayOfMonth.toString(),
                                    options = dayOptions,
                                    onSelected = {
                                        updateDateParts(day = it)
                                    },
                                    modifier = Modifier.weight(0.72f)
                                )
                                CalendarDatePartSelector(
                                    label = "Mes",
                                    selectedValue = selectedDate.monthValue,
                                    selectedLabel = selectedDate.format(
                                        monthFormatter
                                    ).replaceFirstChar {
                                        if (it.isLowerCase()) {
                                            it.titlecase(Locale("es", "MX"))
                                        } else {
                                            it.toString()
                                        }
                                    },
                                    options = monthOptions,
                                    onSelected = {
                                        updateDateParts(month = it)
                                    },
                                    modifier = Modifier.weight(1.35f)
                                )
                                CalendarDatePartSelector(
                                    label = "Año",
                                    selectedValue = selectedDate.year,
                                    selectedLabel = selectedDate.year.toString(),
                                    options = yearOptions,
                                    onSelected = {
                                        updateDateParts(year = it)
                                    },
                                    modifier = Modifier.weight(0.95f)
                                )
                            }
                        }
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                visibleMonthText = visibleMonth
                                    .minusMonths(1)
                                    .toString()
                            },
                            enabled = canGoPrevious
                        ) {
                            Icon(
                                Icons.Default.ChevronLeft,
                                contentDescription = "Mes anterior"
                            )
                        }

                        Text(
                            visibleMonth.format(
                                monthTitleFormatter
                            ).replaceFirstChar {
                                if (it.isLowerCase()) {
                                    it.titlecase(Locale("es", "MX"))
                                } else {
                                    it.toString()
                                }
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = {
                                visibleMonthText = visibleMonth
                                    .plusMonths(1)
                                    .toString()
                            },
                            enabled = canGoNext
                        ) {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "Mes siguiente"
                            )
                        }
                    }

                    CalendarWeekHeader()
                    CalendarMonthGrid(
                        month = visibleMonth,
                        selectedDate = selectedDate,
                        today = today,
                        minDate = minDate,
                        maxDate = maxDate,
                        onSelected = {
                            selectedDateText = it.toString()
                        }
                    )

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (
                            (minDate == null || !today.isBefore(minDate)) &&
                            (maxDate == null || !today.isAfter(maxDate))
                        ) {
                            AssistChip(
                                onClick = {
                                    selectedDateText = today.toString()
                                    visibleMonthText = YearMonth.from(today)
                                        .toString()
                                },
                                label = { Text("Hoy") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }

                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = onDismiss) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                onConfirm(selectedDate)
                            }
                        ) {
                            Text("Aceptar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarWeekHeader() {
    val labels = listOf(
        "L",
        "M",
        "X",
        "J",
        "V",
        "S",
        "D"
    )

    Row(Modifier.fillMaxWidth()) {
        labels.forEach { label ->
            Text(
                label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CalendarMonthGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    onSelected: (LocalDate) -> Unit
) {
    val firstDayOffset = (
        month.atDay(1).dayOfWeek.value -
            DayOfWeek.MONDAY.value +
            7
        ) % 7
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(6) { week ->
            Row(Modifier.fillMaxWidth()) {
                repeat(7) { weekday ->
                    val cellIndex = week * 7 + weekday
                    val dayNumber = cellIndex -
                        firstDayOffset + 1

                    if (
                        dayNumber !in 1..month.lengthOfMonth()
                    ) {
                        Spacer(
                            Modifier
                                .weight(1f)
                                .height(42.dp)
                        )
                    } else {
                        val date = month.atDay(dayNumber)
                        val enabled = (
                            (minDate == null ||
                                !date.isBefore(minDate)) &&
                                (maxDate == null ||
                                    !date.isAfter(maxDate))
                            )
                        val selected = date == selectedDate
                        val isToday = date == today

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                onClick = {
                                    if (enabled) {
                                        onSelected(date)
                                    }
                                },
                                enabled = enabled,
                                shape = RoundedCornerShape(14.dp),
                                color = when {
                                    selected ->
                                        MaterialTheme.colorScheme.primary
                                    isToday ->
                                        MaterialTheme.colorScheme
                                            .secondaryContainer
                                    else -> Color.Transparent
                                },
                                contentColor = when {
                                    selected ->
                                        MaterialTheme.colorScheme.onPrimary
                                    isToday ->
                                        MaterialTheme.colorScheme
                                            .onSecondaryContainer
                                    enabled ->
                                        MaterialTheme.colorScheme.onSurface
                                    else ->
                                        MaterialTheme.colorScheme
                                            .onSurface.copy(alpha = 0.32f)
                                },
                                border = if (isToday && !selected) {
                                    androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                } else {
                                    null
                                }
                            ) {
                                Box(
                                    modifier = Modifier.size(38.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        dayNumber.toString(),
                                        fontWeight = if (
                                            selected || isToday
                                        ) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Medium
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
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
        item { NutritionStatusCard(assessment.nutritionSummary) }
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
private fun NutritionStatusCard(summary: NutritionSummary) {
    val containerColor = when (summary.status) {
        NutritionStatus.LOW_WEIGHT -> Color(0xFFDCEEFF)
        NutritionStatus.EXPECTED -> Color(0xFFDDF5E5)
        NutritionStatus.OVERWEIGHT -> Color(0xFFFFE0A6)
        NutritionStatus.OBESITY -> Color(0xFFFFC7C7)
    }

    val contentColor = when (summary.status) {
        NutritionStatus.LOW_WEIGHT -> Color(0xFF164E7A)
        NutritionStatus.EXPECTED -> Color(0xFF14532D)
        NutritionStatus.OVERWEIGHT -> Color(0xFF7C4200)
        NutritionStatus.OBESITY -> Color(0xFF8B1E1E)
    }

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "Situación nutricional",
                style = MaterialTheme.typography.labelLarge,
                color = contentColor
            )

            Surface(
                color = contentColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    summary.label,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                "${summary.reference} · ${formatPercentile(summary.bmiPercentile)}",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor
            )

            ThresholdRow(
                label = "Sobrepeso a partir de:",
                value = "${formatDecimal(summary.overweightFromKg, 1)} kg",
                color = contentColor
            )
            ThresholdRow(
                label = "Obesidad a partir de:",
                value = "${formatDecimal(summary.obesityFromKg, 1)} kg",
                color = contentColor
            )

            Text(
                "Los límites se calculan para la edad, sexo y talla capturados. Son orientativos y no sustituyen la valoración clínica.",
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.82f)
            )
        }
    }
}

@Composable
private fun ThresholdRow(
    label: String,
    value: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.55f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.weight(1f)
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
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
    var showExpanded by remember { mutableStateOf(false) }

    BoxWithConstraints {
        val previewHeight = if (maxWidth >= 600.dp) 440.dp else 330.dp

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    chart.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Toca para ampliar",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(previewHeight)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { showExpanded = true }
            ) {
                GrowthChartCanvas(
                    chart = chart,
                    modifier = Modifier.fillMaxSize(),
                    zoom = 1f,
                    pan = Offset.Zero
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.94f)
                ) {
                    Text(
                        "⛶ Ampliar gráfica",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                "Eje X: ${chart.xLabel} · Eje Y: ${chart.yLabel}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ChartLegend(chart)
        }
    }

    if (showExpanded) {
        ExpandedGrowthChartDialog(
            chart = chart,
            onDismiss = { showExpanded = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExpandedGrowthChartDialog(
    chart: GrowthChart,
    onDismiss: () -> Unit
) {
    var zoom by remember(chart) { mutableStateOf(1f) }
    var pan by remember(chart) { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        val nextZoom = (zoom * zoomChange).coerceIn(1f, 6f)
        zoom = nextZoom
        pan = if (nextZoom <= 1.01f) {
            Offset.Zero
        } else {
            pan + panChange
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            chart.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Pellizca para acercar · arrastra para desplazarte · doble toque para alternar zoom",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar gráfica")
                    }
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Zoom ${formatDecimal(zoom.toDouble(), 1)}×") }
                    )
                    TextButton(
                        onClick = {
                            zoom = 1f
                            pan = Offset.Zero
                        }
                    ) {
                        Text("Restablecer")
                    }
                    TextButton(
                        onClick = {
                            zoom = 2.5f
                            pan = Offset.Zero
                        }
                    ) {
                        Text("Acercar")
                    }
                }

                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(16.dp)
                        )
                        .transformable(transformState)
                        .pointerInput(chart) {
                            detectTapGestures(
                                onDoubleTap = {
                                    zoom = if (zoom > 1.1f) 1f else 2.5f
                                    pan = Offset.Zero
                                }
                            )
                        }
                ) {
                    GrowthChartCanvas(
                        chart = chart,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = zoom,
                                scaleY = zoom,
                                translationX = pan.x,
                                translationY = pan.y
                            ),
                        zoom = zoom,
                        pan = pan
                    )
                }

                Text(
                    "Medición: ${formatDecimal(chart.patientPoint.x, 2)} en eje X · ${formatDecimal(chart.patientPoint.y, 2)} ${chart.yLabel}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                ChartLegend(chart)
            }
        }
    }
}

@Composable
private fun GrowthChartCanvas(
    chart: GrowthChart,
    modifier: Modifier,
    zoom: Float,
    pan: Offset
) {
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

    val allPoints = remember(chart) {
        chart.curves.flatMap { it.points } + chart.patientPoint
    }
    val minX = allPoints.minOfOrNull { it.x } ?: 0.0
    val maxX = allPoints.maxOfOrNull { it.x } ?: 1.0
    val rawMinY = allPoints.minOfOrNull { it.y } ?: 0.0
    val rawMaxY = allPoints.maxOfOrNull { it.y } ?: 1.0
    val yPadding = max((rawMaxY - rawMinY) * 0.08, 0.5)
    val minY = max(0.0, rawMinY - yPadding)
    val maxY = rawMaxY + yPadding

    Canvas(modifier.padding(5.dp)) {
        val left = 58.dp.toPx()
        val right = 18.dp.toPx()
        val top = 22.dp.toPx()
        val bottom = 44.dp.toPx()
        val plotWidth = (size.width - left - right).coerceAtLeast(1f)
        val plotHeight = (size.height - top - bottom).coerceAtLeast(1f)

        fun xToPx(value: Double): Float {
            val ratio = if (maxX == minX) 0.0 else (value - minX) / (maxX - minX)
            return left + (ratio * plotWidth).toFloat()
        }

        fun yToPx(value: Double): Float {
            val ratio = if (maxY == minY) 0.0 else (value - minY) / (maxY - minY)
            return top + ((1.0 - ratio) * plotHeight).toFloat()
        }

        repeat(6) { index ->
            val ratio = index / 5f
            val y = top + ratio * plotHeight
            drawLine(
                gridColor,
                Offset(left, y),
                Offset(left + plotWidth, y),
                strokeWidth = 1.dp.toPx()
            )
            val value = maxY - ratio * (maxY - minY)
            val paint = AndroidPaint().apply {
                color = axisColor.toArgb()
                textSize = 10.sp.toPx()
                textAlign = AndroidPaint.Align.RIGHT
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText(
                formatDecimal(value, 1),
                left - 7.dp.toPx(),
                y + 4.dp.toPx(),
                paint
            )
        }

        repeat(6) { index ->
            val ratio = index / 5f
            val x = left + ratio * plotWidth
            drawLine(
                gridColor,
                Offset(x, top),
                Offset(x, top + plotHeight),
                strokeWidth = 1.dp.toPx()
            )
            val value = minX + ratio * (maxX - minX)
            val paint = AndroidPaint().apply {
                color = axisColor.toArgb()
                textSize = 10.sp.toPx()
                textAlign = AndroidPaint.Align.CENTER
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText(
                chart.xFormatter(value),
                x,
                top + plotHeight + 20.dp.toPx(),
                paint
            )
        }

        drawLine(
            axisColor,
            Offset(left, top),
            Offset(left, top + plotHeight),
            strokeWidth = 1.5.dp.toPx()
        )
        drawLine(
            axisColor,
            Offset(left, top + plotHeight),
            Offset(left + plotWidth, top + plotHeight),
            strokeWidth = 1.5.dp.toPx()
        )

        chart.curves.forEachIndexed { index, curve ->
            val path = Path()
            curve.points.forEachIndexed { pointIndex, point ->
                val x = xToPx(point.x)
                val y = yToPx(point.y)
                if (pointIndex == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            drawPath(
                path = path,
                color = curveColors[index % curveColors.size],
                style = Stroke(
                    width = 2.4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        val patientX = xToPx(chart.patientPoint.x)
        val patientY = yToPx(chart.patientPoint.y)

        drawCircle(
            color = patientColor.copy(alpha = 0.2f),
            radius = 13.dp.toPx(),
            center = Offset(patientX, patientY)
        )
        drawCircle(
            Color.White,
            radius = 8.dp.toPx(),
            center = Offset(patientX, patientY)
        )
        drawCircle(
            patientColor,
            radius = 5.5.dp.toPx(),
            center = Offset(patientX, patientY)
        )

        val labelPaint = AndroidPaint().apply {
            color = patientColor.toArgb()
            textSize = 11.sp.toPx()
            textAlign = AndroidPaint.Align.LEFT
            isAntiAlias = true
            isFakeBoldText = true
        }
        drawContext.canvas.nativeCanvas.drawText(
            "Niño/a",
            patientX + 9.dp.toPx(),
            patientY - 9.dp.toPx(),
            labelPaint
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChartLegend(chart: GrowthChart) {
    val curveColors = listOf(
        Color(0xFF6A5ACD),
        Color(0xFF1E88E5),
        Color(0xFF00897B),
        Color(0xFFF9A825),
        Color(0xFFD81B60)
    )
    val patientColor = MaterialTheme.colorScheme.error

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        chart.curves.forEachIndexed { index, curve ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    Modifier
                        .size(10.dp)
                        .background(
                            curveColors[index % curveColors.size],
                            RoundedCornerShape(50)
                        )
                )
                Text(curve.label, style = MaterialTheme.typography.labelSmall)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                Modifier
                    .size(10.dp)
                    .background(patientColor, RoundedCornerShape(50))
            )
            Text("Medición", style = MaterialTheme.typography.labelSmall)
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
    suffix: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        placeholder = {
            if (placeholder.isNotBlank()) {
                Text(placeholder)
            }
        },
        suffix = if (suffix.isNotBlank()) {
            { Text(suffix) }
        } else {
            null
        },
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
