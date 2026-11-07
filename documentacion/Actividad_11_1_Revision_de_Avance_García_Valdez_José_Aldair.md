# UNIVERSIDAD AUTÓNOMA DE SINALOA

## LICENCIATURA EN INGENIERÍA EN SOFTWARE

### DESARROLLO MÓVIL NATIVO

**Actividad 11.1.** Revisión de avance del Proyecto Integrador

- **Alumno:** José Aldair García Valdez
- **Grupo:** 4-01
- **Profesor:** Dr. Gibrán Uriel Coronel
- **Proyecto:** ReportaGLS
- **Fecha de revisión:** 06/11/2026

---

## 1. Resumen del avance

ReportaGLS es una aplicación móvil nativa (Android/Kotlin + Jetpack Compose) para
que los habitantes de Gabriel Leyva Solano, Guasave, Sinaloa, reporten fallas de
servicios públicos (baches, alumbrado, agua, basura y otros) con foto, categoría,
ubicación GPS y descripción, y les den seguimiento mediante estatus
(**Recibido / En proceso / Resuelto**).

En este avance (equivalente a dos meses de trabajo individual) ya está implementado
un **producto mínimo viable funcional** con arquitectura por capas (MVVM +
`ui / domain / data / di`), persistencia local y navegación completa entre pantallas.

### Funcionalidades implementadas

| Módulo | Estado | Descripción |
| --- | --- | --- |
| Autenticación local | Implementado | Registro e inicio de sesión, sesión persistente, roles **Ciudadano** y **Administrador** (contraseñas con SHA-256). |
| Pantalla de inicio | Implementado | Lista de reportes cercanos ordenados por distancia y **mapa esquemático** dibujado con `Canvas`. |
| Filtros | Implementado | Chips multi-selección por categoría y `Slider` de radio de búsqueda (500 m – 5 km). |
| Ubicación | Implementado | Permisos en tiempo de ejecución y obtención de última ubicación con `LocationManager` (sin Google Play Services). |
| Crear reporte | Implementado | Foto con la cámara, categoría (selección única), descripción, fecha/hora de detección (`DatePicker`/`TimePicker`) y referencia de ubicación. |
| Validaciones | Implementado | Categoría obligatoria, descripción obligatoria y valores de fecha/hora válidos. |
| Detalle del reporte | Implementado | Foto, información, línea de tiempo de estatus y distancia. |
| Mis reportes | Implementado | Historial personal filtrable por estatus mediante chips. |
| Administración | Implementado | Panel con conteos por estatus y cambio de estatus por `DropdownMenu` (rol admin). |
| Persistencia | Implementado | Reportes en archivo JSON local + fotografías en almacenamiento interno (funciona sin conexión). |
| Tema / UI | Implementado | Material 3 con paleta propia, navegación por estado y `BackHandler`, `NavigationBar` + FAB. |

### Arquitectura y estructura

```
com.gls.reportagls/
├── MainActivity.kt
├── ReportaGlsApplication.kt        # Application + contenedor de dependencias
├── di/AppModule.kt                 # service locator (repositorios)
├── domain/                         # Report, ReportCategory, ReportStatus, User, LatLng
├── data/
│   ├── ReportRepository.kt
│   ├── UserRepository.kt
│   └── local/                      # ReportLocalDataSource (JSON), UserLocalDataSource (prefs)
├── viewmodel/ReportaGlsViewModel.kt # estado único (StateFlow) + sesión/filtros
├── ui/
│   ├── ReportaGlsApp.kt            # navegación y scaffold
│   ├── login/ home/ report/ detail/ myreports/ admin/ common/ theme/
└── util/                           # Geo (Haversine), PhotoStore, LocationHelper
```

> **Nota:** se ajustó el paquete a `com.gls.reportagls` para que coincida con la
> ficha técnica y la estructura documentadas en la Actividad 2.3.

---

## 2. Evidencia de ejecución

- **Compilación:** `gradlew :app:assembleDebug` → **BUILD SUCCESSFUL**.
- **Pruebas unitarias:** `gradlew :app:testDebugUnitTest` → **BUILD SUCCESSFUL**.
- **APK generado:** `app/build/outputs/apk/debug/app-debug.apk` (~11.7 MB).
- **Entorno:** Android Studio + JDK incluido, SDK de compilación 37, `minSdk` 24.

### Evidencia visual (capturas de la app)

Adjuntar capturas tomadas del emulador o dispositivo físico:

- [ ] Inicio de sesión como ciudadano.
- [ ] Pantalla de inicio en modo **Lista** con filtros por categoría y radio.
- [ ] Pantalla de inicio en modo **Mapa** con los marcadores.
- [ ] Formulario de **nuevo reporte** (foto, categoría, fecha/hora y ubicación).
- [ ] **Detalle** del reporte con la línea de tiempo de estatus.
- [ ] **Mis reportes** filtrado por estatus.
- [ ] Panel **Admin** cambiando el estatus de un reporte.

### Guion de demostración (para la sesión)

1. Abrir la app e iniciar sesión como **ciudadano / 1234**.
2. En **Inicio**, alternar **Lista** y **Mapa**, mover el `Slider` de radio y
   activar/desactivar categorías con los chips.
