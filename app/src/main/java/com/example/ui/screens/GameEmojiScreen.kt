package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.util.HapticHelper
import com.example.ui.util.SoundHelper
import kotlinx.coroutines.delay
import kotlin.random.Random

data class FallingItem(
  val id: Long,
  val lane: Int, // 0..4
  val yPercent: Float = 0f, // 0.0 top to 1.0 bottom
  val emoji: String,
  val points: Int,
  val isBomb: Boolean = false
)

@Composable
fun GameEmojiScreen(
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean) -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val isMuted by SoundHelper.isMuted.collectAsStateWithLifecycle()

  var isPlaying by remember { mutableStateOf(false) }
  var isGameOver by remember { mutableStateOf(false) }
  var score by remember { mutableIntStateOf(0) }
  var lives by remember { mutableIntStateOf(3) }
  var basketLane by remember { mutableIntStateOf(2) } // 0..4
  var items by remember { mutableStateOf<List<FallingItem>>(emptyList()) }
  var nextId by remember { mutableStateOf(0L) }

  // Optimized 60 FPS game loop with immutable state updates
  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      items = emptyList()
      score = 0
      lives = 3
      basketLane = 2
      var frameTicker = 0

      while (isPlaying && lives > 0) {
        delay(16L) // ~60 FPS
        frameTicker++

        val updated = ArrayList<FallingItem>(items.size + 1)
        var currentLives = lives
        var currentScore = score

        // Spawn new item every ~28 frames (~450ms)
        if (frameTicker % 28 == 0) {
          val isBomb = Random.nextFloat() < 0.34f
          val emoji = if (isBomb) "💣" else listOf("💎", "🍒", "⭐").random()
          val pts = if (isBomb) 0 else if (emoji == "💎") 50 else if (emoji == "⭐") 30 else 15
          updated.add(
            FallingItem(
              id = nextId++,
              lane = Random.nextInt(5),
              yPercent = 0.0f,
              emoji = emoji,
              points = pts,
              isBomb = isBomb
            )
          )
        }

        for (item in items) {
          val nextY = item.yPercent + 0.017f
          if (nextY in 0.82f..0.95f && item.lane == basketLane) {
            if (item.isBomb) {
              HapticHelper.vibrateOnly(context, 140L)
              SoundHelper.playExplosion()
              currentLives--
            } else {
              HapticHelper.vibrateOnly(context, 15L)
              SoundHelper.playSuccess()
              currentScore += item.points
            }
          } else if (nextY <= 1.0f) {
            updated.add(item.copy(yPercent = nextY))
          }
        }

        items = updated
        score = currentScore
        if (currentLives != lives) {
          lives = currentLives
          if (currentLives <= 0) {
            isPlaying = false
            isGameOver = true
            SoundHelper.playBuzzer()
            onRecordResult(currentScore, "$currentScore pts atrapados", "Atrapa y Esquiva", currentScore >= 300)
          }
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
    // Header with Mute Toggle
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
          text = "Atrapa y Esquiva",
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

    Spacer(modifier = Modifier.height(12.dp))

    // Stats bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Vidas: " + "❤️".repeat(lives.coerceAtLeast(0)),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "💎 $score pts",
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
        color = NeonCyan
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Game Board Canvas (Single optimized 5-lane layout using graphicsLayer)
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .clip(RoundedCornerShape(20.dp))
        .background(SurfaceDark)
        .border(2.dp, SurfaceVariantDark, RoundedCornerShape(20.dp))
    ) {
      val boardHeightPx = with(LocalDensity.current) { (maxHeight - 44.dp).toPx() }

      Row(modifier = Modifier.fillMaxSize()) {
        for (laneIdx in 0..4) {
          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight()
              .border(0.5.dp, Color(0xFF1E2132))
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
              ) {
                if (isPlaying && basketLane != laneIdx) {
                  HapticHelper.tap(context)
                  basketLane = laneIdx
                }
              }
          ) {
            // Draw falling items in this lane using graphicsLayer (zero layout thrashing)
            items.forEach { falling ->
              if (falling.lane == laneIdx) {
                Box(
                  modifier = Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                      translationY = falling.yPercent * boardHeightPx
                    }
                ) {
                  Text(text = falling.emoji, fontSize = 26.sp)
                }
              }
            }

            // Draw Basket at bottom if this is active lane
            if (basketLane == laneIdx) {
              Box(
                modifier = Modifier
                  .align(Alignment.BottomCenter)
                  .padding(bottom = 16.dp)
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(NeonPink),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "🧺", fontSize = 24.sp)
              }
            }
          }
        }
      }

      // Start Overlay if not playing
      if (!isPlaying && !isGameOver) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC0B0C12)),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "💎 🍒 💣", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Toca las columnas para mover la cesta.\n¡Atrapa gemas y esquiva las bombas!",
              color = TextPrimary,
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(horizontal = 24.dp)
            )
          }
        }
      } else if (isGameOver) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xDD0B0C12)),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "💥 ¡JUEGO TERMINADO!", fontSize = 24.sp, fontWeight = FontWeight.Black, color = NeonPink)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Puntaje: $score pts", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ElectricGold)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Controls
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Button(
        onClick = {
          if (basketLane > 0) {
            HapticHelper.tap(context)
            basketLane--
          }
        },
        enabled = isPlaying && basketLane > 0,
        modifier = Modifier.weight(1f).height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = TextPrimary),
        shape = RoundedCornerShape(14.dp)
      ) {
        Text("⬅️ Izquierda", fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = {
          if (basketLane < 4) {
            HapticHelper.tap(context)
            basketLane++
          }
        },
        enabled = isPlaying && basketLane < 4,
        modifier = Modifier.weight(1f).height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = TextPrimary),
        shape = RoundedCornerShape(14.dp)
      ) {
        Text("Derecha ➡️", fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (!isPlaying) {
      Button(
        onClick = {
          HapticHelper.click(context)
          isGameOver = false
          isPlaying = true
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(60.dp)
          .testTag("start_emoji_button"),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = if (isGameOver) "Reintentar" else "¡COMENZAR PARTIDA!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
