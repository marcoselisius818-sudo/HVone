package com.example.ui.screens

import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import java.util.Locale
import kotlin.math.abs
import kotlin.random.Random

enum class BlindTimerMode(val label: String, val fixedSeconds: Int?) {
  SEC_1("1.00s", 1),
  SEC_10("10.00s", 10),
  SEC_15("15.00s", 15),
  RANDOM_1_30("🎲 Azar (1-30s)", null)
}

@Composable
fun Game10sScreen(
  isDuel: Boolean,
  player1Name: String = "Tú",
  player2Name: String = "Rival",
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean, diffMs: Long) -> Unit
) {
  BackHandler { onBack() }

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
  val isMuted by SoundHelper.isMuted.collectAsStateWithLifecycle()

  var selectedMode by remember { mutableStateOf(BlindTimerMode.SEC_10) }
  var targetSeconds by remember { mutableIntStateOf(10) }
  var isRunning by remember { mutableStateOf(false) }
  var startTime by remember { mutableLongStateOf(0L) }
  var elapsedTime by remember { mutableLongStateOf(0L) }
  var stoppedTime by remember { mutableLongStateOf(0L) }
  var isCompleted by remember { mutableStateOf(false) }

  val targetTimeMs = targetSeconds * 1000L
  val isOneSecondMode = targetSeconds == 1
  val blindThresholdMs = if (isOneSecondMode) 0L else targetTimeMs / 2L

  // Timer loop
  LaunchedEffect(isRunning, targetTimeMs) {
    if (isRunning) {
      startTime = SystemClock.elapsedRealtime()
      var lastWholeSec = 0
      val maxVisibleTickSec = (blindThresholdMs / 1000L).toInt()
      while (isRunning) {
        elapsedTime = SystemClock.elapsedRealtime() - startTime
        val wholeSec = (elapsedTime / 1000L).toInt()
        if (!isOneSecondMode && wholeSec > lastWholeSec && wholeSec <= maxVisibleTickSec) {
          lastWholeSec = wholeSec
          SoundHelper.playTick(urgency = (wholeSec.toFloat() / maxVisibleTickSec.coerceAtLeast(1)).coerceIn(0f, 1f))
        }
        delay(16) // ~60fps
      }
    }
  }

  val displaySeconds = elapsedTime / 1000f
  val isFullyBlind = isRunning && (isOneSecondMode || elapsedTime >= blindThresholdMs)

  // Progressive fade-out alpha from 1.0f down to 0.0f up to half of targetTimeMs (except in 1s mode)
  val progressiveAlpha = if (!isRunning) {
    1f
  } else if (isOneSecondMode) {
    0f
  } else {
    (1f - (elapsedTime.toFloat() / blindThresholdMs.coerceAtLeast(1L).toFloat())).coerceIn(0f, 1f)
  }

  val diffMs = abs(stoppedTime - targetTimeMs)
  val score = if (isCompleted) {
    val raw = 10000 - diffMs * 2
    raw.coerceIn(0, 10000).toInt()
  } else 0

  val rankTitle = when {
    diffMs <= 25 -> "👑 ¡LEYENDA EXACTA!"
    diffMs <= 80 -> "🔥 ¡PRECISIÓN MAESTRA!"
    diffMs <= 200 -> "⚡ ¡OJO DE HALCÓN!"
    diffMs <= 500 -> "✨ ¡MUY CERCA!"
    else -> "😅 ¡SIGUE PRACTICANDO!"
  }

  fun startSoloChallenge() {
    targetSeconds = selectedMode.fixedSeconds ?: Random.nextInt(1, 31)
    elapsedTime = 0L
    stoppedTime = 0L
    isCompleted = false
    isRunning = true
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
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = {
            HapticHelper.click(context)
            onBack()
          },
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
          text = "Cronómetro a Ciegas",
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

    Spacer(modifier = Modifier.height(10.dp))

    // Mode Selector Chips (1s, 10s, 15s, Random 1-30s)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      BlindTimerMode.entries.forEach { mode ->
        val selected = selectedMode == mode
        FilterChip(
          selected = selected,
          enabled = !isRunning,
          onClick = {
            HapticHelper.click(context)
            selectedMode = mode
            targetSeconds = mode.fixedSeconds ?: Random.nextInt(1, 31)
            isCompleted = false
            elapsedTime = 0L
            stoppedTime = 0L
          },
          label = {
            Text(
              text = mode.label,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth()
            )
          },
          modifier = Modifier.weight(1f),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = NeonCyan,
            selectedLabelColor = Color.Black,
            containerColor = SurfaceDark,
            labelColor = TextSecondary
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Target & Instruction Banner
    Card(
      colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = if (selectedMode == BlindTimerMode.RANDOM_1_30) {
            "🎲 OBJETIVO AL AZAR: ${targetSeconds}.000s EXACTOS"
          } else {
            "🎯 OBJETIVO: ${targetSeconds}.000s EXACTOS"
          },
          fontWeight = FontWeight.Black,
          fontSize = 16.sp,
          color = ElectricGold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (isOneSecondMode) {
            "Modo 1.00s: Directamente a ciegas sin desvanecimiento. ¡Pura intuición!"
          } else {
            "El cronómetro se desvanece progresivamente hasta los ${String.format(Locale.US, "%.1fs", targetSeconds / 2f)} y queda en incógnito."
          },
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary,
          textAlign = TextAlign.Center
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

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
              if (isFullyBlind) Color(0xFF1E0A1E) else SurfaceVariantDark
            )
          )
        )
        .border(
          width = 2.dp,
          brush = Brush.horizontalGradient(
            colors = if (isFullyBlind) listOf(NeonPink, MagentaGlow) else listOf(NeonCyan, NeonPink)
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
            text = if (stoppedTime >= targetTimeMs) "+${diffMs}ms de exceso (Meta: ${targetSeconds}.000s)" else "-${diffMs}ms antes (Meta: ${targetSeconds}.000s)",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (diffMs <= 100) ElectricGreen else NeonPink
          )
        }
      } else if (isFullyBlind) {
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
            text = "⚡ ¡A CIEGAS! META: ${targetSeconds}.000s ⚡",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = String.format(Locale.US, "%.3fs", displaySeconds),
            fontSize = 50.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = NeonCyan,
            modifier = Modifier.alpha(progressiveAlpha)
          )
          if (isRunning && !isOneSecondMode) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Desvaneciéndose hacia modo incógnito...",
              fontSize = 11.sp,
              color = TextSecondary.copy(alpha = progressiveAlpha.coerceAtLeast(0.25f))
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

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
          startSoloChallenge()
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
        Text(
          text = if (selectedMode == BlindTimerMode.RANDOM_1_30) "¡SORTEAR META Y COMENZAR!" else "¡COMENZAR DESAFÍO (${targetSeconds}s)!",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold
        )
      }
    } else if (isRunning) {
      Button(
        onClick = {
          HapticHelper.success(context)
          isRunning = false
          stoppedTime = elapsedTime
          isCompleted = true
          val finalDiff = abs(stoppedTime - targetTimeMs)
          val finalScore = (10000 - finalDiff * 2).coerceIn(0, 10000).toInt()
          val isWin = finalDiff <= 150L
          onRecordResult(
            finalScore,
            String.format(Locale.US, "Meta %ds: %.3fs (dif: %dms)", targetSeconds, stoppedTime / 1000f, finalDiff),
            "Desafío ${targetSeconds}s",
            isWin,
            finalDiff
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
        Text(text = "¡¡ PARAR EN ${targetSeconds}.000s !!", fontSize = 20.sp, fontWeight = FontWeight.Black)
      }
    } else {
      // Completed, Reintentar
      Button(
        onClick = {
          HapticHelper.click(context)
          startSoloChallenge()
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
        Text(
          text = if (selectedMode == BlindTimerMode.RANDOM_1_30) "Nuevo Tiempo Al Azar" else "Jugar de Nuevo",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
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
  var selectedMode by remember { mutableStateOf(BlindTimerMode.SEC_10) }
  var targetSeconds by remember { mutableIntStateOf(10) }

  var isDuelRunning by remember { mutableStateOf(false) }
  var duelStartTime by remember { mutableLongStateOf(0L) }
  var duelElapsedTime by remember { mutableLongStateOf(0L) }

  var p1StoppedTime by remember { mutableLongStateOf(0L) }
  var p1HasStopped by remember { mutableStateOf(false) }

  var p2StoppedTime by remember { mutableLongStateOf(0L) }
  var p2HasStopped by remember { mutableStateOf(false) }

  var roundFinished by remember { mutableStateOf(false) }

  val targetTimeMs = targetSeconds * 1000L
  val isOneSecondMode = targetSeconds == 1
  val blindThresholdMs = if (isOneSecondMode) 0L else targetTimeMs / 2L

  LaunchedEffect(isDuelRunning, targetTimeMs) {
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

  val isFullyBlind = isDuelRunning && (isOneSecondMode || duelElapsedTime >= blindThresholdMs)
  val progressiveAlpha = if (!isDuelRunning) {
    1f
  } else if (isOneSecondMode) {
    0f
  } else {
    (1f - (duelElapsedTime.toFloat() / blindThresholdMs.coerceAtLeast(1L).toFloat())).coerceIn(0f, 1f)
  }

  val p1Diff = abs(p1StoppedTime - targetTimeMs)
  val p2Diff = abs(p2StoppedTime - targetTimeMs)
  val p1Won = roundFinished && p1Diff <= p2Diff

  fun launchDuelRound() {
    targetSeconds = selectedMode.fixedSeconds ?: Random.nextInt(1, 31)
    p1StoppedTime = 0L
    p2StoppedTime = 0L
    p1HasStopped = false
    p2HasStopped = false
    roundFinished = false
    duelElapsedTime = 0L
    isDuelRunning = true
  }

  // Record outcome when round finished
  LaunchedEffect(roundFinished) {
    if (roundFinished) {
      HapticHelper.success(context)
      val p1Score = (10000 - p1Diff * 2).coerceIn(0, 10000).toInt()
      onRecordResult(
        p1Score,
        "Duelo ${targetSeconds}s: P1=%.3fs vs P2=%.3fs".format(Locale.US, p1StoppedTime / 1000f, p2StoppedTime / 1000f),
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
        .padding(14.dp)
    ) {
      DuelPlayerHalf(
        playerName = player2Name,
        targetSeconds = targetSeconds,
        isFullyBlind = isFullyBlind,
        progressiveAlpha = progressiveAlpha,
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

    // Center divider bar with Mode Selector & Start / Restart controls
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF202334))
        .padding(horizontal = 12.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (!isDuelRunning) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          BlindTimerMode.entries.forEach { mode ->
            val selected = selectedMode == mode
            FilterChip(
              selected = selected,
              onClick = {
                HapticHelper.click(context)
                selectedMode = mode
                targetSeconds = mode.fixedSeconds ?: Random.nextInt(1, 31)
                roundFinished = false
              },
              label = {
                Text(
                  text = mode.label,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth()
                )
              },
              modifier = Modifier.weight(1f),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ElectricGold,
                selectedLabelColor = Color.Black,
                containerColor = Color(0xFF141624),
                labelColor = TextSecondary
              )
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = {
          HapticHelper.click(context)
          onBack()
        }) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Salir", tint = TextSecondary)
        }

        if (!isDuelRunning && !roundFinished) {
          Button(
            onClick = {
              HapticHelper.click(context)
              launchDuelRound()
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(
              text = if (selectedMode == BlindTimerMode.RANDOM_1_30) "¡DUELO AL AZAR (1-30s)!" else "¡INICIAR DUELO (${targetSeconds}s)!",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        } else if (roundFinished) {
          Button(
            onClick = {
              HapticHelper.click(context)
              launchDuelRound()
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(text = "REVANCHA 🔄", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        } else {
          Text(
            text = if (isFullyBlind) "¡¡ A CIEGAS EN ${targetSeconds}.00s !!" else "META: ${targetSeconds}.000s",
            color = if (isFullyBlind) NeonPink else NeonCyan,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
        }

        Text(text = "${targetSeconds}s", fontWeight = FontWeight.Black, color = ElectricGold)
      }
    }

    // Bottom Half: Player 1 (Facing normal)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .background(if (roundFinished && p1Won) Color(0xFF1B3B24) else Color(0xFF161828))
        .padding(14.dp)
    ) {
      DuelPlayerHalf(
        playerName = player1Name,
        targetSeconds = targetSeconds,
        isFullyBlind = isFullyBlind,
        progressiveAlpha = progressiveAlpha,
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
  targetSeconds: Int,
  isFullyBlind: Boolean,
  progressiveAlpha: Float,
  elapsedTime: Long,
  stoppedTime: Long,
  hasStopped: Boolean,
  isWinner: Boolean,
  isRoundFinished: Boolean,
  buttonColor: Color,
  onStop: () -> Unit
) {
  val targetTimeMs = targetSeconds * 1000L
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
        text = "$playerName (Meta: ${targetSeconds}s)",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
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
          text = "Dif: ${abs(stoppedTime - targetTimeMs)}ms",
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
        .height(82.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFF0F101A)),
      contentAlignment = Alignment.Center
    ) {
      if (hasStopped) {
        Text(
          text = String.format(Locale.US, "%.3fs", stoppedTime / 1000f),
          fontSize = 30.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          color = if (isWinner) ElectricGold else TextPrimary
        )
      } else if (isFullyBlind) {
        Text(
          text = "??.???s",
          fontSize = 30.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          color = NeonPink
        )
      } else {
        Text(
          text = String.format(Locale.US, "%.3fs", elapsedTime / 1000f),
          fontSize = 30.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          color = buttonColor,
          modifier = Modifier.alpha(progressiveAlpha)
        )
      }
    }

    // Giant STOP button
    Button(
      onClick = onStop,
      enabled = !hasStopped,
      modifier = Modifier
        .fillMaxWidth()
        .height(66.dp),
      shape = RoundedCornerShape(18.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = if (hasStopped) Color.DarkGray else buttonColor,
        contentColor = Color.Black
      )
    ) {
      Text(
        text = if (hasStopped) "¡DETENIDO!" else "¡FRENAR EN ${targetSeconds}.00s!",
        fontSize = 18.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}
