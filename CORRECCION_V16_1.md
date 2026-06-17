# Corrección v16.1

- Se restauró el componente `TableCell`.
- Se corrigieron los errores `Unresolved reference: TableCell`.
- Al recuperar el contexto composable también desaparecen los errores
  secundarios de `Composable invocations can only happen...`.
- Se conserva toda la optimización de la versión 16.
- El preflight ahora comprueba que `TableCell` y `TableTextCell`
  existan antes de compilar.
