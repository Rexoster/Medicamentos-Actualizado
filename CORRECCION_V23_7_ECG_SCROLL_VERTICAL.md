# Corrección v23.7 - Scroll vertical dentro del ECG

## Cambio realizado

Se corrigió el bloqueo del desplazamiento vertical en la pantalla de vista previa del ECG.

En la versión previa, el área del ECG capturaba todos los gestos táctiles para detectar zoom, incluyendo los gestos verticales de un solo dedo. Eso impedía bajar normalmente por la pantalla cuando el dedo iniciaba el movimiento sobre el ECG.

## Solución

- El gesto de zoom por pellizco ahora se activa solo cuando hay dos o más dedos sobre el ECG.
- Los gestos verticales de un solo dedo quedan libres para el scroll principal de la pantalla.
- El desplazamiento horizontal del ECG se conserva.
- Los botones de zoom siguen funcionando igual.
- No se modificaron cálculos, derivaciones, áreas cardíacas ni territorios coronarios.

## Archivos modificados

- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `version.properties`

## Versión

- `VERSION_CODE=43`
- `VERSION_NAME=3.16.7-native`
