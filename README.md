# Comedero IoT — Unidad 2 Formativa

**Estado: implementación IoT de Hitos 0–4 verificada; pantalla y acceso Firebase del Login incorporados según la retroalimentación docente.** El proyecto Firebase y `google-services.json` todavía no están configurados, por lo que el inicio de sesión real y la repetición de pruebas en el APK con Login quedan pendientes. Las P1–P7 se realizaron en dos teléfonos con la versión IoT anterior a esta integración.

La app contiene Login con Firebase Authentication, Conexión, Nodo IoT y Panel de control, comunicación Wi-Fi TCP, cifrado AES-GCM, lógica de rasguños y entrega, y notificaciones locales. La guía considera Hitos 0–4 parte de la Formativa. El profesor aprobó la revisión previa a Hito 3 con el comentario «está perfecto Marcelo», según la captura compartida por el estudiante. La aprobación se registra sin incluir su captura de WhatsApp ni datos personales.

La versión IoT anterior se instaló en un Moto G35 (Nodo) y un HONOR RBN-NX3 (Panel), ambos conectados a la misma red Wi-Fi local. Se verificaron conexión, monitoreo, alertas locales, entrega/ACK, código de vinculación distinto, pérdida del Panel y operación con notificaciones denegadas. P3 y P4 se repitieron después de P7; las capturas completas entregadas por el estudiante están en el repositorio. Ver [verificación](docs/VERIFICACION_BASE.md), [resultados P1–P7 y Login](docs/PRUEBAS_FORMATIVA.md) y [capturas físicas](docs/evidencia/README.md).

La variante actual sin `google-services.json` compila y pasan las 7 pruebas unitarias existentes; permite revisar la pantalla de Login, pero muestra el aviso de configuración y no deja entrar a Conexión. Firebase real no está configurado. Las P1–P7 físicas registradas corresponden a la versión IoT anterior al Login; deben repetirse después de configurar Firebase.

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
| F1 | Simular rasguños, aplicar ventana fija de 5 s y comunicar cada cambio. | Implementado; lecturas físicas verificadas en P2/P3 |
| F2 | Monitorear X/3, enlace y comedero en Panel. | Implementado; P1/P2 pasaron |
| F3 | Orden DISPENSAR, entrega no bloqueante de unos 1,5 s, cierre y ACK. | Implementado y verificado en P4 |
| F4 | Notificación única de solicitud en Panel y de comando recibido en Nodo. | Implementado y observado con permiso concedido en P3/P4 |
| F5 | Ante pérdida: aviso, CERRADO, cancelar dispensación y reiniciar contador/solicitud. | Verificado al detener el Panel; falta probar apagando Wi-Fi desde Ajustes |

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

Login con usuario/correo y contraseña se muestra primero. Firebase Authentication valida las credenciales; al iniciar sesión, Conexión permite elegir Nodo/Panel, muestra Wi-Fi y pide código de seis dígitos. Nodo muestra su IP; Panel pide la IP del Nodo. Nodo y Panel muestran enlace, contador y comedero. El Login requiere crear un proyecto Firebase y colocar `app/google-services.json` localmente; hasta entonces se presenta el aviso de configuración y no se permite entrar.

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

Pruebas físicas realizadas en dos teléfonos. P1–P7 tienen resultado registrado; P6 se probó cerrando el Panel, sin apagar el Wi-Fi desde Ajustes. La tabla completa, el alcance y las limitaciones están en [docs/PRUEBAS_FORMATIVA.md](docs/PRUEBAS_FORMATIVA.md).

