package com.example.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfileEntity
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
import com.example.ui.viewmodel.AppScreen

@Composable
fun HomeScreen(
  profile: UserProfileEntity?,
  onNavigateTo: (AppScreen) -> Unit,
  onStartGame: (gameType: GameType, isDuel: Boolean) -> Unit
) {
  val context = LocalContext.current
  val user = profile ?: UserProfileEntity()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))
      // User Profile Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { onNavigateTo(AppScreen.PROFILE) }
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(SurfaceVariantDark)
              .border(1.5.dp, NeonPink, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(text = user.avatar, fontSize = 24.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = user.nickname,
              fontWeight = FontWeight.Black,
              fontSize = 17.sp,
              color = TextPrimary
            )
            Text(
              text = "Nivel ${user.level} • Jugador Pro",
              fontSize = 11.sp,
              color = NeonCyan,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // Streak & Diamonds
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(SurfaceDark)
              .border(1.dp, Color(0xFF2C2F44), RoundedCornerShape(12.dp))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(text = "🔥 ${user.streakDays}d", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ElectricGold)
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(SurfaceDark)
              .border(1.dp, Color(0xFF2C2F44), RoundedCornerShape(12.dp))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(text = "💎 ${user.diamonds}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonCyan)
          }
        }
      }
    }

    // Hero Card: 1v1 Split Screen Duel on Same Device
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(
            1.5.dp,
            Brush.horizontalGradient(listOf(NeonCyan, NeonPink)),
            RoundedCornerShape(22.dp)
          )
          .clickable {
            HapticHelper.click(context)
            onNavigateTo(AppScreen.SPLIT_HUB)
          }
          .testTag("hero_1v1_card")
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF0C1929), Color(0xFF2B0E23))
              )
            )
            .padding(18.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(NeonPink)
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text(text = "MISMO DISPOSITIVO", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Duelo 1v1 Cara a Cara",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = TextPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Pantalla dividida con mitad invertida para competir de frente contra amigos.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
              )
              Spacer(modifier = Modifier.height(14.dp))
              Button(
                onClick = {
                  HapticHelper.click(context)
                  onNavigateTo(AppScreen.SPLIT_HUB)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
              ) {
                Text(text = "¡JUGAR 1v1 AHORA!", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "⚔️", fontSize = 56.sp)
          }
        }
      }
    }

    // Featured Tournaments Card
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, Color(0xFF282C40), RoundedCornerShape(18.dp))
          .clickable {
            HapticHelper.click(context)
            onNavigateTo(AppScreen.TOURNAMENTS)
          }
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(50.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0xFF261E0A)),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "🏆", fontSize = 26.sp)
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Torneos Privados y Copa Diaria",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = TextPrimary
            )
            Text(
              text = "Crea tu sala con código o juega la copa con reinicio a medianoche",
              fontSize = 12.sp,
              color = TextSecondary
            )
          }
          Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ElectricGold)
        }
      }
    }

    item {
      Text(
        text = "Juegos de Fiesta y Retos para Amigos",
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        color = TextPrimary
      )
    }

    // Party Game 1: La Bomba
    item {
      GameHubItem(
        gameType = GameType.BOMBA,
        badge = "💣 TOP FIESTA",
        badgeColor = NeonPink,
        onPlaySolo = { onStartGame(GameType.BOMBA, false) },
        onPlayDuel = null
      )
    }

    // Party Game 2: Adivina la Palabra (En la Frente)
    item {
      GameHubItem(
        gameType = GameType.HEADS_UP,
        badge = "🎴 EN LA FRENTE",
        badgeColor = ElectricGreen,
        onPlaySolo = { onStartGame(GameType.HEADS_UP, false) },
        onPlayDuel = null
      )
    }

    // Party Game 3: Adivina el Número (Mayor o Menor)
    item {
      GameHubItem(
        gameType = GameType.GUESS_NUMBER,
        badge = "🔢 NÚMEROS",
        badgeColor = ElectricGold,
        onPlaySolo = { onStartGame(GameType.GUESS_NUMBER, false) },
        onPlayDuel = { onStartGame(GameType.GUESS_NUMBER, true) }
      )
    }

    // Mini-game 4: 10.00s Stop
    item {
      GameHubItem(
        gameType = GameType.STOP_10S,
        badge = "⏱️ RETO 10s",
        badgeColor = NeonCyan,
        onPlaySolo = { onStartGame(GameType.STOP_10S, false) },
        onPlayDuel = { onStartGame(GameType.STOP_10S, true) }
      )
    }

    // Mini-game 5: Tap War
    item {
      GameHubItem(
        gameType = GameType.TAP_BATTLE,
        badge = "⚡ RUSH",
        badgeColor = NeonPink,
        onPlaySolo = { onStartGame(GameType.TAP_BATTLE, false) },
        onPlayDuel = { onStartGame(GameType.TAP_BATTLE, true) }
      )
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

@Composable
private fun GameHubItem(
  gameType: GameType,
  badge: String,
  badgeColor: Color,
  onPlaySolo: () -> Unit,
  onPlayDuel: (() -> Unit)?
) {
  val context = LocalContext.current

  Card(
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    shape = RoundedCornerShape(18.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, Color(0xFF262A3E), RoundedCornerShape(18.dp))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = gameType.iconEmoji, fontSize = 28.sp)
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(text = gameType.title, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimary)
            Text(text = gameType.subtitle, fontSize = 12.sp, color = TextSecondary)
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(badgeColor.copy(alpha = 0.2f))
            .border(1.dp, badgeColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(text = badge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeColor)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = gameType.description,
        fontSize = 12.sp,
        color = TextMuted,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = {
            HapticHelper.click(context)
            onPlaySolo()
          },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = TextPrimary)
        ) {
          Text("Solo Récord", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        if (onPlayDuel != null) {
          Button(
            onClick = {
              HapticHelper.click(context)
              onPlayDuel()
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
          ) {
            Text("⚔️ Duelo 1v1", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }
  }
}
