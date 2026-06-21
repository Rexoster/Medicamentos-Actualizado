# Cambios v23.18 · ECG patologías con criterios completos

## Resumen

- En `ECG > Patologías ECG` se agregó un panel desplegable de **Criterios diagnósticos descritos**.
- Cada patología ahora muestra una lista más completa de criterios clínicos/didácticos, no solo los 1-2 criterios que se sombrean sobre el ECG.
- Los criterios visuales sobre el ECG ahora tienen campos de control por latido para evitar repetir sombras sin sentido.
- Los marcadores pueden apuntar a latidos concretos, útil para Mobitz I, Mobitz II, BAV 2:1, extrasístoles y pausa sinusal.
- Se amplió la cantidad de zonas/ondas señalables en el ECG didáctico: P, PR, QRS, ST, T, QT, voltaje, ritmo completo.

## Motivo

El usuario observó que algunas patologías sólo mostraban pocos criterios y que las sombras podían parecer colocadas fuera de la onda o segmento correcto. Esta corrección separa:

1. **Criterios diagnósticos completos descritos**: texto educativo más amplio.
2. **Criterios visuales señalables**: lo que sí se puede sombrear sin saturar el trazo.

## Archivos modificados

- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `version.properties`

## Versión

- `VERSION_CODE=54`
- `VERSION_NAME=3.16.18-native`
