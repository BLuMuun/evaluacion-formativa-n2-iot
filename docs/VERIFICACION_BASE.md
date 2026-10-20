# Verificación de la app — Hitos 0–4

Actualizado: 20 de octubre de 2026 (hora de Santiago).

## Login añadido después de las pruebas físicas

Por retroalimentación del profesor se integraron la pantalla inicial de Login y Firebase Authentication Email/Password. El proyecto Android está registrado y `app/google-services.json` se encuentra localmente, excluido de Git. `testDebugUnitTest assembleDebug` pasó con esta configuración. El proveedor Email/Password ya está habilitado, pero falta crear y autenticar una cuenta de prueba; por ello el acceso real no se ha validado. Las pruebas físicas IoT descritas abajo se ejecutaron en el build anterior al Login. Después de iniciar sesión en ambos teléfonos, repetir L1–L3 y P1–P7. Ver [FIREBASE_LOGIN.md](FIREBASE_LOGIN.md).

El build con la configuración Firebase local se compiló correctamente y pasó `testDebugUnitTest`; no se inició sesión porque aún no hay cuenta de prueba.

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

P7 se ejecutó con POST_NOTIFICATIONS denegado en ambos teléfonos; la conexión y el control siguieron funcionando. Después se concedió el permiso y se repitieron P3/P4: se observaron las dos notificaciones locales y se tomaron capturas completas. También se ejecutó P5 con códigos distintos; las tramas no se interpretaron y no se procesó ninguna orden. P6 se probó deteniendo el Panel tanto con solicitud pendiente como durante la dispensación; el Nodo quedó en ESPERANDO CONEXIÓN, 0/3 y CERRADO, con aviso de enlace perdido. No se probó apagar Wi-Fi desde Ajustes. No hubo excepciones AndroidRuntime en los registros revisados.

## Controles automatizados

| Control | Resultado |
|---|---|
| Hito 0: compilación/arranque inicial | Confirmado por el estudiante en Moto G35; captura en el repositorio |
| `testDebugUnitTest` de la implementación IoT anterior al Login | Correcto: 7 pruebas, 0 errores y 0 fallos (parser, cifrado y ventana fija) |
| `testDebugUnitTest assembleDebug` con Login y configuración Firebase local (20-oct-2026) | `BUILD SUCCESSFUL`; siete pruebas unitarias pasaron. Auth real no se probó |
| `assembleDebug` después de refrescar la IP del Nodo, versión IoT anterior | `BUILD SUCCESSFUL`; APK anterior de 11.061.471 bytes |
| SHA-256 de la APK IoT anterior al Login | `3F7A81C594962EF67133713CF809E87626904B4FA19B2C3820F44C9CBE36BBA4` |
| APK de Login integrada sin Firebase | 13.593.601 bytes; SHA-256 `15994A8F6A3F58776E25C5E2CD5C8225CF845EC34273D80F0ECB4B271F21194B` |
| Instalación y apertura en dos teléfonos | Correctas en Moto G35 / Android 15 y HONOR RBN-NX3 / Android 14; sin excepciones AndroidRuntime en los registros revisados |
| IP dinámica y Nodo | IP apareció al activar Wi-Fi sin reiniciar; Nodo quedó en `ESPERANDO CONEXIÓN`, contador 0/3, comedero `CERRADO`; puerto 5050 en escucha |
| P1 conexión | Pasó: ambos teléfonos mostraron CONECTADO |
| P2 monitoreo | Pasó: el HONOR mostró 2/3 aproximadamente 1,2 s después de la segunda pulsación |
| P3 alerta | Pasó: 3/3, solicitud retenida, sin notificaciones repetidas y una notificación local observada |
| P4 entrega | Pasó: DISPENSANDO, notificación local observada, CERRADO, contador 0 y ACK confirmado |
| P5 código distinto | Pasó: no se interpretaron lecturas ni se procesó una orden |
| P6 pérdida de Panel | Pasó al detener el Panel con solicitud pendiente y durante DISPENSANDO; Nodo reinició a 0/CERRADO y mostró el aviso. La variante de apagar Wi-Fi no se probó |
| P7 permiso denegado | Pasó: se denegó el permiso en ambos; conexión y control siguieron funcionando |
| `git diff --check` | Correcto; sin errores de whitespace |
| `lintDebug` | No concluyente: el análisis `lintAnalyzeDebug` no terminó tras varios minutos y se interrumpió; no se dispone de informe Lint |
| Emulador | No iniciado: API 37 exigió 4096 MiB; el sistema reportó 4007 MiB disponibles |
| `minSdk` | 26 en `app/build.gradle.kts` |
| Permisos | INTERNET, ACCESS_NETWORK_STATE y POST_NOTIFICATIONS; los dos primeros son normales, el último se solicita desde Android 13 |
| `allowBackup` / tráfico claro | `false` / `false` en AndroidManifest.xml |
| Git ignore | Excluye `local.properties`, `.gradle/`, `app/build/`, secretos, configuración Firebase y volcados de JVM |

El SDK Android emitió una advertencia de que el archivo de métricas `C:\Users\Warlo\.android\analytics.settings` no se puede escribir en este entorno; no impidió compilar, ejecutar los tests, instalar la APK ni abrir la app. El cambio de conexión hace que la pantalla actualice la IP local mientras permanece abierta en rol Nodo.

## Casos físicos adicionales pendientes

- Probar P6 apagando Wi-Fi desde Ajustes durante una solicitud y durante la dispensación; P6 ya pasó al detener el Panel.
- Completar los casos de borde y el segundo ciclo registrados en [PRUEBAS_FORMATIVA.md](PRUEBAS_FORMATIVA.md). Estos casos adicionales no cambian el resultado de P1–P7.

## Revisiones y publicación

El comentario docente «está perfecto Marcelo» fue compartido por el estudiante y se registra como aprobación previa a Hito 3. No se conserva la captura de mensajería en el repositorio porque incluye un teléfono personal. Los Hitos 0–4 y las actualizaciones de pruebas/evidencias están publicados en el repositorio GitHub privado; no se afirma que `entrega_aai.txt` se haya subido al AAI.
