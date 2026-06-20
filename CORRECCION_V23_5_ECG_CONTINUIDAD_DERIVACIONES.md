# Corrección v23.5 · Continuidad visual entre derivaciones ECG

## Motivo
En la vista de ECG completo, cada celda/derivación iniciaba su trazo desde el mismo punto temporal. Esto podía provocar que al comenzar una nueva derivación apareciera un complejo QRS al inicio de la celda y visualmente se sobrepusiera o se percibiera pegado al complejo final de la derivación anterior.

## Cambio realizado
Se ajustó el renderizado de la vista de 12 derivaciones para que cada columna represente un segmento temporal consecutivo.

Antes:
- Cada derivación reiniciaba el trazo desde cero.
- El primer QRS de cada celda podía aparecer demasiado cerca del QRS previo.

Ahora:
- La fila mantiene continuidad temporal por columnas.
- Cada derivación conserva su polaridad, amplitud aproximada, eje y región.
- El trazo se recorta dentro de su celda para evitar invasión visual sobre otras derivaciones.
- El ECG completo se ve más continuo y ordenado al desplazarse o hacer zoom.

## Archivos modificados
- app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt
- version.properties

## Versión
- VERSION_CODE=41
- VERSION_NAME=3.16.5-native
