# Pruebas — Unidad 2 Formativa

**Registro físico actualizado: 18 de octubre de 2026.** Se probó la app instalada en dos teléfonos reales: A = Moto G35, Android 15/API 35 (Nodo); B = HONOR RBN-NX3, Android 14/API 34 (Panel). Ambos estaban conectados a la misma red Wi-Fi local. No se guardan en este documento el SSID, las IP locales ni el código temporal.

### Login Formativo (retroalimentación posterior del profesor)

Se incorporó la pantalla de Login con usuario/correo y contraseña y la autenticación mediante Firebase Authentication Email/Password. El proyecto Android Firebase y `app/google-services.json` están configurados localmente; Email/Password está habilitado y la compilación con esa configuración y las siete pruebas unitarias pasaron. **Pendiente de validación:** crear una cuenta de prueba y probar acceso válido/inválido en dispositivo. Pasos en [FIREBASE_LOGIN.md](FIREBASE_LOGIN.md). P1–P7 de abajo se ejecutaron con el build IoT anterior al Login; deben repetirse después de probar el Login, que ahora protege la entrada a Conexión.

| Caso | Resultado esperado | Resultado real | Evidencia |
|---|---|---|---|
| L1 — Login con credenciales válidas | Firebase autentica al usuario y abre Conexión. | **PENDIENTE:** falta crear la cuenta de prueba. | Registrar después de probar en teléfono. |
| L2 — Credenciales inválidas | Mostrar error legible, permanecer en Login y no cerrar la app. | **PENDIENTE:** falta una cuenta configurada para probar el rechazo. | Registrar después de probar en teléfono. |
| L3 — Sesión y cierre | Una sesión válida abre Conexión al volver a abrir; Cerrar sesión retorna a Login. | **PENDIENTE:** requiere una cuenta Firebase de prueba. | Registrar después de probar en teléfono. |

No registrar ni subir contraseñas, tokens de sesión ni archivos de configuración.

P1–P7 se ejecutaron en los dos teléfonos. P3 y P4 se repitieron después de P7 con notificaciones habilitadas; las alertas se observaron y se guardaron capturas completas. Se corrigió la actualización del aviso de permiso al volver a la app y las capturas de interfaz de esta repetición ya reflejan el permiso habilitado. Las dos capturas nuevas de notificación fueron proporcionadas por el estudiante.

## Preparación futura de dos teléfonos

1. Instalar la misma app en A y B con los pasos de [INSTALACION.md](INSTALACION.md).
2. Conectar ambos a la misma red local sin aislamiento, o conectar uno al hotspot del otro. Mantener las apps visibles durante las pruebas.
3. En A elegir Nodo, escribir un código de seis dígitos y pulsar Iniciar Nodo. Anotar la IP mostrada.
4. En B elegir Panel, ingresar esa IP y el mismo código, y pulsar Conectar. El puerto será 5050.
5. Para P3/P4 permitir notificaciones en ambos; para P7 denegarlas desde el diálogo del sistema. Después de P7, restaurar el permiso antes de repetir P3/P4.
6. Para registrar cada caso, anotar modelos, versión Android, versión/commit de la app, red utilizada, fecha, resultado y ruta de captura/video. No publicar códigos, direcciones de red o información personal real sin revisar las evidencias.

## P1–P7

