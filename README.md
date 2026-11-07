# ReportaGLS

Aplicación móvil nativa (Android) para que los habitantes de Gabriel Leyva Solano, Guasave,
Sinaloa, reporten fallas de servicios públicos (baches, alumbrado, agua, basura) con foto,
categoría y ubicación GPS, y les den seguimiento hasta su resolución.

Proyecto integrador — IS26 Desarrollo Móvil Nativo.
Autor: José Aldair García Valdez.

## Ficha técnica

|Elemento|Detalle|
|-|-|
|IDE|Android Studio Quail 3 \| 2026.1.3|
|Lenguaje|Kotlin 2.2.10|
|Paquete|`com.gls.reportagls`|
|SDK mínimo (minSdk)|24 (Android 7.0)|
|SDK objetivo (targetSdk)|37|
|SDK compilación (compileSdk)|37|
|Gestor de dependencias|Gradle (Kotlin DSL) 9.6.0|
|Sistema de UI|Jetpack Compose + Material 3|
|Arquitectura|MVVM con separación por capas (ui / domain / data / di)|
|Control de versiones|Git + GitHub|

## Dependencias principales

* AndroidX Core KTX, Lifecycle y Activity Compose
* Jetpack Compose (BOM) + Material 3
* Lifecycle ViewModel Compose (`StateFlow`)
* Cámara por intent del sistema (`TakePicturePreview`)
* Ubicación del sistema (`LocationManager`)
* Persistencia local en JSON + almacenamiento interno de fotografías
* Coroutines / Flow (estado reactivo)

> Planeadas para la entrega final: Firebase (Auth, Firestore, Storage, FCM),
> Google Maps Compose, CameraX y Room + WorkManager para sincronización offline.

## Estructura del proyecto

```
app/src/main/java/com/gls/reportagls/
├── MainActivity.kt
├── ReportaGlsApplication.kt   # Application + contenedor de dependencias
├── ui/                        # Pantallas Compose, organizadas por feature
│   ├── login/                 # Registro e inicio de sesión
│   ├── home/                  # Mapa esquemático + lista de reportes cercanos
│   ├── report/                # Formulario para crear un nuevo reporte
│   ├── detail/                # Detalle y estatus de un reporte
│   ├── myreports/             # Historial personal filtrable por estatus
│   ├── admin/                 # Cambio de estatus (rol administrador)
│   ├── common/ theme/         # Componentes reutilizables y tema Material 3
│   └── ReportaGlsApp.kt       # Navegación y Scaffold principal
├── domain/                    # Modelos de dominio (Report, User, LatLng)
├── data/                      # Repositorios y acceso a datos
│   └── local/                 # Persistencia local (JSON / SharedPreferences)
├── viewmodel/                 # Estado único de la app (StateFlow)
├── util/                      # Geo, fotos y ubicación
└── di/                        # Contenedor de dependencias
```

Esta separación mantiene la UI, la lógica de negocio y el acceso a datos independientes
entre sí, siguiendo el criterio de separación de responsabilidades visto en clase.

## Cómo ejecutar el proyecto

1. Clonar el repositorio: `git clone https://github.com/valdezvaldez9098-star/reportagls.git`
2. Abrir la carpeta en Android Studio.
3. Dejar que Gradle sincronice las dependencias.
4. Ejecutar en un emulador o dispositivo con Android 7.0 o superior.

Cuentas de demostración:

* Ciudadano: `ciudadano` / `1234`
* Administrador: `admin` / `admin123`

## Estado del proyecto

Primer avance (MVP funcional, Actividad 11.1): autenticación local con roles,
creación y consulta de reportes, mapa esquemático y lista con filtros por
categoría y radio, detalle con estatus, historial "Mis reportes" y panel de
administración. La persistencia es local y funciona sin conexión.

Pendiente para la entrega final: backend Firebase (Auth, Firestore, Storage),
Google Maps Compose, notificaciones push, CameraX, Room + WorkManager y pruebas.

