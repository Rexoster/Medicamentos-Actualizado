# Cambios v23.21 · ECG sombreado de criterios corregido en vistas completas

## Corrección principal
- Se corrigió el sombreado de criterios en ECG completo, áreas cardíacas e irrigación coronaria.
- Los marcadores ya no se dibujan por simple recorte al borde de la celda cuando el evento ocurre fuera del segmento temporal visible.
- Ahora cada sombreado se coloca solo si el intervalo real del criterio intersecta el segmento temporal de esa derivación.

## Qué se ajustó
- P bloqueada: se marca la onda P bloqueada, no todo el ciclo.
- QRS caído: se marca la ventana esperada del QRS ausente.
- PR progresivo: se marca el intervalo real P-QRS de cada latido conducido.
- RR irregular / pausas / relación de conducción: se dibuja como banda superior/inferior, evitando cubrir toda la derivación.
- En vistas completas se permiten más criterios visibles y se expanden criterios globales de ritmo/conducción a los segmentos donde realmente ocurren.

## Archivos modificados
- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `version.properties`

## Versión
- `VERSION_CODE=57`
- `VERSION_NAME=3.16.21-native`
