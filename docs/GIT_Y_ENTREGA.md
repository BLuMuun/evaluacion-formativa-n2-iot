# Git, GitHub y entrega al AAI

El repositorio local está en `master` y el remoto privado es https://github.com/BLuMuun/evaluacion-formativa-n2-iot. Los commits registran Hitos 0–3, y el cambio de Hito 4 se conserva en un commit separado. El comentario docente compartido por el estudiante autorizó continuar después de revisar el avance; no se copia la captura de WhatsApp al repositorio porque contiene un teléfono personal.

## Historial por hito

Se mantienen commits separados por hito. El commit de Hito 3 contiene la app conectada; Hito 4 agrega pruebas y documentación de seguridad. La existencia de un commit no prueba ejecución física. `.gitignore` excluye build, cachés, `local.properties` y archivos de configuración de secretos; revisa los cambios antes de publicar.

## Publicar en el repositorio

```powershell
git status --short
git log --oneline -5
git push origin master
```

La rama es `master`; el remoto es privado. El profesor necesita permiso concedido por el propietario para acceder. Para obtener una copia en otro equipo:

```powershell
git clone https://github.com/BLuMuun/evaluacion-formativa-n2-iot.git
```

## TXT para AAI

`entrega_aai.txt` contiene el enlace real del repositorio. La subida de ese archivo al AAI se realiza manualmente por el estudiante y no se declara hecha aquí.
