package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlin.random.Random

data class GuessAttempt(
  val number: Int,
  val hint: String, // "Mayor ⬆️" or "Menor ⬇️" or "¡Correcto! 🎯"
  val isCorrect: Boolean
)

@Composable
fun GameGuessNumberScreen(
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean) -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current

  var maxRange by remember { mutableIntStateOf(100) }
  var secretNumber by remember { mutableIntStateOf(Random.nextInt(1, maxRange + 1)) }
  var inputGuess by remember { mutableStateOf("") }
  var attempts = remember { mutableStateListOf<GuessAttempt>() }
  var isWon by remember { mutableStateOf(false) }
  var hintText by remember { mutableStateOf("Ingresa un número del 1 al $maxRange") }

  fun resetGame(newRange: Int = maxRange) {
    maxRange = newRange
    secretNumber = Random.nextInt(1, newRange + 1)
    attempts.clear()
    isWon = false
    inputGuess = ""
    hintText = "Ingresa un número del 1 al $newRange"
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "Adivina el Número Secreto",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Range Selector Chips (up to 5000)
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
            if (maxRange != rangeVal) {
              HapticHelper.click(context)
              resetGame(rangeVal)
            }
          },
          label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = if (rangeVal == 5000) ElectricGold else NeonPink,
            selectedLabelColor = Color.Black,
            containerColor = SurfaceDark,
            labelColor = TextSecondary
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Info Card
    Card(
      colors = CardDefaults.cardColors(containerColor = SurfaceDark),
      shape = RoundedCornerShape(20.dp),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, Color(0xFF282C40), RoundedCornerShape(20.dp))
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = "Rango Activo: 1 al $maxRange", fontSize = 13.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = if (isWon) "🎉 ¡$secretNumber! ¡CORRECTO! 🎉" else hintText,
          fontSize = 19.sp,
          fontWeight = FontWeight.Black,
          color = if (isWon) ElectricGreen else ElectricGold,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Intentos realizados: ${attempts.size}",
          fontSize = 12.sp,
          color = TextSecondary
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (!isWon) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = inputGuess,
          onValueChange = { str ->
            if (str.all { it.isDigit() } && (str.isEmpty() || str.toLongOrNull()?.let { it <= maxRange } == true)) {
              inputGuess = str
            }
          },
          placeholder = { Text("1 a $maxRange") },
          modifier = Modifier.weight(1f).testTag("guess_number_input"),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = Color(0xFF333852),
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        Button(
          onClick = {
            val guess = inputGuess.toIntOrNull()
            if (guess != null && guess in 1..maxRange) {
              inputGuess = ""
              val diff = Math.abs(secretNumber - guess)
              if (guess == secretNumber) {
                HapticHelper.success(context)
                isWon = true
                attempts.add(0, GuessAttempt(guess, "¡CORRECTO! 🎯", true))
                val baseScore = when (maxRange) {
                  5000 -> 3500
                  1000 -> 2000
                  500 -> 1500
                  else -> 1000
                }
                val penaltyPerAttempt = when (maxRange) {
                  5000 -> 60
                  1000 -> 70
                  else -> 80
                }
                val score = (baseScore - attempts.size * penaltyPerAttempt).coerceAtLeast(150)
                onRecordResult(score, "Adivinó el $secretNumber (Rango 1-$maxRange) en ${attempts.size} intentos", "Adivina el Número", true)
              } else if (guess < secretNumber) {
                HapticHelper.tap(context)
                val extraHint = if (maxRange >= 1000 && diff > 500) " (Mucho más alto 🚀)" else if (diff <= 25) " (¡Muy cerca! 🔥)" else ""
                hintText = "¡El número secreto es MAYOR que $guess! ⬆️$extraHint"
                attempts.add(0, GuessAttempt(guess, "Es MAYOR ⬆️$extraHint", false))
              } else {
                HapticHelper.tap(context)
                val extraHint = if (maxRange >= 1000 && diff > 500) " (Mucho más bajo ❄️)" else if (diff <= 25) " (¡Muy cerca! 🔥)" else ""
                hintText = "¡El número secreto es MENOR que $guess! ⬇️$extraHint"
                attempts.add(0, GuessAttempt(guess, "Es MENOR ⬇️$extraHint", false))
              }
            }
          },
          modifier = Modifier.height(56.dp),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
        ) {
          Text(text = "Probar", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
      }
    } else {
      Button(
        onClick = {
          HapticHelper.click(context)
          resetGame()
        },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
      ) {
        Icon(Icons.Default.Refresh, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Jugar con Otro Número (1-$maxRange)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "Historial de Intentos:",
      fontWeight = FontWeight.Bold,
      color = TextSecondary,
      fontSize = 14.sp,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(attempts) { att ->
        Card(
          colors = CardDefaults.cardColors(
            containerColor = if (att.isCorrect) Color(0xFF162E20) else SurfaceDark
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Número: ${att.number}", fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(
              text = att.hint,
              fontWeight = FontWeight.Black,
              color = if (att.isCorrect) ElectricGreen else if (att.hint.contains("MAYOR")) NeonCyan else NeonPink
            )
          }
        }
      }
    }
  }
}
