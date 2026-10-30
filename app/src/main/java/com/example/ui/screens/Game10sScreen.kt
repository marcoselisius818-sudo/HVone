package com.example.ui.screens

import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameType
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.MagentaGlow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.HapticHelper
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.abs

@Composable
fun Game10sScreen(
  isDuel: Boolean,
  player1Name: String = "Tú",
  player2Name: String = "Rival",
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean, diffMs: Long) -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current

  if (isDuel) {
    Game10sDuelView(
      player1Name = player1Name,
      player2Name = player2Name,
      onBack = onBack,
      onRecordResult = onRecordResult
    )
  } else {
    Game10sSoloView(
      onBack = onBack,
      onRecordResult = onRecordResult
    )
  }
}

@Composable
private fun Game10sSoloView(
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean, diffMs: Long) -> Unit
) {
  val context = LocalContext.current
  var isRunning by remember { mutableStateOf(false) }
  var startTime by remember { mutableLongStateOf(0L) }
  var elapsedTime by remember { mutableLongStateOf(0L) }
  var stoppedTime by remember { mutableLongStateOf(0L) }
  var isCompleted by remember { mutableStateOf(false) }

  // Timer loop
  LaunchedEffect(isRunning) {
    if (isRunning) {
      startTime = SystemClock.elapsedRealtime()
      while (isRunning) {
        elapsedTime = SystemClock.elapsedRealtime() - startTime
        delay(16) // ~60fps
      }
    }
  }

  val displaySeconds = elapsedTime / 1000f
  val isBlind = isRunning && elapsedTime >= 5000L

  val diffMs = abs(stoppedTime - 10000L)
  val score = if (isCompleted) {
    val raw = 10000 - diffMs * 2
    raw.coerceIn(0, 10000).toInt()
  } else 0

  val rankTitle = when {
    diffMs <= 25 -> "👑 ¡DIOS DE TIKTOK!"
    diffMs <= 80 -> "🔥 ¡VIRAL MAESTRO!"
    diffMs <= 200 -> "⚡ ¡OJO DE HALCÓN!"
    diffMs <= 500 -> "✨ ¡MUY CERCA!"
    else -> "😅 ¡SIGUE PRACTICANDO!"
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier.testTag("back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Volver",
          tint = TextPrimary
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "Reto Viral 10.00s",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Instruction pill
    Card(
      colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "🎯", fontSize = 24.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = "A los 5.00s la pantalla se apaga. ¡Usa tu ritmo y frena exactamente en 10.000s!",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Main Timer Display Box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .clip(RoundedCornerShape(24.dp))
        .background(
          Brush.verticalGradient(
            colors = listOf(
              SurfaceDark,
              if (isBlind) Color(0xFF1E0A1E) else SurfaceVariantDark
            )
          )
        )
        .border(
          width = 2.dp,
          brush = Brush.horizontalGradient(
            colors = if (isBlind) listOf(NeonPink, MagentaGlow) else listOf(NeonCyan, NeonPink)
          ),
          shape = RoundedCornerShape(24.dp)
        ),
      contentAlignment = Alignment.Center
    ) {
      if (isCompleted) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = String.format(Locale.US, "%.3fs", stoppedTime / 1000f),
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = if (diffMs <= 50) ElectricGold else NeonCyan
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = if (stoppedTime >= 10000) "+${diffMs}ms de exceso" else "-${diffMs}ms antes",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (diffMs <= 100) ElectricGreen else NeonPink
          )
        }
      } else if (isBlind) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "? ? . ? ? ?",
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = NeonPink
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "⚡ ¡A CIEGAS! ¡SIENTE EL TIEMPO! ⚡",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      } else {
        Text(
          text = String.format(Locale.US, "%.3fs", displaySeconds),
          fontSize = 50.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          color = NeonCyan
        )
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Result card if completed
    AnimatedVisibility(
      visible = isCompleted,
      enter = fadeIn() + scaleIn()
    ) {
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = rankTitle,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = ElectricGold
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Puntaje", fontSize = 12.sp, color = TextMuted)
              Text(
                text = "+$score pts",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricGreen
              )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Precisión", fontSize = 12.sp, color = TextMuted)
              Text(
                text = "${diffMs}ms",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Giant Action Button
    if (!isRunning && !isCompleted) {
      Button(
        onClick = {
          HapticHelper.click(context)
          isRunning = true
          isCompleted = false
          elapsedTime = 0L
          stoppedTime = 0L
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(72.dp)
          .testTag("start_10s_button"),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = NeonCyan,
          contentColor = Color.Black
        )
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "¡COMENZAR DESAFÍO!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
      }
    } else if (isRunning) {
      Button(
        onClick = {
          HapticHelper.success(context)
          isRunning = false
          stoppedTime = elapsedTime
          isCompleted = true
          val isWin = diffMs <= 150L
          onRecordResult(
            score,
            String.format(Locale.US, "%.3fs (dif: %dms)", stoppedTime / 1000f, diffMs),
            "Desafío Solo",
            isWin,
            diffMs
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(80.dp)
          .testTag("stop_10s_button"),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = NeonPink,
          contentColor = Color.White
        )
      ) {
        Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(36.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = "¡¡ PARAR AHORA !!", fontSize = 22.sp, fontWeight = FontWeight.Black)
      }
    } else {
      // Completed, Reintentar
      Button(
        onClick = {
          HapticHelper.click(context)
          isCompleted = false
          isRunning = true
          elapsedTime = 0L
          stoppedTime = 0L
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(64.dp)
          .testTag("retry_10s_button"),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = SurfaceVariantDark,
          contentColor = NeonCyan
        )
      ) {
        Icon(Icons.Default.Refresh, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Jugar de Nuevo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun Game10sDuelView(
  player1Name: String,
  player2Name: String,
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean, diffMs: Long) -> Unit
) {
  val context = LocalContext.current
  var isDuelRunning by remember { mutableStateOf(false) }
  var duelStartTime by remember { mutableLongStateOf(0L) }
  var duelElapsedTime by remember { mutableLongStateOf(0L) }

  var p1StoppedTime by remember { mutableLongStateOf(0L) }
  var p1HasStopped by remember { mutableStateOf(false) }

  var p2StoppedTime by remember { mutableLongStateOf(0L) }
  var p2HasStopped by remember { mutableStateOf(false) }

  var roundFinished by remember { mutableStateOf(false) }

  LaunchedEffect(isDuelRunning) {
    if (isDuelRunning) {
      duelStartTime = SystemClock.elapsedRealtime()
      while (isDuelRunning) {
        duelElapsedTime = SystemClock.elapsedRealtime() - duelStartTime
        if (p1HasStopped && p2HasStopped) {
          isDuelRunning = false
          roundFinished = true
        }
        delay(16)
      }
    }
  }

  val isBlind = isDuelRunning && duelElapsedTime >= 5000L

  val p1Diff = abs(p1StoppedTime - 10000L)
  val p2Diff = abs(p2StoppedTime - 10000L)
  val p1Won = roundFinished && p1Diff <= p2Diff

  // Record outcome when round finished
  LaunchedEffect(roundFinished) {
    if (roundFinished) {
      HapticHelper.success(context)
      val p1Score = (10000 - p1Diff * 2).coerceIn(0, 10000).toInt()
      onRecordResult(
        p1Score,
        "Duelo 10s: P1=%.3fs vs P2=%.3fs".format(Locale.US, p1StoppedTime / 1000f, p2StoppedTime / 1000f),
        player2Name,
        p1Won,
        p1Diff
      )
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
  ) {
    // Top Half: Player 2 (Rotated 180° for same-device face-off)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .graphicsLayer(rotationZ = 180f)
        .background(if (roundFinished && !p1Won) Color(0xFF1B3B24) else Color(0xFF141624))
        .padding(16.dp)
    ) {
      DuelPlayerHalf(
        playerName = player2Name,
        isBlind = isBlind,
        elapsedTime = duelElapsedTime,
        stoppedTime = p2StoppedTime,
        hasStopped = p2HasStopped,
        isWinner = roundFinished && !p1Won,
        isRoundFinished = roundFinished,
        buttonColor = NeonPink,
        onStop = {
          if (isDuelRunning && !p2HasStopped) {
            HapticHelper.tap(context)
            p2StoppedTime = duelElapsedTime
            p2HasStopped = true
          }
        }
      )
    }

    // Center divider bar with Start / Restart controls
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .background(Color(0xFF202334))
        .padding(horizontal = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Salir", tint = TextSecondary)
        }

        if (!isDuelRunning && !roundFinished) {
          Button(
            onClick = {
              HapticHelper.click(context)
              p1StoppedTime = 0L
              p2StoppedTime = 0L
              p1HasStopped = false
              p2HasStopped = false
              roundFinished = false
              duelElapsedTime = 0L
              isDuelRunning = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(text = "¡LISTOS! INICIAR DUELO", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        } else if (roundFinished) {
          Button(
            onClick = {
              HapticHelper.click(context)
              p1StoppedTime = 0L
              p2StoppedTime = 0L
              p1HasStopped = false
              p2HasStopped = false
              roundFinished = false
              duelElapsedTime = 0L
              isDuelRunning = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(text = "REVANCHA 🔄", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        } else {
          Text(
            text = if (isBlind) "¡¡ A CIEGAS EN 10.00s !!" else "¡CRONÓMETRO CORRIENDO!",
            color = if (isBlind) NeonPink else NeonCyan,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
        }

        Text(text = "1v1", fontWeight = FontWeight.Black, color = TextMuted)
      }
    }

    // Bottom Half: Player 1 (Facing normal)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .background(if (roundFinished && p1Won) Color(0xFF1B3B24) else Color(0xFF161828))
        .padding(16.dp)
    ) {
      DuelPlayerHalf(
        playerName = player1Name,
        isBlind = isBlind,
        elapsedTime = duelElapsedTime,
        stoppedTime = p1StoppedTime,
        hasStopped = p1HasStopped,
        isWinner = roundFinished && p1Won,
        isRoundFinished = roundFinished,
        buttonColor = NeonCyan,
        onStop = {
          if (isDuelRunning && !p1HasStopped) {
            HapticHelper.tap(context)
            p1StoppedTime = duelElapsedTime
            p1HasStopped = true
          }
        }
      )
    }
  }
}

@Composable
private fun DuelPlayerHalf(
  playerName: String,
  isBlind: Boolean,
  elapsedTime: Long,
  stoppedTime: Long,
  hasStopped: Boolean,
  isWinner: Boolean,
  isRoundFinished: Boolean,
  buttonColor: Color,
  onStop: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = playerName,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = buttonColor
      )
      if (isWinner) {
        Text(
          text = "👑 ¡GANADOR!",
          fontWeight = FontWeight.Black,
          fontSize = 16.sp,
          color = ElectricGold
        )
      } else if (isRoundFinished) {
        Text(
          text = "Diferencia: ${abs(stoppedTime - 10000L)}ms",
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp,
          color = TextSecondary
        )
      }
    }

    // Timer display
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(90.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFF0F101A)),
      contentAlignment = Alignment.Center
    ) {
      if (hasStopped) {
        Text(
          text = String.format(Locale.US, "%.3fs", stoppedTime / 1000f),
          fontSize = 32.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          color = if (isWinner) ElectricGold else TextPrimary
        )
      } else if (isBlind) {
        Text(
          text = "??.???s",
          fontSize = 32.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          color = NeonPink
        )
      } else {
        Text(
          text = String.format(Locale.US, "%.3fs", elapsedTime / 1000f),
          fontSize = 32.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          color = buttonColor
        )
      }
    }

    // Giant STOP button
    Button(
      onClick = onStop,
      enabled = !hasStopped,
      modifier = Modifier
        .fillMaxWidth()
        .height(72.dp),
      shape = RoundedCornerShape(18.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = if (hasStopped) Color.DarkGray else buttonColor,
        contentColor = Color.Black
      )
    ) {
      Text(
        text = if (hasStopped) "¡DETENIDO!" else "¡FRENAR!",
        fontSize = 20.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}