| ID | Caso | Resultado esperado | Resultado real | Evidencia |
|---|---|---|---|---|
| P1 | Conexión | Nodo y Panel muestran CONECTADO. | Pasó | [Capturas](docs/evidencia/README.md) |
| P2 | Monitoreo | Panel muestra 2/3 en menos de 2 segundos. | Pasó | [Panel 2/3](docs/evidencia/P2-panel-honor.png) |
| P3 | Alerta | Panel muestra 3/3 y una sola notificación. | Pasó; se observó una notificación | [Panel 3/3 y captura de alerta](docs/evidencia/README.md) |
| P4 | Control | DISPENSAR, cierre, ACK y contador 0. | Pasó; se observó la notificación del Nodo | [Capturas de entrega](docs/evidencia/README.md) |
| P5 | Código diferente | Datos cifrados se descartan. | Pasó; no se interpretaron lecturas ni se procesó una orden | [Capturas de P5](docs/evidencia/README.md) |
| P6 | Pérdida de enlace | Estado seguro y app sin caída. | Pasó al detener Panel durante solicitud y entrega | [Capturas P6](docs/evidencia/README.md) |
| P7 | Notificaciones denegadas | Conexión y control funcionan con permiso denegado. | Pasó; el permiso se negó en ambos teléfonos y conexión/control continuaron | [Capturas de P7](docs/evidencia/README.md) |

## Ejecutar en dos teléfonos

Para repetir las pruebas, seguir [INSTALACION.md](docs/INSTALACION.md) y las instrucciones de [Nodo/Panel y P1–P7](docs/PRUEBAS_FORMATIVA.md). P1–P7 están completos; algunos casos físicos adicionales de borde están identificados por separado.

## Plan, revisiones y entrega

- [PLAN_MVP.md](PLAN_MVP.md): solo Hitos 0–4; marca como completadas únicamente las verificaciones que el estudiante confirmó.
- [Hito 1: problemática, F1–F5 y herramientas](docs/HITO_1_DEFINICION.md): preparado y revisado.
- [Hito 2: medio, mensajes y diseño](docs/HITO_2_DISENO.md): preparado y revisado.
- [Git y entrega](docs/GIT_Y_ENTREGA.md): historial por hito y estado de publicación.
- [entrega_aai.txt](entrega_aai.txt): contiene el enlace real al repositorio; subir ese TXT al AAI no equivale a que la Formativa esté terminada ni a que se haya enviado.

El repositorio GitHub del proyecto es [Evaluación Formativa N°2 IoT](https://github.com/BLuMuun/evaluacion-formativa-n2-iot). Si permanece privado, el profesor necesitará acceso autorizado para abrirlo. El historial conserva commits por hito.

## Login Formativo y decisiones futuras de Sumativa

Por la retroalimentación del profesor, el Login con **Firebase Authentication (correo y contraseña)** se incorpora como excepción solicitada para la Formativa. El código está integrado, pero requiere un proyecto Firebase, habilitar el proveedor Email/Password, registrar al menos una cuenta y agregar `app/google-services.json` localmente. La guía paso a paso está en [docs/FIREBASE_LOGIN.md](docs/FIREBASE_LOGIN.md). No se sube ese archivo ni se incluyen contraseñas en GitHub. El inicio de sesión real y la regresión en teléfonos siguen pendientes hasta completar esa configuración.

**Cloud Firestore** para almacenar lecturas, el registro de usuarios dentro de la app, historial y las mejoras indicadas por el profesor permanecen para la Sumativa. No se implementan en esta versión. También quedan para Sumativa el latido en ambas direcciones, la protección anti-repetición con secuencia, un código/sal más robusto y la trazabilidad explícita de controles ISO/OT.

## Uso de IA

Herramienta: **OpenAI Codex / ChatGPT**.

Se utilizó como apoyo para planificación, estructura del proyecto, generación inicial de código y revisión. El estudiante debe revisar, probar y corregir el código, y es responsable de poder explicarlo al profesor. Los resultados físicos registrados corresponden a las observaciones realizadas en los teléfonos; las comprobaciones físicas adicionales pendientes se identifican como tales.

## Fuentes y límites del alcance

Fuente principal: **Guía de desarrollo — Unidad 2 · App Android para IoT**, entregada por el estudiante: pp. 2–4 (alcance), 9–14 (Hitos 0–2 y revisiones), 15–26 (Hitos 3–4 y pruebas). **Desarrollo apoyado con IA** aporta planificación, decisiones explícitas y registro honesto; su enfoque de MVP Sumativa y pruebas con placas no amplía esta Formativa.

El estudiante compartió el comentario del profesor que aprueba la revisión previa a Hito 3. La ventana fija documentada es una regla específica del proyecto, definida por él. La compilación automática y las pruebas unitarias no sustituyen P1–P7 en teléfonos ni el instrumento de evaluación del AAI.
