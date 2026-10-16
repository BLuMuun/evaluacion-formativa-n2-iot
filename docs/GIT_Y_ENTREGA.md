# Git, GitHub y entrega al AAI

El repositorio Git local está en la rama `master`. Los materiales de Hitos 0, 1 y 2 se registraron en commits separados y se publicaron en el remoto privado https://github.com/BLuMuun/evaluacion-formativa-n2-iot. Esta preparación se limita a Hitos 0–2; la revisión docente y los Hitos 3–4 siguen pendientes.

## Historial por hito

Los commits contienen solo el material de preparación de cada etapa. Su existencia no representa revisión docente ni pruebas físicas. Para mantener privado el correo personal, la identidad local usa el nombre público `BLuMuun` y el alias noreply configurado por GitHub.

```text
Hito 0: proyecto Android base
Hito 1: problemática y funcionalidades (revisión pendiente)
Hito 2: diseño y protocolo (revisión pendiente)
```

Estos commits registran preparación de archivos, no certifican aprobación docente ni pruebas. No crear commits de Hitos 3–4 mientras no se implementen después de la autorización.

## Publicar en el repositorio creado

El repositorio remoto es privado. El profesor necesitará acceso autorizado para abrir el enlace. Para obtener una copia en otro equipo, usar:

```powershell
git clone https://github.com/BLuMuun/evaluacion-formativa-n2-iot.git
```

La publicación se hizo desde la rama `master`. Revisar los archivos antes de futuros pushes; `.gitignore` ayuda pero no detecta automáticamente cualquier secreto o dato personal.

## TXT para AAI

`entrega_aai.txt` contiene únicamente el enlace real del repositorio:

```text
Repositorio GitHub:
https://github.com/BLuMuun/evaluacion-formativa-n2-iot
```

El TXT se prepara para subirlo al AAI cuando corresponda. La entrega de la Formativa requiere completar y probar los hitos pendientes; compartir este enlace ahora sirve para revisar Hitos 1–2 y no acredita una Formativa terminada. Subir el archivo al AAI antes del cierre indicado por el docente.
