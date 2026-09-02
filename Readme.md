# Mi Formación CTMA 🚀

Aplicación móvil nativa en Android construida para optimizar la organización, trazabilidad y gestión de compromisos formativos de los aprendices del SENA.

---

## 1. Propósito y Descripción
**Mi Formación CTMA** centraliza la administración de evidencias y actividades formativas en una interfaz moderna basada en **Jetpack Compose** y **Material Design 3**. La app elimina la fragmentación de información, permitiendo al aprendiz enfocarse en su proceso de aprendizaje con herramientas de seguimiento en tiempo real.

---

## 2. Arquitectura y Decisiones Técnicas (Estado Actual)

### A. Gestión Avanzada de Fechas y Retrocompatibilidad
*   **Selección Inteligente:** Se implementó un `DatePicker` de Material 3 con validación de `SelectableDates` que bloquea automáticamente la selección de fechas pasadas.
*   **API Desugaring:** Para soportar la lógica de `java.time` en dispositivos antiguos (Android 7.0+ / API 24), se habilitó `isCoreLibraryDesugaringEnabled`, garantizando estabilidad sin sacrificar el uso de APIs modernas de tiempo.
*   **Utilidades Puras:** La lógica de cálculo de días restantes está desacoplada en `DateUtils.kt`, facilitando las pruebas unitarias.

### B. Experiencia de Usuario Continua (UX/UI)
*   **Navegación Fluida:** Transiciones interactivas entre pantallas mediante `slideInHorizontally` y `fadeIn`. Se implementó lógica de detección de **"Reducir Movimiento"** del sistema operativo para desactivar animaciones si el usuario lo requiere por accesibilidad.
*   **Preservación de Estado:** El scroll de la lista (`LazyListState`) se mantiene persistente al navegar hacia el detalle y regresar, eliminando la fricción de búsqueda repetitiva.
*   **Guía de Onboarding (Empty State):** Sistema de guía de 3 pasos que se activa automáticamente cuando no hay registros, orientando al usuario desde su primera interacción.

### C. Seguimiento de Progreso Granular
*   **Validación de Rango:** El ingreso de progreso (0-100%) cuenta con validación estricta de entrada.
*   **Reactividad:** La barra de progreso visual y el estado ("En proceso" vs "Completado") se actualizan en tiempo real mediante estados observables (`mutableStateListOf`), asegurando que los cambios en el detalle se reflejen instantáneamente en la lista principal.

---

## 3. Historias de Usuario Implementadas

### Historia: Selección de Fecha Límite
*   **Como** aprendiz, **quiero** seleccionar una fecha desde un calendario, **para** evitar calcular manualmente los días restantes.
*   **Resultado:** Integración de calendario nativo que convierte fechas a días de forma interna.

### Historia: Guía de Inicio (Estado Vacío)
*   **Como** usuario nuevo, **quiero** ver instrucciones claras si no tengo actividades, **para** saber cómo empezar a usar la app.
*   **Resultado:** Interfaz educativa con ilustración, pasos guiados y botón de acción directa (CTA).

### Historia: Seguimiento Preciso de Avance
*   **Como** aprendiz, **quiero** registrar el porcentaje exacto de mi curso (ej. 45%), **para** tener un control detallado de mi avance.
*   **Resultado:** Campo numérico validado con actualización visual de barras de progreso.

### Historia: Transiciones Modernas
*   **Como** usuario, **quiero** ver animaciones fluidas al navegar, **para** sentir una experiencia de aplicación premium y continua.
*   **Resultado:** Animaciones de transición de 300ms optimizadas para evitar caídas de fotogramas (Jank).

---

## 4. Requisitos Técnicos
*   **Mínimo SDK:** 24 (Android 7.0) con Core Library Desugaring.
*   **Lenguaje:** Kotlin 2.0+
*   **UI Framework:** Jetpack Compose con Material Design 3.
*   **Navegación:** Navigation Compose con animaciones personalizadas.
*   **Iconografía:** Material Icons Extended.

---

## 5. Forma de Ejecución
1. Clonar el repositorio.
2. Abrir en **Android Studio Ladybug** o superior.
3. Ejecutar `Gradle Sync`.
4. Correr las pruebas unitarias: `./gradlew test`.
5. Desplegar en un emulador o dispositivo físico.