3. Presionar **+** y crear un reporte: tomar foto, elegir categoría, escribir la
   descripción, desactivar "ocurrió ahora mismo" y elegir fecha/hora, usar la
   ubicación actual y enviar. El reporte aparece en el mapa/lista.
4. Abrir el reporte y revisar la línea de tiempo de estatus.
5. Ir a **Mis reportes** y filtrar por estatus.
6. Cerrar sesión, entrar como **admin / admin123**, abrir **Admin** y cambiar el
   estatus de un reporte; volver a **Mis reportes** (como ciudadano) para verlo.

> La APK compila correctamente y el guion anterior está listo para ejecutarse en
> el emulador o dispositivo físico. Para la evidencia visual se deben capturar
> las pantallas de la lista anterior.

---

## 3. Estado del repositorio

- **Raíz del repositorio:** `C:\Users\valde\AndroidStudioProjects\ReportaGLS`.
- **Repositorio en GitHub:** <https://github.com/valdezvaldez9098-star/reportagls>.
- **Rama `main` sincronizada con `origin/main`.**
- La documentación del proyecto quedó versionada en la carpeta `documentacion/`.

Commits que componen el avance:

| Commit | Descripción |
| --- | --- |
| `af63fe2` | Configuración inicial de la estructura del proyecto. |
| `ddb5c57` | Avance 1: MVP funcional (auth, reportes, mapa/lista, admin). |
| `980812b` | Documentación del proyecto (actividades 1.3, 2.3, 6.1 y 11.1). |
| `e320a09` | Boceto inicial referenciado en la Actividad 1.3. |

---

## 4. Requerimientos pendientes (priorizados)

### Prioridad 1 — indispensable para la entrega final

| # | Pendiente | Justificación |
| --- | --- | --- |
| 1 | Backend real (Firebase Auth + Firestore + Storage) y sincronización | Hoy la persistencia es local; se requiere que los reportes sean compartidos entre usuarios/dispositivos. |
| 2 | Sincronización offline con `WorkManager` + `Room` | Cumplir el requisito de crear reportes sin conexión y sincronizarlos al recuperar señal. |
| 3 | Mapa real con Google Maps Compose (API key) | El mapa actual es esquemático (`Canvas`); se requiere geolocalización real. |
| 4 | Notificaciones push (Firebase Cloud Messaging) al cambiar estatus | Requisito funcional del proyecto (aviso al ciudadano). |
| 5 | Roles reales y permisos en backend | Evitar que un ciudadano modifique estatus desde el cliente. |
| 6 | Migrar autenticación local a Firebase Auth | Seguridad y recuperación de cuenta. |
| 7 | CameraX + almacenamiento remoto de fotos | Sustituir el intent de cámara y subir evidencias a Storage. |
| 8 | Pruebas unitarias de reglas de negocio y pruebas de UI | Calidad y regresión antes de la entrega. |

### Prioridad 2 — experiencia de usuario

| # | Pendiente |
| --- | --- |
| 9 | Chips rápidos de fecha ("Hoy", "Esta semana", "Este mes") según la Actividad 6.1. |
| 10 | Geocodificación inversa para mostrar la dirección real. |
| 11 | Varias fotos por reporte y compresión de imagen. |
| 12 | Editar/eliminar reportes propios y estados de carga/error. |
| 13 | Accesibilidad (content descriptions, tamaños táctiles) e internacionalización. |

### Prioridad 3 — extras

| # | Pendiente |
| --- | --- |
| 14 | Panel admin con asignación de responsables y comentarios de seguimiento. |
| 15 | Estadísticas/exportación de reportes por categoría y periodo. |

---

## 5. Plan de ajuste (dos meses restantes)

| Iteración | Duración | Entregable |
| --- | --- | --- |
| I1 | Semanas 1–2 | Firebase Auth + Firestore; lectura/escritura de reportes en la nube. |
| I2 | Semanas 3–4 | Firebase Storage (fotos) + CameraX + sincronización offline (Room/WorkManager). |
| I3 | Semanas 5–6 | Google Maps Compose con marcadores y filtros geográficos. |
| I4 | Semanas 7 | Notificaciones push (FCM) y endurecimiento de reglas de seguridad. |
| I5 | Semana 8 | Pruebas, accesibilidad, corrección de retroalimentación y preparación de la entrega final. |

### Registro de retroalimentación recibida en la sesión

> Completar durante/después de la revisión presencial. Cada observación se
> convierte en una tarea del backlog con su prioridad y se refleja en el plan.

| Retroalimentación técnica | Ajuste acordado | Prioridad | Iteración |
| --- | --- | --- | --- |
| (pendiente de registrar) | | | |

---

## 6. Decisiones tomadas para este avance

- **Mapa esquemático en lugar de Google Maps:** permite demostrar la lógica de
  proximidad y filtros sin depender de una API key ni de conexión; el mapa real
  queda como pendiente P1.
- **Persistencia en JSON en lugar de Room:** reduce el riesgo del primer avance y
  mantiene la interfaz de repositorio, de modo que el cambio a Room no afectará
  a la UI.
- **Cámara por intent (`TakePicturePreview`):** captura funcional en emulador sin
  configurar `FileProvider`; CameraX queda como mejora.
- **Autenticación local con hash SHA-256:** permite probar roles y flujos de
  inmediato; se sustituirá por Firebase Auth.
