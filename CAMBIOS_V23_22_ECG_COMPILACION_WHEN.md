# Cambios v23.22 · Corrección de compilación ECG

## Corrección

Se corrigió el error de compilación en `EcgScreen.kt`:

```text
when expression must be exhaustive. Add an else branch.
```

El problema estaba en el cálculo de rectángulos para sombreado de criterios ECG. Kotlin exigía una rama de respaldo en el `when` usado como expresión.

## Archivos modificados

- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `version.properties`

## Versión

- `VERSION_CODE=58`
- `VERSION_NAME=3.16.22-native`
