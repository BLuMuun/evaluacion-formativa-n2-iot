# Pruebas — Unidad 2 Formativa

**Todas PENDIENTES.** Hitos 3–4 no implementados: estos casos son el plan de pruebas para después de la aprobación docente. No hay capturas, teléfonos probados ni resultados de interconexión.

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
| P1 | Conexión | A inicia Nodo. B usa IP de A y el mismo código. Pulsa Conectar. | Ambos muestran CONECTADO. | PENDIENTE | Por completar |
| P2 | Monitoreo | Desde contador 0, pulsar Simular rasguño dos veces en A en menos de 5 s. Observar B antes de vencer la ventana. | B muestra 2/3 en menos de 2 s desde la segunda pulsación. | PENDIENTE | Por completar |
| P3 | Alerta | Desde 0, realizar tres pulsaciones en A antes de que pasen 5 s desde la primera. Esperar más de 5 s y pulsar otra vez. | B muestra 3/3 y una sola notificación «El gato está solicitando comida». Permanece 3/3 y no dispensa ni renotifica por pulsaciones adicionales. | PENDIENTE | Por completar |
| P4 | Control | Con solicitud 3/3, pulsar Dispensar comida en B. Observar A durante unos 1,5 s y luego B. | A muestra DISPENSANDO y notifica «Comando de alimentación recibido»; vuelve a CERRADO. B recibe ACK, muestra «Última entrega: confirmada» y el contador vuelve a 0. UI sin bloqueo. | PENDIENTE | Por completar |
| P5 | Código diferente | Desconectar. A usa 111111 y B 222222. Conectar TCP; simular rasguños y enviar DISPENSAR desde B. | Puede existir TCP conectado; no se interpretan lecturas ni órdenes cifradas. A no dispensa ni notifica comando válido. | PENDIENTE | Por completar |
| P6 | Pérdida / estado seguro | Conectar con mismo código. Primero alcanzar 3/3 y cortar Wi-Fi. Reconectar. Luego iniciar entrega y cortar Wi-Fi durante los 1,5 s. Repetir forzando detención de B desde Ajustes mientras A está conectado. | Ambos detectan pérdida cuando pueden ejecutarse, avisan visualmente/notifican si permitido; A queda CERRADO, cancela entrega y reinicia a 0. Sin caída ni ACK de entrega interrumpida. Al reconectar permanece seguro. Medir el tiempo real de detección. | PENDIENTE | Por completar |
| P7 | Notificaciones denegadas | En Android 13+, negar POST_NOTIFICATIONS en A y B (instalación limpia o revocar en Ajustes). Repetir P1, P3 y P4. | La conexión, lecturas y control siguen funcionando, no hay cierre inesperado; no aparece notificación del sistema. Puede mostrarse aviso visual de notificaciones deshabilitadas. | PENDIENTE | Por completar |

P7 adapta el caso de permiso Bluetooth del ejemplo de la guía a POST_NOTIFICATIONS, según el pedido del estudiante: Wi-Fi TCP no requiere Bluetooth ni un permiso de ubicación. En Android 8–12 no corresponde el diálogo POST_NOTIFICATIONS; registrar «no aplica por versión» para ese diálogo, sin declarar probado el caso Android 13+.

## Casos de borde de las reglas ya solicitadas

No son funciones adicionales ni pruebas de Sumativa.

| Caso | Procedimiento | Esperado | Resultado |
|---|---|---|---|
| Ventana incompleta | Pulsar 1 o 2 veces y esperar más de 5 s desde la primera. | Nodo y Panel vuelven a 0; un nuevo rasguño inicia nueva ventana. | PENDIENTE |
| Ventana fija, no móvil | Pulsar en t=0 s, t≈4 s y t≈6 s. | Se reinicia al vencer t=5 s; el rasguño de t≈6 s inicia contador 1, no completa 3. | PENDIENTE |
| Solicitud retenida | Completar 3 antes de 5 s; esperar y pulsar varias veces. | Permanece 3, sin nuevas alertas ni dispensación automática. | PENDIENTE |
| Segundo ciclo | Completar P4 y hacer tres nuevos rasguños. | Nueva solicitud y una nueva notificación, una sola por ciclo. | PENDIENTE |
| Mensaje alterado | En las pruebas de Hito 4 alterar un byte cifrado/etiqueta. | Se descarta sin acción ni cierre inesperado. | PENDIENTE |
| Parseo estricto | Probar 2/4 campos, tipo/clave desconocidos, valor >16 caracteres, contador 4 y combinaciones no permitidas. | Se descartan; solo se aceptan combinaciones documentadas. | PENDIENTE |

## Registro individual de evidencia

Copiar por caso: ID; fecha; versión/commit; teléfono A/Android; teléfono B/Android; tipo de red; pasos ejecutados; resultado observado; tiempo medido cuando aplique; captura/video; observación o error; corrección y repetición.

## Verificaciones actuales del proyecto base

La compilación y los controles estáticos se registran por separado en [VERIFICACION_BASE.md](VERIFICACION_BASE.md). No reemplazan P1–P7 ni la prueba de arranque en teléfono. No existen casos aprobados de dos teléfonos en esta entrega de preparación.
