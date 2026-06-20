package com.luisangel.calculadoramedicamentos.ui.ecg

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.luisangel.calculadoramedicamentos.ecg.AxisResult
import com.luisangel.calculadoramedicamentos.ecg.EcgAxisMethod
import com.luisangel.calculadoramedicamentos.ecg.EcgCalculator
import com.luisangel.calculadoramedicamentos.ecg.EcgLeadGroup
import com.luisangel.calculadoramedicamentos.ecg.EcgPaperSpeed
import com.luisangel.calculadoramedicamentos.ecg.EcgSex
import com.luisangel.calculadoramedicamentos.ecg.EcgSummaryInput
import com.luisangel.calculadoramedicamentos.ecg.LvhResult
import com.luisangel.calculadoramedicamentos.ecg.QtcResult
import com.luisangel.calculadoramedicamentos.ecg.StElevationResult
import kotlin.math.roundToInt

private enum class EcgTool(
    val label: String,
    val shortLabel: String,
    val description: String,
    val icon: ImageVector
) {
    ANALYZER(
        label = "Analizador guiado",
        shortLabel = "Analizador",
        description = "Integra ritmo, intervalos, eje, ST y HVI en un resumen copiable.",
        icon = Icons.Default.MonitorHeart
    ),
    RATE(
        label = "Frecuencia y RR",
        shortLabel = "FC/RR",
        description = "Calcula frecuencia por RR, cuadros o tira de 10 segundos.",
        icon = Icons.Default.Speed
    ),
    QTC(
        label = "QT corregido",
        shortLabel = "QTc",
        description = "Bazett, Fridericia, Framingham y Hodges.",
        icon = Icons.Default.Calculate
    ),
    AXIS(
        label = "Eje eléctrico",
        shortLabel = "Eje",
        description = "Cálculo vectorial con DI+aVF o DI+DIII.",
        icon = Icons.Default.Timeline
    ),
    LVH(
        label = "Hipertrofia VI",
        shortLabel = "HVI",
        description = "Sokolow-Lyon, Cornell voltaje y Cornell producto.",
        icon = Icons.Default.Favorite
    ),
    ST(
        label = "Elevación del ST",
        shortLabel = "ST",
        description = "Umbrales por edad, sexo y derivaciones contiguas.",
        icon = Icons.Default.WarningAmber
    )
}

private data class EcgReferenceItem(
    val source: String,
    val citation: String,
    val useInApp: String
)

private data class EcgInfoContent(
    val title: String,
    val purpose: String,
    val method: String,
    val references: List<EcgReferenceItem>,
    val limitations: List<String>,
    val reviewedOn: String = "19 de junio de 2026"
)

@Composable
fun EcgScreen(modifier: Modifier = Modifier) {
    var selectedName by rememberSaveable { mutableStateOf(EcgTool.ANALYZER.name) }
    var ecgMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val selected = remember(selectedName) { EcgTool.valueOf(selectedName) }

    BoxWithConstraints(modifier = modifier) {
        val compactHeight = maxHeight < 520.dp
        val padding = if (compactHeight) 8.dp else 14.dp

        EcgToolBody(
            selected = selected,
            compact = compactHeight,
            contentPadding = PaddingValues(padding),
            modifier = Modifier.fillMaxSize(),
            menu = {
                EcgCollapsibleToolMenu(
                    selected = selected,
                    expanded = ecgMenuExpanded,
                    onToggle = { ecgMenuExpanded = !ecgMenuExpanded },
                    onSelected = { tool ->
                        selectedName = tool.name
                        ecgMenuExpanded = false
                    },
                    compact = compactHeight,
                    availableWidth = maxWidth
                )
            }
        )
    }
}

@Composable
private fun EcgToolBody(
    selected: EcgTool,
    compact: Boolean,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    menu: (@Composable () -> Unit)? = null
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 12.dp)
    ) {
        item { EcgHeaderCard(selected, compact) }
        if (menu != null) {
            item { menu() }
        }
        item { EcgInfoButton(info = selected.info()) }
        item { EcgCalculatorContent(selected) }
    }
}

@Composable
private fun EcgCollapsibleToolMenu(
    selected: EcgTool,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelected: (EcgTool) -> Unit,
    compact: Boolean,
    availableWidth: Dp,
    modifier: Modifier = Modifier
) {
    val expandedButtonWidth = if (compact) 178.dp else 206.dp
    val collapsedButtonWidth = if (compact) 52.dp else 58.dp
    val buttonHeight = if (compact) 46.dp else 52.dp
    val buttonWidth by animateDpAsState(
        targetValue = if (expanded) expandedButtonWidth else collapsedButtonWidth,
        label = "ecgMenuButtonWidth"
    )
    val panelMaxHeight = if (compact) 250.dp else 420.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp)
    ) {
        Surface(
            onClick = onToggle,
            modifier = Modifier
                .width(buttonWidth)
                .height(buttonHeight),
            shape = RoundedCornerShape(if (compact) 16.dp else 18.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.primary,
            border = BorderStroke(
                width = 1.4.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.82f)
            ),
            tonalElevation = if (expanded) 5.dp else 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = if (compact) 9.dp else 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                EcgHeartMenuIcon(
                    modifier = Modifier.size(if (compact) 29.dp else 33.dp)
                )
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Menú ECG",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 980.dp)
                    .heightIn(max = panelMaxHeight),
                shape = RoundedCornerShape(if (compact) 20.dp else 26.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                tonalElevation = 8.dp,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(if (compact) 9.dp else 14.dp),
                    verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp)
                ) {
                    Text(
                        "Selecciona una herramienta de ECG",
                        style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    EcgToolGridRows(
                        selected = selected,
                        onSelected = onSelected,
                        compact = compact,
                        availableWidth = availableWidth
                    )
                }
            }
        }
    }
}

