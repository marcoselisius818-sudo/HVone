package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

private val AnimalEntries = listOf(
  ImpostorWordEntry("Camaleón", "Se adapta silenciosamente a su entorno y cambia según la situación", "Animales"),
  ImpostorWordEntry("Delfín", "Muy inteligente, sociable y da grandes saltos en el agua", "Animales"),
  ImpostorWordEntry("León", "Símbolo de realeza salvaje, liderazgo y melena imponente", "Animales"),
  ImpostorWordEntry("Pingüino", "Siempre parece vestir de gala y camina de forma peculiar en el frío", "Animales"),
  ImpostorWordEntry("Pulpo", "Maestro del camuflaje marino con extremidades muy versátiles", "Animales"),
  ImpostorWordEntry("Águila Real", "Visión aguda desde las alturas y vuelo majestuoso", "Animales"),
  ImpostorWordEntry("Lobo", "Lealtad a su manada y famoso por sus sonidos bajo la luna", "Animales"),
  ImpostorWordEntry("Oso Panda", "Tranquilo, adorable y fanático de masticar tallos verdes todo el día", "Animales"),
  ImpostorWordEntry("Canguro", "Se desplaza con grandes impulsos y lleva todo consigo adelante", "Animales"),
  ImpostorWordEntry("Tiburón Blanco", "Depredador temido que nunca deja de moverse bajo la superficie", "Animales"),
  ImpostorWordEntry("Perezoso", "Se toma la vida con muchísima calma en lo alto de los árboles", "Animales"),
  ImpostorWordEntry("Flamenco", "Elegante, de tono rosado y famoso por mantener el equilibrio en una pata", "Animales"),
  ImpostorWordEntry("Cobra Real", "Postura hipnótica y sigilosa que impone respeto inmediato", "Animales"),
  ImpostorWordEntry("Gorila", "Fuerza colosal en la selva y se golpea el pecho para imponer presencia", "Animales"),
  ImpostorWordEntry("Búho", "Vigilante nocturno que puede girar la cabeza casi por completo", "Animales"),
  ImpostorWordEntry("Guepardo", "Aceleración pura y manchas que corren a máxima velocidad", "Animales"),
  ImpostorWordEntry("Ornitorrinco", "Una combinación tan rara de rasgos que parece inventado", "Animales"),
  ImpostorWordEntry("Murciélago", "Sale de noche, se orienta con eco y duerme de cabeza", "Animales"),
  ImpostorWordEntry("Zorro", "Famoso en las fábulas por su astucia y cola esponjosa", "Animales"),
  ImpostorWordEntry("Ballena Azul", "El gigante más colosal y pacífico de las profundidades", "Animales"),
  ImpostorWordEntry("Escorpión", "Pequeño, blindado y con una defensa peligrosa en la punta de la cola", "Animales"),
  ImpostorWordEntry("Pavo Real", "Exhibe un abanico espectacular de colores para llamar la atención", "Animales")
)

private val ObjectEntries = listOf(
  ImpostorWordEntry("Brújula", "Nunca pierde el norte cuando estás desorientado", "Objetos"),
  ImpostorWordEntry("Reloj de Arena", "Mide algo que cae grano a grano y se reinicia al darle la vuelta", "Objetos"),
  ImpostorWordEntry("Paraguas", "Se abre solo cuando el cielo se pone difícil y suele olvidarse al salir", "Objetos"),
  ImpostorWordEntry("Espejo", "Siempre te devuelve exactamente lo que tiene enfrente sin mentir", "Objetos"),
  ImpostorWordEntry("Candado", "Guarda secretos bajo llave y requiere una combinación o pieza exacta", "Objetos"),
  ImpostorWordEntry("Telescopio", "Acerca mundos lejanos que brillan en la oscuridad", "Objetos"),
  ImpostorWordEntry("Microondas", "Calienta todo en segundos mientras da vueltas por dentro", "Objetos"),
  ImpostorWordEntry("Extintor", "Siempre está colgado esperando una emergencia de calor para actuar", "Objetos"),
  ImpostorWordEntry("Auriculares", "Te aíslan del mundo exterior para que solo escuches tu propio ritmo", "Objetos"),
  ImpostorWordEntry("Maleta de Viaje", "Va llena de ropa e ilusiones antes de pasar por el aeropuerto", "Objetos"),
  ImpostorWordEntry("Termómetro", "Sube o baja para delatar si algo está hirviendo o tienes fiebre", "Objetos"),
  ImpostorWordEntry("Lupa", "Hace que los detalles más pequeños se vean gigantes para investigar", "Objetos"),
  ImpostorWordEntry("Imán", "Atrae metales sin necesidad de pegamento", "Objetos"),
  ImpostorWordEntry("Ancla", "Pesada y metálica; sirve para quedarse firme en un solo lugar en el mar", "Objetos"),
  ImpostorWordEntry("Paracaídas", "Tu única salvación de tela cuando saltas desde muy alto", "Objetos"),
  ImpostorWordEntry("Bumerán", "Por más lejos que lo lances con fuerza, busca volver a tu mano", "Objetos"),
  ImpostorWordEntry("Semáforo", "Controla el caos de la calle cambiando entre tres colores", "Objetos"),
  ImpostorWordEntry("Cofre del Tesoro", "Madera antigua y metal que esconde riquezas bajo llave", "Objetos"),
  ImpostorWordEntry("Billetera", "Viaja en el bolsillo con documentos, tarjetas y algo de efectivo", "Objetos"),
  ImpostorWordEntry("Tijeras", "Dos hojas afiladas que solo funcionan trabajando juntas para cortar", "Objetos")
)

