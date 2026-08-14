# Mi Formación CTMA 

Aplicación móvil nativa para Android desarrollada con **Jetpack Compose** y **Kotlin**, diseñada para gestionar, listar y supervisar el progreso de actividades formativas y de aprendizaje.

---

## Características Principales

* **Interfaz Moderna con Jetpack Compose:** Diseño declarativo que incluye barras superiores (TopAppBar), contenedores de tarjetas (Card), indicadores de progreso lineales y *Chips* informativos.
* **Capa de Dominio y Lógica de Negocio (domain):**
  * ActividadFormativa: Estructura de datos principal (data class) que define los atributos de cada tarea.
  * EstadoActividad: Enumeración (enum class) con los posibles estados (PENDIENTE, EN_PROGRESO, COMPLETADA, VENCIDA).
  * Prioridad: Clasificación de importancia (ALTA, MEDIA, BAJA).
  * ReglasActividad: Objeto de negocio centralizado que valida entradas y calcula dinámicamente el estado de la actividad según su progreso y días restantes.
* **Listado Dinámico (LazyColumn):** Catálogo optimizado que renderiza 10 actividades formativas con estados calculados en tiempo real.
* **Pruebas Unitarias (	est):** Suite de pruebas implementada con JUnit para verificar los flujos de validación y cambio de estado en la capa de negocio.

---

## Tecnologías y Arquitectura

* **Lenguaje:** Kotlin
* **UI Toolkit:** Jetpack Compose & Material Design 3
* **Arquitectura:** Orientada a Dominio (Domain-Driven Package Structure)
* **Control de Versiones:** Git & GitHub (eat/Thomas ➔ develop)

---

## Listado de Actividades Integradas

1. **Guía 05 - Persistencia de datos** (En Progreso)
2. **Pruebas de Desempeño** (En Progreso)
3. **Documentación del Proyecto** (Completada)
4. **Diseño de Interfaces Compose** (Pendiente)
5. **Modelo Entidad-Relación SORAKA** (Vencida / Completada)
6. **Autenticación JWT en Backend** (En Progreso)
7. **Configuración de Contenedores Docker** (En Progreso)
8. **Control de Versiones y Ramas Git** (Completada)
9. **Navegación Jetpack Compose** (En Progreso)
10. **Validación de Formulario Pydantic** (En Progreso)

---

## Pruebas Unitarias

Las reglas de negocio de ReglasActividad cuentan con pruebas unitarias enfocadas en:
* Validación de títulos vacíos y rangos de progreso válidos (0 a 100).
* Verificación de estados límite (progreso al 100%, días restantes negativos, progreso en cero).

---

## Control de Versiones (Git Workflow)

* **Rama de trabajo:** eat/Thomas
* **Rama de integración:** develop
* Integración consolidada mediante fusión de ramas (merge) y sincronización con el repositorio remoto.
