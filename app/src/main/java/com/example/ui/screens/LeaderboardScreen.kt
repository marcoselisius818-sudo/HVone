package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfileEntity
import com.example.data.model.LeaderboardEntry
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale

@Composable
fun LeaderboardScreen(
  userProfile: UserProfileEntity?,
  entries: List<LeaderboardEntry>,
  selectedPeriod: String,
  onSelectPeriod: (String) -> Unit
) {
  // Live Countdown to midnight for daily reset
  var secondsUntilMidnight by remember { mutableLongStateOf(0L) }

  LaunchedEffect(Unit) {
    while (true) {
      val cal = Calendar.getInstance()
      val now = cal.timeInMillis
      cal.set(Calendar.HOUR_OF_DAY, 24)
      cal.set(Calendar.MINUTE, 0)
      cal.set(Calendar.SECOND, 0)
      val midnight = cal.timeInMillis
      secondsUntilMidnight = ((midnight - now) / 1000).coerceAtLeast(0)
      delay(1000)
    }
  }

  val hours = secondsUntilMidnight / 3600
  val minutes = (secondsUntilMidnight % 3600) / 60
  val seconds = secondsUntilMidnight % 60
  val countdownFormatted = String.format(Locale.US, "%02dh %02dm %02ds", hours, minutes, seconds)

  val userEntry = entries.find { it.isCurrentUser }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = ElectricGold, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Tablas de Clasificación",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Black,
          color = TextPrimary
        )
      }
    }

    // Daily Reset Countdown Banner
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, Color(0xFF2E334D), RoundedCornerShape(16.dp))
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Timer, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Reinicio de Marcador Diario",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
              )
              Text(
                text = "Se reinicia a medianoche",
                fontSize = 11.sp,
                color = TextMuted
              )
            }
          }

          Text(
            text = countdownFormatted,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = ElectricGold
          )
        }
      }
    }

    // Tabs: HOY, SEMANAL, HISTÓRICO
    item {
      val periods = listOf("HOY", "SEMANAL", "HISTORICO")
      val selectedIndex = periods.indexOf(selectedPeriod).coerceAtLeast(0)

      TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = SurfaceDark,
        contentColor = NeonCyan,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
            color = NeonPink
          )
        },
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
      ) {
        periods.forEachIndexed { index, period ->
          val label = when (period) {
            "HOY" -> "Hoy (Diario)"
            "SEMANAL" -> "Semanal"
            else -> "Histórico"
          }
          Tab(
            selected = selectedIndex == index,
            onClick = { onSelectPeriod(period) },
            text = {
              Text(
                text = label,
                fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedIndex == index) NeonPink else TextSecondary
              )
            }
          )
        }
      }
    }

    // User Sticky Rank Position Card
    if (userEntry != null) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(
              1.5.dp,
              Brush.horizontalGradient(listOf(NeonCyan, NeonPink)),
              RoundedCornerShape(18.dp)
            )
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF20263C)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "#${userEntry.rank}",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = ElectricGold
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = userEntry.avatar, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${userEntry.username} (Tú)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = userEntry.countryFlag, fontSize = 12.sp)
              }
              Text(
                text = userEntry.badge,
                fontSize = 11.sp,
                color = ElectricGold
              )
            }
            Text(
              text = "${userEntry.score} pts",
              fontWeight = FontWeight.Black,
              fontSize = 16.sp,
              color = NeonCyan
            )
          }
        }
      }
    }

    item {
      Text(
        text = "Ránking de Jugadores Reales",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = TextSecondary
      )
    }

    if (entries.size <= 1) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF2E334D), RoundedCornerShape(16.dp))
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "🎮 ¡Compite contra tus amigos!",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = NeonCyan
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Los amigos y rivales con los que juegues en Duelos 1v1, Torneos o La Bomba aparecerán registrados aquí automáticamente con sus puntajes reales.",
              fontSize = 12.sp,
              color = TextSecondary,
              textAlign = TextAlign.Center,
              lineHeight = 16.sp
            )
          }
        }
      }
    }

    // Leaderboard entries list
    items(entries) { entry ->
      val medalEmoji = when (entry.rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#${entry.rank}"
      }

      Card(
        colors = CardDefaults.cardColors(
          containerColor = if (entry.isCurrentUser) Color(0xFF1B2338) else SurfaceDark
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(
            width = if (entry.isCurrentUser) 1.dp else 0.5.dp,
            color = if (entry.isCurrentUser) NeonCyan else Color(0xFF222538),
            shape = RoundedCornerShape(14.dp)
          )
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier.width(36.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = medalEmoji,
              fontWeight = FontWeight.Black,
              fontSize = if (entry.rank <= 3) 20.sp else 14.sp,
              color = if (entry.rank == 1) ElectricGold else TextPrimary
            )
          }

          Spacer(modifier = Modifier.width(8.dp))
          Text(text = entry.avatar, fontSize = 24.sp)
          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = entry.username,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (entry.isCurrentUser) NeonCyan else TextPrimary
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = entry.countryFlag, fontSize = 12.sp)
            }
            Text(
              text = if (entry.isCurrentUser) "Tú" else "Rival",
              fontSize = 11.sp,
              color = TextMuted
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "${entry.score} pts",
              fontWeight = FontWeight.Black,
              fontSize = 15.sp,
              color = if (entry.rank == 1) ElectricGold else if (entry.isCurrentUser) NeonCyan else TextPrimary
            )
            Text(
              text = entry.badge,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = NeonPink
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}
