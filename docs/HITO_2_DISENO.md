# Hito 2 — Diseño para revisión docente

Estado: DISEÑO REVISADO Y APROBADO POR EL DOCENTE, según respuesta compartida por el estudiante.

## Medio inalámbrico

Wi-Fi local mediante sockets TCP, puerto **5050**. El Nodo abrirá el servidor y el Panel se conectará a su IP. Una aplicación, dos roles y un solo Panel por Nodo.

La cama y el comedero pueden estar en habitaciones distintas: Wi-Fi permite cubrir esa distancia mediante un router o hotspot local. Su consumo es mayor que Bluetooth, pero en esta simulación con teléfonos se priorizan alcance y disponibilidad. TCP mantiene el orden de los mensajes y resulta suficiente para la pequeña cantidad de datos. La estabilidad depende de la red: una red institucional puede aislar clientes; debe probarse un router propio o hotspot. No se necesita Internet para operar, aunque sí para descargar las herramientas y dependencias inicialmente.

## Interfaz y navegación

El diseño de las tres pantallas está en [mockup.md](mockup.md) y [mockup.svg](mockup.svg).

Conexión → Nodo o Panel según rol. Desconectar → Conexión. La primera pantalla funcional será Conexión, nunca login. La versión base actual solo contiene una bienvenida del Hito 0.

## Protocolo de aplicación

Después de descifrar, cada mensaje contiene exactamente tres campos: `TIPO;CLAVE;VALOR`.

| Dirección | Mensaje permitido | Efecto |
|---|---|---|
| Nodo → Panel | `LECTURA;rasgunos;0` | Contador 0/3 |
| Nodo → Panel | `LECTURA;rasgunos;1` | Contador 1/3 |
| Nodo → Panel | `LECTURA;rasgunos;2` | Contador 2/3 |
| Nodo → Panel | `LECTURA;rasgunos;3` | Solicitud pendiente; notificar solo transición de menos de 3 a 3 |
| Panel → Nodo | `CMD;comida;DISPENSAR` | Iniciar entrega simulada |
| Nodo → Panel | `ACK;comida;DISPENSADA` | Entrega confirmada y comedero cerrado |
| Nodo → Panel | `LECTURA;comedero;CERRADO` | Informar estado cerrado |
| Nodo → Panel | `LECTURA;comedero;DISPENSANDO` | Informar estado de entrega |

Las dos lecturas de estado cumplen la visualización del comedero solicitada. El Nodo reutiliza `LECTURA;rasgunos;X` cada 2 segundos como latido; si el Panel pasa 6 segundos sin recibir un mensaje válido, cierra la sesión y el Nodo detecta el cierre del socket. No se agregan tipos ni claves al protocolo. Cada línea de transporte es Base64 de IV + texto cifrado y etiqueta GCM, seguida de salto de línea. El texto del protocolo nunca se envía en claro.

`Mensaje.desdeLinea()` valida exactamente tres campos; tipos, claves y valores están restringidos a las combinaciones de esta tabla, respetando mayúsculas. La guía usa un máximo de 16 caracteres para clave y valor; todos los valores del proyecto cumplen ese límite. Se descartan campos adicionales, vacíos, tipos/claves desconocidos, valores fuera de rango o demasiado largos. El receptor comprueba también la dirección permitida por rol. Descartar una entrada no cierra la app.

## Reglas específicas del proyecto: ventana FIJA

1. El primer rasguño inicia un temporizador de 5 segundos y lleva el contador a 1.
2. Los rasguños siguientes antes de vencer esa misma ventana aumentan el contador. El temporizador no se reinicia con cada pulsación.
3. Si termina la ventana sin alcanzar 3, el contador vuelve a 0 y se espera un nuevo primer rasguño. Se comunica ese cambio.
4. Si se alcanzan 3 antes de vencer la ventana, la solicitud queda confirmada; el contador permanece en 3/3, se comunica la lectura y el Panel notifica una vez.
5. Los rasguños adicionales se ignoran mientras exista una solicitud pendiente. El Nodo espera la decisión del Panel y no dispensa automáticamente.
6. Al recibir `CMD;comida;DISPENSAR`, el Nodo muestra DISPENSANDO y la notificación «Comando de alimentación recibido». Simula la apertura durante aproximadamente 1,5 segundos con lógica no bloqueante.
7. Si la entrega termina correctamente, el Nodo cierra, envía `ACK;comida;DISPENSADA`, reinicia a 0 y comunica la nueva lectura. Queda disponible para otra solicitud. El Panel muestra «Última entrega: confirmada».
8. Si se pierde el enlace, se cancela cualquier dispensación, se cierra la compuerta y se reinician solicitud y contador a 0. No se enviará una confirmación de entrega completa por una operación interrumpida. Al restablecer el enlace se parte del estado seguro.

