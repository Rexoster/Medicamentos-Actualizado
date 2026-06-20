# Corrección v23.2 · Menú ECG con botón corazón

## Cambios

- El menú interno de Electrocardiograma ahora inicia colapsado.
- El botón del menú ECG queda centrado, similar al menú principal.
- Se agregó un ícono personalizado: corazón con tres líneas internas de menú.
- Al tocar el corazón se despliega el menú de herramientas ECG.
- Al seleccionar una herramienta, el menú se vuelve a colapsar automáticamente.
- Se conserva el comportamiento responsivo en pantallas compactas y orientación horizontal.
- No se modificaron los cálculos clínicos ni las referencias del módulo ECG.

## Archivos modificados

- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `version.properties`

## Nota

Esta corrección solo cambia la navegación visual del apartado ECG. No toca medicamentos, percentiles, obstetricia, renal ni actualizaciones.
