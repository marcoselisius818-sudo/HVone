package com.example.data.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

private const val TAG = "TikTokAiCoach"

data class AiCoachFeedback(
  val viralTitle: String,
  val commentary: String,
  val viralTip: String,
  val hypeRating: Int // 0..100
)

class TikTokAiCoach {
  private val model by lazy {
    Firebase.ai.generativeModel(
      modelName = "gemini-3.8-flash"
    )
  }

  suspend fun getMatchCoachReview(
    gameTitle: String,
    scoreDetail: String,
    isWin: Boolean,
    playerNickname: String,
    opponentName: String
  ): Result<String> {
    return try {
      val prompt = """
        Eres un comentarista viral y coach experto de retos de TikTok, hiper energético, divertido, que usa jerga juvenil de creadores de contenido (como 'hype', 'épico', 'nivel pro', 'f en el chat', 'se picó el duelo', 'POV', 'racha legendaria').
        El jugador $playerNickname acaba de jugar a '$gameTitle'.
        Resultado: ${if (isWin) "VICTORIA" else "DERROTA"} contra $opponentName.
        Detalle del récord / puntaje: $scoreDetail.
        
        Dame un comentario breve de 3 o 4 oraciones máximo con tono de TikTok:
        1. Una reacción graciosa o eufórica al puntaje.
        2. Un consejo pro para mejorar los reflejos en el siguiente duelo.
        3. Termina con un remate viral y emojis.
      """.trimIndent()

      val response = withTimeout(45_000L) {
        model.generateContent(prompt)
      }

      val text = response.text
      if (!text.isNullOrBlank()) {
        Result.success(text.trim())
      } else {
        Result.failure(IllegalStateException("El Coach de TikTok no devolvió texto."))
      }
    } catch (e: TimeoutCancellationException) {
      Log.e(TAG, "AI review timed out", e)
      Result.failure(Exception("La conexión con Gemini tardó más de lo esperado. ¡Intenta de nuevo!"))
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "Error generating AI review", e)
      Result.failure(e)
    }
  }

  suspend fun generateCustomViralChallenge(): Result<String> {
    return try {
      val prompt = """
        Genera una idea para un reto viral loco y divertido de TikTok para jugar entre dos amigos en un mismo teléfono o en directo.
        Debe ser una regla extra picante (ej: 'El que pierda baila la tendencia del momento', 'Jugar usando solo los dedos meñiques', 'El perdedor manda un mensaje gracioso al chat del grupo', 'Jugar tap tap con una sola mano y ojos entrecerrados').
        Sé creativo, divertido y conciso (máximo 3 líneas).
      """.trimIndent()

      val response = withTimeout(45_000L) {
        model.generateContent(prompt)
      }

      val text = response.text
      if (!text.isNullOrBlank()) {
        Result.success(text.trim())
      } else {
        Result.failure(IllegalStateException("Respuesta vacía."))
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "Error generating viral challenge", e)
      Result.failure(e)
    }
  }
}
