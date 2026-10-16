# Abrir y probar el proyecto base

Estado actual: el estudiante instaló Android Studio y el SDK, y confirmó que la base abrió en un Moto G35 el 14-10-2026. Falta registrar captura/versión Android y repetir el arranque en un segundo teléfono. P1–P7 no se han ejecutado.

## Instalar las herramientas

1. Descargar Android Studio estable desde [Android Developers](https://developer.android.com/studio) y ejecutar su instalador.
2. Abrir Android Studio y completar el Setup Wizard. Revisar y aceptar personalmente las licencias que correspondan.
3. En SDK Manager instalar **Android SDK Platform 36**, **Android SDK Build-Tools 35.0.0** y **Android SDK Platform-Tools**. No se necesita emulador para usar teléfonos reales.
4. Abrir la carpeta raíz de este repositorio con **Open**. No crear otro proyecto encima ni abrir solo `app/`.
5. En Settings → Build, Execution, Deployment → Build Tools → Gradle, usar un JDK compatible (por ejemplo JDK 21 de Android Studio). El bytecode del proyecto es Java 17. No usar Java 8.
6. Si el IDE pregunta por Android SDK, seleccionar la ubicación que muestre SDK Manager. Android Studio crea `local.properties`; ese archivo se excluye de Git porque contiene una ruta local.
7. Ejecutar **Sync Project with Gradle Files**. Requiere Internet para descargar Gradle y dependencias la primera vez.
8. Ejecutar la tarea Gradle `:app:assembleDebug`, desde la ventana Gradle o la terminal configurada con JDK 17/21: `./gradlew.bat :app:assembleDebug` en Windows.
9. Exigir `BUILD SUCCESSFUL`. El APK resultante estará en `app/build/outputs/apk/debug/app-debug.apk`. Si falla, conservar el error completo; todavía no se ha confirmado la compilación en este entorno.

## Instalar la base en Teléfono A

1. Usar un teléfono Android 8.0/API 26 o superior.
2. En Ajustes → Acerca del teléfono, tocar siete veces Número de compilación; en algunos modelos está dentro de Información de software.
3. En Opciones de desarrollador habilitar Depuración USB.
4. Conectar A al PC con cable de datos; aceptar la autorización de depuración en el teléfono.
5. En Android Studio seleccionar A en la lista de dispositivos y ejecutar **Run app**.
6. Verificar que se muestran «Comedero IoT», «Unidad 2 · Formativa» y el mensaje de revisión docente pendiente. Registrar captura y versión Android. Esto prueba solo el Hito 0.

## Instalar la base en Teléfono B

1. Repetir habilitación de depuración y autorización en B.
2. Conectar B, seleccionarlo por su nombre en Android Studio y ejecutar el mismo módulo **app**.
3. Verificar la misma bienvenida y registrar evidencia. Actualmente ninguno de los dos teléfonos puede elegir Nodo o Panel: esas pantallas aún no se implementan.

Alternativa una vez compilado: copiar el APK debug a cada teléfono, abrirlo y permitir la instalación desde esa fuente cuando Android lo solicite. No confundir ese permiso del instalador con permisos solicitados por la app.

## Después de la revisión docente

Primero implementar y verificar Hitos 3–4. Reinstalar la app actualizada en ambos equipos siguiendo los mismos pasos. Después seguir exactamente la preparación y los casos [P1–P7](PRUEBAS_FORMATIVA.md). No intentar validar conexión, cifrado o alimentación con la bienvenida del Hito 0.

## Si algo no aparece

- Teléfono no listado: revisar cable de datos, autorización USB y controlador del fabricante en Windows.
- Error de SDK: verificar la ruta en SDK Manager; no copiar la ruta del computador de otra persona.
- Error de Java: comprobar el Gradle JDK del IDE; el Java 8 de PATH no sirve para este proyecto.
- Error de descarga: comprobar Internet/proxy y conservar la salida completa de Gradle.

Compatibilidad de AGP/Gradle/JDK: [documentación oficial de AGP 8.13](https://developer.android.com/build/releases/agp-8-13-0-release-notes). Compose se configura con su plugin de compilador del mismo número que Kotlin; [documentación oficial](https://developer.android.com/develop/ui/compose/compiler).
