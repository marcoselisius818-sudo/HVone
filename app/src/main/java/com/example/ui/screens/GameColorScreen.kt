package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
import com.example.ui.util.SoundHelper
import kotlinx.coroutines.delay
import kotlin.random.Random

data class ColorItem(val name: String, val color: Color)

val GameColors = listOf(
  ColorItem("ROJO", Color(0xFFFF2A55)),
  ColorItem("AZUL", Color(0xFF00D2FF)),
  ColorItem("VERDE", Color(0xFF00E676)),
  ColorItem("AMARILLO", Color(0xFFFFEA00))
)

@Composable
fun GameColorScreen(
  isDuel: Boolean,
  player1Name: String = "Tú",
  player2Name: String = "Rival",
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean) -> Unit
) {
  BackHandler { onBack() }

  if (isDuel) {
    GameColorDuelView(
      player1Name = player1Name,
      player2Name = player2Name,
      onBack = onBack,
      onRecordResult = onRecordResult
    )
  } else {
    GameColorSoloView(
      onBack = onBack,
      onRecordResult = onRecordResult
    )
  }
}

@Composable
private fun GameColorSoloView(
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean) -> Unit
) {
  val context = LocalContext.current
  val isMuted by SoundHelper.isMuted.collectAsStateWithLifecycle()
  var isRunning by remember { mutableStateOf(false) }
  var score by remember { mutableIntStateOf(0) }
  var streak by remember { mutableIntStateOf(0) }
  var timeLeft by remember { mutableIntStateOf(15) }
  var isCompleted by remember { mutableStateOf(false) }

  var currentWordItem by remember { mutableStateOf(GameColors[0]) }
  var currentInkItem by remember { mutableStateOf(GameColors[1]) }

  fun nextQuestion() {
    val word = GameColors[Random.nextInt(GameColors.size)]
    var ink = GameColors[Random.nextInt(GameColors.size)]
    while (ink == word) {
      ink = GameColors[Random.nextInt(GameColors.size)]
    }
    currentWordItem = word
    currentInkItem = ink
  }

  LaunchedEffect(isRunning) {
    if (isRunning) {
      nextQuestion()
      timeLeft = 15
      while (timeLeft > 0 && isRunning) {
        delay(1000)
        timeLeft--
        if (timeLeft == 0) {
          isRunning = false
          isCompleted = true
          HapticHelper.vibrateOnly(context, 90L)
          SoundHelper.playVictory()
          onRecordResult(score, "$score pts ($streak racha)", "Solo Stroop", score >= 800)
        }
      }
    }
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
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Reflejo Mental Stroop",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
      }
      IconButton(onClick = { SoundHelper.toggleMute(context) }) {
        Icon(
          imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
          contentDescription = if (isMuted) "Activar sonido" else "Silenciar sonido",
          tint = if (isMuted) NeonPink else NeonCyan
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Info bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = "⏳ ${timeLeft}s", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ElectricGold)
      Text(text = "🔥 Racha: x$streak", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NeonPink)
      Text(text = "🏆 $score pts", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeonCyan)
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Instruction Banner
    Card(
      colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = "¡TOCA EL COLOR DE LA TINTA! No te dejes engañar por el texto.",
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(12.dp)
      )
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Word Box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .clip(RoundedCornerShape(24.dp))
        .background(SurfaceDark)
        .border(2.dp, SurfaceVariantDark, RoundedCornerShape(24.dp)),
      contentAlignment = Alignment.Center
    ) {
      if (isRunning) {
        Text(
          text = currentWordItem.name,
          color = currentInkItem.color,
          fontSize = 48.sp,
          fontWeight = FontWeight.Black
        )
      } else if (isCompleted) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "¡Ronda Finalizada!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ElectricGold)
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "Puntaje Final: $score pts", fontSize = 20.sp, fontWeight = FontWeight.Black, color = ElectricGreen)
        }
      } else {
        Text(text = "🧠 ¿Listo?", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Color buttons 2x2 grid
    if (isRunning) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          ColorButton(GameColors[0], Modifier.weight(1f)) {
            checkAnswer(GameColors[0], currentInkItem, context) { isCorrect ->
              if (isCorrect) {
                score += 100 + streak * 20
                streak++
              } else {
                streak = 0
                score = (score - 50).coerceAtLeast(0)
              }
              nextQuestion()
            }
          }
          ColorButton(GameColors[1], Modifier.weight(1f)) {
            checkAnswer(GameColors[1], currentInkItem, context) { isCorrect ->
              if (isCorrect) {
                score += 100 + streak * 20
                streak++
              } else {
                streak = 0
                score = (score - 50).coerceAtLeast(0)
              }
              nextQuestion()
            }
          }
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          ColorButton(GameColors[2], Modifier.weight(1f)) {
            checkAnswer(GameColors[2], currentInkItem, context) { isCorrect ->
              if (isCorrect) {
                score += 100 + streak * 20
                streak++
              } else {
                streak = 0
                score = (score - 50).coerceAtLeast(0)
              }
              nextQuestion()
            }
          }
          ColorButton(GameColors[3], Modifier.weight(1f)) {
            checkAnswer(GameColors[3], currentInkItem, context) { isCorrect ->
              if (isCorrect) {
                score += 100 + streak * 20
                streak++
              } else {
                streak = 0
                score = (score - 50).coerceAtLeast(0)
              }
              nextQuestion()
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    if (!isRunning) {
      Button(
        onClick = {
          HapticHelper.click(context)
          score = 0
          streak = 0
          isCompleted = false
          isRunning = true
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(64.dp)
          .testTag("start_color_button"),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = if (isCompleted) "Jugar Otra Vez" else "¡EMPEZAR!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

private fun checkAnswer(
  selected: ColorItem,
  correct: ColorItem,
  context: android.content.Context,
  onResult: (Boolean) -> Unit
) {
  if (selected.name == correct.name) {
    HapticHelper.tap(context)
    onResult(true)
  } else {
    HapticHelper.buzzer(context)
    onResult(false)
  }
}

@Composable
private fun ColorButton(item: ColorItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
  Button(
    onClick = onClick,
    modifier = modifier.height(64.dp),
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = item.color,
      contentColor = Color.Black
    )
  ) {
    Text(text = item.name, fontWeight = FontWeight.Black, fontSize = 16.sp)
  }
}

@Composable
private fun GameColorDuelView(
  player1Name: String,
  player2Name: String,
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean) -> Unit
) {
  val context = LocalContext.current
  var p1Score by remember { mutableIntStateOf(0) }
  var p2Score by remember { mutableIntStateOf(0) }
  var isDuelActive by remember { mutableStateOf(false) }
  var winner by remember { mutableStateOf<String?>(null) }

  var currentWordItem by remember { mutableStateOf(GameColors[0]) }
  var currentInkItem by remember { mutableStateOf(GameColors[1]) }

  fun nextQuestion() {
    val word = GameColors[Random.nextInt(GameColors.size)]
    var ink = GameColors[Random.nextInt(GameColors.size)]
    while (ink == word) {
      ink = GameColors[Random.nextInt(GameColors.size)]
    }
    currentWordItem = word
    currentInkItem = ink
  }

  fun handleDuelAnswer(isPlayer1: Boolean, selected: ColorItem) {
    if (!isDuelActive) return
    if (selected.name == currentInkItem.name) {
      HapticHelper.tap(context)
      if (isPlayer1) p1Score++ else p2Score++
      if (p1Score >= 5 || p2Score >= 5) {
        isDuelActive = false
        winner = if (p1Score >= 5) player1Name else player2Name
        HapticHelper.success(context)
        onRecordResult(p1Score * 200, "Duelo Stroop: $player1Name ($p1Score) vs $player2Name ($p2Score)", player2Name, p1Score >= 5)
      } else {
        nextQuestion()
      }
    } else {
      HapticHelper.buzzer(context)
      // Penalty: deduct point if >0
      if (isPlayer1) p1Score = (p1Score - 1).coerceAtLeast(0) else p2Score = (p2Score - 1).coerceAtLeast(0)
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
  ) {
    // Player 2 Area (Top, rotated 180°)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .graphicsLayer(rotationZ = 180f)
        .background(Color(0xFF1A1224))
        .padding(12.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = player2Name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeonPink)
          Text(text = "$p2Score / 5 pts", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White)
        }

        // Color buttons for P2
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          GameColors.forEach { item ->
            Button(
              onClick = { handleDuelAnswer(isPlayer1 = false, selected = item) },
              modifier = Modifier.weight(1f).height(54.dp),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = item.color, contentColor = Color.Black)
            ) {
              Text(text = item.name.take(3), fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
          }
        }
      }
    }

    // Center Display with Word and Controls
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(130.dp)
        .background(Color(0xFF10121C))
        .border(1.dp, Color(0xFF282C40))
        .padding(12.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (!isDuelActive && winner == null) {
          Button(
            onClick = {
              HapticHelper.click(context)
              p1Score = 0
              p2Score = 0
              winner = null
              nextQuestion()
              isDuelActive = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("¡INICIAR DUELO (PRIMERO A 5)!", fontWeight = FontWeight.Bold)
          }
        } else if (winner != null) {
          Text(text = "👑 ¡$winner GANA EL DUELO!", color = ElectricGold, fontWeight = FontWeight.Black, fontSize = 16.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Button(
            onClick = {
              p1Score = 0
              p2Score = 0
              winner = null
              nextQuestion()
              isDuelActive = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Revancha 🔄", fontWeight = FontWeight.Bold)
          }
        } else {
          Text(
            text = currentWordItem.name,
            color = currentInkItem.color,
            fontSize = 38.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "¡Toca la tinta en tu lado!",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }
      }
    }

    // Player 1 Area (Bottom, normal)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .background(Color(0xFF0F1A24))
        .padding(12.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = player1Name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeonCyan)
          Text(text = "$p1Score / 5 pts", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White)
        }

        // Color buttons for P1
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          GameColors.forEach { item ->
            Button(
              onClick = { handleDuelAnswer(isPlayer1 = true, selected = item) },
              modifier = Modifier.weight(1f).height(54.dp),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = item.color, contentColor = Color.Black)
            ) {
              Text(text = item.name.take(3), fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
          }
        }
      }
    }
  }
}