private val AbstractEntries = listOf(
  ImpostorWordEntry("El Tiempo", "Avanza sin detenerse jamás, no se puede tocar y vale oro", "Cosas Abstractas"),
  ImpostorWordEntry("La Nostalgia", "Esa emoción suave y agridulce al recordar buenos momentos del pasado", "Cosas Abstractas"),
  ImpostorWordEntry("El Karma", "La ley invisible de que todo lo que haces tarde o temprano regresa", "Cosas Abstractas"),
  ImpostorWordEntry("El Silencio", "Existe cuando nadie habla y se rompe en cuanto lo nombras", "Cosas Abstractas"),
  ImpostorWordEntry("La Gravedad", "Fuerza invisible que nos mantiene con los pies en la tierra", "Cosas Abstractas"),
  ImpostorWordEntry("Un Secreto", "Algo que deja de serlo si se lo cuentas a demasiadas personas", "Cosas Abstractas"),
  ImpostorWordEntry("La Suerte", "El azar inexplicable que hace que todo salga bien en el momento justo", "Cosas Abstractas"),
  ImpostorWordEntry("El Destino", "La idea de que el futuro ya está escrito sin importar qué camino tomes", "Cosas Abstractas"),
  ImpostorWordEntry("Un Sueño", "Historia vívida que ocurre en tu mente mientras duermes", "Cosas Abstractas"),
  ImpostorWordEntry("La Paciencia", "La virtud de saber esperar con calma sin desesperarse", "Cosas Abstractas"),
  ImpostorWordEntry("El Caos", "Desorden total donde nada sigue las reglas habituales", "Cosas Abstractas"),
  ImpostorWordEntry("La Libertad", "Poder elegir tu propio camino sin ataduras ni jaulas", "Cosas Abstractas"),
  ImpostorWordEntry("La Ironía", "Cuando ocurre o se dice exactamente lo contrario de lo que se esperaba", "Cosas Abstractas"),
  ImpostorWordEntry("Un Recuerdo", "Una fotografía mental que guardas en la memoria con los años", "Cosas Abstractas"),
  ImpostorWordEntry("El Orgullo", "Sentimiento fuerte que a veces impide pedir perdón o reconocer un fallo", "Cosas Abstractas"),
  ImpostorWordEntry("El Infinito", "Algo que no tiene principio ni final y se representa con un ocho acostado", "Cosas Abstractas"),
  ImpostorWordEntry("Un Rumor", "Algo que corre de boca en boca sin saber si es verdad o mentira", "Cosas Abstractas"),
  ImpostorWordEntry("Una Promesa", "Un compromiso de palabra que no debería romperse nunca", "Cosas Abstractas"),
  ImpostorWordEntry("El Miedo", "Alerta interna que acelera el corazón ante lo desconocido", "Cosas Abstractas"),
  ImpostorWordEntry("La Envidia", "Deseo incómodo por lo que otra persona tiene o logra", "Cosas Abstractas")
)

