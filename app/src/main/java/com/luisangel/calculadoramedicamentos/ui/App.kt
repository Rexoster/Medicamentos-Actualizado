package com.luisangel.calculadoramedicamentos.ui

import android.graphics.Paint as AndroidPaint
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PregnantWoman
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luisangel.calculadoramedicamentos.R
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
import com.luisangel.calculadoramedicamentos.renal.AcrUnit
import com.luisangel.calculadoramedicamentos.renal.AlbuminuriaCategory
import com.luisangel.calculadoramedicamentos.renal.CkdRiskLevel
import com.luisangel.calculadoramedicamentos.renal.CreatinineUnit
import com.luisangel.calculadoramedicamentos.renal.GfrCategory
import com.luisangel.calculadoramedicamentos.renal.RenalCalculator
import com.luisangel.calculadoramedicamentos.renal.RenalInput
import com.luisangel.calculadoramedicamentos.renal.RenalMethod
import com.luisangel.calculadoramedicamentos.renal.RenalResult
import com.luisangel.calculadoramedicamentos.renal.RenalSex
import com.luisangel.calculadoramedicamentos.model.FilterState
import com.luisangel.calculadoramedicamentos.model.MedicationDraft
import com.luisangel.calculadoramedicamentos.model.MedicationRecord
import com.luisangel.calculadoramedicamentos.model.MedicationType
import com.luisangel.calculadoramedicamentos.model.SortOption
import com.luisangel.calculadoramedicamentos.model.TypeFilter
import com.luisangel.calculadoramedicamentos.model.calculatedDose
import com.luisangel.calculadoramedicamentos.model.fingerprint
import com.luisangel.calculadoramedicamentos.model.hasValidInteractiveRange
import com.luisangel.calculadoramedicamentos.model.interactiveDoseStart
import com.luisangel.calculadoramedicamentos.model.toDraft
import com.luisangel.calculadoramedicamentos.model.toRecord
import com.luisangel.calculadoramedicamentos.model.validationError
import com.luisangel.calculadoramedicamentos.ui.theme.CalculatorTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt


private data class ClinicalReferenceItem(
    val source: String,
    val citation: String,
    val useInApp: String
)

private data class ClinicalInfoContent(
    val title: String,
    val purpose: String,
    val method: String,
    val references: List<ClinicalReferenceItem>,
    val limitations: List<String>,
    val reviewedOn: String = "17 de junio de 2026"
)

private fun medicationClinicalInfo() = ClinicalInfoContent(
    title = "Medicamentos · información y referencias",
    purpose = (
        "La aplicación almacena un catálogo local de medicamentos y calcula " +
            "peso × dosis por kilogramo cuando el registro lo requiere."
        ),
    method = (
        "Los medicamentos agregados por el usuario no son validados contra una " +
            "base farmacológica. Las referencias siguientes corresponden solamente " +
            "a los ejemplos precargados de paracetamol e insulina basal."
        ),
    references = listOf(
        ClinicalReferenceItem(
            source = "Facultad de Medicina, UNAM",
            citation = "Paracetamol. Biblioteca Médica Digital, catálogo de medicamentos.",
            useInApp = "Referencia farmacológica general del ejemplo de paracetamol."
        ),
        ClinicalReferenceItem(
            source = "PLM México",
            citation = "Monografías de paracetamol y suspensión infantil 160 mg/5 mL.",
            useInApp = "Presentación, frecuencia orientativa y rango pediátrico del ejemplo."
        ),
        ClinicalReferenceItem(
            source = "Consejo de Salubridad General, México",
            citation = "PRONAM: Diabetes tipo 2 y síndrome metabólico, edición 2025.",
            useInApp = "Ejemplo educativo de inicio y titulación de insulina basal."
        )
    ),
    limitations = listOf(
        "La app no comprueba interacciones, contraindicaciones, función renal, embarazo ni alergias.",
        "Las dosis introducidas o importadas son responsabilidad del usuario.",
        "Los pesos se usan durante la sesión y no se almacenan."
    )
)

private fun percentileClinicalInfo() = ClinicalInfoContent(
    title = "Percentiles pediátricos · información y referencias",
    purpose = (
        "Calcula puntajes Z y percentiles de crecimiento pediátrico con parámetros " +
            "LMS incorporados localmente."
        ),
    method = (
        "La referencia principal es OMS. La app usa estándares 0–5 años y la " +
            "referencia 5–19 años. CDC se documenta como referencia alternativa, " +
            "pero no se mezcla con OMS dentro de un mismo resultado."
        ),
    references = listOf(
        ClinicalReferenceItem(
            source = "Organización Mundial de la Salud",
            citation = "WHO Child Growth Standards, 2006: peso, longitud/talla e IMC de 0 a 5 años.",
            useInApp = "Curvas LMS y percentiles para menores de 5 años."
        ),
        ClinicalReferenceItem(
            source = "Organización Mundial de la Salud",
            citation = "Growth reference data for 5–19 years, 2007.",
            useInApp = "Talla, peso e IMC para escolares y adolescentes."
        ),
        ClinicalReferenceItem(
            source = "Centers for Disease Control and Prevention",
            citation = "CDC Extended BMI-for-Age Growth Charts, publicadas en 2022 y revisadas en 2024.",
            useInApp = "Cotejo documental para IMC muy alto; no modifica los cálculos OMS."
        )
    ),
    limitations = listOf(
        "Un percentil aislado no establece diagnóstico ni sustituye la trayectoria longitudinal.",
        "La postura de medición, prematuridad, síndromes y enfermedades crónicas pueden requerir otras referencias.",
        "Los datos introducidos del niño no se guardan."
    )
)

private fun renalClinicalInfo() = ClinicalInfoContent(
    title = "Función renal · información y referencias",
    purpose = (
        "Estima función renal en adultos mediante una ecuación seleccionable y " +
            "clasifica la categoría G de KDIGO. Si se captura ACR, también " +
            "asigna A1–A3 y el nivel de riesgo combinado."
        ),
    method = (
        "Solo se muestra y calcula un método a la vez. CKD-EPI produce TFGe " +
            "indexada a 1.73 m². Cockcroft-Gault produce depuración de creatinina " +
            "en mL/min y la app muestra además una indexación orientativa por superficie corporal."
        ),
    references = listOf(
        ClinicalReferenceItem(
            source = "KDIGO",
            citation = "KDIGO 2024 Clinical Practice Guideline for the Evaluation and Management of Chronic Kidney Disease. Kidney Int. 2024;105(Suppl 4S):S117–S314.",
            useInApp = "Categorías G1–G5, A1–A3, matriz de riesgo y requisito de cronicidad."
        ),
        ClinicalReferenceItem(
            source = "Inker LA et al.",
            citation = "New Creatinine- and Cystatin C-Based Equations to Estimate GFR without Race. N Engl J Med. 2021;385:1737–1749.",
            useInApp = "Ecuaciones CKD-EPI 2021 de creatinina y creatinina-cistatina C."
        ),
        ClinicalReferenceItem(
            source = "NIDDK",
            citation = "eGFR Equations for Adults, revisión 2025.",
            useInApp = "Constantes, unidades y ecuación CKD-EPI 2012 por cistatina C."
        ),
        ClinicalReferenceItem(
            source = "Levey AS et al.",
            citation = "Using Standardized Serum Creatinine Values in the MDRD Study Equation. Ann Intern Med. 2006;145:247–254.",
            useInApp = "MDRD-4 IDMS como método de legado."
        ),
        ClinicalReferenceItem(
            source = "Cockcroft DW, Gault MH",
            citation = "Prediction of Creatinine Clearance from Serum Creatinine. Nephron. 1976;16:31–41.",
            useInApp = "Depuración estimada de creatinina."
        ),
        ClinicalReferenceItem(
            source = "Sociedad Española de Nefrología",
            citation = "Calculadora de función renal: Cockcroft-Gault, MDRD y CKD-EPI.",
            useInApp = "Cotejo funcional y selección de métodos."
        )
    ),
    limitations = listOf(
        "Las ecuaciones son estimaciones y pueden ser inexactas con masa muscular extrema, amputaciones, embarazo, enfermedad aguda o creatinina inestable.",
        "Un solo valor anormal no demuestra enfermedad renal crónica; debe documentarse duración mínima de 3 meses o evidencia equivalente.",
        "Cockcroft-Gault no es una TFGe y su valor para dosificación debe cotejarse con la ficha del medicamento.",
        "La matriz G-A describe riesgo pronóstico y no constituye por sí sola un diagnóstico ni una indicación terapéutica."
    )
)

private fun clinicalInfoForCalculator(
    calculatorTitle: String
): ClinicalInfoContent? = when (calculatorTitle) {
    "Gestograma" -> ClinicalInfoContent(
        title = "Gestograma · información y referencias",
        purpose = "Estima edad gestacional y fecha probable de parto a partir de la FUM.",
        method = "Edad gestacional = días transcurridos desde la FUM. FPP = FUM + 280 días.",
        references = listOf(
            ClinicalReferenceItem(
                source = "American College of Obstetricians and Gynecologists",
                citation = "Committee Opinion No. 700: Methods for Estimating the Due Date. Obstet Gynecol. 2017;129:e150–e154.",
                useInApp = "Datación obstétrica, FPP y criterios de comparación con ultrasonido."
            )
        ),
        limitations = listOf(
            "Una FUM incierta, ciclos irregulares o anticoncepción reciente reducen la confiabilidad.",
            "La FPP establecida clínicamente no debe modificarse sin valorar el ultrasonido y los antecedentes."
        )
    )

    "Edad gestacional por ultrasonido" -> ClinicalInfoContent(
        title = "Edad gestacional por ultrasonido · referencias",
        purpose = "Estima edad gestacional por LCC/CRL o biometría fetal según trimestre.",
        method = (
            "Primer trimestre: ecuación de LCC/CRL. Segundo y tercer trimestre: " +
                "ecuaciones Hadlock con DBP, CC, CA y LF disponibles."
            ),
        references = listOf(
            ClinicalReferenceItem(
                source = "ACOG",
                citation = "Committee Opinion No. 700: Methods for Estimating the Due Date, 2017.",
                useInApp = "Precisión esperada y umbrales orientativos de redatación."
            ),
            ClinicalReferenceItem(
                source = "Hadlock FP, Deter RL, Harrist RB, Park SK",
                citation = "Estimating fetal age: computer-assisted analysis of multiple fetal growth parameters. Radiology. 1984;152(2):497–501. DOI: 10.1148/radiology.152.2.6739822.",
                useInApp = "Ecuaciones biométricas del segundo y tercer trimestre."
            ),
            ClinicalReferenceItem(
                source = "British Medical Ultrasound Society",
                citation = "Fetal measurements guidance: datación por crown-rump length.",
                useInApp = "Conversión LCC/CRL a edad gestacional en el primer trimestre."
            )
        ),
        limitations = listOf(
            "El tercer trimestre ofrece la menor precisión para datación.",
            "La biometría puede reflejar alteraciones del crecimiento y no una edad gestacional distinta.",
            "El resultado no reemplaza la FPP obstétrica documentada."
        )
    )

    "Peso fetal estimado · Hadlock" -> ClinicalInfoContent(
        title = "Peso fetal estimado Hadlock · referencias",
        purpose = "Estima peso fetal usando DBP, CC, CA y LF.",
        method = (
            "Log10(peso) = 1.3596 − 0.00386(CA×LF) + 0.0064(CC) + " +
                "0.00061(DBP×CA) + 0.0424(CA) + 0.174(LF)."
            ),
        references = listOf(
            ClinicalReferenceItem(
                source = "Hadlock FP et al.",
                citation = "Estimation of fetal weight with the use of head, body, and femur measurements. Am J Obstet Gynecol. 1985.",
                useInApp = "Fórmula Hadlock de cuatro parámetros utilizada por la calculadora."
            ),
            ClinicalReferenceItem(
                source = "ISUOG",
                citation = "Practice Guidelines: ultrasound assessment of fetal biometry and growth. Ultrasound Obstet Gynecol. 2019.",
                useInApp = "Contexto técnico para biometría y evaluación del crecimiento."
            )
        ),
        limitations = listOf(
            "El peso fetal estimado tiene margen de error y depende de la calidad de las mediciones.",
            "La app calcula gramos, no asigna automáticamente percentil fetal."
        )
    )

    "Discordancia de peso gemelar" -> ClinicalInfoContent(
        title = "Discordancia gemelar · referencias",
        purpose = "Calcula la diferencia porcentual de peso fetal estimado entre gemelos.",
        method = "((peso mayor − peso menor) / peso mayor) × 100.",
        references = listOf(
            ClinicalReferenceItem(
                source = "ISUOG",
                citation = "Updated Practice Guidelines: role of ultrasound in twin pregnancy. Ultrasound Obstet Gynecol. 2025.",
                useInApp = "Fórmula de discordancia y contexto de vigilancia en embarazo gemelar."
            )
        ),
        limitations = listOf(
            "El porcentaje debe interpretarse junto con corionicidad, Doppler y percentiles individuales.",
            "Los umbrales de 20–25 % son orientativos y no constituyen una decisión aislada."
        )
    )

    "Relación cerebroplacentaria" -> ClinicalInfoContent(
        title = "Relación cerebroplacentaria · referencias",
        purpose = "Calcula CPR a partir de índices de pulsatilidad fetal.",
        method = "CPR = IP de arteria cerebral media / IP de arteria umbilical.",
        references = listOf(
            ClinicalReferenceItem(
                source = "ISUOG",
                citation = "Practice Guidelines: diagnosis and management of the small-for-gestational-age fetus and fetal growth restriction. 2020.",
                useInApp = "Contexto clínico de Doppler, ACM, arteria umbilical y CPR."
            ),
            ClinicalReferenceItem(
                source = "Medicina Fetal Barcelona",
                citation = "Calculadoras de Doppler y crecimiento fetal.",
                useInApp = "Referencia estructural del módulo obstétrico."
            )
        ),
        limitations = listOf(
            "Un corte fijo de CPR <1 es solamente orientativo.",
            "La interpretación clínica requiere percentiles por edad gestacional y técnica Doppler adecuada."
        )
    )

    "Doppler rápido" -> ClinicalInfoContent(
        title = "Doppler rápido · referencias",
        purpose = "Realiza operaciones aritméticas de apoyo para índices Doppler.",
        method = "IP uterina media = (IP derecha + IP izquierda) / 2. Índice TEI = (ICT + IRT) / ET.",
        references = listOf(
            ClinicalReferenceItem(
                source = "ISUOG",
                citation = "Practice Guidelines: use of Doppler velocimetry in obstetrics, actualización vigente.",
                useInApp = "Técnica, uso del índice de pulsatilidad y contexto Doppler."
            ),
            ClinicalReferenceItem(
                source = "Medicina Fetal Barcelona",
                citation = "Calculadoras de Doppler fetal y arterias uterinas.",
                useInApp = "Referencia funcional del apartado."
            )
        ),
        limitations = listOf(
            "La calculadora no asigna percentiles ni clasifica automáticamente una exploración.",
            "Los tiempos e índices deben proceder de un estudio técnicamente válido."
        )
    )

    "LHR · hernia diafragmática" -> ClinicalInfoContent(
        title = "LHR en hernia diafragmática · referencias",
        purpose = "Calcula lung-to-head ratio por el método de diámetros.",
        method = "Área pulmonar = diámetro longitudinal × diámetro perpendicular. LHR = área / circunferencia cefálica.",
        references = listOf(
            ClinicalReferenceItem(
                source = "ISUOG",
                citation = "Congenital diaphragmatic hernia: educational guidance and O/E LHR methodology.",
                useInApp = "Método de medición pulmonar y relación con circunferencia cefálica."
            ),
            ClinicalReferenceItem(
                source = "Medicina Fetal Barcelona",
                citation = "Calculadora de hernia diafragmática congénita.",
                useInApp = "Referencia estructural del cálculo LHR."
            )
        ),
        limitations = listOf(
            "La app calcula LHR observado, no el O/E LHR completo.",
            "El pronóstico requiere lateralidad, hígado, edad gestacional, técnica y evaluación especializada."
        )
    )

    else -> null
}

