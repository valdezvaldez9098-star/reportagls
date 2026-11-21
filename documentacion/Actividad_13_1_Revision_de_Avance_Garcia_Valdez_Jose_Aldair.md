# UNIVERSIDAD AUTÓNOMA DE SINALOA

## LICENCIATURA EN INGENIERÍA EN SOFTWARE

### DESARROLLO MÓVIL NATIVO

**Actividad 13.1.** Revisión de avance del Proyecto Integrador

- **Alumno:** José Aldair García Valdez
- **Grupo:** 4-01
- **Profesor:** Dr. Gibrán Uriel Coronel
- **Proyecto:** ReportaGLS
- **Fecha de revisión:** 20/11/2026
- **Periodo que cubre este avance:** dos semanas de trabajo individual posteriores al avance de la Actividad 11.1

---

## 1. Resumen del avance

ReportaGLS es una aplicación móvil nativa (Android/Kotlin + Jetpack Compose) para reportar fallas de servicios públicos en Gabriel Leyva Solano, Guasave, Sinaloa (baches, alumbrado, agua, basura y otros), con foto, categoría, ubicación GPS y descripción, y dar seguimiento a los estatus **Recibido / En proceso / Resuelto**.

En las dos semanas posteriores a la primera revisión se conservó el MVP funcional y se trabajó principalmente en la **conectividad**, uno de los componentes que la lista de cotejo de esta actividad pide exponer junto con las interfaces, la persistencia y la arquitectura. La aplicación ya no depende únicamente del archivo local: incorpora un contrato REST consumido con Retrofit, OkHttp y Gson, un backend simulado que permite demostrar el flujo sin servidor externo y un `SyncManager` que reconcile los datos locales con la respuesta de la API.

El proyecto sigue siendo **individual**; todas las decisiones, implementación, pruebas y documentación corresponden al mismo autor.

---

## 2. Módulos integrados hasta el momento

| Componente | Estado | Evidencia en el código |
| --- | --- | --- |
| Interfaces Jetpack Compose | Implementado | `ui/` (login, home, report, detail, myreports, admin, common), Material 3, `NavigationBar` + FAB y `BackHandler`. |
| Arquitectura | Implementado | `domain/` (modelos), `data/` (repositorios y fuentes), `viewmodel/` (`StateFlow`), `di/AppModule.kt` (service locator). |
| Persistencia | Implementado | `data/local/ReportLocalDataSource.kt` (JSON en almacenamiento interno), `UserLocalDataSource` (SharedPreferences), fotos en almacenamiento interno. |
| Autenticación y roles | Implementado | Registro/login local con SHA-256, sesión persistente y roles ciudadano/administrador. |
| Mapa y filtros | Implementado | Mapa esquemático con `Canvas`, chips por categoría, `Slider` de radio (500 m–5 km) y ubicación con `LocationManager`. |
| Reportes y administración | Implementado | Creación con foto/fecha/ubicación, detalle con línea de tiempo, "Mis reportes" y cambio de estatus admin. |
| **Conectividad (nuevo en este avance)** | Implementado | `data/remote/ReportApiService.kt` (GET/POST/PATCH), `MockApiInterceptor.kt`, `MockBackend.kt`, `data/remote/ReportDto.kt` y `data/SyncManager.kt`. |
| **Estado de red en la UI (nuevo)** | Implementado | Tarjeta "Sincronizar / Simular sin conexión" en `ui/home/HomeScreen.kt` y campo de sincronización en `ui/detail/ReportDetailScreen.kt`. |
| **Resiliencia offline (nuevo)** | Parcial | Los reportes creados o editados sin red quedan marcados como `synced = false` y se envían en la siguiente sincronización; falta automatizar con `WorkManager`. |

### Estructura agregada en este avance

