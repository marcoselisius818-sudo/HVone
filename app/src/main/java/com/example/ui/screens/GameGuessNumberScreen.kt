package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.HapticHelper
import com.example.ui.util.PlayerRosterManager
import com.example.ui.util.SoundHelper
import kotlin.math.abs
import kotlin.random.Random

data class GuessAttempt(
  val number: Int,
  val playerName: String,
  val hint: String,
  val isCorrect: Boolean
)

@Composable
fun GameGuessNumberScreen(
  isPartyModeInitial: Boolean = false,
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean) -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val isMuted by SoundHelper.isMuted.collectAsStateWithLifecycle()

  val savedPlayers = remember { PlayerRosterManager.getPartyPlayers(context, "Jugador 1") }
  var isPartyTurns by remember { mutableStateOf(isPartyModeInitial) }
  var currentTurnIdx by remember { mutableIntStateOf(0) }

  // Default to 5000 so the 1 to 5.000 mode is immediately active and ready!
  var maxRange by remember { mutableIntStateOf(5000) }
  var secretNumber by remember { mutableIntStateOf(Random.nextInt(1, 5001)) }
  var minPossible by remember { mutableIntStateOf(1) }
  var maxPossible by remember { mutableIntStateOf(5000) }

  var inputGuess by remember { mutableStateOf("") }
  val attempts = remember { mutableStateListOf<GuessAttempt>() }
  var isWon by remember { mutableStateOf(false) }
  var winnerName by remember { mutableStateOf("") }
  var hintText by remember { mutableStateOf("Adivina el número secreto entre 1 y 5000") }

  val currentTurnPlayer = if (isPartyTurns && savedPlayers.isNotEmpty()) {
    savedPlayers[currentTurnIdx % savedPlayers.size]
  } else {
    "Tú"
  }

  fun resetGame(newRange: Int = maxRange) {
    maxRange = newRange
    secretNumber = Random.nextInt(1, newRange + 1)
    minPossible = 1
    maxPossible = newRange
    attempts.clear()
    isWon = false
    winnerName = ""
    currentTurnIdx = 0
    inputGuess = ""
    hintText = "Adivina el número secreto entre 1 y $newRange"
  }

  fun submitGuess(guessVal: Int? = inputGuess.toIntOrNull()) {
    if (isWon) return
    val guess = guessVal ?: return
    if (guess !in 1..maxRange) {
      HapticHelper.buzzer(context)
      hintText = "⚠️ El número debe estar entre 1 y $maxRange"
      return
    }
    inputGuess = ""
    val playerWhoGuessed = currentTurnPlayer
    val diff = abs(secretNumber - guess)

    if (guess == secretNumber) {
      HapticHelper.vibrateOnly(context, 90L)
      SoundHelper.playVictory()
      isWon = true
      winnerName = playerWhoGuessed
      attempts.add(0, GuessAttempt(guess, playerWhoGuessed, "¡EXACTO! 🎯", true))
      val baseScore = when (maxRange) {
        5000 -> 3500
        1000 -> 2000
        500 -> 1500
        else -> 1000
      }
      val penaltyPerAttempt = when (maxRange) {
        5000 -> 50
        1000 -> 65
        else -> 75
      }
      val score = (baseScore - (attempts.size - 1) * penaltyPerAttempt).coerceAtLeast(200)
      onRecordResult(
        score,
        "Adivinó el $secretNumber (1-$maxRange) en ${attempts.size} intentos",
        if (isPartyTurns) "Grupo ($playerWhoGuessed)" else "Adivina el Número",
        true
      )
    } else if (guess < secretNumber) {
      HapticHelper.vibrateOnly(context, 20L)
      SoundHelper.playHintHigher()
      if (guess + 1 > minPossible) {
        minPossible = (guess + 1).coerceAtMost(maxPossible)
      }
      val proximity = when {
        diff <= 15 -> " ¡Hirviendo! 🔥"
        diff <= 75 -> " ¡Muy cerca! ⚡"
        maxRange >= 1000 && diff >= 1000 -> " ¡Mucho más alto! 🚀"
        else -> ""
      }
      hintText = "El secreto es MAYOR que $guess ⬆️$proximity"
      attempts.add(0, GuessAttempt(guess, playerWhoGuessed, "MAYOR ⬆️$proximity", false))
      if (isPartyTurns && savedPlayers.isNotEmpty()) {
        currentTurnIdx = (currentTurnIdx + 1) % savedPlayers.size
      }
    } else {
      HapticHelper.vibrateOnly(context, 20L)
      SoundHelper.playHintLower()
      if (guess - 1 < maxPossible) {
        maxPossible = (guess - 1).coerceAtLeast(minPossible)
      }
      val proximity = when {
        diff <= 15 -> " ¡Hirviendo! 🔥"
        diff <= 75 -> " ¡Muy cerca! ⚡"
        maxRange >= 1000 && diff >= 1000 -> " ¡Mucho más bajo! ❄️"
        else -> ""
      }
      hintText = "El secreto es MENOR que $guess ⬇️$proximity"
      attempts.add(0, GuessAttempt(guess, playerWhoGuessed, "MENOR ⬇️$proximity", false))
      if (isPartyTurns && savedPlayers.isNotEmpty()) {
        currentTurnIdx = (currentTurnIdx + 1) % savedPlayers.size
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp, vertical = 10.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header with Mute Toggle
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        IconButton(
          onClick = {
            HapticHelper.click(context)
            onBack()
          },
          modifier = Modifier.testTag("back_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Números (1 a $maxRange)",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Black,
          color = TextPrimary
        )
      }

      FilterChip(
        selected = isPartyTurns,
        onClick = {
          HapticHelper.click(context)
          isPartyTurns = !isPartyTurns
        },
        label = {
          Text(
            text = if (isPartyTurns) "👥 Por Turnos" else "👤 Solo",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = NeonCyan,
          selectedLabelColor = Color.Black,
          containerColor = SurfaceDark,
          labelColor = TextSecondary
        )
      )

      IconButton(onClick = { SoundHelper.toggleMute(context) }) {
        Icon(
          imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
          contentDescription = if (isMuted) "Activar sonido" else "Silenciar sonido",
          tint = if (isMuted) NeonPink else NeonCyan
        )
      }
    }

    // Range Selector Chips (1-100, 1-500, 1-1000, 1-5000)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf(
        100 to "1-100",
        500 to "1-500",
        1000 to "1-1.000",
        5000 to "1-5.000 🏆"
      ).forEach { (rangeVal, label) ->
        FilterChip(
          selected = maxRange == rangeVal,
          onClick = {
            HapticHelper.click(context)
            resetGame(rangeVal)
          },
          label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          modifier = Modifier.weight(1f),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = if (rangeVal == 5000) ElectricGold else NeonPink,
            selectedLabelColor = if (rangeVal == 5000) Color.Black else Color.White,
            containerColor = SurfaceDark,
            labelColor = TextSecondary
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Status & Narrowed Range Card
    Card(
      colors = CardDefaults.cardColors(containerColor = SurfaceDark),
      shape = RoundedCornerShape(18.dp),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.5.dp, if (isWon) ElectricGreen else NeonCyan, RoundedCornerShape(18.dp))
        .animateContentSize()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isPartyTurns) "Turno: $currentTurnPlayer" else "Rango Posible: $minPossible — $maxPossible",
            fontSize = 12.sp,
            color = NeonCyan,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Intentos: ${attempts.size}",
            fontSize = 12.sp,
            color = ElectricGold,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = if (isWon) "🎉 ¡$secretNumber! ¡Ganó $winnerName en ${attempts.size} intentos! 🎉" else hintText,
          fontSize = 16.sp,
          fontWeight = FontWeight.Black,
          color = if (isWon) ElectricGreen else TextPrimary,
          textAlign = TextAlign.Center
        )

        if (!isWon && isPartyTurns) {
          Text(
            text = "Se busca entre $minPossible y $maxPossible",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (!isWon) {
      // Digital Display + Quick Midpoint Button + Submit Button
      val midValue = ((minPossible + maxPossible) / 2).coerceIn(1, maxRange)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .weight(1.2f)
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background( Color(0xFF101320))
            .border(1.5.dp, if (inputGuess.isNotEmpty()) NeonPink else Color(0xFF2E344E), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp)
            .testTag("guess_number_input"),
          contentAlignment = Alignment.CenterStart
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (inputGuess.isEmpty()) "Escribe (1-$maxRange)" else inputGuess,
              fontSize = if (inputGuess.isEmpty()) 13.sp else 24.sp,
              fontWeight = FontWeight.Black,
              fontFamily = if (inputGuess.isEmpty()) FontFamily.Default else FontFamily.Monospace,
              color = if (inputGuess.isEmpty()) TextMuted else ElectricGold
            )
            if (inputGuess.isNotEmpty()) {
              IconButton(
                onClick = {
                  HapticHelper.tap(context)
                  inputGuess = inputGuess.dropLast(1)
                }
              ) {
                Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Borrar", tint = NeonPink)
              }
            }
          }
        }

        Button(
          onClick = {
            HapticHelper.click(context)
            submitGuess(midValue)
          },
          modifier = Modifier.height(52.dp),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = NeonCyan)
        ) {
          Text(text = "Mitad\n($midValue)", fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 13.sp)
        }

        Button(
          onClick = {
            HapticHelper.click(context)
            submitGuess()
          },
          enabled = inputGuess.isNotEmpty(),
          modifier = Modifier.height(52.dp).testTag("submit_guess_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
        ) {
          Text(text = "PROBAR", fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Built-in Fast Numeric Pad (0-9) so numbers up to 5000 work smoothly on every screen
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val rows = listOf(
          listOf("1", "2", "3", "4", "5"),
          listOf("6", "7", "8", "9", "0")
        )
        rows.forEach { rowDigits ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            rowDigits.forEach { digit ->
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(SurfaceDark)
                  .border(1.dp, Color(0xFF2A2E45), RoundedCornerShape(12.dp))
                  .clickable {
                    HapticHelper.tap(context)
                    val candidate = if (inputGuess == "0") digit else inputGuess + digit
                    val num = candidate.toLongOrNull()
                    if (num != null && num <= maxRange) {
                      inputGuess = candidate
                    } else if (num != null && num > maxRange) {
                      inputGuess = maxRange.toString()
                    }
                  },
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = digit,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Black,
                  color = TextPrimary
                )
              }
            }
          }
        }
      }
    } else {
      Button(
        onClick = {
          HapticHelper.click(context)
          resetGame(maxRange)
        },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
      ) {
        Icon(Icons.Default.Refresh, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Jugar Otra Partida (1-$maxRange)", fontWeight = FontWeight.Black, fontSize = 15.sp)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Attempts History
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      itemsIndexed(attempts, key = { index, item -> "${attempts.size - index}_${item.number}" }) { idx, att ->
        val attemptNum = attempts.size - idx
        Card(
          colors = CardDefaults.cardColors(
            containerColor = if (att.isCorrect) Color(0xFF162E20) else SurfaceDark
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "#$attemptNum",
                fontSize = 11.sp,
                color = TextMuted,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "${att.number}",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = TextPrimary
              )
              if (isPartyTurns) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "(${att.playerName})",
                  fontSize = 12.sp,
                  color = TextSecondary
                )
              }
            }
            Text(
              text = att.hint,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = if (att.isCorrect) ElectricGreen else if (att.hint.contains("MAYOR")) NeonCyan else NeonPink
            )
          }
        }
      }
    }
  }
}
