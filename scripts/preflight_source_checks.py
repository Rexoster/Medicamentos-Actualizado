#!/usr/bin/env python3
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
APP_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/ui/App.kt"
GROWTH_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/growth/GrowthEngine.kt"
ULTRASOUND_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/obstetrics/UltrasoundDating.kt"
ULTRASOUND_TEST_PATH = ROOT / "app/src/test/java/com/luisangel/calculadoramedicamentos/obstetrics/UltrasoundDatingTest.kt"
RENAL_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/renal/RenalEngine.kt"
RENAL_TEST_PATH = ROOT / "app/src/test/java/com/luisangel/calculadoramedicamentos/renal/RenalEngineTest.kt"
DOSE_RANGE_TEST_PATH = ROOT / "app/src/test/java/com/luisangel/calculadoramedicamentos/model/MedicationDoseRangeTest.kt"
ENTITY_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/data/MedicationEntity.kt"
DATABASE_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/data/AppDatabase.kt"
APP_GRADLE_PATH = ROOT / "app/build.gradle.kts"
RELEASE_WORKFLOW_PATH = ROOT / ".github/workflows/build-release.yml"
DEBUG_WORKFLOW_PATH = ROOT / ".github/workflows/build-apk.yml"
MODELS_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/model/Models.kt"
EXCEL_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/io/ExcelService.kt"
MANIFEST_PATH = ROOT / "app/src/main/AndroidManifest.xml"
VERSION_PATH = ROOT / "version.properties"
VIEW_MODEL_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/ui/MainViewModel.kt"
MAIN_ACTIVITY_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/MainActivity.kt"
APPLICATION_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/CalculatorApplication.kt"
REPOSITORY_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/data/MedicationRepository.kt"
DIAL_SOUND_PATH = ROOT / "app/src/main/res/raw/combination_dial_tick.wav"
UPDATER_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/update/AppUpdateManager.kt"
UPDATE_SCREEN_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/ui/AppUpdateScreen.kt"
FILE_PATHS_PATH = ROOT / "app/src/main/res/xml/file_paths.xml"

errors = []


def require_file(path: Path) -> str:
    if not path.exists():
        errors.append(f"Falta archivo: {path.relative_to(ROOT)}")
        return ""
    return path.read_text(encoding="utf-8")


def require(text: str, token: str, source: str) -> None:
    if token not in text:
        errors.append(f"{source} no contiene: {token}")


def balanced(text: str, source: str) -> None:
    cleaned = re.sub(r"/\*.*?\*/", "", text, flags=re.S)
    cleaned = re.sub(r"//[^\n]*", "", cleaned)
    cleaned = re.sub(r'""".*?"""', '""', cleaned, flags=re.S)
    cleaned = re.sub(r'"(?:\\.|[^"\\])*"', '""', cleaned)
    cleaned = re.sub(r"'(?:\\.|[^'\\])'", "''", cleaned)

    pairs = {"{": "}", "(": ")", "[": "]"}
    reverse = {value: key for key, value in pairs.items()}
    stack = []

    for char in cleaned:
        if char in pairs:
            stack.append(char)
        elif char in reverse:
            if not stack or stack[-1] != reverse[char]:
                errors.append(f"{source}: delimitadores incompatibles.")
                return
            stack.pop()

    if stack:
        errors.append(f"{source}: hay delimitadores sin cerrar.")


app = require_file(APP_PATH)
growth = require_file(GROWTH_PATH)
ultrasound = require_file(ULTRASOUND_PATH)
ultrasound_test = require_file(ULTRASOUND_TEST_PATH)
renal = require_file(RENAL_PATH)
renal_test = require_file(RENAL_TEST_PATH)
dose_range_test = require_file(DOSE_RANGE_TEST_PATH)
entity = require_file(ENTITY_PATH)
database = require_file(DATABASE_PATH)
app_gradle = require_file(APP_GRADLE_PATH)
release_workflow = require_file(RELEASE_WORKFLOW_PATH)
debug_workflow = require_file(DEBUG_WORKFLOW_PATH)
models = require_file(MODELS_PATH)
excel = require_file(EXCEL_PATH)
manifest = require_file(MANIFEST_PATH)
version = require_file(VERSION_PATH)
view_model = require_file(VIEW_MODEL_PATH)
main_activity = require_file(MAIN_ACTIVITY_PATH)
application = require_file(APPLICATION_PATH)
repository = require_file(REPOSITORY_PATH)
updater = require_file(UPDATER_PATH)
update_screen = require_file(UPDATE_SCREEN_PATH)
file_paths = require_file(FILE_PATHS_PATH)