Estas reglas fueron definidas expresamente por el estudiante. No se pueden sustituir por una ventana móvil ni cambiar sin consultarlo. La pérdida del enlace es la excepción de seguridad al mantenimiento de 3/3 hasta la entrega.

## Seguridad Formativa implementada

- Código exactamente de seis dígitos; PBKDF2-HMAC-SHA256 (120.000 iteraciones) deriva clave AES-256 desde el código.
- AES-GCM con IV aleatorio de 12 bytes por mensaje y etiqueta de autenticidad/integridad; Base64 para transporte.
- Mensajes alterados o con otro código se descartan. TCP conectado no significa que los mensajes puedan descifrarse.
- INTERNET para sockets, ACCESS_NETWORK_STATE para observar pérdida de Wi-Fi y POST_NOTIFICATIONS (Android 13+). El permiso de notificaciones se pide por versión; negarlo no impide controlar ni conectar.
- Sin Bluetooth, ubicación, archivos, cámara, micrófono ni contactos.
- `allowBackup=false` y `usesCleartextTraffic=false` en el manifiesto. Este último atributo no cifra un socket TCP: la protección de su contenido será AES-GCM.
- CERRADO y contador 0 ante pérdida del enlace, con aviso visible/notificación cuando esté permitido.

Los parámetros de derivación usan una sal fija de contexto no secreta. Un código de seis dígitos tiene entropía limitada; estas medidas corresponden al nivel básico Formativo y no deben presentarse como seguridad de producción. No se afirma certificación ni cumplimiento auditado de ISO/IEC 27400.

## Arquitectura implementada

| Archivo | Responsabilidad futura | Estado |
|---|---|---|
| MainActivity.kt | Entrada, permiso de notificaciones y navegación | Implementado |
| Sesion.kt | Rol, contador, temporizador, solicitud, entrega y estado seguro | Implementado |
| Notificaciones.kt | Canal y eventos locales | Implementado |
| enlace/Mensaje.kt | Parseo y validación estricta | Implementado |
| enlace/Enlace.kt | Contrato de conexión y mensajes | Implementado |
| enlace/WifiEnlace.kt | Servidor/cliente TCP 5050, trabajo de red fuera de UI | Implementado |
| seguridad/Cripto.kt | Derivación, AES-GCM y descifrado | Implementado |
| ui/PantallaConexion.kt | Rol, código e IP | Implementado |
| ui/PantallaNodo.kt | Contador y simulación | Implementado |
| ui/PantallaPanel.kt | Monitoreo, orden y confirmación | Implementado |

La implementación necesita compilación y pruebas físicas para cerrar la aceptación de Hitos 3–4. Los resultados P1–P7 no se presuponen.

## Revisión con el docente antes de programar Hito 3

| Registro | Valor |
|---|---|
| Estado | APROBADO, según el mensaje confirmado por el estudiante |
| Fecha | No visible en la captura compartida |
| Retroalimentación sobre Wi-Fi, protocolo y mockup | «Está perfecto, Marcelo» |
| Ajustes solicitados | Ninguno informado |
| Evidencia de revisión / aprobación | Confirmación recibida por mensajería. No se copia la captura al repositorio porque contiene un número telefónico personal. |

Fuente principal: *Guía de desarrollo — Unidad 2 · App Android para IoT*, pp. 12–16, 25–26. La guía de IA aporta el método de planificación; sus requisitos de Sumativa y placas no se trasladan a esta entrega.
