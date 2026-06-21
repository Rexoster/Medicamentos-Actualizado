# v23.16 · Corrección de compilación + más patologías ECG

## Corrección crítica

- Se corrigió el error de compilación en `EcgScreen.kt` donde `maxWidth` se estaba usando dentro de un contexto sin receptor implícito.
- La corrección captura `maxWidth` en una variable local (`availableMenuWidth`) dentro de `BoxWithConstraints` y luego la pasa al menú ECG.

## Patologías agregadas

Se añadieron ejemplos didácticos nuevos al apartado **ECG > Patologías ECG**:

- BAV de segundo grado Mobitz I / Wenckebach.
- BAV de segundo grado Mobitz II.
- BAV de tercer grado / completo.
- Bradicardia sinusal.
- Taquicardia sinusal.
- Flutter auricular.
- Taquicardia supraventricular.
- Taquicardia ventricular.
- Wolff-Parkinson-White.
- Pericarditis aguda.
- Hipokalemia.
- IAM lateral.
- IAM posterior indirecto.

## Cambios didácticos

- Se añadieron criterios sombreados para las patologías nuevas.
- Se ajustaron valores precargados en calculadoras: FC, PR, QRS, QT/QTc, eje, ST y ritmo según corresponda.
- El trazo didáctico ahora representa patrones adicionales como flutter, taquicardia ventricular, WPW e hipokalemia de manera visual aproximada.

## Nota clínica

Estos trazos son ejemplos de enseñanza y no sustituyen lectura de ECG real, correlación clínica ni valoración médica.