@Composable
private fun EcgHeartMenuIcon(modifier: Modifier = Modifier) {
    val heartColor = MaterialTheme.colorScheme.primary
    val lineColor = MaterialTheme.colorScheme.onPrimary

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val heart = Path().apply {
            moveTo(w * 0.50f, h * 0.88f)
            cubicTo(w * 0.13f, h * 0.62f, w * 0.03f, h * 0.38f, w * 0.20f, h * 0.22f)
            cubicTo(w * 0.34f, h * 0.09f, w * 0.47f, h * 0.18f, w * 0.50f, h * 0.33f)
            cubicTo(w * 0.53f, h * 0.18f, w * 0.66f, h * 0.09f, w * 0.80f, h * 0.22f)
            cubicTo(w * 0.97f, h * 0.38f, w * 0.87f, h * 0.62f, w * 0.50f, h * 0.88f)
            close()
        }
        drawPath(path = heart, color = heartColor)

        val strokeWidth = (h * 0.065f).coerceAtLeast(2.2f)
        listOf(0.43f, 0.55f, 0.67f).forEach { yFactor ->
            drawLine(
                color = lineColor,
                start = Offset(w * 0.34f, h * yFactor),
                end = Offset(w * 0.66f, h * yFactor),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}


@Composable
private fun EcgToolMenu(
    selected: EcgTool,
    onSelected: (EcgTool) -> Unit,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (compact) 8.dp else 10.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 8.dp)
        ) {
            Text(
                "Menú ECG",
                style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 8.dp)
            ) {
                items(EcgTool.entries.size) { index ->
                    val tool = EcgTool.entries[index]
                    EcgToolListItem(
                        tool = tool,
                        selected = selected == tool,
                        compact = compact,
                        onClick = { onSelected(tool) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EcgToolListItem(
    tool: EcgTool,
    selected: Boolean,
    compact: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        shape = RoundedCornerShape(if (compact) 14.dp else 16.dp),
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(
            width = if (selected) 1.6.dp else 0.8.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        tonalElevation = if (selected) 4.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(if (compact) 9.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    modifier = Modifier.padding(7.dp).size(if (compact) 19.dp else 22.dp)
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(tool.shortLabel, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    tool.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.74f),
                    maxLines = if (compact) 1 else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun EcgToolGrid(
    selected: EcgTool,
    onSelected: (EcgTool) -> Unit,
    compact: Boolean,
    availableWidth: Dp
) {
    OutlinedCard {
        Column(
            modifier = Modifier.padding(if (compact) 9.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 10.dp)
        ) {
            Text(
                "Menú ECG",
                style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            EcgToolGridRows(
                selected = selected,
                onSelected = onSelected,
                compact = compact,
                availableWidth = availableWidth
            )
        }
    }
}

@Composable
private fun EcgToolGridRows(
    selected: EcgTool,
    onSelected: (EcgTool) -> Unit,
    compact: Boolean,
    availableWidth: Dp
) {
    val columnCount = when {
        availableWidth >= 900.dp -> 3
        availableWidth >= 620.dp -> 3
        else -> 2
    }

    Column(verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 10.dp)) {
        EcgTool.entries.chunked(columnCount).forEach { rowTools ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 10.dp)
            ) {
                rowTools.forEach { tool ->
                    EcgToolGridTile(
                        tool = tool,
                        selected = selected == tool,
                        compact = compact,
                        onClick = { onSelected(tool) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(columnCount - rowTools.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun EcgToolGridTile(
    tool: EcgTool,
    selected: Boolean,
    compact: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        shape = RoundedCornerShape(if (compact) 16.dp else 19.dp),
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(
            if (selected) 1.7.dp else 0.8.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        tonalElevation = if (selected) 5.dp else 1.dp,
        modifier = modifier
            .height(if (compact) 102.dp else 128.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (compact) 8.dp else 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    modifier = Modifier.padding(if (compact) 6.dp else 8.dp).size(if (compact) 20.dp else 25.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    tool.shortLabel,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    tool.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.74f),
                    textAlign = TextAlign.Center,
                    maxLines = if (compact) 1 else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(if (compact) 15.dp else 18.dp))
        }
    }
}

@Composable
private fun EcgHeaderCard(selected: EcgTool, compact: Boolean) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (compact) 12.dp else 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = selected.icon,
                    contentDescription = null,
                    modifier = Modifier.padding(if (compact) 8.dp else 10.dp).size(if (compact) 24.dp else 30.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Electrocardiograma",
                    style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "${selected.label}: ${selected.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.86f)
                )
                Text(
                    "Captura mediciones del trazo; esta versión no interpreta imágenes del ECG.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.74f)
                )
            }
        }
    }
}

@Composable
private fun EcgInfoButton(
    info: EcgInfoContent,
    modifier: Modifier = Modifier
) {
    var showDialog by rememberSaveable(info.title) { mutableStateOf(false) }

    OutlinedButton(
        onClick = { showDialog = true },
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.Info, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Info y referencias", fontWeight = FontWeight.Bold)
    }

    if (showDialog) {
        EcgReferencesDialog(info = info, onDismiss = { showDialog = false })
    }
}

@Composable
private fun EcgReferencesDialog(
    info: EcgInfoContent,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val compactHeight = maxHeight < 520.dp
            val landscape = maxWidth > maxHeight
            Surface(
                shape = RoundedCornerShape(if (compactHeight) 18.dp else 24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = if (landscape) 1050.dp else 720.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(if (compactHeight) 0.98f else 0.92f)
                    .padding(if (compactHeight) 5.dp else 16.dp)
            ) {
                Column(Modifier.fillMaxSize()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(if (compactHeight) 9.dp else 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.padding(if (compactHeight) 6.dp else 9.dp)
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Text(
                                info.title,
                                style = if (compactHeight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "Revisión: ${info.reviewedOn}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(if (compactHeight) 10.dp else 16.dp),
                        verticalArrangement = Arrangement.spacedBy(if (compactHeight) 9.dp else 14.dp)
                    ) {
                        item { EcgInfoSection("Qué hace", info.purpose) }
                        item { EcgInfoSection("Método utilizado", info.method) }
                        item {
                            Text(
                                "Referencias usadas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                        }
                        items(info.references.size) { index ->
                            val reference = info.references[index]
                            OutlinedCard(
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                )
                            ) {
                                Column(
                                    Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        "${index + 1}. ${reference.source}",
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(reference.citation, style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        "Uso en la app: ${reference.useInApp}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        item {
                            Text(
                                "Limitaciones",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                        }
                        items(info.limitations.size) { index ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("•", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Black)
                                Text(
                                    info.limitations[index],
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Herramienta de apoyo. No sustituye guías vigentes, juicio clínico, interpretación especializada ni valoración individual.",
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    HorizontalDivider()
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(if (compactHeight) 8.dp else 14.dp)
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

@Composable
private fun EcgInfoSection(title: String, text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EcgCalculatorContent(tool: EcgTool) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (tool) {
            EcgTool.ANALYZER -> EcgAnalyzerContent()
            EcgTool.RATE -> EcgRateContent()
            EcgTool.QTC -> QtcCalculatorContent()
            EcgTool.AXIS -> AxisCalculatorContent()
            EcgTool.LVH -> LvhCalculatorContent()
            EcgTool.ST -> StElevationContent()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EcgAnalyzerContent() {
    val clipboard = LocalClipboardManager.current
    var ageText by rememberSaveable { mutableStateOf("") }
    var sexName by rememberSaveable { mutableStateOf(EcgSex.MALE.name) }
    val sex = EcgSex.valueOf(sexName)
    var heartRateText by rememberSaveable { mutableStateOf("") }
    var prText by rememberSaveable { mutableStateOf("") }
    var qrsText by rememberSaveable { mutableStateOf("") }
    var qtText by rememberSaveable { mutableStateOf("") }
    var axisText by rememberSaveable { mutableStateOf("") }
    var sinusRhythm by rememberSaveable { mutableStateOf(true) }
    var regularRhythm by rememberSaveable { mutableStateOf(true) }
    var includeSt by rememberSaveable { mutableStateOf(false) }
    var includeLvh by rememberSaveable { mutableStateOf(false) }
    var stElevationText by rememberSaveable { mutableStateOf("") }
    var stLeadGroupName by rememberSaveable { mutableStateOf(EcgLeadGroup.OTHER_CONTIGUOUS.name) }
    var stContiguous by rememberSaveable { mutableStateOf(true) }
    var sV1Text by rememberSaveable { mutableStateOf("") }
    var rV5Text by rememberSaveable { mutableStateOf("") }
    var rV6Text by rememberSaveable { mutableStateOf("") }
    var rAvlText by rememberSaveable { mutableStateOf("") }
    var sV3Text by rememberSaveable { mutableStateOf("") }

    val age = ageText.toIntOrNull()
    val heartRate = heartRateText.toDecimalOrNull()
    val pr = prText.toDecimalOrNull()
    val qrs = qrsText.toDecimalOrNull()
    val qt = qtText.toDecimalOrNull()
    val axis = axisText.toDecimalOrNull()

    val stResult = if (includeSt && age != null) {
        EcgCalculator.stElevationCriteria(
            ageYears = age,
            sex = sex,
            leadGroup = EcgLeadGroup.valueOf(stLeadGroupName),
            elevationMm = stElevationText.toDecimalOrNull() ?: 0.0,
            contiguousLeads = stContiguous
        ).getOrNull()
    } else {
        null
    }

    val lvhResult = if (includeLvh) {
        EcgCalculator.lvh(
            sex = sex,
            sV1Mm = sV1Text.toDecimalOrNull(),
            rV5Mm = rV5Text.toDecimalOrNull(),
            rV6Mm = rV6Text.toDecimalOrNull(),
            rAvlMm = rAvlText.toDecimalOrNull(),
            sV3Mm = sV3Text.toDecimalOrNull(),
            qrsDurationMs = qrs
        )
    } else {
        null
    }

    val summary = EcgCalculator.buildSummary(
        EcgSummaryInput(
            ageYears = age,
            sex = sex,
            heartRateBpm = heartRate,
            prMs = pr,
            qrsMs = qrs,
            qtMs = qt,
            axisDegrees = axis,
            rhythmSinus = sinusRhythm,
            rhythmRegular = regularRhythm,
            stElevationResult = stResult,
            lvhResult = lvhResult
        )
    )

    EcgCard(
        title = "Datos básicos",
        note = "Captura los datos medidos en el ECG. La app redacta una interpretación preliminar, no una sentencia divina tallada en piedra."
    ) {
        ResponsiveFields {
            NumericField("Edad", ageText, { ageText = it.onlyDigits() }, suffix = "años")
            EnumDropdown("Sexo", sexName, { sexName = it }, EcgSex.entries.associate { it.name to it.label })
            NumericField("Frecuencia cardiaca", heartRateText, { heartRateText = it.decimalInput() }, suffix = "lpm")
            NumericField("PR", prText, { prText = it.decimalInput() }, suffix = "ms")
            NumericField("QRS", qrsText, { qrsText = it.decimalInput() }, suffix = "ms")
            NumericField("QT", qtText, { qtText = it.decimalInput() }, suffix = "ms")
            NumericField("Eje eléctrico", axisText, { axisText = it.signedDecimalInput() }, suffix = "°")
        }
        CheckRow("Ritmo sinusal", sinusRhythm) { sinusRhythm = it }
        CheckRow("Ritmo regular", regularRhythm) { regularRhythm = it }
    }

    EcgCard(
        title = "Hallazgos opcionales",
        note = "Agrega criterios de ST o HVI cuando tengas los datos medidos. Vacío no significa normal: significa no capturado. Vaya revelación."
    ) {
        CheckRow("Evaluar elevación del ST", includeSt) { includeSt = it }
        if (includeSt) {
            ResponsiveFields {
                EnumDropdown(
                    "Grupo de derivaciones",
                    stLeadGroupName,
                    { stLeadGroupName = it },
                    EcgLeadGroup.entries.associate { it.name to it.label }
                )
                NumericField("Elevación máxima", stElevationText, { stElevationText = it.decimalInput() }, suffix = "mm")
            }
            CheckRow("Presente en ≥2 derivaciones contiguas", stContiguous) { stContiguous = it }
            stResult?.let { StResultCard(it) }
        }
        HorizontalDivider()
        CheckRow("Evaluar HVI", includeLvh) { includeLvh = it }
        if (includeLvh) {
            ResponsiveFields {
                NumericField("S en V1", sV1Text, { sV1Text = it.decimalInput() }, suffix = "mm")
                NumericField("R en V5", rV5Text, { rV5Text = it.decimalInput() }, suffix = "mm")
                NumericField("R en V6", rV6Text, { rV6Text = it.decimalInput() }, suffix = "mm")
                NumericField("R en aVL", rAvlText, { rAvlText = it.decimalInput() }, suffix = "mm")
                NumericField("S en V3", sV3Text, { sV3Text = it.decimalInput() }, suffix = "mm")
            }
            lvhResult?.let { LvhResultCard(it) }
        }
    }

    EcgCard(
        title = "Resumen generado",
        note = "Puedes copiarlo para nota clínica, pase o comentario rápido. Lo sé, copiar y pegar también salvó más tiempo que muchas juntas."
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(summary, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium)
        }
        Button(
            onClick = { clipboard.setText(AnnotatedString(summary)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Copiar resumen")
        }
    }
}

@Composable
private fun EcgRateContent() {
    var paperSpeedName by rememberSaveable { mutableStateOf(EcgPaperSpeed.SPEED_25.name) }
    val paperSpeed = EcgPaperSpeed.valueOf(paperSpeedName)
    var rrMsText by rememberSaveable { mutableStateOf("") }
    var rrSecText by rememberSaveable { mutableStateOf("") }
    var largeSquaresText by rememberSaveable { mutableStateOf("") }
    var smallSquaresText by rememberSaveable { mutableStateOf("") }
    var qrsCountText by rememberSaveable { mutableStateOf("") }
    var heartRateText by rememberSaveable { mutableStateOf("") }

    val rrMsResult = rrMsText.toDecimalOrNull()?.let { EcgCalculator.rateFromRrMs(it).getOrNull() }
    val rrSecResult = rrSecText.toDecimalOrNull()?.let { EcgCalculator.rateFromRrSeconds(it).getOrNull() }
    val largeResult = largeSquaresText.toDecimalOrNull()?.let { EcgCalculator.rateFromLargeSquares(it, paperSpeed).getOrNull() }
    val smallResult = smallSquaresText.toDecimalOrNull()?.let { EcgCalculator.rateFromSmallSquares(it, paperSpeed).getOrNull() }
    val stripResult = qrsCountText.toIntOrNull()?.let { EcgCalculator.rateFromTenSecondStrip(it).getOrNull() }
    val rrFromHr = heartRateText.toDecimalOrNull()?.let { EcgCalculator.rrMsFromHeartRate(it).getOrNull() }

    EcgCard(
        title = "Frecuencia cardiaca e intervalo RR",
        note = "A 25 mm/s: 1 cuadro pequeño = 40 ms, 1 cuadro grande = 200 ms, 300/cuadros grandes y 1500/cuadros pequeños. La humanidad sobrevivió siglos para acabar dividiendo cuadritos, pero funciona."
    ) {
        EnumDropdown("Velocidad del papel", paperSpeedName, { paperSpeedName = it }, EcgPaperSpeed.entries.associate { it.name to it.label })
        ResultGrid(
            listOf(
                "1 cuadro pequeño" to "${EcgCalculator.smallSquareMs(paperSpeed).roundClean()} ms",
                "1 cuadro grande" to "${EcgCalculator.largeSquareMs(paperSpeed).roundClean()} ms"
            )
        )
    }

    EcgCard("Calcular FC", "Usa el dato que tengas disponible.") {
        ResponsiveFields {
            NumericField("RR", rrMsText, { rrMsText = it.decimalInput() }, suffix = "ms")
            NumericField("RR", rrSecText, { rrSecText = it.decimalInput() }, suffix = "s")
            NumericField("Cuadros grandes", largeSquaresText, { largeSquaresText = it.decimalInput() })
            NumericField("Cuadros pequeños", smallSquaresText, { smallSquaresText = it.decimalInput() })
            NumericField("QRS en tira de 10 s", qrsCountText, { qrsCountText = it.onlyDigits() })
        }
        listOf(
            "Por RR en ms" to rrMsResult,
            "Por RR en s" to rrSecResult,
            "Por cuadros grandes" to largeResult,
            "Por cuadros pequeños" to smallResult,
            "Por tira de 10 s" to stripResult
        ).forEach { (label, result) ->
            result?.let {
                ResultGrid(
                    listOf(
                        label to "${it.bpm.roundClean()} lpm",
                        "RR estimado" to "${it.rrMs.roundClean()} ms",
                        "Lectura" to it.interpretation
                    )
                )
            }
        }
    }

    EcgCard("Calcular RR desde FC", "Útil para QTc y para revisar coherencia de mediciones.") {
        NumericField("Frecuencia cardiaca", heartRateText, { heartRateText = it.decimalInput() }, suffix = "lpm")
        rrFromHr?.let {
            ResultGrid(listOf("RR" to "${it.roundClean()} ms", "RR" to "${(it / 1000.0).format(3)} s"))
        }
    }
}

@Composable
private fun QtcCalculatorContent() {
    var qtText by rememberSaveable { mutableStateOf("") }
    var heartRateText by rememberSaveable { mutableStateOf("") }
    var rrText by rememberSaveable { mutableStateOf("") }
    var useRr by rememberSaveable { mutableStateOf(false) }

    val qt = qtText.toDecimalOrNull()
    val result = if (qt != null) {
        if (useRr) {
            rrText.toDecimalOrNull()?.let { EcgCalculator.qtcFromRrMs(qt, it).getOrNull() }
        } else {
            heartRateText.toDecimalOrNull()?.let { EcgCalculator.qtcFromHeartRate(qt, it).getOrNull() }
        }
    } else {
        null
    }

    EcgCard(
        title = "QT corregido",
        note = "Calcula Bazett, Fridericia, Framingham y Hodges. En frecuencias muy altas o bajas conviene mirar más allá de Bazett, porque una fórmula también puede ponerse dramática."
    ) {
        NumericField("QT medido", qtText, { qtText = it.decimalInput() }, suffix = "ms")
        CheckRow("Usar RR en lugar de FC", useRr) { useRr = it }
        if (useRr) {
            NumericField("RR", rrText, { rrText = it.decimalInput() }, suffix = "ms")
        } else {
            NumericField("Frecuencia cardiaca", heartRateText, { heartRateText = it.decimalInput() }, suffix = "lpm")
        }
        result?.let { QtcResultCard(it) }
    }
}

@Composable
private fun AxisCalculatorContent() {
    var methodName by rememberSaveable { mutableStateOf(EcgAxisMethod.LEAD_I_AVF.name) }
    val method = EcgAxisMethod.valueOf(methodName)
    var firstText by rememberSaveable { mutableStateOf("") }
    var secondText by rememberSaveable { mutableStateOf("") }

    val firstLabel = "QRS neto en DI"
    val secondLabel = if (method == EcgAxisMethod.LEAD_I_AVF) "QRS neto en aVF" else "QRS neto en DIII"
    val result = firstText.toDecimalOrNull()?.let { first ->
        secondText.toDecimalOrNull()?.let { second -> EcgCalculator.calculateAxis(method, first, second) }
    }

    EcgCard(
        title = "Eje eléctrico",
        note = "Introduce amplitudes netas del QRS en mm: R positiva menos Q/S negativas. DI+aVF es práctico; DI+DIII replica la idea de calculadoras clásicas como My EKG."
    ) {
        EnumDropdown("Método", methodName, { methodName = it }, EcgAxisMethod.entries.associate { it.name to it.label })
        ResponsiveFields {
            NumericField(firstLabel, firstText, { firstText = it.signedDecimalInput() }, suffix = "mm")
            NumericField(secondLabel, secondText, { secondText = it.signedDecimalInput() }, suffix = "mm")
        }
        result?.let { AxisResultCard(it) }
    }
}

@Composable
private fun LvhCalculatorContent() {
    var sexName by rememberSaveable { mutableStateOf(EcgSex.MALE.name) }
    val sex = EcgSex.valueOf(sexName)
    var sV1Text by rememberSaveable { mutableStateOf("") }
    var rV5Text by rememberSaveable { mutableStateOf("") }
    var rV6Text by rememberSaveable { mutableStateOf("") }
    var rAvlText by rememberSaveable { mutableStateOf("") }
    var sV3Text by rememberSaveable { mutableStateOf("") }
    var qrsText by rememberSaveable { mutableStateOf("") }

    val result = EcgCalculator.lvh(
        sex = sex,
        sV1Mm = sV1Text.toDecimalOrNull(),
        rV5Mm = rV5Text.toDecimalOrNull(),
        rV6Mm = rV6Text.toDecimalOrNull(),
        rAvlMm = rAvlText.toDecimalOrNull(),
        sV3Mm = sV3Text.toDecimalOrNull(),
        qrsDurationMs = qrsText.toDecimalOrNull()
    )

    EcgCard(
        title = "Hipertrofia ventricular izquierda",
        note = "Incluye Sokolow-Lyon, Cornell voltaje y Cornell producto. Son criterios eléctricos, no ecocardiograma disfrazado de app."
    ) {
        EnumDropdown("Sexo", sexName, { sexName = it }, EcgSex.entries.associate { it.name to it.label })
        ResponsiveFields {
            NumericField("S en V1", sV1Text, { sV1Text = it.decimalInput() }, suffix = "mm")
            NumericField("R en V5", rV5Text, { rV5Text = it.decimalInput() }, suffix = "mm")
            NumericField("R en V6", rV6Text, { rV6Text = it.decimalInput() }, suffix = "mm")
            NumericField("R en aVL", rAvlText, { rAvlText = it.decimalInput() }, suffix = "mm")
            NumericField("S en V3", sV3Text, { sV3Text = it.decimalInput() }, suffix = "mm")
            NumericField("QRS", qrsText, { qrsText = it.decimalInput() }, suffix = "ms")
        }
        LvhResultCard(result)
    }
}

@Composable
private fun StElevationContent() {
    var ageText by rememberSaveable { mutableStateOf("") }
    var sexName by rememberSaveable { mutableStateOf(EcgSex.MALE.name) }
    val sex = EcgSex.valueOf(sexName)
    var leadGroupName by rememberSaveable { mutableStateOf(EcgLeadGroup.OTHER_CONTIGUOUS.name) }
    val leadGroup = EcgLeadGroup.valueOf(leadGroupName)
    var elevationText by rememberSaveable { mutableStateOf("") }
    var contiguous by rememberSaveable { mutableStateOf(true) }

    val result = ageText.toIntOrNull()?.let { age ->
        EcgCalculator.stElevationCriteria(
            ageYears = age,
            sex = sex,
            leadGroup = leadGroup,
            elevationMm = elevationText.toDecimalOrNull() ?: 0.0,
            contiguousLeads = contiguous
        ).getOrNull()
    }

    EcgCard(
        title = "Criterios de elevación del ST",
        note = "Evalúa el umbral en punto J para derivaciones contiguas. Si hay dolor torácico, inestabilidad o equivalente isquémico, esto no es juego de llenar casillas: requiere protocolo clínico."
    ) {
        ResponsiveFields {
            NumericField("Edad", ageText, { ageText = it.onlyDigits() }, suffix = "años")
            EnumDropdown("Sexo", sexName, { sexName = it }, EcgSex.entries.associate { it.name to it.label })
            EnumDropdown("Grupo", leadGroupName, { leadGroupName = it }, EcgLeadGroup.entries.associate { it.name to it.label })
            NumericField("Elevación máxima", elevationText, { elevationText = it.decimalInput() }, suffix = "mm")
        }
        CheckRow("Está en ≥2 derivaciones contiguas", contiguous) { contiguous = it }
        result?.let { StResultCard(it) }
    }
}

@Composable
private fun EcgCard(
    title: String,
    note: String,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text(note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
            SafetyNote()
        }
    }
}

@Composable
private fun SafetyNote() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
        Text(
            "Uso educativo y de apoyo clínico. Correlacionar con síntomas, exploración, ECG completo, troponinas/biomarcadores y guías locales cuando aplique.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResponsiveFields(content: @Composable () -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        content()
    }
}

@Composable
private fun NumericField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.width(210.dp),
    suffix: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        suffix = suffix?.let { { Text(it) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
    )
}

@Composable
private fun EnumDropdown(
    label: String,
    selectedKey: String,
    onSelected: (String) -> Unit,
    options: Map<String, String>,
    modifier: Modifier = Modifier.width(230.dp)
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(options[selectedKey].orEmpty(), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (key, text) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelected(key)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CheckRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            Text(text, modifier = Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResultGrid(rows: List<Pair<String, String>>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { (label, value) ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.52f),
                modifier = Modifier.width(210.dp).heightIn(min = 72.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
private fun QtcResultCard(result: QtcResult) {
    ResultGrid(
        listOf(
            "Bazett" to "${result.bazettMs.roundClean()} ms",
            "Fridericia" to "${result.fridericiaMs.roundClean()} ms",
            "Framingham" to "${result.framinghamMs.roundClean()} ms",
            "Hodges" to "${result.hodgesMs.roundClean()} ms",
            "RR usado" to "${result.rrSeconds.format(3)} s"
        )
    )
    Text(result.interpretation, fontWeight = FontWeight.Bold)
    result.warning?.let { WarningText(it) }
}

@Composable
private fun AxisResultCard(result: AxisResult) {
    ResultGrid(
        listOf(
            "Eje" to "${result.degrees.roundClean()}°",
            "Clasificación" to result.category
        )
    )
    Text(result.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun LvhResultCard(result: LvhResult) {
    ResultGrid(
        listOfNotNull(
            result.sokolowLyonMm?.let { "Sokolow-Lyon" to "${it.format(1)} mm · ${if (result.sokolowPositive == true) "positivo" else "negativo"}" },
            result.cornellVoltageMm?.let { "Cornell voltaje" to "${it.format(1)} mm · ${if (result.cornellVoltagePositive == true) "positivo" else "negativo"}" },
            result.cornellProduct?.let { "Cornell producto" to "${it.roundClean()} mm·ms · ${if (result.cornellProductPositive == true) "positivo" else "negativo"}" }
        ).ifEmpty { listOf("Resultado" to "Pendiente de datos") }
    )
    Text(result.interpretation, fontWeight = FontWeight.Bold)
}

@Composable
private fun StResultCard(result: StElevationResult) {
    ResultGrid(
        listOf(
            "Umbral" to "${result.thresholdMm.format(1)} mm",
            "Resultado" to if (result.meetsCriteria) "Cumple criterio" else "No cumple criterio"
        )
    )
    Text(result.interpretation, fontWeight = FontWeight.Bold)
}

@Composable
private fun WarningText(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Default.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
        Text(text, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
    }
}

private fun EcgTool.info(): EcgInfoContent = when (this) {
    EcgTool.ANALYZER -> EcgInfoContent(
        title = "Analizador ECG · información y referencias",
        purpose = "Genera una interpretación preliminar a partir de datos medidos manualmente del ECG: frecuencia, ritmo, intervalos, eje, ST e HVI.",
        method = "No analiza imágenes. Usa reglas determinísticas del motor ECG: clasificación de FC, intervalos PR/QRS, QTc si se captura QT y FC, eje frontal, criterios de ST y criterios eléctricos de HVI cuando se capturan.",
        references = listOf(
            EcgReferenceItem(
                source = "AHA/ACCF/HRS",
                citation = "Recommendations for the Standardization and Interpretation of the Electrocardiogram. Part IV: ST segment, T and U waves, and QT interval. J Am Coll Cardiol. 2009;53:982–991.",
                useInApp = "Marco de medición e interpretación de ST, T/U y QT."
            ),
            EcgReferenceItem(
                source = "AHA/ACCF/HRS",
                citation = "Recommendations for the Standardization and Interpretation of the Electrocardiogram. Part V: cardiac chamber hypertrophy. J Am Coll Cardiol. 2009;53:992–1002.",
                useInApp = "Contexto para criterios eléctricos de hipertrofia."
            ),
            EcgReferenceItem(
                source = "My EKG",
                citation = "Calculadoras del EKG: frecuencia cardiaca, eje, QT corregido, RR e hipertrofia ventricular izquierda.",
                useInApp = "Referencia funcional para organizar el menú de calculadoras."
            )
        ),
        limitations = listOf(
            "El resumen depende por completo de mediciones introducidas por el usuario.",
            "No sustituye lectura médica del ECG de 12 derivaciones ni evaluación de síntomas.",
            "No detecta arritmias complejas, patrones equivalentes de IAMCEST ni cambios dinámicos si no se capturan."
        )
    )

    EcgTool.RATE -> EcgInfoContent(
        title = "Frecuencia y RR · referencias",
        purpose = "Calcula frecuencia cardiaca o RR usando mediciones del papel ECG.",
        method = "A 25 mm/s: 1 cuadro pequeño = 40 ms y 1 cuadro grande = 200 ms. FC = 300/cuadros grandes, 1500/cuadros pequeños o QRS×6 en tira de 10 segundos. A 50 mm/s se recalculan los milisegundos por cuadro.",
        references = listOf(
            EcgReferenceItem(
                source = "Life in the Fast Lane",
                citation = "ECG Rate Interpretation. Papel estándar de 25 mm/s, reglas 300/1500 y tira de 10 segundos.",
                useInApp = "Conversión cuadros-tiempo y cálculo de frecuencia."
            ),
            EcgReferenceItem(
                source = "My EKG",
                citation = "Calculadoras de frecuencia cardiaca e intervalo RR del EKG.",
                useInApp = "Referencia funcional para entradas de RR, cuadros y frecuencia."
            )
        ),
        limitations = listOf(
            "Las reglas por cuadros grandes/pequeños son más fiables en ritmo regular.",
            "En ritmo irregular conviene usar una tira más larga y promediar varios ciclos.",
            "Debe confirmarse la velocidad real del papel: 25 mm/s o 50 mm/s."
        )
    )

    EcgTool.QTC -> EcgInfoContent(
        title = "QT corregido · referencias",
        purpose = "Corrige QT por frecuencia cardiaca con varias fórmulas para comparación clínica.",
        method = "Calcula RR desde FC o lo toma en milisegundos. Fórmulas: Bazett = QT/√RR, Fridericia = QT/RR^(1/3), Framingham = QT + 0.154(1−RR) y Hodges = QT + 1.75(FC−60). RR se usa en segundos.",
        references = listOf(
            EcgReferenceItem(
                source = "AHA/ACCF/HRS",
                citation = "Part IV: ST segment, T and U waves, and QT interval. J Am Coll Cardiol. 2009;53:982–991.",
                useInApp = "Marco clínico de medición e interpretación del QT."
            ),
            EcgReferenceItem(
                source = "Life in the Fast Lane",
                citation = "QT Interval. Fórmulas Bazett, Fridericia, Framingham y Hodges; advertencia sobre Bazett fuera de 60–100 lpm.",
                useInApp = "Fórmulas implementadas y nota de cautela sobre Bazett."
            ),
            EcgReferenceItem(
                source = "European Society of Cardiology",
                citation = "How to measure the QT interval. Cardiogenomics Insights, 2024.",
                useInApp = "Cotejo de medición de QT y corrección por frecuencia."
            )
        ),
        limitations = listOf(
            "No debe incluir onda U dentro del QT medido.",
            "Bazett puede sobrecorregir con FC alta y subcorregir con FC baja.",
            "QTc prolongado requiere valorar fármacos, electrolitos, QRS ancho, contexto y riesgo clínico."
        )
    )

    EcgTool.AXIS -> EcgInfoContent(
        title = "Eje eléctrico · referencias",
        purpose = "Calcula y clasifica el eje frontal del QRS.",
        method = "Permite DI+aVF como método vectorial práctico o DI+DIII usando relación de Einthoven. Clasifica normal, desviación izquierda, derecha o eje extremo.",
        references = listOf(
            EcgReferenceItem(
                source = "Life in the Fast Lane",
                citation = "ECG Axis Interpretation. Método de cuadrantes con DI y aVF; rangos de eje normal y desviaciones.",
                useInApp = "Clasificación del eje y método práctico."
            ),
            EcgReferenceItem(
                source = "My EKG",
                citation = "Heart Axis Calculator. Uso de DI y DIII para aproximar el eje cardiaco.",
                useInApp = "Referencia funcional para el método DI+DIII."
            ),
            EcgReferenceItem(
                source = "StatPearls / NCBI Bookshelf",
                citation = "Electrical Right and Left Axis Deviation, actualización 2024.",
                useInApp = "Cotejo de rangos normal, desviación izquierda, derecha y eje extremo."
            )
        ),
        limitations = listOf(
            "Debe capturarse la deflexión neta del QRS, no solo la onda R.",
            "La clasificación por eje no diagnostica por sí sola bloqueo fascicular, hipertrofia o sobrecarga.",
            "Errores de colocación de electrodos pueden alterar el resultado."
        )
    )

    EcgTool.LVH -> EcgInfoContent(
        title = "Hipertrofia ventricular izquierda · referencias",
        purpose = "Evalúa criterios eléctricos de HVI por voltaje y producto voltaje-duración.",
        method = "Sokolow-Lyon: S en V1 + R mayor de V5/V6 ≥35 mm. Cornell voltaje: R en aVL + S en V3, con umbral >28 mm en hombres y >20 mm en mujeres. Cornell producto: Cornell ajustado por sexo × QRS, positivo si >2440 mm·ms.",
        references = listOf(
            EcgReferenceItem(
                source = "AHA/ACCF/HRS",
                citation = "Part V: ECG changes associated with cardiac chamber hypertrophy. J Am Coll Cardiol. 2009;53:992–1002.",
                useInApp = "Marco de criterios electrocardiográficos de hipertrofia."
            ),
            EcgReferenceItem(
                source = "MSD Manual Professional",
                citation = "Criteria for ECG Diagnosis of Left Ventricular Hypertrophy.",
                useInApp = "Cotejo de Sokolow-Lyon, Cornell voltaje y Cornell producto."
            ),
            EcgReferenceItem(
                source = "My EKG",
                citation = "Calculadoras y criterios de hipertrofia ventricular izquierda.",
                useInApp = "Referencia funcional del apartado de HVI."
            )
        ),
        limitations = listOf(
            "Los criterios eléctricos tienen sensibilidad limitada y no reemplazan ecocardiograma o imagen.",
            "Bloqueos de rama, fasciculares, obesidad, edad y técnica pueden modificar voltajes.",
            "Un criterio positivo debe correlacionarse con clínica, presión arterial e imagen cuando aplique."
        )
    )

    EcgTool.ST -> EcgInfoContent(
        title = "Elevación del ST · referencias",
        purpose = "Evalúa si la elevación del ST alcanza umbrales electrocardiográficos por derivaciones contiguas.",
        method = "Usa elevación del ST en punto J. Requiere ≥2 derivaciones contiguas. En V2–V3: hombres <40 años ≥2.5 mm, hombres ≥40 años ≥2.0 mm, mujeres ≥1.5 mm. En otras derivaciones contiguas: ≥1.0 mm.",
        references = listOf(
            EcgReferenceItem(
                source = "Fourth Universal Definition of Myocardial Infarction",
                citation = "Thygesen K, Alpert JS, Jaffe AS, et al. Circulation. 2018;138:e618–e651.",
                useInApp = "Umbrales de elevación del ST por derivación, sexo y edad."
            ),
            EcgReferenceItem(
                source = "AHA/ACCF/HRS",
                citation = "Part IV: ST segment, T and U waves, and QT interval. J Am Coll Cardiol. 2009;53:982–991.",
                useInApp = "Contexto técnico para medición del ST."
            ),
            EcgReferenceItem(
                source = "Life in the Fast Lane",
                citation = "ST Segment ECG Library Basics.",
                useInApp = "Referencia educativa complementaria sobre punto J y segmento ST."
            )
        ),
        limitations = listOf(
            "No identifica automáticamente equivalentes de IAMCEST ni patrones sutiles.",
            "No sustituye protocolo de dolor torácico, ECG seriados, biomarcadores ni valoración urgente.",
            "El contexto clínico pesa más que una casilla aislada. Trágico, pero cierto."
        )
    )
}

private fun String.toDecimalOrNull(): Double? = replace(',', '.').toDoubleOrNull()

private fun String.decimalInput(): String =
    filter { it.isDigit() || it == '.' || it == ',' }
        .replace(',', '.')
        .let { text ->
            val firstDot = text.indexOf('.')
            if (firstDot < 0) text else text.take(firstDot + 1) + text.drop(firstDot + 1).replace(".", "")
        }

private fun String.signedDecimalInput(): String {
    val cleaned = filterIndexed { index, c -> c.isDigit() || c == '.' || c == ',' || (c == '-' && index == 0) }
        .replace(',', '.')
    val sign = if (cleaned.startsWith('-')) "-" else ""
    val body = cleaned.removePrefix("-")
    val firstDot = body.indexOf('.')
    val normalized = if (firstDot < 0) body else body.take(firstDot + 1) + body.drop(firstDot + 1).replace(".", "")
    return sign + normalized
}

private fun String.onlyDigits(): String = filter { it.isDigit() }

private fun Double.roundClean(): String = roundToInt().toString()

private fun Double.format(decimals: Int): String = "% .${decimals}f"
    .format(this)
    .trim()
