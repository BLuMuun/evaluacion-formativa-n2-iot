# Verificación del proyecto base — Hito 0

Actualizado: 16 de octubre de 2026 (hora de Santiago).

## Resultado de compilación

**Compilación y arranque base confirmados parcialmente en un teléfono físico.**

El intento inicial de `:app:assembleDebug` terminó con error al no encontrar el Android SDK. Después de instalar/configurar Android Studio y elegir el JDK 21, Android Studio mostró «Gradle build finished in 6 s 303 ms». El estudiante seleccionó su Moto G35 y confirmó que la app abrió en ese teléfono. Esto confirma que el IDE pudo compilar, instalar y abrir la app base en dicho dispositivo.

Se adjunta [captura del arranque en el Moto G35](evidencia_hito0_moto_g35.png). La imagen muestra la bienvenida base y el aviso de que la revisión docente sigue pendiente; sirve como evidencia visual de que la app abrió, pero no verifica conexión entre teléfonos ni funciones IoT. La versión de Android no quedó registrada.

El error inicial fue:

```text
Could not determine the dependencies of task ':app:compileDebugJavaWithJavac'.
> SDK location not found. Define a valid SDK location with an ANDROID_HOME
  environment variable or by setting the sdk.dir path in your project's
  local properties file.
BUILD FAILED in 45s
```

En ese momento, el estudiante aún no había instalado Android Studio ni SDK. La situación cambió: ambos ya están configurados y la app base abrió en el dispositivo físico.

Un intento anterior encontró una restricción de escritura en la carpeta `.android` del perfil. Tras autorizar el acceso, el intento final alcanzó el diagnóstico de SDK ausente. El JDK usado explícitamente fue 21; el `java` de PATH corresponde a Java 8 y no debe usarse para este build.

Gradle también emitió una advertencia de deprecación de `kotlinOptions.jvmTarget`; es una configuración admitida por Kotlin 2.2.21, no la causa del fallo. El bytecode está configurado en 17.

La advertencia de deprecación de `kotlinOptions.jvmTarget` sigue presente; no impidió que esta versión abriera en el Moto G35. Mantener el JDK de Gradle en 21 para este proyecto.

## Controles realmente ejecutados

| Control | Resultado |
|---|---|
| Inspección inicial de carpeta y Git | Solo .git; rama master sin commits; sin código previo que preservar |
| Wrapper oficial de Gradle 8.13 | Descargados gradlew, gradlew.bat y gradle-wrapper.jar |
| Integridad SHA-256 del JAR | Coincide con checksum oficial: 81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f |
| Checksum de distribución | Fijado en gradle-wrapper.properties: 20f1b1176237254a6fc204d8434196fa11a4cfb387567519c61556e8710aed78 |
| Parseo XML | AndroidManifest.xml, strings.xml y themes.xml válidos sintácticamente |
| Parseo SVG | mockup.svg válido sintácticamente; no es validación de UI Android |
| minSdk | 26 presente en configuración |
| Permisos de la base | Ninguno: la bienvenida no usa red/notificaciones; permisos previstos solo en Hito 3 |
| Checkboxes | Se marcaron como completados instalación de SDK, compilación y primer arranque en Moto G35, confirmados por el estudiante |
| git diff --check / --cached --check | Sin errores de espacios en los archivos examinados |
| git check-ignore | Excluye local.properties, .gradle, app/build, .gradle-user-home, secrets.properties y google-services.json |

El SHA-256 del Wrapper se contrastó con `https://services.gradle.org/distributions/gradle-8.13-wrapper.jar.sha256`; los archivos proceden del repositorio oficial Gradle, etiqueta v8.13.0. Los enlaces locales Markdown se verificaron al terminar la documentación.

## Git

El repositorio remoto privado `BLuMuun/evaluacion-formativa-n2-iot` contiene los commits de preparación de Hitos 0–2. No se ha entregado nada al AAI. P1–P7 y la revisión docente siguen pendientes.

## Verificaciones que NO se realizaron

- Versión Android del Moto G35; no quedó visible ni registrada en la evidencia.
- Repetición del arranque base en un segundo teléfono.
- Revisión/aprobación del profesor de Hitos 1 y 2.
- P1–P7, pruebas de ventana fija o pruebas de criptografía.
- Conexión entre teléfonos, notificaciones, dispensación o ACK.

Las últimas funciones todavía no existen en el código. Hitos 3–4 permanecen detenidos por la condición establecida por el estudiante.