balanced(app, "App.kt")
balanced(growth, "GrowthEngine.kt")
balanced(ultrasound, "UltrasoundDating.kt")
balanced(ultrasound_test, "UltrasoundDatingTest.kt")
balanced(dose_range_test, "MedicationDoseRangeTest.kt")
balanced(entity, "MedicationEntity.kt")
balanced(database, "AppDatabase.kt")
balanced(models, "Models.kt")

# Firma exacta del componente que causó el fallo.
form_start = app.find("private fun FormTextField(")
form_end = app.find("\n) {", form_start)
if form_start < 0 or form_end < 0:
    errors.append("No se encontró la firma completa de FormTextField.")
else:
    form_signature = app[form_start:form_end]
    for token in (
        "label: String",
        "value: String",
        "onValue: (String) -> Unit",
        "modifier: Modifier = Modifier",
        "keyboardType: KeyboardType = KeyboardType.Text",
        'placeholder: String = ""',
        'suffix: String = ""',
        "singleLine: Boolean = true",
        "minLines: Int = 1",
    ):
        require(form_signature, token, "Firma FormTextField")

# Debe trasladar el sufijo al componente Material 3.
require(app, "suffix = if (suffix.isNotBlank())", "App.kt")
require(app, "{ Text(suffix) }", "App.kt")

suffix_calls = app.count('suffix = "')
if suffix_calls < 9:
    errors.append(
        f"Se esperaban al menos 9 llamadas FormTextField con sufijo; hay {suffix_calls}."
    )

# Contrato nutricional compartido.
for token in (
    "enum class NutritionStatus",
    "data class NutritionSummary",
    "val nutritionSummary: NutritionSummary",
    "val bmiPercentile: Double",
    "val overweightFromKg: Double",
    "val obesityFromKg: Double",
):
    require(growth, token, "GrowthEngine.kt")

for token in (
    "MainSection.RENAL",
    "MainSection.UPDATES",
    "AppUpdateScreen(",
    "mutableStateOf<MainSection?>(null)",
    "private fun SectionNavigationMenu(",
    "maxPanelHeight: Dp",
    "wideCompactLayout",
    "verticalScroll(",
    "private fun rememberCombinationDialTick()",
    "AudioAttributes.USAGE_MEDIA",
    "setOnLoadCompleteListener",
    "soundLoaded",
    "pendingPlay",
    "currentSoundLoaded",
    "0.72f",
    "R.raw.combination_dial_tick",
    "val landscape =",
    "Cada avance produce un clic mecánico discreto.",
    "private fun SectionMenuPanel(",
    "private fun SectionMenuTile(",
    "animateDpAsState(",
    "sectionMenuOffset",
    "Selecciona un apartado para comenzar",
    "Cambiar de apartado",
    "private fun RenalFunctionScreen(",
    "private fun KidneyRiskGrid(",
    "Dos ruedas independientes",
    "private enum class DateOrbitRing",
    "contentType = { _, _ ->",
    "private fun TableCell(",
    "private fun OverflowAwareTableText(",
    "private fun MedicationRecord.formattedDoseForTable()",
    "record.formattedDoseForTable()",
    "· Unidad:",
    "private fun TableCellDetailsDialog(",
    "hasVisualOverflow",
    "La tabla muestra solo datos clínicos",
    "private fun TableTextCell(",
    "private fun EditableSuggestionField(",
    "PopupProperties(",
    "focusable = false",
    ".onFocusChanged { focusState ->",
    "private fun ClinicalInfoButton(",
    "private fun ClinicalReferencesDialog(",
    "Info y referencias",
    "Referencias usadas",
    "private fun DateOrbitWheel(",
    "calendarMonthRotation",
    "calendarDayRotation",
    "gestogramRotation",
    "ringInteraction",
    "monthRotationTarget",
    "dayRotationTarget",
    "private fun InteractiveDoseWheel(",
    "Dosis interactiva por rango",
    "private fun GestogramWheel(",
    "private fun GestogramWheelPanel(",
    "LocalConfiguration.current",
    "screenIsLandscape",
    "compactHeight",
    "heightBudget",
    "availableSize",
    "landscape: Boolean = false",
    "compactHeight: Boolean = false",
    "private fun EditableDateInput(",
    "private fun GestogramSummaryCard(",
    "Desliza en círculo para modificar la FUM",
    "Fecha probable de parto",
    "private data class CalendarPartOption",
    "private fun <T> CalendarDatePartSelector",
    "Ir directamente a una fecha",
    "updateDateParts(",
    "import com.luisangel.calculadoramedicamentos.obstetrics.UltrasoundDatingCalculator",
    "import com.luisangel.calculadoramedicamentos.obstetrics.UltrasoundTrimester",
    "ULTRASOUND_DATING",
    "private fun UltrasoundGestationalAgeCalculator",
    "private fun ClinicalCalendarDialog",
    "itemsIndexed(",
    "rememberCoroutineScope()",
    "withContext(Dispatchers.Default)",
    "import com.luisangel.calculadoramedicamentos.growth.NutritionStatus",
    "import com.luisangel.calculadoramedicamentos.growth.NutritionSummary",
    "private fun NutritionStatusCard",
    "private fun ObstetricsScreen",
):
    require(app, token, "App.kt")

