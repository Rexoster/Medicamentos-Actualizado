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
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateZoom
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
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
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt

private const val ECG_PAPER_SPEED_MM_PER_SECOND = 25.0
private const val ECG_SMALL_SQUARE_DP = 5f
private const val ECG_SINGLE_LEAD_VERTICAL_MM = 56f
private const val ECG_TWELVE_LEAD_VERTICAL_MM = 42f
private const val ECG_TWELVE_LEAD_SEGMENT_MS = 2500.0

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
    ),
    PATHOLOGIES(
        label = "Patologías ECG",
        shortLabel = "Patologías",
        description = "Carga ejemplos didácticos y rellena las calculadoras para comparar trazos.",
        icon = Icons.Default.MonitorHeart
    ),
    PREVIEW(
        label = "Vista previa ECG",
        shortLabel = "Vista ECG",
        description = "Dibuja una tira de ritmo configurada con los datos capturados.",
        icon = Icons.Default.MonitorHeart
    )
}

private enum class EcgPathologyPattern(
    val displayName: String,
    val teachingNote: String
) {
    NORMAL_SINUS("Ritmo sinusal normal", "P antes de cada QRS, RR regular, PR/QRS/QT dentro de rangos habituales."),
    PVC("Extrasístole ventricular", "Latido prematuro de QRS ancho, sin P previa clara y con pausa compensadora en la tira."),
    ATRIAL_FIBRILLATION("Fibrilación auricular", "Ritmo irregularmente irregular, sin ondas P organizadas y QRS habitualmente estrecho."),
    FIRST_DEGREE_AV_BLOCK("BAV de primer grado", "PR prolongado con conducción 1:1 entre P y QRS."),
    LEFT_VENTRICULAR_HYPERTROPHY("Crecimiento ventricular izquierdo", "Voltajes aumentados y posible repolarización secundaria; se cargan criterios de Sokolow-Lyon/Cornell."),
    RIGHT_VENTRICULAR_HYPERTROPHY("Crecimiento ventricular derecho", "Eje derecho y predominio de R en V1 con transición precordial alterada de forma didáctica."),
    RIGHT_BUNDLE_BRANCH_BLOCK("Bloqueo de rama derecha", "QRS ancho, patrón rSR' en V1 y S terminal ancha en DI/V6."),
    LEFT_BUNDLE_BRANCH_BLOCK("Bloqueo de rama izquierda", "QRS ancho, complejo negativo en V1 y R ancha/notchada en derivaciones laterales."),
    ANTERIOR_STEMI("IAM con elevación anterior", "Elevación del ST predominante en V2-V4/V5, orientativa de territorio anterior/DA."),
    INFERIOR_STEMI("IAM con elevación inferior", "Elevación del ST en DII, DIII y aVF con posible descenso recíproco en DI/aVL."),
    HYPERKALEMIA("Hiperkalemia", "Ondas T altas y picudas, QT corto relativo y ensanchamiento progresivo del QRS en el ejemplo."),
    LONG_QT("QT prolongado", "QT/QTc prolongado con repolarización extendida; útil para comparar fórmulas QTc.")
}

private data class EcgPathologyExample(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val pattern: EcgPathologyPattern,
    val focusLead: EcgLead = EcgLead.DII,
    val references: String
)

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

private data class EcgSharedInputState(
    val ageText: MutableState<String>,
    val sexName: MutableState<String>,
    val heartRateText: MutableState<String>,
    val prText: MutableState<String>,
    val qrsText: MutableState<String>,
    val qtText: MutableState<String>,
    val axisText: MutableState<String>,
    val sinusRhythm: MutableState<Boolean>,
    val regularRhythm: MutableState<Boolean>,
    val includeSt: MutableState<Boolean>,
    val stElevationText: MutableState<String>,
    val stLeadGroupName: MutableState<String>,
    val stContiguous: MutableState<Boolean>,
    val includeLvh: MutableState<Boolean>,
    val sV1Text: MutableState<String>,
    val rV5Text: MutableState<String>,
    val rV6Text: MutableState<String>,
    val rAvlText: MutableState<String>,
    val sV3Text: MutableState<String>,
    val ratePaperSpeedName: MutableState<String>,
    val rateRrMsText: MutableState<String>,
    val rateRrSecText: MutableState<String>,
    val rateLargeSquaresText: MutableState<String>,
    val rateSmallSquaresText: MutableState<String>,
    val rateQrsCountText: MutableState<String>,
    val qtcRrText: MutableState<String>,
    val qtcUseRr: MutableState<Boolean>,
    val axisMethodName: MutableState<String>,
    val axisFirstText: MutableState<String>,
    val axisSecondText: MutableState<String>,
    val pathologyPatternName: MutableState<String>
)

private data class EcgPreviewModel(
    val heartRateBpm: Double,
    val rrMs: Double,
    val prMs: Double,
    val qrsMs: Double,
    val qtMs: Double,
    val qtcResult: QtcResult?,
    val qtcPreferredMs: Double?,
    val qtcSourceNote: String,
    val stMm: Double,
    val axisDegrees: Double?,
    val rhythmSinus: Boolean,
    val rhythmRegular: Boolean,
    val lvhPositive: Boolean,
    val pathologyPattern: EcgPathologyPattern,
    val sourceNote: String
)

private enum class EcgPreviewMode(val label: String, val description: String) {
    SINGLE_LEAD("Una derivación", "Ver una derivación ampliada."),
    TWELVE_LEADS("ECG completo", "Ver 12 derivaciones simuladas."),
    CARDIAC_AREA("Áreas cardíacas", "Comparar derivaciones por región anatómica."),
    CORONARY_TERRITORY("Irrigación", "Comparar por territorio coronario probable.")
}

private enum class EcgLead(
    val label: String,
    val region: String,
    val limbAxisDegrees: Double?,
    val precordialProjection: Double
) {
    DI("DI", "Lateral alta", 0.0, 0.85),
    DII("DII", "Inferior", 60.0, 1.00),
    DIII("DIII", "Inferior", 120.0, 0.70),
    AVR("aVR", "Alta derecha", -150.0, -0.90),
    AVL("aVL", "Lateral alta", -30.0, 0.65),
    AVF("aVF", "Inferior", 90.0, 0.80),
    V1("V1", "Septal / VD", null, -0.65),
    V2("V2", "Septal", null, -0.35),
    V3("V3", "Anterior", null, 0.10),
    V4("V4", "Anterior", null, 0.65),
    V5("V5", "Lateral baja", null, 0.95),
    V6("V6", "Lateral baja", null, 0.82)
}

private data class EcgLeadCluster(
    val id: String,
    val title: String,
    val subtitle: String,
    val leads: Set<EcgLead>,
    val tint: Color
)

private val ecgAreaGroups = listOf(
    EcgLeadCluster("inferior", "Inferior", "DII, DIII y aVF", setOf(EcgLead.DII, EcgLead.DIII, EcgLead.AVF), Color(0xFF2E7D32)),
    EcgLeadCluster("septal", "Septal", "V1 y V2", setOf(EcgLead.V1, EcgLead.V2), Color(0xFF1565C0)),
    EcgLeadCluster("anterior", "Anterior", "V3 y V4", setOf(EcgLead.V3, EcgLead.V4), Color(0xFFC62828)),
    EcgLeadCluster("lateral_alta", "Lateral alta", "DI y aVL", setOf(EcgLead.DI, EcgLead.AVL), Color(0xFF6A1B9A)),
    EcgLeadCluster("lateral_baja", "Lateral baja", "V5 y V6", setOf(EcgLead.V5, EcgLead.V6), Color(0xFFEF6C00)),
    EcgLeadCluster("anteroseptal", "Anteroseptal", "V1 a V4", setOf(EcgLead.V1, EcgLead.V2, EcgLead.V3, EcgLead.V4), Color(0xFF00838F)),
    EcgLeadCluster("lateral", "Lateral", "DI, aVL, V5 y V6", setOf(EcgLead.DI, EcgLead.AVL, EcgLead.V5, EcgLead.V6), Color(0xFFAD1457)),
    EcgLeadCluster("posterior_indirecta", "Posterior indirecta", "V1 a V3 como espejo; V7-V9 no están en ECG estándar", setOf(EcgLead.V1, EcgLead.V2, EcgLead.V3), Color(0xFF455A64))
)

private val ecgCoronaryGroups = listOf(
    EcgLeadCluster("da", "DA / LAD", "Septal, anterior y parte anterolateral: V1-V4, DI/aVL según extensión", setOf(EcgLead.V1, EcgLead.V2, EcgLead.V3, EcgLead.V4, EcgLead.DI, EcgLead.AVL), Color(0xFFC62828)),
    EcgLeadCluster("cd", "CD / RCA", "Inferior y posible VD: DII, DIII, aVF; V1 puede orientar a VD", setOf(EcgLead.DII, EcgLead.DIII, EcgLead.AVF, EcgLead.V1), Color(0xFF2E7D32)),
    EcgLeadCluster("cx", "Cx / LCx", "Lateral y posterolateral: DI, aVL, V5-V6; a veces inferior por dominancia", setOf(EcgLead.DI, EcgLead.AVL, EcgLead.V5, EcgLead.V6, EcgLead.DII, EcgLead.DIII, EcgLead.AVF), Color(0xFF6A1B9A))
)

private val twelveLeadLayout = listOf(
    listOf(EcgLead.DI, EcgLead.AVR, EcgLead.V1, EcgLead.V4),
    listOf(EcgLead.DII, EcgLead.AVL, EcgLead.V2, EcgLead.V5),
    listOf(EcgLead.DIII, EcgLead.AVF, EcgLead.V3, EcgLead.V6)
)


private fun selectedClusterIds(raw: String, groups: List<EcgLeadCluster>): Set<String> {
    val validIds = groups.map { it.id }.toSet()
    val parsed = raw.split("|")
        .map { it.trim() }
        .filter { it.isNotBlank() && it in validIds }
        .toSet()
    return parsed.ifEmpty { setOf(groups.first().id) }
}

private fun toggleClusterSelection(raw: String, cluster: EcgLeadCluster, groups: List<EcgLeadCluster>): String {
    val current = selectedClusterIds(raw, groups).toMutableSet()
    if (cluster.id in current) {
        if (current.size > 1) current.remove(cluster.id)
    } else {
        current.add(cluster.id)
    }
    return groups.filter { it.id in current }.joinToString("|") { it.id }
}

