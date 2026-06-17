# Cambios v10

- Nuevo calendario clínico completamente nativo y personalizado.
- Encabezado visual con día, mes y fecha completa.
- Navegación mensual, días deshabilitados, indicador de hoy y selección llamativa.
- Diseño adaptable a celular y tableta.
- Apache POI/Excel deja de inicializarse durante el arranque.
- Conversión de entidades Room y filtros se ejecutan fuera del hilo principal.
- Tabla de medicamentos usa renderizado perezoso y no compone todas las filas de golpe.
- Cálculos de percentiles y carga LMS se ejecutan en Dispatchers.Default.
- Resultados de conteo y pestaña visible usan estado derivado.
