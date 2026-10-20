# Configurar Login con Firebase Authentication

El Login se añadió a la Formativa por solicitud del profesor. Usa Firebase Authentication con el proveedor **Correo electrónico/Contraseña**. La aplicación no registra usuarios y no usa Firestore.

## Estado actual

El proyecto Firebase y su aplicación Android `cl.inacap.comederoiot` ya están registrados. El archivo `app/google-services.json` se encuentra en el equipo local y fue reconocido al compilar. `.gitignore` lo excluye del repositorio. Se agregó el plugin Google Services y Firebase Authentication al proyecto; `testDebugUnitTest assembleDebug` terminó correctamente con siete pruebas unitarias aprobadas.

**Pendiente:** habilitar el proveedor Email/Password en Firebase Console, crear una cuenta de prueba y validar el Login en un teléfono. La cuenta y su contraseña las administra el estudiante; no compartas la contraseña con Codex ni la incluyas en GitHub.

## 1. Registro de proyecto y configuración Android (completado)

1. Entra a [Firebase Console](https://console.firebase.google.com/) con tu cuenta Google y crea un proyecto.
2. En la vista del proyecto, agrega una aplicación Android.
3. Escribe como nombre del paquete exactamente `cl.inacap.comederoiot`. Debe coincidir con `applicationId` en `app/build.gradle.kts`.
4. Descarga el archivo `google-services.json` que genera Firebase.
5. Guárdalo en la carpeta `app/` del proyecto, con esta ruta exacta: `app/google-services.json`.

El JSON ya está instalado en `app/google-services.json`. Mantén esa copia local: `.gitignore` la excluye deliberadamente; no la agregues, fuerces ni subas al repositorio. Tampoco guardes contraseñas, tokens o claves privadas en el código o en la documentación.

## 2. Habilitar el proveedor y crear una cuenta

1. En Firebase Console abre **Authentication → Sign-in method** (o **Proveedores de acceso**).
2. Habilita **Correo electrónico/Contraseña** y guarda. Este paso está pendiente.
3. Abre la pestaña **Users / Usuarios**, selecciona **Add user / Agregar usuario** y crea una cuenta de prueba con un correo tuyo y una contraseña segura. Este paso está pendiente; no entregues la contraseña a nadie.
4. La pantalla pide “Usuario (correo electrónico)” porque Firebase Authentication Email/Password inicia sesión con una dirección de correo. No existe un usuario separado del correo en este flujo.

No compartas la contraseña con Codex ni la incluyas en GitHub. El usuario de prueba se administra desde Firebase Console.

## 3. Sincronizar, compilar y usar

1. El plugin Google Services, Firebase BoM 35.0.2 y Firebase Authentication ya están integrados. Kotlin se actualizó a 2.4.20 para ser compatible con el SDK actual.
2. La compilación local ya pasó con `testDebugUnitTest assembleDebug`; tras habilitar el proveedor y crear la cuenta, puedes repetir **Sync Project with Gradle Files** y compilar desde Android Studio.
3. Instala el APK en un teléfono conectado a Internet e inicia sesión con la cuenta de prueba.
4. Tras autenticar, aparece la pantalla Conexión. Desde allí puedes elegir Nodo o Panel y seguir [INSTALACION.md](INSTALACION.md).
5. Usa **Cerrar sesión** en Conexión para volver a Login.

El Login deja entrar solo después de una autenticación exitosa. Si no existe la configuración, la pantalla explica qué falta y mantiene deshabilitado el botón. Si el proveedor rechaza las credenciales, se muestra un error y la app permanece en Login.

## Alcance y validación

La integración compiló correctamente con la configuración Firebase local, pero todavía no se ha autenticado una cuenta. Las pruebas L1–L3 y la repetición de P1–P7 quedan pendientes hasta habilitar Email/Password, crear la cuenta de prueba y probar en teléfonos. No se afirma que Login haya sido validado físicamente mientras eso no ocurra.

Referencias oficiales: [Configurar Firebase en Android](https://firebase.google.com/docs/android/setup) y [autenticación con correo y contraseña](https://firebase.google.com/docs/auth/android/password-auth).
