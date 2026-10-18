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

**Prueba física en dos teléfonos (18 de octubre de 2026):** APK instalada en un Moto G35 con Android 15 (API 35), rol Nodo, y un HONOR RBN-NX3 con Android 14 (API 34), rol Panel. En la misma red Wi-Fi local se verificaron conexión TCP, lecturas 2/3 y 3/3, retención de la solicitud, entrega DISPENSANDO → CERRADO, ACK confirmado y recuperación segura al detener el Panel con solicitud pendiente y durante la dispensación. Las capturas asociadas están en [docs/evidencia](evidencia/). No se guardaron el SSID, las direcciones IP ni el código temporal.

POST_NOTIFICATIONS figuró como no concedido en ambos equipos. Por ese motivo no se pudieron verificar las notificaciones locales de P3/P4; la app siguió conectada y el control funcionó. P5 queda pendiente de repetición con evidencia; el corte de Wi-Fi desde Ajustes tampoco se probó. En la pérdida TCP causada al detener el Panel, el Nodo quedó en ESPERANDO CONEXIÓN, 0/3 y CERRADO, y mostró el aviso de enlace perdido. El proceso del Nodo permaneció activo y no hubo excepciones AndroidRuntime en los registros revisados.

## Controles automatizados

| Control | Resultado |
|---|---|
| Hito 0: compilación/arranque inicial | Confirmado por el estudiante en Moto G35; captura en el repositorio |
| `testDebugUnitTest` de la implementación actual | Correcto: 7 pruebas, 0 errores y 0 fallos (parser, cifrado y ventana fija) |
| `assembleDebug` después de refrescar la IP del Nodo en pantalla | `BUILD SUCCESSFUL`; APK en `app/build/outputs/apk/debug/app-debug.apk` (11.061.471 bytes) |
| SHA-256 de la APK actual | `3F7A81C594962EF67133713CF809E87626904B4FA19B2C3820F44C9CBE36BBA4` |
| Instalación y apertura en dos teléfonos | Correctas en Moto G35 / Android 15 y HONOR RBN-NX3 / Android 14; sin excepciones AndroidRuntime en los registros revisados |
| IP dinámica y Nodo | IP apareció al activar Wi-Fi sin reiniciar; Nodo quedó en `ESPERANDO CONEXIÓN`, contador 0/3, comedero `CERRADO`; puerto 5050 en escucha |
| P1 conexión | Pasó: ambos teléfonos mostraron CONECTADO |
| P2 monitoreo | Pasó: el HONOR mostró 2/3 aproximadamente 1,2 s después de la segunda pulsación |
| P3 alerta | Parcial: 3/3 y solicitud retenida verificados; notificación no visible porque POST_NOTIFICATIONS no estaba concedido |
| P4 entrega | Parcial: DISPENSANDO, CERRADO, contador 0 y ACK confirmado observados; notificación del Nodo no verificable con permiso denegado |
| P5 código distinto | Pendiente de repetición documentada |
| P6 pérdida de Panel | Pasó para cierre forzado del Panel con solicitud pendiente y durante DISPENSANDO; Nodo reinició a 0/CERRADO y mostró el aviso |
| P7 permiso denegado | Parcial: el sistema informa permiso no concedido en ambos; conexión y control siguieron funcionando |
| `git diff --check` | Correcto; sin errores de whitespace |
| `lintDebug` | No concluyente: el análisis `lintAnalyzeDebug` no terminó tras varios minutos y se interrumpió; no se dispone de informe Lint |
| Emulador | No iniciado: API 37 exigió 4096 MiB; el sistema reportó 4007 MiB disponibles |
| `minSdk` | 26 en `app/build.gradle.kts` |
| Permisos | INTERNET, ACCESS_NETWORK_STATE y POST_NOTIFICATIONS; los dos primeros son normales, el último se solicita desde Android 13 |
| `allowBackup` / tráfico claro | `false` / `false` en AndroidManifest.xml |
| Git ignore | Excluye `local.properties`, `.gradle/`, `app/build/`, secretos, configuración Firebase y volcados de JVM |

El SDK Android emitió una advertencia de que el archivo de métricas `C:\Users\Warlo\.android\analytics.settings` no se puede escribir en este entorno; no impidió compilar, ejecutar los tests, instalar la APK ni abrir la app. El cambio de conexión hace que la pantalla actualice la IP local mientras permanece abierta en rol Nodo.

## Pruebas físicas por completar

- Repetir P5 con códigos distintos y capturar el estado de conexión TCP inicial y la desconexión por falta de lecturas válidas.
- Repetir P3/P4 con POST_NOTIFICATIONS concedido para verificar las notificaciones locales y que la alerta de solicitud no se duplique.
- Probar P6 apagando Wi-Fi desde Ajustes durante una solicitud y durante la dispensación; hasta ahora se verificó la pérdida al detener el Panel.
- Completar los casos de borde y el segundo ciclo registrados en [PRUEBAS_FORMATIVA.md](PRUEBAS_FORMATIVA.md).

## Revisiones y publicación

El comentario docente «está perfecto Marcelo» fue compartido por el estudiante y se registra como aprobación previa a Hito 3. No se conserva la captura de mensajería en el repositorio porque incluye un teléfono personal. Los commits Hitos 0–2 están en el repositorio GitHub privado; no se afirma que la entrega se haya subido al AAI.
