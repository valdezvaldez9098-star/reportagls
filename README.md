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
|Lenguaje|Kotlin 2.3|
|SDK mínimo (minSdk)|24 (Android 7.0)|
|SDK objetivo (targetSdk)|34 (Android 14)|
|SDK compilación (compileSdk)|34|
|Gestor de dependencias|Gradle (Kotlin DSL) 9.5.0|
|Sistema de UI|Jetpack Compose|
|Arquitectura|MVVM con separación por capas (ui / domain / data)|
|Control de versiones|Git + GitHub|

## 

## Dependencias principales

* AndroidX Core KTX, Lifecycle, Activity Compose
* Jetpack Compose (BOM) + Material 3
* Navigation Compose
* CameraX (captura de fotografía del reporte)
* Google Play Services Location (GPS del reporte)
* Retrofit + OkHttp (comunicación con backend/Firebase, si aplica)
* Room (caché local de reportes para uso sin conexión)
* Coroutines (operaciones asíncronas)

## Estructura del proyecto

```
app/src/main/java/com/gls/reportagls/
├── ui/            # Pantallas y ViewModels (Compose), organizadas por feature
│   ├── home/      # Pantalla principal: mapa + lista de reportes cercanos
│   ├── report/    # Formulario para crear un nuevo reporte
│   └── detail/    # Pantalla de detalle y estatus de un reporte
├── domain/        # Modelos de dominio y casos de uso (lógica de negocio pura)
├── data/          # Acceso a datos
│   ├── local/     # Room (persistencia local / modo sin conexión)
│   └── remote/    # Firebase / API REST (Retrofit)
└── di/            # Módulos de inyección de dependencias
```

Esta separación mantiene la UI, la lógica de negocio y el acceso a datos independientes
entre sí, siguiendo el criterio de separación de responsabilidades visto en clase.

## Cómo ejecutar el proyecto

1. Clonar el repositorio: `git clone https://github.com/valdezvaldez9098-star/reportagls.git`
2. Abrir la carpeta en Android Studio.
3. Dejar que Gradle sincronice las dependencias.
4. Ejecutar en un emulador o dispositivo con Android 7.0 o superior.

## Estado del proyecto

Fase actual: configuración de la estructura base del proyecto e inicialización del
repositorio (Actividad 2.3). Aún no se ha implementado funcionalidad.

