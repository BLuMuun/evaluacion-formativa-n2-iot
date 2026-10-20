# PLAN_MVP — Comedero IoT · Unidad 2 Formativa

Estado: Hitos 0–4 de la aplicación IoT están implementados; P1–P7 se probaron en dos teléfonos con el build anterior al Login. Por retroalimentación del profesor, se agregó a la Formativa una pantalla de Login integrada con Firebase Authentication. Falta que el estudiante cree/configure el proyecto Firebase, agregue `app/google-services.json` localmente y ejecute pruebas de acceso y regresión en teléfonos. El archivo de configuración no se publica. Repositorio: https://github.com/BLuMuun/evaluacion-formativa-n2-iot.

El Login aún no puede autenticar hasta que exista configuración y una cuenta Firebase; además se deben repetir P1–P7 con la versión que ya exige Login. El detalle de preparación está en docs/FIREBASE_LOGIN.md y el registro de pruebas en docs/PRUEBAS_FORMATIVA.md.

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
| Login Formativo (pedido del profesor) | Firebase Authentication con proveedor Email/Password; requiere configuración local pendiente |
| Sumativa futura | Cloud Firestore para lecturas; no implementado |

Construcción fijada: AGP 8.13.2, Gradle 8.13, Kotlin/Compose compiler 2.2.21, Compose BOM 2025.12.00, Activity Compose 1.12.2, compileSdk/targetSdk 36 y bytecode Java/Kotlin 17. El mínimo continúa en 26.

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
- [ ] Crear proyecto Firebase y registrar Android package `cl.inacap.comederoiot`.
- [ ] Habilitar Email/Password, crear cuenta de prueba y guardar `google-services.json` en `app/`.
- [ ] Compilar e instalar con configuración Firebase, probar acceso correcto/incorrecto y cerrar sesión.
- [ ] Repetir P1–P7 con el APK que incluye Login.

## 5. Pruebas

La tabla y los pasos están en [docs/PRUEBAS_FORMATIVA.md](docs/PRUEBAS_FORMATIVA.md). P1–P7 pasaron en teléfonos físicos; los casos opcionales pendientes están separados. Una compilación o prueba unitaria no sustituye las pruebas de interconexión física.

## 6. Trazabilidad

| Requisito | Hito | Verificación pendiente |
|---|---|---|
| Herramientas y proyecto | 0, 1 | Hito 0 confirmado; versión Android de esa primera captura no registrada |
| Problemática, F1–F5 | 1 | Revisión docente recibida; explicación oral del estudiante pendiente |
| Medio, mensajes y mockup | 2 | Revisión docente recibida; explicación oral del estudiante pendiente |
| Login | Ajuste profesor | Pantalla y puente Firebase implementados; configuración y prueba real pendientes |
| F1 sensor y F2 monitoreo | 3 | P1/P2 pasaron en versión anterior; repetir en APK con Login |
| F3 entrega y ACK | 3 | P4 pasó; notificación local observada |
| F4 notificaciones | 3 | P3/P4 pasaron con permiso concedido; P7 pasó con permiso denegado |
| F5 estado seguro | 3, 4 | P6 pasó al cerrar el Panel; no se apagó el Wi-Fi desde Ajustes |
| Cifrado y validación | 3, 4 | P5 pasó; mensajes alterados y parser estricto pasan pruebas unitarias |

## 7. Riesgos y límites pendientes

- Red institucional puede aislar clientes; P1 se probó en una red Wi-Fi local compartida.
- El tiempo para detectar una pérdida depende de la notificación que Android/red entregue al socket; P6 se observó al detener el Panel.
- El código de seis dígitos tiene entropía limitada y AES-GCM no equivale a certificación ISO/IEC.
- La app debe permanecer ejecutándose en primer plano; no se implementa servicio en segundo plano.
- La app usa una sal de contexto fija definida en código y PBKDF2-HMAC-SHA256; esto es una protección académica básica, no una autenticación robusta de dispositivos.

## 8. Fuera de alcance

Firestore, almacenamiento de lecturas, registro de cuentas dentro de la app, historial, MQTT, HiveMQ, FCM, ESP32 real, varios nodos y funciones opcionales. Firebase Authentication se limita al Login solicitado por el profesor; no se agrega base de datos.
