package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.util.PlayerRosterManager
import com.example.ui.util.SoundHelper
import com.example.ui.util.WordHistoryManager
import kotlin.random.Random

data class ImpostorWordEntry(
  val word: String,
  val hint: String,
  val categoryLabel: String
)

data class ImpostorThemeCategory(
  val id: String,
  val name: String,
  val icon: String,
  val entries: List<ImpostorWordEntry>
)

// Pistas naturales y cotidianas (LATAM): 1 palabra (o 2 máx.) que tiene relación directa
// con la palabra secreta para que el Impostor pueda hablar sin levantar sospechas,
// pero sin regalar cuál es exactamente la palabra.
private val AnimalEntries = listOf(
  ImpostorWordEntry("Camaleón", "Rama", "Animales"),
  ImpostorWordEntry("Delfín", "Salto", "Animales"),
  ImpostorWordEntry("León", "Selva", "Animales"),
  ImpostorWordEntry("Pingüino", "Hielo", "Animales"),
  ImpostorWordEntry("Pulpo", "Mar", "Animales"),
  ImpostorWordEntry("Águila Real", "Vuelo", "Animales"),
  ImpostorWordEntry("Lobo", "Noche", "Animales"),
  ImpostorWordEntry("Oso Panda", "Hojas", "Animales"),
  ImpostorWordEntry("Canguro", "Brinco", "Animales"),
  ImpostorWordEntry("Tiburón Blanco", "Profundo", "Animales"),
  ImpostorWordEntry("Perezoso", "Árbol", "Animales"),
  ImpostorWordEntry("Flamenco", "Laguna", "Animales"),
  ImpostorWordEntry("Cobra Real", "Veneno", "Animales"),
  ImpostorWordEntry("Gorila", "Fuerza", "Animales"),
  ImpostorWordEntry("Búho", "Ojos", "Animales"),
  ImpostorWordEntry("Guepardo", "Carrera", "Animales"),
  ImpostorWordEntry("Ornitorrinco", "Río", "Animales"),
  ImpostorWordEntry("Murciélago", "Cueva", "Animales"),
  ImpostorWordEntry("Zorro", "Bosque", "Animales"),
  ImpostorWordEntry("Ballena Azul", "Gigante", "Animales"),
  ImpostorWordEntry("Escorpión", "Desierto", "Animales"),
  ImpostorWordEntry("Pavo Real", "Plumas", "Animales")
)

private val ObjectEntries = listOf(
  ImpostorWordEntry("Brújula", "Mapa", "Objetos"),
  ImpostorWordEntry("Reloj de Arena", "Minutos", "Objetos"),
  ImpostorWordEntry("Paraguas", "Lluvia", "Objetos"),
  ImpostorWordEntry("Espejo", "Baño", "Objetos"),
  ImpostorWordEntry("Candado", "Reja", "Objetos"),
  ImpostorWordEntry("Telescopio", "Estrellas", "Objetos"),
  ImpostorWordEntry("Microondas", "Cocina", "Objetos"),
  ImpostorWordEntry("Extintor", "Humo", "Objetos"),
  ImpostorWordEntry("Auriculares", "Música", "Objetos"),
  ImpostorWordEntry("Maleta de Viaje", "Ropa", "Objetos"),
  ImpostorWordEntry("Termómetro", "Fiebre", "Objetos"),
  ImpostorWordEntry("Lupa", "Vidrio", "Objetos"),
  ImpostorWordEntry("Imán", "Nevera", "Objetos"),
  ImpostorWordEntry("Ancla", "Barco", "Objetos"),
  ImpostorWordEntry("Paracaídas", "Avión", "Objetos"),
  ImpostorWordEntry("Bumerán", "Viento", "Objetos"),
  ImpostorWordEntry("Semáforo", "Esquina", "Objetos"),
  ImpostorWordEntry("Cofre del Tesoro", "Monedas", "Objetos"),
  ImpostorWordEntry("Billetera", "Tarjetas", "Objetos"),
  ImpostorWordEntry("Tijeras", "Papel", "Objetos")
)

