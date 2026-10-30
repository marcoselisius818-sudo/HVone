package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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

val BombaCategories = listOf(
  "Cosas que encuentras en la cocina o el refrigerador",
  "Países, capitales o ciudades del mundo",
  "Películas famosas o sagas de cine",
  "Series populares de Netflix, HBO o Disney+",
  "Marcas conocidas de ropa, autos o zapatillas",
  "Animales salvajes, marinos o domésticos",
  "Cosas que NUNCA debes decir en una primera cita",
  "Comidas rápidas, antojitos o platos típicos",
  "Cantantes famosos, reggaetoneros o bandas",
  "Canciones virales o éxitos que todos cantan",
  "Objetos que caben dentro de un bolsillo del pantalón",
  "Cosas indispensables que llevas a la playa o vacaciones",
  "Palabras en español que rimen con 'Amor' o 'Canción'",
  "Deportes, futbolistas o atletas reconocidos",
  "Superhéroes, villanos o personajes de cómics",
  "Excusas típicas para llegar tarde a clases o al trabajo",
  "Cosas que compras en una farmacia o supermercado",
  "Profesiones, oficios o trabajos raros",
  "Videojuegos populares de consola, PC o celular",
  "Instrumentos musicales o géneros musicales",
  "Cosas que encuentras en el fondo de una mochila o bolso",
  "Cosas rojas que ves en la vida cotidiana",
  "Marcas famosas de refrescos, botanas o golosinas",
  "Fobias, miedos o cosas que dan mucho pánico",
  "Cosas que harías si te ganaras la lotería hoy mismo",
  "Insultos graciosos o apodos entre amigos",
  "Cosas que NO deberías hacer en un velorio o iglesia",
  "Personajes de Disney, Pixar o Nickelodeon",
  "Cosas que tienen ruedas o motores",
  "Tipos de dulces, postres o sabores de helado",
  "Aplicaciones famosas que tienes instaladas en el celular",
  "Cosas que compras con menos de un dólar",
  "Partes del cuerpo humano que son dobles o pares",
  "Mentiras piadosas que todo el mundo ha dicho",
  "Cosas que vuelan (animales, máquinas o fantasías)",
  "Celebridades o influencers con millones de seguidores",
  "Cosas que huelen muy rico o aromas agradables",
  "Cosas que huelen terrible o dan asco",
  "Juegos clásicos que jugabas en el recreo de la escuela",
  "Cosas que se derriten con el calor",
  "Cosas verdes que encuentras en la naturaleza",
  "Preguntas incómodas que te hacen tus tíos en Navidad",
  "Cosas que jamás prestarías a un amigo",
  "Cosas que encuentras en el baño de una casa",
  "Razones por las que te dejaría tu ex",
  "Cosas que siempre se pierden en la casa (llaves, control...)",
  "Bebidas alcohólicas, cócteles o tragos de fiesta",
  "Cosas frías o congeladas",
  "Personajes de anime o caricaturas legendarias",
  "Cosas que hacen ruido muy fuerte de noche",
  "Cosas que haces cuando estás solo en tu habitación",
  "Trabajos en los que tienes que usar uniforme obligatorio",
  "Razones para bloquear a alguien de WhatsApp o Instagram",
  "Cosas que te dan vergüenza ajena ('cringe')",
  "Cosas amarillas",
  "Cosas que explotan o hacen fuego",
  "Frutas y verduras exóticas o comunes",
  "Cosas que nunca meterías al microondas",
  "Cosas que flotan en el agua",
  "Costumbres o tradiciones típicas de cumpleaños",
  "Cosas que dan buena suerte o supersticiones",
  "Marcas de tecnología, apps o computadoras",
  "Cosas redondas que ves todos los días",
  "Nombres de personajes de Los Simpson o Bob Esponja"
)

enum class BombaPhase {
  SETUP,
  COUNTDOWN,
  TICKING,
  EXPLODED,
  GAME_OVER
}

data class BombaPlayer(
  val id: Int,
  val name: String,
  var penaltyPoints: Int = 0
)

