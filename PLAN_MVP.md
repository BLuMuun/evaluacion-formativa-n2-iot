# PLAN_MVP — Comedero IoT · Unidad 2 Formativa

Estado: Hitos 0–4 del alcance Formativo están documentados y el código está implementado. En dos teléfonos pasaron P1, P2 y P6; P3 y P4 quedaron parciales por el permiso de notificaciones no concedido; P5 sigue pendiente de repetición; P7 se comprobó parcialmente con el permiso denegado. El profesor aprobó la revisión previa a Hito 3 con «está perfecto Marcelo», según la evidencia compartida por el estudiante. Repositorio: https://github.com/BLuMuun/evaluacion-formativa-n2-iot.

Las casillas de aceptación final permanecen pendientes cuando falta un resultado o evidencia. El detalle de cada prueba física está en docs/PRUEBAS_FORMATIVA.md.

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
| Sumativa futura | Firebase Authentication + Cloud Firestore, solo declarados en documentación |

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

Código implementado; comprobación física parcial, registrada en docs/PRUEBAS_FORMATIVA.md.

- Implementado en código: pantallas, TCP, protocolo, ventana fija, notificaciones, entrega y cierre seguro. Esta descripción no marca pruebas como completadas.
- [x] Ejecutar P1 y P2 entre dos teléfonos; registrar resultado y evidencia.
- [ ] Completar P3 y P4 verificando las notificaciones locales; contador, dispensación, cierre y ACK sí se observaron.
- [x] Ejecutar pérdida del Panel con solicitud pendiente y durante DISPENSANDO; Nodo reinició a 0/CERRADO.
- [x] Comprobar conexión y control con POST_NOTIFICATIONS no concedido; repetir la prueba del diálogo de permiso sigue pendiente.
- [ ] Revisar y explicar el código con el profesor; cerrar criterios del hito solo tras evidencias reales.

### Hito 4 — Seguridad, pruebas y preentrega (2.1.3, 2.1.4)

Cifrado, validación y casos unitarios implementados. P5 y la aceptación completa permanecen pendientes.

- Implementado en código: AES-256-GCM, clave derivada, validación por rol y descarte de trama inválida. La aceptación queda pendiente de prueba.
- [ ] Repetir P5 con códigos distintos y guardar evidencia del descarte y del timeout.
- [ ] Probar mensajes alterados, parser estricto, ventana fija móvil/inmóvil y segundo ciclo.
- Compilación y 7 pruebas unitarias pasan; resultados físicos, parciales y pendientes están diferenciados en docs/PRUEBAS_FORMATIVA.md.
- [ ] Revisar README, seguridad y ausencia de secretos/datos personales antes de publicar.
- [ ] Publicar los commits finales y actualizar la entrega AAI solo cuando corresponda.

## 5. Pruebas

La tabla y los pasos están en [docs/PRUEBAS_FORMATIVA.md](docs/PRUEBAS_FORMATIVA.md). Allí se separan los casos pasados, parciales y pendientes. Una compilación o prueba unitaria no sustituye las pruebas de interconexión física.

## 6. Trazabilidad

| Requisito | Hito | Verificación pendiente |
|---|---|---|
| Herramientas y proyecto | 0, 1 | Evidencia de arranque actual y versión Android |
| Problemática, F1–F5 | 1 | Explicación del estudiante; revisión docente recibida |
| Medio, mensajes y mockup | 2 | Explicación del estudiante; revisión docente recibida |
| F1 sensor y F2 monitoreo | 3 | P1/P2 pasaron; ventana incompleta observada |
| F3 entrega y ACK | 3 | P4 pasó en entrega/ACK; notificación local pendiente |
| F4 notificaciones | 3 | P3/P4 parciales; permisos no concedidos |
| F5 estado seguro | 3, 4 | P6 pasó al detener el Panel; falta apagar Wi-Fi desde Ajustes |
| Cifrado y validación | 3, 4 | P5 pendiente; parser y mensajes alterados tienen pruebas unitarias, falta la prueba física |

## 7. Riesgos y límites pendientes

- Red institucional puede aislar clientes; probar router/hotspot propio en P1.
- El tiempo para detectar una pérdida depende de la notificación que Android/red entregue al socket; medir en P6.
- El código de seis dígitos tiene entropía limitada y AES-GCM no equivale a certificación ISO/IEC.
- La app debe permanecer ejecutándose en primer plano; no se implementa servicio en segundo plano.
- La app usa una sal de contexto fija definida en código y PBKDF2-HMAC-SHA256; esto es una protección académica básica, no una autenticación robusta de dispositivos.

## 8. Fuera de alcance

Login, usuarios, SQLite, Firestore, Firebase Authentication implementado, historial, MQTT, HiveMQ, FCM, ESP32 real, varios nodos y funciones opcionales. Firebase Authentication + Cloud Firestore son decisiones futuras de Sumativa y solo se documentan.
