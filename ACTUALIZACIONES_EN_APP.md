# Actualizaciones dentro de la aplicación

## Cómo funciona

1. Ejecutas el workflow `Generar actualización firmada`.
2. GitHub crea una Release con:
   - APK firmada.
   - AAB.
   - `update.json`.
3. La app consulta:
   `https://github.com/<owner>/<repo>/releases/latest/download/update.json`
4. Si el `versionCode` publicado es mayor al instalado, permite descargar e instalar.

## Requisitos

- La Release debe ser pública para que la app pueda descargarla sin token.
- La APK debe estar firmada con la misma clave.
- El `applicationId` debe mantenerse:
  `com.luisangel.calculadoramedicamentos`.
- En Android 8 o superior, el usuario debe autorizar una vez:
  `Permitir instalar apps de esta fuente`.

## Limitación importante

Android no permite instalación silenciosa para apps normales.
Siempre aparecerá el instalador del sistema para confirmar.
