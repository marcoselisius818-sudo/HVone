package com.example.ui.screens

import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.ui.util.SoundHelper
import kotlinx.coroutines.delay

@Composable
fun GameTapScreen(
  isDuel: Boolean,
  player1Name: String = "Tú",
  player2Name: String = "Rival",
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean, tapsPerSec: Float) -> Unit
) {
  BackHandler { onBack() }

  if (isDuel) {
    GameTapDuelView(
      player1Name = player1Name,
      player2Name = player2Name,
      onBack = onBack,
      onRecordResult = onRecordResult
    )
  } else {
    GameTapSoloView(
      onBack = onBack,
      onRecordResult = onRecordResult
    )
  }
}

@Composable
private fun GameTapSoloView(
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean, tapsPerSec: Float) -> Unit
) {
  val context = LocalContext.current
  val isMuted by SoundHelper.isMuted.collectAsStateWithLifecycle()
  var isRunning by remember { mutableStateOf(false) }
  var tapCount by remember { mutableIntStateOf(0) }
  var timeLeftMs by remember { mutableLongStateOf(5000L) }
  var isCompleted by remember { mutableStateOf(false) }

  LaunchedEffect(isRunning) {
    if (isRunning) {
      val startTime = SystemClock.elapsedRealtime()
      val duration = 5000L
      while (isRunning) {
        val elapsed = SystemClock.elapsedRealtime() - startTime
        val remaining = (duration - elapsed).coerceAtLeast(0L)
        timeLeftMs = remaining
        if (remaining <= 0L) {
          isRunning = false
          isCompleted = true
          HapticHelper.vibrateOnly(context, 90L)
          SoundHelper.playVictory()
          val tps = tapCount / 5.0f
          val score = tapCount * 120
          onRecordResult(
            score,
            "$tapCount taps en 5s (%.1f taps/s)".format(tps),
            "Tap Rush Solo",
            tapCount >= 35,
            tps
          )
        }
        delay(30)
      }
    }
  }

  val tps = if (isCompleted) tapCount / 5.0f else (tapCount / ((5000L - timeLeftMs) / 1000f).coerceAtLeast(0.1f))

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
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Tap Rush 5s",
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

    // Time Progress Bar
    LinearProgressIndicator(
      progress = { (timeLeftMs / 5000f).coerceIn(0f, 1f) },
      modifier = Modifier
        .fillMaxWidth()
        .height(10.dp)
        .clip(RoundedCornerShape(5.dp)),
      color = if (timeLeftMs < 2000L) NeonPink else NeonCyan,
      trackColor = SurfaceVariantDark,
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "Tiempo: ${(timeLeftMs / 1000f).coerceAtLeast(0f)}s",
        fontWeight = FontWeight.Bold,
        color = TextSecondary
      )
      Text(
        text = "Velocidad: %.1f Taps/s".format(tps),
        fontWeight = FontWeight.Bold,
        color = ElectricGold
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Interactive Tap Area
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .clip(RoundedCornerShape(24.dp))
        .background(
          Brush.verticalGradient(
            colors = listOf(
              SurfaceDark,
              if (isRunning) Color(0xFF28112C) else SurfaceVariantDark
            )
          )
        )
        .border(
          width = 3.dp,
          brush = Brush.horizontalGradient(listOf(NeonCyan, NeonPink)),
          shape = RoundedCornerShape(24.dp)
        )
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null
        ) {
          if (isRunning) {
            HapticHelper.tap(context)
            tapCount++
          }
        },
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (!isRunning && !isCompleted) {
          Text(text = "⚡", fontSize = 64.sp)
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "¡Toca Comenzar y golpea con tus dedos tan rápido como puedas!",
            textAlign = TextAlign.Center,
            color = TextSecondary,
            fontSize = 15.sp,
            modifier = Modifier.padding(horizontal = 32.dp)
          )
        } else if (isRunning) {
          Text(
            text = "$tapCount",
            fontSize = 80.sp,
            fontWeight = FontWeight.Black,
            color = if (tapCount > 40) ElectricGold else NeonCyan
          )
          Text(
            text = "¡TOCA AQUÍ RÁPIDO!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = NeonPink
          )
        } else {
          // Completed
          Text(text = "🔥 ¡TIEMPO!", fontSize = 36.sp, fontWeight = FontWeight.Black, color = ElectricGold)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "$tapCount Taps",
            fontSize = 56.sp,
            fontWeight = FontWeight.Black,
            color = NeonCyan
          )
          Text(
            text = "Promedio: %.1f Taps por Segundo".format(tps),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricGreen
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    if (!isRunning && !isCompleted) {
      Button(
        onClick = {
          HapticHelper.click(context)
          tapCount = 0
          timeLeftMs = 5000L
          isRunning = true
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(64.dp)
          .testTag("start_tap_button"),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "¡INICIAR RUSH!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
      }
    } else if (isCompleted) {
      Button(
        onClick = {
          HapticHelper.click(context)
          tapCount = 0
          timeLeftMs = 5000L
          isCompleted = false
          isRunning = true
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(64.dp)
          .testTag("retry_tap_button"),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
      ) {
        Icon(Icons.Default.Refresh, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Intentar de Nuevo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun GameTapDuelView(
  player1Name: String,
  player2Name: String,
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean, tapsPerSec: Float) -> Unit
) {
  val context = LocalContext.current
  var isDuelActive by remember { mutableStateOf(false) }
  var tugPosition by remember { mutableFloatStateOf(0.5f) } // 0.0 = P2 wins, 1.0 = P1 wins
  var p1Taps by remember { mutableIntStateOf(0) }
  var p2Taps by remember { mutableIntStateOf(0) }
  var duelTimeLeft by remember { mutableIntStateOf(15) }
  var winner by remember { mutableStateOf<String?>(null) }

  // Timer loop
  LaunchedEffect(isDuelActive) {
    if (isDuelActive) {
      duelTimeLeft = 15
      while (duelTimeLeft > 0 && isDuelActive) {
        delay(1000)
        duelTimeLeft--
        if (duelTimeLeft == 0) {
          isDuelActive = false
          winner = if (tugPosition > 0.5f) player1Name else if (tugPosition < 0.5f) player2Name else "¡EMPATE!"
          HapticHelper.success(context)
          val p1Won = winner == player1Name
          val tps = p1Taps / 15.0f
          onRecordResult(
            p1Taps * 100,
            "Tug-of-Tap: $player1Name ($p1Taps) vs $player2Name ($p2Taps)",
            player2Name,
            p1Won,
            tps
          )
        }
      }
    }
  }

  // Knockout condition
  LaunchedEffect(tugPosition) {
    if (isDuelActive) {
      if (tugPosition >= 0.95f) {
        isDuelActive = false
        winner = player1Name
        HapticHelper.success(context)
        onRecordResult(
          p1Taps * 150,
          "K.O. en Tug-of-Tap: $player1Name aplastó a $player2Name!",
          player2Name,
          true,
          p1Taps / 10f
        )
      } else if (tugPosition <= 0.05f) {
        isDuelActive = false
        winner = player2Name
        HapticHelper.buzzer(context)
        onRecordResult(
          p1Taps * 50,
          "Derrota K.O. en Tug-of-Tap contra $player2Name",
          player2Name,
          false,
          p1Taps / 10f
        )
      }
    }
  }

  val animatedTug by animateFloatAsState(targetValue = tugPosition, label = "tug")

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
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(0xFF260D1E),
              if (winner == player2Name) Color(0xFF143B1E) else Color(0xFF181024)
            )
          )
        )
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null
        ) {
          if (isDuelActive) {
            HapticHelper.tap(context)
            p2Taps++
            tugPosition = (tugPosition - 0.025f).coerceIn(0.0f, 1.0f)
          }
        }
        .padding(16.dp)
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
          Text(text = player2Name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = NeonPink)
          Text(text = "$p2Taps Taps", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White)
        }
        Text(
          text = if (isDuelActive) "¡TOCA AQUÍ PARA EMPUJAR HACIA EL RIVAL!" else if (winner == player2Name) "👑 ¡¡GANADOR!!" else "Toca para ganar",
          fontWeight = FontWeight.Black,
          fontSize = 16.sp,
          color = NeonPink,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
      }
    }

    // Tug-of-War Center Bar & Controls
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF181A28))
        .padding(vertical = 8.dp, horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Salir", tint = TextSecondary)
        }

        if (!isDuelActive && winner == null) {
          Button(
            onClick = {
              HapticHelper.click(context)
              p1Taps = 0
              p2Taps = 0
              tugPosition = 0.5f
              winner = null
              isDuelActive = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(text = "¡INICIAR BATALLA!", fontWeight = FontWeight.Bold)
          }
        } else if (winner != null) {
          Button(
            onClick = {
              HapticHelper.click(context)
              p1Taps = 0
              p2Taps = 0
              tugPosition = 0.5f
              winner = null
              isDuelActive = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(text = "REVANCHA 🔄", fontWeight = FontWeight.Bold)
          }
        } else {
          Text(
            text = "⏳ ${duelTimeLeft}s restantes",
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = ElectricGold
          )
        }

        Text(text = "TUG-WAR", fontWeight = FontWeight.Bold, color = TextMuted)
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Rope Tug Visualizer
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(24.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF0F101A))
          .border(1.dp, Color(0xFF333852), RoundedCornerShape(12.dp))
      ) {
        // Player 2 color bar (left)
        Box(
          modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(fraction = (1f - animatedTug).coerceIn(0f, 1f))
            .align(Alignment.CenterStart)
            .background(Brush.horizontalGradient(listOf(NeonPink, MagentaGlow)))
        )

        // Player 1 color bar (right)
        Box(
          modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(fraction = animatedTug.coerceIn(0f, 1f))
            .align(Alignment.CenterEnd)
            .background(Brush.horizontalGradient(listOf(NeonCyan, Color(0xFF0072FF))))
        )

        // Center Marker
        Box(
          modifier = Modifier
            .width(4.dp)
            .fillMaxHeight()
            .align(Alignment.Center)
            .background(Color.White)
        )
      }
    }

    // Player 1 Area (Bottom, Normal orientation)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .background(
          Brush.verticalGradient(
            colors = listOf(
              if (winner == player1Name) Color(0xFF143B1E) else Color(0xFF101C26),
              Color(0xFF0A1822)
            )
          )
        )
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null
        ) {
          if (isDuelActive) {
            HapticHelper.tap(context)
            p1Taps++
            tugPosition = (tugPosition + 0.025f).coerceIn(0.0f, 1.0f)
          }
        }
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = if (isDuelActive) "¡TOCA AQUÍ PARA EMPUJAR HACIA EL RIVAL!" else if (winner == player1Name) "👑 ¡¡GANADOR!!" else "Toca para ganar",
          fontWeight = FontWeight.Black,
          fontSize = 16.sp,
          color = NeonCyan,
          textAlign = TextAlign.Center
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = player1Name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = NeonCyan)
          Text(text = "$p1Taps Taps", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White)
        }
      }
    }
  }
}