private fun leadHighlightColors(clusters: List<EcgLeadCluster>): Map<EcgLead, List<Color>> {
    val result = mutableMapOf<EcgLead, MutableList<Color>>()
    clusters.forEach { cluster ->
        cluster.leads.forEach { lead ->
            result.getOrPut(lead) { mutableListOf() }.add(cluster.tint)
        }
    }
    return result.mapValues { it.value.toList() }
}

private fun clusterSummaryTitle(prefix: String, clusters: List<EcgLeadCluster>): String =
    if (clusters.size == 1) "$prefix ${clusters.first().title}" else "$prefix: ${clusters.joinToString { it.title }}"

private fun clusterSummarySubtitle(clusters: List<EcgLeadCluster>): String =
    clusters.joinToString(" · ") { it.subtitle }

@Composable
private fun rememberEcgSharedInputState(): EcgSharedInputState = EcgSharedInputState(
    ageText = rememberSaveable { mutableStateOf("") },
    sexName = rememberSaveable { mutableStateOf(EcgSex.MALE.name) },
    heartRateText = rememberSaveable { mutableStateOf("") },
    prText = rememberSaveable { mutableStateOf("") },
    qrsText = rememberSaveable { mutableStateOf("") },
    qtText = rememberSaveable { mutableStateOf("") },
    axisText = rememberSaveable { mutableStateOf("") },
    sinusRhythm = rememberSaveable { mutableStateOf(true) },
    regularRhythm = rememberSaveable { mutableStateOf(true) },
    includeSt = rememberSaveable { mutableStateOf(false) },
    stElevationText = rememberSaveable { mutableStateOf("") },
    stLeadGroupName = rememberSaveable { mutableStateOf(EcgLeadGroup.OTHER_CONTIGUOUS.name) },
    stContiguous = rememberSaveable { mutableStateOf(true) },
    includeLvh = rememberSaveable { mutableStateOf(false) },
    sV1Text = rememberSaveable { mutableStateOf("") },
    rV5Text = rememberSaveable { mutableStateOf("") },
    rV6Text = rememberSaveable { mutableStateOf("") },
    rAvlText = rememberSaveable { mutableStateOf("") },
    sV3Text = rememberSaveable { mutableStateOf("") },
    ratePaperSpeedName = rememberSaveable { mutableStateOf(EcgPaperSpeed.SPEED_25.name) },
    rateRrMsText = rememberSaveable { mutableStateOf("") },
    rateRrSecText = rememberSaveable { mutableStateOf("") },
    rateLargeSquaresText = rememberSaveable { mutableStateOf("") },
    rateSmallSquaresText = rememberSaveable { mutableStateOf("") },
    rateQrsCountText = rememberSaveable { mutableStateOf("") },
    qtcRrText = rememberSaveable { mutableStateOf("") },
    qtcUseRr = rememberSaveable { mutableStateOf(false) },
    axisMethodName = rememberSaveable { mutableStateOf(EcgAxisMethod.LEAD_I_AVF.name) },
    axisFirstText = rememberSaveable { mutableStateOf("") },
    axisSecondText = rememberSaveable { mutableStateOf("") },
    pathologyPatternName = rememberSaveable { mutableStateOf(EcgPathologyPattern.NORMAL_SINUS.name) }
)

