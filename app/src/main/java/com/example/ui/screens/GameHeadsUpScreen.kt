package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.HapticHelper
import com.example.ui.util.SoundHelper
import kotlinx.coroutines.delay

data class HeadsUpTheme(
  val id: String,
  val name: String,
  val icon: String,
  val words: List<String>
)

val HeadsUpThemes = listOf(
  HeadsUpTheme(
    id = "NUMBERS",
    name = "Sólo Números",
    icon = "🔢",
    words = listOf(
      "7", "100", "2026", "50", "12", "99", "0", "1 millón",
      "77", "18", "69", "24", "365", "10", "42", "3.14",
      "21", "88", "13", "500", "1999", "33", "2000", "777",
      "5000", "666", "1000", "404", "15", "8", "90", "2024",
      "11", "300", "2500", "45", "16", "2", "5", "80",
      "10000", "4", "30", "14", "9", "3", "1", "60",
      "25", "17", "70", "40", "100K", "555", "1234"
    )
  ),
  HeadsUpTheme(
    id = "MOVIES",
    name = "Películas y Series",
    icon = "🎬",
    words = listOf(
      "Titanic", "Harry Potter", "Stranger Things", "Spider-Man",
      "El Rey León", "Shrek", "Avatar", "Jurassic Park", "Avengers",
      "La Casa de Papel", "Batman", "Star Wars", "Toy Story", "Coco",
      "Barbie", "Oppenheimer", "Joker", "Merlina (Wednesday)",
      "Los Simpson", "Breaking Bad", "Rápidos y Furiosos", "Matrix",
      "El Padrino", "Mi Pobre Angelito", "Piratas del Caribe", "El Conjuro",
      "Interestelar", "Crepúsculo", "Shrek 2", "Intensamente", "Moana",
      "Encanto", "Bob Esponja", "Los Juegos del Hambre", "El Señor de los Anillos",
      "Cars (El Rayo McQueen)", "Monsters Inc", "Buscando a Nemo", "Up (Una Aventura de Altura)",
      "Kung Fu Panda", "Madagascar", "Rocky", "Terminator", "El Juego del Calamar"
    )
  ),
  HeadsUpTheme(
    id = "ANIMALS",
    name = "Animales y Bichos",
    icon = "🦁",
    words = listOf(
      "León", "Pingüino", "Delfín", "Elefante", "Jirafa", "Canguro",
      "Tiburón", "Koala", "Águila", "Mono", "Oso Panda", "Serpiente",
      "Caballo", "Lobo", "Camaleón", "Murciélago", "Pulpo", "Hipopótamo",
      "Rinoceronte", "Búho", "Tigre", "Cebra", "Flamenco", "Cocodrilo",
      "Rana", "Tortuga", "Perezoso", "Gorila", "Camello", "Zorro",
      "Mapache", "Erizo", "Gato", "Perro", "Vaca", "Ballena Azul",
      "Loro", "Cangrejo", "Hormiga", "Abeja", "Escorpión", "Guepardo",
      "Oso Polar", "Cisne", "Hámster", "Medusa", "Pavo Real"
    )
  ),
  HeadsUpTheme(
    id = "FOOD",
    name = "Comidas y Bebidas",
    icon = "🍕",
    words = listOf(
      "Pizza", "Hamburguesa", "Tacos", "Sushi", "Helado", "Chocolate",
      "Café", "Papas Fritas", "Empanada", "Paella", "Hot Dog", "Donas",
      "Mate", "Ceviche", "Palomitas de Maíz", "Lasagna", "Churros",
      "Guacamole", "Ramen", "Tarta de Manzana", "Burrito", "Arepa",
      "Croissant", "Salchipapa", "Milanesa", "Tequila", "Nachos",
      "Quesadilla", "Panqueques", "Flan", "Waffles", "Pizza con Piña",
      "Asado", "Salmón", "Pasta Carbonara", "Batido de Fresa", "Mojito",
      "Limonada", "Pollo Frito", "Tamal", "Alitas BBQ", "Brownie",
      "Brownie con Helado", "Fondue de Queso"
    )
  ),
  HeadsUpTheme(
    id = "ACTIONS",
    name = "Acciones y Mímica",
    icon = "🕺",
    words = listOf(
      "Bailar Salsa", "Dormir", "Llorar de Risa", "Cocinar", "Tomar una Selfie",
      "Conducir un Auto", "Tocar la Guitarra", "Nadar", "Cepillarse los dientes",
      "Montar a caballo", "Cantar karaoke", "Jugar al fútbol", "Desfilar como modelo",
      "Hacer pesas en el gym", "Bostezar", "Escalar una montaña", "Pintarse las uñas",
      "Atrapar una mosca", "Pelear en cámara lenta", "Andar en patineta", "Tirarse en paracaídas",
      "Enhebrar una aguja", "Maquillarse", "Cambiar una llanta", "Volar como avión",
      "Ser atacado por abejas", "Estornudar fuerte", "Caminar como pingüino",
      "Comer comida muy picante", "Pisar una pieza de Lego", "Estar en una montaña rusa",
      "Robar un banco", "Hipnotizar a alguien", "Fingir estar desmayado", "Perder el equilibrio"
    )
  ),
  HeadsUpTheme(
    id = "OBJECTS",
    name = "Objetos Cotidianos",
    icon = "📱",
    words = listOf(
      "Teléfono Móvil", "Reloj despertador", "Zapatos deportivos", "Gafas de Sol",
      "Guitarra eléctrica", "Mochila escolar", "Espejo", "Llaves de la casa",
      "Botella de Agua", "Almohada suave", "Paraguas", "Cepillo de pelo",
      "Auriculares inalámbricos", "Sartén", "Control remoto", "Lámpara",
      "Tijeras", "Cuaderno", "Billetera", "Maleta de viaje", "Microondas",
      "Candado", "Gorra", "Toalla", "Enchufe", "Encendedor", "Calculadora",
      "Cepillo de dientes", "Ventilador", "Cámara de fotos", "Pilas", "Plancha"
    )
  ),
  HeadsUpTheme(
    id = "VIRAL_TIKTOK",
    name = "Trends y Memes de TikTok",
    icon = "🔥",
    words = listOf(
      "Baile viral de TikTok", "Skibidi", "Pedro Pedro Pedro", "Mewing",
      "Rizzler", "Filtro de envejecimiento", "POV", "Storytime",
      "GRWM (Arréglate conmigo)", "Trend de la botella", "CapCut template",
      "Lip sync", "Unboxing", "ASMR comiendo", "Dúo viral", "Challenge de baile",
      "NPC en vivo", "La queso", "Chefcito", "Cringe", "Viral en Para Ti (FYP)",
      "Fanum Tax", "Tuki", "Live de TikTok", "Room Tour", "Expectativa vs Realidad",
      "Terapia de compras", "Prank a mi amigo", "Filtro de llanto"
    )
  ),
  HeadsUpTheme(
    id = "CELEBS",
    name = "Famosos y Creadores",
    icon = "🌟",
    words = listOf(
      "Lionel Messi", "Cristiano Ronaldo", "Shakira", "Bad Bunny", "MrBeast",
      "Billie Eilish", "Rosalía", "Taylor Swift", "Elon Musk", "Ibai Llanos",
      "AuronPlay", "Bizarrap", "Peso Pluma", "Daddy Yankee", "Dua Lipa",
      "Selena Gomez", "Dwayne The Rock", "Justin Bieber", "Will Smith",
      "Zendaya", "Tom Holland", "Karol G", "Feid", "Maluma", "Rauw Alejandro",
      "Kylie Jenner", "Beyoncé", "Ariana Grande", "Kanye West", "Speed"
    )
  ),
  HeadsUpTheme(
    id = "SUPERHEROES",
    name = "Superhéroes y Cómics",
    icon = "🦸",
    words = listOf(
      "Spider-Man", "Batman", "Iron Man", "Superman", "Wonder Woman",
      "Thor", "Capitán América", "Hulk", "Deadpool", "Wolverine",
      "Flash", "Joker", "Thanos", "Loki", "Black Panther", "Aquaman",
      "Venom", "Doctor Strange", "Robin", "Groot", "Magneto",
      "Harley Quinn", "Miles Morales", "Carnage", "Capitana Marvel", "Linterna Verde"
    )
  ),
  HeadsUpTheme(
    id = "COUNTRIES",
    name = "Países y Ciudades",
    icon = "🌎",
    words = listOf(
      "México", "España", "Argentina", "Colombia", "Chile", "Perú",
      "Estados Unidos", "Japón", "Brasil", "Italia", "Francia",
      "París", "Tokio", "Nueva York", "Roma", "Londres", "Buenos Aires",
      "Madrid", "Egipto", "Australia", "Canadá", "Alemania",
      "Corea del Sur", "Machu Picchu", "Pirámides de Giza", "Miami", "Río de Janeiro"
    )
  ),
  HeadsUpTheme(
    id = "PROFESSIONS",
    name = "Profesiones y Oficios",
    icon = "💼",
    words = listOf(
      "Médico cirujano", "Astronauta", "Bombero", "Policía", "Chef de cocina",
      "Piloto de avión", "Detective privado", "Profesor de matemáticas", "Youtuber",
      "Dentista", "Fotógrafo", "Veterinario", "Abogado", "Electricista",
      "Albañil", "Dj de discoteca", "Azafata", "Payaso de circo", "Mecánico",
      "Juez", "Arquitecto", "Peluquero", "Buzo profesional", "Entrenador personal"
    )
  ),
  HeadsUpTheme(
    id = "GAMING",
    name = "Videojuegos y Gamers",
    icon = "🎮",
    words = listOf(
      "Minecraft", "Roblox", "Fortnite", "GTA V", "Free Fire", "Among Us",
      "Super Mario", "Zelda", "Call of Duty", "FIFA / EA FC", "Clash Royale",
      "Pac-Man", "Sonic", "PlayStation 5", "Nintendo Switch", "Game Over",
      "Headshot", "Noob", "Gamer Pro", "Brawl Stars", "Counter-Strike", "Tetris"
    )
  ),
  HeadsUpTheme(
    id = "MUSIC",
    name = "Música y Canciones",
    icon = "🎵",
    words = listOf(
      "Despacito", "Bohemian Rhapsody", "Thriller de Michael Jackson", "La Bachata",
      "Gasolina", "Tusa", "Blinding Lights", "Shape of You", "Macarena",
      "Batería", "Saxofón", "Piano de cola", "Micrófono", "Autotune",
      "Concierto en vivo", "Solista", "Bailarín de apoyo", "Solo de guitarra"
    )
  ),
  HeadsUpTheme(
    id = "SPORTS",
    name = "Deportes y Campeones",
    icon = "⚽",
    words = listOf(
      "Fútbol", "Baloncesto (NBA)", "Tenis", "Fórmula 1", "Boxeo",
      "Natación olímpica", "Golf", "Voleibol", "Ciclismo", "Béisbol",
      "Tiro con arco", "Skateboarding", "Maratón", "Gol de chilena",
      "Penalti", "Triple en el último segundo", "Trofeo de la Copa del Mundo"
    )
  )
)

