# UNIVERSIDAD AUTÓNOMA DE SINALOA

## INGENIERÍA EN SOFTWARE

- **Alumno:** José Aldair García Valdez
- **Grupo:** 4-01
- **Materia:** Desarrollo Móvil Nativo
- **Profesor:** Dr. Gibrán Uriel López Coronel
- **Actividad:** Actividad 6.1. Análisis de requerimientos de UI
- **Fecha de Entrega:** 06/10/2026

---

## Matriz de Selección de Controles

**Proyecto integrador:** ReportaGLS — aplicación móvil nativa (Android/Kotlin) para que los habitantes de Gabriel Leyva Solano, Guasave, Sinaloa, reporten fallas de servicios públicos (baches, alumbrado, agua, basura) con foto, categoría, ubicación GPS y descripción, y les den seguimiento mediante un mapa/lista de reportes cercanos y un historial personal con estatus.

A partir de los requerimientos funcionales de la interfaz de usuario del proyecto, se identificó el tipo de dato de cada campo (opciones discretas, selección múltiple, valores continuos, fechas y horas) y se relacionó con su control avanzado óptimo según las guías de Material Design, priorizando la experiencia de usuario y la prevención de errores.

| Requerimiento funcional (pantalla) | Dato a capturar | Tipo de dato | Control UI seleccionado | Justificación (UX / prevención de errores) |
| --- | --- | --- | --- | --- |
| Crear reporte — Categoría de la falla | bache, alumbrado, agua, basura, otro | Opción discreta, selección única | Chips (FilterChip, selección única) | Son solo 5 opciones fijas y visibles de un vistazo; evita texto libre mal escrito y da feedback visual inmediato de cuál está activa. |
| Crear reporte — Fecha en que se detectó la falla (si no es "ahora mismo") | Fecha puntual | Fecha | DatePicker | Evita errores de formato (día/mes/año) y garantiza una fecha válida; solo aparece si el usuario indica que la falla fue detectada otro día. |
| Crear reporte — Hora aproximada de la falla | Hora puntual | Hora | TimePicker | Mismo beneficio que el DatePicker: sin ambigüedad AM/PM ni formatos inválidos, con menos fricción que escribirla manualmente. |
| Mapa/lista — Filtrar reportes por distancia desde mi ubicación | Radio de búsqueda (ej. 500 m – 5 km) | Valor numérico continuo dentro de un rango | Slider | Comunica visualmente el límite mínimo/máximo y evita que el usuario escriba un número fuera de rango o con formato inválido. |
| Mapa/lista — Filtrar por una o varias categorías a la vez | bache, alumbrado, agua, basura, otro | Selección múltiple de opciones discretas | Chips (FilterChip, multi-select) | A diferencia del formulario de creación, aquí se pueden combinar categorías; cada chip se activa/desactiva de forma independiente. |
| Historial "Mis reportes" — Filtrar por estatus | Recibido / En proceso / Resuelto | Opción discreta y fija (3 estados excluyentes) | SegmentedButton | Con solo 3 estados siempre visibles, deja claro cuál está activo sin abrir un menú extra; menos pasos que un dropdown. |
| Admin — Cambiar el estatus de un reporte | Recibido / En proceso / Resuelto | Opción discreta y fija (3 estados) | Dropdown (ExposedDropdownMenu) | Pantalla de uso poco frecuente (solo administrador); reutiliza el mismo patrón de Dropdown ya usado en prácticas anteriores, manteniendo el código simple y consistente. |

> **Nota sobre el alcance:** dado que ReportaGLS se desarrollará de forma individual durante aproximadamente 4 meses, se decidió no incluir un `RangeSlider`/`DateRangePicker` para filtrar reportes por rango de fechas; en su lugar, se usarán Chips rápidos ("Hoy", "Esta semana", "Este mes") que ofrecen una experiencia similar con bastante menos código que implementar y mantener un selector de rango de fechas.
