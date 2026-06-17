# Corrección v8.2

`FormTextField` ahora acepta un sufijo opcional para unidades como mm y g.

Se añadió un preflight a GitHub Actions que comprueba:

- Firma y parámetros del componente reutilizable.
- Llamadas con sufijo.
- Coherencia entre App.kt y GrowthEngine.kt.
- Estructura básica de Kotlin.
- Versionado.
- Ausencia de WebView, Supabase y permiso INTERNET.

Los workflows ejecutan explícitamente `compileDebugKotlin` con stacktrace
antes de crear APK y AAB.