private val AbstractEntries = listOf(
  ImpostorWordEntry("El Tiempo", "Reloj", "Cosas Abstractas"),
  ImpostorWordEntry("La Nostalgia", "Recuerdos", "Cosas Abstractas"),
  ImpostorWordEntry("El Karma", "Acciones", "Cosas Abstractas"),
  ImpostorWordEntry("El Silencio", "Madrugada", "Cosas Abstractas"),
  ImpostorWordEntry("La Gravedad", "Piso", "Cosas Abstractas"),
  ImpostorWordEntry("Un Secreto", "Confianza", "Cosas Abstractas"),
  ImpostorWordEntry("La Suerte", "Lotería", "Cosas Abstractas"),
  ImpostorWordEntry("El Destino", "Futuro", "Cosas Abstractas"),
  ImpostorWordEntry("Un Sueño", "Dormir", "Cosas Abstractas"),
  ImpostorWordEntry("La Paciencia", "Fila", "Cosas Abstractas"),
  ImpostorWordEntry("El Caos", "Tráfico", "Cosas Abstractas"),
  ImpostorWordEntry("La Libertad", "Viajar", "Cosas Abstractas"),
  ImpostorWordEntry("La Ironía", "Broma", "Cosas Abstractas"),
  ImpostorWordEntry("Un Recuerdo", "Foto", "Cosas Abstractas"),
  ImpostorWordEntry("El Orgullo", "Disculpa", "Cosas Abstractas"),
  ImpostorWordEntry("El Infinito", "Universo", "Cosas Abstractas"),
  ImpostorWordEntry("Un Rumor", "Chisme", "Cosas Abstractas"),
  ImpostorWordEntry("Una Promesa", "Juramento", "Cosas Abstractas"),
  ImpostorWordEntry("El Miedo", "Oscuridad", "Cosas Abstractas"),
  ImpostorWordEntry("La Envidia", "Éxito", "Cosas Abstractas")
)

private val SeriesEntries = listOf(
  ImpostorWordEntry("Stranger Things", "Monstruos", "Series Populares"),
  ImpostorWordEntry("La Casa de Papel", "Banco", "Series Populares"),
  ImpostorWordEntry("El Juego del Calamar", "Millonario", "Series Populares"),
  ImpostorWordEntry("Breaking Bad", "Química", "Series Populares"),
  ImpostorWordEntry("Los Simpson", "Familia", "Series Populares"),
  ImpostorWordEntry("Merlina (Wednesday)", "Colegio", "Series Populares"),
  ImpostorWordEntry("Game of Thrones", "Espadas", "Series Populares"),
  ImpostorWordEntry("The Walking Dead", "Apocalipsis", "Series Populares"),
  ImpostorWordEntry("Friends", "Departamento", "Series Populares"),
  ImpostorWordEntry("Peaky Blinders", "Mafia", "Series Populares"),
  ImpostorWordEntry("Cobra Kai", "Pelea", "Series Populares"),
  ImpostorWordEntry("Rick y Morty", "Científico", "Series Populares"),
  ImpostorWordEntry("Black Mirror", "Tecnología", "Series Populares"),
  ImpostorWordEntry("The Boys", "Poderes", "Series Populares"),
  ImpostorWordEntry("One Piece", "Barco", "Series Populares"),
  ImpostorWordEntry("Bob Esponja", "Fondo del mar", "Series Populares"),
  ImpostorWordEntry("Dragon Ball Z", "Combate", "Series Populares"),
  ImpostorWordEntry("The Last of Us", "Infección", "Series Populares"),
  ImpostorWordEntry("Dark", "Tiempo", "Series Populares"),
  ImpostorWordEntry("The Mandalorian", "Espacio", "Series Populares")
)

private val MovieEntries = listOf(
  ImpostorWordEntry("Titanic", "Océano", "Películas Populares"),
  ImpostorWordEntry("Avatar", "Selva", "Películas Populares"),
  ImpostorWordEntry("Harry Potter", "Magia", "Películas Populares"),
  ImpostorWordEntry("El Rey León", "África", "Películas Populares"),
  ImpostorWordEntry("Toy Story", "Cuarto", "Películas Populares"),
  ImpostorWordEntry("Shrek", "Cuento", "Películas Populares"),
  ImpostorWordEntry("Avengers: Endgame", "Batalla", "Películas Populares"),
  ImpostorWordEntry("Spider-Man", "Ciudad", "Películas Populares"),
  ImpostorWordEntry("Jurassic Park", "Isla", "Películas Populares"),
  ImpostorWordEntry("Coco", "Guitarra", "Películas Populares"),
  ImpostorWordEntry("Joker", "Maquillaje", "Películas Populares"),
  ImpostorWordEntry("Matrix", "Computadora", "Películas Populares"),
  ImpostorWordEntry("Rápidos y Furiosos", "Motores", "Películas Populares"),
  ImpostorWordEntry("Interestelar", "Planetas", "Películas Populares"),
  ImpostorWordEntry("El Padrino", "Jefe", "Películas Populares"),
  ImpostorWordEntry("Piratas del Caribe", "Mar", "Películas Populares"),
  ImpostorWordEntry("Barbie", "Rosa", "Películas Populares"),
  ImpostorWordEntry("El Señor de los Anillos", "Montaña", "Películas Populares"),
  ImpostorWordEntry("Mi Pobre Angelito", "Navidad", "Películas Populares"),
  ImpostorWordEntry("Intensamente", "Mente", "Películas Populares")
)

