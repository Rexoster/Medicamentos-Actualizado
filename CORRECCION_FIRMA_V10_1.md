# Corrección de firma v10.1

## Causa comprobada

La firma personal está almacenada en formato PKCS12.

En PKCS12, la clave privada utiliza la misma contraseña que el almacén.
El secreto `ANDROID_KEY_PASSWORD` creado inicialmente contenía otra
contraseña, por eso Gradle fallaba al descifrar la clave con:

`Given final block not properly padded`

## Corrección

- El workflow usa `ANDROID_KEYSTORE_PASSWORD` tanto para el almacén como
  para la clave privada.
- Ya no depende del valor de `ANDROID_KEY_PASSWORD`.
- La cadena Base64 se limpia antes de decodificarse.
- Se comprueba que el archivo no esté vacío.
- `keytool` valida el almacén y el alias.
- Un programa Java abre realmente la clave privada antes de ejecutar
  Gradle.
- Gradle conserva respaldo para contraseñas de clave separadas en otros
  almacenes, pero el workflow actual pasa la contraseña PKCS12 correcta.

El secreto antiguo `ANDROID_KEY_PASSWORD` puede quedarse en GitHub; el
workflow ya no utiliza su valor.
