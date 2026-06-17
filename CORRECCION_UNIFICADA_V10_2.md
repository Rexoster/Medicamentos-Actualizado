# Corrección unificada v10.2

Este paquete corrige simultáneamente dos problemas.

## 1. Archivos de edad gestacional por ultrasonido

El repositorio tenía el preflight y la interfaz actualizados, pero no
recibió:

- app/src/main/java/com/luisangel/calculadoramedicamentos/
  obstetrics/UltrasoundDating.kt
- app/src/test/java/com/luisangel/calculadoramedicamentos/
  obstetrics/UltrasoundDatingTest.kt

El parche incluye ambos archivos, además de App.kt y GrowthEngine.kt
compatibles con ellos.

## 2. Firma PKCS12

El workflow release usa ahora la contraseña del almacén PKCS12 como
contraseña de la clave privada.

También valida antes de compilar:

- Base64.
- Archivo de firma.
- Alias.
- Contraseña del almacén.
- Acceso real a la clave privada.

El secreto ANDROID_KEY_PASSWORD puede permanecer en GitHub, pero el
workflow no depende de su valor anterior.

## Uso

Copia el parche sobre la raíz del repositorio, acepta reemplazar los
archivos, haz Commit y Push y ejecuta:

Actions > Generar actualización firmada > Run workflow
