# Hito 1 — Definición para revisión docente

Estado: DOCUMENTACIÓN PREPARADA. Revisión docente: **PENDIENTE**.
Evaluación: únicamente Formativa, Unidad 2, TI3042, INACAP. Trabajo individual.

## Problemática

En el hogar, un gato rasguña la cama cuando quiere alimento, provocando agujeros y deterioro. Actualmente no existe una forma automática de interpretar este comportamiento y gestionar una solicitud de alimento. El proyecto busca representar este sistema mediante una aplicación móvil que simule la detección de rasguños y el control del comedero.

## Solución y relación con Unidad 1

Una sola aplicación se instalará en dos teléfonos Android. El teléfono A, Nodo IoT, simulará el sensor de rasguños, la solicitud y la compuerta. El teléfono B, Panel de control, mostrará las lecturas y permitirá al usuario decidir cuándo dispensar. Llegar a tres rasguños genera una solicitud, nunca una entrega automática.

En Unidad 1: sensor/botón → ESP32 de la cama → Wi-Fi/MQTT y HiveMQ Cloud → ESP32 del comedero → servo.

En esta Formativa: teléfono Nodo ↔ Wi-Fi local TCP ↔ teléfono Panel. La simulación del sensor y del actuador reside en el Nodo. No se conectarán las placas reales y no se utilizarán MQTT ni HiveMQ.

## Funcionalidades F1–F5

| ID | Funcionalidad adaptada | Quién | Verificación futura |
|---|---|---|---|
| F1 | Simular rasguños y comunicar cada cambio; ventana fija de 5 s para completar 3. | Nodo | P2, P3 y casos de ventana |
| F2 | Mostrar conexión, contador X/3 y estado del comedero. | Panel | P1, P2, P4 |
| F3 | Enviar DISPENSAR; simular apertura 1,5 s sin bloquear; cerrar, confirmar con ACK y reiniciar. | Panel → Nodo → Panel | P4 |
| F4 | Notificación única al pasar a 3 en Panel y notificación al recibir el comando en Nodo. | Ambos | P3, P4, P7 |
| F5 | Ante pérdida del enlace: aviso en ambos, compuerta CERRADO, cancelar dispensación y reiniciar solicitud/contador. | Ambos | P6 |

Todas corresponden a la Formativa. F6 (login) y F7 (almacenamiento/historial) del ejemplo de la guía no se implementarán.

## Herramientas justificadas

| Herramienta | Motivo |
|---|---|
| Android Studio | IDE solicitado; integra proyecto Android, SDK, compilación, dispositivos y Logcat. |
| Kotlin | Lenguaje solicitado; facilita el manejo de nulos y las tareas no bloqueantes con corrutinas. |
| Jetpack Compose | Interfaz declarativa que muestra el estado y se actualiza al cambiarlo, con menos código de vistas. |
| Material 3 | Componentes y tipografía coherentes con el diseño Android; interfaz simple y legible. |
| Gradle con Kotlin DSL | Construcción reproducible con configuración escrita en Kotlin. |
| java.net | Sockets TCP nativos; no requiere broker ni biblioteca externa de conectividad. |
| javax.crypto / APIs Android | AES-GCM y derivación de claves sin bibliotecas criptográficas externas. Implementación posterior. |
| NotificationCompat | Notificaciones locales al recibir eventos, sin servidor ni FCM. Implementación posterior. |
| Git / GitHub | Versiones por hito y repositorio para revisión y entrega mediante enlace. |

No hay credenciales reales ni datos personales. Las versiones fijadas de las dependencias del proyecto base figuran en los archivos Gradle; no se usan versiones dinámicas.

## Revisión con el docente

Debe presentarse este documento antes de continuar con la validación del diseño. La preparación anticipada del Hito 2 fue autorizada por el estudiante; no sustituye esta revisión.

| Registro | Valor |
|---|---|
| Estado | PENDIENTE |
| Fecha | Por completar |
| Retroalimentación | Por completar |
| Ajustes solicitados | Por completar |
| Evidencia de revisión / aprobación | Por completar |

Fuente principal: *Guía de desarrollo — Unidad 2 · App Android para IoT*, pp. 11–12.
