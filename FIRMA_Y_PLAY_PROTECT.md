# Firma y distribución segura de la APK

La aplicación no puede desactivar Google Play Protect. Una APK `debug` instalada desde GitHub puede mostrar una advertencia porque procede de una fuente desconocida y usa una clave de depuración.

## Opción recomendada

Publica la versión firmada mediante Google Play Console usando una prueba interna o cerrada. Los usuarios reciben la aplicación desde Google Play y las actualizaciones conservan la misma firma.

## Crear una clave propia

En una computadora con Java ejecuta:

```bash
keytool -genkeypair -v -keystore calculadora-release.jks -alias calculadora -keyalg RSA -keysize 2048 -validity 10000
```

Guarda el archivo y las contraseñas. No subas el `.jks` al repositorio.

## Convertir a Base64

Linux/macOS:

```bash
base64 -w 0 calculadora-release.jks > keystore.txt
```

PowerShell:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("calculadora-release.jks")) | Set-Content keystore.txt
```

## Crear secretos de GitHub

En `Settings > Secrets and variables > Actions` crea:

- `ANDROID_KEYSTORE_BASE64`: contenido de `keystore.txt`
- `ANDROID_KEYSTORE_PASSWORD`: contraseña del almacén
- `ANDROID_KEY_ALIAS`: alias, por ejemplo `calculadora`
- `ANDROID_KEY_PASSWORD`: contraseña de la clave

Después ejecuta `Actions > Generar APK release firmada`.

## Importante

- Conserva siempre la misma clave para publicar actualizaciones.
- Una APK firmada fuera de Google Play todavía puede recibir advertencias de “fuente desconocida”.
- La vía con menos bloqueos es Google Play Internal Testing o Closed Testing.
- No desactives Play Protect de forma permanente.

## AAB para Google Play

El flujo de release genera también `app-release.aab`. Ese es el archivo recomendado para crear una prueba interna o cerrada en Google Play Console.


## Actualizaciones sin desinstalar

Esta versión incluye el workflow `Generar actualización firmada`.

Después de configurar los cuatro secretos una sola vez, todas las ejecuciones
usan la misma clave y un `versionCode` automático superior. No vuelvas a usar
la APK del workflow debug para instalar actualizaciones.

La primera transición desde la APK debug exige exportar, desinstalar e instalar
la release firmada. Las versiones posteriores se instalan encima y conservan
la base local.


## Corrección PKCS12 v10.1

La firma generada es PKCS12. El workflow usa la contraseña del almacén
también como contraseña de la clave privada. No necesitas modificar ni
borrar el secreto `ANDROID_KEY_PASSWORD`; simplemente ya no se utiliza.

Antes de compilar, GitHub valida tanto el almacén como la clave privada.
