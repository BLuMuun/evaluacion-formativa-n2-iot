# Configurar Login con Firebase Authentication

El Login se añadió a la Formativa por solicitud del profesor. Usa Firebase Authentication con el proveedor **Correo electrónico/Contraseña**. La aplicación no registra usuarios y no usa Firestore.

## 1. Crear el proyecto y registrar la app Android

1. Entra a [Firebase Console](https://console.firebase.google.com/) con tu cuenta Google y crea un proyecto.
2. En la vista del proyecto, agrega una aplicación Android.
3. Escribe como nombre del paquete exactamente `cl.inacap.comederoiot`. Debe coincidir con `applicationId` en `app/build.gradle.kts`.
4. Descarga el archivo `google-services.json` que genera Firebase.
5. Guárdalo en la carpeta `app/` del proyecto, con esta ruta exacta: `app/google-services.json`.

Ese JSON contiene identificadores de configuración del proyecto. `.gitignore` lo excluye deliberadamente: no lo agregues, fuerces ni subas al repositorio. Tampoco guardes contraseñas, tokens o claves privadas en el código o en la documentación.

## 2. Habilitar el proveedor y crear una cuenta

1. En Firebase Console abre **Authentication → Sign-in method** (o **Proveedores de acceso**).
2. Habilita **Correo electrónico/Contraseña** y guarda.
3. Abre la pestaña **Users / Usuarios**, selecciona **Add user / Agregar usuario** y crea una cuenta de prueba con un correo tuyo y una contraseña segura.
4. La pantalla pide “Usuario (correo electrónico)” porque Firebase Authentication Email/Password inicia sesión con una dirección de correo. No existe un usuario separado del correo en este flujo.

No compartas la contraseña con Codex ni la incluyas en GitHub. El usuario de prueba se administra desde Firebase Console.

## 3. Sincronizar, compilar y usar

1. Cierra y vuelve a abrir Android Studio, o pulsa **Sync Project with Gradle Files**.
2. Espera a que Gradle procese el plugin de Google Services y las dependencias de Firebase.
3. Ejecuta `testDebugUnitTest assembleDebug` o **Build → Make Project**.
4. Instala el APK en ambos teléfonos conectados a Internet e inicia sesión con la cuenta de prueba.
5. Tras autenticar, aparece la pantalla Conexión. Desde allí puedes elegir Nodo o Panel y seguir [INSTALACION.md](INSTALACION.md).
6. Usa **Cerrar sesión** en Conexión para volver a Login.

El Login deja entrar solo después de una autenticación exitosa. Si no existe la configuración, la pantalla explica qué falta y mantiene deshabilitado el botón. Si el proveedor rechaza las credenciales, se muestra un error y la app permanece en Login.

## Alcance y validación

La integración actual compila sin incluir el archivo JSON cuando el proyecto Firebase aún no está listo; en ese estado no permite autenticar ni abrir Conexión. Las pruebas L1–L3 y la repetición de P1–P7 quedan pendientes hasta crear el proyecto, agregar la configuración y probar en teléfonos. No se afirma que Login haya sido validado físicamente mientras eso no ocurra.

Referencias oficiales: [Configurar Firebase en Android](https://firebase.google.com/docs/android/setup) y [autenticación con correo y contraseña](https://firebase.google.com/docs/auth/android/password-auth).
