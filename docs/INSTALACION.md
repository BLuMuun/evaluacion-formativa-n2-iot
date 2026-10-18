# Compilar, instalar y probar en dos teléfonos

La app Formativa usa un teléfono A como Nodo servidor y un teléfono B como Panel cliente. Instala el mismo APK de depuración en ambos. Se necesita Android 8.0/API 26 o superior y que los dos teléfonos compartan una red Wi-Fi local o hotspot que permita comunicación entre clientes.

## 1. Abrir y compilar en Android Studio

1. Abre la carpeta raíz del repositorio con **Open** en Android Studio; no crees otro proyecto ni abras solo `app/`.
2. En **Settings → Build, Execution, Deployment → Build Tools → Gradle**, selecciona el JDK integrado de Android Studio (JDK 21 funciona con esta configuración). El proyecto genera bytecode Java 17.
3. En SDK Manager instala Android SDK Platform 36 y Android SDK Build-Tools. La ruta del SDK se guarda en `local.properties`, archivo local que no se publica.
4. Pulsa **Sync Project with Gradle Files** y espera `BUILD SUCCESSFUL`.
5. Selecciona **Build → Make Project** o, en la terminal de Android Studio, ejecuta:

   ```powershell
   .\gradlew.bat testDebugUnitTest assembleDebug
   ```

   La primera vez se descargan Gradle y dependencias. El APK aparece en `app/build/outputs/apk/debug/app-debug.apk`.

## 2. Instalar el mismo APK en A y B

Con USB y Android Studio:

1. En cada teléfono habilita Opciones de desarrollador y **Depuración USB** (Ajustes → Acerca del teléfono → toca siete veces Número de compilación).
2. Conecta A al PC, acepta la autorización en el teléfono, selecciónalo en Android Studio y pulsa **Run app**. Repite con B.

También puedes instalar el APK directamente:

1. Copia `app-debug.apk` a cada teléfono por cable o transferencia local.
2. Abre el APK desde Archivos. Si Android lo solicita, habilita **Permitir desde esta fuente** para esa aplicación instaladora y confirma instalar.
3. Esa autorización corresponde solo a la instalación manual; la app solicita únicamente sus permisos de funcionamiento.

## 3. Iniciar la conexión

1. Conecta ambos teléfonos al mismo router Wi-Fi o conecta ambos al hotspot de uno de ellos. Evita redes con aislamiento de clientes. Mantén Comedero IoT visible en primer plano.
2. En A abre **Comedero IoT**, deja el rol **Nodo**, ingresa un código de seis dígitos y pulsa **Iniciar Nodo**. A muestra la IP local y queda esperando.
3. En B elige **Panel**, escribe la IP mostrada por A y el mismo código de seis dígitos. Pulsa **Conectar**.
4. Espera a que ambos indiquen **CONECTADO**. ACCESS_NETWORK_STATE se concede automáticamente como permiso normal y no muestra diálogo. Si Android 13 o superior muestra las notificaciones deshabilitadas, pulsa **Permitir notificaciones** y acepta para probar alertas; si lo niegas, la conexión debe seguir funcionando.
5. Ejecuta los pasos de P1–P7 en [PRUEBAS_FORMATIVA.md](PRUEBAS_FORMATIVA.md). Las pruebas están pendientes hasta que las realices y registres resultados/evidencia.

## 4. Ejecutar P1–P7

En P1–P7 conserva A como Nodo y B como Panel. Reinicia la conexión antes de pruebas que especifican códigos distintos o pérdida del enlace. Para P5 usa códigos de prueba `111111` y `222222`, nunca un secreto personal. En P6 corta el Wi-Fi del Panel o fuerza la detención de la app desde Ajustes; registra cuánto tarda en reflejarse la pérdida. En P7 deniega la notificación en Android 13+ y repite conexión, alerta y control.

Completa manualmente teléfono/modelo, versión Android, red, fecha, resultado observado y evidencia. No marques una prueba como aprobada si no se ejecutó en los teléfonos.

## Si Android Studio o el teléfono informa un error

- Teléfono no listado: verifica cable de datos, autorización de depuración y controlador del fabricante en Windows.
- `SDK location not found`: selecciona la ruta desde SDK Manager y sincroniza Gradle; no publiques tu `local.properties`.
- Error de Java/Gradle: usa el JDK de Android Studio y revisa la salida completa de Build.
- Conexión rechazada/esperando: confirma que ambos están en la misma red, que el router no aísla los equipos, que la IP del Nodo no cambió y que el puerto TCP 5050 está permitido en la red.
- Código diferente: el socket puede conectar, pero los mensajes cifrados se descartan; vuelve a Conexión y utiliza el mismo código de prueba en ambos.

Las pruebas físicas P1–P7 no se consideran realizadas por compilar, instalar o ejecutar pruebas unitarias.
