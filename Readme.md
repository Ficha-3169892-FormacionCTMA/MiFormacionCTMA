# Mi Formación CTMA

Aplicación móvil nativa en Android construida para optimizar la organización, trazabilidad y gestión de compromisos formativos de los aprendices.

---

## 1. Propósito y Descripción del Problema
Los aprendices suelen administrar actividades, enlaces, evidencias y fechas en diferentes canales de comunicación. Esto produce olvidos, duplicación de trabajo y poca trazabilidad. **Mi Formación CTMA** resuelve este problema centralizando el progreso en una interfaz moderna, accesible y adaptable basada en **Jetpack Compose** y **Material Design 3**.

---

## 2. Usuarios y Necesidades

| Actor | Necesidad Inicial | Valor Esperado |
| :--- | :--- | :--- |
| **Aprendiz** | Consultar compromisos, jerarquizar prioridades y monitorear el avance general de su proceso. | Organización centralizada, baja carga cognitiva y visibilidad inmediata de entregas. |
| **Instructor** | Comunicar actividades formativas y verificar criterios de avance. | Trazabilidad clara de resultados de aprendizaje. |

---

## 3. Arquitectura de Interfaz y Decisiones Técnicas (Semana 03)

### A. Adaptabilidad Responsiva (Layout Adaptable)
* **Criterio de Adaptabilidad:** Se implementó una evaluación del ancho disponible en pantalla con el umbral estándar de **`600.dp`**.
* **Comportamiento Móvil (`< 600.dp`):** La interfaz renderiza una lista de desplazamiento vertical fluido (`LazyColumn`) ideal para interacción a una sola mano en smartphones.
* **Comportamiento Pantalla Ancha / Tablet (`>= 600.dp`):** La interfaz se reestructura automáticamente a una cuadrícula de dos columnas (`LazyVerticalGrid`), optimizando el aprovechamiento del espacio horizontal sin estirar las tarjetas.

### B. Rendimiento y Optimización de Listas
* **Claves Estables (`key = { it.id }`):** Tanto en `LazyColumn` como en `LazyVerticalGrid` se asignó la propiedad única e inmutable `id` de la entidad `ActividadFormativa`. Esto permite que el motor de Jetpack Compose identifique cada elemento de forma única, evitando la recomposición innecesaria de elementos de la lista durante eventos de desplazamiento (scrolling) o mutaciones de datos.

### C. Accesibilidad y Diseño Inclusivo (WCAG / Material 3)
* **Doble Canal de Identificación:** Se implementó una estrategia visual redundante. El estado de las actividades no depende únicamente del color, sino de la combinación simultánea de **color, ícono descriptivo (CheckCircle vs. Warning) y texto explicativo**, garantizando la legibilidad para usuarios con acromatopsia o daltonismo.
* **Semántica para Lectores de Pantalla:** Los íconos de estado incluyen descripciones textuales explícitas a través de `contentDescription = "Estado: ..."` para asistencia en tecnologías como TalkBack.
* **Jerarquía Tipográfica y Manejo de Textos Extremos:** La tarjeta (`TarjetaActividad`) utiliza `TextOverflow.Ellipsis` con límite de líneas para soportar nombres extensos sin romper el diseño de la interfaz ni solapar componentes adyacentes.

### D. Panel de Control y Métricas de Rendimiento (Dashboard)
* **Reducción de Carga Cognitiva:** En lugar de requerir que el usuario procese mentalmente la suma de sus entregas, el componente `EncabezadoFormacion` calcula en tiempo real el porcentaje global de avance y las evidencias completadas, mostrando un indicador visual circular (`CircularProgressIndicator`) y barras de progreso individuales (`LinearProgressIndicator`).

---

## 4. Historias de Usuario e Incremento Desarrollado

### Historia 1: Interfaz Base y Bienvenida
* **Como** aprendiz,
* **quiero** abrir la aplicación y ver un panel con mi nombre y resumen general,
* **para** confirmar mi estado actual dentro del trimestre formativo.
> **Criterio de Aceptación:** La app ejecuta `EncabezadoFormacion` calculando el número total de actividades y el porcentaje de cumplimiento de forma dinámica.

### Historia 2: Visualización y Gestión de Compromisos Formativos
* **Como** aprendiz,
* **quiero** visualizar la lista completa de actividades con sus prioridades y porcentajes individuales de entrega,
* **para** enfocar mis esfuerzos en las tareas urgentes.
> **Criterio de Aceptación:** Las tarjetas de actividad muestran chips de prioridad (ALTA/MEDIA/BAJA), barras de progreso visual y reaccionan correctamente cuando la lista está vacía (`emptyList()`).

### Historia 3: Estabilidad y Versionado del Repositorio
* **Como** equipo de desarrollo,
* **quiero** contar con un repositorio limpio y versionado bajo estándares semánticos de Git,
* **para** garantizar la evolución continua del proyecto sin subir artefactos temporales de compilación.
> **Criterio de Aceptación:** Repositorio estructurado con `.gitignore` correcto, sin carpetas `build/` o contraseñas, e incremento registrado bajo la convención *Conventional Commits*: `feat: construye interfaz accesible de actividades de Mi Formación CTMA`.

---

## 5. Requisitos Técnicos
* **Lenguaje:** Kotlin
* **Interfaz:** Jetpack Compose (Declarativa)
* **Componentes de Diseño:** Material Design 3 (`Card`, `Scaffold`, `LazyColumn`, `LazyVerticalGrid`)
* **Entorno de Desarrollo:** Android Studio
* **Sistema de Compilación:** Gradle (Kotlin DSL)

---

## 6. Forma de Ejecución
1. Clonar el repositorio localmente:
   ```bash
   git clone <URL_DEL_REPOSITORIO>