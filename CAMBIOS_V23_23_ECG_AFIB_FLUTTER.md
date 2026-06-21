# Cambios v23.23 - ECG FA/Flutter y sombreado

## Correcciones principales

1. **Fibrilación auricular**
   - Se redibujó la actividad auricular para que parezca una línea de base fibrilatoria real y no una pseudo-P aislada.
   - Se corrigió el sombreado del criterio **"Sin P"** para que marque la zona de actividad auricular desorganizada en vez de una onda P falsa.
   - Se recolocaron los criterios **RR irregular** para que midan de **R a R** y no desde posiciones desplazadas.

2. **Flutter auricular**
   - Se sustituyó el dibujo anterior por un trazado de **ondas F en serrucho** más continuo y ordenado.
   - Las ondas F ahora se muestran de forma más visible en derivaciones inferiores y con menor interferencia sobre QRS/T.
   - Se corrigió el sombreado del criterio **"Ondas F"** para que quede sobre el segmento donde realmente se observan.

3. **Vistas completas / áreas cardíacas / irrigación**
   - Se incrementó el límite de criterios visibles para que en vistas compactas se muestren más recuadros diagnósticos.
   - Se redujo el desorden visual del sombreado en las patologías de ritmo que más fallaban.

## Archivos modificados
- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `version.properties`
