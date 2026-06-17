# Corrección v16.3 · Conflicto KAPT de Room

## Causa

`kaptDebugKotlin` y `kaptReleaseKotlin` se ejecutaban en paralelo.
Ambos intentaban escribir y leer el mismo archivo de esquema Room
`2.json`. Uno de los procesos encontraba el archivo incompleto y
fallaba con:

`Expected end of the object '}', but had 'EOF'`

## Solución

- `exportSchema` se cambió a `false`.
- Se eliminó `room.schemaLocation`.
- Los workflows ya no usan `--parallel`.
- Antes de compilar se eliminan `app/build` y `app/schemas`.
- La caché de dependencias y Gradle se conserva.
- La migración manual Room 1 a 2 permanece activa.
- Los medicamentos guardados no se eliminan.
