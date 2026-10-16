# Hito 2 — Diseño para revisión docente

Estado: DOCUMENTACIÓN PREPARADA. Revisión docente: **PENDIENTE**.
No autoriza a implementar el Hito 3.

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

Las dos lecturas de estado están previstas para cumplir la visualización del comedero solicitada; no agregan una función distinta. Cada línea de transporte será Base64 de IV + texto cifrado y etiqueta GCM, seguida de salto de línea. El texto del protocolo nunca se enviará en claro.

Validación prevista de `Mensaje.desdeLinea()`: exactamente tres campos; tipos, claves y valores restringidos a las combinaciones de esta tabla, respetando mayúsculas. La guía usa un máximo de 16 caracteres para clave y valor; todos los valores del proyecto cumplen ese límite. Se descartarán campos adicionales, vacíos, tipos/claves desconocidos, valores fuera de rango o demasiado largos. El receptor comprobará también la dirección permitida por rol. Descartar una entrada no cerrará la app.

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

## Seguridad prevista, sin implementar

- Código exactamente de seis dígitos; derivación de clave AES-256 desde el código.
- AES-GCM con IV aleatorio por mensaje y autenticación/integridad; Base64 para transporte.
- Mensajes alterados o con otro código se descartan. TCP conectado no significa vinculación criptográfica validada.
- Solo INTERNET y POST_NOTIFICATIONS (Android 13+) en el Hito 3. Permiso de notificaciones pedido por versión; negarlo no debe impedir controlar ni conectar.
- Sin Bluetooth, ubicación, archivos, cámara, micrófono ni contactos.
- `allowBackup=false` y `usesCleartextTraffic=false` en el manifiesto. Este último atributo no cifra un socket TCP: la protección de su contenido será AES-GCM.
- CERRADO y contador 0 ante pérdida del enlace, con aviso visible/notificación cuando esté permitido.

Antes de implementar Cripto se concretarán los parámetros de derivación tomando la guía como referencia y consultando cualquier decisión indispensable no resuelta. Un código de seis dígitos tiene entropía limitada; no debe presentarse como seguridad de producción. No se afirma cumplimiento certificado de ISO/IEC 27400.

## Arquitectura prevista

| Archivo | Responsabilidad futura | Estado |
|---|---|---|
| MainActivity.kt | Entrada, permisos y navegación | Solo bienvenida base |
| Sesion.kt | Rol, contador, temporizador, solicitud, entrega y estado seguro | No creado |
| Notificaciones.kt | Canal y eventos locales | No creado |
| enlace/Mensaje.kt | Parseo y validación estricta | No creado |
| enlace/Enlace.kt | Contrato común de conexión y mensajes | No creado |
| enlace/WifiEnlace.kt | Servidor/cliente TCP 5050, tareas de red fuera de la UI | No creado |
| seguridad/Cripto.kt | Derivación de clave, cifrado y descifrado | No creado |
| ui/PantallaConexion.kt | Selección de rol, código e IP | No creado |
| ui/PantallaNodo.kt | Contador y simulación | No creado |
| ui/PantallaPanel.kt | Monitoreo, orden y confirmación | No creado |

Los directorios vacíos reservados se conservan con `.gitkeep`; no contienen implementaciones ni funciones falsas.

## Revisión con el docente antes de programar Hito 3

| Registro | Valor |
|---|---|
| Estado | PENDIENTE |
| Fecha | Por completar |
| Retroalimentación sobre Wi-Fi, protocolo y mockup | Por completar |
| Ajustes solicitados | Por completar |
| Evidencia de revisión / aprobación | Por completar |

Fuente principal: *Guía de desarrollo — Unidad 2 · App Android para IoT*, pp. 12–16, 25–26. La guía de IA aporta el método de planificación; sus requisitos de Sumativa y placas no se trasladan a esta entrega.
