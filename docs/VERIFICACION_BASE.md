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

**Prueba física de esta implementación:** `adb devices` no muestra teléfonos conectados durante esta revisión. No se han ejecutado P1–P7 ni se declara que la app actual funcione entre dos teléfonos.

## Controles automatizados

| Control | Resultado |
|---|---|
| Hito 0: compilación/arranque inicial | Confirmado por el estudiante en Moto G35; captura en el repositorio |
| `testDebugUnitTest` de la implementación actual | Correcto: 7 pruebas, 0 errores y 0 fallos (parser, cifrado y ventana fija) |
| `assembleDebug` de la implementación actual | `BUILD SUCCESSFUL`; APK generado en `app/build/outputs/apk/debug/app-debug.apk` (11.028.807 bytes) |
| Repetición después del último cambio criptográfico | `BUILD SUCCESSFUL` en 1 min 1 s; 8 tareas ejecutadas y 35 UP-TO-DATE |
| `git diff --check` | Ejecutado después de los últimos cambios; sin errores de whitespace |
| `minSdk` | 26 en `app/build.gradle.kts` |
| Permisos | INTERNET, ACCESS_NETWORK_STATE y POST_NOTIFICATIONS; los dos primeros son normales, el último se solicita desde Android 13 |
| `allowBackup` / tráfico claro | `false` / `false` en AndroidManifest.xml |
| Git ignore | Excluye `local.properties`, `.gradle/`, `app/build/`, secretos, configuración Firebase y volcados de JVM |

El SDK Android emitió una advertencia de que el archivo de métricas `C:\Users\Warlo\.android\analytics.settings` no se puede escribir en este entorno; no impidió compilar, ejecutar los tests ni generar el APK. El SHA-256 del APK más reciente es `E06AAA81076E2851B87CD851D1B964638A02868F986AB15F0BE9F0EB8AC5FFE2`.

## Pruebas físicas pendientes

- Conectar Nodo y Panel mediante Wi-Fi TCP 5050.
- Probar ventana fija, notificación única, DISPENSAR, cierre, ACK y segundo ciclo.
- Cambiar el código para verificar descarte criptográfico.
- Cortar Wi-Fi o detener la app del Panel y observar el estado seguro CERRADO.
- Negar POST_NOTIFICATIONS en Android 13+ y repetir conexión/control.
- Registrar modelos, versiones Android, red, tiempo y evidencia en [PRUEBAS_FORMATIVA.md](PRUEBAS_FORMATIVA.md).

## Revisiones y publicación

El comentario docente «está perfecto Marcelo» fue compartido por el estudiante y se registra como aprobación previa a Hito 3. No se conserva la captura de mensajería en el repositorio porque incluye un teléfono personal. Los commits Hitos 0–2 están en el repositorio GitHub privado; no se afirma que la entrega se haya subido al AAI.
