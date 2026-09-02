# Mi Formación CTMA 📱

**Mi Formación CTMA** es una solución móvil diseñada para aprendices del SENA, enfocada en la centralización y seguimiento de actividades formativas. La aplicación permite gestionar tareas, plazos y evidencias de manera eficiente bajo una arquitectura moderna y persistente.

---

## 🚀 Tecnologías y Arquitectura

*   **UI:** Jetpack Compose (Material Design 3).
*   **Arquitectura:** MVVM (Model-View-ViewModel) + Repository Pattern.
*   **Persistencia:** Room Database (SQLite).
*   **Concurrencia:** Kotlin Coroutines & Flow.
*   **Pruebas:** JUnit 4, Mockito-Kotlin y Compose Testing.
*   **Gestión de Dependencias:** Gradle Kotlin DSL + Version Catalog (TOML).

---

## 📋 Funcionalidades Core (Sprint 1)

1.  **Persistencia Total:** Los datos se guardan localmente mediante Room, garantizando que el progreso no se pierda al cerrar la app.
2.  **Gestión Ágil:** Eliminación de tareas mediante gestos (*Swipe-to-Dismiss*).
3.  **Edición Dinámica:** Posibilidad de corregir títulos y descripciones de actividades ya creadas.
4.  **Control de Avance:** Visualización del porcentaje de completitud y filtrado por historial de finalizadas.

---

## 🛠️ Configuración y Pruebas

### Prerrequisitos
*   Android Studio Ladybug o superior.
*   JDK 17 o superior.
*   SDK de Android nivel 35.

### Ejecución de Pruebas
Para validar la integridad del sistema, ejecuta los siguientes comandos o usa el IDE:
*   **Unit Tests:** `./gradlew test` (Valida lógica de negocio).
*   **UI Tests:** `./gradlew connectedAndroidTest` (Valida gestos y navegación).

---

## 👥 Equipo de Desarrollo
Proyecto desarrollado bajo estándares de calidad de software móvil, implementando pruebas unitarias e instrumentadas para cada incremento funcional.