@Composable
private fun ClinicalInfoButton(
    info: ClinicalInfoContent,
    modifier: Modifier = Modifier
) {
    var showDialog by rememberSaveable(info.title) {
        mutableStateOf(false)
    }

    OutlinedButton(
        onClick = { showDialog = true },
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "Info y referencias",
            fontWeight = FontWeight.Bold
        )
    }

    if (showDialog) {
        ClinicalReferencesDialog(
            info = info,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun ClinicalReferencesDialog(
    info: ClinicalInfoContent,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val compactHeight =
                maxHeight < 520.dp
            val landscape =
                maxWidth > maxHeight

            Surface(
                shape = RoundedCornerShape(
                    if (compactHeight) 18.dp
                    else 24.dp
                ),
                color =
                    MaterialTheme.colorScheme
                        .surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(
                        max =
                            if (landscape) {
                                1050.dp
                            } else {
                                720.dp
                            }
                    )
                    .fillMaxWidth()
                    .fillMaxHeight(
                        if (compactHeight) {
                            0.98f
                        } else {
                            0.92f
                        }
                    )
                    .padding(
                        if (compactHeight) 5.dp
                        else 16.dp
                    )
            ) {
                Column(
                    Modifier.fillMaxSize()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer
                            )
                            .padding(
                                if (compactHeight) {
                                    9.dp
                                } else {
                                    16.dp
                                }
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape =
                                RoundedCornerShape(
                                    999.dp
                                ),
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.Info,
                                contentDescription =
                                    null,
                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimary,
                                modifier =
                                    Modifier.padding(
                                        if (
                                            compactHeight
                                        ) {
                                            6.dp
                                        } else {
                                            9.dp
                                        }
                                    )
                            )
                        }
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Text(
                                info.title,
                                style = if (
                                    compactHeight
                                ) {
                                    MaterialTheme
                                        .typography
                                        .titleMedium
                                } else {
                                    MaterialTheme
                                        .typography
                                        .titleLarge
                                },
                                fontWeight =
                                    FontWeight.Black,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimaryContainer,
                                maxLines = 1,
                                overflow =
                                    TextOverflow.Ellipsis
                            )
                            Text(
                                "Revisión: ${info.reviewedOn}",
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelSmall,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimaryContainer
                            )
                        }
                        IconButton(
                            onClick = onDismiss
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription =
                                    "Cerrar"
                            )
                        }
                    }

                    Column(
                        Modifier
                            .weight(1f)
                            .verticalScroll(
                                rememberScrollState()
                            )
                            .padding(
                                if (compactHeight) {
                                    10.dp
                                } else {
                                    16.dp
                                }
                            ),
                        verticalArrangement =
                            Arrangement.spacedBy(
                                if (compactHeight) {
                                    9.dp
                                } else {
                                    14.dp
                                }
                            )
                    ) {
                        ClinicalInfoSection(
                            title = "Qué hace",
                            text = info.purpose
                        )
                        ClinicalInfoSection(
                            title =
                                "Método utilizado",
                            text = info.method
                        )

                        Text(
                            "Referencias usadas",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.Black
                        )

                        info.references
                            .forEachIndexed {
                                    index,
                                    reference ->
                                OutlinedCard(
                                    colors =
                                        CardDefaults
                                            .outlinedCardColors(
                                                containerColor =
                                                    MaterialTheme
                                                        .colorScheme
                                                        .surfaceContainer
                                            )
                                ) {
                                    Column(
                                        Modifier.padding(
                                            12.dp
                                        ),
                                        verticalArrangement =
                                            Arrangement
                                                .spacedBy(
                                                    5.dp
                                                )
                                    ) {
                                        Text(
                                            "${index + 1}. ${reference.source}",
                                            fontWeight =
                                                FontWeight
                                                    .Black,
                                            color =
                                                MaterialTheme
                                                    .colorScheme
                                                    .primary
                                        )
                                        Text(
                                            reference
                                                .citation,
                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .bodySmall
                                        )
                                        Text(
                                            "Uso en la app: ${reference.useInApp}",
                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .labelSmall,
                                            color =
                                                MaterialTheme
                                                    .colorScheme
                                                    .onSurfaceVariant
                                        )
                                    }
                                }
                            }

                        Text(
                            "Limitaciones",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.Black
                        )

                        info.limitations.forEach {
                                limitation ->
                            Row(
                                horizontalArrangement =
                                    Arrangement
                                        .spacedBy(8.dp),
                                verticalAlignment =
                                    Alignment.Top
                            ) {
                                Text(
                                    "•",
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .tertiary,
                                    fontWeight =
                                        FontWeight.Black
                                )
                                Text(
                                    limitation,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall,
                                    modifier =
                                        Modifier.weight(
                                            1f
                                        )
                                )
                            }
                        }

                        Surface(
                            shape =
                                RoundedCornerShape(
                                    14.dp
                                ),
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .errorContainer
                                    .copy(alpha = 0.55f),
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Herramienta de apoyo. No sustituye guías vigentes, " +
                                    "juicio clínico, interpretación especializada ni " +
                                    "valoración individual.",
                                modifier =
                                    Modifier.padding(
                                        12.dp
                                    ),
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelSmall,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onErrorContainer
                            )
                        }
                    }

                    HorizontalDivider()
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                if (compactHeight) {
                                    8.dp
                                } else {
                                    14.dp
                                }
                            )
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

@Composable
private fun ClinicalInfoSection(
    title: String,
    text: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black
        )
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private enum class MainSection(
    val label: String,
    val description: String,
    val icon: ImageVector
) {
    MEDICATIONS(
        label = "Medicamentos",
        description = "Consultar y gestionar medicamentos locales",
        icon = Icons.Default.Medication
    ),
    PERCENTILES(
        label = "Percentiles",
        description = "Tablas y curvas de crecimiento",
        icon = Icons.Default.Insights
    ),
    OBSTETRICS(
        label = "Gineco-OB",
        description = "Herramientas para salud materna",
        icon = Icons.Default.PregnantWoman
    ),
    RENAL(
        label = "Renal",
        description = "Cálculos y fórmulas renales",
        icon = Icons.Default.WaterDrop
    ),
    UPDATES(
        label = "Actualizaciones",
        description = "Buscar e instalar una versión nueva",
        icon = Icons.Default.Download
    )
}

