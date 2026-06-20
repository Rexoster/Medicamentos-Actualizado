# Cambios v23.13 · ECG con patologías didácticas

## Nuevo apartado
Se agregó en el menú ECG el apartado **Patologías ECG**.

## Qué permite
Al tocar una patología:
- rellena automáticamente los datos compartidos de las calculadoras ECG;
- actualiza FC/RR, PR, QRS, QT/QTc, eje, ST o HVI según el patrón;
- activa un patrón visual en la vista previa ECG;
- muestra un trazo didáctico en la derivación más útil para el ejemplo;
- permite luego entrar a las calculadoras ya existentes para revisar los criterios.

## Ejemplos incluidos
- Ritmo sinusal normal.
- Extrasístole ventricular.
- Fibrilación auricular.
- Bloqueo AV de primer grado.
- Crecimiento ventricular izquierdo.
- Crecimiento ventricular derecho.
- Bloqueo de rama derecha.
- Bloqueo de rama izquierda.
- IAM con elevación anterior.
- IAM con elevación inferior.
- Hiperkalemia.
- QT prolongado.

## Cambios visuales
- La vista previa ECG ahora reconoce el patrón activo.
- Extrasístole ventricular: QRS ancho prematuro y pausa compensadora didáctica.
- Fibrilación auricular: ausencia de P organizada y RR irregular.
- BRD: patrón rSR’ en V1 y S terminal en laterales.
- BRI: complejo negativo amplio en V1 y R ancha/notchada en laterales.
- HVI: voltaje aumentado y datos para criterios Sokolow-Lyon/Cornell.
- Hiperkalemia: T picudas y QRS ensanchado.
- IAM anterior/inferior: elevación del ST por derivaciones relacionadas y cambios recíprocos orientativos.

## Archivos modificados
- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `version.properties`

## Nota clínica
Los patrones son simulaciones educativas configuradas por parámetros. No sustituyen ECG real, criterio clínico, seriación, biomarcadores, imagen ni lectura especializada.
