# Comedero IoT — Unidad 2 Formativa

**Estado: base del Hito 0 compilada y abierta en Moto G35 (confirmado por el estudiante); contenido de Hitos 0–2 publicado en el repositorio GitHub privado; revisión docente PENDIENTE.**

La app actual solo muestra una bienvenida en Kotlin + Compose + Material 3. Hitos 3 y 4 **no implementados**, por instrucción del estudiante de respetar la revisión docente previa. No hay login, Firebase, base de datos, historial, MQTT, HiveMQ ni FCM. Las funcionalidades descritas a continuación son el diseño para la Formativa, no capacidades ya verificadas. Según la guía, Hitos 0–4 forman parte de la Formativa; este repositorio se prepara para revisión de Hitos 1 y 2 y no es todavía el prototipo funcional final.

No se declara completa la Formativa. El Hito 0 fue verificado parcialmente: la app base compiló y abrió en el Moto G35, según confirmó el estudiante. La captura de la bienvenida en Moto G35 está en [docs/evidencia_hito0_moto_g35.png](docs/evidencia_hito0_moto_g35.png); la versión Android no quedó registrada. P1–P7 y revisión docente siguen pendientes. Ver [verificación de la base](docs/VERIFICACION_BASE.md).

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

Las herramientas previstas para Hitos 3–4 son sockets nativos `java.net`, corrutinas, `javax.crypto` y NotificationCompat. No están implementadas en la bienvenida base.

## Medio inalámbrico

Wi-Fi local mediante TCP, puerto **5050**. El Nodo será servidor y el Panel cliente.

- Distancia: cama y comedero pueden estar en habitaciones distintas.
- Red: ambos teléfonos compartirán un router o hotspot, sin broker externo.
- Estabilidad: TCP entrega bytes ordenados; deben comprobarse señal y ausencia de aislamiento entre clientes.
- Datos: las lecturas y órdenes son mensajes pequeños.
- Energía: Wi-Fi consume más que Bluetooth; se priorizan alcance y disponibilidad para esta simulación con teléfonos.

No se implementará un segundo medio inalámbrico.

## Funcionalidades de la Formativa

| ID | Funcionalidad prevista | Estado |
|---|---|---|
| F1 | Simular rasguños, aplicar ventana fija de 5 s y comunicar cada cambio. | Hito 3 pendiente |
| F2 | Monitorear X/3, enlace y comedero en Panel. | Hito 3 pendiente |
| F3 | Orden DISPENSAR, entrega no bloqueante de unos 1,5 s, cierre y ACK. | Hito 3 pendiente |
| F4 | Notificación única de solicitud en Panel y de comando recibido en Nodo. | Hito 3 pendiente |
| F5 | Ante pérdida: aviso, CERRADO, cancelar dispensación y reiniciar contador/solicitud. | Hito 3 pendiente |

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

LECTURA y ACK viajan del Nodo al Panel; CMD del Panel al Nodo. Se descartarán tipos, claves, combinaciones, valores o cantidades de campos no permitidos. El diseño usa el límite de 16 caracteres para clave/valor del ejemplo de la guía, junto con una lista estricta de valores válidos. [Diseño del protocolo](docs/HITO_2_DISENO.md).

## Mockup

[Mockup textual y navegación](docs/mockup.md) · [Mockup SVG](docs/mockup.svg).

Conexión permite elegir Nodo/Panel, muestra Wi-Fi y pide código de seis dígitos. Nodo muestra su IP; Panel pide la IP del Nodo. Nodo y Panel muestran enlace, contador y comedero. No hay pantalla de login.

## Seguridad

Diseñada para implementar y verificar después de la revisión docente:

- Código de seis dígitos del que se derivará una clave AES-256.
- AES-GCM, IV aleatorio por mensaje y etiqueta de autenticidad/integridad; Base64 para transportar el resultado.
- Código distinto o datos alterados: descarte sin ejecutar órdenes ni cerrar la app.
- Validación estricta del protocolo y dirección por rol.
- Permisos mínimos en Hito 3: INTERNET y POST_NOTIFICATIONS; pedir este último solo desde Android 13. Negarlo no impedirá operar.
- Estado seguro CERRADO, cancelar entrega y contador a 0 al perder el enlace.

