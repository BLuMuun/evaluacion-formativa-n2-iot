# Pruebas — Unidad 2 Formativa

**Registro físico actualizado: 18 de octubre de 2026.** Se probó la app instalada en dos teléfonos reales: A = Moto G35, Android 15/API 35 (Nodo); B = HONOR RBN-NX3, Android 14/API 34 (Panel). Ambos estaban conectados a la misma red Wi-Fi local. No se guardan en este documento el SSID, las IP locales ni el código temporal.

Android informa POST_NOTIFICATIONS: granted=false en ambos equipos. Por eso se verificaron los estados y el control, pero las notificaciones del sistema de P3 y P4 no pudieron comprobarse. P1, P2 y la parte funcional de P3, P4, P6 y P7 se probaron en los teléfonos. P5 queda pendiente de repetición controlada; no se declara completada.

## Preparación futura de dos teléfonos

1. Instalar la misma app en A y B con los pasos de [INSTALACION.md](INSTALACION.md).
2. Conectar ambos a la misma red local sin aislamiento, o conectar uno al hotspot del otro. Mantener las apps visibles durante las pruebas.
3. En A elegir Nodo, escribir un código de seis dígitos y pulsar Iniciar Nodo. Anotar la IP mostrada.
4. En B elegir Panel, ingresar esa IP y el mismo código, y pulsar Conectar. El puerto será 5050.
5. Permitir notificaciones en ambos para P3/P4; si ya se habían denegado, habilitarlas en Ajustes → Aplicaciones → Comedero IoT → Notificaciones.
6. Para registrar cada caso, anotar modelos, versión Android, versión/commit de la app, red utilizada, fecha, resultado y ruta de captura/video. No publicar códigos, direcciones de red o información personal real sin revisar las evidencias.

## P1–P7

| ID | Caso | Pasos exactos | Resultado esperado | Resultado real | Evidencia |
|---|---|---|---|---|---|
| P1 | Conexión | A inicia Nodo. B usa IP de A y el mismo código. Pulsa Conectar. | Ambos muestran CONECTADO. | **PASÓ.** Ambos teléfonos mostraron CONECTADO; Panel inició en 0/3 y CERRADO. | [Nodo Moto G35](evidencia/P1-nodo-moto-g35.png), [Panel HONOR](evidencia/P1-panel-honor.png) |
| P2 | Monitoreo | Desde contador 0, pulsar Simular rasguño dos veces en A en menos de 5 s. Observar B antes de vencer la ventana. | B muestra 2/3 en menos de 2 s desde la segunda pulsación. | **PASÓ.** El Panel mostró 2/3 en la captura tomada aproximadamente 1,2 s después de la segunda pulsación; la lectura llegó dentro de la ventana. Tras esperar más de 5 s, el contador volvió a 0. | [Panel HONOR: 2/3](evidencia/P2-panel-honor.png) |
| P3 | Alerta | Desde 0, realizar tres pulsaciones en A antes de que pasen 5 s desde la primera. Esperar más de 5 s y pulsar otra vez. | B muestra 3/3 y una sola notificación «El gato está solicitando comida». Permanece 3/3 y no dispensa ni renotifica por pulsaciones adicionales. | **PARCIAL.** Panel llegó a 3/3 y el Nodo mantuvo la solicitud sin dispensar automáticamente; rasguños adicionales se ignoraron. No se pudo verificar la notificación única porque el permiso estaba denegado. | [Panel HONOR: 3/3](evidencia/P3-panel-honor.png) |
| P4 | Control | Con solicitud 3/3, pulsar Dispensar comida en B. Observar A durante unos 1,5 s y luego B. | A muestra DISPENSANDO y notifica «Comando de alimentación recibido»; vuelve a CERRADO. B recibe ACK, muestra «Última entrega: confirmada» y el contador vuelve a 0. UI sin bloqueo. | **PARCIAL.** Nodo mostró DISPENSANDO y volvió a CERRADO; Panel mostró entrega confirmada y 0/3. No se pudo verificar la notificación local del Nodo por el permiso denegado. No hubo cierre de la app. | [Nodo abriendo](evidencia/P4-nodo-dispensando-moto-g35.png), [Nodo cerrado](evidencia/P4-nodo-cerrado-moto-g35.png) |
| P5 | Código diferente | Desconectar. A usa 111111 y B 222222. Conectar y observar durante 7 s. | Al inicio puede existir TCP, pero las tramas cifradas se descartan; al no recibir lecturas válidas, B cierra a los 6 s. No se interpreta una orden ni se dispensa. | **PENDIENTE.** Se observó una conexión TCP breve y posterior pérdida durante un intento con códigos distintos, pero falta repetirlo y guardar evidencia con las pantallas identificadas correctamente. | Por completar |
| P6 | Pérdida / estado seguro | Conectar con mismo código, alcanzar 3/3, iniciar entrega y detener la app B durante la apertura. | A queda CERRADO, cancela entrega, reinicia contador a 0 y muestra aviso de enlace perdido; la app no se cae. | **PASÓ para cierre del Panel.** Se detuvo el Panel primero con solicitud pendiente y luego durante DISPENSANDO. En ambas pérdidas, Nodo quedó en ESPERANDO CONEXIÓN, 0/3 y CERRADO, con aviso visible; no se observó excepción AndroidRuntime. No se probó apagar el Wi-Fi desde Ajustes. | [HONOR antes de la pérdida](evidencia/P6-panel-pendiente-honor.png), [Nodo DISPENSANDO](evidencia/P6-nodo-dispensando-moto-g35.png), [Nodo seguro](evidencia/P6-nodo-seguro-moto-g35.png) |
| P7 | Notificaciones denegadas | En Android 13+, negar POST_NOTIFICATIONS en A y B. Repetir conexión y control. | La conexión, lecturas y control siguen funcionando sin cierre inesperado; se muestra aviso visual de notificaciones deshabilitadas. | **PARCIAL.** POST_NOTIFICATIONS figura como no concedido en ambos teléfonos. P1–P4 mantuvieron conexión y control; la app mostró el aviso. El diálogo de solicitud/denegación no se volvió a ejecutar en esta sesión. | [Panel HONOR con aviso](evidencia/P1-panel-honor.png), [Nodo con aviso](evidencia/P1-nodo-moto-g35.png) |

