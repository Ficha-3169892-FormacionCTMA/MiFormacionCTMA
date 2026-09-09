# Informe de Desarrollo: Mi Formación CTMA

## Actividad: Desarrollo de Aplicación Móvil con Concurrencia y Estado Reactivo
**Responsable Técnico:** Wilson Castro Gil  
**Coordinación de Proyecto:** Equipo de Desarrollo (4 integrantes)  
**Rama Principal de Trabajo:** `feature/semana-07-coroutines-flow`  
**Scrum Master:** Thomas

---

## 1. Contexto del Proyecto
"Mi Formación CTMA" evoluciona hacia una arquitectura totalmente asíncrona y reactiva. En esta fase, se integra la gestión de corrutinas de Kotlin y flujos reactivos (Flow/StateFlow) para manejar la persistencia de datos y el estado de la interfaz de usuario de manera eficiente, respetando el ciclo de vida de Android y evitando bloqueos en el hilo principal.

---

## 2. Arquitectura y Tecnologías (Semana 7 - MAD Stack)
La aplicación consolida su **Arquitectura de Capas** con un enfoque reactivo:
*   **Lenguaje:** Kotlin 2.4.10 (Compilador K2).
*   **Concurrencia:** **Kotlin Coroutines** para operaciones asíncronas no bloqueantes.
*   **Flujos Reactivos:** **Flow** y **StateFlow** para el transporte y exposición de estados.
*   **Ciclo de Vida:** **Lifecycle Runtime Compose** para una recolección de flujos segura.
*   **UI Toolkit:** Jetpack Compose con Material Design 3.
*   **Persistencia:** Room 3.0.2 y Preferences DataStore 1.2.1.

---

## 3. Implementaciones Detalladas (Semana 7)

### A. Gestión de Estado UI (UiState)
*   **Modelado de Estados**: Implementación de `sealed interface` para `ListadoUiState` (Cargando, Vacio, Contenido, Error) y `OperacionUiState` (Inactiva, EnCurso, Exitosa, Fallida).
*   **Exposición Segura**: Uso de `stateIn` con la política `SharingStarted.WhileSubscribed(5_000)` para optimizar el uso de recursos y mantener el estado durante cambios de configuración (rotación).

### B. Concurrencia Estructurada
*   **ViewModelScope**: Todo el trabajo asíncrono de UI se liga al ciclo de vida del ViewModel, garantizando la cancelación automática al cerrar la pantalla.
*   **Búsqueda Cancelable**: Uso del operador `flatMapLatest` en la barra de búsqueda para cancelar automáticamente consultas obsoletas cuando el usuario escribe rápidamente, priorizando siempre la entrada más reciente.
*   **Main-Safety**: Las operaciones de escritura (guardar/eliminar) se ejecutan de forma segura sin bloquear la interfaz, comunicando progreso y errores mediante `OperacionUiState`.

### C. Integración Compose y Lifecycle
*   **Recolección Consciente**: Migración de `collectAsState` a **`collectAsStateWithLifecycle`**, asegurando que la aplicación deje de consumir datos cuando la interfaz no es visible para el usuario.

---

## 4. Validación de Casos de Aceptación (CA)

| Caso | Escenario | Resultado |
| :--- | :--- | :--- |
| **CA-01** | Abrir sin actividades | Visualización correcta de `ListadoUiState.Vacio`. |
| **CA-02** | Insertar actividad | Actualización reactiva instantánea vía Flow de Room. |
| **CA-03** | Reiniciar con filtros | Restauración exitosa desde DataStore mediante `combine`. |
| **CA-04** | Búsquedas rápidas | Cancelación de Jobs previos mediante `flatMapLatest`. |
| **CA-05** | Fallo de repositorio | Captura de excepción y muestra de `ListadoUiState.Error`. |
| **CA-06** | Salir durante operación | Cancelación automática de la corrutina en `onCleared`. |
| **CA-07** | Rotación de pantalla | Persistencia del estado gracias a `StateFlow` y `stateIn`. |
| **CA-08** | Suite de pruebas | 14 tests deterministas ejecutados con `runTest`. |

---

## 5. Aseguramiento de Calidad (QA)
*   **Pruebas Unitarias**: Suite completa en `ActividadesViewModelTest.kt` validando transiciones de estado y lógica de filtrado/ordenamiento.
*   **Tiempo Virtual**: Uso exclusivo de `StandardTestDispatcher` y `runTest`, cumpliendo con la prohibición institucional de usar `Thread.sleep`.
*   **Higiene**: 0 Errores, 0 Warnings críticos.

---

## 6. Reflexión Técnica
La implementación de flujos reactivos y concurrencia estructurada ha transformado la aplicación en un sistema resiliente. El mayor desafío fue la coordinación de múltiples fuentes de datos (Room y DataStore); el uso del operador `combine` permitió unificar estas fuentes en un único `UiState` coherente, eliminando "estados imposibles" donde la UI mostraba información contradictoria. La migración a `collectAsStateWithLifecycle` garantiza que la app sea responsable con los recursos del sistema (batería/RAM), un estándar indispensable para el desarrollo profesional en 2026.
