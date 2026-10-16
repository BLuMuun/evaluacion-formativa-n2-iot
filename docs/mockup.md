# Mockup — Hito 2 · Comedero IoT

**Diseño, no pantallas implementadas. Revisión docente PENDIENTE.**

Material 3, interfaz clara, títulos legibles, botones amplios y estados escritos en texto; no depender solo del color. [Versión SVG](mockup.svg).

## 1. Conexión

```text
Comedero IoT
Conexión

Rol: [Nodo] [Panel]
Medio: Wi-Fi

Código de vinculación:
[______]  (6 dígitos)

Si Nodo:
IP de este teléfono: xxx.xxx.xxx.xxx
[ INICIAR NODO ]

Si Panel:
IP del Nodo: [_______________]
[ CONECTAR ]

Estado / error en texto
```

Solo se mostrará el bloque del rol elegido. Código exactamente de seis dígitos; IP no vacía para Panel. Los errores no cerrarán la aplicación. Mostrar «Esperando conexión» o «Conectando» según corresponda. TCP conectado con código distinto no garantiza que los datos puedan descifrarse.

## 2. Nodo IoT

```text
Nodo IoT
Enlace: CONECTADO

Rasguños:
2 / 3

[ SIMULAR RASGUÑO ]

Comedero:
CERRADO

[ DESCONECTAR ]
```

El primer rasguño abre una ventana fija de 5 s. En 3/3 se ignorarán nuevas pulsaciones mientras haya solicitud pendiente. No se abrirá automáticamente. Al recibir el comando: DISPENSANDO durante aproximadamente 1,5 s; luego CERRADO, ACK y contador 0. Si se pierde el enlace: cancelar, cerrar y reiniciar.

## 3. Panel de control

```text
Panel de control
Enlace: CONECTADO

Rasguños:
2 / 3

Comedero:
CERRADO

[ DISPENSAR COMIDA ]

Última entrega:
—

[ DESCONECTAR ]
```

Recibir 3 después de menos de 3 genera una sola notificación «El gato está solicitando comida». Un ACK válido muestra «Última entrega: confirmada». No se debe mostrar una confirmación solo por pulsar el botón.

## Navegación y avisos

Conexión → Nodo o Panel → Desconectar → Conexión. No hay login ni historial. Los avisos incluyen código inválido, IP faltante, error de conexión, enlace perdido y notificaciones deshabilitadas. Negar notificaciones no impedirá conexión/control.

El SVG representa las tres pantallas y muestra los bloques condicionales de Conexión como alternativas de diseño. El texto xxx.xxx.xxx.xxx es un marcador de IP, no un valor real.