private val SeriesEntries = listOf(
  ImpostorWordEntry("Stranger Things", "Grupo de amigos en bicicleta, luces navideñas y otra dimensión ochentera", "Series Populares"),
  ImpostorWordEntry("La Casa de Papel", "Overoles rojos, máscaras famosas y un plan milimétrico del Profesor", "Series Populares"),
  ImpostorWordEntry("El Juego del Calamar", "Juegos infantiles tradicionales con un premio millonario y guardias de rosa", "Series Populares"),
  ImpostorWordEntry("Breaking Bad", "Química de alto nivel en el desierto y una transformación implacable", "Series Populares"),
  ImpostorWordEntry("Los Simpson", "Familia amarilla legendaria que parece haber predicho todo lo que pasa", "Series Populares"),
  ImpostorWordEntry("Merlina (Wednesday)", "Humor negro, trenzas oscuras, violonchelo y una academia para raros", "Series Populares"),
  ImpostorWordEntry("Game of Thrones", "Reinos rivales, dragones, invierno amenazante y lucha por el poder", "Series Populares"),
  ImpostorWordEntry("The Walking Dead", "Supervivencia extrema en un mundo invadido por caminantes sin vida", "Series Populares"),
  ImpostorWordEntry("Friends", "Seis amigos inseparables, un sofá en una cafetería y risas en Nueva York", "Series Populares"),
  ImpostorWordEntry("Peaky Blinders", "Boinas con filo, trajes elegantes y negocios turbios en Inglaterra", "Series Populares"),
  ImpostorWordEntry("Cobra Kai", "Rivalidad de karate que regresa décadas después con nuevos alumnos", "Series Populares"),
  ImpostorWordEntry("Rick y Morty", "Un abuelo científico loco y su nieto viajando por portales interdimensionales", "Series Populares"),
  ImpostorWordEntry("Black Mirror", "Historias independientes sobre el lado más inquietante de la tecnología", "Series Populares"),
  ImpostorWordEntry("The Boys", "Cuando los superhéroes famosos son en realidad corporativos y corruptos", "Series Populares"),
  ImpostorWordEntry("One Piece", "Búsqueda del tesoro definitivo navegando con una tripulación muy leal", "Series Populares"),
  ImpostorWordEntry("Bob Esponja", "Aventuras bajo el mar, hamburguesas secretas y una piña como hogar", "Series Populares"),
  ImpostorWordEntry("Dragon Ball Z", "Guerreros que entrenan al límite, esferas mágicas y transformaciones doradas", "Series Populares"),
  ImpostorWordEntry("The Last of Us", "Un viaje peligroso cruzando un país devastado por una infección de hongos", "Series Populares"),
  ImpostorWordEntry("Dark", "Una cueva en el bosque alemán y misterios familiares a través de las décadas", "Series Populares"),
  ImpostorWordEntry("The Mandalorian", "Un cazarrecompensas con casco metálico protegiendo a una criatura pequeña", "Series Populares")
)

private val MovieEntries = listOf(
  ImpostorWordEntry("Titanic", "Un romance inolvidable a bordo de un gigante de lujo en aguas heladas", "Películas Populares"),
  ImpostorWordEntry("Avatar", "Un planeta exuberante de seres altos y azules conectados con la naturaleza", "Películas Populares"),
  ImpostorWordEntry("Harry Potter", "Un colegio oculto, varitas, cicatriz famosa y amistad contra la oscuridad", "Películas Populares"),
  ImpostorWordEntry("El Rey León", "El ciclo de la vida en la sabana africana y el regreso del heredero", "Películas Populares"),
  ImpostorWordEntry("Toy Story", "Cobra vida secreta en la habitación en cuanto los humanos no están mirando", "Películas Populares"),
  ImpostorWordEntry("Shrek", "Un pantano tranquilo, un burro parlanchín y un cuento de hadas al revés", "Películas Populares"),
  ImpostorWordEntry("Avengers: Endgame", "Gemas cósmicas, viajes en el tiempo y la batalla final de todos los héroes", "Películas Populares"),
  ImpostorWordEntry("Spider-Man", "Saltos entre rascacielos, sentido de alerta y gran responsabilidad juvenil", "Películas Populares"),
  ImpostorWordEntry("Jurassic Park", "Una isla temática donde la ciencia revivió gigantes prehistóricos", "Películas Populares"),
  ImpostorWordEntry("Coco", "Música de guitarra, flores naranjas y un viaje colorido para recordar a la familia", "Películas Populares"),
  ImpostorWordEntry("Joker", "Una risa incontrolable, maquillaje colorido y caos en una ciudad sombría", "Películas Populares"),
  ImpostorWordEntry("Matrix", "Elegir entre dos pastillas para descubrir que la realidad es una simulación", "Películas Populares"),
  ImpostorWordEntry("Rápidos y Furiosos", "Motores al límite, adrenalina en el asfalto y la familia como lema", "Películas Populares"),
  ImpostorWordEntry("Interestelar", "Un viaje espacial desesperado a través de un agujero de gusano por la humanidad", "Películas Populares"),
  ImpostorWordEntry("El Padrino", "Honor familiar, trajes clásicos y ofertas que nadie puede rechazar", "Películas Populares"),
  ImpostorWordEntry("Piratas del Caribe", "Barcos fantasma, brújula extraña y un capitán muy excéntrico en el mar", "Películas Populares"),
  ImpostorWordEntry("Barbie", "Un mundo perfectamente rosa que choca de frente con el mundo real", "Películas Populares"),
  ImpostorWordEntry("El Señor de los Anillos", "Una larga caminata hacia un volcán para destruir una joya muy peligrosa", "Películas Populares"),
  ImpostorWordEntry("Mi Pobre Angelito", "Quedarse solo en casa en Navidad y defenderla con trampas ingeniosas", "Películas Populares"),
  ImpostorWordEntry("Intensamente", "Pequeños personajes de colores manejando el panel de control de la mente", "Películas Populares")
)