for token in (
    "enum class UltrasoundTrimester",
    "object UltrasoundDatingCalculator",
    "fun fromCrl(",
    "fun fromBiometry(",
    "8.052 * sqrt(1.037 * crlMm) + 23.73",
    "10.85 +",
    "redatingThresholdDays",
):
    require(ultrasound, token, "UltrasoundDating.kt")

for token in (
    "crl50mmProducesApproximatelyElevenWeeksFiveDays",
    "fourParameterHadlockProducesCompositeSecondTrimesterAge",
):
    require(ultrasound_test, token, "UltrasoundDatingTest.kt")

table_columns_start = app.find("private val medicationColumns = listOf(")
table_columns_end = app.find(")\n\n@Composable\nprivate fun MedicationTable(", table_columns_start)

if table_columns_start == -1 or table_columns_end == -1:
    errors.append("No se encontró medicationColumns.")
else:
    medication_columns_block = app[table_columns_start:table_columns_end]
    for removed_column in (
        'MedicationColumn("Familia"',
        'MedicationColumn("Subgrupo"',
        'MedicationColumn("Especialidades"',
    ):
        if removed_column in medication_columns_block:
            errors.append(
                f"La tabla todavía contiene columna no necesaria: {removed_column}"
            )

    expected_visible_columns = (
        'MedicationColumn("Medicamento"',
        'MedicationColumn("Presentación"',
        'MedicationColumn("Dosis"',
        'MedicationColumn("Dosis calculada"',
        'MedicationColumn("Uso por día"',
        'MedicationColumn("Días"',
        'MedicationColumn("Notas"',
        'MedicationColumn("Opciones"',
    )
    for visible_column in expected_visible_columns:
        if visible_column not in medication_columns_block:
            errors.append(
                f"Falta columna visible esperada: {visible_column}"
            )

row_start = app.find("private fun MedicationDataRow(")
row_end = app.find("private fun TableCell(", row_start)

if row_start == -1 or row_end == -1:
    errors.append("No se encontró MedicationDataRow para revisar la tabla.")
else:
    medication_row_block = app[row_start:row_end]
    for removed_dialog in (
        "· Familia",
        "· Subgrupo",
        "· Especialidades",
    ):
        if removed_dialog in medication_row_block:
            errors.append(
                f"MedicationDataRow todavía pinta campo de filtro: {removed_dialog}"
            )

if 'item(key = "medication-header")' in app:
    errors.append(
        "MedicationHeaderRow todavía se desplaza dentro del LazyColumn."
    )

if "DatePickerDialog" in app:
    errors.append("App.kt todavía usa el calendario clásico DatePickerDialog.")