P7 adapta el caso de permiso Bluetooth del ejemplo de la guía a POST_NOTIFICATIONS, según el pedido del estudiante: Wi-Fi TCP no requiere Bluetooth ni un permiso de ubicación. En estas pruebas, Android 14 y 15 informaron el permiso como no concedido; conexión y control continuaron funcionando. El diálogo no se volvió a lanzar. En Android 8–12 no corresponde el diálogo POST_NOTIFICATIONS.

## Casos de borde de las reglas ya solicitadas

No son funciones adicionales ni pruebas de Sumativa.

| Caso | Procedimiento | Esperado | Resultado |
|---|---|---|---|
| Ventana incompleta | Pulsar 1 o 2 veces y esperar más de 5 s desde la primera. | Nodo y Panel vuelven a 0; un nuevo rasguño inicia nueva ventana. | **PASÓ:** después de dos rasguños, la ventana venció y ambos volvieron a 0. |
| Ventana fija, no móvil | Pulsar en t=0 s, t≈4 s y t≈6 s. | Se reinicia al vencer t=5 s; el rasguño de t≈6 s inicia contador 1, no completa 3. | PENDIENTE |
| Solicitud retenida | Completar 3 antes de 5 s; esperar y pulsar varias veces. | Permanece 3, sin nuevas alertas ni dispensación automática. | **PARCIAL:** permaneció 3/3 y no hubo dispensación automática; no fue posible observar las notificaciones. |
| Segundo ciclo | Completar P4 y hacer tres nuevos rasguños. | Nueva solicitud y una nueva notificación, una sola por ciclo. | PENDIENTE |
| Mensaje alterado | En las pruebas de Hito 4 alterar un byte cifrado/etiqueta. | Se descarta sin acción ni cierre inesperado. | PENDIENTE |
| Parseo estricto | Probar 2/4 campos, tipo/clave desconocidos, valor >16 caracteres, contador 4 y combinaciones no permitidas. | Se descartan; solo se aceptan combinaciones documentadas. | PENDIENTE |

## Registro individual de evidencia

Copiar por caso: ID; fecha; versión/commit; teléfono A/Android; teléfono B/Android; tipo de red; pasos ejecutados; resultado observado; tiempo medido cuando aplique; captura/video; observación o error; corrección y repetición.

## Verificaciones separadas de P1–P7

La compilación, pruebas unitarias y controles estáticos se registran por separado en [VERIFICACION_BASE.md](VERIFICACION_BASE.md). La conexión física P1, el monitoreo P2 y la pérdida segura P6 se observaron en teléfonos reales; las filas parciales y pendientes indican lo que todavía falta y no se consideran aprobadas.
