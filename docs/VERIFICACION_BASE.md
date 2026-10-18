# Verificación de la app — Hitos 0–4

Actualizado: 18 de octubre de 2026 (hora de Santiago).

## Hito 0: antecedente confirmado por el estudiante

Android Studio compiló, instaló y abrió la bienvenida inicial en el Moto G35. La [captura del Hito 0](evidencia_hito0_moto_g35.png) muestra solo esa versión base; no demuestra las funciones IoT actuales. No se registró la versión Android del teléfono.

La primera compilación local falló porque no encontraba el SDK; el estudiante instaló Android Studio/SDK y posteriormente confirmó el arranque base. En la configuración de Gradle del IDE se usó JDK 21; la versión `java` de PATH corresponde a Java 8 y no se debe utilizar para este proyecto.

## Hitos 3–4: validación de la implementación actual

La primera compilación automatizada detectó que `androidx.core:core-ktx:1.19.1` requiere compileSdk 37 y AGP 9.1. Se fijó Core 1.17.0 para conservar compileSdk 36 y AGP 8.13.2, según las notas oficiales de AndroidX Core: https://developer.android.com/jetpack/androidx/releases/core.

El siguiente intento hizo que Gradle agotara la memoria nativa en un equipo con 3 GB de RAM, configurado inicialmente con heap de 2 GB. Se bajó Gradle a 1 GB, dos trabajadores y compilador Kotlin en proceso para adaptarse a la memoria disponible. Después de corregir imports de `SecretKey` y `KeyboardOptions`, la construcción y las pruebas terminaron correctamente.

Comando final:

```powershell
.\gradlew.bat --no-daemon --max-workers=2 testDebugUnitTest assembleDebug
```

**Prueba física de humo de esta implementación:** APK instalada por ADB y pantalla `MainActivity` abierta en un Moto G35 con Android 15 (API 35). El proceso permaneció activo y `logcat` no mostró excepciones `AndroidRuntime`. La interfaz mostró Conexión, el rol Nodo y el mensaje «Sin IP Wi-Fi disponible»; por ello el botón de inicio quedó deshabilitado como corresponde. El teléfono no tenía una interfaz Wi-Fi activa durante la prueba. Esta ejecución confirma instalación y apertura, pero no prueba TCP ni reemplaza P1–P7.

## Controles automatizados

| Control | Resultado |
|---|---|
| Hito 0: compilación/arranque inicial | Confirmado por el estudiante en Moto G35; captura en el repositorio |
| `testDebugUnitTest` de la implementación actual | Correcto: 7 pruebas, 0 errores y 0 fallos (parser, cifrado y ventana fija) |
| `assembleDebug` después de refrescar la IP del Nodo en pantalla | `BUILD SUCCESSFUL`; APK en `app/build/outputs/apk/debug/app-debug.apk` (11.061.471 bytes) |
| SHA-256 de la APK actual | `3F7A81C594962EF67133713CF809E87626904B4FA19B2C3820F44C9CBE36BBA4` |
| Instalación y apertura en un teléfono | Correctas en Moto G35 / Android 15; proceso activo y sin excepciones `AndroidRuntime` en el registro revisado |
| Estado de red durante la prueba de humo | Sin IP Wi-Fi disponible; no se intentó iniciar el servidor Nodo |
| `git diff --check` | Correcto; sin errores de whitespace |
| `lintDebug` | No concluyente: el análisis `lintAnalyzeDebug` no terminó tras varios minutos y se interrumpió; no se dispone de informe Lint |
| Emulador | No iniciado: API 37 exigió 4096 MiB; el sistema reportó 4007 MiB disponibles |
| `minSdk` | 26 en `app/build.gradle.kts` |
| Permisos | INTERNET, ACCESS_NETWORK_STATE y POST_NOTIFICATIONS; los dos primeros son normales, el último se solicita desde Android 13 |
| `allowBackup` / tráfico claro | `false` / `false` en AndroidManifest.xml |
| Git ignore | Excluye `local.properties`, `.gradle/`, `app/build/`, secretos, configuración Firebase y volcados de JVM |

El SDK Android emitió una advertencia de que el archivo de métricas `C:\Users\Warlo\.android\analytics.settings` no se puede escribir en este entorno; no impidió compilar, ejecutar los tests, instalar la APK ni abrir la app. El cambio de conexión hace que la pantalla actualice la IP local mientras permanece abierta en rol Nodo.

## Pruebas físicas pendientes

- Conectar Nodo y Panel mediante Wi-Fi TCP 5050.
- Probar ventana fija, notificación única, DISPENSAR, cierre, ACK y segundo ciclo.
- Cambiar el código para verificar descarte criptográfico.
- Cortar Wi-Fi o detener la app del Panel y observar el estado seguro CERRADO.
- Negar POST_NOTIFICATIONS en Android 13+ y repetir conexión/control.
- Registrar modelos, versiones Android, red, tiempo y evidencia en [PRUEBAS_FORMATIVA.md](PRUEBAS_FORMATIVA.md).

## Revisiones y publicación

El comentario docente «está perfecto Marcelo» fue compartido por el estudiante y se registra como aprobación previa a Hito 3. No se conserva la captura de mensajería en el repositorio porque incluye un teléfono personal. Los commits Hitos 0–2 están en el repositorio GitHub privado; no se afirma que la entrega se haya subido al AAI.