private val StyleEntries = listOf(
  ImpostorWordEntry("Zapatillas Jordan", "Cancha", "Estilo y Moda"),
  ImpostorWordEntry("Chaqueta de Cuero", "Moto", "Estilo y Moda"),
  ImpostorWordEntry("Gafas de Sol Vintage", "Playa", "Estilo y Moda"),
  ImpostorWordEntry("Alfombra Roja", "Famosos", "Estilo y Moda"),
  ImpostorWordEntry("Perfume Francés", "Olor", "Estilo y Moda"),
  ImpostorWordEntry("Tatuaje Minimalista", "Aguja", "Estilo y Moda"),
  ImpostorWordEntry("Traje de Gala (Esmoquin)", "Boda", "Estilo y Moda"),
  ImpostorWordEntry("Ropa Oversize", "Ancho", "Estilo y Moda"),
  ImpostorWordEntry("Corte Degradado (Fade)", "Barbería", "Estilo y Moda"),
  ImpostorWordEntry("Reloj de Lujo", "Muñeca", "Estilo y Moda"),
  ImpostorWordEntry("Pasarela de Moda", "Modelos", "Estilo y Moda"),
  ImpostorWordEntry("Cadena de Oro", "Cuello", "Estilo y Moda"),
  ImpostorWordEntry("Tacones de Aguja", "Fiesta", "Estilo y Moda"),
  ImpostorWordEntry("Botas Vaqueras", "Rancho", "Estilo y Moda"),
  ImpostorWordEntry("Gorra Urbana", "Sol", "Estilo y Moda")
)

private val FoodAndPlacesEntries = listOf(
  ImpostorWordEntry("Pizza Napolitana", "Horno", "Comidas y Lugares"),
  ImpostorWordEntry("Tacos al Pastor", "Salsa", "Comidas y Lugares"),
  ImpostorWordEntry("Sushi", "Arroz", "Comidas y Lugares"),
  ImpostorWordEntry("Hamburguesa Doble", "Papas", "Comidas y Lugares"),
  ImpostorWordEntry("Helado de Chocolate", "Postre", "Comidas y Lugares"),
  ImpostorWordEntry("Torre Eiffel", "Europa", "Comidas y Lugares"),
  ImpostorWordEntry("Pirámides de Egipto", "Arena", "Comidas y Lugares"),
  ImpostorWordEntry("Isla Desierta", "Palmera", "Comidas y Lugares"),
  ImpostorWordEntry("Triángulo de las Bermudas", "Misterio", "Comidas y Lugares"),
  ImpostorWordEntry("Casino de Las Vegas", "Apuestas", "Comidas y Lugares"),
  ImpostorWordEntry("Estación Espacial", "Gravedad", "Comidas y Lugares"),
  ImpostorWordEntry("Área 51", "Secreto", "Comidas y Lugares"),
  ImpostorWordEntry("Parque de Diversiones", "Juegos", "Comidas y Lugares"),
  ImpostorWordEntry("Crucero de Lujo", "Vacaciones", "Comidas y Lugares")
)

val ImpostorThemes = listOf(
  ImpostorThemeCategory(
    id = "ALL_MIXED",
    name = "Todo Mezclado (Sorpresa)",
    icon = "🎲",
    entries = AnimalEntries + ObjectEntries + AbstractEntries + SeriesEntries + MovieEntries + StyleEntries + FoodAndPlacesEntries
  ),
  ImpostorThemeCategory("SERIES", "Series Muy Populares", "📺", SeriesEntries),
  ImpostorThemeCategory("MOVIES", "Películas Populares", "🎬", MovieEntries),
  ImpostorThemeCategory("ANIMALS", "Animales", "🐾", AnimalEntries),
  ImpostorThemeCategory("OBJECTS", "Objetos", "📦", ObjectEntries),
  ImpostorThemeCategory("ABSTRACT", "Cosas Abstractas", "🌀", AbstractEntries),
  ImpostorThemeCategory("STYLE", "Estilo y Moda", "✨", StyleEntries),
  ImpostorThemeCategory("FOOD_PLACES", "Comidas, Lugares y Más", "🌎", FoodAndPlacesEntries)
)

enum class ImpostorPhase {
  SETUP,
  PASS_AND_REVEAL,
  DEBATE,
  VOTING,
  ELIMINATION_REVEAL,
  GAME_OVER
}

data class ImpostorPlayerState(
  val id: Int,
  val name: String,
  val isImpostor: Boolean = false,
  val isEliminated: Boolean = false
)

data class ImpostorRoundSummary(
  val innocentsWon: Boolean,
  val impostorNames: String,
  val impostorCount: Int,
  val word: String,
  val hint: String,
  val categoryLabel: String
)

private fun maxAllowedImpostorsForPlayers(playerCount: Int): Int {
  return when {
    playerCount >= 7 -> 3
    playerCount >= 5 -> 2
    else -> 1
  }
}

