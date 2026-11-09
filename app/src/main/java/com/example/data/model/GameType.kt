package com.example.data.model

enum class GameType(
  val title: String,
  val subtitle: String,
  val iconEmoji: String,
  val description: String,
  val isDuelSupported: Boolean
) {
  IMPOSTOR(
    title = "El Impostor",
    subtitle = "Deducción social, pistas y votación",
    iconEmoji = "🕵️",
    description = "Todos reciben la palabra secreta menos el Impostor, quien recibe una pista sutil. ¡Debatan y voten manualmente para descubrirlo!",
    isDuelSupported = false
  ),
  BOMBA(
    title = "La Bomba",
    subtitle = "Temáticas y tensión progresiva",
    iconEmoji = "💣",
    description = "Menciona palabras de la temática en voz alta y pásate la ronda con tus amigos antes de que la bomba estalle sin previo aviso.",
    isDuelSupported = false
  ),
  HEADS_UP(
    title = "Adivina Qué Soy",
    subtitle = "Rondas de 1, 3, 5 o 10 min en la frente",
    iconEmoji = "🎴",
    description = "Coloca el móvil en tu frente. Elige entre 1 y 10 minutos y adivina palabras o números del 1 al 5.000 sin repeticiones.",
    isDuelSupported = false
  ),
  GUESS_NUMBER(
    title = "Adivina el Número",
    subtitle = "Mayor o Menor (del 1 al 5.000)",
    iconEmoji = "🔢",
    description = "Descubre el número secreto del 1 hasta 5.000 en modo individual o por turnos con tus amigos usando el teclado rápido.",
    isDuelSupported = true
  ),
  STOP_10S(
    title = "Cronómetro a Ciegas",
    subtitle = "Modos 1s, 10s, 15s y Azar (1-30s)",
    iconEmoji = "⏱️",
    description = "El cronómetro se desvanece progresivamente hasta quedar incógnito. ¡Frena en el segundo exacto en 1s, 10s, 15s o al azar!",
    isDuelSupported = true
  ),
  TAP_BATTLE(
    title = "Guerra de Taps",
    subtitle = "Tug-of-War de dedos",
    iconEmoji = "⚡",
    description = "Toca tu mitad de pantalla lo más rápido que puedas para empujar la barra al rival.",
    isDuelSupported = true
  )
}