| ID | Caso | Pasos exactos | Resultado esperado | Resultado real | Evidencia |
|---|---|---|---|---|---|
| P1 | Conexión | A inicia Nodo. B usa IP de A y el mismo código. Pulsa Conectar. | Ambos muestran CONECTADO. | **PASÓ.** Ambos teléfonos mostraron CONECTADO; Panel inició en 0/3 y CERRADO. | [Nodo Moto G35](evidencia/P1-nodo-moto-g35.png), [Panel HONOR](evidencia/P1-panel-honor.png) |
| P2 | Monitoreo | Desde contador 0, pulsar Simular rasguño dos veces en A en menos de 5 s. Observar B antes de vencer la ventana. | B muestra 2/3 en menos de 2 s desde la segunda pulsación. | **PASÓ.** El Panel mostró 2/3 en la captura tomada aproximadamente 1,2 s después de la segunda pulsación; la lectura llegó dentro de la ventana. Tras esperar más de 5 s, el contador volvió a 0. | [Panel HONOR: 2/3](evidencia/P2-panel-honor.png) |
| P3 | Alerta | Desde 0, realizar tres pulsaciones en A antes de que pasen 5 s desde la primera. Luego pulsar dos veces más y observar. | B muestra 3/3 y una sola notificación «El gato está solicitando comida». Permanece 3/3; no dispensa ni renotifica por pulsaciones adicionales. | **PASÓ.** Panel mostró 3/3; dos pulsaciones adicionales no cambiaron el contador ni generaron otra alerta. En el HONOR se observó una sola notificación nueva de Comedero IoT con el texto esperado. | [Panel HONOR 3/3](evidencia/P3-panel-3de3-honor-nueva.png), [Nodo con solicitud](evidencia/P3-nodo-solicitud-moto-nueva.png), [Notificación completa HONOR](evidencia/P3-notificacion-solicitud-comida-honor.jpg) |
| P4 | Control | Con solicitud 3/3, pulsar Dispensar comida en B. Observar A durante la apertura y luego A y B. | A muestra DISPENSANDO y notifica «Comando de alimentación recibido»; vuelve a CERRADO. B recibe ACK, muestra «Última entrega: confirmada» y el contador vuelve a 0. UI sin bloqueo. | **PASÓ.** Nodo entró en DISPENSANDO, publicó la notificación esperada, regresó a CERRADO y reinició a 0/3. Panel recibió la confirmación del ACK. No se cerró ni congeló la app. | [Nodo DISPENSANDO](evidencia/P4-nodo-dispensando-moto-nueva.png), [Notificación completa Nodo](evidencia/P4-notificacion-comando-recibido-nodo.png), [Nodo CERRADO](evidencia/P4-nodo-cerrado-moto-nueva.png), [Panel confirmado](evidencia/P4-panel-confirmado-honor-nueva.png) |
| P5 | Código diferente | Desconectar. A usa 111111 y B 222222. Conectar y observar durante al menos 6 s; comprobar que no se procese una orden. | Puede abrirse TCP, pero las tramas cifradas no se interpretan; no hay lectura válida ni dispensación. | **PASÓ.** Se observó una conexión TCP breve, pero Panel no recibió lecturas interpretables y se desconectó al vencer el tiempo sin mensajes válidos. El botón de dispensación permaneció deshabilitado al no existir solicitud; no se procesó ninguna orden ni se dispensó. | [Panel tras rechazo](evidencia/P5-panel-rechaza-codigo-honor.png), [Nodo seguro](evidencia/P5-nodo-seguro-moto-g35.png) |
| P6 | Pérdida / estado seguro | Conectar con mismo código, alcanzar 3/3, iniciar entrega y detener la app B durante la apertura. | A queda CERRADO, cancela entrega, reinicia contador a 0 y muestra aviso de enlace perdido; la app no se cae. | **PASÓ para cierre del Panel.** Se detuvo el Panel primero con solicitud pendiente y luego durante DISPENSANDO. En ambas pérdidas, Nodo quedó en ESPERANDO CONEXIÓN, 0/3 y CERRADO, con aviso visible; no se observó excepción AndroidRuntime. No se probó apagar el Wi-Fi desde Ajustes. | [HONOR antes de la pérdida](evidencia/P6-panel-pendiente-honor.png), [Nodo DISPENSANDO](evidencia/P6-nodo-dispensando-moto-g35.png), [Nodo seguro](evidencia/P6-nodo-seguro-moto-g35.png) |
| P7 | Notificaciones denegadas | En Android 13+, denegar POST_NOTIFICATIONS desde el diálogo del sistema en A y B. Repetir conexión y control. | La conexión, lecturas y control siguen funcionando sin cierre inesperado; se muestra aviso visual de notificaciones deshabilitadas. | **PASÓ.** Se denegó el permiso en ambos teléfonos desde el diálogo del sistema. La conexión y el control continuaron funcionando, el Panel confirmó la entrega y ambas pantallas mostraron el aviso visual; no hubo cierre inesperado. Al terminar, se volvió a conceder para repetir P3/P4. | [Nodo durante P7](evidencia/P7-nodo-dispensando-moto-g35.png), [Nodo con control funcional](evidencia/P7-nodo-control-sin-notificaciones-moto-g35.png), [Panel con control funcional](evidencia/P7-panel-control-sin-notificaciones-honor.png) |