@Composable
fun GameImpostorScreen(
  initialPlayerName: String = "Jugador 1",
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean) -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val isMuted by SoundHelper.isMuted.collectAsStateWithLifecycle()

  // Load persistent player names
  val savedNames = remember { PlayerRosterManager.getPartyPlayers(context, initialPlayerName) }
  val playerNames = remember {
    mutableStateListOf<String>().apply {
      addAll(savedNames)
      if (size < 3) {
        while (size < 3) add("Amigo ${size}")
      }
    }
  }
  var newPlayerName by remember { mutableStateOf("") }

  var selectedTheme by remember { mutableStateOf(ImpostorThemes[0]) }
  var giveImpostorHint by remember { mutableStateOf(true) }
  var selectedImpostorCount by remember { mutableIntStateOf(1) }
  var phase by remember { mutableStateOf(ImpostorPhase.SETUP) }

  // Active match state
  val matchPlayers = remember { mutableStateListOf<ImpostorPlayerState>() }
  var secretEntry by remember { mutableStateOf(AnimalEntries[0]) }
  var revealIndex by remember { mutableIntStateOf(0) }
  var isCardRevealed by remember { mutableStateOf(false) }
  var starterPlayerName by remember { mutableStateOf("") }
  var lastEjectedPlayer by remember { mutableStateOf<ImpostorPlayerState?>(null) }

  // Frozen snapshot for GAME_OVER so starting a new round never flashes the new secret word during exit animation
  var roundOverSummary by remember {
    mutableStateOf(
      ImpostorRoundSummary(
        innocentsWon = true,
        impostorNames = "",
        impostorCount = 1,
        word = "",
        hint = "",
        categoryLabel = ""
      )
    )
  }

  // When "ALL_MIXED" (Todo Mezclado) is selected, hide the category during reveal and debate
  val isRandomMixedMode = selectedTheme.id == "ALL_MIXED"

  fun persistRoster() {
    PlayerRosterManager.savePartyPlayers(context, playerNames.toList())
    val maxImp = maxAllowedImpostorsForPlayers(playerNames.size)
    if (selectedImpostorCount > maxImp) {
      selectedImpostorCount = maxImp
    }
  }

  fun captureRoundSummary(didInnocentsWin: Boolean) {
    val allImpostors = matchPlayers.filter { it.isImpostor }
    val namesJoined = allImpostors.joinToString(", ") { it.name }.ifEmpty { "Desconocido" }
    roundOverSummary = ImpostorRoundSummary(
      innocentsWon = didInnocentsWin,
      impostorNames = namesJoined,
      impostorCount = allImpostors.size.coerceAtLeast(1),
      word = secretEntry.word,
      hint = secretEntry.hint,
      categoryLabel = secretEntry.categoryLabel
    )
  }

  fun startNewImpostorMatch() {
    persistRoster()
    val chosenEntry = WordHistoryManager.pickSmartItem(
      context = context,
      categoryKey = "IMPOSTOR_${selectedTheme.id}",
      items = selectedTheme.entries,
      wordSelector = { it.word }
    )
    secretEntry = chosenEntry

    val total = playerNames.size
    val actualImpostorCount = selectedImpostorCount.coerceIn(1, maxAllowedImpostorsForPlayers(total))
    val impostorIndices = (0 until total).shuffled().take(actualImpostorCount).toSet()

    matchPlayers.clear()
    playerNames.forEachIndexed { idx, name ->
      matchPlayers.add(
        ImpostorPlayerState(
          id = idx + 1,
          name = name,
          isImpostor = idx in impostorIndices,
          isEliminated = false
        )
      )
    }

    // Low but non-zero probability (~12%) that an Impostor starts the round
    val impostorPlayers = matchPlayers.filter { it.isImpostor }
    val innocentPlayers = matchPlayers.filter { !it.isImpostor }
    val impostorStarts = Random.nextFloat() < 0.12f
    starterPlayerName = if (impostorStarts && impostorPlayers.isNotEmpty()) {
      impostorPlayers.random().name
    } else {
      innocentPlayers.randomOrNull()?.name ?: matchPlayers.first().name
    }

    revealIndex = 0
    isCardRevealed = false
    lastEjectedPlayer = null
    phase = ImpostorPhase.PASS_AND_REVEAL
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
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        IconButton(
          onClick = {
            HapticHelper.click(context)
            onBack()
          },
          modifier = Modifier.testTag("back_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "🕵️ El Impostor",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Black,
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

    AnimatedContent(
      targetState = phase,
      transitionSpec = {
        fadeIn(tween(220)) togetherWith fadeOut(tween(160))
      },
      modifier = Modifier.weight(1f),
      label = "impostor_phase"
    ) { currentPhase ->
      when (currentPhase) {
        ImpostorPhase.SETUP -> {
          val maxImpostors = maxAllowedImpostorsForPlayers(playerNames.size)
          Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item {
                Card(
                  colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                  shape = RoundedCornerShape(18.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonCyan, RoundedCornerShape(18.dp))
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                      text = "🕵️ ¿Cómo se juega a El Impostor?",
                      fontWeight = FontWeight.Black,
                      fontSize = 15.sp,
                      color = ElectricGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Los inocentes ven la palabra secreta y los impostores reciben una pista natural para camuflarse. Los impostores ganan si logran igualar en número a los inocentes.",
                      fontSize = 12.sp,
                      color = TextSecondary,
                      lineHeight = 17.sp
                    )
                  }
                }
              }

              // Impostor count selector (1, 2 or 3 based on player count)
              item {
                Card(
                  colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF282C40), RoundedCornerShape(16.dp))
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                      text = "🕵️ Cantidad de Impostores:",
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp,
                      color = ElectricGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "1 impostor (3+ jug.) • 2 impostores (5+ jug.) • 3 impostores (7+ jug.)",
                      fontSize = 11.sp,
                      color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      listOf(
                        Triple(1, "1 Impostor", 3),
                        Triple(2, "2 Impostores", 5),
                        Triple(3, "3 Impostores", 7)
                      ).forEach { (count, label, minReq) ->
                        val enabled = playerNames.size >= minReq
                        val selected = selectedImpostorCount == count
                        FilterChip(
                          selected = selected,
                          enabled = enabled,
                          onClick = {
                            HapticHelper.click(context)
                            selectedImpostorCount = count
                          },
                          label = {
                            Text(
                              text = if (enabled) label else "$label (${minReq}+)",
                              fontSize = 11.sp,
                              fontWeight = FontWeight.Bold,
                              textAlign = TextAlign.Center,
                              modifier = Modifier.fillMaxWidth()
                            )
                          },
                          modifier = Modifier.weight(1f),
                          colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPink,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceVariantDark,
                            labelColor = TextSecondary,
                            disabledContainerColor = Color(0xFF141622),
                            disabledLabelColor = TextMuted
                          )
                        )
                      }
                    }
                  }
                }
              }

              // Impostor smart hint switch
              item {
                Card(
                  colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "💡 Pista de Apoyo para el Impostor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                      )
                      Text(
                        text = "Una palabra cotidiana relacionada que no lo delata ni le regala la respuesta",
                        fontSize = 11.sp,
                        color = TextSecondary
                      )
                    }
                    Switch(
                      checked = giveImpostorHint,
                      onCheckedChange = {
                        HapticHelper.click(context)
                        giveImpostorHint = it
                      },
                      colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = ElectricGreen
                      )
                    )
                  }
                }
              }

              // Theme selection
              item {
                Text(
                  text = "Elige la Temática:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = TextPrimary
                )
              }

              items(ImpostorThemes, key = { it.id }) { theme ->
                val isSelected = selectedTheme.id == theme.id
                Card(
                  colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF1D243B) else SurfaceDark
                  ),
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .border(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) NeonPink else Color(0xFF282C40),
                      shape = RoundedCornerShape(14.dp)
                    )
                    .clickable {
                      HapticHelper.click(context)
                      selectedTheme = theme
                    }
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(text = theme.icon, fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text(text = theme.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                      Text(
                        text = if (theme.id == "ALL_MIXED") "${theme.entries.size} palabras • Categoría oculta" else "${theme.entries.size} palabras con pistas cotidianas",
                        fontSize = 11.sp,
                        color = TextSecondary
                      )
                    }
                    if (isSelected) {
                      Text(text = "ACTIVO", fontWeight = FontWeight.Black, fontSize = 11.sp, color = NeonPink)
                    }
                  }
                }
              }

              // Persistent Players section
              item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Jugadores Guardados (${playerNames.size} — Máx. $maxImpostors impostor${if (maxImpostors > 1) "es" else ""}):",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = ElectricGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  OutlinedTextField(
                    value = newPlayerName,
                    onValueChange = { newPlayerName = it },
                    placeholder = { Text("Agregar jugador...") },
                    modifier = Modifier.weight(1f),
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
                      val clean = newPlayerName.trim()
                      if (clean.isNotEmpty()) {
                        HapticHelper.click(context)
                        playerNames.add(clean)
                        newPlayerName = ""
                        persistRoster()
                      }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                  ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Añadir", fontWeight = FontWeight.Bold)
                  }
                }
              }

              items(playerNames.toList()) { pName ->
                Card(
                  colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(text = "🕵️", fontSize = 18.sp)
                      Spacer(modifier = Modifier.width(10.dp))
                      Text(text = pName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    }
                    if (playerNames.size > 3) {
                      IconButton(
                        onClick = {
                          HapticHelper.click(context)
                          playerNames.remove(pName)
                          persistRoster()
                        }
                      ) {
                        Icon(Icons.Default.Close, contentDescription = "Quitar", tint = NeonPink)
                      }
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
              onClick = {
                HapticHelper.click(context)
                startNewImpostorMatch()
              },
              enabled = playerNames.size >= 3,
              modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .testTag("start_impostor_button"),
              shape = RoundedCornerShape(18.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              val impLabel = if (selectedImpostorCount == 1) "1 IMPOSTOR" else "$selectedImpostorCount IMPOSTORES"
              Text(
                text = "¡REPARTIR ROLES ($impLabel)!",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
              )
            }
          }
        }

        ImpostorPhase.PASS_AND_REVEAL -> {
          val currentPlayer = matchPlayers.getOrNull(revealIndex)
          if (currentPlayer != null) {
            Column(
              modifier = Modifier.fillMaxSize(),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Jugador ${revealIndex + 1} de ${matchPlayers.size}",
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                fontSize = 14.sp
              )

              Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .border(
                    width = 2.dp,
                    color = if (!isCardRevealed) NeonCyan else if (currentPlayer.isImpostor) NeonPink else ElectricGreen,
                    shape = RoundedCornerShape(24.dp)
                  )
                  .animateContentSize()
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "Turno de:",
                    fontSize = 13.sp,
                    color = TextSecondary
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = currentPlayer.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = ElectricGold,
                    textAlign = TextAlign.Center
                  )

                  Spacer(modifier = Modifier.height(24.dp))

                  if (!isCardRevealed) {
                    Text(text = "🔒", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                      text = "Asegúrate de que nadie más mire la pantalla y toca el botón para ver tu rol.",
                      fontSize = 13.sp,
                      color = TextSecondary,
                      textAlign = TextAlign.Center
                    )
                  } else {
                    if (currentPlayer.isImpostor) {
                      Text(text = "🕵️", fontSize = 54.sp)
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                        text = "¡¡ERES EL IMPOSTOR!!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonPink,
                        textAlign = TextAlign.Center
                      )
                      if (!isRandomMixedMode) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                          text = "Categoría: ${secretEntry.categoryLabel}",
                          fontSize = 13.sp,
                          fontWeight = FontWeight.Bold,
                          color = ElectricGold
                        )
                      }
                      if (giveImpostorHint) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                          modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF2B1321))
                            .border(1.dp, NeonPink, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                        ) {
                          Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                              text = "💡 PISTA RELACIONADA:",
                              fontSize = 11.sp,
                              fontWeight = FontWeight.Black,
                              color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                              text = secretEntry.hint,
                              fontSize = 22.sp,
                              fontWeight = FontWeight.Black,
                              color = TextPrimary,
                              textAlign = TextAlign.Center
                            )
                          }
                        }
                      }
                    } else {
                      Text(text = "✅", fontSize = 50.sp)
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                        text = "ERES INOCENTE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricGreen
                      )
                      if (!isRandomMixedMode) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                          text = "Categoría: ${secretEntry.categoryLabel}",
                          fontSize = 12.sp,
                          color = TextSecondary
                        )
                      }
                      Spacer(modifier = Modifier.height(12.dp))
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .clip(RoundedCornerShape(14.dp))
                          .background(Color(0xFF142B22))
                          .border(1.dp, ElectricGreen, RoundedCornerShape(14.dp))
                          .padding(16.dp),
                        contentAlignment = Alignment.Center
                      ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                          Text(
                            text = "PALABRA SECRETA:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                          )
                          Spacer(modifier = Modifier.height(4.dp))
                          Text(
                            text = secretEntry.word,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center
                          )
                        }
                      }
                    }
                  }
                }
              }

              if (!isCardRevealed) {
                Button(
                  onClick = {
                    HapticHelper.click(context)
                    isCardRevealed = true
                  },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                  shape = RoundedCornerShape(18.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                ) {
                  Icon(Icons.Default.Visibility, contentDescription = null)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "VER MI ROL SECRETO",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                  )
                }
              } else {
                Button(
                  onClick = {
                    HapticHelper.click(context)
                    isCardRevealed = false
                    if (revealIndex + 1 < matchPlayers.size) {
                      revealIndex++
                    } else {
                      SoundHelper.playCountdownBeep(isFinal = true)
                      phase = ImpostorPhase.DEBATE
                    }
                  },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                  shape = RoundedCornerShape(18.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
                ) {
                  Icon(Icons.Default.VisibilityOff, contentDescription = null)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = if (revealIndex + 1 < matchPlayers.size) "OCULTAR Y PASAR AL SIGUIENTE" else "¡EMPEZAR RONDA DE PISTAS!",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                  )
                }
              }
            }
          }
        }

        ImpostorPhase.DEBATE -> {
          val activePlayers = matchPlayers.filter { !it.isEliminated }
          val remainingImpostors = activePlayers.count { it.isImpostor }
          val remainingInnocents = activePlayers.count { !it.isImpostor }

          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Card(
              colors = CardDefaults.cardColors(containerColor = SurfaceDark),
              shape = RoundedCornerShape(22.dp),
              modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, ElectricGold, RoundedCornerShape(22.dp))
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "🗣️ RONDA DE PISTAS Y DEBATE",
                  fontWeight = FontWeight.Black,
                  fontSize = 16.sp,
                  color = ElectricGold
                )
                if (!isRandomMixedMode) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "Categoría: ${secretEntry.categoryLabel}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeonCyan
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "🕵️ Impostores activos: $remainingImpostors • Inocentes: $remainingInnocents",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = NeonPink
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceVariantDark)
                    .padding(14.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                      text = "Empieza dando la primera pista:",
                      fontSize = 12.sp,
                      color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "👉 $starterPlayerName",
                      fontSize = 22.sp,
                      fontWeight = FontWeight.Black,
                      color = Color.White
                    )
                  }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "Cada jugador dice una palabra relacionada en círculo. ¡Luego voten para expulsar al sospechoso!",
                  fontSize = 12.sp,
                  color = TextSecondary,
                  textAlign = TextAlign.Center
                )
              }
            }

            Column(modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 12.dp)) {
              Text(
                text = "Jugadores en la partida (${activePlayers.size}):",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextSecondary
              )
              Spacer(modifier = Modifier.height(8.dp))
              LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(activePlayers, key = { it.id }) { p ->
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
                      Text(
                        text = "👤 ${p.name}",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 15.sp
                      )
                      if (p.name == starterPlayerName) {
                        Text(
                          text = "Inicia ronda",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = ElectricGold
                        )
                      }
                    }
                  }
                }
              }
            }

            Button(
              onClick = {
                HapticHelper.click(context)
                phase = ImpostorPhase.VOTING
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
              shape = RoundedCornerShape(18.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
            ) {
              Icon(Icons.Default.HowToVote, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "🗳️ VOTAR Y SACAR A UN JUGADOR",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
              )
            }
          }
        }

        ImpostorPhase.VOTING -> {
          val activePlayers = matchPlayers.filter { !it.isEliminated }
          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "🗳️ VOTACIÓN MANUAL DEL GRUPO",
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              color = ElectricGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "A la cuenta de 3, señalen al sospechoso. Toca al jugador más votado para expulsarlo de la partida:",
              fontSize = 13.sp,
              color = TextSecondary,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(activePlayers, key = { it.id }) { p ->
                Card(
                  colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2E334D), RoundedCornerShape(16.dp))
                    .clickable {
                      val idx = matchPlayers.indexOfFirst { it.id == p.id }
                      if (idx >= 0) {
                        val updated = matchPlayers[idx].copy(isEliminated = true)
                        matchPlayers[idx] = updated
                        lastEjectedPlayer = updated

                        val remainingImpostors = matchPlayers.count { !it.isEliminated && it.isImpostor }
                        val remainingInnocents = matchPlayers.count { !it.isEliminated && !it.isImpostor }

                        if (remainingImpostors == 0) {
                          // All impostors caught -> Innocents Win!
                          HapticHelper.vibrateOnly(context, 90L)
                          SoundHelper.playVictory()
                          captureRoundSummary(didInnocentsWin = true)
                          phase = ImpostorPhase.GAME_OVER
                          onRecordResult(
                            600,
                            "El Impostor: Descubrieron a ${roundOverSummary.impostorNames} (Palabra: ${secretEntry.word})",
                            "Modo El Impostor",
                            true
                          )
                        } else if (remainingImpostors >= remainingInnocents) {
                          // Impostors equal (or outnumber) Innocents -> Impostors Win!
                          HapticHelper.vibrateOnly(context, 160L)
                          SoundHelper.playBuzzer()
                          captureRoundSummary(didInnocentsWin = false)
                          phase = ImpostorPhase.GAME_OVER
                          onRecordResult(
                            400,
                            "El Impostor: Ganó ${roundOverSummary.impostorNames} al igualar a los inocentes (${secretEntry.word})",
                            "Modo El Impostor",
                            false
                          )
                        } else {
                          // Game continues: reveal whether the ejected player was an Impostor or Innocent
                          HapticHelper.vibrateOnly(context, 120L)
                          if (updated.isImpostor) {
                            SoundHelper.playSuccess()
                          } else {
                            SoundHelper.playBuzzer()
                          }
                          phase = ImpostorPhase.ELIMINATION_REVEAL
                        }
                      }
                    }
                ) {
                  Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(text = "🎯", fontSize = 22.sp)
                      Spacer(modifier = Modifier.width(12.dp))
                      Text(text = p.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
                    }
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonPink)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                      Text(text = "Expulsar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Button(
              onClick = {
                HapticHelper.click(context)
                phase = ImpostorPhase.DEBATE
              },
              modifier = Modifier.fillMaxWidth().height(52.dp),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = TextSecondary)
            ) {
              Text("Volver al Debate", fontWeight = FontWeight.Bold)
            }
          }
        }

        ImpostorPhase.ELIMINATION_REVEAL -> {
          val ejected = lastEjectedPlayer
          val wasImpostor = ejected?.isImpostor == true
          val remainingActive = matchPlayers.filter { !it.isEliminated }
          val remainingImpostors = remainingActive.count { it.isImpostor }
          val remainingInnocents = remainingActive.count { !it.isImpostor }

          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Card(
              colors = CardDefaults.cardColors(containerColor = SurfaceDark),
              shape = RoundedCornerShape(22.dp),
              modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, if (wasImpostor) ElectricGreen else NeonPink, RoundedCornerShape(22.dp))
            ) {
              Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = if (wasImpostor) "🕵️" else "😱", fontSize = 56.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "${ejected?.name ?: "Jugador"} ha sido expulsado...",
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Black,
                  color = TextPrimary,
                  textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = if (wasImpostor) "¡¡SÍ ERA UN IMPOSTOR!! ✅" else "¡¡ERA INOCENTE!! ❌",
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Black,
                  color = if (wasImpostor) ElectricGreen else NeonPink,
                  textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "Quedan $remainingImpostors impostor${if (remainingImpostors > 1) "es" else ""} y $remainingInnocents inocentes en juego.\n(Los impostores ganan si igualan a los inocentes)",
                  fontSize = 13.sp,
                  color = TextSecondary,
                  textAlign = TextAlign.Center
                )
              }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
              onClick = {
                HapticHelper.click(context)
                val active = matchPlayers.filter { !it.isEliminated }
                val imps = active.filter { it.isImpostor }
                val inno = active.filter { !it.isImpostor }
                starterPlayerName = if (Random.nextFloat() < 0.12f && imps.isNotEmpty()) {
                  imps.random().name
                } else {
                  inno.randomOrNull()?.name ?: active.first().name
                }
                phase = ImpostorPhase.DEBATE
              },
              modifier = Modifier.fillMaxWidth().height(60.dp),
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
            ) {
              Text("Seguir Debatiendo y Votar de Nuevo", fontWeight = FontWeight.Black, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
              onClick = {
                HapticHelper.click(context)
                captureRoundSummary(didInnocentsWin = false)
                phase = ImpostorPhase.GAME_OVER
              },
              modifier = Modifier.fillMaxWidth().height(52.dp),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = TextSecondary)
            ) {
              Text("Rendirse y Revelar Roles", fontWeight = FontWeight.Bold)
            }
          }
        }

        ImpostorPhase.GAME_OVER -> {
          // Render exclusively from the frozen roundOverSummary snapshot so starting a new round
          // NEVER flashes the new secret word while GAME_OVER fades out!
          val summary = roundOverSummary
          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Card(
              colors = CardDefaults.cardColors(containerColor = SurfaceDark),
              shape = RoundedCornerShape(24.dp),
              modifier = Modifier
                .fillMaxWidth()
                .border(
                  width = 2.dp,
                  color = if (summary.innocentsWon) ElectricGreen else NeonPink,
                  shape = RoundedCornerShape(24.dp)
                )
            ) {
              Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = if (summary.innocentsWon) {
                    "🎉 ¡GANAN LOS INOCENTES! 🎉"
                  } else if (summary.impostorCount > 1) {
                    "🕵️ ¡GANARON LOS IMPOSTORES! 🕵️"
                  } else {
                    "🕵️ ¡GANÓ EL IMPOSTOR! 🕵️"
                  },
                  fontSize = 22.sp,
                  fontWeight = FontWeight.Black,
                  color = if (summary.innocentsWon) ElectricGreen else NeonPink,
                  textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  text = if (summary.impostorCount > 1) "Los Impostores eran:" else "El Impostor era:",
                  fontSize = 13.sp,
                  color = TextSecondary
                )
                Text(
                  text = summary.impostorNames,
                  fontSize = 26.sp,
                  fontWeight = FontWeight.Black,
                  color = ElectricGold,
                  textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceVariantDark)
                    .padding(14.dp)
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Palabra Secreta (${summary.categoryLabel}):", fontSize = 12.sp, color = NeonCyan)
                    Text(text = summary.word, fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
                    if (giveImpostorHint) {
                      Spacer(modifier = Modifier.height(6.dp))
                      Text(
                        text = "Pista del Impostor: \"${summary.hint}\"",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                      )
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
              onClick = {
                HapticHelper.click(context)
                startNewImpostorMatch()
              },
              modifier = Modifier.fillMaxWidth().height(60.dp),
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Jugar Otra Ronda Rápida", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
              onClick = {
                HapticHelper.click(context)
                phase = ImpostorPhase.SETUP
              },
              modifier = Modifier.fillMaxWidth().height(52.dp),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = NeonCyan)
            ) {
              Text("Cambiar Temática o Jugadores", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
