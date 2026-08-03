# Mi Formación CTMA 

**Instructor del proceso formativo:** Wilson Castro 
**Programa:** Análisis y Desarrollo de Software (ADSO) - SENA CTMA  
**Desarrollador:** Thomas Isaza Chalarca - Liney Ricardo

---

## 1. Propósito y Descripción del Problema

Los aprendices suelen administrar actividades, enlaces, evidencias y fechas en diferentes canales de comunicación. Esto produce olvidos, duplicación de trabajo y poca trazabilidad. 

**Mi Formación CTMA** es una aplicación móvil nativa para Android construida para solucionar esto, permitiendo organizar actividades y compromisos del proceso formativo, garantizando una base técnica estable y desacoplada que evoluciona progresivamente cada semana.

---

## 2. Usuarios y Necesidades

| Actor | Necesidad Inicial | Valor Esperado |
| :--- | :--- | :--- |
| **Aprendiz** | Consultar compromisos y registrar avance. | Organización y visibilidad del progreso. |
| **Instructor** | Comunicar actividades y criterios. | Trazabilidad formativa y entregas oportunas. |

---

## 3. Historias de Usuario y Criterios de Aceptación

### Incremento Semana 1: Interfaz Base y Configuración
* **Historia 1: Bienvenida y Estabilidad:**
  * *Como aprendiz,* quiero abrir la app y ver un mensaje de bienvenida para confirmar que el sistema está listo.
  * *Criterio de Aceptación:* La app compila y ejecuta mostrando "Mi Formación CTMA" y "Hola, Aprendiz" sin cierres inesperados (*crashes*).
* **Historia 2: Repositorio Límite:**
  * *Como equipo de desarrollo,* quiero un repositorio versionado en Git con `.gitignore` para no subir archivos generados ni configuraciones locales (como la carpeta `.idea/` o `build/`).

### Incremento Semana 2: Núcleo de Dominio y Reglas de Negocio
* **Historia 3: Modelado de Actividades Formativas:**
  * *Como aprendiz,* quiero que mis actividades tengan atributos definidos (título, progreso, días restantes, prioridad, estado y evidencias opcionales) para gestionar mi formación con claridad.
  * *Criterio de Aceptación:* Creación de las data classes y enums en la capa `domain` aplicando inmutabilidad y tipos nulos explícitos.
* **Historia 4: Reglas de Negocio Desacopladas:**
  * *Como aprendiz,* quiero ver el promedio de avance y el total de entregas urgentes/vencidas en la pantalla inicial, calculados de forma independiente a la interfaz.
  * *Criterio de Aceptación:* Integración de un resumen visual en Jetpack Compose que consume los resultados del singleton `ReglasActividad` sin incluir lógica dentro del `@Composable`.

---

## 4. Estructura del Proyecto y Requisitos Técnicos

### Requisitos
* **Lenguaje:** Kotlin 2.x
* **UI:** Jetpack Compose (Material Design 3)
* **Entorno:** Android Studio (Ladybug / Jellyfish)
* **Sistema de compilación:** Gradle (Kotlin DSL)
* **Arquitectura:** Clean Architecture / Separation of Concerns (Capa de Dominio)

### Árbol de Paquetes
```text
com.example.miformacionctma/
├── domain/                    # Núcleo de Lógica de Negocio (Puro Kotlin)
│   ├── ActividadFormativa.kt  # Data Class de la entidad principal
│   ├── EstadoActividad.kt     # Enum: PENDIENTE, EN_PROCESO, COMPLETADA, VENCIDA
│   ├── Prioridad.kt           # Enum: BAJA, MEDIA, ALTA
│   └── ReglasActividad.kt     # Singleton con funciones puras de cálculo y validación
├── ui/
│   └── theme/                 # Configuración del tema Material 3
└── MainActivity.kt            # Punto de entrada de la UI en Jetpack Compose
```
## 5. Decisiones de Diseño y Calidad Kotlin (Semana 2)

## Inmutabilidad (val vs var):

Se prefirió val en todas las propiedades del modelo ActividadFormativa y en el estado de la UI.

Justificación: Evita modificaciones accidentales, reduce estados inconsistentes y encaja de manera natural con el flujo reactivo de Jetpack Compose.

Gestión de Nulabilidad (Null Safety):

Atributos como descripcion: String? y enlaceEvidencia: String? se declararon como opcionales.

Justificación: Se evita el uso del operador inseguro !!. Se implementan llamadas seguras (?.), valores por defecto y funciones de filtrado para prevenir NullPointerException ante datos faltantes.

Desacoplamiento de la Capa de Dominio:

Las reglas de validación, filtros de urgencia, ordenamiento y promedios se encapsularon en el objeto singleton ReglasActividad.

Justificación: La interfaz @Composable solo muestra datos precalculados. La lógica no está duplicada en la UI, garantizando mantenibilidad, reutilización y facilidad para realizar pruebas unitarias.

## 6. Reglas de Negocio Implementadas (ReglasActividad)
Validación de Actividades: Devuelve la lista completa de errores (título en blanco, progreso fuera del rango 0..100).

Cálculo de Estado: Asigna el estado dinámicamente (COMPLETADA si progreso es 100%, VENCIDA si días restantes < 0, EN_PROCESO o PENDIENTE).

Filtro de Urgencia: Filtra actividades no completadas con 2 días o menos para su fecha límite.

Promedio Controlado: Calcula el promedio de avance validando listas vacías para prevenir divisiones por cero o resultados NaN.

Búsqueda e Inspección: Búsqueda flexible por título ignorando espacios en blanco externos y diferencias entre mayúsculas/minúsculas.

Ordenamiento Priorizado: Ordena colocando vencidas primero, luego prioridad alta y finalmente menor número de días restantes.

## 7. Forma de Ejecución
Clonar el repositorio localmente:

Bash
git clone [https://github.com/ThomasIsaza04/MiFormacionCTMA.git](https://github.com/ThomasIsaza04/MiFormacionCTMA.git)
Abrir el proyecto en Android Studio.

Esperar a que la sincronización de Gradle finalice correctamente.

Seleccionar un emulador (p. ej., Pixel 8 Pro) o un dispositivo físico configurado.

Presionar Run 'app' (Shift + F10) para visualizar la pantalla inicial con el resumen calculado del dominio.


---

### Comandos Git para guardar el README en GitHub

Una vez que reemplaces el texto en tu archivo `Readme.md` de Android Studio, ejecuta estos comandos en la terminal[cite: 1]:

```powershell
git add Readme.md
git commit -m "docs: actualiza README.md con la consolidacion de la Semana 1 y Semana 2"
git push origin main
