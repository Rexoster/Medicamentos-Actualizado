# Cambios v22 · Actualización desde la app

- Se agregó el apartado `Actualizaciones`.
- La app consulta `update.json` desde la última versión publicada.
- El workflow release genera y publica `update.json`.
- La app descarga la APK firmada desde GitHub Releases.
- Se abre el instalador de Android desde la propia aplicación.
- Se agregó FileProvider para compartir la APK con el instalador.
- Se agregó permiso INTERNET únicamente para buscar/descargar versiones.
- Se agregó REQUEST_INSTALL_PACKAGES para permitir la instalación solicitada por el usuario.
- No se suben medicamentos, pesos, cálculos ni datos clínicos.
- Android sigue mostrando confirmación antes de instalar.
