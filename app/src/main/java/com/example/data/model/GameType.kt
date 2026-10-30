package com.example.data.model

enum class GameType(
  val title: String,
  val subtitle: String,
  val iconEmoji: String,
  val description: String,
  val isDuelSupported: Boolean
) {
  BOMBA(
    title = "La Bomba",
    subtitle = "Temáticas y tiempo secreto",
    iconEmoji = "💣",
    description = "Menciona palabras de la temática en voz alta y pásate la ronda con tus amigos antes de que la bomba estalle en un segundo secreto.",
    isDuelSupported = false
  ),
  HEADS_UP(
    title = "Adivina la Palabra",
    subtitle = "Juego en la frente con temáticas",
    iconEmoji = "🎴",
    description = "Coloca el móvil en tu frente. Tus amigos te dan pistas para adivinar palabras o números de la temática.",
    isDuelSupported = false
  ),
  GUESS_NUMBER(
    title = "Adivina el Número",
    subtitle = "Mayor o Menor (hasta 5.000)",
    iconEmoji = "🔢",
    description = "Descubre el número secreto del 1 hasta 5.000 con pistas de 'Mayor' o 'Menor' en el menor número de intentos.",
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
