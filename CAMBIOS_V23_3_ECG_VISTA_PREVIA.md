# Cambios v23.3 · Vista previa dinámica de ECG

## Nuevo
- Se agrega el apartado **Vista previa ECG** dentro del menú interno de ECG.
- La vista previa dibuja una **tira de ritmo simulada en derivación II**.
- La tira se configura automáticamente con datos capturados en los apartados del módulo ECG:
  - Frecuencia cardiaca directa.
  - RR en ms/s, cuadros grandes/pequeños o tira de 10 segundos.
  - PR.
  - QRS.
  - QT.
  - Ritmo sinusal / regularidad.
  - Elevación o depresión visual del ST.
  - Criterios de HVI para aumentar voltaje QRS de manera didáctica.
  - Eje eléctrico, mostrado y usado para invertir o modificar de forma aproximada la polaridad del QRS en DII simulada cuando aplique.

## Comportamiento
- Si no hay datos capturados, usa un ejemplo base de **75 lpm** para que la pantalla no quede vacía.
- Si se captura FC directa, esa tiene prioridad.
- Si no hay FC directa, intenta usar el primer cálculo válido de frecuencia desde RR, cuadros o tira de 10 segundos.
- El ritmo irregular modifica visualmente la separación RR entre complejos.
- El QRS ancho se dibuja más ancho.
- El QT prolongado alarga visualmente la repolarización.
- El ST positivo eleva el segmento; un valor negativo lo deprime visualmente.

## Seguridad clínica
- La vista previa es **didáctica y generada por parámetros**.
- No es un ECG real de paciente.
- No reconstruye 12 derivaciones.
- No reemplaza interpretación médica ni correlación clínica.