@Composable
fun EcgScreen(modifier: Modifier = Modifier) {
    var selectedName by rememberSaveable { mutableStateOf(EcgTool.ANALYZER.name) }
    var ecgMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val sharedInput = rememberEcgSharedInputState()
    val selected = remember(selectedName) { EcgTool.valueOf(selectedName) }

    BoxWithConstraints(modifier = modifier) {
        val compactHeight = maxHeight < 520.dp
        val padding = if (compactHeight) 8.dp else 14.dp

        EcgToolBody(
            selected = selected,
            sharedInput = sharedInput,
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
    sharedInput: EcgSharedInputState,
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
        item { EcgCalculatorContent(selected, sharedInput) }
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
private fun EcgCalculatorContent(tool: EcgTool, sharedInput: EcgSharedInputState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (tool) {
            EcgTool.ANALYZER -> EcgAnalyzerContent(sharedInput)
            EcgTool.RATE -> EcgRateContent(sharedInput)
            EcgTool.QTC -> QtcCalculatorContent(sharedInput)
            EcgTool.AXIS -> AxisCalculatorContent(sharedInput)
            EcgTool.LVH -> LvhCalculatorContent(sharedInput)
            EcgTool.ST -> StElevationContent(sharedInput)
            EcgTool.PATHOLOGIES -> EcgPathologiesContent(sharedInput)
            EcgTool.PREVIEW -> EcgPreviewContent(sharedInput)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EcgAnalyzerContent(state: EcgSharedInputState) {
    val clipboard = LocalClipboardManager.current
    val sex = EcgSex.valueOf(state.sexName.value)

    val age = state.ageText.value.toIntOrNull()
    val heartRate = state.heartRateText.value.toDecimalOrNull()
    val pr = state.prText.value.toDecimalOrNull()
    val qrs = state.qrsText.value.toDecimalOrNull()
    val qt = state.qtText.value.toDecimalOrNull()
    val axis = state.axisText.value.toDecimalOrNull()

    val stResult = if (state.includeSt.value && age != null) {
        EcgCalculator.stElevationCriteria(
            ageYears = age,
            sex = sex,
            leadGroup = EcgLeadGroup.valueOf(state.stLeadGroupName.value),
            elevationMm = state.stElevationText.value.toDecimalOrNull() ?: 0.0,
            contiguousLeads = state.stContiguous.value
        ).getOrNull()
    } else {
        null
    }

    val lvhResult = if (state.includeLvh.value) {
        EcgCalculator.lvh(
            sex = sex,
            sV1Mm = state.sV1Text.value.toDecimalOrNull(),
            rV5Mm = state.rV5Text.value.toDecimalOrNull(),
            rV6Mm = state.rV6Text.value.toDecimalOrNull(),
            rAvlMm = state.rAvlText.value.toDecimalOrNull(),
            sV3Mm = state.sV3Text.value.toDecimalOrNull(),
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
            rhythmSinus = state.sinusRhythm.value,
            rhythmRegular = state.regularRhythm.value,
            stElevationResult = stResult,
            lvhResult = lvhResult
        )
    )

    EcgCard(
        title = "Datos básicos",
        note = "Captura los datos medidos en el ECG. La app redacta una interpretación preliminar, no una sentencia divina tallada en piedra."
    ) {
        ResponsiveFields {
            NumericField("Edad", state.ageText.value, { state.ageText.value = it.onlyDigits() }, suffix = "años")
            EnumDropdown("Sexo", state.sexName.value, { state.sexName.value = it }, EcgSex.entries.associate { it.name to it.label })
            NumericField("Frecuencia cardiaca", state.heartRateText.value, { state.heartRateText.value = it.decimalInput() }, suffix = "lpm")
            NumericField("PR", state.prText.value, { state.prText.value = it.decimalInput() }, suffix = "ms")
            NumericField("QRS", state.qrsText.value, { state.qrsText.value = it.decimalInput() }, suffix = "ms")
            NumericField("QT", state.qtText.value, { state.qtText.value = it.decimalInput() }, suffix = "ms")
            NumericField("Eje eléctrico", state.axisText.value, { state.axisText.value = it.signedDecimalInput() }, suffix = "°")
        }
        CheckRow("Ritmo sinusal", state.sinusRhythm.value) { state.sinusRhythm.value = it }
        CheckRow("Ritmo regular", state.regularRhythm.value) { state.regularRhythm.value = it }
    }

    EcgCard(
        title = "Hallazgos opcionales",
        note = "Agrega criterios de ST o HVI cuando tengas los datos medidos. Vacío no significa normal: significa no capturado. Vaya revelación."
    ) {
        CheckRow("Evaluar elevación del ST", state.includeSt.value) { state.includeSt.value = it }
        if (state.includeSt.value) {
            ResponsiveFields {
                EnumDropdown(
                    "Grupo de derivaciones",
                    state.stLeadGroupName.value,
                    { state.stLeadGroupName.value = it },
                    EcgLeadGroup.entries.associate { it.name to it.label }
                )
                NumericField("Elevación máxima", state.stElevationText.value, { state.stElevationText.value = it.decimalInput() }, suffix = "mm")
            }
            CheckRow("Presente en ≥2 derivaciones contiguas", state.stContiguous.value) { state.stContiguous.value = it }
            stResult?.let { StResultCard(it) }
        }
        HorizontalDivider()
        CheckRow("Evaluar HVI", state.includeLvh.value) { state.includeLvh.value = it }
        if (state.includeLvh.value) {
            ResponsiveFields {
                NumericField("S en V1", state.sV1Text.value, { state.sV1Text.value = it.decimalInput() }, suffix = "mm")
                NumericField("R en V5", state.rV5Text.value, { state.rV5Text.value = it.decimalInput() }, suffix = "mm")
                NumericField("R en V6", state.rV6Text.value, { state.rV6Text.value = it.decimalInput() }, suffix = "mm")
                NumericField("R en aVL", state.rAvlText.value, { state.rAvlText.value = it.decimalInput() }, suffix = "mm")
                NumericField("S en V3", state.sV3Text.value, { state.sV3Text.value = it.decimalInput() }, suffix = "mm")
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
private fun EcgRateContent(state: EcgSharedInputState) {
    val paperSpeed = EcgPaperSpeed.valueOf(state.ratePaperSpeedName.value)

    val rrMsResult = state.rateRrMsText.value.toDecimalOrNull()?.let { EcgCalculator.rateFromRrMs(it).getOrNull() }
    val rrSecResult = state.rateRrSecText.value.toDecimalOrNull()?.let { EcgCalculator.rateFromRrSeconds(it).getOrNull() }
    val largeResult = state.rateLargeSquaresText.value.toDecimalOrNull()?.let { EcgCalculator.rateFromLargeSquares(it, paperSpeed).getOrNull() }
    val smallResult = state.rateSmallSquaresText.value.toDecimalOrNull()?.let { EcgCalculator.rateFromSmallSquares(it, paperSpeed).getOrNull() }
    val stripResult = state.rateQrsCountText.value.toIntOrNull()?.let { EcgCalculator.rateFromTenSecondStrip(it).getOrNull() }
    val rrFromHr = state.heartRateText.value.toDecimalOrNull()?.let { EcgCalculator.rrMsFromHeartRate(it).getOrNull() }

    EcgCard(
        title = "Frecuencia cardiaca e intervalo RR",
        note = "A 25 mm/s: 1 cuadro pequeño = 40 ms, 1 cuadro grande = 200 ms, 300/cuadros grandes y 1500/cuadros pequeños. La humanidad sobrevivió siglos para acabar dividiendo cuadritos, pero funciona."
    ) {
        EnumDropdown("Velocidad del papel", state.ratePaperSpeedName.value, { state.ratePaperSpeedName.value = it }, EcgPaperSpeed.entries.associate { it.name to it.label })
        ResultGrid(
            listOf(
                "1 cuadro pequeño" to "${EcgCalculator.smallSquareMs(paperSpeed).roundClean()} ms",
                "1 cuadro grande" to "${EcgCalculator.largeSquareMs(paperSpeed).roundClean()} ms"
            )
        )
    }

    EcgCard("Calcular FC", "Usa el dato que tengas disponible. La vista previa toma el primer resultado válido si no capturaste FC directa.") {
        ResponsiveFields {
            NumericField("RR", state.rateRrMsText.value, { state.rateRrMsText.value = it.decimalInput() }, suffix = "ms")
            NumericField("RR", state.rateRrSecText.value, { state.rateRrSecText.value = it.decimalInput() }, suffix = "s")
            NumericField("Cuadros grandes", state.rateLargeSquaresText.value, { state.rateLargeSquaresText.value = it.decimalInput() })
            NumericField("Cuadros pequeños", state.rateSmallSquaresText.value, { state.rateSmallSquaresText.value = it.decimalInput() })
            NumericField("QRS en tira de 10 s", state.rateQrsCountText.value, { state.rateQrsCountText.value = it.onlyDigits() })
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

    EcgCard("Calcular RR desde FC", "Útil para QTc y para revisar coherencia de mediciones. También alimenta la vista previa ECG.") {
        NumericField("Frecuencia cardiaca", state.heartRateText.value, { state.heartRateText.value = it.decimalInput() }, suffix = "lpm")
        rrFromHr?.let {
            ResultGrid(listOf("RR" to "${it.roundClean()} ms", "RR" to "${(it / 1000.0).format(3)} s"))
        }
    }
}

@Composable
private fun QtcCalculatorContent(state: EcgSharedInputState) {
    val qt = state.qtText.value.toDecimalOrNull()
    val result = if (qt != null) {
        if (state.qtcUseRr.value) {
            state.qtcRrText.value.toDecimalOrNull()?.let { EcgCalculator.qtcFromRrMs(qt, it).getOrNull() }
        } else {
            state.heartRateText.value.toDecimalOrNull()?.let { EcgCalculator.qtcFromHeartRate(qt, it).getOrNull() }
        }
    } else {
        null
    }

    EcgCard(
        title = "QT corregido",
        note = "Calcula Bazett, Fridericia, Framingham y Hodges. La vista previa usa el QT capturado para alargar o acortar visualmente la repolarización. No, no convierte al teléfono en holter."
    ) {
        NumericField("QT medido", state.qtText.value, { state.qtText.value = it.decimalInput() }, suffix = "ms")
        CheckRow("Usar RR en lugar de FC", state.qtcUseRr.value) { state.qtcUseRr.value = it }
        if (state.qtcUseRr.value) {
            NumericField("RR", state.qtcRrText.value, { state.qtcRrText.value = it.decimalInput() }, suffix = "ms")
        } else {
            NumericField("Frecuencia cardiaca", state.heartRateText.value, { state.heartRateText.value = it.decimalInput() }, suffix = "lpm")
        }
        result?.let { QtcResultCard(it) }
    }
}

@Composable
private fun AxisCalculatorContent(state: EcgSharedInputState) {
    val method = EcgAxisMethod.valueOf(state.axisMethodName.value)

    val firstLabel = "QRS neto en DI"
    val secondLabel = if (method == EcgAxisMethod.LEAD_I_AVF) "QRS neto en aVF" else "QRS neto en DIII"
    val result = state.axisFirstText.value.toDecimalOrNull()?.let { first ->
        state.axisSecondText.value.toDecimalOrNull()?.let { second -> EcgCalculator.calculateAxis(method, first, second) }
    }

    EcgCard(
        title = "Eje eléctrico",
        note = "Introduce amplitudes netas del QRS en mm: R positiva menos Q/S negativas. Si calculas el eje aquí, también alimenta la vista previa. Milagro: los datos ahora sí hablan entre pantallas."
    ) {
        EnumDropdown("Método", state.axisMethodName.value, { state.axisMethodName.value = it }, EcgAxisMethod.entries.associate { it.name to it.label })
        ResponsiveFields {
            NumericField(firstLabel, state.axisFirstText.value, { state.axisFirstText.value = it.signedDecimalInput() }, suffix = "mm")
            NumericField(secondLabel, state.axisSecondText.value, { state.axisSecondText.value = it.signedDecimalInput() }, suffix = "mm")
        }
        result?.let { AxisResultCard(it) }
    }
}

@Composable
private fun LvhCalculatorContent(state: EcgSharedInputState) {
    val sex = EcgSex.valueOf(state.sexName.value)

    val result = EcgCalculator.lvh(
        sex = sex,
        sV1Mm = state.sV1Text.value.toDecimalOrNull(),
        rV5Mm = state.rV5Text.value.toDecimalOrNull(),
        rV6Mm = state.rV6Text.value.toDecimalOrNull(),
        rAvlMm = state.rAvlText.value.toDecimalOrNull(),
        sV3Mm = state.sV3Text.value.toDecimalOrNull(),
        qrsDurationMs = state.qrsText.value.toDecimalOrNull()
    )

    EcgCard(
        title = "Hipertrofia ventricular izquierda",
        note = "Incluye Sokolow-Lyon, Cornell voltaje y Cornell producto. Si sale positivo, la vista previa aumenta el voltaje del QRS para representar el hallazgo de forma didáctica."
    ) {
        EnumDropdown("Sexo", state.sexName.value, { state.sexName.value = it }, EcgSex.entries.associate { it.name to it.label })
        ResponsiveFields {
            NumericField("S en V1", state.sV1Text.value, { state.sV1Text.value = it.decimalInput() }, suffix = "mm")
            NumericField("R en V5", state.rV5Text.value, { state.rV5Text.value = it.decimalInput() }, suffix = "mm")
            NumericField("R en V6", state.rV6Text.value, { state.rV6Text.value = it.decimalInput() }, suffix = "mm")
            NumericField("R en aVL", state.rAvlText.value, { state.rAvlText.value = it.decimalInput() }, suffix = "mm")
            NumericField("S en V3", state.sV3Text.value, { state.sV3Text.value = it.decimalInput() }, suffix = "mm")
            NumericField("QRS", state.qrsText.value, { state.qrsText.value = it.decimalInput() }, suffix = "ms")
        }
        LvhResultCard(result)
    }
}

@Composable
private fun StElevationContent(state: EcgSharedInputState) {
    val sex = EcgSex.valueOf(state.sexName.value)
    val leadGroup = EcgLeadGroup.valueOf(state.stLeadGroupName.value)

    val result = state.ageText.value.toIntOrNull()?.let { age ->
        EcgCalculator.stElevationCriteria(
            ageYears = age,
            sex = sex,
            leadGroup = leadGroup,
            elevationMm = state.stElevationText.value.toDecimalOrNull() ?: 0.0,
            contiguousLeads = state.stContiguous.value
        ).getOrNull()
    }

    EcgCard(
        title = "Criterios de elevación del ST",
        note = "Evalúa el umbral en punto J para derivaciones contiguas. La vista previa eleva o deprime el segmento ST con el valor capturado, sin fingir que eso reemplaza un ECG real."
    ) {
        ResponsiveFields {
            NumericField("Edad", state.ageText.value, { state.ageText.value = it.onlyDigits() }, suffix = "años")
            EnumDropdown("Sexo", state.sexName.value, { state.sexName.value = it }, EcgSex.entries.associate { it.name to it.label })
            EnumDropdown("Grupo", state.stLeadGroupName.value, { state.stLeadGroupName.value = it }, EcgLeadGroup.entries.associate { it.name to it.label })
            NumericField("Elevación máxima", state.stElevationText.value, { state.stElevationText.value = it.signedDecimalInput() }, suffix = "mm")
        }
        CheckRow("Está en ≥2 derivaciones contiguas", state.stContiguous.value) { state.stContiguous.value = it }
        result?.let { StResultCard(it) }
    }
}

private val ecgPathologyExamples = listOf(
    EcgPathologyExample(
        id = "normal_sinus",
        title = "Ritmo sinusal normal",
        subtitle = "Base comparativa",
        description = "FC 75 lpm, PR 160 ms, QRS 90 ms y QT 390 ms. Útil como punto de partida para comparar cambios.",
        pattern = EcgPathologyPattern.NORMAL_SINUS,
        focusLead = EcgLead.DII,
        references = "AHA/ACCF/HRS Part IV; ECG basics."
    ),
    EcgPathologyExample(
        id = "pvc",
        title = "Extrasístole ventricular",
        subtitle = "QRS ancho prematuro + pausa",
        description = "Simula latidos ventriculares prematuros: QRS ancho, sin P previa clara y pausa compensadora. Se cargan QRS 160 ms y ritmo irregular.",
        pattern = EcgPathologyPattern.PVC,
        focusLead = EcgLead.DII,
        references = "LITFL ECG Library: premature ventricular complexes; NCBI/StatPearls arrhythmias."
    ),
    EcgPathologyExample(
        id = "afib",
        title = "Fibrilación auricular",
        subtitle = "RR irregular + sin P organizada",
        description = "Carga ritmo no sinusal e irregular, con FC 125 lpm y QRS estrecho para revisar la tira y el cálculo de frecuencia.",
        pattern = EcgPathologyPattern.ATRIAL_FIBRILLATION,
        focusLead = EcgLead.DII,
        references = "AHA/ACC/HRS AF guidance; ECG rhythm basics."
    ),
    EcgPathologyExample(
        id = "first_degree_avb",
        title = "BAV de primer grado",
        subtitle = "PR prolongado",
        description = "Conducción 1:1 con PR de 240 ms. Sirve para ver cómo se desplaza la onda P respecto al QRS.",
        pattern = EcgPathologyPattern.FIRST_DEGREE_AV_BLOCK,
        focusLead = EcgLead.DII,
        references = "AHA/ACCF/HRS Part III conduction disturbances."
    ),
    EcgPathologyExample(
        id = "lvh",
        title = "Crecimiento ventricular izquierdo",
        subtitle = "Voltajes altos + criterios HVI",
        description = "Carga S en V1 y R en V5/V6 altos para activar Sokolow-Lyon y Cornell. El trazo aumenta voltaje de forma didáctica.",
        pattern = EcgPathologyPattern.LEFT_VENTRICULAR_HYPERTROPHY,
        focusLead = EcgLead.V5,
        references = "AHA/ACCF/HRS Part V; Sokolow-Lyon/Cornell."
    ),
    EcgPathologyExample(
        id = "rvh",
        title = "Crecimiento ventricular derecho",
        subtitle = "Eje derecho + R dominante en V1",
        description = "Carga eje derecho y morfología orientativa con predominio anterior derecho, útil para comparar V1 y derivaciones inferiores.",
        pattern = EcgPathologyPattern.RIGHT_VENTRICULAR_HYPERTROPHY,
        focusLead = EcgLead.V1,
        references = "AHA/ACCF/HRS Part V chamber hypertrophy."
    ),
    EcgPathologyExample(
        id = "rbbb",
        title = "Bloqueo de rama derecha",
        subtitle = "rSR' en V1 + S lateral",
        description = "QRS 140 ms, patrón terminal derecho. El ejemplo se aprecia mejor en V1 y V6.",
        pattern = EcgPathologyPattern.RIGHT_BUNDLE_BRANCH_BLOCK,
        focusLead = EcgLead.V1,
        references = "AHA/ACCF/HRS Part III; bundle branch block criteria."
    ),
    EcgPathologyExample(
        id = "lbbb",
        title = "Bloqueo de rama izquierda",
        subtitle = "QRS ancho + R lateral amplia",
        description = "QRS 150 ms, V1 negativo y laterales con R ancha/notchada de manera didáctica.",
        pattern = EcgPathologyPattern.LEFT_BUNDLE_BRANCH_BLOCK,
        focusLead = EcgLead.V6,
        references = "AHA/ACCF/HRS Part III; LBBB criteria."
    ),
    EcgPathologyExample(
        id = "anterior_stemi",
        title = "IAM con elevación anterior",
        subtitle = "V2-V4 / DA",
        description = "Carga elevación del ST de 3 mm en precordiales anteriores y permite revisar criterios ST por edad/sexo.",
        pattern = EcgPathologyPattern.ANTERIOR_STEMI,
        focusLead = EcgLead.V3,
        references = "Fourth Universal Definition of MI; AHA/ACCF/HRS Part VI."
    ),
    EcgPathologyExample(
        id = "inferior_stemi",
        title = "IAM con elevación inferior",
        subtitle = "DII, DIII, aVF",
        description = "Carga elevación del ST inferior y descenso recíproco orientativo en lateral alta.",
        pattern = EcgPathologyPattern.INFERIOR_STEMI,
        focusLead = EcgLead.DIII,
        references = "Fourth Universal Definition of MI; STEMI territories."
    ),
    EcgPathologyExample(
        id = "hyperkalemia",
        title = "Hiperkalemia",
        subtitle = "T picudas + QRS ensanchado",
        description = "Carga QRS 130 ms, QT más corto y ondas T altas/picudas. No estima potasio sérico, solo representa patrón educativo.",
        pattern = EcgPathologyPattern.HYPERKALEMIA,
        focusLead = EcgLead.V4,
        references = "NCBI/StatPearls Hyperkalemia; ECG electrolyte patterns."
    ),
    EcgPathologyExample(
        id = "long_qt",
        title = "QT prolongado",
        subtitle = "QT/QTc prolongado",
        description = "Carga QT 520 ms con FC 70 lpm para revisar QTc por Bazett y Fridericia.",
        pattern = EcgPathologyPattern.LONG_QT,
        focusLead = EcgLead.DII,
        references = "AHA/ACCF/HRS Part IV; QT interval measurement."
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EcgPathologiesContent(state: EcgSharedInputState) {
    var selectedId by rememberSaveable { mutableStateOf(ecgPathologyExamples.first().id) }
    val selected = ecgPathologyExamples.firstOrNull { it.id == selectedId } ?: ecgPathologyExamples.first()
    val model = buildEcgPreviewModel(state)

    EcgCard(
        title = "Ejemplos de patologías ECG",
        note = "Toca una patología para cargar valores en las calculadoras del módulo ECG y ver un trazo didáctico. Sí, por fin las calculadoras dejan de vivir aisladas como residentes postguardia."
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ecgPathologyExamples.forEach { example ->
                PreviewChoiceChip(
                    text = example.title,
                    selected = selectedId == example.id,
                    onClick = {
                        selectedId = example.id
                        applyEcgPathologyExample(example, state)
                    }
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.40f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(selected.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                Text(selected.subtitle, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(selected.description, style = MaterialTheme.typography.bodySmall)
                Text(
                    "Al seleccionarlo se rellenan: FC/RR, PR, QRS, QT/QTc, eje, ST o HVI según corresponda.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Referencias: ${selected.references}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            DynamicEcgStrip(
                model = model,
                lead = selected.focusLead,
                title = "${selected.focusLead.label} · ${selected.title}",
                subtitle = selected.focusLead.region,
                highlighted = true,
                tint = MaterialTheme.colorScheme.primary,
                onTap = {},
                zoomLevel = 1.15f,
                modifier = Modifier
                    .width(ecgSingleLeadWidthDp(model, 1.15f))
                    .height(ecgSingleLeadHeightDp(1.15f))
            )
        }

        ResultGrid(
            listOfNotNull(
                "Patrón activo" to model.pathologyPattern.displayName,
                "FC" to "${model.heartRateBpm.roundClean()} lpm",
                "PR" to "${model.prMs.roundClean()} ms",
                "QRS" to "${model.qrsMs.roundClean()} ms",
                "QT" to "${model.qtMs.roundClean()} ms",
                model.qtcResult?.let { "QTc Bazett" to "${it.bazettMs.roundClean()} ms" },
                model.qtcResult?.let { "QTc Fridericia" to "${it.fridericiaMs.roundClean()} ms" },
                model.axisDegrees?.let { "Eje" to "${it.roundClean()}°" },
                "ST visual" to "${model.stMm.format(1)} mm",
                "Ritmo" to if (model.rhythmRegular) "Regular" else "Irregular"
            )
        )
        WarningText("Son ejemplos de enseñanza. Para un paciente real, mide el ECG real y confirma con clínica, derivaciones completas, biomarcadores o imagen según el caso.")
    }
}

private fun applyEcgPathologyExample(example: EcgPathologyExample, state: EcgSharedInputState) {
    state.pathologyPatternName.value = example.pattern.name
    state.ageText.value = "45"
    state.sexName.value = EcgSex.MALE.name
    state.heartRateText.value = "75"
    state.prText.value = "160"
    state.qrsText.value = "90"
    state.qtText.value = "390"
    state.axisText.value = "60"
    state.sinusRhythm.value = true
    state.regularRhythm.value = true
    state.includeSt.value = false
    state.stElevationText.value = "0"
    state.stLeadGroupName.value = EcgLeadGroup.OTHER_CONTIGUOUS.name
    state.stContiguous.value = true
    state.includeLvh.value = false
    state.sV1Text.value = "8"
    state.rV5Text.value = "14"
    state.rV6Text.value = "12"
    state.rAvlText.value = "5"
    state.sV3Text.value = "8"
    state.ratePaperSpeedName.value = EcgPaperSpeed.SPEED_25.name
    state.rateRrMsText.value = ""
    state.rateRrSecText.value = ""
    state.rateLargeSquaresText.value = ""
    state.rateSmallSquaresText.value = ""
    state.rateQrsCountText.value = ""
    state.qtcUseRr.value = false
    state.qtcRrText.value = ""
    state.axisMethodName.value = EcgAxisMethod.LEAD_I_AVF.name
    state.axisFirstText.value = "5"
    state.axisSecondText.value = "9"

    when (example.pattern) {
        EcgPathologyPattern.NORMAL_SINUS -> Unit
        EcgPathologyPattern.PVC -> {
            state.heartRateText.value = "82"
            state.prText.value = "160"
            state.qrsText.value = "160"
            state.qtText.value = "420"
            state.regularRhythm.value = false
        }
        EcgPathologyPattern.ATRIAL_FIBRILLATION -> {
            state.heartRateText.value = "125"
            state.prText.value = ""
            state.qrsText.value = "90"
            state.qtText.value = "340"
            state.sinusRhythm.value = false
            state.regularRhythm.value = false
        }
        EcgPathologyPattern.FIRST_DEGREE_AV_BLOCK -> {
            state.heartRateText.value = "68"
            state.prText.value = "240"
            state.qrsText.value = "90"
            state.qtText.value = "410"
        }
        EcgPathologyPattern.LEFT_VENTRICULAR_HYPERTROPHY -> {
            state.heartRateText.value = "78"
            state.qrsText.value = "100"
            state.qtText.value = "410"
            state.axisText.value = "-20"
            state.includeLvh.value = true
            state.sV1Text.value = "24"
            state.rV5Text.value = "26"
            state.rV6Text.value = "22"
            state.rAvlText.value = "13"
            state.sV3Text.value = "19"
            state.axisFirstText.value = "9"
            state.axisSecondText.value = "-3"
        }
        EcgPathologyPattern.RIGHT_VENTRICULAR_HYPERTROPHY -> {
            state.heartRateText.value = "92"
            state.qrsText.value = "96"
            state.qtText.value = "360"
            state.axisText.value = "115"
            state.axisFirstText.value = "-3"
            state.axisSecondText.value = "9"
        }
        EcgPathologyPattern.RIGHT_BUNDLE_BRANCH_BLOCK -> {
            state.heartRateText.value = "76"
            state.qrsText.value = "140"
            state.qtText.value = "430"
            state.axisText.value = "80"
        }
        EcgPathologyPattern.LEFT_BUNDLE_BRANCH_BLOCK -> {
            state.heartRateText.value = "72"
            state.qrsText.value = "150"
            state.qtText.value = "450"
            state.axisText.value = "-35"
            state.axisFirstText.value = "8"
            state.axisSecondText.value = "-5"
        }
        EcgPathologyPattern.ANTERIOR_STEMI -> {
            state.heartRateText.value = "96"
            state.qrsText.value = "92"
            state.qtText.value = "390"
            state.includeSt.value = true
            state.stElevationText.value = "3.0"
            state.stLeadGroupName.value = EcgLeadGroup.V2_V3.name
            state.axisText.value = "55"
        }
        EcgPathologyPattern.INFERIOR_STEMI -> {
            state.heartRateText.value = "88"
            state.qrsText.value = "94"
            state.qtText.value = "400"
            state.includeSt.value = true
            state.stElevationText.value = "2.5"
            state.stLeadGroupName.value = EcgLeadGroup.OTHER_CONTIGUOUS.name
            state.axisText.value = "80"
        }
        EcgPathologyPattern.HYPERKALEMIA -> {
            state.heartRateText.value = "58"
            state.prText.value = "210"
            state.qrsText.value = "130"
            state.qtText.value = "320"
            state.regularRhythm.value = true
        }
        EcgPathologyPattern.LONG_QT -> {
            state.heartRateText.value = "70"
            state.prText.value = "160"
            state.qrsText.value = "92"
            state.qtText.value = "520"
        }
    }
}

@Composable
private fun EcgPreviewContent(state: EcgSharedInputState) {
    val model = buildEcgPreviewModel(state)
    var selectorExpanded by rememberSaveable { mutableStateOf(false) }
    var modeName by rememberSaveable { mutableStateOf(EcgPreviewMode.SINGLE_LEAD.name) }
    var selectedLeadName by rememberSaveable { mutableStateOf(EcgLead.DII.name) }
    var selectedAreaIdsRaw by rememberSaveable { mutableStateOf(ecgAreaGroups.first().id) }
    var selectedTerritoryIdsRaw by rememberSaveable { mutableStateOf(ecgCoronaryGroups.first().id) }
    var zoomLevel by rememberSaveable { mutableStateOf(1.0f) }

    val mode = EcgPreviewMode.valueOf(modeName)
    val selectedLead = EcgLead.valueOf(selectedLeadName)
    val selectedAreaIds = selectedClusterIds(selectedAreaIdsRaw, ecgAreaGroups)
    val selectedTerritoryIds = selectedClusterIds(selectedTerritoryIdsRaw, ecgCoronaryGroups)
    val selectedAreas = ecgAreaGroups.filter { it.id in selectedAreaIds }.ifEmpty { listOf(ecgAreaGroups.first()) }
    val selectedTerritories = ecgCoronaryGroups.filter { it.id in selectedTerritoryIds }.ifEmpty { listOf(ecgCoronaryGroups.first()) }

    val activeClusters = when (mode) {
        EcgPreviewMode.CARDIAC_AREA -> selectedAreas
        EcgPreviewMode.CORONARY_TERRITORY -> selectedTerritories
        else -> emptyList()
    }
    val highlightedLeadColors = when (mode) {
        EcgPreviewMode.SINGLE_LEAD -> mapOf(selectedLead to listOf(MaterialTheme.colorScheme.primary))
        EcgPreviewMode.TWELVE_LEADS -> emptyMap()
        EcgPreviewMode.CARDIAC_AREA -> leadHighlightColors(selectedAreas)
        EcgPreviewMode.CORONARY_TERRITORY -> leadHighlightColors(selectedTerritories)
    }
    val highlightedLeads = highlightedLeadColors.keys
    val tint = activeClusters.firstOrNull()?.tint ?: MaterialTheme.colorScheme.primary
    val displayTitle = when (mode) {
        EcgPreviewMode.SINGLE_LEAD -> "Derivación ${selectedLead.label}"
        EcgPreviewMode.TWELVE_LEADS -> "ECG completo · 12 derivaciones"
        EcgPreviewMode.CARDIAC_AREA -> clusterSummaryTitle("Área", selectedAreas)
        EcgPreviewMode.CORONARY_TERRITORY -> clusterSummaryTitle("Territorio", selectedTerritories)
    }
    val displaySubtitle = when (mode) {
        EcgPreviewMode.SINGLE_LEAD -> selectedLead.region
        EcgPreviewMode.TWELVE_LEADS -> "Toca el ECG para abrir el menú de derivaciones y comparaciones. Pellizca el trazo para acercar o alejar."
        EcgPreviewMode.CARDIAC_AREA -> clusterSummarySubtitle(selectedAreas)
        EcgPreviewMode.CORONARY_TERRITORY -> clusterSummarySubtitle(selectedTerritories)
    }

    EcgCard(
        title = "Vista previa del ECG",
        note = "Vista didáctica generada con los datos capturados. Toca el trazo para elegir derivación, ECG completo, áreas cardíacas o territorios coronarios. Pellizca sobre el ECG para hacer zoom."
    ) {
        EcgPreviewModeHeader(
            title = displayTitle,
            subtitle = displaySubtitle,
            mode = mode,
            onOpenMenu = { selectorExpanded = !selectorExpanded }
        )

        AnimatedVisibility(
            visible = selectorExpanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            EcgPreviewSelectorPanel(
                mode = mode,
                onModeSelected = { modeName = it.name },
                selectedLead = selectedLead,
                onLeadSelected = {
                    selectedLeadName = it.name
                    modeName = EcgPreviewMode.SINGLE_LEAD.name
                    selectorExpanded = false
                },
                selectedAreaIds = selectedAreaIds,
                onAreaToggled = {
                    selectedAreaIdsRaw = toggleClusterSelection(selectedAreaIdsRaw, it, ecgAreaGroups)
                    modeName = EcgPreviewMode.CARDIAC_AREA.name
                },
                selectedTerritoryIds = selectedTerritoryIds,
                onTerritoryToggled = {
                    selectedTerritoryIdsRaw = toggleClusterSelection(selectedTerritoryIdsRaw, it, ecgCoronaryGroups)
                    modeName = EcgPreviewMode.CORONARY_TERRITORY.name
                }
            )
        }

        ZoomControls(
            zoomLevel = zoomLevel,
            onZoomOut = { zoomLevel = (zoomLevel - 0.25f).coerceAtLeast(0.75f) },
            onZoomIn = { zoomLevel = (zoomLevel + 0.25f).coerceAtMost(3.0f) },
            onReset = { zoomLevel = 1.0f }
        )
        Text(
            "También puedes pellizcar directamente el ECG para acercar o alejar.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        do {
                            val event = awaitPointerEvent()
                            val pressedPointers = event.changes.count { it.pressed }
                            if (pressedPointers >= 2) {
                                val zoomChange = event.calculateZoom()
                                if (zoomChange.isFinite() && zoomChange > 0f && zoomChange != 1f) {
                                    zoomLevel = (zoomLevel * zoomChange).coerceIn(0.75f, 3.0f)
                                }
                                event.changes.forEach { change ->
                                    if (change.positionChanged()) change.consume()
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
                .horizontalScroll(rememberScrollState())
        ) {
            if (mode == EcgPreviewMode.SINGLE_LEAD) {
                DynamicEcgStrip(
                    model = model,
                    lead = selectedLead,
                    title = displayTitle,
                    subtitle = displaySubtitle,
                    highlighted = true,
                    tint = tint,
                    onTap = { selectorExpanded = !selectorExpanded },
                    zoomLevel = zoomLevel,
                    modifier = Modifier
                        .width(ecgSingleLeadWidthDp(model, zoomLevel))
                        .height(ecgSingleLeadHeightDp(zoomLevel))
                )
            } else {
                DynamicTwelveLeadEcg(
                    model = model,
                    highlightedLeadColors = highlightedLeadColors,
                    title = displayTitle,
                    subtitle = displaySubtitle,
                    onTap = { selectorExpanded = !selectorExpanded },
                    zoomLevel = zoomLevel,
                    modifier = Modifier
                        .width(ecgTwelveLeadWidthDp(zoomLevel))
                        .height(ecgTwelveLeadHeightDp(zoomLevel))
                )
            }
        }

        if (mode == EcgPreviewMode.CARDIAC_AREA || mode == EcgPreviewMode.CORONARY_TERRITORY) {
            EcgLeadLegend(
                title = displayTitle,
                subtitle = displaySubtitle,
                clusters = activeClusters,
                leads = highlightedLeads
            )
        }

        ResultGrid(
            listOfNotNull(
                "Patrón" to model.pathologyPattern.displayName,
                "FC usada" to "${model.heartRateBpm.roundClean()} lpm",
                "RR" to "${model.rrMs.roundClean()} ms",
                "PR" to "${model.prMs.roundClean()} ms",
                "QRS" to "${model.qrsMs.roundClean()} ms",
                "QT medido" to "${model.qtMs.roundClean()} ms",
                model.qtcResult?.let { "QTc Bazett" to "${it.bazettMs.roundClean()} ms" },
                model.qtcResult?.let { "QTc Fridericia" to "${it.fridericiaMs.roundClean()} ms" },
                model.qtcPreferredMs?.let { "QTc usado" to "${it.roundClean()} ms · ${model.qtcSourceNote}" },
                "ST visual" to "${model.stMm.format(1)} mm",
                model.axisDegrees?.let { "Eje" to "${it.roundClean()}°" },
                "Ritmo" to if (model.rhythmRegular) "Regular" else "Irregular",
                "Origen FC" to model.sourceNote
            )
        )
        WarningText(
            "La imagen es una simulación didáctica. Las derivaciones, áreas e irrigación son aproximaciones visuales para estudio; no sustituyen ECG real, correlación clínica ni lectura especializada."
        )
    }
}

@Composable
private fun EcgPreviewModeHeader(
    title: String,
    subtitle: String,
    mode: EcgPreviewMode,
    onOpenMenu: () -> Unit
) {
    Surface(
        onClick = onOpenMenu,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.58f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EcgHeartMenuIcon(modifier = Modifier.size(34.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(mode.description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EcgPreviewSelectorPanel(
    mode: EcgPreviewMode,
    onModeSelected: (EcgPreviewMode) -> Unit,
    selectedLead: EcgLead,
    onLeadSelected: (EcgLead) -> Unit,
    selectedAreaIds: Set<String>,
    onAreaToggled: (EcgLeadCluster) -> Unit,
    selectedTerritoryIds: Set<String>,
    onTerritoryToggled: (EcgLeadCluster) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Menú de visualización ECG", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EcgPreviewMode.entries.forEach { item ->
                    PreviewChoiceChip(
                        text = item.label,
                        selected = item == mode,
                        onClick = { onModeSelected(item) }
                    )
                }
            }
            HorizontalDivider()
            Text("Derivaciones", fontWeight = FontWeight.Black)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EcgLead.entries.forEach { lead ->
                    PreviewChoiceChip(
                        text = lead.label,
                        selected = mode == EcgPreviewMode.SINGLE_LEAD && lead == selectedLead,
                        onClick = { onLeadSelected(lead) }
                    )
                }
            }
            HorizontalDivider()
            Text("Comparar por área cardíaca · selección múltiple", fontWeight = FontWeight.Black)
            Text(
                "Puedes marcar más de una zona; cada una conserva un color distinto sobre el ECG.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ecgAreaGroups.forEach { group ->
                    PreviewChoiceChip(
                        text = group.title,
                        selected = mode == EcgPreviewMode.CARDIAC_AREA && group.id in selectedAreaIds,
                        selectedColor = group.tint,
                        onClick = { onAreaToggled(group) }
                    )
                }
            }
            HorizontalDivider()
            Text("Comparar por irrigación coronaria · selección múltiple", fontWeight = FontWeight.Black)
            Text(
                "Puedes activar más de un territorio para comparar superposición de derivaciones.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ecgCoronaryGroups.forEach { group ->
                    PreviewChoiceChip(
                        text = group.title,
                        selected = mode == EcgPreviewMode.CORONARY_TERRITORY && group.id in selectedTerritoryIds,
                        selectedColor = group.tint,
                        onClick = { onTerritoryToggled(group) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewChoiceChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = if (selected) selectedColor.copy(alpha = 0.86f) else MaterialTheme.colorScheme.surfaceContainer,
        contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, if (selected) selectedColor else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold
        )
    }
}

@Composable
private fun ZoomControls(
    zoomLevel: Float,
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(onClick = onZoomOut, modifier = Modifier.weight(1f)) { Text("− Zoom") }
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                "${(zoomLevel * 100f).roundToInt()}%",
                modifier = Modifier.padding(vertical = 10.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black
            )
        }
        OutlinedButton(onClick = onZoomIn, modifier = Modifier.weight(1f)) { Text("+ Zoom") }
        OutlinedButton(onClick = onReset, modifier = Modifier.weight(1f)) { Text("Restablecer") }
    }
}

@Composable
private fun EcgLeadLegend(
    title: String,
    subtitle: String,
    clusters: List<EcgLeadCluster>,
    leads: Set<EcgLead>
) {
    val borderTint = clusters.firstOrNull()?.tint ?: MaterialTheme.colorScheme.primary
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, borderTint.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, fontWeight = FontWeight.Black, color = borderTint)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
            clusters.forEach { cluster ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = cluster.tint.copy(alpha = 0.13f),
                    border = BorderStroke(1.dp, cluster.tint.copy(alpha = 0.32f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(shape = RoundedCornerShape(999.dp), color = cluster.tint, modifier = Modifier.size(14.dp)) {}
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cluster.title, fontWeight = FontWeight.Black, color = cluster.tint)
                            Text(cluster.subtitle, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            Text(
                "Derivaciones resaltadas: ${leads.sortedBy { it.ordinal }.joinToString { it.label }}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun ecgPreviewDisplayMs(model: EcgPreviewModel): Double = when {
    model.heartRateBpm < 55.0 -> 9000.0
    model.heartRateBpm > 140.0 -> 4200.0
    else -> 6200.0
}

private fun ecgSingleLeadWidthDp(model: EcgPreviewModel, zoomLevel: Float): Dp {
    val displayMm = ecgPreviewDisplayMs(model) * ECG_PAPER_SPEED_MM_PER_SECOND / 1000.0
    val widthDp = displayMm.toFloat() * ECG_SMALL_SQUARE_DP * zoomLevel + 20f
    return widthDp.coerceIn(520f, 2200f).dp
}

private fun ecgSingleLeadHeightDp(zoomLevel: Float): Dp {
    val heightDp = ECG_SINGLE_LEAD_VERTICAL_MM * ECG_SMALL_SQUARE_DP * zoomLevel + 20f
    return heightDp.coerceIn(230f, 980f).dp
}

private fun ecgTwelveLeadWidthDp(zoomLevel: Float): Dp {
    val displayMm = ECG_TWELVE_LEAD_SEGMENT_MS * ECG_PAPER_SPEED_MM_PER_SECOND / 1000.0
    val widthDp = displayMm.toFloat() * ECG_SMALL_SQUARE_DP * 4f * zoomLevel + 20f
    return widthDp.coerceIn(760f, 3600f).dp
}

private fun ecgTwelveLeadHeightDp(zoomLevel: Float): Dp {
    val heightDp = 70f + (ECG_TWELVE_LEAD_VERTICAL_MM * ECG_SMALL_SQUARE_DP * 3f * zoomLevel) + 20f
    return heightDp.coerceIn(520f, 2300f).dp
}

@Composable
private fun DynamicEcgStrip(
    model: EcgPreviewModel,
    lead: EcgLead,
    title: String,
    subtitle: String,
    highlighted: Boolean,
    tint: Color,
    onTap: () -> Unit,
    zoomLevel: Float,
    modifier: Modifier = Modifier
) {
    val gridMinor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
    val gridMajor = MaterialTheme.colorScheme.primary.copy(alpha = 0.23f)
    val baselineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.50f)
    val traceColor = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick = onTap,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 2.dp,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val smallSquarePx = ECG_SMALL_SQUARE_DP.dp.toPx() * zoomLevel
            drawEcgGrid(gridMinor, gridMajor, smallSquarePx)
            if (highlighted) {
                drawRect(
                    color = tint.copy(alpha = 0.10f),
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height)
                )
            }
            drawLeadTrace(
                model = model,
                lead = lead,
                topLeft = Offset(0f, 0f),
                width = size.width,
                height = size.height,
                baselineColor = baselineColor,
                traceColor = traceColor,
                labelColor = labelColor,
                label = "$title · ${model.heartRateBpm.roundClean()} lpm",
                footer = subtitle,
                smallSquarePx = smallSquarePx
            )
        }
    }
}

@Composable
private fun DynamicTwelveLeadEcg(
    model: EcgPreviewModel,
    highlightedLeadColors: Map<EcgLead, List<Color>>,
    title: String,
    subtitle: String,
    onTap: () -> Unit,
    zoomLevel: Float,
    modifier: Modifier = Modifier
) {
    val gridMinor = MaterialTheme.colorScheme.primary.copy(alpha = 0.09f)
    val gridMajor = MaterialTheme.colorScheme.primary.copy(alpha = 0.21f)
    val baselineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.44f)
    val traceColor = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick = onTap,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 2.dp,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val smallSquarePx = ECG_SMALL_SQUARE_DP.dp.toPx() * zoomLevel
            drawEcgGrid(gridMinor, gridMajor, smallSquarePx)
            val rows = twelveLeadLayout.size
            val columns = twelveLeadLayout.first().size
            val headerH = 70f
            val cellW = size.width / columns
            val cellH = (size.height - headerH).coerceAtLeast(260f) / rows
            val headerPaint = android.graphics.Paint().apply {
                color = labelColor.toArgb()
                textSize = 28f
                isAntiAlias = true
                isFakeBoldText = true
            }
            drawContext.canvas.nativeCanvas.drawText(title, 14f, 30f, headerPaint)
            drawContext.canvas.nativeCanvas.drawText(subtitle, 14f, 60f, android.graphics.Paint(headerPaint).apply { textSize = 22f; isFakeBoldText = false })

            twelveLeadLayout.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { columnIndex, lead ->
                    val left = columnIndex * cellW
                    val top = headerH + rowIndex * cellH
                    val highlightColors = highlightedLeadColors[lead].orEmpty()
                    if (highlightColors.isNotEmpty()) {
                        val stripeWidth = (cellW - 6f) / highlightColors.size.coerceAtLeast(1)
                        highlightColors.forEachIndexed { index, color ->
                            drawRect(
                                color = color.copy(alpha = if (highlightColors.size == 1) 0.14f else 0.18f),
                                topLeft = Offset(left + 3f + index * stripeWidth, top + 3f),
                                size = Size(stripeWidth, cellH - 6f)
                            )
                        }
                    }
                    val segmentMs = ECG_TWELVE_LEAD_SEGMENT_MS
                    val segmentStartMs = columnIndex * segmentMs
                    drawLeadTrace(
                        model = model,
                        lead = lead,
                        topLeft = Offset(left + 8f, top + 14f),
                        width = cellW - 16f,
                        height = cellH - 22f,
                        baselineColor = baselineColor,
                        traceColor = highlightColors.firstOrNull() ?: traceColor,
                        labelColor = labelColor,
                        label = lead.label,
                        footer = lead.region,
                        compactLabel = true,
                        displayMsOverride = segmentMs,
                        segmentStartMs = segmentStartMs,
                        smallSquarePx = smallSquarePx
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawEcgGrid(
    minorColor: Color,
    majorColor: Color,
    smallSquarePx: Float
) {
    val minorStep = smallSquarePx.coerceAtLeast(2.5f)
    val majorStep = minorStep * 5f
    var x = 0f
    var index = 0
    while (x <= size.width + minorStep) {
        val major = index % 5 == 0
        drawLine(
            color = if (major) majorColor else minorColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = if (major) (1.15f * (minorStep / 5f).coerceIn(0.75f, 2.3f)) else (0.55f * (minorStep / 5f).coerceIn(0.75f, 1.8f))
        )
        index += 1
        x = index * minorStep
    }
    var y = 0f
    index = 0
    while (y <= size.height + minorStep) {
        val major = index % 5 == 0
        drawLine(
            color = if (major) majorColor else minorColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = if (major) (1.15f * (minorStep / 5f).coerceIn(0.75f, 2.3f)) else (0.55f * (minorStep / 5f).coerceIn(0.75f, 1.8f))
        )
        index += 1
        y = index * minorStep
    }
    // Líneas guía de 5 mm más visibles: equivalen a cuadros grandes del papel ECG.
    if (majorStep >= 18f) {
        var gx = 0f
        while (gx <= size.width) {
            drawLine(majorColor.copy(alpha = majorColor.alpha * 1.18f), Offset(gx, 0f), Offset(gx, size.height), strokeWidth = 1.45f)
            gx += majorStep
        }
        var gy = 0f
        while (gy <= size.height) {
            drawLine(majorColor.copy(alpha = majorColor.alpha * 1.18f), Offset(0f, gy), Offset(size.width, gy), strokeWidth = 1.45f)
            gy += majorStep
        }
    }
}

private fun DrawScope.drawLeadTrace(
    model: EcgPreviewModel,
    lead: EcgLead,
    topLeft: Offset,
    width: Float,
    height: Float,
    baselineColor: Color,
    traceColor: Color,
    labelColor: Color,
    label: String,
    footer: String,
    compactLabel: Boolean = false,
    displayMsOverride: Double? = null,
    segmentStartMs: Double = 0.0,
    smallSquarePx: Float
) {
    val baseline = topLeft.y + height * 0.56f
    drawLine(
        color = baselineColor,
        start = Offset(topLeft.x, baseline),
        end = Offset(topLeft.x + width, baseline),
        strokeWidth = 1.15f
    )

    val displayMs = displayMsOverride ?: ecgPreviewDisplayMs(model)
    val paperPxPerMs = ((ECG_PAPER_SPEED_MM_PER_SECOND / 1000.0) * smallSquarePx).toFloat()
    val segmentEndMs = segmentStartMs + displayMs
    fun xFor(globalMs: Double): Float = topLeft.x + ((globalMs - segmentStartMs).toFloat() * paperPxPerMs)
    fun yFor(mm: Double): Float = baseline - (mm.toFloat() * smallSquarePx)

    val pattern = model.pathologyPattern
    val projectionAxis = when (pattern) {
        EcgPathologyPattern.RIGHT_VENTRICULAR_HYPERTROPHY -> 115.0
        EcgPathologyPattern.LEFT_BUNDLE_BRANCH_BLOCK -> -35.0
        else -> model.axisDegrees
    }
    val projection = leadProjection(projectionAxis, lead)
    val qrsSign = if (projection < -0.18) -1.0 else 1.0
    val axisMagnitude = abs(projection).coerceIn(0.34, 1.0)
    val lvhActive = model.lvhPositive || pattern == EcgPathologyPattern.LEFT_VENTRICULAR_HYPERTROPHY
    val qrsAmplitude = when {
        pattern == EcgPathologyPattern.RIGHT_VENTRICULAR_HYPERTROPHY && lead == EcgLead.V1 -> 14.0
        pattern == EcgPathologyPattern.RIGHT_VENTRICULAR_HYPERTROPHY && (lead == EcgLead.V5 || lead == EcgLead.V6) -> 7.0
        lvhActive -> 16.5 * (0.60 + 0.40 * axisMagnitude)
        else -> 10.0 * (0.55 + 0.45 * axisMagnitude)
    }
    val pAmplitude = when {
        pattern == EcgPathologyPattern.ATRIAL_FIBRILLATION -> 0.0
        model.rhythmSinus -> 1.25 * qrsSign
        else -> 0.20
    }
    val qtcPreferred = model.qtcPreferredMs ?: model.qtMs
    val stVisual = ecgStForLead(model, lead).coerceIn(-4.0, 5.0) * if (qrsSign < 0) 0.75 else 1.0
    val tAmplitude = when {
        pattern == EcgPathologyPattern.HYPERKALEMIA -> 7.8 * qrsSign
        pattern == EcgPathologyPattern.LEFT_VENTRICULAR_HYPERTROPHY && (lead == EcgLead.V5 || lead == EcgLead.V6 || lead == EcgLead.DI || lead == EcgLead.AVL) -> -2.4
        qtcPreferred >= 500.0 -> 3.0 * qrsSign
        stVisual < -0.5 -> -2.0 * qrsSign
        else -> 2.8 * qrsSign
    }
    val lateralLead = lead == EcgLead.DI || lead == EcgLead.AVL || lead == EcgLead.V5 || lead == EcgLead.V6
    val lateralLowLead = lead == EcgLead.V5 || lead == EcgLead.V6
    val irregularFactors = listOf(0.88, 1.12, 0.76, 1.22, 0.95, 1.08)
    var beatStart = 80.0
    var beatIndex = 0
    while (beatStart + model.rrMs < segmentStartMs - 260.0) {
        val rrFactor = if (model.rhythmRegular) 1.0 else irregularFactors[beatIndex % irregularFactors.size]
        val rr = (model.rrMs * rrFactor).coerceAtLeast(260.0)
        beatStart += rr
        beatIndex += 1
    }
    val path = Path().apply {
        moveTo(topLeft.x, baseline)
        while (beatStart < segmentEndMs + model.rrMs) {
            val rrFactor = if (model.rhythmRegular) 1.0 else irregularFactors[beatIndex % irregularFactors.size]
            val rr = (model.rrMs * rrFactor).coerceAtLeast(260.0)
            val isPvcBeat = pattern == EcgPathologyPattern.PVC && beatIndex % 4 == 1
            val pr = if (isPvcBeat) 0.0 else model.prMs.coerceIn(80.0, 340.0)
            val qrs = (if (isPvcBeat) model.qrsMs.coerceAtLeast(160.0) else model.qrsMs).coerceIn(50.0, 230.0)
            val qt = model.qtMs.coerceIn(qrs + 140.0, (rr * 0.88).coerceAtLeast(qrs + 170.0))
            val qrsOn = if (isPvcBeat) beatStart + rr * 0.30 else beatStart + pr
            val qrsEnd = qrsOn + qrs
            val pStart = (qrsOn - 115.0).coerceAtLeast(beatStart + 10.0)
            val pPeak = pStart + 42.0
            val pEnd = (qrsOn - 24.0).coerceAtLeast(pPeak + 20.0)
            val rPeak = qrsOn + qrs * 0.45
            val stEnd = qrsEnd + 115.0
            val tEnd = qrsOn + qt
            val tPeak = (stEnd + (tEnd - stEnd) * 0.45).coerceAtLeast(stEnd + 35.0)
            val showP = pAmplitude != 0.0 && !isPvcBeat

            if (showP) {
                lineTo(xFor(pStart), yFor(0.0))
                quadraticBezierTo(xFor(pPeak), yFor(pAmplitude), xFor(pEnd), yFor(0.0))
            }
            if (pattern == EcgPathologyPattern.ATRIAL_FIBRILLATION) {
                lineTo(xFor(qrsOn - 52.0), yFor(0.25 * kotlin.math.sin(beatIndex.toDouble() * 1.7)))
                lineTo(xFor(qrsOn - 28.0), yFor(-0.18 * kotlin.math.cos(beatIndex.toDouble() * 1.4)))
            }
            lineTo(xFor(qrsOn), yFor(0.0))
            when {
                isPvcBeat -> {
                    val pvcSign = -qrsSign
                    lineTo(xFor(qrsOn + qrs * 0.18), yFor(-2.4 * pvcSign))
                    lineTo(xFor(qrsOn + qrs * 0.48), yFor(qrsAmplitude * 1.25 * pvcSign))
                    lineTo(xFor(qrsOn + qrs * 0.86), yFor(-5.0 * pvcSign))
                }
                pattern == EcgPathologyPattern.RIGHT_BUNDLE_BRANCH_BLOCK && lead == EcgLead.V1 -> {
                    lineTo(xFor(qrsOn + qrs * 0.16), yFor(3.5))
                    lineTo(xFor(qrsOn + qrs * 0.36), yFor(-3.2))
                    lineTo(xFor(qrsOn + qrs * 0.76), yFor(qrsAmplitude * 1.10))
                }
                pattern == EcgPathologyPattern.RIGHT_BUNDLE_BRANCH_BLOCK && lateralLead -> {
                    lineTo(xFor(qrsOn + qrs * 0.18), yFor(-1.4 * qrsSign))
                    lineTo(xFor(qrsOn + qrs * 0.42), yFor(qrsAmplitude * qrsSign))
                    lineTo(xFor(qrsOn + qrs * 0.88), yFor(-5.2))
                }
                pattern == EcgPathologyPattern.LEFT_BUNDLE_BRANCH_BLOCK && lead == EcgLead.V1 -> {
                    lineTo(xFor(qrsOn + qrs * 0.22), yFor(-qrsAmplitude * 0.70))
                    lineTo(xFor(qrsOn + qrs * 0.55), yFor(-qrsAmplitude * 1.05))
                    lineTo(xFor(qrsOn + qrs * 0.86), yFor(-qrsAmplitude * 0.70))
                }
                pattern == EcgPathologyPattern.LEFT_BUNDLE_BRANCH_BLOCK && lateralLead -> {
                    lineTo(xFor(qrsOn + qrs * 0.20), yFor(qrsAmplitude * 0.75))
                    lineTo(xFor(qrsOn + qrs * 0.48), yFor(qrsAmplitude * 1.10))
                    lineTo(xFor(qrsOn + qrs * 0.66), yFor(qrsAmplitude * 0.82))
                    lineTo(xFor(qrsOn + qrs * 0.88), yFor(qrsAmplitude * 1.02))
                }
                pattern == EcgPathologyPattern.RIGHT_VENTRICULAR_HYPERTROPHY && lead == EcgLead.V1 -> {
                    lineTo(xFor(qrsOn + qrs * 0.18), yFor(-1.2))
                    lineTo(xFor(rPeak), yFor(qrsAmplitude * 1.18))
                    lineTo(xFor(qrsOn + qrs * 0.78), yFor(-2.0))
                }
                pattern == EcgPathologyPattern.RIGHT_VENTRICULAR_HYPERTROPHY && lateralLowLead -> {
                    lineTo(xFor(qrsOn + qrs * 0.20), yFor(3.0))
                    lineTo(xFor(qrsOn + qrs * 0.72), yFor(-7.5))
                }
                else -> {
                    lineTo(xFor(qrsOn + qrs * 0.16), yFor(-1.6 * qrsSign))
                    lineTo(xFor(rPeak), yFor(qrsAmplitude * qrsSign))
                    lineTo(xFor(qrsOn + qrs * 0.72), yFor(-3.0 * qrsSign))
                }
            }
            lineTo(xFor(qrsEnd), yFor(stVisual))
            lineTo(xFor(stEnd), yFor(stVisual))
            quadraticBezierTo(xFor(tPeak), yFor(tAmplitude + stVisual * 0.18), xFor(tEnd), yFor(0.0))
            lineTo(xFor(beatStart + rr), yFor(0.0))

            beatStart += if (isPvcBeat) rr * 1.35 else rr
            beatIndex += 1
        }
        lineTo(topLeft.x + width, baseline)
    }
    clipRect(
        left = topLeft.x,
        top = topLeft.y,
        right = topLeft.x + width,
        bottom = topLeft.y + height
    ) {
        drawPath(path = path, color = traceColor, style = Stroke(width = if (compactLabel) 2.45f else 3.2f, cap = StrokeCap.Round))
    }

    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = labelColor.toArgb()
            textSize = if (compactLabel) 21f else 28f
            isAntiAlias = true
            isFakeBoldText = true
        }
        drawText(label, topLeft.x + 10f, topLeft.y + if (compactLabel) 24f else 34f, paint)
        val footerPaint = android.graphics.Paint(paint).apply {
            textSize = if (compactLabel) 17f else 22f
            isFakeBoldText = false
            alpha = 205
        }
        drawText(footer, topLeft.x + 10f, topLeft.y + height - 10f, footerPaint)
    }
}

private fun leadProjection(axisDegrees: Double?, lead: EcgLead): Double {
    if (lead.limbAxisDegrees == null) return lead.precordialProjection
    val axis = axisDegrees ?: 60.0
    return cos((axis - lead.limbAxisDegrees) * PI / 180.0).coerceIn(-1.0, 1.0)
}

private fun ecgStForLead(model: EcgPreviewModel, lead: EcgLead): Double = when (model.pathologyPattern) {
    EcgPathologyPattern.ANTERIOR_STEMI -> when (lead) {
        EcgLead.V2, EcgLead.V3, EcgLead.V4, EcgLead.V5 -> model.stMm.takeIf { it != 0.0 } ?: 3.0
        EcgLead.DII, EcgLead.DIII, EcgLead.AVF -> -0.6
        else -> 0.0
    }
    EcgPathologyPattern.INFERIOR_STEMI -> when (lead) {
        EcgLead.DII, EcgLead.DIII, EcgLead.AVF -> model.stMm.takeIf { it != 0.0 } ?: 2.5
        EcgLead.DI, EcgLead.AVL -> -1.0
        else -> 0.0
    }
    else -> model.stMm
}

private fun buildEcgPreviewModel(state: EcgSharedInputState): EcgPreviewModel {
    val paperSpeed = EcgPaperSpeed.valueOf(state.ratePaperSpeedName.value)
    val directRate = state.heartRateText.value.toDecimalOrNull()
    val rateCandidates = listOfNotNull(
        directRate,
        state.rateRrMsText.value.toDecimalOrNull()?.let { EcgCalculator.rateFromRrMs(it).getOrNull()?.bpm },
        state.rateRrSecText.value.toDecimalOrNull()?.let { EcgCalculator.rateFromRrSeconds(it).getOrNull()?.bpm },
        state.rateLargeSquaresText.value.toDecimalOrNull()?.let { EcgCalculator.rateFromLargeSquares(it, paperSpeed).getOrNull()?.bpm },
        state.rateSmallSquaresText.value.toDecimalOrNull()?.let { EcgCalculator.rateFromSmallSquares(it, paperSpeed).getOrNull()?.bpm },
        state.rateQrsCountText.value.toIntOrNull()?.let { EcgCalculator.rateFromTenSecondStrip(it).getOrNull()?.bpm }
    )
    val heartRate = (rateCandidates.firstOrNull() ?: 75.0).coerceIn(25.0, 240.0)
    val rrMs = EcgCalculator.rrMsFromHeartRate(heartRate).getOrDefault(800.0)
    val qtMs = (state.qtText.value.toDecimalOrNull() ?: 390.0).coerceIn(180.0, 720.0)
    val qtcResult = if (state.qtcUseRr.value) {
        state.qtcRrText.value.toDecimalOrNull()?.let { EcgCalculator.qtcFromRrMs(qtMs, it).getOrNull() }
            ?: EcgCalculator.qtcFromHeartRate(qtMs, heartRate).getOrNull()
    } else {
        EcgCalculator.qtcFromHeartRate(qtMs, heartRate).getOrNull()
    }
    val qtcPreferredMs = qtcResult?.let {
        if (heartRate < 60.0 || heartRate > 100.0) it.fridericiaMs else it.bazettMs
    }
    val qtcSourceNote = when {
        state.qtcUseRr.value && state.qtcRrText.value.toDecimalOrNull() != null -> "por RR capturado"
        directRate != null || rateCandidates.isNotEmpty() -> "por FC"
        else -> "por ejemplo 75 lpm"
    }
    val axisFromCalculator = state.axisFirstText.value.toDecimalOrNull()?.let { first ->
        state.axisSecondText.value.toDecimalOrNull()?.let { second ->
            EcgCalculator.calculateAxis(EcgAxisMethod.valueOf(state.axisMethodName.value), first, second).degrees
        }
    }
    val sex = EcgSex.valueOf(state.sexName.value)
    val lvh = EcgCalculator.lvh(
        sex = sex,
        sV1Mm = state.sV1Text.value.toDecimalOrNull(),
        rV5Mm = state.rV5Text.value.toDecimalOrNull(),
        rV6Mm = state.rV6Text.value.toDecimalOrNull(),
        rAvlMm = state.rAvlText.value.toDecimalOrNull(),
        sV3Mm = state.sV3Text.value.toDecimalOrNull(),
        qrsDurationMs = state.qrsText.value.toDecimalOrNull()
    )
    val lvhPositive = lvh.sokolowPositive == true || lvh.cornellVoltagePositive == true || lvh.cornellProductPositive == true
    val sourceNote = when {
        directRate != null -> "FC directa"
        rateCandidates.isNotEmpty() -> "FC calculada"
        else -> "Ejemplo 75 lpm"
    }
    return EcgPreviewModel(
        heartRateBpm = heartRate,
        rrMs = rrMs,
        prMs = (state.prText.value.toDecimalOrNull() ?: 160.0).coerceIn(60.0, 360.0),
        qrsMs = (state.qrsText.value.toDecimalOrNull() ?: 90.0).coerceIn(45.0, 240.0),
        qtMs = qtMs,
        qtcResult = qtcResult,
        qtcPreferredMs = qtcPreferredMs,
        qtcSourceNote = qtcSourceNote,
        stMm = state.stElevationText.value.toDecimalOrNull()?.coerceIn(-5.0, 8.0) ?: 0.0,
        axisDegrees = state.axisText.value.toDecimalOrNull() ?: axisFromCalculator,
        rhythmSinus = state.sinusRhythm.value,
        rhythmRegular = state.regularRhythm.value,
        lvhPositive = lvhPositive,
        pathologyPattern = runCatching { EcgPathologyPattern.valueOf(state.pathologyPatternName.value) }.getOrDefault(EcgPathologyPattern.NORMAL_SINUS),
        sourceNote = sourceNote
    )
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

    EcgTool.PATHOLOGIES -> EcgInfoContent(
        title = "Patologías ECG · ejemplos didácticos",
        purpose = "Muestra patrones clínicos frecuentes como ejemplos configurables y rellena las calculadoras ya existentes para que el usuario compare FC, RR, PR, QRS, QT/QTc, eje, ST e HVI.",
        method = "Cada patología carga un conjunto de valores de ejemplo en el estado compartido del módulo ECG. La vista previa usa esos valores para dibujar un trazo simulado y las calculadoras quedan listas para revisar los criterios relacionados.",
        references = listOf(
            EcgReferenceItem(
                source = "AHA/ACCF/HRS",
                citation = "Recommendations for the Standardization and Interpretation of the Electrocardiogram. Parts III, IV, V and VI. J Am Coll Cardiol. 2009.",
                useInApp = "Criterios y contexto para conducción intraventricular, ST/QT, crecimiento de cavidades e isquemia/infarto."
            ),
            EcgReferenceItem(
                source = "Life in the Fast Lane",
                citation = "ECG Library: PVCs, atrial fibrillation, bundle branch block, LVH, STEMI territories and electrolyte patterns.",
                useInApp = "Morfología educativa de ejemplos y correlación visual por derivaciones."
            ),
            EcgReferenceItem(
                source = "NCBI Bookshelf / StatPearls",
                citation = "Electrocardiogram, Bundle Branch Block, STEMI and Hyperkalemia clinical reviews.",
                useInApp = "Cotejo de descripciones clínicas y rasgos electrocardiográficos."
            )
        ),
        limitations = listOf(
            "Los ejemplos son plantillas didácticas, no pacientes reales ni diagnóstico automático.",
            "La morfología puede variar por derivación, edad, electrodos, cardiopatía previa y contexto clínico.",
            "Al tocar un ejemplo se rellenan calculadoras, pero el usuario debe verificar medidas reales si evalúa un ECG clínico."
        )
    )

    EcgTool.PREVIEW -> EcgInfoContent(
        title = "Vista previa ECG · referencias",
        purpose = "Dibuja una tira de ritmo y un ECG de 12 derivaciones simulados, configurados por los valores capturados en las calculadoras ECG.",
        method = "No interpreta imágenes ni reconstruye un ECG real. Usa FC/RR para separar complejos, PR para ubicar P-QRS, QRS para anchura del complejo, QT y QTc para duración de repolarización, ST para desplazamiento visual, eje para polaridad aproximada por derivación y HVI para aumentar voltaje didáctico. Permite vista por derivación, 12 derivaciones, áreas cardíacas y territorios coronarios con sombreado tenue.",
        references = listOf(
            EcgReferenceItem(
                source = "AHA/ACCF/HRS",
                citation = "Recommendations for the Standardization and Interpretation of the Electrocardiogram. Part I: the electrocardiogram and its technology. J Am Coll Cardiol. 2007;49:1109–1127.",
                useInApp = "Base técnica de calibración, velocidad de papel, rejilla y representación temporal del ECG."
            ),
            EcgReferenceItem(
                source = "AHA/ACCF/HRS",
                citation = "Recommendations for the Standardization and Interpretation of the Electrocardiogram. Part IV: ST segment, T and U waves, and QT interval. J Am Coll Cardiol. 2009;53:982–991.",
                useInApp = "Relación visual de ST, onda T, QT y QTc dentro del trazo simulado."
            ),
            EcgReferenceItem(
                source = "Life in the Fast Lane",
                citation = "ECG Basics, ECG Rate Interpretation y territorios de IAM: papel estándar, velocidad 25 mm/s, cuadros de 40 ms/200 ms, derivaciones y regiones.",
                useInApp = "Cotejo educativo para escala de tiempo, lectura de la tira, regiones y correlación de derivaciones."
            ),
            EcgReferenceItem(
                source = "AHA/ACCF/HRS",
                citation = "Recommendations for the Standardization and Interpretation of the Electrocardiogram. Part VI: Acute ischemia/infarction. J Am Coll Cardiol. 2009;53:1003–1011.",
                useInApp = "Concepto de derivaciones contiguas, región anatómica y correlación orientativa con arteria culpable."
            )
        ),
        limitations = listOf(
            "La imagen es una simulación didáctica generada por parámetros, no una señal clínica adquirida.",
            "La morfología por derivación es aproximada y didáctica; no sustituye un ECG de 12 derivaciones adquirido.",
            "La correlación área-arteria coronaria es orientativa: dominancia, variantes anatómicas y extensión del evento pueden cambiar la interpretación."
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
