# v23.20 · ECG con series reales por latido

## Cambios principales

- Se corrigió el problema visual del PR progresivo en Mobitz I: la onda P ya no se mueve pegada al QRS; ahora la P queda anclada al ciclo auricular y el QRS se retrasa según el PR de cada latido.
- Se agregaron series editables por latido en el analizador:
  - Serie PR.
  - Serie RR/PP.
  - Serie QRS.
  - Serie QT.
- Las patologías que requieren variación cargan automáticamente valores de serie para que el trazo no dependa de una sola cifra promedio.
- Se aplican series reales a:
  - Mobitz I / Wenckebach.
  - Mobitz II.
  - BAV 2:1.
  - BAV completo.
  - Fibrilación auricular.
  - Extrasístole auricular.
  - Pausa sinusal.
  - Hiperkalemia.
  - Hipokalemia.
  - Flutter auricular.
  - Taquicardia ventricular.
- Los marcadores del ECG ahora usan las mismas posiciones temporales calculadas para P, PR, QRS, ST, T y QT.

## Corrección importante

Antes, el PR progresivo no se veía porque el cálculo de la onda P dependía del inicio del QRS. Entonces, aunque el PR cambiara internamente, la P también se desplazaba y el intervalo se veía casi igual. En esta versión, la P y el QRS se calculan por separado.

## Archivos modificados

- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `version.properties`