@Composable
fun CalculatorApp(viewModel: MainViewModel) {
    val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()

    CalculatorTheme(darkTheme) {
        ApplicationShell(
            viewModel = viewModel,
            darkTheme = darkTheme
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApplicationShell(
    viewModel: MainViewModel,
    darkTheme: Boolean
) {
    var section by rememberSaveable {
        mutableStateOf<MainSection?>(null)
    }
    var menuExpanded by rememberSaveable {
        mutableStateOf(true)
    }

    val snackbarHost = remember {
        SnackbarHostState()
    }

    LaunchedEffect(Unit) {
        viewModel.messages.collectLatest {
            snackbarHost.showSnackbar(it)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Herramientas clínicas",
                            maxLines = 1,
                            overflow =
                                TextOverflow.Ellipsis
                        )
                        Text(
                            "Aplicación nativa · datos locales",
                            style =
                                MaterialTheme.typography.labelSmall,
                            color =
                                MaterialTheme.colorScheme
                                    .onPrimaryContainer
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = viewModel::toggleTheme
                    ) {
                        Icon(
                            imageVector = if (darkTheme) {
                                Icons.Default.DarkMode
                            } else {
                                Icons.Default.LightMode
                            },
                            contentDescription = if (darkTheme) {
                                "Cambiar a tema claro"
                            } else {
                                "Cambiar a tema oscuro"
                            }
                        )
                    }
                },
                colors =
                    TopAppBarDefaults
                        .centerAlignedTopAppBarColors(
                            containerColor =
                                MaterialTheme.colorScheme
                                    .primaryContainer,
                            titleContentColor =
                                MaterialTheme.colorScheme
                                    .onPrimaryContainer,
                            actionIconContentColor =
                                MaterialTheme.colorScheme
                                    .onPrimaryContainer
                        )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHost)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(
                visible = section != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 68.dp)
                ) {
                    when (section) {
                        MainSection.MEDICATIONS ->
                            MedicationCalculatorScreen(
                                viewModel = viewModel,
                                modifier =
                                    Modifier.fillMaxSize()
                            )

                        MainSection.PERCENTILES ->
                            PercentilesScreen(
                                modifier =
                                    Modifier.fillMaxSize()
                            )

                        MainSection.OBSTETRICS ->
                            ObstetricsScreen(
                                modifier =
                                    Modifier.fillMaxSize()
                            )

                        MainSection.RENAL ->
                            RenalFunctionScreen(
                                modifier =
                                    Modifier.fillMaxSize()
                            )

                        MainSection.UPDATES ->
                            AppUpdateScreen(
                                modifier =
                                    Modifier.fillMaxSize()
                            )

                        null -> Unit
                    }
                }
            }

            AnimatedVisibility(
                visible =
                    menuExpanded && section != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(
                                alpha = 0.46f
                            )
                        )
                        .clickable {
                            menuExpanded = false
                        }
                )
            }

            SectionNavigationMenu(
                expanded = menuExpanded,
                selectedSection = section,
                onToggle = {
                    if (section != null) {
                        menuExpanded = !menuExpanded
                    }
                },
                onSectionSelected = {
                    section = it
                    menuExpanded = false
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SectionNavigationMenu(
    expanded: Boolean,
    selectedSection: MainSection?,
    onToggle: () -> Unit,
    onSectionSelected: (MainSection) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        val compactHeight = maxHeight < 520.dp
        val wideCompactLayout =
            compactHeight && maxWidth >= 720.dp

        val expandedButtonWidth =
            if (compactHeight) 164.dp else 188.dp
        val compactButtonWidth =
            if (compactHeight) 46.dp else 52.dp
        val buttonHeight =
            if (compactHeight) 44.dp else 50.dp

        val panelMaxHeight = (
            maxHeight -
                buttonHeight -
                if (compactHeight) 22.dp else 30.dp
            ).coerceAtLeast(132.dp)

        val buttonWidth by animateDpAsState(
            targetValue = if (expanded) {
                expandedButtonWidth
            } else {
                compactButtonWidth
            },
            label = "sectionMenuWidth"
        )

        val targetOffset = if (expanded) {
            (
                (maxWidth - expandedButtonWidth) /
                    2
                ).coerceAtLeast(8.dp)
        } else {
            if (compactHeight) 8.dp else 16.dp
        }

        val buttonOffset by animateDpAsState(
            targetValue = targetOffset,
            label = "sectionMenuOffset"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            verticalArrangement =
                Arrangement.spacedBy(
                    if (compactHeight) 5.dp else 10.dp
                )
        ) {
            Spacer(
                modifier = Modifier.height(
                    if (compactHeight) 3.dp else 8.dp
                )
            )

            Surface(
                onClick = onToggle,
                modifier = Modifier
                    .offset(x = buttonOffset)
                    .width(buttonWidth)
                    .height(buttonHeight),
                shape = RoundedCornerShape(
                    if (compactHeight) 15.dp else 18.dp
                ),
                color =
                    MaterialTheme.colorScheme
                        .surfaceContainerHigh,
                contentColor =
                    MaterialTheme.colorScheme.primary,
                border =
                    androidx.compose.foundation
                        .BorderStroke(
                            width = 1.4.dp,
                            color =
                                MaterialTheme.colorScheme
                                    .primary.copy(
                                        alpha = 0.82f
                                    )
                        ),
                tonalElevation = if (expanded) {
                    5.dp
                } else {
                    2.dp
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal =
                                if (compactHeight) 10.dp
                                else 13.dp
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.Center
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.Menu,
                        contentDescription =
                            "Menú de apartados",
                        modifier = Modifier.size(
                            if (compactHeight) 24.dp
                            else 27.dp
                        )
                    )

                    AnimatedVisibility(
                        visible = expanded,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row {
                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )
                            Text(
                                "Menú",
                                style =
                                    MaterialTheme.typography
                                        .titleMedium,
                                fontWeight =
                                    FontWeight.Black
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter =
                    fadeIn() +
                        expandVertically(
                            expandFrom =
                                Alignment.Top
                        ),
                exit =
                    fadeOut() +
                        shrinkVertically(
                            shrinkTowards =
                                Alignment.Top
                        )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal =
                                if (compactHeight) 6.dp
                                else 16.dp,
                            vertical = 2.dp
                        ),
                    contentAlignment =
                        Alignment.TopCenter
                ) {
                    SectionMenuPanel(
                        selectedSection =
                            selectedSection,
                        onSectionSelected =
                            onSectionSelected,
                        maxPanelHeight =
                            panelMaxHeight,
                        compactHeight =
                            compactHeight,
                        wideCompactLayout =
                            wideCompactLayout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 1180.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionMenuPanel(
    selectedSection: MainSection?,
    onSectionSelected: (MainSection) -> Unit,
    maxPanelHeight: Dp,
    compactHeight: Boolean,
    wideCompactLayout: Boolean,
    modifier: Modifier = Modifier
) {
    val columnCount = if (wideCompactLayout) {
        4
    } else {
        2
    }

    Surface(
        modifier = modifier.heightIn(
            max = maxPanelHeight
        ),
        shape = RoundedCornerShape(
            if (compactHeight) 20.dp else 26.dp
        ),
        color =
            MaterialTheme.colorScheme
                .surfaceContainerHigh,
        border =
            androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color =
                    MaterialTheme.colorScheme
                        .outlineVariant
            ),
        tonalElevation = 8.dp,
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    if (compactHeight) 9.dp
                    else 14.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    if (compactHeight) 8.dp
                    else 12.dp
                )
        ) {
            Text(
                if (selectedSection == null) {
                    "Selecciona un apartado para comenzar"
                } else {
                    "Cambiar de apartado"
                },
                style = if (compactHeight) {
                    MaterialTheme.typography.titleSmall
                } else {
                    MaterialTheme.typography.titleMedium
                },
                fontWeight = FontWeight.Black,
                color =
                    MaterialTheme.colorScheme
                        .onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 4.dp,
                        vertical = 2.dp
                    ),
                textAlign = TextAlign.Center
            )

            MainSection.entries
                .chunked(columnCount)
                .forEach { rowSections ->
                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                if (compactHeight) 7.dp
                                else 12.dp
                            )
                    ) {
                        rowSections.forEach {
                            menuSection ->
                            SectionMenuTile(
                                section = menuSection,
                                selected =
                                    selectedSection ==
                                        menuSection,
                                compact =
                                    compactHeight,
                                onClick = {
                                    onSectionSelected(
                                        menuSection
                                    )
                                },
                                modifier =
                                    Modifier.weight(1f)
                            )
                        }

                        repeat(
                            columnCount -
                                rowSections.size
                        ) {
                            Spacer(
                                modifier =
                                    Modifier.weight(1f)
                            )
                        }
                    }
                }
        }
    }
}

@Composable
private fun SectionMenuTile(
    section: MainSection,
    selected: Boolean,
    compact: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme
            .primaryContainer
    } else {
        MaterialTheme.colorScheme
            .surfaceContainer
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme
            .onPrimaryContainer
    } else {
        MaterialTheme.colorScheme
            .onSurface
    }

    Surface(
        onClick = onClick,
        modifier = modifier.height(
            if (compact) 112.dp else 156.dp
        ),
        shape = RoundedCornerShape(
            if (compact) 17.dp else 22.dp
        ),
        color = containerColor,
        contentColor = contentColor,
        border =
            androidx.compose.foundation.BorderStroke(
                width = if (selected) {
                    1.8.dp
                } else {
                    0.8.dp
                },
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme
                        .outlineVariant
                }
            ),
        tonalElevation = if (selected) {
            6.dp
        } else {
            1.dp
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    if (compact) 8.dp else 13.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.SpaceBetween
        ) {
            Surface(
                shape =
                    RoundedCornerShape(999.dp),
                color =
                    MaterialTheme.colorScheme
                        .secondaryContainer,
                contentColor =
                    MaterialTheme.colorScheme
                        .onSecondaryContainer
            ) {
                Icon(
                    imageVector = section.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(
                            if (compact) 6.dp
                            else 10.dp
                        )
                        .size(
                            if (compact) 23.dp
                            else 31.dp
                        )
                )
            }

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    section.label,
                    style = if (compact) {
                        MaterialTheme.typography
                            .titleSmall
                    } else {
                        MaterialTheme.typography
                            .titleMedium
                    },
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow =
                        TextOverflow.Ellipsis
                )
                Text(
                    section.description,
                    style =
                        MaterialTheme.typography
                            .labelSmall,
                    color =
                        contentColor.copy(
                            alpha = 0.76f
                        ),
                    textAlign = TextAlign.Center,
                    maxLines =
                        if (compact) 1 else 2,
                    overflow =
                        TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector =
                    Icons.Default.ChevronRight,
                contentDescription = null,
                tint =
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(
                    if (compact) 16.dp else 20.dp
                )
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
            if (filtered.isEmpty()) {
                emptyList()
            } else {
                filtered.filter { it.type == selectedTab }
            }
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
                item {
                    val info = remember { medicationClinicalInfo() }
                    ClinicalInfoButton(info = info)
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
        val families by viewModel.availableFamilies.collectAsStateWithLifecycle()
        val subgroups by viewModel.availableSubgroups.collectAsStateWithLifecycle()
        val frequencies by viewModel.availableFrequencies.collectAsStateWithLifecycle()
        val specialties by viewModel.availableSpecialties.collectAsStateWithLifecycle()

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
        val families by viewModel.availableFamilies.collectAsStateWithLifecycle()
        val subgroups by viewModel.availableSubgroups.collectAsStateWithLifecycle()
        val specialties by viewModel.availableSpecialties.collectAsStateWithLifecycle()

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
    MedicationColumn("Notas", 360.dp),
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
    val horizontalScrollState =
        rememberScrollState()
    val naturalWidth =
        medicationColumns.fold(0.dp) {
                total,
                column ->
            total + column.width
        }
    val tableWidth = if (
        availableWidth > naturalWidth
    ) {
        availableWidth
    } else {
        naturalWidth
    }

    OutlinedCard(
        Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                Column(
                    Modifier.weight(1f)
                ) {
                    Text(
                        "Tabla de medicamentos",
                        style =
                            MaterialTheme.typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.Bold
                    )
                    Text(
                        "Los encabezados permanecen visibles mientras bajas. La tabla muestra solo datos clínicos; familia, subgrupo y especialidades siguen disponibles en filtros.",
                        style =
                            MaterialTheme.typography
                                .labelSmall,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }
                Text(
                    "${records.size} registros",
                    style =
                        MaterialTheme.typography
                            .labelMedium
                )
            }

            HorizontalDivider()

            val maximumVisibleRows = if (
                availableWidth < 700.dp
            ) {
                3
            } else {
                5
            }
            val visibleRows = min(
                records.size,
                maximumVisibleRows
            ).coerceAtLeast(1)
            val bodyViewportHeight =
                112.dp * visibleRows

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        horizontalScrollState
                    )
            ) {
                Column(
                    modifier =
                        Modifier.width(tableWidth)
                ) {
                    // El encabezado queda fuera del
                    // desplazamiento vertical del cuerpo.
                    MedicationHeaderRow()

                    LazyColumn(
                        modifier = Modifier
                            .width(tableWidth)
                            .height(
                                bodyViewportHeight
                            ),
                        userScrollEnabled =
                            records.size >
                                visibleRows
                    ) {
                        itemsIndexed(
                            items = records,
                            key = {
                                    _,
                                    record ->
                                record.id
                            },
                            contentType = { _, _ ->
                                "medication-row"
                            }
                        ) {
                                index,
                                record ->
                            MedicationDataRow(
                                record = record,
                                weight = if (
                                    record.type ==
                                        MedicationType.ADULT
                                ) {
                                    adultWeight
                                } else {
                                    pediatricWeight
                                },
                                alternate =
                                    index % 2 == 1,
                                onEdit = {
                                    onEdit(record)
                                },
                                onDelete = {
                                    onDelete(record)
                                }
                            )
                        }
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


private fun formatDoseValue(
    value: Double
): String = java.math.BigDecimal
    .valueOf(value)
    .stripTrailingZeros()
    .toPlainString()

private fun nextInteractiveDose(
    current: Double,
    minimum: Double,
    maximum: Double,
    step: Double,
    direction: Int
): Double {
    val normalizedStep = step.coerceAtLeast(0.000001)
    val raw = current +
        normalizedStep * direction
    val stepIndex = (
        (raw - minimum) /
            normalizedStep
        ).roundToInt()
    val aligned = minimum +
        stepIndex * normalizedStep

    return java.math.BigDecimal
        .valueOf(
            aligned.coerceIn(
                minimum,
                maximum
            )
        )
        .setScale(
            6,
            java.math.RoundingMode.HALF_UP
        )
        .stripTrailingZeros()
        .toDouble()
}

@Composable
private fun InteractiveDoseWheel(
    record: MedicationRecord,
    selectedDose: Double,
    onSelectedDose: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val minimum = record.dosePerKgMin
        ?: selectedDose
    val maximum = record.dosePerKgMax
        ?: selectedDose
    val step = record.dosePerKgStep
        ?.takeIf { it > 0.0 }
        ?: 0.1

    fun changeDose(direction: Int) {
        onSelectedDose(
            nextInteractiveDose(
                current = selectedDose,
                minimum = minimum,
                maximum = maximum,
                step = step,
                direction = direction
            )
        )
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(
            alpha = 0.62f
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(
                alpha = 0.55f
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(
                minimum,
                maximum,
                step,
                selectedDose
            ) {
                var accumulatedVerticalDrag = 0f

                detectDragGestures(
                    onDragStart = {
                        accumulatedVerticalDrag = 0f
                    },
                    onDragEnd = {
                        accumulatedVerticalDrag = 0f
                    },
                    onDragCancel = {
                        accumulatedVerticalDrag = 0f
                    }
                ) { pointerChange, dragAmount ->
                    accumulatedVerticalDrag +=
                        dragAmount.y

                    when {
                        accumulatedVerticalDrag <= -24f -> {
                            changeDose(1)
                            accumulatedVerticalDrag = 0f
                        }
                        accumulatedVerticalDrag >= 24f -> {
                            changeDose(-1)
                            accumulatedVerticalDrag = 0f
                        }
                    }
                    pointerChange.consume()
                }
            }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 7.dp,
                    vertical = 2.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { changeDose(1) },
                enabled = selectedDose < maximum,
                modifier = Modifier.size(21.dp)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription =
                        "Aumentar dosis",
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                formatDoseValue(
                    nextInteractiveDose(
                        selectedDose,
                        minimum,
                        maximum,
                        step,
                        -1
                    )
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = if (
                        selectedDose > minimum
                    ) {
                        0.70f
                    } else {
                        0.30f
                    }
                )
            )

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    "${formatDoseValue(selectedDose)} " +
                        "${record.doseUnit}/kg",
                    modifier = Modifier.padding(
                        horizontal = 9.dp,
                        vertical = 3.dp
                    ),
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Text(
                formatDoseValue(
                    nextInteractiveDose(
                        selectedDose,
                        minimum,
                        maximum,
                        step,
                        1
                    )
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = if (
                        selectedDose < maximum
                    ) {
                        0.70f
                    } else {
                        0.30f
                    }
                )
            )

            IconButton(
                onClick = { changeDose(-1) },
                enabled = selectedDose > minimum,
                modifier = Modifier.size(21.dp)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription =
                        "Disminuir dosis",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun MedicationRecord.formattedDoseForTable(): String {
    val cleanDose = dose.trim()
    val cleanUnit = doseUnit.trim()

    if (
        type != MedicationType.ADULT ||
        isSpecialAdult
    ) {
        val value = dosePerKg
            ?.let(::formatDoseValue)
            .orEmpty()

        return when {
            value.isBlank() &&
                cleanUnit.isBlank() -> "—"

            cleanUnit.isBlank() ->
                "$value/kg"

            else ->
                "$value $cleanUnit/kg"
        }
    }

    if (cleanDose.isBlank()) {
        return cleanUnit.ifBlank {
            "—"
        }
    }

    if (cleanUnit.isBlank()) {
        return cleanDose
    }

    val unitPattern = Regex(
        pattern = (
            "(^|[^\\p{L}\\p{N}])" +
                Regex.escape(cleanUnit) +
                "(?:/kg)?" +
                "($|[^\\p{L}\\p{N}])"
            ),
        option = RegexOption.IGNORE_CASE
    )

    if (unitPattern.containsMatchIn(cleanDose)) {
        return cleanDose
    }

    val isSimpleNumericDose = Regex(
        pattern = (
            "^[0-9]+(?:[.,][0-9]+)?" +
                "(?:\\s*[-–]\\s*" +
                "[0-9]+(?:[.,][0-9]+)?)?$"
            )
    ).matches(cleanDose)

    return if (isSimpleNumericDose) {
        "$cleanDose $cleanUnit"
    } else {
        "$cleanDose · Unidad: $cleanUnit"
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
    var menuOpen by remember {
        mutableStateOf(false)
    }

    val initialInteractiveDose = remember(
        record.id,
        record.updatedAt
    ) {
        record.interactiveDoseStart()
            ?: record.dosePerKg
            ?: 0.0
    }
    var selectedInteractiveDose by rememberSaveable(
        record.id,
        record.updatedAt
    ) {
        mutableStateOf(
            initialInteractiveDose
        )
    }

    val rowColor = if (alternate) {
        MaterialTheme.colorScheme
            .surfaceVariant.copy(
                alpha = 0.34f
            )
    } else {
        MaterialTheme.colorScheme.surface
    }

    val selectedDose = if (
        record.hasValidInteractiveRange()
    ) {
        selectedInteractiveDose
    } else {
        record.dosePerKg
    }

    val doseText =
        record.formattedDoseForTable()

    Row(
        modifier = Modifier
            .background(rowColor)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    menuOpen = true
                }
            )
            .height(112.dp)
    ) {
        TableCell(
            medicationColumns[0].width
        ) {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(2.dp)
            ) {
                OverflowAwareTableText(
                    text = record.name,
                    dialogTitle =
                        "Medicamento",
                    maxLines = 2,
                    fontWeight =
                        FontWeight.Bold
                )

                if (record.isSpecialAdult) {
                    Text(
                        "Especial",
                        style =
                            MaterialTheme.typography
                                .labelSmall,
                        color =
                            MaterialTheme.colorScheme
                                .tertiary
                    )
                }

                if (record.isInteractiveDose) {
                    Text(
                        "Dosis interactiva",
                        style =
                            MaterialTheme.typography
                                .labelSmall,
                        color =
                            MaterialTheme.colorScheme
                                .primary
                    )
                }
            }
        }

        TableTextCell(
            text = record.presentation,
            width =
                medicationColumns[1].width,
            dialogTitle =
                "${record.name} · Presentación"
        )

        TableCell(
            medicationColumns[2].width,
            contentAlignment =
                Alignment.Center
        ) {
            if (
                record.hasValidInteractiveRange()
            ) {
                InteractiveDoseWheel(
                    record = record,
                    selectedDose =
                        selectedInteractiveDose,
                    onSelectedDose = {
                        selectedInteractiveDose = it
                    },
                    modifier = Modifier.padding(
                        horizontal = 7.dp
                    )
                )
            } else {
                OverflowAwareTableText(
                    text = doseText,
                    dialogTitle =
                        "${record.name} · Dosis",
                    maxLines = 3
                )
            }
        }

        TableTextCell(
            text = record.calculatedDose(
                weight = weight,
                selectedDosePerKg =
                    selectedDose
            ),
            width =
                medicationColumns[3].width,
            dialogTitle =
                "${record.name} · Dosis calculada",
            fontWeight =
                FontWeight.SemiBold
        )

        TableTextCell(
            text = record.frequencyPerDay,
            width =
                medicationColumns[4].width,
            dialogTitle =
                "${record.name} · Uso por día"
        )

        TableTextCell(
            text =
                record.durationDays.toString(),
            width =
                medicationColumns[5].width,
            dialogTitle =
                "${record.name} · Días",
            maxLines = 2
        )

        TableTextCell(
            text = record.notes.ifBlank {
                "—"
            },
            width =
                medicationColumns[6].width,
            dialogTitle =
                "${record.name} · Notas",
            maxLines = 2
        )

        TableCell(
            medicationColumns[7].width,
            contentAlignment =
                Alignment.Center
        ) {
            Box {
                IconButton(
                    onClick = {
                        menuOpen = true
                    }
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription =
                            "Opciones"
                    )
                }

                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = {
                        menuOpen = false
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Editar fármaco"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                null
                            )
                        },
                        onClick = {
                            menuOpen = false
                            onEdit()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                "Eliminar fármaco"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                null
                            )
                        },
                        onClick = {
                            menuOpen = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }

    HorizontalDivider()
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
            .border(
                width = 0.5.dp,
                color = MaterialTheme
                    .colorScheme
                    .outlineVariant
                    .copy(alpha = 0.72f)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

@Composable
private fun OverflowAwareTableText(
    text: String,
    dialogTitle: String,
    maxLines: Int = 3,
    fontWeight: FontWeight? = null
) {
    val displayText = text.ifBlank {
        "—"
    }
    var hasOverflow by remember(
        displayText,
        maxLines
    ) {
        mutableStateOf(false)
    }
    var showFullText by remember(
        displayText,
        dialogTitle
    ) {
        mutableStateOf(false)
    }

    Column(
        verticalArrangement =
            Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = displayText,
            maxLines = maxLines,
            overflow =
                TextOverflow.Ellipsis,
            style =
                MaterialTheme.typography
                    .bodySmall,
            fontWeight = fontWeight,
            onTextLayout = {
                layoutResult ->
                hasOverflow =
                    layoutResult
                        .hasVisualOverflow
            }
        )

        if (
            hasOverflow &&
            displayText != "—"
        ) {
            Text(
                text = "+ Ver más",
                style =
                    MaterialTheme.typography
                        .labelSmall,
                fontWeight =
                    FontWeight.Bold,
                color =
                    MaterialTheme.colorScheme
                        .primary,
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(6.dp)
                    )
                    .clickable {
                        showFullText = true
                    }
                    .padding(
                        horizontal = 3.dp,
                        vertical = 2.dp
                    )
            )
        }
    }

    if (showFullText) {
        TableCellDetailsDialog(
            title = dialogTitle,
            text = displayText,
            onDismiss = {
                showFullText = false
            }
        )
    }
}

@Composable
private fun TableCellDetailsDialog(
    title: String,
    text: String,
    onDismiss: () -> Unit
) {
    val configuration =
        LocalConfiguration.current
    val maximumTextHeight = (
        configuration.screenHeightDp.dp *
            0.55f
        ).coerceAtLeast(130.dp)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                fontWeight =
                    FontWeight.Black
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        max = maximumTextHeight
                    )
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                Text(
                    text = text,
                    style =
                        MaterialTheme.typography
                            .bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun TableTextCell(
    text: String,
    width: Dp,
    dialogTitle: String,
    maxLines: Int = 3,
    fontWeight: FontWeight? = null
) {
    TableCell(width) {
        OverflowAwareTableText(
            text = text,
            dialogTitle = dialogTitle,
            maxLines = maxLines,
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(10.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GestationalAgeCalculator() {
    var lmp by rememberSaveable {
        mutableStateOf(
            LocalDate.now()
                .minusWeeks(20)
                .toString()
        )
    }
    var reference by rememberSaveable {
        mutableStateOf(
            LocalDate.now().toString()
        )
    }

    val configuration =
        LocalConfiguration.current
    val screenIsLandscape =
        configuration.screenWidthDp >
            configuration.screenHeightDp
    val compactHeight =
        configuration.screenHeightDp < 560

    val lmpDate = remember(lmp) {
        runCatching {
            LocalDate.parse(lmp)
        }.getOrElse {
            LocalDate.now().minusWeeks(20)
        }
    }
    val referenceDate = remember(reference) {
        runCatching {
            LocalDate.parse(reference)
        }.getOrElse {
            LocalDate.now()
        }
    }

    val totalDays = ChronoUnit.DAYS
        .between(lmpDate, referenceDate)
        .coerceAtLeast(0)
    val weeks = totalDays / 7
    val days = totalDays % 7
    val dueDate = lmpDate.plusDays(280)

    CalculatorCard(
        title = "Gestograma",
        note = if (
            screenIsLandscape &&
            compactHeight
        ) {
            "Ajusta la FUM con la rueda, el campo o el calendario."
        } else {
            (
                "Rueda obstétrica interactiva inspirada en el gestograma clásico. " +
                    "Arrastra la rueda para ajustar la FUM, escribe la fecha o " +
                    "selecciónala en el calendario."
                )
        }
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val sideBySide = (
                screenIsLandscape &&
                    maxWidth >= 560.dp
                ) || maxWidth >= 900.dp

            if (sideBySide) {
                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            if (compactHeight) {
                                8.dp
                            } else {
                                18.dp
                            }
                        ),
                    verticalAlignment =
                        Alignment.Top
                ) {
                    GestogramWheelPanel(
                        lmpDate = lmpDate,
                        referenceDate =
                            referenceDate,
                        dueDate = dueDate,
                        gestationalDays =
                            totalDays.toInt(),
                        onLmpDateChange = {
                            lmp = it.toString()
                        },
                        compactHeight =
                            compactHeight,
                        landscape = true,
                        modifier = Modifier.weight(
                            if (compactHeight) {
                                0.92f
                            } else {
                                1.08f
                            }
                        )
                    )

                    GestogramControls(
                        lmpDate = lmpDate,
                        referenceDate =
                            referenceDate,
                        dueDate = dueDate,
                        weeks = weeks,
                        days = days,
                        onLmpDate = {
                            lmp = it.toString()
                        },
                        onReferenceDate = {
                            reference =
                                it.toString()
                        },
                        compactHeight =
                            compactHeight,
                        modifier = Modifier.weight(
                            if (compactHeight) {
                                1.08f
                            } else {
                                0.92f
                            }
                        )
                    )
                }
            } else {
                Column(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            if (compactHeight) {
                                8.dp
                            } else {
                                14.dp
                            }
                        )
                ) {
                    GestogramWheelPanel(
                        lmpDate = lmpDate,
                        referenceDate =
                            referenceDate,
                        dueDate = dueDate,
                        gestationalDays =
                            totalDays.toInt(),
                        onLmpDateChange = {
                            lmp = it.toString()
                        },
                        compactHeight =
                            compactHeight,
                        landscape =
                            screenIsLandscape,
                        modifier =
                            Modifier.fillMaxWidth()
                    )

                    GestogramControls(
                        lmpDate = lmpDate,
                        referenceDate =
                            referenceDate,
                        dueDate = dueDate,
                        weeks = weeks,
                        days = days,
                        onLmpDate = {
                            lmp = it.toString()
                        },
                        onReferenceDate = {
                            reference =
                                it.toString()
                        },
                        compactHeight =
                            compactHeight,
                        modifier =
                            Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun GestogramWheelPanel(
    lmpDate: LocalDate,
    referenceDate: LocalDate,
    dueDate: LocalDate,
    gestationalDays: Int,
    onLmpDateChange: (LocalDate) -> Unit,
    compactHeight: Boolean = false,
    landscape: Boolean = false,
    modifier: Modifier = Modifier
) {
    val configuration =
        LocalConfiguration.current

    OutlinedCard(
        modifier = modifier,
        colors =
            CardDefaults.outlinedCardColors(
                containerColor =
                    MaterialTheme.colorScheme
                        .surfaceContainer
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    if (compactHeight) {
                        8.dp
                    } else {
                        12.dp
                    }
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    if (compactHeight) {
                        6.dp
                    } else {
                        9.dp
                    }
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Rueda gestacional",
                        style = if (
                            compactHeight
                        ) {
                            MaterialTheme.typography
                                .titleSmall
                        } else {
                            MaterialTheme.typography
                                .titleMedium
                        },
                        fontWeight =
                            FontWeight.Black,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )
                    Text(
                        if (compactHeight) {
                            "Gira para modificar la FUM"
                        } else {
                            "Desliza en círculo para modificar la FUM"
                        },
                        style =
                            MaterialTheme.typography
                                .labelSmall,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape =
                        RoundedCornerShape(999.dp),
                    color =
                        MaterialTheme.colorScheme
                            .primaryContainer
                ) {
                    Text(
                        (
                            "${gestationalDays.coerceAtLeast(0) / 7} + " +
                                "${gestationalDays.coerceAtLeast(0) % 7}"
                            ),
                        modifier = Modifier.padding(
                            horizontal =
                                if (compactHeight) {
                                    8.dp
                                } else {
                                    11.dp
                                },
                            vertical =
                                if (compactHeight) {
                                    4.dp
                                } else {
                                    6.dp
                                }
                        ),
                        color =
                            MaterialTheme.colorScheme
                                .onPrimaryContainer,
                        style =
                            MaterialTheme.typography
                                .labelLarge,
                        fontWeight =
                            FontWeight.Black
                    )
                }
            }

            BoxWithConstraints(
                modifier =
                    Modifier.fillMaxWidth(),
                contentAlignment =
                    Alignment.Center
            ) {
                val screenHeight =
                    configuration.screenHeightDp.dp

                val heightBudget = if (
                    landscape
                ) {
                    (
                        screenHeight -
                            if (compactHeight) {
                                178.dp
                            } else {
                                238.dp
                            }
                        ).coerceAtLeast(
                        if (compactHeight) {
                            138.dp
                        } else {
                            190.dp
                        }
                    )
                } else {
                    maxWidth
                }

                val availableSize = minOf(
                    maxWidth,
                    heightBudget
                )

                val maximumSize = when {
                    landscape &&
                        compactHeight -> 270.dp

                    landscape -> 430.dp

                    else -> 560.dp
                }

                val minimumSize = when {
                    landscape &&
                        compactHeight -> 138.dp

                    landscape -> 190.dp

                    else -> 220.dp
                }

                val wheelSize = availableSize
                    .coerceAtMost(maximumSize)
                    .coerceAtLeast(
                        minimumSize.coerceAtMost(
                            availableSize
                        )
                    )

                GestogramWheel(
                    lmpDate = lmpDate,
                    referenceDate =
                        referenceDate,
                    dueDate = dueDate,
                    gestationalDays =
                        gestationalDays,
                    onLmpDateChange =
                        onLmpDateChange,
                    modifier =
                        Modifier.size(wheelSize)
                )
            }

            FlowRow(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        if (compactHeight) {
                            5.dp
                        } else {
                            7.dp
                        }
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(
                        if (compactHeight) {
                            4.dp
                        } else {
                            7.dp
                        }
                    )
            ) {
                AssistChip(
                    onClick = {
                        onLmpDateChange(
                            lmpDate.minusDays(7)
                        )
                    },
                    label = {
                        Text(
                            if (compactHeight) {
                                "−1 sem"
                            } else {
                                "− 1 semana"
                            }
                        )
                    }
                )
                AssistChip(
                    onClick = {
                        val candidate =
                            lmpDate.plusDays(7)

                        onLmpDateChange(
                            if (
                                candidate.isAfter(
                                    referenceDate
                                )
                            ) {
                                referenceDate
                            } else {
                                candidate
                            }
                        )
                    },
                    label = {
                        Text(
                            if (compactHeight) {
                                "+1 sem"
                            } else {
                                "+ 1 semana"
                            }
                        )
                    }
                )
                AssistChip(
                    onClick = {
                        onLmpDateChange(
                            referenceDate
                                .minusWeeks(20)
                        )
                    },
                    label = {
                        Text(
                            if (compactHeight) {
                                "20 semanas"
                            } else {
                                "Centrar en 20 semanas"
                            }
                        )
                    }
                )
            }
        }
    }
}


@Composable
private fun rememberCombinationDialTick(): () -> Unit {
    val context = LocalContext.current

    val soundPool = remember {
        val attributes = AudioAttributes.Builder()
            .setUsage(
                AudioAttributes.USAGE_MEDIA
            )
            .setContentType(
                AudioAttributes.CONTENT_TYPE_SONIFICATION
            )
            .build()

        SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(attributes)
            .build()
    }

    var soundId by remember {
        mutableStateOf(0)
    }
    var soundLoaded by remember {
        mutableStateOf(false)
    }
    var pendingPlay by remember {
        mutableStateOf(false)
    }
    var lastPlayAt by remember {
        mutableStateOf(0L)
    }

    DisposableEffect(
        soundPool,
        context
    ) {
        soundPool.setOnLoadCompleteListener {
                _,
                loadedSampleId,
                status ->

            if (
                loadedSampleId == soundId &&
                status == 0
            ) {
                soundLoaded = true

                if (pendingPlay) {
                    soundPool.play(
                        loadedSampleId,
                        0.72f,
                        0.72f,
                        2,
                        0,
                        1f
                    )
                    pendingPlay = false
                    lastPlayAt =
                        android.os.SystemClock
                            .elapsedRealtime()
                }
            }
        }

        soundLoaded = false
        pendingPlay = false
        soundId = soundPool.load(
            context,
            R.raw.combination_dial_tick,
            1
        )

        onDispose {
            soundPool.release()
        }
    }

    val currentSoundId by rememberUpdatedState(
        soundId
    )
    val currentSoundLoaded by rememberUpdatedState(
        soundLoaded
    )

    return remember(soundPool) {
        {
            val now = android.os.SystemClock
                .elapsedRealtime()

            if (now - lastPlayAt >= 28L) {
                if (
                    currentSoundLoaded &&
                    currentSoundId != 0
                ) {
                    soundPool.play(
                        currentSoundId,
                        0.72f,
                        0.72f,
                        2,
                        0,
                        1f
                    )
                    lastPlayAt = now
                } else {
                    pendingPlay = true
                }
            }
        }
    }
}

@Composable
private fun GestogramWheel(
    lmpDate: LocalDate,
    referenceDate: LocalDate,
    dueDate: LocalDate,
    gestationalDays: Int,
    onLmpDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLmpDate by rememberUpdatedState(lmpDate)
    val currentReferenceDate by rememberUpdatedState(referenceDate)
    val playDialTick = rememberCombinationDialTick()

    var gestogramRotationTarget by remember {
        mutableStateOf(0f)
    }
    var gestogramDragging by remember {
        mutableStateOf(false)
    }

    val gestogramRotation by animateFloatAsState(
        targetValue = gestogramRotationTarget,
        animationSpec = if (gestogramDragging) {
            tween(durationMillis = 45)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "gestogramRotation"
    )

    val gestogramInteraction by animateFloatAsState(
        targetValue = if (gestogramDragging) 1f else 0f,
        animationSpec = tween(durationMillis = 180),
        label = "gestogramInteraction"
    )

    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val secondaryContainer = MaterialTheme.colorScheme.secondaryContainer
    val tertiaryContainer = MaterialTheme.colorScheme.tertiaryContainer
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val outline = MaterialTheme.colorScheme.outlineVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val error = MaterialTheme.colorScheme.error

    val monthNames = remember {
        listOf(
            "Ene", "Feb", "Mar", "Abr",
            "May", "Jun", "Jul", "Ago",
            "Sep", "Oct", "Nov", "Dic"
        )
    }

    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(surface)
            .pointerInput(referenceDate) {
                var previousAngle = 0f
                var accumulatedDegrees = 0f
                var dragStartDate = currentLmpDate
                var lastTickDate = currentLmpDate

                fun pointerAngle(position: Offset): Float {
                    val center = Offset(
                        size.width / 2f,
                        size.height / 2f
                    )
                    return Math.toDegrees(
                        atan2(
                            (position.y - center.y).toDouble(),
                            (position.x - center.x).toDouble()
                        )
                    ).toFloat()
                }

                fun normalizedDelta(
                    current: Float,
                    previous: Float
                ): Float {
                    var delta = current - previous
                    if (delta > 180f) delta -= 360f
                    if (delta < -180f) delta += 360f
                    return delta
                }

                detectDragGestures(
                    onDragStart = {
                        dragStartDate = currentLmpDate
                        lastTickDate = currentLmpDate
                        previousAngle = pointerAngle(it)
                        accumulatedDegrees = 0f
                        gestogramDragging = true
                        gestogramRotationTarget = 0f
                    },
                    onDragEnd = {
                        gestogramDragging = false
                        gestogramRotationTarget = 0f
                    },
                    onDragCancel = {
                        gestogramDragging = false
                        gestogramRotationTarget = 0f
                    }
                ) { change, _ ->
                    val currentAngle = pointerAngle(
                        change.position
                    )
                    accumulatedDegrees += normalizedDelta(
                        currentAngle,
                        previousAngle
                    )
                    previousAngle = currentAngle

                    val yearDays = ChronoUnit.DAYS
                        .between(
                            dragStartDate,
                            dragStartDate.plusYears(1)
                        )
                        .coerceAtLeast(365)
                    val degreesPerDay =
                        360f / yearDays.toFloat()
                    val dayShift = (
                        -accumulatedDegrees /
                            degreesPerDay
                        ).roundToInt()

                    gestogramRotationTarget =
                        accumulatedDegrees +
                            dayShift * degreesPerDay

                    val minimumDate = currentReferenceDate
                        .minusDays(294)
                    val candidate = dragStartDate.plusDays(
                        dayShift.toLong()
                    )
                    val clamped = candidate.coerceIn(
                        minimumDate,
                        currentReferenceDate
                    )

                    if (clamped != lastTickDate) {
                        playDialTick()
                        lastTickDate = clamped
                        onLmpDateChange(clamped)
                    }
                    change.consume()
                }
            }
            .padding(5.dp)
    ) {
        val diameter = min(size.width, size.height)
        val radius = diameter / 2f
        val center = Offset(
            size.width / 2f,
            size.height / 2f
        )

        val yearDays = ChronoUnit.DAYS
            .between(lmpDate, lmpDate.plusYears(1))
            .coerceAtLeast(365)
            .toFloat()

        fun angleFor(date: LocalDate): Float {
            val daysFromLmp = ChronoUnit.DAYS
                .between(lmpDate, date)
                .toFloat()
            return -90f + (
                daysFromLmp / yearDays * 360f
                ) + gestogramRotation
        }

        fun pointAt(
            angleDegrees: Float,
            distance: Float
        ): Offset {
            val radians = angleDegrees / 180f *
                PI.toFloat()
            return Offset(
                center.x + cos(radians) * distance,
                center.y + sin(radians) * distance
            )
        }

        fun ringRect(ringRadius: Float): Pair<Offset, Size> =
            Offset(
                center.x - ringRadius,
                center.y - ringRadius
            ) to Size(
                ringRadius * 2f,
                ringRadius * 2f
            )

        val monthRadius = radius * 0.84f
        val monthStroke = radius * 0.16f
        val weekRadius = radius * 0.62f
        val weekStroke = radius * 0.19f
        val trimesterRadius = radius * 0.47f
        val centerRadius = radius * (
            0.34f +
                gestogramInteraction * 0.006f
            )

        drawCircle(
            color = surfaceVariant.copy(alpha = 0.42f),
            radius = radius * 0.98f,
            center = center
        )

        val monthStart = YearMonth.from(lmpDate)
            .minusMonths(1)

        repeat(13) { index ->
            val yearMonth = monthStart.plusMonths(
                index.toLong()
            )
            val start = yearMonth.atDay(1)
            val end = yearMonth.plusMonths(1)
                .atDay(1)
            val startAngle = angleFor(start)
            val sweep = angleFor(end) - startAngle
            val (topLeft, arcSize) = ringRect(monthRadius)

            drawArc(
                color = if (index % 2 == 0) {
                    primaryContainer
                } else {
                    secondaryContainer
                },
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = monthStroke * (
                        1f +
                            gestogramInteraction * 0.025f
                        ),
                    cap = StrokeCap.Butt
                )
            )

            val midAngle = startAngle + sweep / 2f
            val labelPoint = pointAt(
                midAngle,
                monthRadius
            )
            val labelPaint = AndroidPaint().apply {
                color = onSurface.toArgb()
                textSize = radius * 0.052f
                textAlign = AndroidPaint.Align.CENTER
                isAntiAlias = true
                isFakeBoldText = true
            }

            drawContext.canvas.nativeCanvas.apply {
                save()
                rotate(
                    midAngle + 90f,
                    labelPoint.x,
                    labelPoint.y
                )
                drawText(
                    monthNames[
                        yearMonth.monthValue - 1
                    ],
                    labelPoint.x,
                    labelPoint.y +
                        radius * 0.018f,
                    labelPaint
                )
                restore()
            }

            for (
                day in 5..
                    yearMonth.lengthOfMonth()
                    step 5
            ) {
                val date = yearMonth.atDay(day)
                val angle = angleFor(date)
                val inner = pointAt(
                    angle,
                    radius * 0.91f
                )
                val outer = pointAt(
                    angle,
                    radius * 0.965f
                )

                drawLine(
                    color = onSurfaceVariant.copy(
                        alpha = 0.72f
                    ),
                    start = inner,
                    end = outer,
                    strokeWidth = if (day % 10 == 0) {
                        2.dp.toPx()
                    } else {
                        1.dp.toPx()
                    }
                )

                if (day % 10 == 0) {
                    val dayPoint = pointAt(
                        angle,
                        radius * 0.945f
                    )
                    val dayPaint = AndroidPaint().apply {
                        color = onSurfaceVariant.toArgb()
                        textSize = radius * 0.030f
                        textAlign = AndroidPaint.Align.CENTER
                        isAntiAlias = true
                    }
                    drawContext.canvas.nativeCanvas
                        .drawText(
                            day.toString(),
                            dayPoint.x,
                            dayPoint.y +
                                radius * 0.010f,
                            dayPaint
                        )
                }
            }
        }

        val postpartumStart = angleFor(dueDate)
        val postpartumSweep = 360f - (
            postpartumStart + 90f
            )
        val (weekTopLeft, weekArcSize) =
            ringRect(weekRadius)

        if (postpartumSweep > 0f) {
            drawArc(
                color = outline.copy(alpha = 0.36f),
                startAngle = postpartumStart,
                sweepAngle = postpartumSweep,
                useCenter = false,
                topLeft = weekTopLeft,
                size = weekArcSize,
                style = Stroke(
                    width = weekStroke,
                    cap = StrokeCap.Butt
                )
            )
        }

        repeat(40) { index ->
            val weekNumber = index + 1
            val weekStart = lmpDate.plusDays(
                (index * 7L)
            )
            val weekEnd = lmpDate.plusDays(
                ((index + 1) * 7L)
            )
            val startAngle = angleFor(weekStart)
            val sweep = angleFor(weekEnd) -
                startAngle

            val color = when (weekNumber) {
                in 1..13 ->
                    primaryContainer
                in 14..27 ->
                    secondaryContainer
                else ->
                    tertiaryContainer
            }

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = weekTopLeft,
                size = weekArcSize,
                style = Stroke(
                    width = weekStroke,
                    cap = StrokeCap.Butt
                )
            )

            drawLine(
                color = outline.copy(alpha = 0.78f),
                start = pointAt(
                    startAngle,
                    radius * 0.525f
                ),
                end = pointAt(
                    startAngle,
                    radius * 0.715f
                ),
                strokeWidth = 0.8.dp.toPx()
            )

            if (
                weekNumber == 1 ||
                weekNumber % 2 == 0
            ) {
                val midAngle = startAngle +
                    sweep / 2f
                val labelPoint = pointAt(
                    midAngle,
                    weekRadius
                )
                val weekPaint = AndroidPaint().apply {
                    this.color =
                        onSurface.toArgb()
                    textSize = radius * 0.036f
                    textAlign =
                        AndroidPaint.Align.CENTER
                    isAntiAlias = true
                    isFakeBoldText =
                        weekNumber % 4 == 0
                }

                drawContext.canvas.nativeCanvas
                    .drawText(
                        weekNumber.toString(),
                        labelPoint.x,
                        labelPoint.y +
                            radius * 0.012f,
                        weekPaint
                    )
            }
        }

        val trimesterData = listOf(
            Triple("1T", 1, 13),
            Triple("2T", 14, 27),
            Triple("3T", 28, 40)
        )
        val trimesterColors = listOf(
            primary,
            secondary,
            tertiary
        )

        trimesterData.forEachIndexed {
                index,
                (label, startWeek, endWeek) ->
            val midpointDay = (
                ((startWeek - 1) * 7) +
                    ((endWeek - startWeek + 1) *
                        7 / 2)
                )
            val angle = angleFor(
                lmpDate.plusDays(
                    midpointDay.toLong()
                )
            )
            val point = pointAt(
                angle,
                trimesterRadius
            )
            val paint = AndroidPaint().apply {
                color = trimesterColors[index]
                    .toArgb()
                textSize = radius * 0.050f
                textAlign =
                    AndroidPaint.Align.CENTER
                isAntiAlias = true
                isFakeBoldText = true
            }
            drawContext.canvas.nativeCanvas.drawText(
                label,
                point.x,
                point.y + radius * 0.015f,
                paint
            )
        }

        val dueAngle = angleFor(dueDate)
        drawLine(
            color = tertiary,
            start = pointAt(
                dueAngle,
                radius * 0.40f
            ),
            end = pointAt(
                dueAngle,
                radius * 0.98f
            ),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(
            color = tertiary,
            radius = radius * 0.027f,
            center = pointAt(
                dueAngle,
                radius * 0.96f
            )
        )

        val dueLabelPoint = pointAt(
            dueAngle,
            radius * 0.76f
        )
        val markerPaint = AndroidPaint().apply {
            color = tertiary.toArgb()
            textSize = radius * 0.040f
            textAlign = AndroidPaint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        }
        drawContext.canvas.nativeCanvas.drawText(
            "FPP",
            dueLabelPoint.x,
            dueLabelPoint.y,
            markerPaint
        )

        val currentDays = ChronoUnit.DAYS
            .between(lmpDate, referenceDate)
        if (currentDays in 0..294) {
            val todayAngle = angleFor(referenceDate)
            drawLine(
                color = error,
                start = pointAt(
                    todayAngle,
                    radius * 0.43f
                ),
                end = pointAt(
                    todayAngle,
                    radius * 0.78f
                ),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = surface,
                radius = radius * 0.035f,
                center = pointAt(
                    todayAngle,
                    weekRadius
                )
            )
            drawCircle(
                color = error,
                radius = radius * 0.024f,
                center = pointAt(
                    todayAngle,
                    weekRadius
                )
            )

            val todayPoint = pointAt(
                todayAngle,
                radius * 0.76f
            )
            val todayPaint = AndroidPaint().apply {
                color = error.toArgb()
                textSize = radius * 0.037f
                textAlign =
                    AndroidPaint.Align.CENTER
                isAntiAlias = true
                isFakeBoldText = true
            }
            drawContext.canvas.nativeCanvas
                .drawText(
                    "HOY",
                    todayPoint.x,
                    todayPoint.y,
                    todayPaint
                )
        }

        drawCircle(
            color = surface,
            radius = centerRadius,
            center = center
        )
        drawCircle(
            color = primaryContainer,
            radius = centerRadius,
            center = center,
            style = Stroke(
                width = radius * 0.035f
            )
        )

        val centerTitlePaint =
            AndroidPaint().apply {
                color = primary.toArgb()
                textSize = radius * 0.085f
                textAlign =
                    AndroidPaint.Align.CENTER
                isAntiAlias = true
                isFakeBoldText = true
            }
        val centerWeekPaint =
            AndroidPaint().apply {
                color = onSurface.toArgb()
                textSize = radius * 0.115f
                textAlign =
                    AndroidPaint.Align.CENTER
                isAntiAlias = true
                isFakeBoldText = true
            }
        val centerDatePaint =
            AndroidPaint().apply {
                color = onSurfaceVariant.toArgb()
                textSize = radius * 0.042f
                textAlign =
                    AndroidPaint.Align.CENTER
                isAntiAlias = true
            }

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                "Gestograma",
                center.x,
                center.y - radius * 0.095f,
                centerTitlePaint
            )
            drawText(
                "${gestationalDays.coerceAtLeast(0) / 7}+" +
                    "${gestationalDays.coerceAtLeast(0) % 7}",
                center.x,
                center.y + radius * 0.035f,
                centerWeekPaint
            )
            drawText(
                "FPP " + dueDate.format(
                    DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                    )
                ),
                center.x,
                center.y + radius * 0.130f,
                centerDatePaint
            )
        }

        val fumPath = Path().apply {
            moveTo(
                center.x,
                center.y - radius * 0.995f
            )
            lineTo(
                center.x - radius * 0.065f,
                center.y - radius * 0.82f
            )
            lineTo(
                center.x + radius * 0.065f,
                center.y - radius * 0.82f
            )
            close()
        }
        drawPath(
            path = fumPath,
            color = error
        )

        val fumPaint = AndroidPaint().apply {
            color = onPrimary.toArgb()
            textSize = radius * 0.042f
            textAlign = AndroidPaint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        }
        drawContext.canvas.nativeCanvas.drawText(
            "FUM",
            center.x,
            center.y - radius * 0.865f,
            fumPaint
        )
    }
}

@Composable
private fun GestogramControls(
    lmpDate: LocalDate,
    referenceDate: LocalDate,
    dueDate: LocalDate,
    weeks: Long,
    days: Long,
    onLmpDate: (LocalDate) -> Unit,
    onReferenceDate: (LocalDate) -> Unit,
    compactHeight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement =
            Arrangement.spacedBy(
                if (compactHeight) {
                    8.dp
                } else {
                    12.dp
                }
            )
    ) {
        EditableDateInput(
            label =
                "Fecha de última menstruación",
            date = lmpDate,
            onDate = onLmpDate,
            maxDate = referenceDate
        )

        EditableDateInput(
            label = "Fecha de evaluación",
            date = referenceDate,
            onDate = onReferenceDate,
            minDate = lmpDate,
            maxDate =
                LocalDate.now().plusDays(1)
        )

        GestogramSummaryCard(
            lmpDate = lmpDate,
            dueDate = dueDate,
            referenceDate = referenceDate,
            weeks = weeks,
            days = days,
            compactHeight =
                compactHeight
        )

        Text(
            if (compactHeight) {
                "FPP = FUM + 280 días. Confirma con ultrasonido cuando corresponda."
            } else {
                (
                    "La FPP se calcula a 280 días desde la FUM. " +
                        "Confirma o redatá con ultrasonido cuando corresponda."
                    )
            },
            style =
                MaterialTheme.typography
                    .labelSmall,
            color =
                MaterialTheme.colorScheme
                    .onSurfaceVariant
        )
    }
}

@Composable
private fun GestogramSummaryCard(
    lmpDate: LocalDate,
    dueDate: LocalDate,
    referenceDate: LocalDate,
    weeks: Long,
    days: Long,
    compactHeight: Boolean = false
) {
    val displayFormatter = remember {
        DateTimeFormatter.ofPattern(
            "d 'de' MMMM 'de' yyyy",
            Locale("es", "MX")
        )
    }

    OutlinedCard(
        colors =
            CardDefaults.outlinedCardColors(
                containerColor =
                    MaterialTheme.colorScheme
                        .primaryContainer
                        .copy(alpha = 0.42f)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    if (compactHeight) {
                        10.dp
                    } else {
                        14.dp
                    }
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    if (compactHeight) {
                        6.dp
                    } else {
                        10.dp
                    }
                )
        ) {
            GestogramSummaryRow(
                label = "FUM",
                value = lmpDate.format(
                    displayFormatter
                )
            )
            GestogramSummaryRow(
                label = "Parto",
                value = dueDate.format(
                    displayFormatter
                )
            )
            GestogramSummaryRow(
                label = "Tienes",
                value =
                    "$weeks semanas + $days días",
                emphasize = true
            )
            GestogramSummaryRow(
                label = "Fecha evaluada",
                value = referenceDate.format(
                    displayFormatter
                )
            )
        }
    }
}

@Composable
private fun GestogramSummaryRow(
    label: String,
    value: String,
    emphasize: Boolean = false
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            "$label:",
            modifier = Modifier.width(86.dp),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black
        )
        Text(
            value,
            modifier = Modifier.weight(1f),
            fontWeight = if (emphasize) {
                FontWeight.Black
            } else {
                FontWeight.SemiBold
            },
            style = if (emphasize) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyMedium
            }
        )
    }
}

private val manualDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern(
        "dd/MM/uuuu",
        Locale("es", "MX")
    ).withResolverStyle(
        ResolverStyle.STRICT
    )

private fun formatManualDateInput(
    rawValue: String
): String {
    val digits = rawValue
        .filter(Char::isDigit)
        .take(8)

    return buildString {
        digits.forEachIndexed { index, char ->
            append(char)
            if (
                (index == 1 || index == 3) &&
                index < digits.lastIndex
            ) {
                append('/')
            }
        }
    }
}

private fun parseManualDateInput(
    value: String
): LocalDate? = runCatching {
    LocalDate.parse(
        value,
        manualDateFormatter
    )
}.getOrNull()

@Composable
private fun EditableDateInput(
    label: String,
    date: LocalDate,
    onDate: (LocalDate) -> Unit,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null
) {
    var text by rememberSaveable(label) {
        mutableStateOf(
            date.format(manualDateFormatter)
        )
    }
    var showCalendar by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(date) {
        val formatted = date.format(
            manualDateFormatter
        )
        if (text != formatted) {
            text = formatted
        }
    }

    val parsedDate = remember(text) {
        parseManualDateInput(text)
    }
    val errorMessage = remember(
        text,
        parsedDate,
        minDate,
        maxDate
    ) {
        when {
            text.length < 10 ->
                null
            parsedDate == null ->
                "La fecha no es válida."
            minDate != null &&
                parsedDate.isBefore(minDate) ->
                "La fecha mínima es " +
                    minDate.format(
                        manualDateFormatter
                    )
            maxDate != null &&
                parsedDate.isAfter(maxDate) ->
                "La fecha máxima es " +
                    maxDate.format(
                        manualDateFormatter
                    )
            else ->
                null
        }
    }

    OutlinedTextField(
        value = text,
        onValueChange = { raw ->
            val formatted = formatManualDateInput(
                raw
            )
            text = formatted

            val candidate = parseManualDateInput(
                formatted
            )
            val inRange = candidate != null &&
                (
                    minDate == null ||
                        !candidate.isBefore(minDate)
                    ) &&
                (
                    maxDate == null ||
                        !candidate.isAfter(maxDate)
                    )

            if (inRange && candidate != date) {
                onDate(candidate)
            }
        },
        label = { Text(label) },
        placeholder = { Text("dd/mm/aaaa") },
        leadingIcon = {
            Icon(
                imageVector =
                    Icons.Default.CalendarMonth,
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    showCalendar = true
                }
            ) {
                Icon(
                    imageVector =
                        Icons.Default.CalendarMonth,
                    contentDescription =
                        "Elegir $label en calendario"
                )
            }
        },
        supportingText = {
            Text(
                errorMessage
                    ?: "Puedes escribir la fecha o abrir el calendario."
            )
        },
        isError = errorMessage != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )

    if (showCalendar) {
        ClinicalCalendarDialog(
            title = label,
            initialDate = date,
            minDate = minDate,
            maxDate = maxDate,
            onDismiss = {
                showCalendar = false
            },
            onConfirm = {
                onDate(it)
                text = it.format(
                    manualDateFormatter
                )
                showCalendar = false
            }
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
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                note,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            content()
            clinicalInfoForCalculator(title)?.let { info ->
                HorizontalDivider()
                ClinicalInfoButton(info = info)
            }
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
            ClinicalInfoButton(
                info = percentileClinicalInfo()
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



@Composable
private fun RenalMethodSelector(
    selected: RenalMethod,
    onSelected: (RenalMethod) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    selected.label,
                    fontWeight = FontWeight.Black
                )
                Text(
                    selected.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Cambiar método"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.widthIn(
                min = 300.dp,
                max = 560.dp
            )
        ) {
            RenalMethod.entries.forEach { method ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                method.label,
                                fontWeight = if (method == selected) {
                                    FontWeight.Black
                                } else {
                                    FontWeight.SemiBold
                                }
                            )
                            Text(
                                method.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onSelected(method)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun RenalFunctionScreen(
    modifier: Modifier = Modifier
) {
    var methodName by rememberSaveable {
        mutableStateOf(
            RenalMethod.CKD_EPI_2021_CREATININE.name
        )
    }
    val method = remember(methodName) {
        RenalMethod.valueOf(methodName)
    }
    var ageText by rememberSaveable { mutableStateOf("") }
    var sexName by rememberSaveable {
        mutableStateOf(RenalSex.MALE.name)
    }
    val sex = remember(sexName) {
        RenalSex.valueOf(sexName)
    }
    var creatinineText by rememberSaveable { mutableStateOf("") }
    var creatinineUnitName by rememberSaveable {
        mutableStateOf(CreatinineUnit.MG_DL.name)
    }
    val creatinineUnit = remember(creatinineUnitName) {
        CreatinineUnit.valueOf(creatinineUnitName)
    }
    var cystatinText by rememberSaveable { mutableStateOf("") }
    var weightText by rememberSaveable { mutableStateOf("") }
    var heightText by rememberSaveable { mutableStateOf("") }
    var includeAcr by rememberSaveable { mutableStateOf(false) }
    var acrText by rememberSaveable { mutableStateOf("") }
    var acrUnitName by rememberSaveable {
        mutableStateOf(AcrUnit.MG_G.name)
    }
    val acrUnit = remember(acrUnitName) {
        AcrUnit.valueOf(acrUnitName)
    }

    val input = remember(
        method,
        ageText,
        sex,
        creatinineText,
        creatinineUnit,
        cystatinText,
        weightText,
        heightText,
        includeAcr,
        acrText,
        acrUnit
    ) {
        RenalInput(
            method = method,
            ageYears = ageText.toIntOrNull() ?: 0,
            sex = sex,
            creatinine = creatinineText
                .replace(',', '.')
                .toDoubleOrNull(),
            creatinineUnit = creatinineUnit,
            cystatinCmgL = cystatinText
                .replace(',', '.')
                .toDoubleOrNull(),
            weightKg = weightText
                .replace(',', '.')
                .toDoubleOrNull(),
            heightCm = heightText
                .replace(',', '.')
                .toDoubleOrNull(),
            acr = if (includeAcr) {
                acrText.replace(',', '.')
                    .toDoubleOrNull()
            } else {
                null
            },
            acrUnit = acrUnit
        )
    }

    val requiredReady = remember(input) {
        input.ageYears >= 18 &&
            (!method.requiresCreatinine || input.creatinine != null) &&
            (!method.requiresCystatinC || input.cystatinCmgL != null) &&
            (!method.requiresWeight || input.weightKg != null) &&
            (!method.requiresHeight || input.heightCm != null) &&
            (!includeAcr || input.acr != null)
    }

    val calculation = remember(input, requiredReady) {
        if (requiredReady) {
            RenalCalculator.calculate(input)
        } else {
            null
        }
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = 0.42f
                    )
                )
            ) {
                Column(
                    Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "Función renal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "Selecciona una ecuación. Solo el método activo aparece y participa en el cálculo.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            OutlinedCard {
                Column(
                    Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Método",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                    RenalMethodSelector(
                        selected = method,
                        onSelected = {
                            methodName = it.name
                        }
                    )

                    FormTextField(
                        label = "Edad",
                        value = ageText,
                        onValue = {
                            ageText = it.filter(Char::isDigit)
                                .take(3)
                        },
                        keyboardType = KeyboardType.Number,
                        suffix = "años"
                    )

                    Text(
                        "Sexo",
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RenalSex.entries.forEach { option ->
                            FilterChip(
                                selected = sex == option,
                                onClick = {
                                    sexName = option.name
                                },
                                label = { Text(option.label) }
                            )
                        }
                    }

                    if (method.requiresCreatinine) {
                        Text(
                            "Creatinina sérica",
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FormTextField(
                                label = "Creatinina",
                                value = creatinineText,
                                onValue = {
                                    creatinineText = decimalText(it)
                                },
                                modifier = Modifier.weight(1f),
                                keyboardType = KeyboardType.Decimal,
                                suffix = creatinineUnit.label
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CreatinineUnit.entries.forEach { unit ->
                                FilterChip(
                                    selected = creatinineUnit == unit,
                                    onClick = {
                                        creatinineUnitName = unit.name
                                    },
                                    label = { Text(unit.label) }
                                )
                            }
                        }
                    }

                    if (method.requiresCystatinC) {
                        FormTextField(
                            label = "Cistatina C",
                            value = cystatinText,
                            onValue = {
                                cystatinText = decimalText(it)
                            },
                            keyboardType = KeyboardType.Decimal,
                            suffix = "mg/L"
                        )
                    }

                    if (method.requiresWeight) {
                        FormTextField(
                            label = "Peso",
                            value = weightText,
                            onValue = {
                                weightText = decimalText(it)
                            },
                            keyboardType = KeyboardType.Decimal,
                            suffix = "kg"
                        )
                    }

                    if (method.requiresHeight) {
                        FormTextField(
                            label = "Talla",
                            value = heightText,
                            onValue = {
                                heightText = decimalText(it)
                            },
                            keyboardType = KeyboardType.Decimal,
                            suffix = "cm"
                        )
                    }
                }
            }
        }

        item {
            OutlinedCard {
                Column(
                    Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = includeAcr,
                            onCheckedChange = {
                                includeAcr = it
                            }
                        )
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Agregar albuminuria",
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                "Opcional. Permite clasificar A1–A3 y el riesgo combinado KDIGO.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (includeAcr) {
                        FormTextField(
                            label = "Cociente albúmina/creatinina (ACR)",
                            value = acrText,
                            onValue = {
                                acrText = decimalText(it)
                            },
                            keyboardType = KeyboardType.Decimal,
                            suffix = acrUnit.label
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AcrUnit.entries.forEach { unit ->
                                FilterChip(
                                    selected = acrUnit == unit,
                                    onClick = {
                                        acrUnitName = unit.name
                                    },
                                    label = { Text(unit.label) }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            when {
                !requiredReady -> RenalPendingCard(method, includeAcr)
                calculation?.isFailure == true -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            calculation.exceptionOrNull()?.message
                                ?: "No fue posible calcular.",
                            modifier = Modifier.padding(14.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                calculation?.isSuccess == true -> {
                    RenalResultCard(
                        result = calculation.getOrThrow()
                    )
                }
            }
        }

        calculation?.getOrNull()?.let { result ->
            item {
                KidneyRiskGrid(
                    selectedGfr = result.gfrCategory,
                    selectedAlbuminuria = result.albuminuriaCategory
                )
            }
        }

        item {
            ClinicalInfoButton(
                info = renalClinicalInfo()
            )
        }
    }
}

@Composable
private fun RenalPendingCard(
    method: RenalMethod,
    includeAcr: Boolean
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                "Completa los datos",
                fontWeight = FontWeight.Black
            )
            Text(
                buildString {
                    append("Se requiere edad y sexo")
                    if (method.requiresCreatinine) append(", creatinina")
                    if (method.requiresCystatinC) append(", cistatina C")
                    if (method.requiresWeight) append(", peso")
                    if (method.requiresHeight) append(" y talla")
                    if (includeAcr) append(", además del ACR")
                    append(".")
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RenalResultCard(
    result: RenalResult
) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.50f
            )
        )
    ) {
        Column(
            Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "Resultado",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
            Text(
                result.method.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "${formatDecimal(result.primaryValue, 1)} ${result.primaryUnit}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )
            Text(
                result.primaryLabel,
                style = MaterialTheme.typography.bodySmall
            )

            result.indexedValue
                ?.takeIf {
                    result.method == RenalMethod.COCKCROFT_GAULT
                }
                ?.let { indexedValue ->
                    HorizontalDivider()
                    ResultBlock(
                        title = "Indexación por superficie corporal",
                        rows = listOf(
                            "Superficie corporal" to (
                                result.bodySurfaceArea?.let {
                                    "${formatDecimal(it, 2)} m²"
                                } ?: "Pendiente"
                                ),
                            "Valor indexado" to (
                                "${formatDecimal(indexedValue, 1)} mL/min/1.73 m²"
                                )
                        )
                    )
                }

            result.gfrCategory?.let { category ->
                HorizontalDivider()
                Text(
                    "${category.label} · ${category.description}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Intervalo: ${category.rangeLabel} mL/min/1.73 m²",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            result.albuminuriaCategory?.let { category ->
                Text(
                    "${category.label} · ${category.description}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    category.rangeLabel,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            result.riskLevel?.let { risk ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = renalRiskColor(risk),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        risk.label,
                        modifier = Modifier.padding(12.dp),
                        color = renalRiskTextColor(risk),
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            result.warnings.forEach { warning ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        warning,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private fun renalRiskColor(
    risk: CkdRiskLevel
): Color = when (risk) {
    CkdRiskLevel.LOW -> Color(0xFF9CCC65)
    CkdRiskLevel.MODERATE -> Color(0xFFFFD54F)
    CkdRiskLevel.HIGH -> Color(0xFFFFA726)
    CkdRiskLevel.VERY_HIGH -> Color(0xFFE85C57)
}

private fun renalRiskTextColor(
    risk: CkdRiskLevel
): Color = if (risk == CkdRiskLevel.VERY_HIGH) {
    Color.White
} else {
    Color(0xFF1A1A1A)
}

@Composable
private fun KidneyRiskGrid(
    selectedGfr: GfrCategory?,
    selectedAlbuminuria: AlbuminuriaCategory?
) {
    OutlinedCard {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(
                "Clasificación KDIGO G × A",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
            Text(
                if (selectedAlbuminuria == null) {
                    "La fila G está marcada. Agrega ACR para señalar una celda y calcular riesgo combinado."
                } else {
                    "La celda resaltada corresponde a la combinación calculada."
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(Modifier.fillMaxWidth()) {
                KidneyGridLabelCell(
                    text = "GFR",
                    modifier = Modifier.weight(0.82f)
                )
                AlbuminuriaCategory.entries.forEach { category ->
                    KidneyGridLabelCell(
                        text = category.label,
                        subtitle = category.rangeLabel.substringBefore(" · "),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            GfrCategory.entries.forEach { gfr ->
                Row(Modifier.fillMaxWidth()) {
                    KidneyGridLabelCell(
                        text = gfr.label,
                        subtitle = gfr.rangeLabel,
                        selected = selectedGfr == gfr,
                        modifier = Modifier.weight(0.82f)
                    )
                    AlbuminuriaCategory.entries.forEach { albuminuria ->
                        val risk = RenalCalculator.combinedRisk(
                            gfr,
                            albuminuria
                        )
                        KidneyRiskCell(
                            risk = risk,
                            selected = selectedGfr == gfr &&
                                selectedAlbuminuria == albuminuria,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CkdRiskLevel.entries.forEach { risk ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = renalRiskColor(risk),
                            modifier = Modifier.size(14.dp)
                        ) {}
                        Text(
                            risk.label.replace("Riesgo ", ""),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KidneyGridLabelCell(
    text: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    selected: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        border = androidx.compose.foundation.BorderStroke(
            if (selected) 2.dp else 0.5.dp,
            if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        ),
        modifier = modifier
            .padding(2.dp)
            .height(52.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.labelMedium
            )
            if (subtitle.isNotBlank()) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun KidneyRiskCell(
    risk: CkdRiskLevel,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = renalRiskColor(risk),
        border = androidx.compose.foundation.BorderStroke(
            if (selected) 3.dp else 0.5.dp,
            if (selected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        ),
        modifier = modifier
            .padding(2.dp)
            .height(52.dp)
    ) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Text(
                    "✓",
                    color = renalRiskTextColor(risk),
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

private enum class DateOrbitRing {
    NONE,
    MONTH,
    DAY
}

private fun shiftDateMonth(
    date: LocalDate,
    months: Int
): LocalDate {
    val target = YearMonth.from(date)
        .plusMonths(months.toLong())
    return target.atDay(
        date.dayOfMonth.coerceAtMost(
            target.lengthOfMonth()
        )
    )
}

private fun shiftDateDayWithinMonth(
    date: LocalDate,
    days: Int
): LocalDate {
    val length = date.lengthOfMonth()
    val zeroBased = date.dayOfMonth - 1
    val wrapped = ((zeroBased + days) % length + length) % length
    return date.withDayOfMonth(wrapped + 1)
}

@Composable
private fun DateOrbitWheel(
    selectedDate: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentDate by rememberUpdatedState(selectedDate)
    val playDialTick = rememberCombinationDialTick()

    var activeRing by remember {
        mutableStateOf(DateOrbitRing.NONE)
    }
    var monthRotationTarget by remember {
        mutableStateOf(0f)
    }
    var dayRotationTarget by remember {
        mutableStateOf(0f)
    }

    val monthRotation by animateFloatAsState(
        targetValue = monthRotationTarget,
        animationSpec = if (
            activeRing == DateOrbitRing.MONTH
        ) {
            tween(durationMillis = 40)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "calendarMonthRotation"
    )

    val dayRotation by animateFloatAsState(
        targetValue = dayRotationTarget,
        animationSpec = if (
            activeRing == DateOrbitRing.DAY
        ) {
            tween(durationMillis = 40)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "calendarDayRotation"
    )

    val ringInteraction by animateFloatAsState(
        targetValue = if (
            activeRing == DateOrbitRing.NONE
        ) {
            0f
        } else {
            1f
        },
        animationSpec = tween(durationMillis = 170),
        label = "calendarRingInteraction"
    )

    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val secondaryContainer = MaterialTheme.colorScheme.secondaryContainer
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val monthNames = remember {
        listOf(
            "Ene", "Feb", "Mar", "Abr",
            "May", "Jun", "Jul", "Ago",
            "Sep", "Oct", "Nov", "Dic"
        )
    }

    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(surface)
            .pointerInput(minDate, maxDate) {
                var previousAngle = 0f
                var accumulatedDegrees = 0f
                var dragStartDate = currentDate
                var lastTickDate = currentDate
                var dragRing = DateOrbitRing.NONE

                fun pointerAngle(position: Offset): Float {
                    val center = Offset(
                        size.width / 2f,
                        size.height / 2f
                    )
                    return Math.toDegrees(
                        atan2(
                            (position.y - center.y).toDouble(),
                            (position.x - center.x).toDouble()
                        )
                    ).toFloat()
                }

                fun pointerRadius(position: Offset): Float {
                    val dx = position.x - size.width / 2f
                    val dy = position.y - size.height / 2f
                    val distance = kotlin.math.sqrt(
                        dx * dx + dy * dy
                    )
                    return distance /
                        (min(size.width, size.height) / 2f)
                }

                fun normalizedDelta(
                    current: Float,
                    previous: Float
                ): Float {
                    var delta = current - previous
                    if (delta > 180f) delta -= 360f
                    if (delta < -180f) delta += 360f
                    return delta
                }

                fun clamp(date: LocalDate): LocalDate =
                    clampCalendarDate(
                        date,
                        minDate,
                        maxDate
                    )

                detectDragGestures(
                    onDragStart = { position ->
                        dragStartDate = currentDate
                        lastTickDate = currentDate
                        previousAngle = pointerAngle(position)
                        accumulatedDegrees = 0f
                        val radiusRatio = pointerRadius(position)
                        dragRing = when {
                            radiusRatio in 0.68f..1.05f ->
                                DateOrbitRing.MONTH
                            radiusRatio >= 0.40f && radiusRatio < 0.68f ->
                                DateOrbitRing.DAY
                            else -> DateOrbitRing.NONE
                        }
                        activeRing = dragRing

                        if (
                            dragRing == DateOrbitRing.MONTH
                        ) {
                            monthRotationTarget = 0f
                        }
                        if (
                            dragRing == DateOrbitRing.DAY
                        ) {
                            dayRotationTarget = 0f
                        }
                    },
                    onDragEnd = {
                        activeRing = DateOrbitRing.NONE
                        monthRotationTarget = 0f
                        dayRotationTarget = 0f
                    },
                    onDragCancel = {
                        activeRing = DateOrbitRing.NONE
                        monthRotationTarget = 0f
                        dayRotationTarget = 0f
                    }
                ) { change, _ ->
                    if (dragRing != DateOrbitRing.NONE) {
                        val angle = pointerAngle(change.position)
                        accumulatedDegrees += normalizedDelta(
                            angle,
                            previousAngle
                        )
                        previousAngle = angle

                        val candidate = when (dragRing) {
                            DateOrbitRing.MONTH -> {
                                val monthShift = (
                                    -accumulatedDegrees / 30f
                                ).roundToInt()

                                monthRotationTarget =
                                    accumulatedDegrees +
                                        monthShift * 30f

                                shiftDateMonth(
                                    dragStartDate,
                                    monthShift
                                )
                            }

                            DateOrbitRing.DAY -> {
                                val degreesPerDay = 360f /
                                    dragStartDate.lengthOfMonth()
                                val dayShift = (
                                    -accumulatedDegrees /
                                        degreesPerDay
                                ).roundToInt()

                                dayRotationTarget =
                                    accumulatedDegrees +
                                        dayShift *
                                            degreesPerDay

                                shiftDateDayWithinMonth(
                                    dragStartDate,
                                    dayShift
                                )
                            }

                            DateOrbitRing.NONE ->
                                dragStartDate
                        }

                        val clamped = clamp(candidate)
                        if (clamped != lastTickDate) {
                            playDialTick()
                            lastTickDate = clamped
                            onDateChange(clamped)
                        }
                        change.consume()
                    }
                }
            }
            .padding(5.dp)
    ) {
        val diameter = min(size.width, size.height)
        val radius = diameter / 2f
        val center = Offset(
            size.width / 2f,
            size.height / 2f
        )

        fun pointAt(
            angleDegrees: Float,
            distance: Float
        ): Offset {
            val radians = angleDegrees / 180f *
                PI.toFloat()
            return Offset(
                center.x + cos(radians) * distance,
                center.y + sin(radians) * distance
            )
        }

        fun ringRect(ringRadius: Float): Pair<Offset, Size> =
            Offset(
                center.x - ringRadius,
                center.y - ringRadius
            ) to Size(
                ringRadius * 2f,
                ringRadius * 2f
            )

        val monthRadius = radius * 0.80f
        val monthStroke = radius * 0.18f
        val dayRadius = radius * 0.58f
        val dayStroke = radius * 0.18f
        val centerRadius = radius * (
            0.35f +
                ringInteraction * 0.005f
            )

        drawCircle(
            color = surfaceVariant.copy(alpha = 0.44f),
            radius = radius * 0.98f,
            center = center
        )

        val selectedMonthIndex =
            selectedDate.monthValue - 1

        repeat(12) { index ->
            val relative = index - selectedMonthIndex
            val startAngle =
                -105f +
                    relative * 30f +
                    monthRotation
            val (topLeft, arcSize) = ringRect(monthRadius)

            drawArc(
                color = if (index % 2 == 0) {
                    primaryContainer
                } else {
                    secondaryContainer
                },
                startAngle = startAngle,
                sweepAngle = 30f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = monthStroke * (
                        1f +
                            if (
                                activeRing ==
                                    DateOrbitRing.MONTH
                            ) {
                                ringInteraction * 0.035f
                            } else {
                                0f
                            }
                        ),
                    cap = StrokeCap.Butt
                )
            )

            val labelAngle = startAngle + 15f
            val labelPoint = pointAt(
                labelAngle,
                monthRadius
            )
            val paint = AndroidPaint().apply {
                color = if (index == selectedMonthIndex) {
                    primary.toArgb()
                } else {
                    onSurface.toArgb()
                }
                textSize = radius * 0.052f
                textAlign = AndroidPaint.Align.CENTER
                isAntiAlias = true
                isFakeBoldText = index == selectedMonthIndex
            }

            drawContext.canvas.nativeCanvas.apply {
                save()
                rotate(
                    labelAngle + 90f,
                    labelPoint.x,
                    labelPoint.y
                )
                drawText(
                    monthNames[index],
                    labelPoint.x,
                    labelPoint.y + radius * 0.018f,
                    paint
                )
                restore()
            }
        }

        val daysInMonth = selectedDate.lengthOfMonth()
        val selectedDayIndex = selectedDate.dayOfMonth - 1

        repeat(daysInMonth) { index ->
            val relative = index - selectedDayIndex
            val sweep = 360f / daysInMonth
            val startAngle =
                -90f -
                    sweep / 2f +
                    relative * sweep +
                    dayRotation
            val (topLeft, arcSize) = ringRect(dayRadius)

            drawArc(
                color = when {
                    index == selectedDayIndex ->
                        tertiary.copy(alpha = 0.88f)
                    index % 2 == 0 ->
                        primaryContainer.copy(alpha = 0.82f)
                    else ->
                        secondaryContainer.copy(alpha = 0.78f)
                },
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = dayStroke * (
                        1f +
                            if (
                                activeRing ==
                                    DateOrbitRing.DAY
                            ) {
                                ringInteraction * 0.035f
                            } else {
                                0f
                            }
                        ),
                    cap = StrokeCap.Butt
                )
            )

            val dayNumber = index + 1
            if (
                dayNumber == selectedDate.dayOfMonth ||
                dayNumber == 1 ||
                dayNumber % 5 == 0
            ) {
                val labelAngle = startAngle + sweep / 2f
                val point = pointAt(labelAngle, dayRadius)
                val paint = AndroidPaint().apply {
                    color = if (
                        dayNumber == selectedDate.dayOfMonth
                    ) {
                        Color.White.toArgb()
                    } else {
                        onSurfaceVariant.toArgb()
                    }
                    textSize = radius * 0.038f
                    textAlign = AndroidPaint.Align.CENTER
                    isAntiAlias = true
                    isFakeBoldText =
                        dayNumber == selectedDate.dayOfMonth
                }
                drawContext.canvas.nativeCanvas.drawText(
                    dayNumber.toString(),
                    point.x,
                    point.y + radius * 0.012f,
                    paint
                )
            }
        }

        drawCircle(
            color = surface,
            radius = centerRadius,
            center = center
        )
        drawCircle(
            color = primaryContainer,
            radius = centerRadius,
            center = center,
            style = Stroke(
                width = radius * (
                    0.035f +
                        ringInteraction * 0.002f
                    )
            )
        )

        val dayPaint = AndroidPaint().apply {
            color = primary.toArgb()
            textSize = radius * 0.20f
            textAlign = AndroidPaint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        }
        val monthPaint = AndroidPaint().apply {
            color = onSurface.toArgb()
            textSize = radius * 0.066f
            textAlign = AndroidPaint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        }
        val yearPaint = AndroidPaint().apply {
            color = onSurfaceVariant.toArgb()
            textSize = radius * 0.055f
            textAlign = AndroidPaint.Align.CENTER
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                selectedDate.dayOfMonth.toString(),
                center.x,
                center.y - radius * 0.01f,
                dayPaint
            )
            drawText(
                selectedDate.format(
                    DateTimeFormatter.ofPattern(
                        "MMMM",
                        Locale("es", "MX")
                    )
                ).replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(Locale("es", "MX"))
                    } else {
                        it.toString()
                    }
                },
                center.x,
                center.y + radius * 0.105f,
                monthPaint
            )
            drawText(
                selectedDate.year.toString(),
                center.x,
                center.y + radius * 0.185f,
                yearPaint
            )
        }

        val markerHalfWidth = radius * (
            0.055f +
                ringInteraction * 0.004f
            )
        val markerPath = Path().apply {
            moveTo(
                center.x,
                center.y - radius * 0.995f
            )
            lineTo(
                center.x - markerHalfWidth,
                center.y - radius * 0.84f
            )
            lineTo(
                center.x + markerHalfWidth,
                center.y - radius * 0.84f
            )
            close()
        }
        drawPath(
            path = markerPath,
            color = tertiary.copy(
                alpha =
                    0.86f +
                        ringInteraction * 0.14f
            )
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
    var selectedDateText by rememberSaveable(
        initialDate
    ) {
        mutableStateOf(initialDate.toString())
    }

    val selectedDate = remember(
        selectedDateText
    ) {
        LocalDate.parse(selectedDateText)
    }
    val today = remember {
        LocalDate.now()
    }
    val minimumYear =
        minDate?.year ?: 1900
    val maximumYear =
        maxDate?.year ?: (today.year + 10)

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
            val candidateMonth =
                YearMonth.of(
                    selectedDate.year,
                    monthNumber
                )
            val monthStart =
                candidateMonth.atDay(1)
            val monthEnd =
                candidateMonth.atEndOfMonth()

            CalendarPartOption(
                value = monthNumber,
                label = candidateMonth.format(
                    monthFormatter
                ).replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(
                            Locale("es", "MX")
                        )
                    } else {
                        it.toString()
                    }
                },
                enabled = (
                    (
                        minDate == null ||
                            !monthEnd.isBefore(
                                minDate
                            )
                        ) &&
                        (
                            maxDate == null ||
                                !monthStart.isAfter(
                                    maxDate
                                )
                            )
                    )
            )
        }
    }

    val dayOptions = remember(
        selectedDate.year,
        selectedDate.monthValue,
        minDate,
        maxDate
    ) {
        val selectedMonth =
            YearMonth.of(
                selectedDate.year,
                selectedDate.monthValue
            )

        (1..selectedMonth.lengthOfMonth())
            .map { day ->
                val candidate =
                    selectedMonth.atDay(day)

                CalendarPartOption(
                    value = day,
                    label = day.toString(),
                    enabled = (
                        (
                            minDate == null ||
                                !candidate.isBefore(
                                    minDate
                                )
                            ) &&
                            (
                                maxDate == null ||
                                    !candidate.isAfter(
                                        maxDate
                                    )
                                )
                        )
                )
            }
    }

    fun updateDateParts(
        year: Int = selectedDate.year,
        month: Int =
            selectedDate.monthValue,
        day: Int =
            selectedDate.dayOfMonth
    ) {
        val targetMonth =
            YearMonth.of(year, month)
        val safeDay = day.coerceIn(
            1,
            targetMonth.lengthOfMonth()
        )
        val candidate =
            targetMonth.atDay(safeDay)
        val clamped =
            clampCalendarDate(
                candidate,
                minDate,
                maxDate
            )

        selectedDateText =
            clamped.toString()
    }

    val formattedDate = selectedDate.format(
        DateTimeFormatter.ofPattern(
            "d 'de' MMMM 'de' yyyy",
            Locale("es", "MX")
        )
    ).replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(
                Locale("es", "MX")
            )
        } else {
            it.toString()
        }
    }

    val controlsContent:
        @Composable ColumnScope.() -> Unit = {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color =
                MaterialTheme.colorScheme
                    .surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(11.dp),
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Dos ruedas independientes",
                    fontWeight =
                        FontWeight.Black,
                    color =
                        MaterialTheme.colorScheme
                            .primary
                )
                Text(
                    "Gira el aro exterior para cambiar el mes y el aro interior para cambiar el día.",
                    style =
                        MaterialTheme.typography
                            .labelSmall,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
                Text(
                    "Cada avance produce un clic mecánico discreto.",
                    style =
                        MaterialTheme.typography
                            .labelSmall,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color =
                MaterialTheme.colorScheme
                    .secondaryContainer
                    .copy(alpha = 0.48f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(12.dp),
                verticalArrangement =
                    Arrangement.spacedBy(9.dp)
            ) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape =
                            RoundedCornerShape(10.dp),
                        color =
                            MaterialTheme.colorScheme
                                .primary
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default
                                    .CalendarMonth,
                            contentDescription = null,
                            tint =
                                MaterialTheme.colorScheme
                                    .onPrimary,
                            modifier =
                                Modifier.padding(7.dp)
                        )
                    }
                    Column(
                        Modifier.weight(1f)
                    ) {
                        Text(
                            "Ir directamente a una fecha",
                            style =
                                MaterialTheme.typography
                                    .labelLarge,
                            fontWeight =
                                FontWeight.Black,
                            color =
                                MaterialTheme.colorScheme
                                    .onSecondaryContainer
                        )
                        Text(
                            "Elige año, mes y día sin girar las ruedas.",
                            style =
                                MaterialTheme.typography
                                    .labelSmall,
                            color =
                                MaterialTheme.colorScheme
                                    .onSecondaryContainer
                                    .copy(alpha = 0.78f)
                        )
                    }
                }

                BoxWithConstraints(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    if (maxWidth < 390.dp) {
                        Column(
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    8.dp
                                )
                        ) {
                            CalendarDatePartSelector(
                                label = "Día",
                                selectedValue =
                                    selectedDate
                                        .dayOfMonth,
                                selectedLabel =
                                    selectedDate
                                        .dayOfMonth
                                        .toString(),
                                options = dayOptions,
                                onSelected = {
                                    updateDateParts(
                                        day = it
                                    )
                                },
                                modifier =
                                    Modifier.fillMaxWidth()
                            )
                            CalendarDatePartSelector(
                                label = "Mes",
                                selectedValue =
                                    selectedDate
                                        .monthValue,
                                selectedLabel =
                                    selectedDate.format(
                                        monthFormatter
                                    ).replaceFirstChar {
                                        if (
                                            it.isLowerCase()
                                        ) {
                                            it.titlecase(
                                                Locale(
                                                    "es",
                                                    "MX"
                                                )
                                            )
                                        } else {
                                            it.toString()
                                        }
                                    },
                                options = monthOptions,
                                onSelected = {
                                    updateDateParts(
                                        month = it
                                    )
                                },
                                modifier =
                                    Modifier.fillMaxWidth()
                            )
                            CalendarDatePartSelector(
                                label = "Año",
                                selectedValue =
                                    selectedDate.year,
                                selectedLabel =
                                    selectedDate.year
                                        .toString(),
                                options = yearOptions,
                                onSelected = {
                                    updateDateParts(
                                        year = it
                                    )
                                },
                                modifier =
                                    Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    8.dp
                                )
                        ) {
                            CalendarDatePartSelector(
                                label = "Día",
                                selectedValue =
                                    selectedDate
                                        .dayOfMonth,
                                selectedLabel =
                                    selectedDate
                                        .dayOfMonth
                                        .toString(),
                                options = dayOptions,
                                onSelected = {
                                    updateDateParts(
                                        day = it
                                    )
                                },
                                modifier =
                                    Modifier.weight(0.72f)
                            )
                            CalendarDatePartSelector(
                                label = "Mes",
                                selectedValue =
                                    selectedDate
                                        .monthValue,
                                selectedLabel =
                                    selectedDate.format(
                                        monthFormatter
                                    ).replaceFirstChar {
                                        if (
                                            it.isLowerCase()
                                        ) {
                                            it.titlecase(
                                                Locale(
                                                    "es",
                                                    "MX"
                                                )
                                            )
                                        } else {
                                            it.toString()
                                        }
                                    },
                                options = monthOptions,
                                onSelected = {
                                    updateDateParts(
                                        month = it
                                    )
                                },
                                modifier =
                                    Modifier.weight(1.35f)
                            )
                            CalendarDatePartSelector(
                                label = "Año",
                                selectedValue =
                                    selectedDate.year,
                                selectedLabel =
                                    selectedDate.year
                                        .toString(),
                                options = yearOptions,
                                onSelected = {
                                    updateDateParts(
                                        year = it
                                    )
                                },
                                modifier =
                                    Modifier.weight(0.95f)
                            )
                        }
                    }
                }
            }
        }

        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val todayAllowed = (
                (
                    minDate == null ||
                        !today.isBefore(minDate)
                    ) &&
                    (
                        maxDate == null ||
                            !today.isAfter(maxDate)
                        )
                )

            if (maxWidth < 390.dp) {
                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    if (todayAllowed) {
                        AssistChip(
                            onClick = {
                                selectedDateText =
                                    today.toString()
                            },
                            label = {
                                Text("Hoy")
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default
                                        .CalendarMonth,
                                    contentDescription =
                                        null,
                                    modifier =
                                        Modifier.size(
                                            18.dp
                                        )
                                )
                            }
                        )
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                onConfirm(
                                    selectedDate
                                )
                            }
                        ) {
                            Text("Aceptar")
                        }
                    }
                }
            } else {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    if (todayAllowed) {
                        AssistChip(
                            onClick = {
                                selectedDateText =
                                    today.toString()
                            },
                            label = {
                                Text("Hoy")
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default
                                        .CalendarMonth,
                                    contentDescription =
                                        null,
                                    modifier =
                                        Modifier.size(
                                            18.dp
                                        )
                                )
                            }
                        )
                    }

                    Spacer(
                        Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = onDismiss
                    ) {
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val landscape =
                maxWidth > maxHeight
            val compactHeight =
                maxHeight < 520.dp
            val outerPadding =
                if (compactHeight) 5.dp
                else 14.dp
            val dialogMaxWidth =
                if (landscape) 1180.dp
                else 620.dp

            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(
                        max = dialogMaxWidth
                    )
                    .fillMaxWidth()
                    .fillMaxHeight(
                        if (compactHeight) {
                            0.99f
                        } else {
                            0.95f
                        }
                    )
                    .padding(outerPadding),
                shape = RoundedCornerShape(
                    if (compactHeight) 20.dp
                    else 28.dp
                ),
                color =
                    MaterialTheme.colorScheme
                        .surface,
                tonalElevation = 10.dp,
                shadowElevation = 14.dp
            ) {
                Column(
                    Modifier.fillMaxSize()
                ) {
                    Surface(
                        color =
                            MaterialTheme.colorScheme
                                .primaryContainer,
                        contentColor =
                            MaterialTheme.colorScheme
                                .onPrimaryContainer
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal =
                                        if (
                                            compactHeight
                                        ) {
                                            13.dp
                                        } else {
                                            20.dp
                                        },
                                    vertical =
                                        if (
                                            compactHeight
                                        ) {
                                            8.dp
                                        } else {
                                            16.dp
                                        }
                                ),
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    if (
                                        compactHeight
                                    ) {
                                        2.dp
                                    } else {
                                        5.dp
                                    }
                                )
                        ) {
                            Text(
                                title,
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelLarge,
                                fontWeight =
                                    FontWeight.Bold,
                                maxLines = 1,
                                overflow =
                                    TextOverflow
                                        .Ellipsis
                            )
                            Text(
                                formattedDate,
                                style = if (
                                    compactHeight
                                ) {
                                    MaterialTheme
                                        .typography
                                        .titleLarge
                                } else {
                                    MaterialTheme
                                        .typography
                                        .headlineSmall
                                },
                                fontWeight =
                                    FontWeight.Black,
                                maxLines = 1,
                                overflow =
                                    TextOverflow
                                        .Ellipsis
                            )
                        }
                    }

                    if (landscape) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(
                                    horizontal =
                                        if (
                                            compactHeight
                                        ) {
                                            6.dp
                                        } else {
                                            12.dp
                                        },
                                    vertical =
                                        if (
                                            compactHeight
                                        ) {
                                            4.dp
                                        } else {
                                            10.dp
                                        }
                                ),
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    if (
                                        compactHeight
                                    ) {
                                        6.dp
                                    } else {
                                        12.dp
                                    }
                                )
                        ) {
                            BoxWithConstraints(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment =
                                    Alignment.Center
                            ) {
                                val available =
                                    if (
                                        maxWidth <
                                            maxHeight
                                    ) {
                                        maxWidth
                                    } else {
                                        maxHeight
                                    }
                                val wheelSize =
                                    available
                                        .coerceAtMost(
                                            430.dp
                                        )
                                        .coerceAtLeast(
                                            155.dp
                                        )

                                DateOrbitWheel(
                                    selectedDate =
                                        selectedDate,
                                    minDate = minDate,
                                    maxDate = maxDate,
                                    onDateChange = {
                                        selectedDateText =
                                            it.toString()
                                    },
                                    modifier =
                                        Modifier.size(
                                            wheelSize
                                        )
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1.08f)
                                    .fillMaxHeight()
                                    .verticalScroll(
                                        rememberScrollState()
                                    )
                                    .padding(
                                        end =
                                            if (
                                                compactHeight
                                            ) {
                                                4.dp
                                            } else {
                                                8.dp
                                            }
                                    ),
                                verticalArrangement =
                                    Arrangement.spacedBy(
                                        if (
                                            compactHeight
                                        ) {
                                            7.dp
                                        } else {
                                            12.dp
                                        }
                                    ),
                                content =
                                    controlsContent
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(
                                    rememberScrollState()
                                )
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    12.dp
                                )
                        ) {
                            BoxWithConstraints(
                                modifier =
                                    Modifier.fillMaxWidth(),
                                contentAlignment =
                                    Alignment.Center
                            ) {
                                val wheelSize =
                                    maxWidth
                                        .coerceAtMost(
                                            360.dp
                                        )
                                        .coerceAtLeast(
                                            220.dp
                                        )

                                DateOrbitWheel(
                                    selectedDate =
                                        selectedDate,
                                    minDate = minDate,
                                    maxDate = maxDate,
                                    onDateChange = {
                                        selectedDateText =
                                            it.toString()
                                    },
                                    modifier =
                                        Modifier.size(
                                            wheelSize
                                        )
                                )
                            }

                            controlsContent()
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
                                onClick = {
                                    draft = draft.copy(
                                        type = MedicationType.PEDIATRIC,
                                        isSpecialAdult = false
                                    )
                                },
                                label = { Text("Pediátrico") }
                            )
                        }
                    }
                    if (draft.type == MedicationType.ADULT) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = draft.isSpecialAdult,
                                    onCheckedChange = {
                                        draft = draft.copy(
                                            isSpecialAdult = it,
                                            isInteractiveDose = if (it) {
                                                draft.isInteractiveDose
                                            } else {
                                                false
                                            }
                                        )
                                    }
                                )
                                Column {
                                    Text("Medicamento especial")
                                    Text(
                                        "Activa el cálculo por kg de peso.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    item { FormTextField("Medicamento", draft.name, { draft = draft.copy(name = it) }) }
                    item { FormTextField("Presentación", draft.presentation, { draft = draft.copy(presentation = it) }) }
                    if (
                        draft.type == MedicationType.PEDIATRIC ||
                        draft.isSpecialAdult
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = 0.48f
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = draft.isInteractiveDose,
                                        onCheckedChange = { checked ->
                                            draft = draft.copy(
                                                isInteractiveDose = checked,
                                                dosePerKgStep = if (
                                                    checked &&
                                                    draft.dosePerKgStep.isBlank()
                                                ) {
                                                    "0.1"
                                                } else {
                                                    draft.dosePerKgStep
                                                }
                                            )
                                        }
                                    )
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            "Dosis interactiva por rango",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Permite elegir la dosis dentro del rango directamente en la tabla.",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FormTextField(
                                    if (draft.isInteractiveDose) {
                                        "Dosis inicial por kg"
                                    } else {
                                        "Dosis por kg"
                                    },
                                    draft.dosePerKg,
                                    {
                                        draft = draft.copy(
                                            dosePerKg = decimalText(it)
                                        )
                                    },
                                    Modifier.weight(1f),
                                    KeyboardType.Decimal
                                )
                                FormTextField(
                                    "Unidad",
                                    draft.doseUnit,
                                    {
                                        draft = draft.copy(
                                            doseUnit = it
                                        )
                                    },
                                    Modifier.weight(1f)
                                )
                            }
                        }

                        if (draft.isInteractiveDose) {
                            item {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FormTextField(
                                        "Dosis mínima",
                                        draft.dosePerKgMin,
                                        {
                                            draft = draft.copy(
                                                dosePerKgMin = decimalText(it)
                                            )
                                        },
                                        Modifier.weight(1f),
                                        KeyboardType.Decimal
                                    )
                                    FormTextField(
                                        "Dosis máxima",
                                        draft.dosePerKgMax,
                                        {
                                            draft = draft.copy(
                                                dosePerKgMax = decimalText(it)
                                            )
                                        },
                                        Modifier.weight(1f),
                                        KeyboardType.Decimal
                                    )
                                }
                            }
                            item {
                                FormTextField(
                                    "Incremento de la rueda",
                                    draft.dosePerKgStep,
                                    {
                                        draft = draft.copy(
                                            dosePerKgStep = decimalText(it)
                                        )
                                    },
                                    keyboardType = KeyboardType.Decimal,
                                    placeholder = "Ej. 0.1"
                                )
                                Text(
                                    "En la tabla podrás deslizar hacia arriba o abajo, o usar las flechas, para elegir el valor.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        item {
                            FormTextField(
                                "Descripción de dosis",
                                draft.dose,
                                {
                                    draft = draft.copy(
                                        dose = it
                                    )
                                },
                                placeholder = if (
                                    draft.isInteractiveDose
                                ) {
                                    "Ej. Rango individualizable por peso"
                                } else {
                                    "Ej. Dosis calculada por peso"
                                }
                            )
                        }
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
    var expanded by remember {
        mutableStateOf(false)
    }
    var fieldHasFocus by remember {
        mutableStateOf(false)
    }

    val cleanOptions = remember(options) {
        options
            .map(String::trim)
            .filter(String::isNotBlank)
            .distinctBy {
                it.lowercase(Locale.ROOT)
            }
            .sortedBy {
                it.lowercase(Locale.ROOT)
            }
    }

    val visibleOptions = remember(
        value,
        cleanOptions
    ) {
        val query = value.trim()

        cleanOptions
            .filter {
                query.isBlank() ||
                    it.contains(
                        query,
                        ignoreCase = true
                    )
            }
            .sortedWith(
                compareBy<String> {
                    if (
                        it.equals(
                            query,
                            ignoreCase = true
                        )
                    ) {
                        0
                    } else {
                        1
                    }
                }.thenBy {
                    it.lowercase(Locale.ROOT)
                }
            )
            .take(12)
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValue(it)

                if (fieldHasFocus) {
                    expanded =
                        cleanOptions.isNotEmpty()
                }
            },
            label = {
                Text(label)
            },
            placeholder = {
                if (placeholder.isNotBlank()) {
                    Text(placeholder)
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (
                            cleanOptions.isNotEmpty()
                        ) {
                            expanded = !expanded
                        }
                    }
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.ArrowDropDown,
                        contentDescription =
                            "Mostrar opciones anteriores",
                        tint = if (
                            cleanOptions.isEmpty()
                        ) {
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                                .copy(alpha = 0.38f)
                        } else {
                            MaterialTheme
                                .colorScheme
                                .primary
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
                    color = MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    fieldHasFocus =
                        focusState.isFocused

                    if (!focusState.isFocused) {
                        expanded = false
                    }
                }
        )

        DropdownMenu(
            expanded = expanded &&
                cleanOptions.isNotEmpty(),
            onDismissRequest = {
                expanded = false
            },
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            modifier = Modifier
                .widthIn(
                    min = 280.dp,
                    max = 520.dp
                )
                .background(
                    MaterialTheme
                        .colorScheme
                        .surfaceContainer
                )
        ) {
            if (visibleOptions.isEmpty()) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 14.dp
                    )
                ) {
                    Text(
                        "Sin coincidencias",
                        fontWeight =
                            FontWeight.Bold,
                        color = MaterialTheme
                            .colorScheme
                            .onSurface
                    )
                    Text(
                        "Continúa escribiendo para guardar una opción nueva.",
                        style = MaterialTheme
                            .typography
                            .bodySmall,
                        color = MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                    )
                }
            } else {
                visibleOptions.forEachIndexed {
                        index,
                        option ->

                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    option,
                                    fontWeight = if (
                                        option.equals(
                                            value.trim(),
                                            ignoreCase = true
                                        )
                                    ) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.SemiBold
                                    }
                                )
                                Text(
                                    "Usado anteriormente",
                                    style = MaterialTheme
                                        .typography
                                        .labelSmall,
                                    color = MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                                )
                            }
                        },
                        leadingIcon = {
                            Surface(
                                shape =
                                    RoundedCornerShape(
                                        10.dp
                                    ),
                                color = MaterialTheme
                                    .colorScheme
                                    .secondaryContainer
                            ) {
                                Icon(
                                    imageVector =
                                        Icons.Default.History,
                                    contentDescription = null,
                                    tint = MaterialTheme
                                        .colorScheme
                                        .onSecondaryContainer,
                                    modifier = Modifier
                                        .padding(7.dp)
                                        .size(18.dp)
                                )
                            }
                        },
                        onClick = {
                            onValue(option)
                            expanded = false
                        }
                    )

                    if (
                        index <
                        visibleOptions.lastIndex
                    ) {
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(
                                    horizontal = 12.dp
                                ),
                            color = MaterialTheme
                                .colorScheme
                                .outlineVariant
                                .copy(alpha = 0.55f)
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
    var search by rememberSaveable {
        mutableStateOf("")
    }
    var working by remember(selected) {
        mutableStateOf(selected)
    }

    val visible = remember(
        all,
        search
    ) {
        all.filter {
            it.contains(
                search,
                ignoreCase = true
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val compactHeight =
                maxHeight < 520.dp
            val landscape =
                maxWidth > maxHeight

            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(
                        max =
                            if (landscape) {
                                820.dp
                            } else {
                                580.dp
                            }
                    )
                    .fillMaxWidth()
                    .fillMaxHeight(
                        if (compactHeight) {
                            0.97f
                        } else {
                            0.82f
                        }
                    )
                    .padding(
                        if (compactHeight) 5.dp
                        else 16.dp
                    ),
                shape = RoundedCornerShape(
                    if (compactHeight) 18.dp
                    else 24.dp
                ),
                color =
                    MaterialTheme.colorScheme
                        .surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    Modifier.fillMaxSize()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer
                            )
                            .padding(
                                if (compactHeight) {
                                    9.dp
                                } else {
                                    14.dp
                                }
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Text(
                            title,
                            modifier =
                                Modifier.weight(1f),
                            style = if (
                                compactHeight
                            ) {
                                MaterialTheme
                                    .typography
                                    .titleMedium
                            } else {
                                MaterialTheme
                                    .typography
                                    .titleLarge
                            },
                            fontWeight =
                                FontWeight.Black,
                            maxLines = 1,
                            overflow =
                                TextOverflow.Ellipsis
                        )
                        IconButton(
                            onClick = onDismiss
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription =
                                    "Cerrar"
                            )
                        }
                    }

                    OutlinedTextField(
                        value = search,
                        onValueChange = {
                            search = it
                        },
                        label = {
                            Text(
                                "Buscar especialidad"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal =
                                    if (
                                        compactHeight
                                    ) {
                                        10.dp
                                    } else {
                                        16.dp
                                    },
                                vertical = 8.dp
                            ),
                        singleLine = true
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding =
                            PaddingValues(
                                horizontal =
                                    if (
                                        compactHeight
                                    ) {
                                        10.dp
                                    } else {
                                        16.dp
                                    },
                                vertical = 4.dp
                            )
                    ) {
                        itemsIndexed(
                            items = visible,
                            key = {
                                    _,
                                    specialty ->
                                specialty
                            }
                        ) {
                                _,
                                specialty ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        working =
                                            if (
                                                specialty
                                                    in working
                                            ) {
                                                working -
                                                    specialty
                                            } else {
                                                working +
                                                    specialty
                                            }
                                    }
                                    .padding(
                                        vertical = 4.dp
                                    ),
                                verticalAlignment =
                                    Alignment
                                        .CenterVertically
                            ) {
                                Checkbox(
                                    checked =
                                        specialty
                                            in working,
                                    onCheckedChange = {
                                            checked ->
                                        working =
                                            if (checked) {
                                                working +
                                                    specialty
                                            } else {
                                                working -
                                                    specialty
                                            }
                                    }
                                )
                                Text(
                                    specialty,
                                    modifier =
                                        Modifier.weight(
                                            1f
                                        )
                                )
                            }
                        }
                    }

                    HorizontalDivider()
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                if (compactHeight) {
                                    8.dp
                                } else {
                                    12.dp
                                }
                            ),
                        horizontalArrangement =
                            Arrangement.End,
                        verticalArrangement =
                            Arrangement.spacedBy(
                                4.dp
                            )
                    ) {
                        TextButton(
                            onClick = {
                                working =
                                    emptySet()
                            }
                        ) {
                            Text("Limpiar")
                        }
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                onApply(working)
                            }
                        ) {
                            Text(
                                "Aceptar (${working.size})"
                            )
                        }
                    }
                }
            }
        }
    }
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
    TypeFilter.SPECIAL_ADULT -> "Especial"
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
