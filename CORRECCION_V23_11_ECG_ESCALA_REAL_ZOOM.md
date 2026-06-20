# Corrección v23.11 · ECG con escala real y zoom de cuadrícula

## Resumen
Se corrige la vista previa del electrocardiograma para que la rejilla del papel ECG escale junto con el trazado.

## Cambios
- La cuadrícula ya no usa un espaciado fijo independiente del zoom.
- Cada cuadro pequeño se maneja como unidad de 1 mm del papel ECG.
- Cada cuadro grande corresponde a 5 cuadros pequeños.
- La escala horizontal se calcula con papel estándar de 25 mm/s.
- El trazo usa la misma escala que la rejilla, por lo que FC, RR, PR, QRS y QT se representan con proporción temporal más coherente.
- Al pellizcar o usar botones de zoom, también crecen o disminuyen los cuadros pequeños y grandes.
- La vista individual y el ECG de 12 derivaciones ajustan su tamaño para conservar proporciones visuales de papel ECG.

## Nota clínica
La vista sigue siendo una simulación didáctica generada por parámetros. No sustituye un ECG real ni una interpretación clínica.

## Archivos modificados
- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `version.properties`
