# Comedero IoT — Unidad 2 Formativa

**Estado: implementación de Hitos 3–4 preparada; revisión docente de Hitos 1–2 aprobada según el comentario compartido por el estudiante; pruebas físicas P1–P7 pendientes.**

La app contiene las pantallas Conexión, Nodo IoT y Panel de control, comunicación Wi-Fi TCP, cifrado AES-GCM, lógica de rasguños y entrega, y notificaciones locales. La guía considera Hitos 0–4 parte de la Formativa. El profesor aprobó la revisión previa a Hito 3 con el comentario «está perfecto Marcelo», según la captura compartida por el estudiante. La aprobación se registra sin incluir su captura de WhatsApp ni datos personales.

La implementación aún requiere instalar y probar la versión actualizada en dos teléfonos. P1–P7 siguen pendientes y no se declara verificada la interconexión física. El arranque anterior del Hito 0 en el Moto G35 se conserva como antecedente, no como prueba de las funciones actuales. Ver [verificación](docs/VERIFICACION_BASE.md) y [pruebas pendientes](docs/PRUEBAS_FORMATIVA.md).

La versión actual compila con `assembleDebug` y pasan las 7 pruebas unitarias de ventana, protocolo y cifrado. En esta sesión no había teléfonos conectados al PC; estas comprobaciones automáticas no sustituyen la ejecución física P1–P7.

## Problemática

En el hogar, un gato rasguña la cama cuando quiere alimento, provocando agujeros y deterioro. Actualmente no existe una forma automática de interpretar este comportamiento y gestionar una solicitud de alimento. El proyecto busca representar este sistema mediante una aplicación móvil que simule la detección de rasguños y el control del comedero.

## Solución

Una misma aplicación se instalará en dos teléfonos. A actuará como Nodo IoT y simulará el sensor, la solicitud y la compuerta. B será el Panel: visualizará lecturas, recibirá una alerta y enviará la orden de dispensar. Tres rasguños dentro de una ventana fija de cinco segundos generan una solicitud; la decisión de alimentar pertenece al usuario del Panel.

## Relación con Unidad 1

Unidad 1: **Sensor → ESP32 → Wi-Fi/MQTT → ESP32 → Servo**, con HiveMQ Cloud. El sensor de vibración se simuló con un botón; el primer ESP32 detectaba rasguños y el segundo abría y cerraba el comedero.

Unidad 2 Formativa: **Teléfono Nodo ↔ Wi-Fi TCP ↔ Teléfono Panel**. El Nodo reúne temporalmente la simulación del sensor y del actuador. Los teléfonos sustituyen al hardware real; no se depende de placas, broker ni Internet para la operación local.

## Herramientas

| Herramienta | Justificación |
|---|---|
| Android Studio | IDE requerido; integra SDK, Gradle, instalación y depuración Android. |
| Kotlin | Lenguaje requerido; código conciso, manejo de nulos y corrutinas para trabajo no bloqueante. |
| Jetpack Compose | Interfaz declarativa que representa el estado sin gestionar vistas manualmente. |
| Material 3 | Componentes, espaciado y tipografía coherentes con Android. |
| Gradle Kotlin DSL | Configuración de construcción en Kotlin con versiones fijadas. |

