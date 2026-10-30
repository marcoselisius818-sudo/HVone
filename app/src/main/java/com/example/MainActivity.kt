package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.BracketMatch
import com.example.data.model.GameType
import com.example.ui.screens.Game10sScreen
import com.example.ui.screens.GameBombaScreen
import com.example.ui.screens.GameColorScreen
import com.example.ui.screens.GameEmojiScreen
import com.example.ui.screens.GameGuessNumberScreen
import com.example.ui.screens.GameHeadsUpScreen
import com.example.ui.screens.GameTapScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplitScreenDuelHub
import com.example.ui.screens.TournamentBracketView
import com.example.ui.screens.TournamentsScreen
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.AuthHelper
import com.example.ui.util.configureAppCheck
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Mandated: Configure App Check before UI or Firebase operations
    configureAppCheck(this, intent)

    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        TikTokGamesApp()
      }
    }
  }
}

@Composable
fun TikTokGamesApp(viewModel: MainViewModel = viewModel()) {
  val currentScreen by viewModel.screen.collectAsStateWithLifecycle()
  val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
  val recentMatches by viewModel.recentMatches.collectAsStateWithLifecycle()
  val savedTournaments by viewModel.savedTournaments.collectAsStateWithLifecycle()
  val dailyRecords by viewModel.dailyRecords.collectAsStateWithLifecycle()
  val leaderboardPeriod by viewModel.leaderboardPeriod.collectAsStateWithLifecycle()
  val activeTournamentName by viewModel.activeTournamentName.collectAsStateWithLifecycle()
  val activeTournamentCode by viewModel.activeTournamentCode.collectAsStateWithLifecycle()
  val tournamentBracket by viewModel.tournamentBracket.collectAsStateWithLifecycle()
  val tournamentWinner by viewModel.tournamentWinner.collectAsStateWithLifecycle()

  val aiCoachMessage by viewModel.aiCoachMessage.collectAsStateWithLifecycle()
  val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
  val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()

  var customP1Name by remember { mutableStateOf("Tú") }
  var customP2Name by remember { mutableStateOf("Rival") }
  var activePlayingBracketMatch by remember { mutableStateOf<BracketMatch?>(null) }

  val showBottomNav = currentScreen in listOf(
    AppScreen.HOME,
    AppScreen.SPLIT_HUB,
    AppScreen.TOURNAMENTS,
    AppScreen.LEADERBOARD,
    AppScreen.PROFILE
  )

  BackHandler(enabled = currentScreen != AppScreen.HOME) {
    viewModel.navigateBack()
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    bottomBar = {
      if (showBottomNav) {
        NavigationBar(
          containerColor = SurfaceDark,
          contentColor = TextPrimary,
          modifier = Modifier.navigationBarsPadding().testTag("bottom_nav_bar")
        ) {
          NavigationBarItem(
            selected = currentScreen == AppScreen.HOME,
            onClick = { viewModel.navigateTo(AppScreen.HOME) },
            icon = {
              Icon(
                if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                contentDescription = "Juegos"
              )
            },
            label = { Text("Juegos", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = NeonPink,
              selectedTextColor = NeonPink,
              indicatorColor = SurfaceDark,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            )
          )

          NavigationBarItem(
            selected = currentScreen == AppScreen.SPLIT_HUB,
            onClick = { viewModel.navigateTo(AppScreen.SPLIT_HUB) },
            icon = {
              Icon(
                if (currentScreen == AppScreen.SPLIT_HUB) Icons.Filled.FlashOn else Icons.Outlined.FlashOn,
                contentDescription = "Duelo 1v1"
              )
            },
            label = { Text("1v1 Local", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = NeonCyan,
              selectedTextColor = NeonCyan,
              indicatorColor = SurfaceDark,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            )
          )

          NavigationBarItem(
            selected = currentScreen == AppScreen.TOURNAMENTS,
            onClick = { viewModel.navigateTo(AppScreen.TOURNAMENTS) },
            icon = {
              Icon(
                if (currentScreen == AppScreen.TOURNAMENTS) Icons.Filled.MilitaryTech else Icons.Outlined.MilitaryTech,
                contentDescription = "Torneos"
              )
            },
            label = { Text("Torneos", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = ElectricGold,
              selectedTextColor = ElectricGold,
              indicatorColor = SurfaceDark,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            )
          )

          NavigationBarItem(
            selected = currentScreen == AppScreen.LEADERBOARD,
            onClick = { viewModel.navigateTo(AppScreen.LEADERBOARD) },
            icon = {
              Icon(
                if (currentScreen == AppScreen.LEADERBOARD) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                contentDescription = "Ránking"
              )
            },
            label = { Text("Ránking", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = NeonPink,
              selectedTextColor = NeonPink,
              indicatorColor = SurfaceDark,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            )
          )

          NavigationBarItem(
            selected = currentScreen == AppScreen.PROFILE,
            onClick = { viewModel.navigateTo(AppScreen.PROFILE) },
            icon = {
              Icon(
                if (currentScreen == AppScreen.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                contentDescription = "Perfil"
              )
            },
            label = { Text("Perfil", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = NeonCyan,
              selectedTextColor = NeonCyan,
              indicatorColor = SurfaceDark,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            )
          )
        }
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(
          top = if (showBottomNav) paddingValues.calculateTopPadding() else 0.dp,
          bottom = if (showBottomNav) paddingValues.calculateBottomPadding() else 0.dp
        )
        .statusBarsPadding()
    ) {
      when (currentScreen) {
        AppScreen.HOME -> {
          HomeScreen(
            profile = userProfile,
            onNavigateTo = { viewModel.navigateTo(it) },
            onStartGame = { gameType, isDuel ->
              val userNickname = userProfile?.nickname ?: "Tú"
              customP1Name = userNickname
              customP2Name = "Rival"
              when (gameType) {
                GameType.BOMBA -> viewModel.navigateTo(AppScreen.GAME_BOMBA)
                GameType.HEADS_UP -> viewModel.navigateTo(AppScreen.GAME_HEADS_UP)
                GameType.GUESS_NUMBER -> viewModel.navigateTo(AppScreen.GAME_GUESS_NUMBER)
                GameType.STOP_10S -> viewModel.navigateTo(if (isDuel) AppScreen.GAME_10S_DUEL else AppScreen.GAME_10S_SOLO)
                GameType.TAP_BATTLE -> viewModel.navigateTo(if (isDuel) AppScreen.GAME_TAP_DUEL else AppScreen.GAME_TAP_SOLO)
              }
            }
          )
        }

        AppScreen.GAME_BOMBA -> {
          GameBombaScreen(
            initialPlayerName = userProfile?.nickname ?: "Jugador 1",
            onBack = { viewModel.navigateBack() },
            onRecordResult = { score, detail, opponent, isWin ->
              viewModel.recordMatch(
                gameType = GameType.BOMBA,
                score = score,
                scoreDetail = detail,
                opponentName = opponent,
                isWin = isWin
              )
            }
          )
        }

        AppScreen.GAME_HEADS_UP -> {
          GameHeadsUpScreen(
            onBack = { viewModel.navigateBack() },
            onRecordResult = { score, detail, opponent, isWin ->
              viewModel.recordMatch(
                gameType = GameType.HEADS_UP,
                score = score,
                scoreDetail = detail,
                opponentName = opponent,
                isWin = isWin
              )
            }
          )
        }

        AppScreen.GAME_GUESS_NUMBER -> {
          GameGuessNumberScreen(
            onBack = { viewModel.navigateBack() },
            onRecordResult = { score, detail, opponent, isWin ->
              viewModel.recordMatch(
                gameType = GameType.GUESS_NUMBER,
                score = score,
                scoreDetail = detail,
                opponentName = opponent,
                isWin = isWin
              )
            }
          )
        }

        AppScreen.SPLIT_HUB -> {
          val defaultP1 = userProfile?.nickname ?: "Tú"
          SplitScreenDuelHub(
            defaultP1Name = defaultP1,
            onLaunchDuel = { duelScreen, p1, p2 ->
              customP1Name = p1
              customP2Name = p2
              viewModel.navigateTo(duelScreen)
            }
          )
        }

        AppScreen.GAME_10S_SOLO -> {
          Game10sScreen(
            isDuel = false,
            onBack = { viewModel.navigateBack() },
            onRecordResult = { score, detail, opponent, isWin, diffMs ->
              viewModel.recordMatch(
                gameType = GameType.STOP_10S,
                score = score,
                scoreDetail = detail,
                opponentName = opponent,
                isWin = isWin,
                diffMs = diffMs
              )
            }
          )
        }

        AppScreen.GAME_10S_DUEL -> {
          Game10sScreen(
            isDuel = true,
            player1Name = customP1Name,
            player2Name = customP2Name,
            onBack = { viewModel.navigateBack() },
            onRecordResult = { score, detail, opponent, isWin, diffMs ->
              viewModel.recordMatch(
                gameType = GameType.STOP_10S,
                score = score,
                scoreDetail = detail,
                opponentName = opponent,
                isWin = isWin,
                diffMs = diffMs
              )
              if (activePlayingBracketMatch != null) {
                val match = activePlayingBracketMatch!!
                val s1 = if (isWin) 1000 else 800
                val s2 = if (!isWin) 1000 else 800
                viewModel.recordTournamentMatchOutcome(match.matchIndex, s1, s2)
                activePlayingBracketMatch = null
              }
            }
          )
        }

        AppScreen.GAME_TAP_SOLO -> {
          GameTapScreen(
            isDuel = false,
            onBack = { viewModel.navigateBack() },
            onRecordResult = { score, detail, opponent, isWin, tps ->
              viewModel.recordMatch(
                gameType = GameType.TAP_BATTLE,
                score = score,
                scoreDetail = detail,
                opponentName = opponent,
                isWin = isWin,
                tapsPerSec = tps
              )
            }
          )
        }

        AppScreen.GAME_TAP_DUEL -> {
          GameTapScreen(
            isDuel = true,
            player1Name = customP1Name,
            player2Name = customP2Name,
            onBack = { viewModel.navigateBack() },
            onRecordResult = { score, detail, opponent, isWin, tps ->
              viewModel.recordMatch(
                gameType = GameType.TAP_BATTLE,
                score = score,
                scoreDetail = detail,
                opponentName = opponent,
                isWin = isWin,
                tapsPerSec = tps
              )
              if (activePlayingBracketMatch != null) {
                val match = activePlayingBracketMatch!!
                val s1 = if (isWin) 100 else 50
                val s2 = if (!isWin) 100 else 50
                viewModel.recordTournamentMatchOutcome(match.matchIndex, s1, s2)
                activePlayingBracketMatch = null
              }
            }
          )
        }

        AppScreen.GAME_COLOR_SOLO -> {
          GameColorScreen(
            isDuel = false,
            onBack = { viewModel.navigateBack() },
            onRecordResult = { score, detail, opponent, isWin ->
              viewModel.recordMatch(
                gameType = GameType.TAP_BATTLE,
                score = score,
                scoreDetail = detail,
                opponentName = opponent,
                isWin = isWin
              )
            }
          )
        }

        AppScreen.GAME_COLOR_DUEL -> {
          GameColorScreen(
            isDuel = true,
            player1Name = customP1Name,
            player2Name = customP2Name,
            onBack = { viewModel.navigateBack() },
            onRecordResult = { score, detail, opponent, isWin ->
              viewModel.recordMatch(
                gameType = GameType.TAP_BATTLE,
                score = score,
                scoreDetail = detail,
                opponentName = opponent,
                isWin = isWin
              )
              if (activePlayingBracketMatch != null) {
                val match = activePlayingBracketMatch!!
                val s1 = if (isWin) 5 else 3
                val s2 = if (!isWin) 5 else 3
                viewModel.recordTournamentMatchOutcome(match.matchIndex, s1, s2)
                activePlayingBracketMatch = null
              }
            }
          )
        }

        AppScreen.GAME_EMOJI_SOLO -> {
          GameEmojiScreen(
            onBack = { viewModel.navigateBack() },
            onRecordResult = { score, detail, opponent, isWin ->
              viewModel.recordMatch(
                gameType = GameType.STOP_10S,
                score = score,
                scoreDetail = detail,
                opponentName = opponent,
                isWin = isWin
              )
            }
          )
        }

        AppScreen.TOURNAMENTS -> {
          TournamentsScreen(
            savedTournaments = savedTournaments,
            onCreateTournament = { name, count, names ->
              viewModel.createTournament(name, count, names)
            },
            onOpenBracket = {
              viewModel.navigateTo(AppScreen.TOURNAMENT_BRACKET)
            }
          )
        }

        AppScreen.TOURNAMENT_BRACKET -> {
          TournamentBracketView(
            tournamentName = activeTournamentName,
            roomCode = activeTournamentCode,
            matches = tournamentBracket,
            winner = tournamentWinner,
            onPlayMatch = { match ->
              activePlayingBracketMatch = match
              customP1Name = match.player1?.name ?: "Jugador 1"
              customP2Name = match.player2?.name ?: "Jugador 2"
              // Pick duel game for tournament matchup
              viewModel.navigateTo(AppScreen.GAME_TAP_DUEL)
            },
            onBack = { viewModel.navigateBack() }
          )
        }

        AppScreen.LEADERBOARD -> {
          val profile = userProfile
          val entries = viewModel.repository.getGlobalLeaderboard(
            userScore = profile?.dailyScore ?: 0,
            userName = profile?.nickname ?: "Jugador",
            userAvatar = profile?.avatar ?: "⚡",
            period = leaderboardPeriod,
            matches = recentMatches
          )
          LeaderboardScreen(
            userProfile = profile,
            entries = entries,
            selectedPeriod = leaderboardPeriod,
            onSelectPeriod = { viewModel.setLeaderboardPeriod(it) }
          )
        }

        AppScreen.PROFILE, AppScreen.EDIT_PROFILE -> {
          val scope = rememberCoroutineScope()
          val context = LocalContext.current
          ProfileScreen(
            profile = userProfile,
            matches = recentMatches,
            dailyRecords = dailyRecords,
            syncStatus = syncStatus,
            onSyncCloud = { viewModel.syncWithCloud() },
            onGoogleSignIn = {
              AuthHelper.onGoogleSignInClicked(
                context = context,
                scope = scope,
                onAuthSuccess = { viewModel.syncWithCloud() },
                onAuthError = { /* handled */ }
              )
            },
            onAskGeminiCoach = { viewModel.requestViralChallenge() },
            onUpdateProfile = { name, avatar ->
              viewModel.updateProfile(name, avatar)
            }
          )
        }
      }

      if (isAiLoading) {
        AlertDialog(
          onDismissRequest = {},
          confirmButton = {},
          containerColor = SurfaceDark,
          title = { Text("Consultando a Gemini 3.8 Flash...", color = TextPrimary, fontWeight = FontWeight.Bold) },
          text = {
            Column(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              CircularProgressIndicator(color = NeonPink)
              Spacer(modifier = Modifier.height(16.dp))
              Text("Generando reto con Inteligencia Artificial...", color = TextSecondary, fontSize = 13.sp)
            }
          }
        )
      } else if (aiCoachMessage != null) {
        AlertDialog(
          onDismissRequest = { viewModel.dismissAiCoachMessage() },
          containerColor = SurfaceDark,
          title = {
            Text("✨ Coach Viral IA", fontWeight = FontWeight.Black, color = ElectricGold, fontSize = 18.sp)
          },
          text = {
            Text(
              text = aiCoachMessage ?: "",
              color = TextPrimary,
              fontSize = 14.sp,
              lineHeight = 20.sp
            )
          },
          confirmButton = {
            Button(
              onClick = { viewModel.dismissAiCoachMessage() },
              colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White)
            ) {
              Text("¡Entendido!")
            }
          }
        )
      }
    }
  }
}