if not DIAL_SOUND_PATH.exists():
    errors.append(
        "Falta el sonido local de la rueda: "
        "app/src/main/res/raw/combination_dial_tick.wav"
    )
elif DIAL_SOUND_PATH.stat().st_size < 500:
    errors.append(
        "El sonido local de la rueda parece estar vacío."
    )

if "val wheelSize = maxWidth" in app[
    app.find("private fun GestogramWheelPanel("):
    app.find("private fun rememberCombinationDialTick()")
]:
    errors.append(
        "GestogramWheelPanel todavía calcula la rueda solo con el ancho."
    )

if "Modifier.height(500.dp)" in app:
    errors.append(
        "App.kt todavía contiene un diálogo con altura fija de 500 dp."
    )

for token in (
    "excelServiceProvider: () -> ExcelService",
    "private val excelService: ExcelService by lazy",
    ".flowOn(Dispatchers.Default)",
):
    require(view_model, token, "MainViewModel.kt")

require(
    main_activity,
    "excelServiceProvider = app::createExcelService",
    "MainActivity.kt"
)
require(
    application,
    "fun createExcelService(): ExcelService",
    "CalculatorApplication.kt"
)
require(
    repository,
    ".flowOn(Dispatchers.Default)",
    "MedicationRepository.kt"
)

for token in (
    "isInteractiveDose",
    "dosePerKgMin",
    "dosePerKgMax",
    "dosePerKgStep",
    "fun MedicationRecord.calculatedDose(",
    "fun MedicationRecord.interactiveDoseStart(",
):
    require(models, token, "Models.kt")

for token in (
    "val isInteractiveDose: Boolean",
    "val dosePerKgMin: Double?",
    "val dosePerKgMax: Double?",
    "val dosePerKgStep: Double?",
):
    require(entity, token, "MedicationEntity.kt")

for token in (
    "version = 2",
    "MIGRATION_1_2",
    "ADD COLUMN isInteractiveDose",
    "ADD COLUMN dosePerKgMin",
):
    require(database, token, "AppDatabase.kt")

for token in (
    "Dosis interactiva",
    "Dosis mínima por kg",
    "Dosis máxima por kg",
    "Paso de dosis",
):
    require(excel, token, "ExcelService.kt")

for token in (
    "selectedDoseIsUsedForCalculation",
    "validInteractiveRangePassesValidation",
):
    require(
        dose_range_test,
        token,
        "MedicationDoseRangeTest.kt"
    )

for token in (
    "@Immutable",
    "data class MedicationRecord",
    "data class FilterState",
):
    require(models, token, "Models.kt")

