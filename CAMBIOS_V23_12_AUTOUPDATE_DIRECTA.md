# Cambios v23.12 · Actualización directa desde aviso automático

## Resumen
- El aviso automático de nueva actualización ya no manda al usuario a la pantalla de Actualizaciones para volver a buscar.
- El botón **Actualizar** del aviso inicia directamente la descarga de la APK usando el manifiesto ya detectado.
- Se agregó progreso de descarga dentro de la ventana del aviso automático.
- Al terminar la descarga, la app abre el instalador si Android permite instalaciones desde esta app.
- Si falta el permiso de instalación, se abre la configuración correspondiente después de descargar.
- La ventana sigue siendo descartable tocando fuera de ella y volverá a aparecer al abrir de nuevo la app si la actualización sigue disponible.

## Archivos modificados
- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/App.kt`
- `version.properties`

## Notas
- No se cambió el flujo manual de la pantalla Actualizaciones.
- No se modificaron módulos clínicos ni cálculos.
