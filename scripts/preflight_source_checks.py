#!/usr/bin/env python3
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
APP_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/ui/App.kt"
GROWTH_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/growth/GrowthEngine.kt"
ULTRASOUND_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/obstetrics/UltrasoundDating.kt"
ULTRASOUND_TEST_PATH = ROOT / "app/src/test/java/com/luisangel/calculadoramedicamentos/obstetrics/UltrasoundDatingTest.kt"
MANIFEST_PATH = ROOT / "app/src/main/AndroidManifest.xml"
VERSION_PATH = ROOT / "version.properties"
VIEW_MODEL_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/ui/MainViewModel.kt"
MAIN_ACTIVITY_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/MainActivity.kt"
APPLICATION_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/CalculatorApplication.kt"
REPOSITORY_PATH = ROOT / "app/src/main/java/com/luisangel/calculadoramedicamentos/data/MedicationRepository.kt"

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
manifest = require_file(MANIFEST_PATH)
version = require_file(VERSION_PATH)
view_model = require_file(VIEW_MODEL_PATH)
main_activity = require_file(MAIN_ACTIVITY_PATH)
application = require_file(APPLICATION_PATH)
repository = require_file(REPOSITORY_PATH)

balanced(app, "App.kt")
balanced(growth, "GrowthEngine.kt")
balanced(ultrasound, "UltrasoundDating.kt")
balanced(ultrasound_test, "UltrasoundDatingTest.kt")

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
    "import com.luisangel.calculadoramedicamentos.obstetrics.UltrasoundDatingCalculator",
    "import com.luisangel.calculadoramedicamentos.obstetrics.UltrasoundTrimester",
    "ULTRASOUND_DATING",
    "private fun UltrasoundGestationalAgeCalculator",
    "private fun ClinicalCalendarDialog",
    "private fun CalendarMonthGrid",
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

if "DatePickerDialog" in app:
    errors.append("App.kt todavía usa el calendario clásico DatePickerDialog.")

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

# La variante debe seguir siendo local.
combined = app + growth + ultrasound + manifest
for forbidden in (
    "android.webkit.WebView",
    "supabase",
    "android.permission.INTERNET",
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
print("Estructura local sin WebView, Supabase ni permiso INTERNET")
