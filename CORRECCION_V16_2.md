# Corrección v16.2 · Esquema Room

## Error corregido

Room fallaba durante `kaptDebugKotlin` porque intentaba deserializar
un JSON de esquema corrupto conservado en `app/schemas`.

Mensaje principal:

`JsonDecodingException: Unexpected JSON token ...`
`path: $.database.entities[0]`

## Solución

- Los esquemas de Room ahora se generan en:
  `app/build/generated/room-schemas`
- Los workflows eliminan `app/schemas` antes de compilar.
- `.gitignore` excluye completamente `app/schemas/`.
- El workflow debug ya no ejecuta `assembleDebug` dos veces.
- La migración Room 1 a 2 se conserva.
- Esta corrección no borra la base local ni los medicamentos.
