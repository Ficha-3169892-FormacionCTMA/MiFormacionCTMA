# 📋 Gestión de Historias de Usuario - Sprint 1

Este documento detalla los criterios de aceptación, casos de prueba y gestión de riesgos para las funcionalidades core de **Mi Formación CTMA**.

---

## HU01: Persistencia de Datos (Room Database)
**Descripción:** Como usuario, quiero que mis actividades se guarden al cerrar la app para no perder mi progreso formativo.

*   **Criterios de Aceptación:**
    1. La aplicación debe almacenar título, descripción, fecha y progreso de forma local.
    2. Los datos deben estar disponibles inmediatamente al reiniciar la aplicación.
    3. El ViewModel no debe gestionar datos "en memoria", sino observar el flujo del Repository.
*   **Casos de Prueba:**
    *   **CP-01-M (Manual):** Crear actividad -> Forzar cierre de app -> Abrir app -> Verificar existencia de la actividad.
    *   **CP-01-A (Automático):** `ActividadesViewModelTest.kt` -> `HU01 - Al agregar actividad se debe persistir en el repositorio`.
*   **Riesgos:**
    *   *Pérdida de datos por migración:* Si se cambia el modelo `Actividad` sin definir una migración en Room, la app podría crashear o borrar la base de datos.
    *   *Rendimiento:* Operaciones en el hilo principal (solucionado usando Corrutinas).

---

## HU02: Borrado de Actividades (Swipe-to-Dismiss)
**Descripción:** Como usuario, quiero poder borrar actividades que ya no son relevantes para mantener mi lista limpia.

*   **Criterios de Aceptación:**
    1. El usuario debe poder deslizar una tarjeta de derecha a izquierda para borrar.
    2. Debe aparecer un indicador visual (fondo rojo e icono de papelera) durante el gesto.
    3. La eliminación debe ser permanente en la base de datos local.
*   **Casos de Prueba:**
    *   **CP-02-M (Manual):** Deslizar actividad a la izquierda -> Observar desaparición y animación.
    *   **CP-02-A (Automático):** `ActividadesUITest.kt` -> `HU02_GestoSwipe_DebeEjecutarEliminacion`.
*   **Riesgos:**
    *   *Borrado accidental:* El gesto de swipe puede ser sensible. Se recomienda a futuro añadir un "Undo" (Deshacer) con Snackbar.

---

## HU03: Edición de Actividades
**Descripción:** Como usuario, quiero corregir errores en el título o descripción de una actividad ya creada.

*   **Criterios de Aceptación:**
    1. Al pulsar "Editar" en el detalle, se debe abrir el formulario con los datos precargados.
    2. Al guardar, se debe ejecutar un `UPDATE` en la base de datos manteniendo el mismo ID.
    3. La lista principal debe refrescarse automáticamente con los nuevos datos.
*   **Casos de Prueba:**
    *   **CP-03-M (Manual):** Entrar a detalle -> Clic Editar -> Cambiar Título -> Guardar -> Verificar cambio en la lista.
    *   **CP-03-A (Automático):** `ActividadesViewModelTest.kt` -> `HU03 - Al editar una actividad, se debe llamar al repositorio con los datos nuevos`.
*   **Riesgos:**
    *   *Inconsistencia de estado:* Que el usuario edite una actividad que acaba de ser marcada como eliminada en otro hilo.

---

## HU04: Historial y Filtros de Finalizadas
**Descripción:** Como usuario, quiero separar las actividades completadas de las pendientes para centrarme en lo que falta.

*   **Criterios de Aceptación:**
    1. La UI debe permitir filtrar actividades con progreso igual al 100%.
    2. Debe existir un control visual (Filter Chip) para alternar entre "Pendientes" e "Historial".
    3. El contador del Dashboard debe reflejar el total independientemente del filtro activo.
*   **Casos de Prueba:**
    *   **CP-04-M (Manual):** Marcar actividad al 100% -> Pulsar Chip "Finalizadas" -> Verificar que la actividad aparece en esa sección y desaparece de pendientes.
    *   **CP-04-A (Automático):** `ActividadesViewModelTest.kt` -> `HU04 - El filtro de finalizadas debe cambiar el estado de la UI`.
*   **Riesgos:**
    *   *Confusión del usuario:* Que el usuario crea que ha perdido datos si olvida que tiene un filtro activo.
