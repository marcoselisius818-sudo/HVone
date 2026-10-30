# Plan de Optimización, Sonido y Pulido Visual

## 1. Eliminación Completa de Coach Gemini y Limpieza de Perfil
- **Retiro de Coach Gemini**: Se eliminará el módulo de inteligencia artificial (`TikTokAiCoach.kt`), el diálogo emergente en la pantalla principal, los botones de sugerencias y las funciones asociadas en el `MainViewModel`.
- **Inicio de Sesión Simplificado y Guardado Automático**:
  - Se reemplazará la tarjeta técnica de `"Gemini AI & Firebase Cloud"` en la pantalla de **Perfil** por una tarjeta limpia de **Guardado de Progreso**.
  - Se eliminará cualquier mención técnica a `"Firebase"` o `"Room"`, mostrando únicamente la invitación clara: *"Inicia sesión con tu cuenta de Google para que tus datos, récords y trofeos queden guardados automáticamente"*.
  - Al iniciar sesión o registrar nuevos puntajes con la sesión activa, el progreso se respaldará y recuperará automáticamente sin requerir botones manuales adicionales.

## 2. Sistema de Efectos de Sonido Arcade (`SoundHelper`) con Botón de Silencio
- **Motor de Audio Ligero sin Lag**: Se creará `SoundHelper` utilizando síntesis de tonos de baja latencia en un hilo secundario para no bloquear jamás la interfaz ni requerir archivos pesados:
  - **La Bomba**: Tic-tac rítmico durante la ronda, pitidos en la cuenta regresiva (`3, 2, 1`) y sonido de explosión al estallar la bomba.
  - **Adivina la Palabra**: Pitidos de preparación, sonido alegre al acertar palabra, tono suave al pasar y timbre de fin de tiempo.
  - **Adivina el Número**: Efecto ascendente/descendente para pistas (*Mayor* / *Menor*) y fanfarria al descubrir el número secreto.
  - **Reto 10.00s, Tap Rush, Stroop y Emojis**: Efectos de toque rápido, acierto/error en colores, recolección de gemas y sonido de victoria al romper récords.
- **Control de Silencio (Mute)**: Se añadirá un botón accesible de sonido activado/silenciado (`🔊` / `🔇`) en la barra superior para silenciar o activar los efectos en cualquier momento.

## 3. Limpieza de Mensajes Innecesarios
- **Juego de La Bomba**: Se eliminará el texto innecesario `"¡Sin botones tediosos! La bomba explotará sola en cualquier segundo sin previo aviso..."` dejando una interfaz directa y enfocada en la temática activa.
- **Revisión General de Textos**: Se simplificarán las descripciones redundantes en las demás pantallas para que la interfaz luzca más limpia y directa.

## 4. Animaciones Suaves y Optimización Anti-Lag (60 FPS)
- **Transiciones Fluidas**: Se aplicarán transiciones suaves (`Crossfade` / `AnimatedContent` y `animateContentSize`) al cambiar entre pantallas y fases de cada minijuego.
- **Optimización de Rendimiento**:
  - En **La Bomba**, la animación de pulso solo se ejecutará mientras la bomba esté activa y usará aceleración por hardware (`graphicsLayer`) en lugar de recalcular el diseño en cada fotograma.
  - En **Atrapa y Esquiva (Emojis)**, se eliminará el bloque duplicado de renderizado de carriles y se optimizará el bucle de movimiento para evitar recomposiciones innecesarias.
  - Se desactivarán dependencias no utilizadas en `app/build.gradle.kts` para reducir el tamaño del APK y acelerar la compilación.

## 5. Verificación de Compatibilidad con GitHub Actions
- Se mantendrán las referencias dinámicas seguras para la configuración de autenticación y base de datos (`resources.getIdentifier`) de modo que GitHub Actions genere el `.apk` sin errores aunque `google-services.json` no esté subido al repositorio.
- Se verificará la compilación completa del proyecto con `compile_applet`.