enum class HeadsUpPhase {
  SELECT_THEME,
  READY_COUNTDOWN,
  PLAYING,
  ROUND_OVER
}

@Composable
fun GameHeadsUpScreen(
  onBack: () -> Unit,
  onRecordResult: (score: Int, detail: String, opponent: String, isWin: Boolean) -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val isMuted by SoundHelper.isMuted.collectAsStateWithLifecycle()

  var phase by remember { mutableStateOf(HeadsUpPhase.SELECT_THEME) }
  var selectedTheme by remember { mutableStateOf(HeadsUpThemes[0]) }
  var countdown by remember { mutableIntStateOf(3) }
  var timeLeft by remember { mutableIntStateOf(60) }
  var currentWordIndex by remember { mutableIntStateOf(0) }
  val shuffledWords = remember { mutableStateListOf<String>() }
  var correctCount by remember { mutableIntStateOf(0) }
  var passedCount by remember { mutableIntStateOf(0) }

  // 3-second ready countdown
  LaunchedEffect(phase) {
    if (phase == HeadsUpPhase.READY_COUNTDOWN) {
      countdown = 3
      while (countdown > 0) {
        HapticHelper.vibrateOnly(context, 20L)
        SoundHelper.playCountdownBeep(isFinal = countdown == 1)
        delay(1000)
        countdown--
      }
      phase = HeadsUpPhase.PLAYING
    }
  }

  // 60-second round timer
  LaunchedEffect(phase) {
    if (phase == HeadsUpPhase.PLAYING) {
      timeLeft = 60
      while (timeLeft > 0 && phase == HeadsUpPhase.PLAYING) {
        delay(1000)
        timeLeft--
        if (timeLeft in 1..5 && phase == HeadsUpPhase.PLAYING) {
          SoundHelper.playTick(0.85f)
        }
      }
      if (phase == HeadsUpPhase.PLAYING) {
        HapticHelper.vibrateOnly(context, 100L)
        SoundHelper.playVictory()
        phase = HeadsUpPhase.ROUND_OVER
        onRecordResult(
          correctCount * 100,
          "Adivina la Palabra (${selectedTheme.name}): $correctCount aciertos",
          "Modo Frente",
          correctCount >= 5
        )
      }
    }
  }

  val currentWord = shuffledWords.getOrNull(currentWordIndex) ?: "¡Terminaron las palabras!"

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
        IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Adivina la Palabra",
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
      label = "headsup_phase"
    ) { currentPhase ->
      when (currentPhase) {
        HeadsUpPhase.SELECT_THEME -> {
          Column(modifier = Modifier.fillMaxSize()) {
            Text(
              text = "Selecciona una Temática para Jugar:",
              fontWeight = FontWeight.Bold,
              color = TextSecondary,
              fontSize = 15.sp,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(HeadsUpThemes, key = { it.id }) { theme ->
                val isSelected = selectedTheme == theme
                Card(
                  colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF1B2338) else SurfaceDark
                  ),
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .border(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) NeonPink else Color(0xFF282C40),
                      shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                      HapticHelper.click(context)
                      selectedTheme = theme
                    }
                ) {
                  Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(text = theme.icon, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text(text = theme.name, fontWeight = FontWeight.Black, fontSize = 17.sp, color = TextPrimary)
                      Text(text = "${theme.words.size} palabras listas", fontSize = 12.sp, color = TextSecondary)
                    }
                    if (isSelected) {
                      Text(text = "SELECCIONADO", fontWeight = FontWeight.Black, fontSize = 11.sp, color = NeonPink)
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
              onClick = {
                HapticHelper.click(context)
                shuffledWords.clear()
                shuffledWords.addAll(selectedTheme.words.shuffled())
                currentWordIndex = 0
                correctCount = 0
                passedCount = 0
                phase = HeadsUpPhase.READY_COUNTDOWN
              },
              modifier = Modifier.fillMaxWidth().height(64.dp),
              shape = RoundedCornerShape(18.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "¡COLOCAR EN LA FRENTE Y JUGAR!", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
          }
        }

        HeadsUpPhase.READY_COUNTDOWN -> {
          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = "📱 COLOCA EL TELÉFONO EN TU FRENTE",
              fontWeight = FontWeight.Black,
              fontSize = 18.sp,
              color = ElectricGold,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "La pantalla debe apuntar hacia tus amigos.\nEllos te darán pistas para que adivines.",
              fontSize = 14.sp,
              color = TextSecondary,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "$countdown", fontSize = 80.sp, fontWeight = FontWeight.Black, color = NeonCyan)
          }
        }

        HeadsUpPhase.PLAYING -> {
          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "⏳ ${timeLeft}s",
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = if (timeLeft <= 10) NeonPink else ElectricGold
              )
              Text(text = "Aciertos: $correctCount", fontWeight = FontWeight.Black, fontSize = 20.sp, color = ElectricGreen)
            }

            // Big Word Card with smooth AnimatedContent
            Card(
              colors = CardDefaults.cardColors(containerColor = SurfaceDark),
              shape = RoundedCornerShape(24.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .border(3.dp, NeonCyan, RoundedCornerShape(24.dp))
            ) {
              Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
              ) {
                AnimatedContent(
                  targetState = currentWord,
                  transitionSpec = {
                    (fadeIn(tween(180)) + scaleIn(initialScale = 0.88f, animationSpec = tween(180))) togetherWith
                      fadeOut(tween(120))
                  },
                  label = "word_transition"
                ) { word ->
                  Text(
                    text = word,
                    fontSize = if (word.length > 15) 36.sp else 48.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 52.sp
                  )
                }
              }
            }

            Text(
              text = "Temática: ${selectedTheme.name}",
              fontSize = 14.sp,
              color = TextSecondary,
              fontWeight = FontWeight.Bold
            )

            // Guess Action Controls
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              Button(
                onClick = {
                  HapticHelper.vibrateOnly(context, 18L)
                  SoundHelper.playPass()
                  passedCount++
                  if (shuffledWords.isNotEmpty()) {
                    currentWordIndex = (currentWordIndex + 1) % shuffledWords.size
                  }
                },
                modifier = Modifier.weight(1f).height(74.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF381522), contentColor = NeonPink)
              ) {
                Icon(Icons.Default.Close, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("PASAR", fontWeight = FontWeight.Black, fontSize = 18.sp)
              }

              Button(
                onClick = {
                  HapticHelper.success(context)
                  correctCount++
                  if (shuffledWords.isNotEmpty()) {
                    currentWordIndex = (currentWordIndex + 1) % shuffledWords.size
                  }
                },
                modifier = Modifier.weight(1.3f).height(74.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = Color.Black)
              ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("¡ACIERTO!", fontWeight = FontWeight.Black, fontSize = 18.sp)
              }
            }
          }
        }

        HeadsUpPhase.ROUND_OVER -> {
          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Text(text = "🎉 ¡TIEMPO TERMINADO! 🎉", fontSize = 24.sp, fontWeight = FontWeight.Black, color = ElectricGold)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
              colors = CardDefaults.cardColors(containerColor = SurfaceDark),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF282C40), RoundedCornerShape(20.dp))
            ) {
              Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "$correctCount", fontSize = 64.sp, fontWeight = FontWeight.Black, color = ElectricGreen)
                Text(text = "Palabras Adivinadas", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Pasadas: $passedCount • Temática: ${selectedTheme.name}", fontSize = 13.sp, color = TextSecondary)
              }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
              onClick = {
                HapticHelper.click(context)
                phase = HeadsUpPhase.SELECT_THEME
              },
              modifier = Modifier.fillMaxWidth().height(60.dp),
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Jugar Otra Ronda", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
          }
        }
      }
    }
  }
}
