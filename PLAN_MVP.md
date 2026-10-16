# PLAN_MVP — Comedero IoT · Unidad 2 Formativa

Estado: Hitos 0–2 preparados y publicados para revisión. Repositorio GitHub privado: https://github.com/BLuMuun/evaluacion-formativa-n2-iot. **DETENER antes del Hito 3 hasta obtener revisión docente.**
P1–P7 no tienen pruebas físicas registradas y la aprobación docente sigue pendiente. Las casillas de Hito 0 que se comprobaron en el Moto G35 aparecen marcadas; las demás siguen pendientes.

## 1. Resumen y alcance

El gato rasguña la cama para pedir comida y la deteriora. Una app en dos teléfonos representará un Nodo (sensor y compuerta) y un Panel (monitoreo y decisión de entrega). Solo Hitos 0–4 de la Formativa. El alcance vigente permite crear la base y preparar el diseño, pero prohíbe implementar Hitos 3–4 antes de la revisión del profesor.

## 2. Declaración técnica

| Decisión del estudiante | Valor |
|---|---|
| Lenguaje / IDE | Kotlin / Android Studio |
| UI | Jetpack Compose + Material 3 |
| Gradle / mínimo | Kotlin DSL / minSdk 26 |
| Package | cl.inacap.comederoiot |
| Comunicación | Wi-Fi local TCP, puerto 5050; Nodo servidor, Panel cliente |
| Hardware actual | Dos teléfonos; sin depender de ESP32 |
| Protocolo | TIPO;CLAVE;VALOR; LECTURA, CMD y ACK |
| Notificaciones | Locales, NotificationCompat; sin FCM |
| Seguridad futura de Formativa | Código de 6 dígitos → AES-256-GCM, IV aleatorio, descarte y validación; javax.crypto |
| Permisos de Hito 3 | INTERNET y POST_NOTIFICATIONS únicamente; este último en ejecución desde API 33 |
| Estado seguro | CERRADO, dispensación cancelada, contador/solicitud a 0 |
| Rasguños | Ventana FIJA de 5 s; 3 confirma; ignorar extras; mantener 3 hasta entrega correcta, salvo desconexión |
| Sumativa futura | Firebase Authentication + Cloud Firestore; solo documentados |

Base de construcción fijada: AGP 8.13.2, Gradle 8.13, Kotlin/Compose compiler 2.2.21, Compose BOM 2025.12.00, Activity Compose 1.12.2, compileSdk/targetSdk 36, bytecode Java/Kotlin 17. API 36 sigue el ejemplo de la guía de desarrollo. Esto no cambia minSdk 26.

## 3. Arquitectura y protocolo

Ver [diseño](docs/HITO_2_DISENO.md): responsabilidades de cada archivo, mensajes, validación y reglas completas de ventana fija. Solo MainActivity contiene código de app en esta preparación. Los paquetes enlace, seguridad y ui están reservados.

## 4. Hitos y criterios de aceptación

### Hito 0 — Proyecto base (2.1.1)

Android Studio/SDK, sincronización, compilación, arranque base en un teléfono físico, commit y publicación confirmados. Los datos de compilación y arranque se limitan a lo que informó el estudiante; ver [verificación](docs/VERIFICACION_BASE.md).

- [x] Instalar Android Studio y Android SDK 36. Aceptación: el IDE abre el proyecto y localiza el SDK.
- [x] Sincronizar Gradle y compilar la base. Aceptación: build de depuración completado e instalado al ejecutar app.
- [x] Ejecutar en teléfono real (Moto G35). Aceptación: app base abierta sin cierre inesperado, según confirmación del estudiante el 14-10-2026. Captura y versión Android no registradas.
- [x] Configurar autor Git y registrar el commit del hito. Aceptación: aparece en git log con la identidad GitHub configurada.
- [x] Crear/publicar repositorio GitHub. Aceptación: el estudiante comprueba el código en el enlace real.

### Hito 1 — Definición (2.1.1)

Documento preparado: [HITO_1_DEFINICION.md](docs/HITO_1_DEFINICION.md).

- [ ] Revisar problemática y adaptación de Unidad 1. Aceptación: el estudiante explica qué simula cada teléfono.
- [ ] Revisar F1–F5 y herramientas. Aceptación: distingue Formativa de Sumativa y justifica cada herramienta.
- [ ] Revisión docente del Paso 1. Aceptación: registrar fecha, observaciones y evidencia reales. **PENDIENTE**.
- [x] Registrar commit del material del Hito 1. Aceptación: historial real; no equivale a visto bueno docente.

### Hito 2 — Diseño (2.1.1, 2.1.2)

Diseño y mockup preparados anticipadamente por autorización del estudiante.

