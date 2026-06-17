# Actualizaciones fáciles

## Qué cambia desde esta versión

El proyecto conserva siempre:

- `applicationId = com.luisangel.calculadoramedicamentos`
- La misma base Room/SQLite.
- Los medicamentos almacenados localmente.
- La misma clave de firma configurada en GitHub Secrets.

GitHub asigna automáticamente un `versionCode` mayor en cada ejecución del
workflow `Generar actualización firmada`.

## Transición única desde la APK debug

La APK que ya estaba instalada fue generada como `debug` en un runner temporal
de GitHub. Su clave privada no puede recuperarse.

Por ello, una sola vez:

1. Exporta los medicamentos a Excel.
2. Configura los cuatro secretos de firma descritos en
   `FIRMA_Y_PLAY_PROTECT.md`.
3. Ejecuta `Generar actualización firmada`.
4. Desinstala la APK debug.
5. Instala la APK release firmada.
6. Importa el Excel.

A partir de esa instalación, las siguientes APK release se instalan encima de
la anterior y conservan la base local.

## Actualizar el código sin sustituir todo el repositorio

Cada actualización entregada incluye un ZIP `PATCH_GITHUB` con únicamente los
archivos modificados.

Forma recomendada:

1. Conserva una copia local del repositorio o usa GitHub Desktop.
2. Extrae el parche sobre la carpeta del proyecto.
3. Acepta reemplazar los archivos indicados.
4. Haz Commit y Push.
5. Ejecuta `Generar actualización firmada`.

También puedes editar o reemplazar únicamente esos archivos desde GitHub.

## Descargar la nueva APK

El workflow crea automáticamente:

- Un artefacto en Actions.
- Una entrada en la sección Releases.
- Una APK y un AAB con el número de versión en el nombre.

Instala la APK nueva encima de la existente. Android conservará los
medicamentos siempre que la firma y el applicationId coincidan.


## Validación temprana de firma

Desde v10.1 el workflow abre la clave privada antes de compilar. Un error
de contraseña o Base64 aparecerá en `Preparar y validar firma`, no al final
de `packageRelease`.
