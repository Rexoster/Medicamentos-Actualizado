# v23.17 · ECG patologías con morfología e intervalos variables

## Correcciones principales

- Se rehízo la lógica didáctica del trazo ECG para que algunas patologías no sean solo valores fijos:
  - Mobitz I ahora muestra PR progresivamente más largo y después una P no conducida.
  - Mobitz II ahora mantiene PR estable y muestra caída súbita de QRS.
  - BAV 2:1 alterna P conducida y P bloqueada.
  - Pausa sinusal muestra un intervalo sin P ni QRS.
  - Extrasístole auricular muestra P prematura con QRS estrecho.
  - Ritmo nodal muestra P retrógrada/pegada al QRS.
- Se añadieron morfologías de ondas:
  - P mitrale en crecimiento auricular izquierdo.
  - P pulmonale en crecimiento auricular derecho.
  - crecimiento biauricular.
  - rSR' tipo “orejas de conejo” más evidente en BRD.
  - onda delta en WPW.
  - onda U didáctica en hipokalemia.
- Los marcadores/sombreados ahora se calculan con los tiempos del latido:
  - P,
  - PR,
  - QRS,
  - ST,
  - T,
  - QT,
  - pausas o latidos bloqueados.

## Nuevo menú de patologías

- El apartado Patologías ECG ahora tiene una lista desplegable compacta.
- Ya no ocupa media pantalla con chips/botones.
- Al seleccionar una patología, se cargan los datos y el ECG se actualiza.

## Analizador

Se agregaron opciones para que el ECG didáctico pueda mostrar variabilidad:
- PR variable por latido.
- QRS variable por latido.
- QT/ST-T variable por latido.
- Morfología variable de ondas.

## Patologías agregadas

- BAV 2:1.
- Crecimiento auricular izquierdo.
- Crecimiento auricular derecho.
- Crecimiento biauricular.
- Extrasístole auricular.
- Ritmo nodal.
- Pausa sinusal.

## Versión

- VERSION_CODE=53
- VERSION_NAME=3.16.17-native