- [ ] Revisar Wi-Fi por distancia, energía y estabilidad. Aceptación: explicar red local/hotspot y puerto 5050.
- [ ] Revisar tabla TIPO;CLAVE;VALOR. Aceptación: explicar un ciclo lectura → comando → ACK y rechazos.
- [ ] Revisar las tres pantallas y navegación. Aceptación: se identifican rol, código, IP, monitoreo y control.
- [ ] Validar con el estudiante las reglas documentadas de ventana fija. Aceptación: no se introduce ventana móvil.
- [ ] Revisión docente del Paso 2. Aceptación: registrar fecha, observaciones y aprobación antes de programar. **PENDIENTE**.
- [x] Registrar commit del material del Hito 2. Aceptación: historial real del diseño.

### Hito 3 — Prototipo conectado (2.1.2, 2.1.4)

**NO INICIADO. Bloqueado por revisión docente pendiente.**

- [ ] Confirmar las revisiones docentes y resolver cualquier decisión indispensable de implementación.
- [ ] Primera comprobación técnica de conectividad entre dos teléfonos (POC acotada). Aceptación: intercambio por TCP 5050 en red local documentado; no cambiar de tecnología sin consultar.
- [ ] Implementar Conexión, Nodo y Panel. Aceptación: P1 y P2 en teléfonos reales.
- [ ] Implementar contador, ventana fija y notificaciones. Aceptación: P3 y casos de ventana, sin repetición en 3/3.
- [ ] Implementar DISPENSAR, cierre y ACK sin bloquear. Aceptación: P4 y operación de UI durante los 1,5 s.
- [ ] Implementar detección de pérdida y estado seguro. Aceptación: P6, incluso durante entrega y con solicitud pendiente.
- [ ] Pedir solo permisos necesarios y tolerar rechazo. Aceptación: P7.
- [ ] Revisar y explicar el código; probar en teléfonos antes de avanzar. Aceptación: evidencia real y commit del hito.

### Hito 4 — Seguridad, pruebas y preentrega (2.1.3, 2.1.4)

**NO INICIADO.** La guía introduce cifrado dentro del Hito 3 y lo verifica en el 4; no se pospone la seguridad para una supuesta Sumativa.

- [ ] Implementar/revisar AES-256-GCM y código de vinculación según diseño. Aceptación: mismo código descifra, otro código o alteración se descartan; no hay texto del protocolo en claro.
- [ ] Validar protocolo y límites. Aceptación: datos malformados no ejecutan acciones ni cierran la app.
- [ ] Ejecutar P1–P7 y casos de ventana/seguridad en teléfonos. Aceptación: resultados reales y evidencias completados.
- [ ] Completar README con resultados y limitaciones, sin inventar evidencias.
- [ ] Revisar que no existan secretos, datos personales ni funciones de Sumativa.
- [ ] Registrar commit, publicar avance y reemplazar el marcador de entrega_aai.txt con URL real.
- [ ] Subir el TXT al AAI antes del cierre indicado por el docente. Aceptación: confirmación real de entrega.

## 5. Pruebas

Ver [PRUEBAS_FORMATIVA.md](docs/PRUEBAS_FORMATIVA.md). P1–P7 están PENDIENTES y solo se ejecutarán después de implementar los hitos correspondientes. La base tiene una prueba separada de arranque.

## 6. Trazabilidad

| Requisito | Hito | Criterio | Evidencia futura |
|---|---|---|---|
| Herramientas y proyecto | 0, 1 | 2.1.1 | Build y captura del teléfono |
| Problemática y F1–F5 | 1 | 2.1.1 | Revisión docente |
| Medio, mensajes y mockup | 2 | 2.1.1, 2.1.2 | Revisión docente |
| F1 sensor y F2 monitoreo | 3 | 2.1.2, 2.1.4 | P1, P2, ventana fija |
| F3 entrega y ACK | 3 | 2.1.2, 2.1.4 | P4 |
| F4 notificaciones | 3 | 2.1.4 | P3, P4, P7 |
| F5 estado seguro | 3, 4 | 2.1.3, 2.1.4 | P6 |
| Cifrado y validación | 3, 4 | 2.1.3 | P5 y pruebas de entradas inválidas |

## 7. Riesgos y decisiones pendientes

- Falta registrar captura y versión Android del primer arranque en Moto G35. El arranque base fue confirmado verbalmente por el estudiante.
- Falta revisión docente: detención antes de Hito 3.
- Red con aislamiento de clientes: comprobar router propio/hotspot durante P1.
- TCP puede tardar en detectar un corte silencioso: al implementar debe resolverse detección en ambos extremos sin dejar DISPENSANDO ni alterar el protocolo sin consulta.
- Parámetros de derivación de clave y cualquier comportamiento indispensable no especificado se consultarán antes de implementarse.
- La app no promete funcionamiento con proceso cerrado ni servicio en segundo plano.

## 8. Fuera de alcance

Login, usuarios, SQLite, Firestore, Firebase Authentication implementado, historial, MQTT, HiveMQ, FCM, ESP32 real, múltiples nodos y funciones opcionales. Firebase Authentication + Cloud Firestore se declaran únicamente como decisión futura de Sumativa. No se incorporan los Must ni las pruebas con placas de la guía de IA orientados a Sumativa.