La base ya declara `allowBackup="false"` y `usesCleartextTraffic="false"`; todavía no pide permisos porque no utiliza red ni notificaciones. `usesCleartextTraffic` no cifra sockets TCP: esa protección deberá aportarla AES-GCM. Un código de seis dígitos no ofrece seguridad suficiente para producción; no se afirma certificación ISO ni cifrado implementado en esta versión.

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

Seguir [INSTALACION.md](docs/INSTALACION.md): abrir esta carpeta y ejecutar en cada teléfono mediante USB. El arranque base ya se confirmó en un Moto G35. En el segundo teléfono se debe repetir la prueba. Ambos mostrarán **solo la bienvenida del Hito 0**; aún no hay elección de roles ni comunicación.

Tras aprobación docente e implementación de Hitos 3–4, reinstalar la misma app en ambos y ejecutar la preparación [Nodo/Panel y P1–P7](docs/PRUEBAS_FORMATIVA.md).

## Plan, revisiones y entrega

- [PLAN_MVP.md](PLAN_MVP.md): solo Hitos 0–4; marca como completadas únicamente las verificaciones que el estudiante confirmó.
- [Hito 1: problemática, F1–F5 y herramientas](docs/HITO_1_DEFINICION.md): revisión docente PENDIENTE.
- [Hito 2: medio, mensajes y diseño](docs/HITO_2_DISENO.md): revisión docente PENDIENTE.
- [Git y entrega](docs/GIT_Y_ENTREGA.md): historial por hito y estado de publicación.
- [entrega_aai.txt](entrega_aai.txt): contiene el enlace real al repositorio; subir ese TXT al AAI no equivale a que la Formativa esté terminada ni a que se haya enviado.

El repositorio GitHub del proyecto es [Evaluación Formativa N°2 IoT](https://github.com/BLuMuun/evaluacion-formativa-n2-iot). Es privado: el profesor necesitará acceso autorizado para abrirlo. El contenido se organiza en commits separados para los Hitos 0, 1 y 2. No se ha entregado nada al AAI.

## Decisiones futuras de Sumativa

Por decisión del docente comunicada por el estudiante: **Firebase Authentication** para login y **Cloud Firestore** para almacenamiento de lecturas. Login, usuarios y almacenamiento pertenecen a la Sumativa. Aquí solo se documentan: no hay SDK Firebase, configuración, login ni base de datos.

## Uso de IA

Herramienta: **OpenAI Codex / ChatGPT**.

Se utilizó como apoyo para planificación, estructura del proyecto, generación inicial de código y revisión. El código generado en esta etapa corresponde a la base del Hito 0; la lógica IoT está documentada, no implementada. El estudiante revisa, prueba y corrige el código, y es responsable de poder explicarlo al profesor. No se atribuyen a la IA pruebas físicas ni revisiones docentes que no ocurrieron.

## Fuentes y límites del alcance

Fuente principal: **Guía de desarrollo — Unidad 2 · App Android para IoT**, entregada por el estudiante: pp. 2–4 (alcance), 9–14 (Hitos 0–2 y revisiones), 15–26 (Hitos 3–4 y pruebas). **Desarrollo apoyado con IA** aporta planificación, decisiones explícitas y registro honesto; su enfoque de MVP Sumativa y pruebas con placas no amplía esta Formativa.

La revisión del Paso 1 y la revisión de medio/mensajes/mockup antes de programar siguen pendientes. El estudiante autorizó preparar Hitos 0–2 y detenerse antes de Hito 3. La ventana fija documentada es una regla de este proyecto, definida por él. Esta preparación no sustituye el instrumento de evaluación del AAI ni constituye una Formativa funcional terminada.
