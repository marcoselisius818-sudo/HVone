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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.GameType
import com.example.ui.theme.ElectricGold
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
fun SplitScreenDuelHub(
  defaultP1Name: String,
  onLaunchDuel: (AppScreen, String, String) -> Unit
) {
  val context = LocalContext.current
  var p1Name by remember { mutableStateOf(defaultP1Name) }
  var p2Name by remember { mutableStateOf("Amigo") }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "⚔️", fontSize = 28.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Duelo Mismo Dispositivo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = TextPrimary
          )
          Text(
            text = "1v1 Cara a cara con pantalla dividida invertida",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
          )
        }
      }
    }

    // Players Config Card
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, Color(0xFF282C40), RoundedCornerShape(20.dp))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Configuración de Jugadores",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = ElectricGold
          )
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            OutlinedTextField(
              value = p1Name,
              onValueChange = { p1Name = it },
              label = { Text("Jugador 1 (Abajo)") },
              modifier = Modifier.weight(1f).testTag("input_p1_name"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color(0xFF333852),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              )
            )

            OutlinedTextField(
              value = p2Name,
              onValueChange = { p2Name = it },
              label = { Text("Jugador 2 (Arriba)") },
              modifier = Modifier.weight(1f).testTag("input_p2_name"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonPink,
                unfocusedBorderColor = Color(0xFF333852),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              )
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "💡 Coloquen el teléfono en una mesa plana entre ambos. La mitad del Jugador 2 se girará 180° automáticamente.",
            fontSize = 12.sp,
            color = TextMuted
          )
        }
      }
    }

    item {
      Text(
        text = "Elige el Juego para el Duelo",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = TextPrimary
      )
    }

    // Game 1: 10s Duel
    item {
      DuelGameCard(
        title = "Desafío 10.00s Cara a Cara",
        subtitle = "El cronómetro viral ciego a los 5s",
        emoji = "⏱️",
        gradientColors = listOf(Color(0xFF0F1A26), Color(0xFF16253A)),
        accentColor = NeonCyan,
        onPlay = {
          HapticHelper.click(context)
          onLaunchDuel(AppScreen.GAME_10S_DUEL, p1Name, p2Name)
        }
      )
    }

    // Game 2: Tap War Duel
    item {
      DuelGameCard(
        title = "Guerra de Taps (Tug-of-War)",
        subtitle = "Empuja la barra de energía hacia tu rival tocando a mil por hora",
        emoji = "⚡",
        gradientColors = listOf(Color(0xFF260D1E), Color(0xFF38122D)),
        accentColor = NeonPink,
        onPlay = {
          HapticHelper.click(context)
          onLaunchDuel(AppScreen.GAME_TAP_DUEL, p1Name, p2Name)
        }
      )
    }

    // Game 3: Stroop Duel
    item {
      DuelGameCard(
        title = "Duelo Stroop de Colores",
        subtitle = "El primero en tocar la tinta correcta se lleva el punto",
        emoji = "🧠",
        gradientColors = listOf(Color(0xFF1F1B0D), Color(0xFF332C12)),
        accentColor = ElectricGold,
        onPlay = {
          HapticHelper.click(context)
          onLaunchDuel(AppScreen.GAME_COLOR_DUEL, p1Name, p2Name)
        }
      )
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

@Composable
private fun DuelGameCard(
  title: String,
  subtitle: String,
  emoji: String,
  gradientColors: List<Color>,
  accentColor: Color,
  onPlay: () -> Unit
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    shape = RoundedCornerShape(20.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, Color(0xFF2B2F44), RoundedCornerShape(20.dp))
      .clickable { onPlay() }
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(Brush.horizontalGradient(gradientColors))
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark),
          contentAlignment = Alignment.Center
        ) {
          Text(text = emoji, fontSize = 28.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = subtitle,
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Button(
          onClick = onPlay,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = accentColor,
            contentColor = Color.Black
          )
        ) {
          Text(text = "¡DUELO!", fontWeight = FontWeight.Black, fontSize = 12.sp)
        }
      }
    }
  }
}
