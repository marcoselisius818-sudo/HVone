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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TournamentEntity
import com.example.data.model.BracketMatch
import com.example.data.model.TournamentParticipant
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
import com.example.ui.viewmodel.AppScreen

@Composable
fun TournamentsScreen(
  savedTournaments: List<TournamentEntity>,
  onCreateTournament: (name: String, playerCount: Int, customNames: List<String>) -> Unit,
  onOpenBracket: (TournamentEntity) -> Unit
) {
  val context = LocalContext.current
  var showCreateDialog by remember { mutableStateOf(false) }
  var tournamentName by remember { mutableStateOf("Copa TikTok Amigos") }
  var playerCount by remember { mutableIntStateOf(4) }
  var friend1 by remember { mutableStateOf("Nico") }
  var friend2 by remember { mutableStateOf("Sofi") }
  var friend3 by remember { mutableStateOf("Lucas") }

  var joinCodeInput by remember { mutableStateOf("") }
  var joinStatusMessage by remember { mutableStateOf<String?>(null) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Torneos Privados",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = TextPrimary
          )
          Text(
            text = "Compite en llaves de eliminación y copas diarias",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
          )
        }

        Button(
          onClick = {
            HapticHelper.click(context)
            showCreateDialog = true
          },
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White),
          modifier = Modifier.testTag("create_tournament_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Crear", fontWeight = FontWeight.Bold)
        }
      }
    }

    // Daily Tournament Banner (Resets Daily)
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(
            1.dp,
            Brush.horizontalGradient(listOf(ElectricGold, NeonPink)),
            RoundedCornerShape(20.dp)
          )
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF261D0A), Color(0xFF1E1428))
              )
            )
            .padding(16.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "🏆 COPA DIARIA TIKTOK",
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = ElectricGold
              )
              Text(
                text = "Reinicio diario a medianoche",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Torneo exprés de 4 jugadores. ¡El ganador diario se lleva 200 diamantes y un Trofeo Oficial al perfil!",
              fontSize = 13.sp,
              color = TextPrimary,
              lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
              onClick = {
                HapticHelper.click(context)
                onCreateTournament("Copa Diaria TikTok", 4, listOf("ViralBot 1", "TikPro 2", "RushKing 3"))
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = ElectricGold, contentColor = Color.Black)
            ) {
              Text(text = "¡JUGAR COPA DIARIA!", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Join with Code Card
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Unirse a Torneo Privado con Código",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = NeonCyan
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = joinCodeInput,
              onValueChange = { joinCodeInput = it.uppercase() },
              placeholder = { Text("Ej: TK-4821") },
              modifier = Modifier.weight(1f).testTag("tournament_code_input"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color(0xFF333852),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              )
            )
            Button(
              onClick = {
                HapticHelper.click(context)
                val code = joinCodeInput.trim()
                if (code.isNotEmpty()) {
                  joinStatusMessage = "¡Conectado a la sala $code! Cargando llave..."
                  onCreateTournament("Torneo $code", 4, listOf("Amigo 1", "Amigo 2", "Amigo 3"))
                }
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
            ) {
              Text(text = "Entrar", fontWeight = FontWeight.Bold)
            }
          }

          if (joinStatusMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = joinStatusMessage!!, fontSize = 12.sp, color = ElectricGreen)
          }
        }
      }
    }

    item {
      Text(
        text = "Historial de Torneos Jugados",
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        color = TextPrimary
      )
    }

    if (savedTournaments.isEmpty()) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "🎯", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Aún no has jugado torneos privados.",
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = "Crea uno arriba o juega la Copa Diaria con amigos.",
              fontSize = 12.sp,
              color = TextSecondary
            )
          }
        }
      }
    } else {
      items(savedTournaments) { tourney ->
        Card(
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF282C40), RoundedCornerShape(16.dp))
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF241C10)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🏆", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = tourney.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
              )
              Text(
                text = "Código: ${tourney.roomCode} • ${tourney.playerCount} Jugadores",
                fontSize = 12.sp,
                color = TextSecondary
              )
              Text(
                text = "Fecha: ${tourney.dateString}",
                fontSize = 11.sp,
                color = TextMuted
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

  // Create Tournament Dialog
  if (showCreateDialog) {
    AlertDialog(
      onDismissRequest = { showCreateDialog = false },
      containerColor = SurfaceDark,
      title = {
        Text("Crear Nuevo Torneo Privado", fontWeight = FontWeight.Bold, color = TextPrimary)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = tournamentName,
            onValueChange = { tournamentName = it },
            label = { Text("Nombre del Torneo") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = NeonPink,
              unfocusedBorderColor = Color(0xFF333852),
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            )
          )

          Text("Cantidad de Jugadores:", fontWeight = FontWeight.Bold, color = TextSecondary, fontSize = 13.sp)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = { playerCount = 4 },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (playerCount == 4) NeonPink else SurfaceVariantDark,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("4 Jugadores (Semifinal)")
            }

            Button(
              onClick = { playerCount = 8 },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (playerCount == 8) NeonPink else SurfaceVariantDark,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("8 Jugadores")
            }
          }

          Text("Nombres de Amigos / Rivales:", fontWeight = FontWeight.Bold, color = TextSecondary, fontSize = 13.sp)
          OutlinedTextField(
            value = friend1,
            onValueChange = { friend1 = it },
            label = { Text("Jugador 2") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = friend2,
            onValueChange = { friend2 = it },
            label = { Text("Jugador 3") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = friend3,
            onValueChange = { friend3 = it },
            label = { Text("Jugador 4") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showCreateDialog = false
            onCreateTournament(
              tournamentName,
              playerCount,
              listOf(friend1, friend2, friend3)
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
        ) {
          Text("Comenzar Torneo")
        }
      },
      dismissButton = {
        Button(
          onClick = { showCreateDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = TextSecondary)
        ) {
          Text("Cancelar")
        }
      }
    )
  }
}

@Composable
fun TournamentBracketView(
  tournamentName: String,
  roomCode: String,
  matches: List<BracketMatch>,
  winner: TournamentParticipant?,
  onPlayMatch: (BracketMatch) -> Unit,
  onBack: () -> Unit
) {
  val context = LocalContext.current

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = tournamentName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = TextPrimary
          )
          Text(
            text = "Código de Sala: $roomCode",
            style = MaterialTheme.typography.bodyMedium,
            color = NeonCyan,
            fontWeight = FontWeight.Bold
          )
        }
        Button(
          onClick = onBack,
          colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = TextPrimary),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Salir")
        }
      }
    }

    // Winner Banner if finished
    if (winner != null) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, ElectricGold, RoundedCornerShape(20.dp))
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "👑 ¡CAMPEÓN DEL TORNEO! 👑", fontWeight = FontWeight.Black, fontSize = 20.sp, color = ElectricGold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = winner.avatar, fontSize = 56.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = winner.name, fontWeight = FontWeight.Black, fontSize = 24.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = if (winner.isUser) "¡Ganaste la Copa! +1 Trofeo oficial agregado a tu perfil personal" else "¡Gran torneo! Sigue compitiendo para coronarte campeón.",
              fontSize = 13.sp,
              color = TextSecondary,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }

    item {
      Text(text = "Llave de Partidos (Toca un partido para jugar 1v1)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
    }

    items(matches) { match ->
      val roundLabel = when (match.roundIndex) {
        0 -> "Fase Inicial"
        1 -> if (matches.size == 3) "🏆 Gran Final" else "Semifinal"
        else -> "🏆 Gran Final"
      }

      val canPlay = match.player1 != null && match.player2 != null && !match.isCompleted

      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(
            width = if (canPlay) 1.5.dp else 1.dp,
            color = if (canPlay) NeonPink else Color(0xFF282C40),
            shape = RoundedCornerShape(16.dp)
          )
          .clickable(enabled = canPlay) {
            HapticHelper.click(context)
            onPlayMatch(match)
          }
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = roundLabel, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonCyan)
            if (match.isCompleted) {
              Text(text = "✓ Finalizado", fontSize = 12.sp, color = ElectricGreen, fontWeight = FontWeight.Bold)
            } else if (canPlay) {
              Text(text = "¡Listo para Jugar!", fontSize = 12.sp, color = NeonPink, fontWeight = FontWeight.Bold)
            } else {
              Text(text = "Esperando rivales", fontSize = 12.sp, color = TextMuted)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Player 1 Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = match.player1?.avatar ?: "❓", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = match.player1?.name ?: "Por definir",
                fontWeight = if (match.winner == match.player1) FontWeight.Black else FontWeight.Normal,
                color = if (match.winner == match.player1) ElectricGold else TextPrimary
              )
            }
            if (match.isCompleted) {
              Text(text = "${match.score1}", fontWeight = FontWeight.Bold, color = ElectricGold)
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Player 2 Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = match.player2?.avatar ?: "❓", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = match.player2?.name ?: "Por definir",
                fontWeight = if (match.winner == match.player2) FontWeight.Black else FontWeight.Normal,
                color = if (match.winner == match.player2) ElectricGold else TextPrimary
              )
            }
            if (match.isCompleted) {
              Text(text = "${match.score2}", fontWeight = FontWeight.Bold, color = ElectricGold)
            }
          }

          if (canPlay) {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
              onClick = {
                HapticHelper.click(context)
                onPlayMatch(match)
              },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "Jugar Partido Ahora en Este Dispositivo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}