P7 adapta el caso de permiso Bluetooth del ejemplo de la guía a POST_NOTIFICATIONS, según el pedido del estudiante: Wi-Fi TCP no requiere Bluetooth ni un permiso de ubicación. Android 14 y 15 mostraron el diálogo de permiso; al denegarlo, conexión y control continuaron funcionando. En Android 8–12 no corresponde el diálogo POST_NOTIFICATIONS.

## Casos de borde de las reglas ya solicitadas

No son funciones adicionales ni pruebas de Sumativa.

| Caso | Procedimiento | Esperado | Resultado |
|---|---|---|---|
| Ventana incompleta | Pulsar 1 o 2 veces y esperar más de 5 s desde la primera. | Nodo y Panel vuelven a 0; un nuevo rasguño inicia nueva ventana. | **PASÓ:** después de dos rasguños, la ventana venció y ambos volvieron a 0. |
| Ventana fija, no móvil | Pulsar en t=0 s, t≈4 s y t≈6 s. | Se reinicia al vencer t=5 s; el rasguño de t≈6 s inicia contador 1, no completa 3. | **Prueba unitaria PASÓ** para el vencimiento fijo desde el primer rasguño. La secuencia cronometrada en teléfonos queda como comprobación opcional. |
| Solicitud retenida | Completar 3 antes de 5 s; esperar y pulsar varias veces. | Permanece 3, sin nuevas alertas ni dispensación automática. | **PASÓ en teléfono:** permaneció 3/3, no hubo dispensación automática y las pulsaciones adicionales no generaron alertas repetidas. |
| Segundo ciclo | Completar P4 y hacer tres nuevos rasguños. | Nueva solicitud y una nueva notificación, una sola por ciclo. | **Lógica de reinicio PASÓ en prueba unitaria.** La repetición de solicitud y notificación con dos teléfonos queda pendiente como prueba adicional. |
| Mensaje alterado | Alterar un byte del cifrado/etiqueta de autenticidad. | Se descarta sin acción ni cierre inesperado. | **Prueba unitaria PASÓ:** AES-GCM rechazó datos alterados y código distinto. No se inyectó una trama alterada por la red física. |
| Parseo estricto | Probar 2/4 campos, tipo/clave desconocidos, valor >16 caracteres, contador 4 y combinaciones no permitidas. | Se descartan; solo se aceptan combinaciones documentadas. | **Prueba unitaria PASÓ** con campos, tipos, claves y valores inválidos. No se inyectaron esas cadenas desde un teléfono. |

## Registro individual de evidencia

Copiar por caso: ID; fecha; versión/commit; teléfono A/Android; teléfono B/Android; tipo de red; pasos ejecutados; resultado observado; tiempo medido cuando aplique; captura/video; observación o error; corrección y repetición.

## Verificaciones separadas de P1–P7

La compilación, pruebas unitarias y controles estáticos se registran por separado en [VERIFICACION_BASE.md](VERIFICACION_BASE.md). P1–P7 se observaron en teléfonos reales. Las evidencias nuevas de P3/P4 se conservaron completas y sin recortar; las capturas anteriores permanecen en su formato original. Los casos de borde pendientes se mantienen separados de las pruebas obligatorias P1–P7.
