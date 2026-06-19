# Cambios v23 · Módulo ECG

## Agregado

- Nuevo apartado principal: **ECG**.
- Analizador guiado de electrocardiograma con resumen copiable.
- Calculadora de frecuencia cardiaca por:
  - RR en milisegundos.
  - RR en segundos.
  - Cuadros grandes.
  - Cuadros pequeños.
  - Complejos QRS en tira de 10 segundos.
- Conversión de cuadros a milisegundos a 25 mm/s y 50 mm/s.
- Cálculo de RR desde frecuencia cardiaca.
- QTc por Bazett, Fridericia, Framingham y Hodges.
- Eje eléctrico por DI + aVF o DI + DIII.
- Criterios eléctricos de HVI:
  - Sokolow-Lyon.
  - Cornell voltaje.
  - Cornell producto.
- Criterios de elevación del ST por edad, sexo y grupo de derivaciones.

## Seguridad clínica

- Se agregó advertencia visible de que es una herramienta de apoyo, no un diagnóstico automático.
- No se interpreta foto o imagen de ECG en esta versión.

## Archivos modificados/agregados

- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/App.kt`
- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `app/src/main/java/com/luisangel/calculadoramedicamentos/ecg/EcgEngine.kt`
- `app/src/test/java/com/luisangel/calculadoramedicamentos/ecg/EcgEngineTest.kt`
- `FUENTES_ECG_V23.md`
- `CAMBIOS_V23_ECG.md`
- `version.properties`
