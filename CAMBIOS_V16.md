# Cambios v16 · Optimización

## Rendimiento de la app

- Las listas de sugerencias ya no se observan al iniciar la pantalla.
- Familias, subgrupos, frecuencias y especialidades se calculan solo al abrir editor o filtros.
- El sembrado inicial de ejemplos se difiere 450 ms y se ejecuta en IO.
- Los modelos principales se marcaron como `@Immutable` para que Compose pueda saltar recomposiciones.
- La tabla compone menos filas visibles de arranque.
- Las filas de la tabla usan `contentType` estable.
- Room usa WAL y ejecutores dedicados para consultas y transacciones.
- El filtrado evita crear listas temporales por cada medicamento.

## Compilación

- Gradle usa caché, paralelo e incremental.
- GitHub Actions usa caché de Java/Gradle.
- El workflow release redujo dos llamadas Gradle a una sola.
- El SDK se instala de forma más ligera.