private val StyleEntries = listOf(
  ImpostorWordEntry("Zapatillas Jordan", "Calzado icónico nacido en las canchas que hoy es joya de colección urbana", "Estilo y Moda"),
  ImpostorWordEntry("Chaqueta de Cuero", "Prenda con actitud rebelde, rockera y clásica que nunca pasa de moda", "Estilo y Moda"),
  ImpostorWordEntry("Gafas de Sol Vintage", "Accesorio oscuro para los ojos con estética retro y misteriosa", "Estilo y Moda"),
  ImpostorWordEntry("Alfombra Roja", "El pasillo donde las estrellas posan ante miles de cámaras con sus mejores galas", "Estilo y Moda"),
  ImpostorWordEntry("Perfume Francés", "Aroma elegante en un frasco pequeño que deja huella al pasar", "Estilo y Moda"),
  ImpostorWordEntry("Tatuaje Minimalista", "Un diseño pequeño de tinta en la piel con significado personal", "Estilo y Moda"),
  ImpostorWordEntry("Traje de Gala (Esmoquin)", "La máxima etiqueta de elegancia para una noche de premiación o boda", "Estilo y Moda"),
  ImpostorWordEntry("Ropa Oversize", "Estilo urbano de llevar prendas varias tallas más holgadas y cómodas", "Estilo y Moda"),
  ImpostorWordEntry("Corte Degradado (Fade)", "Estilo de barbería muy pulido donde el cabello se difumina gradualmente", "Estilo y Moda"),
  ImpostorWordEntry("Reloj de Lujo", "Joya de precisión en la muñeca que simboliza estatus y puntualidad", "Estilo y Moda"),
  ImpostorWordEntry("Pasarela de Moda", "Escenario largo donde se camina con actitud mostrando la nueva colección", "Estilo y Moda"),
  ImpostorWordEntry("Cadena de Oro", "Accesorio brillante en el cuello muy popular en la cultura urbana", "Estilo y Moda"),
  ImpostorWordEntry("Tacones de Aguja", "Calzado que estiliza la altura con un soporte muy fino y elegante", "Estilo y Moda"),
  ImpostorWordEntry("Botas Vaqueras", "Calzado de piel con punta marcada y espíritu del viejo oeste", "Estilo y Moda"),
  ImpostorWordEntry("Gorra Urbana", "Accesorio en la cabeza con visera para protegerse del sol o completar el outfit", "Estilo y Moda")
)

