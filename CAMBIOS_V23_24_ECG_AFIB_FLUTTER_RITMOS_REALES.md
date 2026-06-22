# Cambios v23.24 - FA y flutter con trazos propios

## Correcciones

### Fibrilación auricular
- Se dejó de usar el molde P-QRS-T normal para FA.
- Se agregó una línea de base fibrilatoria continua, fina e irregular.
- Los intervalos RR se marcan de R a R, no desde zonas desplazadas.
- El criterio "Sin P" ahora marca actividad auricular desorganizada, no una falsa zona de onda P.

### Flutter auricular
- Se dejó de dibujar el flutter como ondas pegadas al QRS.
- Se agregó actividad auricular tipo serrucho continua.
- Las ondas F se muestran con más amplitud en derivaciones inferiores y menor amplitud en derivaciones donde no son el foco.
- QRS y onda T se dibujan encima de las ondas F para que no desaparezca la T.

### ECG completo, áreas e irrigación
- El sombreado de criterios en FA/flutter usa tiempos reales del trazo.
- Se mantienen más criterios visibles sin llenar toda la celda.

## Archivos modificados
- `app/src/main/java/com/luisangel/calculadoramedicamentos/ui/ecg/EcgScreen.kt`
- `version.properties`
