# Corrección v23.6 · ECG zoom por pellizco y selección múltiple

## Cambios

- La vista previa del ECG ahora permite hacer zoom con gesto de pellizco directamente sobre el trazo.
- Se mantiene el control manual de zoom con botones.
- El límite máximo de zoom se amplió a 300%.
- El menú de áreas cardíacas permite seleccionar más de una zona al mismo tiempo.
- El menú de territorios coronarios permite seleccionar más de un territorio al mismo tiempo.
- Cada área o territorio seleccionado usa un color distinto.
- Cuando una derivación pertenece a más de una zona seleccionada, el sombreado se divide en franjas de colores para mostrar la superposición.
- La leyenda ahora muestra cada área/territorio seleccionado con su color y sus derivaciones.

## Archivos modificados

- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `version.properties`

## Nota clínica

La vista ECG continúa siendo una representación didáctica configurada con parámetros ingresados por el usuario. No sustituye un ECG real ni una interpretación clínica formal.
