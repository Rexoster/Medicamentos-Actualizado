# Optimización y calendarios v10

## Calendarios

Todos los campos de fecha utilizan un calendario Compose propio:

- Tarjeta de fecha con día y mes destacados.
- Encabezado grande dentro del diálogo.
- Navegación por meses.
- Semana iniciando en lunes.
- Fechas fuera del rango desactivadas.
- Día actual resaltado.
- Botón rápido Hoy cuando está permitido.
- Diseño claro y oscuro.
- Ancho adaptable para teléfonos y tabletas.

## Arranque

La causa principal de carga innecesaria era la creación de `ExcelService` al
construir el ViewModel. Esa clase depende de Apache POI y ahora se crea solo
cuando el usuario importa o exporta un archivo.

También se aplicó:

- Conversión Room fuera del hilo principal.
- Filtros y listas de sugerencias en Dispatchers.Default.
- Tabla con LazyColumn e itemsIndexed.
- Máximo de 4 filas visibles en móvil y 7 en tableta antes del desplazamiento.
- Conteos y pestaña visible con derivedStateOf.
- Cálculo y carga de tablas LMS de percentiles en Dispatchers.Default.
