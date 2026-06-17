package com.luisangel.calculadoramedicamentos.io

import com.luisangel.calculadoramedicamentos.model.FilterState
import com.luisangel.calculadoramedicamentos.model.MedicationRecord
import com.luisangel.calculadoramedicamentos.model.MedicationType
import com.luisangel.calculadoramedicamentos.model.SortOption
import com.luisangel.calculadoramedicamentos.model.TypeFilter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.OutputStream
import java.io.InputStreamReader
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


data class ExportRequest(
    val all: List<MedicationRecord>,
    val visibleAdults: List<MedicationRecord>,
    val visiblePediatrics: List<MedicationRecord>,
    val filters: FilterState,
    val darkTheme: Boolean,
    val adultWeight: Double?,
    val pediatricWeight: Double?
)

data class ImportBundle(
    val records: List<MedicationRecord>,
    val filters: FilterState? = null,
    val darkTheme: Boolean? = null
)

class ExcelService {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val formatter = DataFormatter(Locale("es", "MX"))
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "MX"))

    fun export(request: ExportRequest, output: OutputStream) {
        XSSFWorkbook().use { workbook ->
            createMedicationSheet(
                workbook = workbook,
                name = "Adultos",
                title = "Calculadora de Medicamentos · Adultos",
                records = request.visibleAdults,
                weight = request.adultWeight,
                pediatric = false,
                tableColor = IndexedColors.DARK_BLUE
            )
            createMedicationSheet(
                workbook = workbook,
                name = "Pediátricos",
                title = "Calculadora de Medicamentos · Pediátricos",
                records = request.visiblePediatrics,
                weight = request.pediatricWeight,
                pediatric = true,
                tableColor = IndexedColors.TEAL
            )
            createConfigurationSheet(workbook, request.filters, request.darkTheme)
            createDatabaseSheet(workbook, request.all)
            workbook.write(output)
            output.flush()
        }
    }

    fun import(input: InputStream, fileName: String): ImportBundle {
        val extension = fileName.substringAfterLast('.', "").lowercase(Locale.ROOT)
        return when (extension) {
            "json" -> importJson(input)
            "csv" -> importCsv(input)
            else -> importWorkbook(input)
        }
    }

    private fun createMedicationSheet(
        workbook: Workbook,
        name: String,
        title: String,
        records: List<MedicationRecord>,
        weight: Double?,
        pediatric: Boolean,
        tableColor: IndexedColors
    ) {
        val sheet = workbook.createSheet(name)
        val headers = listOf(
            "Medicamento",
            "Presentación",
            "Tipo",
            "Dosis",
            "Dosis por kg",
            "Dosis interactiva",
            "Dosis mínima por kg",
            "Dosis máxima por kg",
            "Paso de dosis",
            "Unidad",
            "Dosis calculada",
            "Tiempo de uso por día",
            "Tiempo de uso por días",
            "Familia",
            "Subgrupo",
            "Especialidades",
            "Notas",
            "Fecha de alta",
            "Última actualización",
            "ID"
        )
        val styles = Styles(workbook, tableColor)

        sheet.createRow(0).apply {
            heightInPoints = 30f
            createCell(0).apply {
                setCellValue(title)
                cellStyle = styles.title
            }
        }
        sheet.addMergedRegion(org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, headers.lastIndex))

        sheet.createRow(1).apply {
            heightInPoints = 34f
            createCell(0).apply {
                val weightText = if (weight != null && weight > 0) {
                    "Cálculo mostrado con peso temporal: ${trimNumber(weight)} kg. El peso no se guarda en la aplicación."
                } else {
                    "Sin peso temporal capturado. Los medicamentos permanecen almacenados localmente."
                }
                setCellValue(weightText)
                cellStyle = styles.subtitle
            }
        }
        sheet.addMergedRegion(org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, headers.lastIndex))

        val headerRow = sheet.createRow(3)
        headers.forEachIndexed { index, header ->
            headerRow.createCell(index).apply {
                setCellValue(header)
                cellStyle = styles.header
            }
        }

        records.forEachIndexed { index, record ->
            val row = sheet.createRow(index + 4)
            val calculated = if (pediatric || record.isSpecialAdult) {
                record.dosePerKg?.takeIf { weight != null && weight > 0 }?.let {
                    "${trimNumber((weight ?: 0.0) * it)} ${record.doseUnit}"
                } ?: "Pendiente"
            } else {
                "No aplica"
            }
            val values = listOf(
                record.name,
                record.presentation,
                when {
                    record.type == MedicationType.PEDIATRIC -> "Pediátrico"
                    record.isSpecialAdult -> "Especial"
                    else -> "Adulto"
                },
                record.dose,
                record.dosePerKg?.let(::trimNumber).orEmpty(),
                if (record.isInteractiveDose) "Sí" else "No",
                record.dosePerKgMin?.let(::trimNumber).orEmpty(),
                record.dosePerKgMax?.let(::trimNumber).orEmpty(),
                record.dosePerKgStep?.let(::trimNumber).orEmpty(),
                record.doseUnit,
                calculated,
                record.frequencyPerDay,
                record.durationDays.toString(),
                record.family,
                record.subgroup,
                record.specialties.joinToString("; "),
                record.notes,
                dateFormat.format(Date(record.createdAt)),
                dateFormat.format(Date(record.updatedAt)),
                record.id
            )
            values.forEachIndexed { column, value ->
                row.createCell(column).apply {
                    setCellValue(value)
                    cellStyle = if (index % 2 == 0) styles.bodyEven else styles.bodyOdd
                }
            }
        }

        if (records.isEmpty()) {
            val row = sheet.createRow(4)
            row.createCell(0).apply {
                setCellValue("No hay medicamentos que coincidan con los filtros aplicados.")
                cellStyle = styles.empty
            }
            sheet.addMergedRegion(org.apache.poi.ss.util.CellRangeAddress(4, 4, 0, headers.lastIndex))
        }

        sheet.setAutoFilter(org.apache.poi.ss.util.CellRangeAddress(3, maxOf(3, records.size + 3), 0, headers.lastIndex))
        sheet.createFreezePane(0, 4)
        sheet.isDisplayGridlines = false
        val widths = intArrayOf(
            24, 30, 18, 22, 14, 18, 18, 18, 15, 10,
            18, 24, 18, 22, 22, 36, 42, 20, 20, 34
        )
        widths.forEachIndexed { index, width -> sheet.setColumnWidth(index, width * 256) }
    }

    private fun createConfigurationSheet(workbook: Workbook, filters: FilterState, darkTheme: Boolean) {
        val sheet = workbook.createSheet("Configuración")
        val styles = Styles(workbook, IndexedColors.ORANGE)
        val rows = listOf(
            "Versión del formato" to "4",
            "Exportado el" to dateFormat.format(Date()),
            "Búsqueda" to filters.search,
            "Familia" to filters.family,
            "Subgrupo" to filters.subgroup,
            "Especialidades filtradas" to filters.specialties.sorted().joinToString("; "),
            "Apartado" to filters.type.name,
            "Ordenar por" to filters.sort.name,
            "Dirección" to if (filters.ascending) "ASC" else "DESC",
            "Tema" to if (darkTheme) "dark" else "light",
            "Almacenamiento" to "Base local Room dentro de la aplicación",
            "Datos de pacientes" to "No se almacenan"
        )
        sheet.createRow(0).apply {
            createCell(0).apply { setCellValue("Configuración"); cellStyle = styles.header }
            createCell(1).apply { setCellValue("Valor"); cellStyle = styles.header }
        }
        rows.forEachIndexed { index, pair ->
            sheet.createRow(index + 1).apply {
                createCell(0).apply { setCellValue(pair.first); cellStyle = if (index % 2 == 0) styles.bodyEven else styles.bodyOdd }
                createCell(1).apply { setCellValue(pair.second); cellStyle = if (index % 2 == 0) styles.bodyEven else styles.bodyOdd }
            }
        }
        sheet.setColumnWidth(0, 34 * 256)
        sheet.setColumnWidth(1, 64 * 256)
        sheet.createFreezePane(0, 1)
        sheet.isDisplayGridlines = false
    }

    private fun createDatabaseSheet(workbook: Workbook, records: List<MedicationRecord>) {
        val sheet = workbook.createSheet("BaseDatos")
        val headers = listOf(
            "ID", "Tipo", "Especial", "Medicamento", "Presentación", "Dosis",
            "Dosis por kg", "Dosis interactiva", "Dosis mínima por kg",
            "Dosis máxima por kg", "Paso de dosis", "Unidad",
            "Tiempo de uso por día", "Tiempo de uso por días",
            "Familia", "Subgrupo", "Especialidades", "Notas",
            "Fecha de alta", "Última actualización"
        )
        val styles = Styles(workbook, IndexedColors.GREY_80_PERCENT)
        sheet.createRow(0).also { row ->
            headers.forEachIndexed { index, header ->
                row.createCell(index).apply { setCellValue(header); cellStyle = styles.header }
            }
        }
        records.forEachIndexed { index, record ->
            val values = listOf(
                record.id,
                if (record.type == MedicationType.PEDIATRIC) "Pediátrico" else "Adulto",
                if (record.isSpecialAdult) "Sí" else "No",
                record.name,
                record.presentation,
                record.dose,
                record.dosePerKg?.let(::trimNumber).orEmpty(),
                if (record.isInteractiveDose) "Sí" else "No",
                record.dosePerKgMin?.let(::trimNumber).orEmpty(),
                record.dosePerKgMax?.let(::trimNumber).orEmpty(),
                record.dosePerKgStep?.let(::trimNumber).orEmpty(),
                record.doseUnit,
                record.frequencyPerDay,
                record.durationDays.toString(),
                record.family,
                record.subgroup,
                record.specialties.joinToString("; "),
                record.notes,
                record.createdAt.toString(),
                record.updatedAt.toString()
            )
            sheet.createRow(index + 1).also { row ->
                values.forEachIndexed { column, value -> row.createCell(column).setCellValue(value) }
            }
        }
        sheet.setAutoFilter(org.apache.poi.ss.util.CellRangeAddress(0, maxOf(0, records.size), 0, headers.lastIndex))
        workbook.setSheetHidden(workbook.getSheetIndex(sheet), true)
    }

    private fun importWorkbook(input: InputStream): ImportBundle {
        BufferedInputStream(input).use { buffered ->
            WorkbookFactory.create(buffered).use { workbook ->
                val databaseSheet = workbook.getSheet("BaseDatos")
                val records = if (databaseSheet != null) {
                    parseMedicationSheet(databaseSheet)
                } else {
                    workbook.sheetIterator().asSequence()
                        .filterNot { normalize(it.sheetName) == "configuracion" }
                        .flatMap { sheet -> parseMedicationSheet(sheet).asSequence() }
                        .toList()
                }
                val config = workbook.getSheet("Configuración")?.let(::parseConfiguration)
                return ImportBundle(
                    records = records,
                    filters = config?.first,
                    darkTheme = config?.second
                )
            }
        }
    }

    private fun parseMedicationSheet(sheet: Sheet): List<MedicationRecord> {
        val headerIndex = (sheet.firstRowNum..sheet.lastRowNum).firstOrNull { rowIndex ->
            val row = sheet.getRow(rowIndex) ?: return@firstOrNull false
            row.any { normalize(formatCell(it)) == "medicamento" }
        } ?: return emptyList()

        val headerRow = sheet.getRow(headerIndex)
        val headers = buildMap {
            headerRow.forEach { cell -> put(normalize(formatCell(cell)), cell.columnIndex) }
        }
        val fallbackType = if (normalize(sheet.sheetName).contains("pediatric")) MedicationType.PEDIATRIC else MedicationType.ADULT

        return (headerIndex + 1..sheet.lastRowNum).mapNotNull { rowIndex ->
            val row = sheet.getRow(rowIndex) ?: return@mapNotNull null
            val name = value(row, headers, "medicamento", "nombre", "nombre del medicamento")
            if (name.isBlank()) return@mapNotNull null

            val typeText = value(row, headers, "tipo", "apartado", "paciente")
            val type = if (normalize(typeText).contains("pedi")) MedicationType.PEDIATRIC else fallbackType
            val specialText = value(row, headers, "adulto especial", "especial", "es especial")
            val isSpecial = type == MedicationType.ADULT && (
                normalize(specialText) in setOf("si", "true", "1", "adulto especial") ||
                normalize(typeText).contains("especial")
            )
            val now = System.currentTimeMillis()

            MedicationRecord(
                id = value(row, headers, "id", "identificador", "folio").ifBlank { UUID.randomUUID().toString() },
                type = type,
                isSpecialAdult = isSpecial,
                name = name.trim(),
                presentation = value(row, headers, "presentacion", "presentacion del medicamento").trim(),
                dose = value(row, headers, "dosis", "dosis del medicamento").trim(),
                dosePerKg = value(
                    row,
                    headers,
                    "dosis por kg",
                    "dosis mg kg",
                    "dosis por kilogramo"
                ).replace(',', '.').toDoubleOrNull(),
                isInteractiveDose = normalize(
                    value(
                        row,
                        headers,
                        "dosis interactiva",
                        "es dosis interactiva",
                        "rango interactivo"
                    )
                ) in setOf("si", "true", "1"),
                dosePerKgMin = value(
                    row,
                    headers,
                    "dosis minima por kg",
                    "dosis minima",
                    "minimo por kg"
                ).replace(',', '.').toDoubleOrNull(),
                dosePerKgMax = value(
                    row,
                    headers,
                    "dosis maxima por kg",
                    "dosis maxima",
                    "maximo por kg"
                ).replace(',', '.').toDoubleOrNull(),
                dosePerKgStep = value(
                    row,
                    headers,
                    "paso de dosis",
                    "incremento de dosis",
                    "paso"
                ).replace(',', '.').toDoubleOrNull(),
                doseUnit = value(row, headers, "unidad", "unidad de dosis").ifBlank { "mg" }.trim(),
                frequencyPerDay = value(row, headers, "tiempo de uso por dia", "uso por dia", "frecuencia").trim(),
                durationDays = value(row, headers, "tiempo de uso por dias", "uso por dias", "duracion")
                    .replace(',', '.').toDoubleOrNull()?.toInt()?.coerceAtLeast(1) ?: 1,
                family = value(row, headers, "familia", "familia del medicamento", "grupo").ifBlank { "Sin clasificar" }.trim(),
                subgroup = value(row, headers, "subgrupo", "subgrupo del medicamento").trim(),
                specialties = value(row, headers, "especialidades", "especialidad", "especialidades medicas")
                    .split(';', ',', '|').map(String::trim).filter(String::isNotBlank).distinct().sorted(),
                notes = value(row, headers, "notas", "observaciones", "comentarios").trim(),
                createdAt = value(row, headers, "fecha de alta", "creado", "createdat").toLongOrNull() ?: now,
                updatedAt = value(row, headers, "ultima actualizacion", "actualizado", "updatedat").toLongOrNull() ?: now
            )
        }
    }

    private fun parseConfiguration(sheet: Sheet): Pair<FilterState, Boolean?> {
        val values = mutableMapOf<String, String>()
        for (index in sheet.firstRowNum..sheet.lastRowNum) {
            val row = sheet.getRow(index) ?: continue
            val key = normalize(formatCell(row.getCell(0)))
            if (key.isNotBlank()) values[key] = formatCell(row.getCell(1))
        }
        val filter = FilterState(
            search = values["busqueda"].orEmpty(),
            family = values["familia"].orEmpty(),
            subgroup = values["subgrupo"].orEmpty(),
            specialties = values["especialidades filtradas"].orEmpty()
                .split(';').map(String::trim).filter(String::isNotBlank).toSet(),
            type = runCatching { TypeFilter.valueOf(values["apartado"].orEmpty()) }.getOrDefault(TypeFilter.BOTH),
            sort = runCatching { SortOption.valueOf(values["ordenar por"].orEmpty()) }.getOrDefault(SortOption.NAME),
            ascending = values["direccion"].orEmpty().uppercase(Locale.ROOT) != "DESC"
        )
        val theme = when (values["tema"]?.lowercase(Locale.ROOT)) {
            "dark" -> true
            "light" -> false
            else -> null
        }
        return filter to theme
    }

    private fun importJson(input: InputStream): ImportBundle {
        val root = json.parseToJsonElement(input.bufferedReader().readText())
        val array = when (root) {
            is JsonArray -> root
            is JsonObject -> root["medicines"]?.jsonArray ?: JsonArray(emptyList())
            else -> JsonArray(emptyList())
        }
        val records = array.mapNotNull { element -> parseJsonMedication(element) }
        return ImportBundle(records)
    }

    private fun parseJsonMedication(element: JsonElement): MedicationRecord? {
        val obj = element as? JsonObject ?: return null
        fun text(vararg keys: String): String = keys.firstNotNullOfOrNull { key -> obj[key]?.jsonPrimitive?.contentOrNull }.orEmpty()
        val name = text("name", "medicamento", "nombre")
        if (name.isBlank()) return null
        val typeText = text("type", "tipo")
        val type = if (normalize(typeText).contains("pedi")) MedicationType.PEDIATRIC else MedicationType.ADULT
        val specialties = when (val node = obj["specialties"] ?: obj["especialidades"]) {
            is JsonArray -> node.mapNotNull { it.jsonPrimitive.contentOrNull }
            else -> node?.jsonPrimitive?.contentOrNull.orEmpty().split(';', ',', '|')
        }.map(String::trim).filter(String::isNotBlank).distinct().sorted()
        val now = System.currentTimeMillis()
        return MedicationRecord(
            id = text("id").ifBlank { UUID.randomUUID().toString() },
            type = type,
            isSpecialAdult = obj["isSpecialAdult"]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull()
                ?: obj["adultSpecial"]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull()
                ?: false,
            name = name,
            presentation = text("presentation", "presentacion"),
            dose = text("dose", "dosis"),
            dosePerKg = obj["dosePerKg"]?.jsonPrimitive?.doubleOrNull
                ?: text("dosisPorKg").replace(',', '.').toDoubleOrNull(),
            isInteractiveDose = obj["isInteractiveDose"]
                ?.jsonPrimitive
                ?.contentOrNull
                ?.toBooleanStrictOrNull()
                ?: text(
                    "dosisInteractiva",
                    "interactiveDose"
                ).toBooleanStrictOrNull()
                ?: false,
            dosePerKgMin = obj["dosePerKgMin"]
                ?.jsonPrimitive
                ?.doubleOrNull
                ?: text("dosisMinimaPorKg")
                    .replace(',', '.')
                    .toDoubleOrNull(),
            dosePerKgMax = obj["dosePerKgMax"]
                ?.jsonPrimitive
                ?.doubleOrNull
                ?: text("dosisMaximaPorKg")
                    .replace(',', '.')
                    .toDoubleOrNull(),
            dosePerKgStep = obj["dosePerKgStep"]
                ?.jsonPrimitive
                ?.doubleOrNull
                ?: text("pasoDeDosis")
                    .replace(',', '.')
                    .toDoubleOrNull(),
            doseUnit = text("doseUnit", "unidad").ifBlank { "mg" },
            frequencyPerDay = text("frequencyPerDay", "frecuencia"),
            durationDays = obj["durationDays"]?.jsonPrimitive?.intOrNull
                ?: text("duracionDias").toIntOrNull() ?: 1,
            family = text("family", "familia").ifBlank { "Sin clasificar" },
            subgroup = text("subgroup", "subgrupo"),
            specialties = specialties,
            notes = text("notes", "notas"),
            createdAt = text("createdAt").toLongOrNull() ?: now,
            updatedAt = text("updatedAt").toLongOrNull() ?: now
        )
    }

    private fun importCsv(input: InputStream): ImportBundle {
        val lines = BufferedReader(InputStreamReader(input)).readLines().filter(String::isNotBlank)
        if (lines.isEmpty()) return ImportBundle(emptyList())
        val delimiter = if (lines.first().count { it == ';' } > lines.first().count { it == ',' }) ';' else ','
        val headers = parseCsvLine(lines.first(), delimiter).map(::normalize)
        val records = lines.drop(1).mapNotNull { line ->
            val cells = parseCsvLine(line, delimiter)
            val map = headers.mapIndexed { index, key -> key to cells.getOrElse(index) { "" } }.toMap()
            val name = map["medicamento"].orEmpty().ifBlank { map["nombre"].orEmpty() }
            if (name.isBlank()) return@mapNotNull null
            val type = if (normalize(map["tipo"].orEmpty()).contains("pedi")) MedicationType.PEDIATRIC else MedicationType.ADULT
            val now = System.currentTimeMillis()
            MedicationRecord(
                id = map["id"].orEmpty().ifBlank { UUID.randomUUID().toString() },
                type = type,
                isSpecialAdult = normalize(map["adulto especial"].orEmpty()) in setOf("si", "true", "1"),
                name = name,
                presentation = map["presentacion"].orEmpty(),
                dose = map["dosis"].orEmpty(),
                dosePerKg = map["dosis por kg"]
                    .orEmpty()
                    .replace(',', '.')
                    .toDoubleOrNull(),
                isInteractiveDose = normalize(
                    map["dosis interactiva"].orEmpty()
                ) in setOf("si", "true", "1"),
                dosePerKgMin = map["dosis minima por kg"]
                    .orEmpty()
                    .replace(',', '.')
                    .toDoubleOrNull(),
                dosePerKgMax = map["dosis maxima por kg"]
                    .orEmpty()
                    .replace(',', '.')
                    .toDoubleOrNull(),
                dosePerKgStep = map["paso de dosis"]
                    .orEmpty()
                    .replace(',', '.')
                    .toDoubleOrNull(),
                doseUnit = map["unidad"].orEmpty().ifBlank { "mg" },
                frequencyPerDay = map["tiempo de uso por dia"].orEmpty().ifBlank { map["frecuencia"].orEmpty() },
                durationDays = map["tiempo de uso por dias"].orEmpty().filter(Char::isDigit).toIntOrNull() ?: 1,
                family = map["familia"].orEmpty().ifBlank { "Sin clasificar" },
                subgroup = map["subgrupo"].orEmpty(),
                specialties = map["especialidades"].orEmpty().split(';', '|').map(String::trim).filter(String::isNotBlank),
                notes = map["notas"].orEmpty(),
                createdAt = now,
                updatedAt = now
            )
        }
        return ImportBundle(records)
    }

    private fun parseCsvLine(line: String, delimiter: Char): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var quoted = false
        var index = 0
        while (index < line.length) {
            val char = line[index]
            when {
                char == '"' && quoted && index + 1 < line.length && line[index + 1] == '"' -> {
                    current.append('"'); index++
                }
                char == '"' -> quoted = !quoted
                char == delimiter && !quoted -> { result += current.toString(); current.clear() }
                else -> current.append(char)
            }
            index++
        }
        result += current.toString()
        return result
    }

    private fun value(row: Row, headers: Map<String, Int>, vararg aliases: String): String {
        val index = aliases.firstNotNullOfOrNull { headers[normalize(it)] } ?: return ""
        return formatCell(row.getCell(index))
    }

    private fun formatCell(cell: Cell?): String {
        if (cell == null) return ""
        return when (cell.cellType) {
            CellType.FORMULA -> formatter.formatCellValue(cell)
            else -> formatter.formatCellValue(cell)
        }.trim()
    }

    private fun normalize(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace("\\p{M}+".toRegex(), "")
        .lowercase(Locale.ROOT)
        .replace("[^a-z0-9]+".toRegex(), " ")
        .trim()

    private fun trimNumber(value: Double): String = java.math.BigDecimal.valueOf(value)
        .stripTrailingZeros().toPlainString()

    private class Styles(workbook: Workbook, accent: IndexedColors) {
        val title: CellStyle
        val subtitle: CellStyle
        val header: CellStyle
        val bodyEven: CellStyle
        val bodyOdd: CellStyle
        val empty: CellStyle

        init {
            val titleFont = workbook.createFont().apply {
                bold = true
                fontHeightInPoints = 18
                color = IndexedColors.WHITE.index
            }
            val headerFont = workbook.createFont().apply {
                bold = true
                color = IndexedColors.WHITE.index
            }
            val normalFont = workbook.createFont().apply { fontHeightInPoints = 10 }
            val italicFont = workbook.createFont().apply { italic = true; color = IndexedColors.GREY_80_PERCENT.index }

            title = workbook.createCellStyle().apply {
                setFillForegroundColor(IndexedColors.DARK_BLUE.index)
                fillPattern = FillPatternType.SOLID_FOREGROUND
                alignment = HorizontalAlignment.CENTER
                verticalAlignment = VerticalAlignment.CENTER
                setFont(titleFont)
            }
            subtitle = workbook.createCellStyle().apply {
                setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index)
                fillPattern = FillPatternType.SOLID_FOREGROUND
                verticalAlignment = VerticalAlignment.CENTER
                wrapText = true
                setFont(italicFont)
            }
            header = workbook.createCellStyle().apply {
                setFillForegroundColor(accent.index)
                fillPattern = FillPatternType.SOLID_FOREGROUND
                alignment = HorizontalAlignment.CENTER
                verticalAlignment = VerticalAlignment.CENTER
                wrapText = true
                setFont(headerFont)
                addBorders(this)
            }
            bodyEven = workbook.createCellStyle().apply {
                setFillForegroundColor(IndexedColors.WHITE.index)
                fillPattern = FillPatternType.SOLID_FOREGROUND
                verticalAlignment = VerticalAlignment.TOP
                wrapText = true
                setFont(normalFont)
                addBorders(this)
            }
            bodyOdd = workbook.createCellStyle().apply {
                setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index)
                fillPattern = FillPatternType.SOLID_FOREGROUND
                verticalAlignment = VerticalAlignment.TOP
                wrapText = true
                setFont(normalFont)
                addBorders(this)
            }
            empty = workbook.createCellStyle().apply {
                alignment = HorizontalAlignment.CENTER
                verticalAlignment = VerticalAlignment.CENTER
                setFont(italicFont)
            }
        }

        private fun addBorders(style: CellStyle) {
            style.borderTop = BorderStyle.THIN
            style.borderBottom = BorderStyle.THIN
            style.borderLeft = BorderStyle.THIN
            style.borderRight = BorderStyle.THIN
            style.topBorderColor = IndexedColors.GREY_40_PERCENT.index
            style.bottomBorderColor = IndexedColors.GREY_40_PERCENT.index
            style.leftBorderColor = IndexedColors.GREY_40_PERCENT.index
            style.rightBorderColor = IndexedColors.GREY_40_PERCENT.index
        }
    }
}