private val FoodAndPlacesEntries = listOf(
  ImpostorWordEntry("Pizza Napolitana", "Masa redonda horneada con queso derretido que se comparte en triángulos", "Comidas y Lugares"),
  ImpostorWordEntry("Tacos al Pastor", "Tortilla suave con carne marinada, piña y salsa picante", "Comidas y Lugares"),
  ImpostorWordEntry("Sushi", "Bocados japoneses de arroz y pescado fresco que se comen con palillos", "Comidas y Lugares"),
  ImpostorWordEntry("Hamburguesa Doble", "Entre dos panes con carne, queso fundido y papas como acompañante", "Comidas y Lugares"),
  ImpostorWordEntry("Helado de Chocolate", "Postre frío y cremoso que se derrite rápido si hace calor", "Comidas y Lugares"),
  ImpostorWordEntry("Torre Eiffel", "Estructura de hierro famosa en la ciudad del romance", "Comidas y Lugares"),
  ImpostorWordEntry("Pirámides de Egipto", "Monumentos antiguos y gigantes en medio de la arena del desierto", "Comidas y Lugares"),
  ImpostorWordEntry("Isla Desierta", "Rodeada de mar por todas partes con apenas una palmera y arena", "Comidas y Lugares"),
  ImpostorWordEntry("Triángulo de las Bermudas", "Zona del océano famosa por leyendas de barcos y aviones desaparecidos", "Comidas y Lugares"),
  ImpostorWordEntry("Casino de Las Vegas", "Luces de neón, ruletas, cartas y apuestas que no duermen de noche", "Comidas y Lugares"),
  ImpostorWordEntry("Estación Espacial", "Laboratorio flotando en órbita donde los astronautas ven la Tierra desde arriba", "Comidas y Lugares"),
  ImpostorWordEntry("Área 51", "Base secreta en el desierto rodeada de teorías sobre ovnis y extraterrestres", "Comidas y Lugares"),
  ImpostorWordEntry("Parque de Diversiones", "Montañas rusas, algodón de azúcar y gritos de adrenalina", "Comidas y Lugares"),
  ImpostorWordEntry("Crucero de Lujo", "Un hotel gigante con piscinas y restaurantes que navega por el mar", "Comidas y Lugares")
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
  var phase by remember { mutableStateOf(ImpostorPhase.SETUP) }

  // Active match state
  val matchPlayers = remember { mutableStateListOf<ImpostorPlayerState>() }
  var secretEntry by remember { mutableStateOf(AnimalEntries[0]) }
  var revealIndex by remember { mutableIntStateOf(0) }
  var isCardRevealed by remember { mutableStateOf(false) }
  var starterPlayerName by remember { mutableStateOf("") }
  var lastEjectedPlayer by remember { mutableStateOf<ImpostorPlayerState?>(null) }
  var innocentsWon by remember { mutableStateOf(true) }

  fun persistRoster() {
    PlayerRosterManager.savePartyPlayers(context, playerNames.toList())
  }

  fun startNewImpostorMatch() {
    persistRoster()
    // Pick secret word using anti-repeat history
    val chosenEntry = WordHistoryManager.pickSmartItem(
      context = context,
      categoryKey = "IMPOSTOR_${selectedTheme.id}",
      items = selectedTheme.entries,
      wordSelector = { it.word }
    )
    secretEntry = chosenEntry

    // Assign 1 impostor (or 2 if 7+ players)
    val total = playerNames.size
    val impostorIndex = Random.nextInt(total)

    matchPlayers.clear()
    playerNames.forEachIndexed { idx, name ->
      matchPlayers.add(
        ImpostorPlayerState(
          id = idx + 1,
          name = name,
          isImpostor = (idx == impostorIndex),
          isEliminated = false
        )
      )
    }

    // Choose who starts speaking:
    // Low but non-zero probability (~12%) that the Impostor starts the round!
    val impostorPlayer = matchPlayers.firstOrNull { it.isImpostor }
    val innocentPlayers = matchPlayers.filter { !it.isImpostor }
    val impostorStarts = Random.nextFloat() < 0.12f
    starterPlayerName = if (impostorStarts && impostorPlayer != null) {
      impostorPlayer.name
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
                      text = "Todos ven la palabra secreta menos el Impostor, quien recibe una pista sutil para camuflarse. Digan una palabra o pista por turnos y voten manualmente para expulsar al sospechoso.",
                      fontSize = 12.sp,
                      color = TextSecondary,
                      lineHeight = 17.sp
                    )
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
                        text = "💡 Pista Inteligente para el Impostor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                      )
                      Text(
                        text = "Le da contexto sutil para que pueda engañar mejor",
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
                      Text(text = "${theme.entries.size} palabras con pistas únicas", fontSize = 11.sp, color = TextSecondary)
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
                  text = "Jugadores Guardados (${playerNames.size} — Mín. 3):",
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
              Text(
                text = "¡REPARTIR ROLES SECRETOS!",
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
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                        text = "Categoría: ${secretEntry.categoryLabel}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricGold
                      )
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
                              text = "💡 TU PISTA SUTIL PARA CAMUFLARTE:",
                              fontSize = 11.sp,
                              fontWeight = FontWeight.Black,
                              color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                              text = secretEntry.hint,
                              fontSize = 14.sp,
                              fontWeight = FontWeight.SemiBold,
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
                      Spacer(modifier = Modifier.height(6.dp))
                      Text(
                        text = "Categoría: ${secretEntry.categoryLabel}",
                        fontSize = 12.sp,
                        color = TextSecondary
                      )
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
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "Categoría: ${secretEntry.categoryLabel}",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = NeonCyan
                )
                Spacer(modifier = Modifier.height(14.dp))
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
                  text = "Cada jugador dice una palabra o frase corta sobre el tema en círculo. ¡Luego debatan quién está fingiendo!",
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
                      // Eject this player manually
                      val idx = matchPlayers.indexOfFirst { it.id == p.id }
                      if (idx >= 0) {
                        val updated = matchPlayers[idx].copy(isEliminated = true)
                        matchPlayers[idx] = updated
                        lastEjectedPlayer = updated

                        val remainingImpostors = matchPlayers.count { !it.isEliminated && it.isImpostor }
                        val remainingInnocents = matchPlayers.count { !it.isEliminated && !it.isImpostor }

                        if (updated.isImpostor && remainingImpostors == 0) {
                          HapticHelper.vibrateOnly(context, 90L)
                          SoundHelper.playVictory()
                          innocentsWon = true
                          phase = ImpostorPhase.GAME_OVER
                          onRecordResult(
                            600,
                            "El Impostor: Atraparon a ${updated.name} (Palabra: ${secretEntry.word})",
                            "Modo El Impostor",
                            true
                          )
                        } else if (remainingInnocents <= remainingImpostors) {
                          HapticHelper.vibrateOnly(context, 160L)
                          SoundHelper.playBuzzer()
                          innocentsWon = false
                          phase = ImpostorPhase.GAME_OVER
                          val realImpostor = matchPlayers.firstOrNull { it.isImpostor }?.name ?: "Impostor"
                          onRecordResult(
                            400,
                            "El Impostor: Ganó el Impostor $realImpostor (Palabra: ${secretEntry.word})",
                            "Modo El Impostor",
                            false
                          )
                        } else {
                          HapticHelper.vibrateOnly(context, 140L)
                          SoundHelper.playBuzzer()
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
          val remainingCount = matchPlayers.count { !it.isEliminated }
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
                .border(2.dp, NeonPink, RoundedCornerShape(22.dp))
            ) {
              Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "😱", fontSize = 56.sp)
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
                  text = "¡¡ERA INOCENTE!! ❌",
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Black,
                  color = NeonPink
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "El Impostor sigue oculto entre los $remainingCount jugadores restantes.",
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
                // Pick a new starter from remaining players (12% chance impostor)
                val active = matchPlayers.filter { !it.isEliminated }
                val imp = active.firstOrNull { it.isImpostor }
                val inno = active.filter { !it.isImpostor }
                starterPlayerName = if (Random.nextFloat() < 0.12f && imp != null) {
                  imp.name
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
                innocentsWon = false
                phase = ImpostorPhase.GAME_OVER
              },
              modifier = Modifier.fillMaxWidth().height(52.dp),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = TextSecondary)
            ) {
              Text("Rendirse y Revelar al Impostor", fontWeight = FontWeight.Bold)
            }
          }
        }

        ImpostorPhase.GAME_OVER -> {
          val realImpostor = matchPlayers.firstOrNull { it.isImpostor }
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
                  color = if (innocentsWon) ElectricGreen else NeonPink,
                  shape = RoundedCornerShape(24.dp)
                )
            ) {
              Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = if (innocentsWon) "🎉 ¡GANAN LOS INOCENTES! 🎉" else "🕵️ ¡GANÓ EL IMPOSTOR! 🕵️",
                  fontSize = 22.sp,
                  fontWeight = FontWeight.Black,
                  color = if (innocentsWon) ElectricGreen else NeonPink,
                  textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  text = "El Impostor era:",
                  fontSize = 13.sp,
                  color = TextSecondary
                )
                Text(
                  text = realImpostor?.name ?: "Desconocido",
                  fontSize = 28.sp,
                  fontWeight = FontWeight.Black,
                  color = ElectricGold
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
                    Text(text = "Palabra Secreta (${secretEntry.categoryLabel}):", fontSize = 12.sp, color = NeonCyan)
                    Text(text = secretEntry.word, fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
                    if (giveImpostorHint) {
                      Spacer(modifier = Modifier.height(6.dp))
                      Text(
                        text = "Pista del Impostor: \"${secretEntry.hint}\"",
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