for token in (
    "delay(450)",
    "viewModelScope.launch(Dispatchers.IO)",
    "record.specialties.any(filter.specialties::contains)",
):
    require(
        require_file(ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/ui/MainViewModel.kt"),
        token,
        "MainViewModel.kt"
    )

for token in (
    "setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)",
    "setQueryExecutor",
    "setTransactionExecutor",
):
    require(database, token, "AppDatabase.kt")

require(
    database,
    "exportSchema = false",
    "AppDatabase.kt"
)

if "room.schemaLocation" in app_gradle:
    errors.append(
        "app/build.gradle.kts todavía configura "
        "room.schemaLocation."
    )

for workflow_name, workflow_text in (
    ("build-release.yml", release_workflow),
    ("build-apk.yml", debug_workflow),
):
    for token in (
        "Limpiar archivos generados anteriores",
        "rm -rf app/build app/schemas",
        "--no-parallel",
    ):
        require(workflow_text, token, workflow_name)

    if "--parallel" in workflow_text:
        errors.append(
            f"{workflow_name} todavía usa --parallel."
        )

for token in (
    "enum class RenalMethod",
    "CKD_EPI_2021_CREATININE",
    "COCKCROFT_GAULT",
    "fun classifyGfr(",
    "fun classifyAlbuminuria(",
    "fun combinedRisk(",
):
    require(renal, token, "RenalEngine.kt")

for token in (
    "ckdEpi2021CreatinineMatchesReferenceExample",
    "cockcroftGaultProducesRawAndIndexedValues",
    "kdigoCategoriesAndRiskMatrixAreClassified",
):
    require(renal_test, token, "RenalEngineTest.kt")

gestogram_start = app.find(
    "private fun GestogramWheel("
)
date_orbit_start = app.find(
    "private fun DateOrbitWheel("
)

if gestogram_start == -1 or date_orbit_start == -1:
    errors.append(
        "No se localizaron GestogramWheel y DateOrbitWheel."
    )
else:
    gestogram_block = app[
        gestogram_start:date_orbit_start
    ]

    for forbidden_token in (
        "activeRing ==",
        "ringInteraction *",
    ):
        if forbidden_token in gestogram_block:
            errors.append(
                "GestogramWheel contiene una variable "
                f"fuera de alcance: {forbidden_token}"
            )

    for required_token in (
        "gestogramInteraction * 0.025f",
        "val outline = MaterialTheme.colorScheme.outlineVariant",
    ):
        if required_token not in gestogram_block:
            errors.append(
                "GestogramWheel no contiene: "
                f"{required_token}"
            )

if 'color = primary.copy(alpha = 0.82f)' in app:
    errors.append(
        "App.kt todavía contiene el aro azul de interacción."
    )

if 'start = pointAt(-90f, radius * 0.68f)' in app:
    errors.append(
        "App.kt todavía contiene la línea radial del calendario."
    )

if "CalendarMonthGrid(" in app or "CalendarWeekHeader(" in app:
    errors.append(
        "App.kt todavía contiene el calendario mensual convencional."
    )

if "TabRow(selectedTabIndex = section.ordinal)" in app:
    errors.append(
        "ApplicationShell todavía contiene la barra fija de apartados."
    )

if 'var section by rememberSaveable { mutableStateOf(MainSection.MEDICATIONS) }' in app:
    errors.append(
        "La aplicación todavía inicia directamente en Medicamentos."
    )

balanced(updater, "AppUpdateManager.kt")
balanced(update_screen, "AppUpdateScreen.kt")

for token in (
    "class AppUpdateManager(",
    "UpdateManifest",
    "UPDATE_MANIFEST_URL",
    "downloadApk(",
    "installApk(",
    "FileProvider.getUriForFile",
    "Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES",
):
    require(updater, token, "AppUpdateManager.kt")

for token in (
    "fun AppUpdateScreen(",
    "Buscar",
    "Descargar e instalar",
    "Permiso instalación",
    "BuildConfig.UPDATE_MANIFEST_URL",
):
    require(update_screen, token, "AppUpdateScreen.kt")

for token in (
    "android.permission.INTERNET",
    "android.permission.REQUEST_INSTALL_PACKAGES",
    "androidx.core.content.FileProvider",
    "@xml/file_paths",
):
    require(manifest, token, "AndroidManifest.xml")

for token in (
    "UPDATE_REPOSITORY",
    "UPDATE_MANIFEST_URL",
    "buildConfigField(",
):
    require(app_gradle, token, "app/build.gradle.kts")

for token in (
    "update.json",
    "APP_UPDATE_REPOSITORY",
    "releases/download",
):
    require(release_workflow, token, "build-release.yml")

require(file_paths, "<cache-path", "file_paths.xml")
require(file_paths, "updates/", "file_paths.xml")

# La variante sigue siendo local en datos clínicos:
# se permite INTERNET únicamente para buscar/descargar actualizaciones.
combined = app + growth + ultrasound + models + renal + manifest + updater + update_screen
for forbidden in (
    "android.webkit.WebView",
    "supabase",
):
    if forbidden.lower() in combined.lower():
        errors.append(f"Referencia prohibida en variante local: {forbidden}")

if not re.search(r"^VERSION_CODE=\d+$", version, flags=re.M):
    errors.append("VERSION_CODE no es numérico.")
if not re.search(r"^VERSION_NAME=.+$", version, flags=re.M):
    errors.append("Falta VERSION_NAME.")

if errors:
    print("PRECHECK FALLÓ")
    for number, error in enumerate(errors, 1):
        print(f"{number}. {error}")
    sys.exit(1)

print("PRECHECK CORRECTO")
print(f"Llamadas FormTextField con sufijo: {suffix_calls}")
print("Contratos App/GrowthEngine: correctos")
print("Estructura local sin WebView/Supabase; INTERNET solo para actualizaciones")
