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
    subtitle = "Juego en la frente con historial inteligente",
    iconEmoji = "🎴",
    description = "Coloca el móvil en tu frente. Tus amigos te dan pistas para adivinar palabras o números del 1 al 5.000 sin repeticiones.",
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
    title = "Desafío 10.00s",
    subtitle = "El cronómetro ciego viral",
    iconEmoji = "⏱️",
    description = "A los 5.00s los números desaparecen. ¡Frena lo más cerca posible de 10.000 segundos!",
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
