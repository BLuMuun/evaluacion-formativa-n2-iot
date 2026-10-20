# PLAN_MVP — Comedero IoT · Unidad 2 Formativa

Estado: Hitos 0–4 de la aplicación IoT y el Login están implementados; L1–L3 y P1–P7 pasaron en dos teléfonos el 20-oct-2026 con el build actual. Por retroalimentación del profesor se añadió Firebase Authentication. El proyecto y la app Android están registrados; Email/Password quedó habilitado; `app/google-services.json` funciona localmente y no se publica. La compilación con Firebase y las siete pruebas unitarias pasaron. Repositorio: https://github.com/BLuMuun/evaluacion-formativa-n2-iot.

L1–L3 del Login se comprobaron en los dos teléfonos; P1–P7 también se repitieron con Login. El detalle está en docs/FIREBASE_LOGIN.md y el registro en docs/PRUEBAS_FORMATIVA.md.

## 1. Resumen y alcance

Una app en dos teléfonos representa el Nodo (sensor y compuerta) y el Panel (monitoreo y decisión de entrega). El alcance es solo Hitos 0–4 de la Formativa. La revisión docente de los Hitos 1–2 se recibió antes de programar Hito 3. La Sumativa queda fuera de implementación.

## 2. Declaración técnica

| Decisión | Valor |
|---|---|
| Lenguaje / IDE | Kotlin / Android Studio |
| UI | Jetpack Compose + Material 3 |
| Gradle / mínimo | Kotlin DSL / minSdk 26 |
| Package | `cl.inacap.comederoiot` |
| Comunicación | Wi-Fi local TCP, puerto 5050; Nodo servidor y Panel cliente |
| Hardware | Dos teléfonos Android; no requiere ESP32 |
| Protocolo | `TIPO;CLAVE;VALOR`, con LECTURA, CMD y ACK |
| Notificaciones | Locales, NotificationCompat; sin FCM |
| Cifrado | Código de 6 dígitos → PBKDF2-HMAC-SHA256 → AES-256-GCM; IV aleatorio |
| Permisos | INTERNET, ACCESS_NETWORK_STATE y POST_NOTIFICATIONS (solicitado desde API 33) |
| Estado seguro | CERRADO, cancelar entrega y solicitud/contador a 0 al perder enlace |
| Rasguños | Ventana fija de 5 s, tres confirma; extras ignorados hasta entrega correcta |
| Login Formativo (pedido del profesor) | Firebase Authentication con Email/Password; L1–L3 probados en teléfonos |
| Sumativa futura | Cloud Firestore para lecturas; no implementado |

Construcción fijada: AGP 8.13.2, Gradle 8.13, Kotlin/Compose compiler 2.4.20, Firebase BoM 35.0.2, Compose BOM 2025.12.00, Activity Compose 1.12.2, compileSdk/targetSdk 36 y bytecode Java/Kotlin 17. Kotlin se actualizó para ser compatible con los metadatos de Firebase Authentication. El mínimo continúa en 26.

## 3. Arquitectura y protocolo

La app usa `MainActivity.kt` para navegación y permiso de notificaciones; `Sesion.kt` coordina el estado y reglas; `WifiEnlace.kt` gestiona servidor/cliente TCP; `Cripto.kt` cifra; `Mensaje.kt` valida y `Notificaciones.kt` crea avisos locales. La UI está en `ui/`.

Mensajes válidos:

| Dirección | Mensaje | Efecto |
|---|---|---|
| Nodo → Panel | `LECTURA;rasgunos;0..3` | Actualiza contador y alerta en transición a 3 |
| Panel → Nodo | `CMD;comida;DISPENSAR` | Solicita una entrega si hay solicitud pendiente |
| Nodo → Panel | `LECTURA;comedero;CERRADO` / `DISPENSANDO` | Muestra estado del comedero |
| Nodo → Panel | `ACK;comida;DISPENSADA` | Confirma la entrega |

Cada mensaje de aplicación descifrado debe tener exactamente tres campos y una combinación permitida. Ver [diseño y reglas de ventana fija](docs/HITO_2_DISENO.md).

## 4. Hitos y criterios de aceptación

### Hito 0 — Proyecto base (2.1.1)

- [x] Instalar Android Studio y SDK; el IDE reconoce la ruta del SDK.
- [x] Sincronizar y compilar la base según el registro de Android Studio.
- [x] Abrir la bienvenida en el Moto G35, confirmado por el estudiante; captura en [docs/evidencia_hito0_moto_g35.png](docs/evidencia_hito0_moto_g35.png). La versión Android no se registró.
- [x] Commit y repositorio GitHub creados.

### Hito 1 — Definición (2.1.1)

- [x] Problemática de Unidad 1, adaptación a dos teléfonos, F1–F5 y herramientas documentados.
- [x] Revisión previa del profesor aprobada, según «está perfecto Marcelo», compartido por el estudiante. No se adjunta la captura privada.
- [ ] El estudiante prepara su explicación oral de la problemática, F1–F5 y herramientas.
- [x] Material registrado en commit separado.

### Hito 2 — Diseño (2.1.1, 2.1.2)

- [x] Wi-Fi local TCP 5050, roles, protocolo y mockup documentados.
- [x] Ventana fija de 5 s y reglas de solicitud/entrega documentadas sin ventana móvil.
- [x] Diseño revisado y aprobado junto con el avance previo a Hito 3, según comentario del profesor compartido por el estudiante.
- [ ] El estudiante prepara su explicación oral de alcance, medio, mensajes y pantallas.
- [x] Material registrado en commit separado.

### Hito 3 — Prototipo conectado (2.1.2, 2.1.4)

Código implementado; comprobación física P1–P7 aprobada y registrada en docs/PRUEBAS_FORMATIVA.md.

