package com.example.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.local.DailyRecordEntity
import com.example.data.local.MatchHistoryEntity
import com.example.data.local.UserProfileEntity
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

val AvatarOptions = listOf("🔥", "⚡", "👑", "🚀", "💎", "🌸", "🎯", "🌪️", "🦄", "🐱", "🎮", "🕶️")

@Composable
fun ProfileScreen(
  profile: UserProfileEntity?,
  matches: List<MatchHistoryEntity>,
  dailyRecords: List<DailyRecordEntity>,
  signedInAccount: String?,
  syncStatus: String?,
  onGoogleSignIn: () -> Unit,
  onGoogleSignOut: () -> Unit,
  onUpdateProfile: (nickname: String, avatar: String) -> Unit
) {
  val context = LocalContext.current
  var showEditDialog by remember { mutableStateOf(false) }
  var historyTab by remember { mutableIntStateOf(0) } // 0 = Partidas, 1 = Días Anteriores
  var filterCategory by remember { mutableStateOf("TODAS") } // TODAS, VICTORIAS, TORNEOS

  val user = profile ?: UserProfileEntity()

  val totalGames = user.totalWins + user.totalLosses
  val winRate = if (totalGames > 0) (user.totalWins * 100 / totalGames) else 0
  val xpInCurrentLevel = user.xp % 250
  val xpProgress = xpInCurrentLevel / 250f

  val filteredMatches = remember(matches, filterCategory) {
    when (filterCategory) {
      "VICTORIAS" -> matches.filter { it.isWin }
      "TORNEOS" -> matches.filter { it.isTournament }
      else -> matches
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Perfil Personal",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Black,
          color = TextPrimary
        )

        Button(
          onClick = {
            HapticHelper.click(context)
            showEditDialog = true
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = NeonCyan),
          modifier = Modifier.testTag("edit_profile_button")
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Editar", fontWeight = FontWeight.Bold)
        }
      }
    }

    // Main Profile Card
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(
            1.dp,
            Brush.horizontalGradient(listOf(NeonPink, NeonCyan)),
            RoundedCornerShape(22.dp)
          )
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color(0xFF231428))
                .border(2.dp, NeonPink, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(text = user.avatar, fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = user.nickname,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = TextPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Nivel ${user.level} • Jugador Pro",
                fontSize = 13.sp,
                color = ElectricGold,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // XP Progress
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Progreso de Nivel", fontSize = 11.sp, color = TextMuted)
            Text(text = "$xpInCurrentLevel / 250 XP", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(4.dp))
          LinearProgressIndicator(
            progress = { xpProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = NeonCyan,
            trackColor = SurfaceVariantDark,
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Stats badges row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            StatBadge(label = "Racha Diaria", value = "🔥 ${user.streakDays}d")
            StatBadge(label = "Diamantes", value = "💎 ${user.diamonds}")
            StatBadge(label = "Trofeos", value = "🏆 ${user.tournamentTrophies}")
            StatBadge(label = "Puntos Hoy", value = "⚡ ${user.dailyScore}")
          }
        }
      }
    }

    // Simplified Progress Saving Card (Google Sign-In with auto-save)
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, Color(0xFF282C40), RoundedCornerShape(18.dp))
          .animateContentSize()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (signedInAccount != null) Icons.Default.CloudDone else Icons.Default.Person,
              contentDescription = null,
              tint = if (signedInAccount != null) ElectricGreen else NeonCyan,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Guardado de Progreso",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = if (signedInAccount != null) ElectricGreen else ElectricGold
            )
          }
          Spacer(modifier = Modifier.height(8.dp))

          if (signedInAccount != null) {
            Text(
              text = "Cuenta conectada: $signedInAccount\nTus datos, récords y trofeos quedan guardados automáticamente.",
              fontSize = 12.sp,
              color = TextSecondary,
              lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = {
                HapticHelper.click(context)
                onGoogleSignOut()
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = TextSecondary),
              modifier = Modifier.testTag("google_sign_out_button")
            ) {
              Text("Cerrar sesión", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          } else {
            Text(
              text = "Inicia sesión con tu cuenta de Google para que tus datos, récords y trofeos queden guardados automáticamente.",
              fontSize = 12.sp,
              color = TextSecondary,
              lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = {
                HapticHelper.click(context)
                onGoogleSignIn()
              },
              modifier = Modifier.fillMaxWidth().testTag("google_sign_in_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
            ) {
              Text("Iniciar sesión con Google", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }

          if (!syncStatus.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = syncStatus,
              fontSize = 12.sp,
              color = ElectricGreen,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }

    // Career Stats 2x2 Grid
    item {
      Text(
        text = "Estadísticas de Competencia",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = TextPrimary
      )
    }

    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatCard(title = "Victorias", value = "${user.totalWins}", icon = "⚔️", modifier = Modifier.weight(1f), color = ElectricGreen)
          StatCard(title = "Win Rate", value = "$winRate%", icon = "📈", modifier = Modifier.weight(1f), color = ElectricGold)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          val bestDiff = if (user.best10sDiffMs < 9999) "${user.best10sDiffMs}ms" else "--"
          StatCard(title = "Récord 10.00s", value = bestDiff, icon = "⏱️", modifier = Modifier.weight(1f), color = NeonCyan)
          val bestTps = if (user.bestTapPerSec > 0) "%.1f tps".format(user.bestTapPerSec) else "--"
          StatCard(title = "Velocidad Taps", value = bestTps, icon = "⚡", modifier = Modifier.weight(1f), color = NeonPink)
        }
      }
    }

    // Tabs: Historial de Partidas vs Registros Diarios
    item {
      TabRow(
        selectedTabIndex = historyTab,
        containerColor = SurfaceDark,
        contentColor = NeonCyan,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[historyTab]),
            color = NeonPink
          )
        },
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
      ) {
        Tab(
          selected = historyTab == 0,
          onClick = {
            HapticHelper.click(context)
            historyTab = 0
          },
          text = { Text("Historial de Partidas", fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = historyTab == 1,
          onClick = {
            HapticHelper.click(context)
            historyTab = 1
          },
          text = { Text("Días Guardados", fontWeight = FontWeight.Bold) }
        )
      }
    }

    if (historyTab == 0) {
      // Filter Chips
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("TODAS", "VICTORIAS", "TORNEOS").forEach { cat ->
            FilterChip(
              selected = filterCategory == cat,
              onClick = {
                HapticHelper.click(context)
                filterCategory = cat
              },
              label = { Text(cat.lowercase().replaceFirstChar { it.uppercase() }) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = NeonPink,
                selectedLabelColor = Color.White,
                containerColor = SurfaceDark,
                labelColor = TextSecondary
              )
            )
          }
        }
      }

      if (filteredMatches.isEmpty()) {
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = "🎮", fontSize = 32.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "No hay partidas registradas en esta categoría.",
                fontSize = 13.sp,
                color = TextSecondary
              )
            }
          }
        }
      } else {
        items(filteredMatches, key = { it.id }) { match ->
          Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .border(0.5.dp, Color(0xFF25293E), RoundedCornerShape(14.dp))
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(if (match.isWin) Color(0xFF16321F) else Color(0xFF321620)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = if (match.isWin) "👑" else "⚔️",
                  fontSize = 20.sp
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = match.scoreDetail,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = TextPrimary
                )
                Text(
                  text = "vs ${match.opponentName} • ${match.dateString}",
                  fontSize = 12.sp,
                  color = TextSecondary
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = if (match.isWin) "VICTORIA" else "DERROTA",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black,
                  color = if (match.isWin) ElectricGreen else NeonPink
                )
                Text(
                  text = "+${match.score} pts",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = ElectricGold
                )
              }
            }
          }
        }
      }
    } else {
      if (dailyRecords.isEmpty()) {
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = "📅", fontSize = 32.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Tu historial de días anteriores aparecerá aquí.",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                text = "Al finalizar cada día, el marcador diario se reinicia pero tus estadísticas quedan archivadas.",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      } else {
        items(dailyRecords, key = { it.dateString }) { record ->
          Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .border(0.5.dp, Color(0xFF282C40), RoundedCornerShape(14.dp))
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(
                  text = "Día: ${record.dateString}",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = TextPrimary
                )
                Text(
                  text = "Partidas jugadas: ${record.gamesPlayed} • ${record.peakRank}",
                  fontSize = 12.sp,
                  color = TextSecondary
                )
              }
              Text(
                text = "${record.totalDailyScore} pts",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = ElectricGold
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }

  // Edit Profile Dialog
  if (showEditDialog) {
    var editName by remember { mutableStateOf(user.nickname) }
    var editAvatar by remember { mutableStateOf(user.avatar) }

    AlertDialog(
      onDismissRequest = { showEditDialog = false },
      containerColor = SurfaceDark,
      title = {
        Text("Editar Perfil", fontWeight = FontWeight.Bold, color = TextPrimary)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Elige tu Avatar:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSecondary)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            AvatarOptions.take(6).forEach { av ->
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(if (editAvatar == av) NeonPink else SurfaceVariantDark)
                  .clickable {
                    HapticHelper.click(context)
                    editAvatar = av
                  },
                contentAlignment = Alignment.Center
              ) {
                Text(text = av, fontSize = 20.sp)
              }
            }
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            AvatarOptions.drop(6).take(6).forEach { av ->
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(if (editAvatar == av) NeonPink else SurfaceVariantDark)
                  .clickable {
                    HapticHelper.click(context)
                    editAvatar = av
                  },
                contentAlignment = Alignment.Center
              ) {
                Text(text = av, fontSize = 20.sp)
              }
            }
          }

          OutlinedTextField(
            value = editName,
            onValueChange = { editName = it },
            label = { Text("Nombre / Nickname") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = NeonCyan,
              unfocusedBorderColor = Color(0xFF333852),
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            HapticHelper.click(context)
            showEditDialog = false
            onUpdateProfile(editName, editAvatar)
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
        ) {
          Text("Guardar Cambios")
        }
      },
      dismissButton = {
        Button(
          onClick = { showEditDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = TextSecondary)
        ) {
          Text("Cancelar")
        }
      }
    )
  }
}

@Composable
private fun StatBadge(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = value, fontWeight = FontWeight.Black, fontSize = 14.sp, color = TextPrimary)
    Text(text = label, fontSize = 10.sp, color = TextMuted)
  }
}

@Composable
private fun StatCard(title: String, value: String, icon: String, modifier: Modifier = Modifier, color: Color) {
  Card(
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    shape = RoundedCornerShape(16.dp),
    modifier = modifier.border(1.dp, Color(0xFF24273A), RoundedCornerShape(16.dp))
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = icon, fontSize = 24.sp)
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(text = title, fontSize = 11.sp, color = TextMuted)
        Text(text = value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = color)
      }
    }
  }
}