@Composable
fun GameBombaScreen(
  initialPlayerName: String = "Jugador 1",
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean) -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val isMuted by SoundHelper.isMuted.collectAsStateWithLifecycle()

  var phase by remember { mutableStateOf(BombaPhase.SETUP) }
  var totalRounds by remember { mutableIntStateOf(5) }
  var currentRound by remember { mutableIntStateOf(1) }
  val players = remember {
    mutableStateListOf(
      BombaPlayer(1, initialPlayerName),
      BombaPlayer(2, "Amigo 1"),
      BombaPlayer(3, "Amigo 2")
    )
  }
  var newPlayerName by remember { mutableStateOf("") }
  var currentCategory by remember { mutableStateOf("") }
  var countdownSeconds by remember { mutableIntStateOf(3) }

  // Countdown timer phase
  LaunchedEffect(phase) {
    if (phase == BombaPhase.COUNTDOWN) {
      countdownSeconds = 3
      while (countdownSeconds > 0) {
        HapticHelper.vibrateOnly(context, 20L)
        SoundHelper.playCountdownBeep(isFinal = countdownSeconds == 1)
        delay(1000)
        countdownSeconds--
      }
      phase = BombaPhase.TICKING
    }
  }

  // Ticking bomb with secret random duration
  LaunchedEffect(phase) {
    if (phase == BombaPhase.TICKING) {
      val secretDurationMs = Random.nextLong(14_000L, 36_000L)
      val startTime = System.currentTimeMillis()
      var elapsed = 0L

      while (elapsed < secretDurationMs && phase == BombaPhase.TICKING) {
        val remainingFraction = (1f - (elapsed.toFloat() / secretDurationMs)).coerceIn(0f, 1f)
        val tickInterval = (220 + (remainingFraction * 480)).toLong().coerceIn(130L, 680L)
        HapticHelper.vibrateOnly(context, 12L)
        SoundHelper.playTick(urgency = 1f - remainingFraction)
        delay(tickInterval)
        elapsed = System.currentTimeMillis() - startTime
      }

      if (phase == BombaPhase.TICKING) {
        HapticHelper.vibrateOnly(context, 220L)
        SoundHelper.playExplosion()
        phase = BombaPhase.EXPLODED
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
    // Top Bar with Mute Toggle
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
          text = "Juego de La Bomba",
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

    AnimatedContent(
      targetState = phase,
      transitionSpec = {
        fadeIn(tween(220)) togetherWith fadeOut(tween(160))
      },
      modifier = Modifier.weight(1f),
      label = "bomba_phase"
    ) { currentPhase ->
      when (currentPhase) {
        BombaPhase.SETUP -> {
          Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              item {
                Card(
                  colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                  shape = RoundedCornerShape(18.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonPink, RoundedCornerShape(18.dp))
                ) {
                  Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                      text = "💣 Reglas de La Bomba",
                      fontWeight = FontWeight.Black,
                      fontSize = 16.sp,
                      color = ElectricGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = "Se muestra una temática y los jugadores dicen palabras relacionadas por turnos en círculo.\n\n" +
                          "Cuando la bomba estalle, selecciona quién perdió la ronda. Al finalizar gana quien tenga menos derrotas.",
                      fontSize = 13.sp,
                      color = TextSecondary,
                      lineHeight = 18.sp
                    )
                  }
                }
              }

              item {
                Text(text = "Rondas a Jugar:", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  listOf(5, 10, 15, 20).forEach { r ->
                    FilterChip(
                      selected = totalRounds == r,
                      onClick = {
                        HapticHelper.click(context)
                        totalRounds = r
                      },
                      label = { Text("$r Rondas") },
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

              item {
                Text(text = "Jugadores Locales (${players.size}):", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(
                    value = newPlayerName,
                    onValueChange = { newPlayerName = it },
                    placeholder = { Text("Nombre del jugador") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = NeonCyan,
                      unfocusedBorderColor = Color(0xFF333852),
                      focusedTextColor = TextPrimary,
                      unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                  )

                  Button(
                    onClick = {
                      val name = newPlayerName.trim()
                      if (name.isNotEmpty()) {
                        HapticHelper.click(context)
                        players.add(BombaPlayer((players.maxOfOrNull { it.id } ?: 0) + 1, name))
                        newPlayerName = ""
                      }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                  ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Añadir")
                  }
                }
              }

              items(players, key = { it.id }) { p ->
                Card(
                  colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(text = "👤", fontSize = 20.sp)
                      Spacer(modifier = Modifier.width(10.dp))
                      Text(text = p.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    }
                    if (players.size > 2) {
                      IconButton(onClick = {
                        HapticHelper.click(context)
                        players.remove(p)
                      }) {
                        Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = NeonPink)
                      }
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
              onClick = {
                HapticHelper.click(context)
                currentRound = 1
                players.forEach { it.penaltyPoints = 0 }
                currentCategory = BombaCategories.random()
                phase = BombaPhase.COUNTDOWN
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .testTag("start_bomba_game_button"),
              shape = RoundedCornerShape(18.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "¡INICIAR JUEGO (RONDA 1 DE $totalRounds)!", fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
          }
        }

        BombaPhase.COUNTDOWN -> {
          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Text(text = "Ronda $currentRound de $totalRounds", color = ElectricGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
              colors = CardDefaults.cardColors(containerColor = SurfaceDark),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, NeonCyan, RoundedCornerShape(20.dp))
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "TEMÁTICA DE LA RONDA", fontSize = 12.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = currentCategory,
                  fontSize = 22.sp,
                  fontWeight = FontWeight.Black,
                  color = TextPrimary,
                  textAlign = TextAlign.Center
                )
              }
            }

            Spacer(modifier = Modifier.height(36.dp))
            Text(text = "Preparados...", fontSize = 16.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "$countdownSeconds",
              fontSize = 72.sp,
              fontWeight = FontWeight.Black,
              color = NeonPink
            )
          }
        }

        BombaPhase.TICKING -> {
          // Only animate the bomb pulse while in TICKING phase using graphicsLayer (zero layout overhead)
          val infiniteTransition = rememberInfiniteTransition(label = "bomb_pulse")
          val pulseScale by infiniteTransition.animateFloat(
            initialValue = 0.94f,
            targetValue = 1.14f,
            animationSpec = infiniteRepeatable(
              animation = tween(380, easing = FastOutSlowInEasing),
              repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
          )

          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Ronda $currentRound / $totalRounds", fontWeight = FontWeight.Bold, color = ElectricGold, fontSize = 15.sp)
              Text(text = "⏱️ Tiempo Secreto", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 14.sp)
            }

            Card(
              colors = CardDefaults.cardColors(containerColor = SurfaceDark),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF2E334D), RoundedCornerShape(16.dp))
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "Temática:", fontSize = 11.sp, color = TextMuted)
                Text(
                  text = currentCategory,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Black,
                  color = TextPrimary,
                  textAlign = TextAlign.Center
                )
              }
            }

            Box(
              modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                  scaleX = pulseScale
                  scaleY = pulseScale
                },
              contentAlignment = Alignment.Center
            ) {
              Text(text = "💣", fontSize = 120.sp)
            }

            Card(
              colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
              shape = RoundedCornerShape(18.dp),
              modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, Color(0xFF3B405A), RoundedCornerShape(18.dp))
            ) {
              Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "🔥 ¡MECHA ENCENDIDA! 🔥",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Black,
                  color = ElectricGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Digan palabras de la temática en voz alta por turnos.",
                  fontSize = 14.sp,
                  color = TextPrimary,
                  textAlign = TextAlign.Center,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }

        BombaPhase.EXPLODED -> {
          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "💥 ¡¡BOOOOOOM!! 💥", fontSize = 32.sp, fontWeight = FontWeight.Black, color = NeonPink)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "¿A quién le explotó la bomba en esta ronda?",
              fontSize = 14.sp,
              color = TextSecondary,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(players, key = { it.id }) { p ->
                Card(
                  colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF282C40), RoundedCornerShape(16.dp))
                    .clickable {
                      p.penaltyPoints++

                      if (currentRound < totalRounds) {
                        HapticHelper.click(context)
                        currentRound++
                        currentCategory = BombaCategories.random()
                        phase = BombaPhase.COUNTDOWN
                      } else {
                        HapticHelper.vibrateOnly(context, 80L)
                        SoundHelper.playVictory()
                        phase = BombaPhase.GAME_OVER
                        val bestPlayer = players.minByOrNull { it.penaltyPoints }
                        onRecordResult(
                          500,
                          "La Bomba: Ganó ${bestPlayer?.name} (${bestPlayer?.penaltyPoints} derrotas)",
                          "Juego Grupal La Bomba",
                          true
                        )
                      }
                    }
                ) {
                  Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(text = "💥", fontSize = 24.sp)
                      Spacer(modifier = Modifier.width(12.dp))
                      Column {
                        Text(text = p.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                        Text(text = "Derrotas acumuladas: ${p.penaltyPoints}", fontSize = 12.sp, color = TextSecondary)
                      }
                    }

                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonPink)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                      Text(text = "Seleccionar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                  }
                }
              }
            }
          }
        }

        BombaPhase.GAME_OVER -> {
          val sortedPlayers = remember(players.toList()) { players.sortedBy { it.penaltyPoints } }
          val winner = sortedPlayers.firstOrNull()

          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "🏆 ¡FIN DEL JUEGO! 🏆", fontSize = 26.sp, fontWeight = FontWeight.Black, color = ElectricGold)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
              colors = CardDefaults.cardColors(containerColor = SurfaceDark),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, ElectricGold, RoundedCornerShape(20.dp))
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "👑 ¡GANADOR OFICIAL! 👑", fontWeight = FontWeight.Black, color = ElectricGold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = winner?.name ?: "Nadie", fontSize = 26.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Text(
                  text = "¡Con solo ${winner?.penaltyPoints ?: 0} bombas!",
                  fontSize = 13.sp,
                  color = ElectricGreen,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Tabla de Posiciones Final:", fontWeight = FontWeight.Bold, color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(sortedPlayers, key = { it.id }) { p ->
                val isTop = p == winner
                Card(
                  colors = CardDefaults.cardColors(containerColor = if (isTop) Color(0xFF162A1F) else SurfaceDark),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(text = if (isTop) "🥇 ${p.name}" else "• ${p.name}", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "${p.penaltyPoints} derrotas", color = if (isTop) ElectricGreen else NeonPink, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }

            Button(
              onClick = {
                HapticHelper.click(context)
                phase = BombaPhase.SETUP
              },
              modifier = Modifier.fillMaxWidth().height(60.dp),
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "Jugar Otra Partida", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
          }
        }
      }
    }
  }
}
