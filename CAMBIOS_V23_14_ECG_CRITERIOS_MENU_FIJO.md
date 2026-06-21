# v23.14 · ECG criterios señalados + menú fijo de subapartados

## Cambios principales

- En **Patologías ECG** ahora el ECG didáctico señala visualmente los criterios relevantes mediante sombreado y contorno por derivación.
- El ECG del apartado **Patologías ECG** ahora comparte las funciones de **Vista previa ECG**:
  - selección de derivación individual,
  - visualización del ECG completo de 12 derivaciones,
  - comparación por áreas cardíacas,
  - comparación por irrigación coronaria,
  - zoom con botones y gesto de pellizco.
- Se añadió una **leyenda de criterios** para mostrar qué está marcado y en qué derivaciones.
- Los menús de subapartados de **ECG**, **Pediatría** y **Gineco-OB** quedaron **fijos/visibles** durante el desplazamiento vertical, colocados en la zona superior central para no perder acceso al menú mientras se trabaja.

## Archivos modificados

- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/App.kt`
- `version.properties`

## Versión

- `VERSION_CODE=50`
- `VERSION_NAME=3.16.14-native`
