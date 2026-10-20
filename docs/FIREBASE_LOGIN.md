# Configurar Login con Firebase Authentication

El Login se añadió a la Formativa por solicitud del profesor. Usa Firebase Authentication con el proveedor **Correo electrónico/Contraseña**. La aplicación no registra usuarios y no usa Firestore.

## Estado actual

El proyecto Firebase y su aplicación Android `cl.inacap.comederoiot` ya están registrados. El archivo `app/google-services.json` se encuentra en el equipo local y fue reconocido al compilar. `.gitignore` lo excluye del repositorio. Se agregó el plugin Google Services y Firebase Authentication al proyecto; `testDebugUnitTest assembleDebug` terminó correctamente con siete pruebas unitarias aprobadas. El proveedor **Correo electrónico/Contraseña quedó habilitado** en Firebase Console. El 20 de octubre de 2026, el acceso válido se comprobó en el Moto G35 y abrió la pantalla Conexión.

La cuenta de prueba ya se creó y el acceso válido pasó en el Moto G35. No se registran la cuenta ni la contraseña en este documento; la contraseña la administra el estudiante y no se incluye en GitHub.

## 1. Registro de proyecto y configuración Android (completado)

1. Entra a [Firebase Console](https://console.firebase.google.com/) con tu cuenta Google y crea un proyecto.
2. En la vista del proyecto, agrega una aplicación Android.
3. Escribe como nombre del paquete exactamente `cl.inacap.comederoiot`. Debe coincidir con `applicationId` en `app/build.gradle.kts`.
4. Descarga el archivo `google-services.json` que genera Firebase.
5. Guárdalo en la carpeta `app/` del proyecto, con esta ruta exacta: `app/google-services.json`.

El JSON ya está instalado en `app/google-services.json`. Mantén esa copia local: `.gitignore` la excluye deliberadamente; no la agregues, fuerces ni subas al repositorio. Tampoco guardes contraseñas, tokens o claves privadas en el código o en la documentación.

## 2. Crear una cuenta de prueba

1. En Firebase Console abre **Authentication → Método de acceso**.
2. **Correo electrónico/Contraseña ya está habilitado** en este proyecto.
3. Abre la pestaña **Usuarios**, selecciona **Agregar usuario** y crea una cuenta de prueba con un correo tuyo y una contraseña segura. La cuenta ya está creada; ingrésala directamente en Firebase Console y no la compartas con Codex.
4. La pantalla pide “Usuario (correo electrónico)” porque Firebase Authentication Email/Password inicia sesión con una dirección de correo. No existe un usuario separado del correo en este flujo.

No compartas la contraseña con Codex ni la incluyas en GitHub. El usuario de prueba se administra desde Firebase Console.

## 3. Sincronizar, compilar y usar

1. El plugin Google Services, Firebase BoM 35.0.2 y Firebase Authentication ya están integrados. Kotlin se actualizó a 2.4.20 para ser compatible con el SDK actual.
2. La compilación local ya pasó con `testDebugUnitTest assembleDebug`; después de crear la cuenta, puedes repetir **Sync Project with Gradle Files** y compilar desde Android Studio.
3. Instala el APK en un teléfono conectado a Internet e inicia sesión con la cuenta de prueba.
4. Tras autenticar, aparece la pantalla Conexión. Desde allí puedes elegir Nodo o Panel y seguir [INSTALACION.md](INSTALACION.md).
5. Usa **Cerrar sesión** en Conexión para volver a Login.

El Login deja entrar solo después de una autenticación exitosa. Si no existe la configuración, la pantalla explica qué falta y mantiene deshabilitado el botón. Si el proveedor rechaza las credenciales, se muestra un error y la app permanece en Login.

## Alcance y validación

La integración compiló correctamente con la configuración Firebase local y Email/Password está habilitado. L1 (credenciales válidas → Conexión) pasó en un Moto G35 el 20 de octubre de 2026. L2 pasó en el HONOR con credenciales ficticias: Firebase rechazó el acceso, mostró un error legible y la app permaneció en Login. L3 también pasó en el HONOR: la sesión persistió al cerrar/reabrir la app y el botón Cerrar sesión volvió a Login. P1–P7 se repitieron en dos teléfonos con el build que incluye Login el 20 de octubre de 2026; resultados y límites están en [PRUEBAS_FORMATIVA.md](PRUEBAS_FORMATIVA.md). No se guardaron credenciales ni capturas nuevas de estas verificaciones.

Referencias oficiales: [Configurar Firebase en Android](https://firebase.google.com/docs/android/setup) y [autenticación con correo y contraseña](https://firebase.google.com/docs/auth/android/password-auth).
