# Corrección v19.1 · Alcance de variables en ruedas

## Error corregido

El aro exterior del gestograma usaba por accidente:

- `activeRing`
- `ringInteraction`

Esas variables pertenecen exclusivamente a `DateOrbitWheel`.
También se había retirado `outline` del gestograma, aunque todavía
se utiliza para dibujar separadores y el área posterior a la FPP.

## Solución

- El gestograma usa ahora `gestogramInteraction`.
- `outline` fue restaurado dentro de `GestogramWheel`.
- La animación del aro de meses se colocó en `DateOrbitWheel`,
  que es donde existen `activeRing` y `ringInteraction`.
- La línea radial azul sigue eliminada.
- El aro azul de interacción sigue eliminado.
- Se mantiene el movimiento fluido y el ajuste suave al soltar.
