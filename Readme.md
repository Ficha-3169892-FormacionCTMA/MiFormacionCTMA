# Mi Formación CTMA

**Instructor del proceso formativo:** Jhon Fredy Valencia

## 1. Propósito y Descripción del Problema
Los aprendices suelen administrar actividades, enlaces, evidencias y fechas en diferentes canales de comunicación. Esto produce olvidos, duplicación de trabajo y poca trazabilidad. *Mi Formación CTMA* es una aplicación móvil nativa Android construida para solucionar esto, permitiendo organizar actividades y compromisos del proceso formativo, garantizando una base técnica estable que evolucionará cada semana.

## 2. Usuarios y Necesidades

| Actor | Necesidad Inicial | Valor Esperado |
| :--- | :--- | :--- |
| **Aprendiz** | Consultar compromisos y registrar avance. | Organización y visibilidad. |
| **Instructor** | Comunicar actividades y criterios. | Trazabilidad formativa. |

## 3. Historias de Usuario y Criterios de Aceptación (Alcance Inicial)

### Historia 1: Interfaz Base y Bienvenida
* **Como** aprendiz,
* **quiero** abrir la aplicación y ver un mensaje de bienvenida,
* **para** confirmar que el sistema está disponible y listo para usarse.
> **Criterio de Aceptación:** La app debe compilar y ejecutarse mostrando el texto "Mi Formación CTMA" y "Hola, Aprendiz", sin cerrarse inesperadamente (crash) en el emulador o dispositivo.

### Historia 2: Visualización del Próximo Compromiso
* **Como** aprendiz,
* **quiero** visualizar una tarjeta destacada en la pantalla de inicio,
* **para** saber inmediatamente cuál es la próxima actividad o evidencia que debo entregar.
> **Criterio de Aceptación:** La interfaz incluye un componente tipo `Card` visible que contiene el texto del próximo compromiso asignado.

### Historia 3: Estabilidad del Repositorio
* **Como** equipo de desarrollo,
* **quiero** contar con un repositorio limpio y versionado,
* **para** poder evolucionar la base del código sin romperla en las siguientes semanas.
> **Criterio de Aceptación:** El proyecto está versionado en Git con un `.gitignore` correcto, sin subir secretos, contraseñas, ni archivos generados automáticamente (como la carpeta `build/`).

## 4. Requisitos Técnicos
* **Lenguaje:** Kotlin
* **Interfaz:** Jetpack Compose (Declarativa)
* **Entorno:** Android Studio
* **Sistema de compilación:** Gradle

## 5. Forma de Ejecución
1. Clonar el repositorio localmente mediante el comando: `git clone <URL_DEL_REPOSITORIO>`.
2. Abrir el proyecto directamente desde **Android Studio**.
3. Esperar a que la sincronización de Gradle (Gradle Sync) finalice sin arrojar errores.
4. Seleccionar un emulador (AVD) configurado o conectar un dispositivo físico con la Depuración USB activada.
5. Presionar el botón **Run** (Ejecutar) en Android Studio para instalar la aplicación en el dispositivo y visualizar la pantalla de inicio.