- Implementado en código: pantallas, TCP, protocolo, ventana fija, notificaciones, entrega y cierre seguro. Esta descripción no marca pruebas como completadas.
- [x] Ejecutar P1 y P2 entre dos teléfonos; registrar resultado y evidencia.
- [x] Completar P3 y P4 verificando las notificaciones locales; contador, dispensación, cierre y ACK se observaron.
- [x] Ejecutar pérdida del Panel con solicitud pendiente y durante DISPENSANDO; Nodo reinició a 0/CERRADO.
- [x] Ejecutar P7: denegar POST_NOTIFICATIONS en ambos teléfonos y comprobar que conexión y control continúan funcionando.
- [ ] Revisar y explicar el código con el profesor; cerrar criterios del hito solo tras evidencias reales.

### Hito 4 — Seguridad, pruebas y preentrega (2.1.3, 2.1.4)

Cifrado, validación y P1–P7 verificados. Los casos unitarios comprueban mensajes alterados, parseo estricto y ventana fija.

- Implementado en código: AES-256-GCM, clave derivada, validación por rol y descarte de trama inválida. P5 pasó en teléfonos y el cifrado/parser también cuentan con pruebas unitarias.
- [x] Ejecutar P5 con códigos distintos y guardar evidencia del descarte y del timeout.
- [x] Probar con tests unitarios mensajes alterados, parser estricto, ventana fija y reinicio de la solicitud.
- [ ] Repetir físicamente en dos teléfonos el segundo ciclo de solicitud/notificación; es una comprobación adicional, fuera de P1–P7.
- Compilación y 7 pruebas unitarias pasan; P1–P7 pasaron en teléfonos. Las comprobaciones físicas adicionales se distinguen en docs/PRUEBAS_FORMATIVA.md.
- [x] Revisar README, seguridad y evidencias antes de publicar; no se subió la captura secundaria P5 que exponía ubicación del teléfono.
- [x] Publicar los commits finales en GitHub.
- [ ] Subir `entrega_aai.txt` al AAI manualmente si la entrega aún está pendiente.

### Ajuste posterior solicitado por el profesor — Login Formativo

- [x] Crear pantalla de usuario (correo) y contraseña como primera pantalla.
- [x] Integrar el acceso con Firebase Authentication; sin registro automático ni credenciales de prueba.
- [x] Mantener `google-services.json` y contraseñas fuera del repositorio.
- [x] Crear proyecto Firebase, registrar Android package `cl.inacap.comederoiot` y guardar `google-services.json` en `app/` localmente (ignorado por Git).
- [x] Habilitar Email/Password en Firebase Console.
- [x] Crear una cuenta de prueba desde Firebase Console; la contraseña la ingresa y administra el estudiante.
- [x] Compilar con la configuración Firebase local: `testDebugUnitTest assembleDebug`, 7 pruebas unitarias aprobadas.
- [x] Instalar y probar acceso correcto en el Moto G35; Firebase autenticó la sesión y la app abrió Conexión.
- [x] Probar credenciales incorrectas, persistencia tras reabrir y cierre de sesión en teléfono.
- [x] Repetir P1–P7 con el APK que incluye Login en Moto G35 y HONOR; resultados registrados en docs/PRUEBAS_FORMATIVA.md.

## 5. Pruebas

La tabla y los pasos están en [docs/PRUEBAS_FORMATIVA.md](docs/PRUEBAS_FORMATIVA.md). P1–P7 pasaron en teléfonos físicos; los casos opcionales pendientes están separados. Una compilación o prueba unitaria no sustituye las pruebas de interconexión física.

## 6. Trazabilidad

| Requisito | Hito | Verificación pendiente |
|---|---|---|
| Herramientas y proyecto | 0, 1 | Hito 0 confirmado; versión Android de esa primera captura no registrada |
| Problemática, F1–F5 | 1 | Revisión docente recibida; explicación oral del estudiante pendiente |
| Medio, mensajes y mockup | 2 | Revisión docente recibida; explicación oral del estudiante pendiente |
| Login | Ajuste profesor | L1–L3 pasaron en Moto G35 y HONOR |
| F1 sensor y F2 monitoreo | 3 | P1/P2 pasaron de nuevo con Login en los dos teléfonos |
| F3 entrega y ACK | 3 | P4 pasó de nuevo con Login; notificación local y ACK observados |
| F4 notificaciones | 3 | P3/P4 pasaron con permiso concedido; P7 pasó de nuevo con permiso denegado |
| F5 estado seguro | 3, 4 | P6 pasó de nuevo al cerrar el Panel con solicitud pendiente; Wi-Fi no se apagó desde Ajustes |
| Cifrado y validación | 3, 4 | P5 pasó de nuevo; mensajes alterados y parser estricto también pasan pruebas unitarias |

## 7. Riesgos y límites pendientes

- Red institucional puede aislar clientes; P1 se probó en una red Wi-Fi local compartida.
- El tiempo para detectar una pérdida depende de la notificación que Android/red entregue al socket; P6 se observó al detener el Panel.
- El código de seis dígitos tiene entropía limitada y AES-GCM no equivale a certificación ISO/IEC.
- La app debe permanecer ejecutándose en primer plano; no se implementa servicio en segundo plano.
- La app usa una sal de contexto fija definida en código y PBKDF2-HMAC-SHA256; esto es una protección académica básica, no una autenticación robusta de dispositivos.

## 8. Fuera de alcance

Firestore, almacenamiento de lecturas, registro de cuentas dentro de la app, historial, MQTT, HiveMQ, FCM, ESP32 real, varios nodos y funciones opcionales. Firebase Authentication se limita al Login solicitado por el profesor; no se agrega base de datos.