```
data/
├── SyncManager.kt              # orquesta local ↔ API y cuenta pendientes
├── remote/
│   ├── ReportApiService.kt     # Retrofit: getReports / createReport / updateStatus
│   ├── ReportDto.kt            # DTO + conversiones con el modelo de dominio
│   ├── MockBackend.kt          # almacén en memoria que simula el servidor
│   └── MockApiInterceptor.kt   # respuestas HTTP con latencia y modo sin conexión
util/NetworkHelper.kt           # comprueba NET_CAPABILITY_INTERNET
```

---

## 3. Evidencia de ejecución

- **Compilación:** `gradlew :app:assembleDebug` → **BUILD SUCCESSFUL**.
- **Pruebas unitarias:** `gradlew :app:testDebugUnitTest` → **BUILD SUCCESSFUL**.
- **APK generado:** `app/build/outputs/apk/debug/app-debug.apk`.
- **Entorno:** Android Studio + JDK incluido, compileSdk 37, minSdk 24.
- Advertencias de compilación: únicamente iconos `Icons.Filled.*` marcados como obsoletos por Material 3 (no afectan la ejecución).

### Evidencia visual pendiente de capturar en emulador/dispositivo

- [ ] Inicio de sesión como ciudadano / administrador.
- [ ] Pantalla de inicio con la **tarjeta de conectividad** ("Conectado a la API", botón **Sincronizar** y contador de pendientes).
- [ ] Pulsar **Simular sin conexión** y verificar el mensaje "Sin conexión" y el envío diferido.
- [ ] Crear un reporte y comprobar que aparece como "Pendiente de enviar" en el detalle.
- [ ] Sincronizar y comprobar el mensaje de reportes recibidos/enviados.
- [ ] Lista y mapa con filtros; panel Admin cambiando un estatus.

> Las capturas se toman en la sesión presencial porque en el entorno de desarrollo
> no hay un AVD configurado; la APK compila y el guion de demostración está listo.

---

## 4. Guion de demostración (sesión presencial)

1. Abrir la app e iniciar sesión como **ciudadano / 1234**.
2. En **Inicio**, mostrar la tarjeta de conectividad y pulsar **Sincronizar**: la primera vez inicializa la base remota con los reportes semilla.
3. Pulsar **Simular sin conexión**; crear un reporte con foto y ubicación. Verificar en el detalle que figura como "Pendiente de enviar".
4. Pulsar **Simular en línea** y luego **Sincronizar**; verificar el mensaje de sincronización y que el reporte queda marcado como sincronizado.
5. Alternar **Lista / Mapa**, mover el radio y filtrar por categorías.
6. Cerrar sesión, entrar como **admin / admin123**, cambiar el estatus de un reporte y sincronizar de nuevo.

---

## 5. Estado del repositorio

- **Raíz local:** `C:\Users\valde\AndroidStudioProjects\ReportaGLS`.
- **Repositorio público:** <https://github.com/valdezvaldez9098-star/reportagls>.
- **Rama:** `main`.

| Commit | Descripción |
| --- | --- |
| `af63fe2` | Configuración inicial de la estructura del proyecto. |
| `ddb5c57` | Avance 1: MVP funcional (auth, reportes, mapa/lista, admin). |
| `980812b` | Documentación del proyecto (actividades 1.3, 2.3, 6.1 y 11.1). |
| `e320a09` | Boceto inicial referenciado en la Actividad 1.3. |
| `a32cfbe` | Actualización del reporte 11.1 con estado del repositorio y evidencia visual. |
| `b0911c5` | **Avance 13.1:** capa REST con Retrofit/OkHttp y sincronización offline. |

---

## 6. Identificación de pendientes del backlog

Leyenda: **Hecho** en este avance, **Parcial** si quedó una parte pendiente, **Pendiente** para las siguientes iteraciones.

