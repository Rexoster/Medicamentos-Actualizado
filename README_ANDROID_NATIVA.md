# Calculadora de Medicamentos Android nativa

Esta versión fue reconstruida en Kotlin y Jetpack Compose. No usa WebView, no
incluye Supabase y no solicita permiso de internet.

## Almacenamiento

Los medicamentos se guardan en una base Room/SQLite ubicada dentro del espacio
privado de la aplicación. El tema y la marca que evita volver a crear los
ejemplos se guardan mediante Preferences DataStore.

Los pesos de adultos y pediátricos son temporales. Solo viven en memoria durante
la sesión y no se escriben en la base de datos, DataStore, Excel automático ni
otro almacenamiento.

La aplicación tiene desactivado Android Backup. Los datos permanecen en el
celular, pero se eliminan si:

- desinstalas la aplicación;
- presionas "Borrar datos" desde ajustes;
- el almacenamiento del teléfono se daña.

Para evitar una pérdida definitiva, exporta un Excel periódicamente.

## Funciones incluidas

- Interfaz Android nativa adaptable a teléfono y tablet.
- Tema claro con sol y tema oscuro con luna.
- Medicamentos adultos, pediátricos y adultos especiales.
- Cálculo temporal por kilogramo de peso.
- Agregar, editar y eliminar medicamentos.
- Menú mediante pulsación larga y botón de opciones.
- Notas con "Ver más" y "Ver menos".
- Familias, subgrupos y selección múltiple de especialidades.
- Filtros por texto, familia, subgrupo, tipo y especialidades.
- Orden por nombre, fecha de alta o familia.
- Detección de duplicados exactos.
- Importación de XLSX, XLS, CSV y respaldos JSON anteriores.
- Exportación XLSX con hojas Adultos, Pediátricos, Configuración y BaseDatos.
- Ejemplos de paracetamol adulto, paracetamol pediátrico e insulina basal.
- Base local automática con Room.
- Sin nube, cuentas, anuncios ni seguimiento.

## Tecnología

- Kotlin 2.1.20
- Jetpack Compose
- Room 2.8.4
- Preferences DataStore 1.2.1
- Apache POI 5.4.1
- Android Gradle Plugin 8.10.1
- Gradle 8.11.1
- Java 17
- Android mínimo 7.0 (API 26)
- compileSdk/targetSdk 36

## Abrir el proyecto

1. Descomprime el ZIP.
2. Abre la carpeta `CalculadoraMedicamentosNativa` en Android Studio.
3. Permite que Gradle descargue las dependencias.
4. Instala el SDK 36 cuando Android Studio lo solicite.
5. Conecta un teléfono o abre un emulador.
6. Presiona **Run**.

## Generar APK

Desde Android Studio:

1. Abre **Build**.
2. Selecciona **Build APK(s)**.
3. El APK aparecerá en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Desde terminal:

```bash
./gradlew testDebugUnitTest assembleDebug
```

En Windows:

```bat
gradlew.bat testDebugUnitTest assembleDebug
```

## Generar APK con GitHub

El proyecto incluye `.github/workflows/build-apk.yml`.

1. Sube la carpeta a un repositorio de GitHub.
2. Abre **Actions**.
3. Ejecuta **Generar APK nativa**.
4. Descarga el artefacto `CalculadoraMedicamentosNativa-debug`.

## Migrar datos desde la versión web

1. En la página web anterior, exporta el Excel.
2. Instala y abre la aplicación nativa.
3. Presiona **Importar**.
4. Selecciona el Excel.
5. Elige **Combinar** o **Reemplazar**.

La comparación de duplicados incluye tipo, presentación, dosis, dosis por kg,
unidad, frecuencia, duración, familia, subgrupo, especialidades y notas. Un
mismo fármaco con distinta presentación o dosis se conserva como registro
diferente.

## Privacidad

La aplicación no contiene permiso de internet. No puede sincronizar ni enviar
medicamentos a servidores. No almacena nombres, expedientes, diagnósticos ni
otros datos identificables de pacientes.


## Versión 3.0

- Tabla nativa con desplazamiento horizontal para medicamentos.
- Diseño adaptable para teléfonos y tabletas.
- Menú superior entre Medicamentos y Percentiles.
- Percentiles pediátricos OMS 2006/2007 con gráficas nativas.
- El módulo de percentiles no guarda las mediciones.
- Flujo opcional para generar APK release firmada.

Consulta `FIRMA_Y_PLAY_PROTECT.md` para la firma y distribución.
