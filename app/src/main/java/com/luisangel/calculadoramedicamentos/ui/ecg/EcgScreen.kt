package com.luisangel.calculadoramedicamentos.ui.ecg

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

private enum class EcgTab(val label: String) {
    ANALYZER("Analizador"),
    RATE("FC/RR"),
    QTC("QTc"),
    AXIS("Eje"),
    LVH("HVI"),
    ST("ST")
}

@Composable
fun EcgScreen(modifier: Modifier = Modifier) {
    var selectedTabName by rememberSaveable { mutableStateOf(EcgTab.ANALYZER.name) }
    val selectedTab = remember(selectedTabName) { EcgTab.valueOf(selectedTabName) }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab.ordinal) {
            EcgTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTabName = tab.name },
                    text = {
                        Text(
                            tab.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        when (selectedTab) {
            EcgTab.ANALYZER -> EcgAnalyzerScreen(Modifier.fillMaxSize())
            EcgTab.RATE -> EcgRateScreen(Modifier.fillMaxSize())
            EcgTab.QTC -> QtcCalculatorScreen(Modifier.fillMaxSize())
            EcgTab.AXIS -> AxisCalculatorScreen(Modifier.fillMaxSize())
            EcgTab.LVH -> LvhCalculatorScreen(Modifier.fillMaxSize())
            EcgTab.ST -> StElevationScreen(Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EcgAnalyzerScreen(modifier: Modifier = Modifier) {
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

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { EcgHeaderCard() }
        item {
            EcgCard(
                title = "Datos básicos",
                note = "Captura los datos medidos en el ECG. La app calcula y redacta una interpretación preliminar, no una sentencia divina tallada en piedra."
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
        }
        item {
            EcgCard(
                title = "Hallazgos opcionales",
                note = "Agrega criterios de ST o HVI cuando tengas los datos medidos."
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
        }
        item {
            EcgCard(
                title = "Resumen generado",
                note = "Puedes copiarlo para nota clínica, pase o comentario rápido."
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        summary,
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
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
    }
}

@Composable
private fun EcgRateScreen(modifier: Modifier = Modifier) {
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

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
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
        }
        item {
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
        }
        item {
            EcgCard("Calcular RR desde FC", "Útil para QTc y para revisar coherencia de mediciones.") {
                NumericField("Frecuencia cardiaca", heartRateText, { heartRateText = it.decimalInput() }, suffix = "lpm")
                rrFromHr?.let {
                    ResultGrid(listOf("RR" to "${it.roundClean()} ms", "RR" to "${(it / 1000.0).format(3)} s"))
                }
            }
        }
    }
}

@Composable
private fun QtcCalculatorScreen(modifier: Modifier = Modifier) {
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

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
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
    }
}

@Composable
private fun AxisCalculatorScreen(modifier: Modifier = Modifier) {
    var methodName by rememberSaveable { mutableStateOf(EcgAxisMethod.LEAD_I_AVF.name) }
    val method = EcgAxisMethod.valueOf(methodName)
    var firstText by rememberSaveable { mutableStateOf("") }
    var secondText by rememberSaveable { mutableStateOf("") }

    val firstLabel = if (method == EcgAxisMethod.LEAD_I_AVF) "QRS neto en DI" else "QRS neto en DI"
    val secondLabel = if (method == EcgAxisMethod.LEAD_I_AVF) "QRS neto en aVF" else "QRS neto en DIII"
    val result = firstText.toDecimalOrNull()?.let { first ->
        secondText.toDecimalOrNull()?.let { second -> EcgCalculator.calculateAxis(method, first, second) }
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            EcgCard(
                title = "Eje eléctrico",
                note = "Introduce amplitudes netas del QRS en mm: R positiva menos Q/S negativas. El cálculo con DI+aVF es el más práctico; DI+DIII replica la idea de calculadoras clásicas como My EKG."
            ) {
                EnumDropdown("Método", methodName, { methodName = it }, EcgAxisMethod.entries.associate { it.name to it.label })
                ResponsiveFields {
                    NumericField(firstLabel, firstText, { firstText = it.signedDecimalInput() }, suffix = "mm")
                    NumericField(secondLabel, secondText, { secondText = it.signedDecimalInput() }, suffix = "mm")
                }
                result?.let { AxisResultCard(it) }
            }
        }
    }
}

@Composable
private fun LvhCalculatorScreen(modifier: Modifier = Modifier) {
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

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
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
    }
}

@Composable
private fun StElevationScreen(modifier: Modifier = Modifier) {
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

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
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
    }
}

@Composable
private fun EcgHeaderCard() {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Text(
                    "Analizador de ECG",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
            }
            Text(
                "Módulo de apoyo para frecuencia, RR, QTc, eje eléctrico, criterios de HVI y elevación del ST. No interpreta imágenes del trazo todavía: aquí se capturan mediciones, como adultos responsables con regla y criterio clínico.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
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
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
            Text(
                note,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
        Icon(
            Icons.Default.WarningAmber,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary
        )
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
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    options[selectedKey].orEmpty(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
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
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        value,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
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
        Icon(
            Icons.Default.WarningAmber,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun String.toDecimalOrNull(): Double? =
    replace(',', '.').toDoubleOrNull()

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
