# Corrección v8.1

## Error corregido

La primera versión del parche v8 incluía `App.kt`, pero omitía
`GrowthEngine.kt`.

Por esa razón GitHub mostraba errores como:

- `Unresolved reference: NutritionStatus`
- `Unresolved reference: NutritionSummary`
- `Unresolved reference: reference`
- `Unresolved reference: bmiPercentile`
- `Unresolved reference: overweightFromKg`
- `Unresolved reference: obesityFromKg`

## Archivos que deben actualizarse juntos

- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/App.kt`
- `app/src/main/java/com/luisangel/calculadoramedicamentos/growth/GrowthEngine.kt`
- `version.properties`

El nuevo parche contiene ambos archivos Kotlin y puede aplicarse sobre v6.1 o v7.