Base: package `cl.inacap.comederoiot`, minSdk 26, compileSdk/targetSdk 36, AGP 8.13.2, Gradle 8.13, Kotlin 2.2.21, Compose BOM 2025.12.00 y Activity Compose 1.12.2. Se utiliza el plugin Compose de la misma versión que Kotlin. La elección de API 36 sigue el ejemplo de la guía; el mínimo permanece en 26. [Compatibilidad oficial de AGP](https://developer.android.com/build/releases/agp-8-13-0-release-notes).

La conexión usa sockets nativos `java.net`; corrutinas mantienen la interfaz disponible durante lectura y entrega; `javax.crypto` implementa el cifrado; NotificationCompat entrega avisos locales.

## Medio inalámbrico

Wi-Fi local mediante TCP, puerto **5050**. El Nodo funciona como servidor y el Panel como cliente.

- Distancia: cama y comedero pueden estar en habitaciones distintas.
- Red: ambos teléfonos compartirán un router o hotspot, sin broker externo.
- Estabilidad: TCP entrega bytes ordenados; deben comprobarse señal y ausencia de aislamiento entre clientes.
- Datos: las lecturas y órdenes son mensajes pequeños.
- Energía: Wi-Fi consume más que Bluetooth; se priorizan alcance y disponibilidad para esta simulación con teléfonos.

No se implementará un segundo medio inalámbrico.

## Funcionalidades de la Formativa

| ID | Funcionalidad | Estado |
|---|---|---|
| F1 | Simular rasguños, aplicar ventana fija de 5 s y comunicar cada cambio. | Implementado; prueba física pendiente |
| F2 | Monitorear X/3, enlace y comedero en Panel. | Implementado; prueba física pendiente |
| F3 | Orden DISPENSAR, entrega no bloqueante de unos 1,5 s, cierre y ACK. | Implementado; prueba física pendiente |
| F4 | Notificación única de solicitud en Panel y de comando recibido en Nodo. | Implementado; prueba física pendiente |
| F5 | Ante pérdida: aviso, CERRADO, cancelar dispensación y reiniciar contador/solicitud. | Implementado; probar especialmente corte de Wi-Fi |

El primer rasguño inicia la ventana fija; si al vencer no hay tres, vuelve a cero. Al llegar a tres, permanece en 3/3, ignora nuevos rasguños y espera la decisión del Panel. Se reinicia tras entrega correcta y ACK; una desconexión también lo reinicia por seguridad. [Reglas completas e inalterables sin consulta](docs/HITO_2_DISENO.md).

## Protocolo

Texto después de descifrar: **TIPO;CLAVE;VALOR**, exactamente tres campos.

```text
LECTURA;rasgunos;0
LECTURA;rasgunos;1
LECTURA;rasgunos;2
LECTURA;rasgunos;3
CMD;comida;DISPENSAR
ACK;comida;DISPENSADA
LECTURA;comedero;CERRADO
LECTURA;comedero;DISPENSANDO
```

LECTURA y ACK viajan del Nodo al Panel; CMD del Panel al Nodo. El Nodo reutiliza `LECTURA;rasgunos;X` cada 2 s como latido; si el Panel no recibe un mensaje válido durante 6 s, cierra la sesión por seguridad. No se agregan tipos ni claves. Se descartan campos y combinaciones no permitidas. [Diseño del protocolo](docs/HITO_2_DISENO.md).

## Mockup

[Mockup textual y navegación](docs/mockup.md) · [Mockup SVG](docs/mockup.svg).

Conexión permite elegir Nodo/Panel, muestra Wi-Fi y pide código de seis dígitos. Nodo muestra su IP; Panel pide la IP del Nodo. Nodo y Panel muestran enlace, contador y comedero. No hay pantalla de login.

## Seguridad

Implementada para esta Formativa:

- Código de seis dígitos; PBKDF2-HMAC-SHA256 deriva la clave AES-256.
- AES-GCM, IV aleatorio por mensaje y etiqueta de autenticidad/integridad; Base64 para transportar el resultado.
- Código distinto o datos alterados: descarte sin ejecutar órdenes ni cerrar la app.
- Validación estricta del protocolo y dirección por rol.
- Permisos mínimos: INTERNET y ACCESS_NETWORK_STATE para TCP y detectar pérdida Wi-Fi; POST_NOTIFICATIONS se solicita solo desde Android 13. Negar notificaciones no impide operar.
- Estado seguro CERRADO, cancelar entrega y contador a 0 al perder el enlace.

El manifiesto declara `allowBackup="false"` y `usesCleartextTraffic="false"`. Este último atributo no cifra sockets TCP; la protección del contenido la aporta AES-GCM. Un código de seis dígitos tiene entropía limitada y no ofrece seguridad suficiente para producción. Se implementan medidas formativas básicas; no se afirma certificación ISO/IEC ni conformidad auditada.

## Pruebas

**P1–P7 PENDIENTES**. No se han probado en dos teléfonos. El arranque base en el Moto G35 corresponde a Hito 0 y no demuestra la conexión del Nodo con el Panel. Los [pasos exactos](docs/PRUEBAS_FORMATIVA.md) incluyen preparación, ventana fija y datos a registrar.

| ID | Caso | Resultado esperado | Resultado real | Evidencia |
|---|---|---|---|---|
| P1 | Conexión | Nodo y Panel muestran CONECTADO. | PENDIENTE | Por completar |
| P2 | Monitoreo | Panel muestra 2/3 en menos de 2 segundos. | PENDIENTE | Por completar |
| P3 | Alerta | Panel muestra 3/3 y una sola notificación «El gato está solicitando comida». | PENDIENTE | Por completar |
| P4 | Control | Nodo dispensa sin bloquear, notifica, cierra y confirma; Panel recibe ACK y contador vuelve a 0. | PENDIENTE | Por completar |
| P5 | Código diferente | Puede existir TCP; datos cifrados se descartan y no se ejecuta una orden válida. | PENDIENTE | Por completar |
| P6 | Pérdida de enlace | Aviso de pérdida, comedero CERRADO, entrega cancelada, contador a 0, app sin caída. | PENDIENTE | Por completar |
| P7 | Notificaciones denegadas | Conexión y control funcionan, sin notificaciones del sistema ni caída. | PENDIENTE | Por completar |

## Ejecutar en dos teléfonos

Seguir [INSTALACION.md](docs/INSTALACION.md) para compilar e instalar el APK actualizado en ambos teléfonos. Luego seguir la preparación [Nodo/Panel y P1–P7](docs/PRUEBAS_FORMATIVA.md). Las pruebas manuales todavía deben ejecutarse y registrarse.

## Plan, revisiones y entrega

- [PLAN_MVP.md](PLAN_MVP.md): solo Hitos 0–4; marca como completadas únicamente las verificaciones que el estudiante confirmó.
- [Hito 1: problemática, F1–F5 y herramientas](docs/HITO_1_DEFINICION.md): preparado y revisado.
- [Hito 2: medio, mensajes y diseño](docs/HITO_2_DISENO.md): preparado y revisado.
- [Git y entrega](docs/GIT_Y_ENTREGA.md): historial por hito y estado de publicación.
- [entrega_aai.txt](entrega_aai.txt): contiene el enlace real al repositorio; subir ese TXT al AAI no equivale a que la Formativa esté terminada ni a que se haya enviado.

El repositorio GitHub del proyecto es [Evaluación Formativa N°2 IoT](https://github.com/BLuMuun/evaluacion-formativa-n2-iot). Es privado: el profesor necesitará acceso autorizado para abrirlo. El historial conserva commits por hito. La actualización de Hitos 3–4 debe publicarse después de validar la compilación.

## Decisiones futuras de Sumativa

Por decisión del docente comunicada por el estudiante: **Firebase Authentication** para login y **Cloud Firestore** para almacenamiento de lecturas. Login, usuarios y almacenamiento pertenecen a la Sumativa. Aquí solo se documentan: no hay SDK Firebase, configuración, login ni base de datos.

## Uso de IA

Herramienta: **OpenAI Codex / ChatGPT**.

Se utilizó como apoyo para planificación, estructura del proyecto, generación inicial de código y revisión. El estudiante debe revisar, probar y corregir el código, y es responsable de poder explicarlo al profesor. No se atribuyen a la IA pruebas físicas ni resultados P1–P7 que aún no se han realizado.

## Fuentes y límites del alcance

Fuente principal: **Guía de desarrollo — Unidad 2 · App Android para IoT**, entregada por el estudiante: pp. 2–4 (alcance), 9–14 (Hitos 0–2 y revisiones), 15–26 (Hitos 3–4 y pruebas). **Desarrollo apoyado con IA** aporta planificación, decisiones explícitas y registro honesto; su enfoque de MVP Sumativa y pruebas con placas no amplía esta Formativa.

El estudiante compartió el comentario del profesor que aprueba la revisión previa a Hito 3. La ventana fija documentada es una regla específica del proyecto, definida por él. La compilación automática y las pruebas unitarias no sustituyen P1–P7 en teléfonos ni el instrumento de evaluación del AAI.