| # | Requerimiento / tarea | Estado | Observación |
| --- | --- | --- | --- |
| 1 | Contrato REST y capa de conectividad | Hecho | Retrofit + OkHttp + Gson con backend simulado; demostrable sin llaves de API. |
| 2 | Sincronización local ↔ remoto | Parcial | `SyncManager` manual con cola de pendientes; falta ejecución automática. |
| 3 | Persistencia local | Hecho | JSON + fotos en almacenamiento interno. |
| 4 | Backend real (Firebase Auth/Firestore/Storage) | Pendiente | El interceptor simulado debe sustituirse por el servicio definitivo. |
| 5 | Room + WorkManager | Pendiente | Evolución natural del repositorio y de la sincronización manual. |
| 6 | Google Maps Compose (API key) | Pendiente | El mapa esquemático resuelve la lógica de proximidad sin llave externa. |
| 7 | Firebase Cloud Messaging | Pendiente | Aviso al ciudadano cuando cambia el estatus. |
| 8 | Roles y reglas de seguridad en backend | Pendiente | Hoy los roles se validan en el cliente. |
| 9 | CameraX + compresión de fotos | Pendiente | Se usa el intent del sistema. |
| 10 | Pruebas de UI y de reglas de negocio | Pendiente | Las unitarias actuales validan la compilación del proyecto. |
| 11 | Geocodificación inversa y dirección real | Pendiente | Por ahora se usa una referencia escrita por el usuario. |
| 12 | Editar/eliminar reportes propios | Pendiente | Fuera del MVP actual. |
| 13 | Accesibilidad e internacionalización | Pendiente | Content descriptions parciales. |

---

## 7. Bitácora de retroalimentación y compromisos aplicados

Las observaciones técnicas registradas en la revisión se convirtieron en compromisos concretos dentro de este avance:

| Observación técnica | Compromiso de mejora | Cómo se aplicó en estas dos semanas |
| --- | --- | --- |
| La demostración anterior mostraba persistencia local, pero no evidenciaba conectividad. | Incorporar una capa REST observable desde la UI. | Se agregaron `ReportApiService`, Retrofit/OkHttp/Gson y una tarjeta de estado de conexión en Inicio. |
| No se había probado el comportamiento cuando falla la red. | Agregar un modo controlado de "sin conexión" y una cola de pendientes. | `MockApiInterceptor.forceOffline`, toggle **Simular sin conexión** y campo `synced` en cada reporte. |
| El avance debía explicar arquitectura y módulos, no solo pantallas. | Documentar las capas y el flujo de datos en el reporte y el README. | Sección 2 de este documento y apartado "Estructura del proyecto" del README. |
| El historial de GitHub debía permanecer actualizado. | Registrar el avance en un commit identificable antes del envío. | Commit `b0911c5` con todos los cambios de conectividad y README. |
| El backlog seguía muy general. | Priorizar tareas con estado y observable. | Sección 6 con 13 elementos y estado Hecho/Parcial/Pendiente. |

---

## 8. Plan de ajustes para las próximas dos semanas

| Iteración | Duración | Entregable |
| --- | --- | --- |
| I2 | Semanas 3–4 | Sustituir el backend simulado por Firebase Firestore/Storage y mantener el mismo contrato de repositorio. |
| I3 | Semanas 5–6 | Migrar la persistencia a Room y ejecutar la sincronización con `WorkManager` en segundo plano. |
| I4 | Semana 7 | Google Maps Compose con marcadores reales y notificaciones push (FCM) al cambiar estatus. |
| I5 | Semana 8 | Pruebas de UI, accesibilidad, corrección de observaciones finales y preparación de la entrega. |

---

## 9. Decisiones técnicas de este avance

- **Backend simulado con interceptor:** permite demostrar el flujo HTTP completo sin credenciales ni servidor; para producción sólo se cambia el `OkHttpClient` en `AppModule`, conservando `ReportApiService`.
- **Sincronización manual primero:** mantiene el control de la demostración y hace visible la cola de pendientes; la automatización con `WorkManager` queda en el plan I3.
- **Campo `synced` en el modelo:** hace explícito qué reportes necesitan enviarse y se persiste en JSON con valor por defecto `true` para los registros antiguos.
- **Retrofit 2.9 + OkHttp 4.12 + Gson 2.11:** versiones estables y compatibles con el resto del proyecto, sin requerir migración del catalogo de dependencias.
