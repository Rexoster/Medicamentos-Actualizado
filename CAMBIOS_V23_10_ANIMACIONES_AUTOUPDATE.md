# Cambios v23.10 · Animaciones mejoradas y aviso automático de actualización

## Menús animados
- Se mejoró el icono animado del menú de **Pediatría**:
  - Feto cerrado con contorno tipo membrana.
  - Transición con efecto de ruptura/salida.
  - Transformación visual a bebé al abrir el menú.
- Se mejoró el icono animado del menú **Gineco-OB**:
  - Mórula cerrada.
  - División celular progresiva durante la apertura.
  - Transición visual hacia feto.

## Actualización automática
- Al abrir la app, se consulta automáticamente el manifiesto de actualización configurado.
- Si existe una versión con `versionCode` mayor al instalado, aparece una ventana: **Nueva actualización disponible**.
- La ventana se puede cerrar tocando fuera de ella o con el botón **Después**.
- Si se cierra, no permanece fija durante esa sesión.
- Al abrir nuevamente la app, se vuelve a consultar y mostrar si la actualización sigue disponible.
- El botón **Actualizar** lleva al apartado de **Actualizaciones** para descargar e instalar desde el flujo ya existente.

## Archivos modificados
- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/App.kt`
- `version.properties`

## Notas
- No se cambiaron cálculos clínicos.
- No se modificó la lógica de descarga/instalación existente; solo se agregó la detección automática y el aviso inicial.
