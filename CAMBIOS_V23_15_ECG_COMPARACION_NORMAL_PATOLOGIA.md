# v23.15 · Comparación normal vs patología en ECG didáctico

## Cambios

- En **ECG > Patologías ECG**, el trazo principal sigue mostrando la patología seleccionada.
- Al hacer **doble toque** sobre el ECG de muestra, se abre una comparación tipo tarjetas movibles entre:
  - ECG normal didáctico.
  - ECG con la patología seleccionada.
- Las tarjetas se pueden desplazar horizontalmente para comparar, parecido a una vista de tarjetas móviles.
- Al tocar una tarjeta, esa muestra queda como la vista activa y se regresa al modo normal del apartado.
- La muestra activa conserva las herramientas ya existentes:
  - selección de derivación,
  - ECG de 12 derivaciones,
  - vista por áreas cardíacas,
  - vista por irrigación coronaria,
  - zoom con botones,
  - zoom por pellizco,
  - sombreado/leyenda de criterios.

## Archivos modificados

- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `version.properties`

## Versión

- `VERSION_CODE=51`
- `VERSION_NAME=3.16.15-native`
