package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.cloud.FirestoreSyncManager
import com.example.data.local.AppDatabase
import com.example.data.local.DailyRecordEntity
import com.example.data.local.MatchHistoryEntity
import com.example.data.local.TournamentEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.BracketMatch
import com.example.data.model.GameType
import com.example.data.model.TournamentParticipant
import com.example.data.repository.GameRepository
import com.example.ui.util.AuthHelper
import com.example.ui.util.SoundHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppScreen {
  HOME,
  SPLIT_HUB,
  GAME_BOMBA,
  GAME_HEADS_UP,
  GAME_GUESS_NUMBER,
  GAME_10S_SOLO,
  GAME_10S_DUEL,
  GAME_TAP_SOLO,
  GAME_TAP_DUEL,
  GAME_COLOR_SOLO,
  GAME_COLOR_DUEL,
  GAME_EMOJI_SOLO,
  TOURNAMENTS,
  TOURNAMENT_BRACKET,
  LEADERBOARD,
  PROFILE,
  EDIT_PROFILE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  val repository = GameRepository(
    userDao = database.userDao(),
    matchDao = database.matchDao(),
    tournamentDao = database.tournamentDao(),
    dailyRecordDao = database.dailyRecordDao()
  )

  private val _screenStack = MutableStateFlow(listOf(AppScreen.HOME))
  private val _currentScreen = MutableStateFlow(AppScreen.HOME)
  val screen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  val userProfile: StateFlow<UserProfileEntity?> = repository.userProfileFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val recentMatches: StateFlow<List<MatchHistoryEntity>> = repository.recentMatchesFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val savedTournaments: StateFlow<List<TournamentEntity>> = repository.tournamentListFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val dailyRecords: StateFlow<List<DailyRecordEntity>> = repository.dailyRecordsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val firestoreSync = FirestoreSyncManager(application)

  private val _signedInAccount = MutableStateFlow<String?>(null)
  val signedInAccount: StateFlow<String?> = _signedInAccount.asStateFlow()

  private val _syncStatus = MutableStateFlow<String?>(null)
  val syncStatus: StateFlow<String?> = _syncStatus.asStateFlow()

  private val _leaderboardPeriod = MutableStateFlow("HOY")
  val leaderboardPeriod: StateFlow<String> = _leaderboardPeriod.asStateFlow()

  private val _activeTournamentName = MutableStateFlow("Torneo Amigos")
  val activeTournamentName: StateFlow<String> = _activeTournamentName.asStateFlow()

  private val _activeTournamentCode = MutableStateFlow("TK-1000")
  val activeTournamentCode: StateFlow<String> = _activeTournamentCode.asStateFlow()

  private val _tournamentBracket = MutableStateFlow<List<BracketMatch>>(emptyList())
  val tournamentBracket: StateFlow<List<BracketMatch>> = _tournamentBracket.asStateFlow()

  private val _tournamentWinner = MutableStateFlow<TournamentParticipant?>(null)
  val tournamentWinner: StateFlow<TournamentParticipant?> = _tournamentWinner.asStateFlow()

  init {
    SoundHelper.init(application)
    _signedInAccount.value = AuthHelper.getSignedInAccountLabel(application)
    viewModelScope.launch {
      val profile = repository.checkAndInitializeProfile()
      if (_signedInAccount.value != null) {
        firestoreSync.syncUserProfileToCloud(profile)
      }
    }
  }

  fun navigateTo(screen: AppScreen) {
    val stack = _screenStack.value.toMutableList()
    if (stack.lastOrNull() != screen) {
      stack.add(screen)
      _screenStack.value = stack
      _currentScreen.value = screen
    }
  }

  fun navigateBack(): Boolean {
    val stack = _screenStack.value.toMutableList()
    return if (stack.size > 1) {
      stack.removeAt(stack.size - 1)
      _screenStack.value = stack
      _currentScreen.value = stack.last()
      true
    } else {
      false
    }
  }

  fun setLeaderboardPeriod(period: String) {
    _leaderboardPeriod.value = period
  }

  fun updateProfile(nickname: String, avatar: String) {
    viewModelScope.launch {
      repository.updateProfile(nickname, avatar)
      autoSaveIfSignedIn()
    }
  }

  fun recordMatch(
    gameType: GameType,
    score: Int,
    scoreDetail: String,
    opponentName: String,
    isWin: Boolean,
    isTournament: Boolean = false,
    tournamentName: String? = null,
    diffMs: Long? = null,
    tapsPerSec: Float? = null
  ) {
    viewModelScope.launch {
      repository.recordMatch(
        gameType = gameType,
        score = score,
        scoreDetail = scoreDetail,
        opponentName = opponentName,
        isWin = isWin,
        isTournament = isTournament,
        tournamentName = tournamentName,
        diffMs = diffMs,
        tapsPerSec = tapsPerSec
      )
      autoSaveIfSignedIn()
    }
  }

  fun createTournament(name: String, playerCount: Int, customNames: List<String>) {
    viewModelScope.launch {
      val profile = userProfile.value ?: repository.checkAndInitializeProfile()
      val code = "TK-" + Random.nextInt(1000, 9999).toString()
      _activeTournamentName.value = name
      _activeTournamentCode.value = code
      _tournamentWinner.value = null

      val participants = mutableListOf<TournamentParticipant>()
      participants.add(TournamentParticipant(1, profile.nickname, profile.avatar, isUser = true))

      val fallbackAvatars = listOf("🔥", "⚡", "👑", "🚀", "💎", "⭐", "🦄", "🎯")
      for (i in 2..playerCount) {
        val customName = customNames.getOrNull(i - 2)
        val pName = if (!customName.isNullOrBlank()) customName else "Jugador $i"
        val pAvatar = fallbackAvatars[(i - 1) % fallbackAvatars.size]
        participants.add(TournamentParticipant(i, pName, pAvatar, isUser = false))
      }

      val matches = mutableListOf<BracketMatch>()
      if (playerCount == 4) {
        matches.add(BracketMatch(0, 0, participants[0], participants[1]))
        matches.add(BracketMatch(0, 1, participants[2], participants[3]))
        matches.add(BracketMatch(1, 2, null, null))
      } else {
        for (m in 0 until 4) {
          matches.add(BracketMatch(0, m, participants[m * 2], participants[m * 2 + 1]))
        }
        matches.add(BracketMatch(1, 4, null, null))
        matches.add(BracketMatch(1, 5, null, null))
        matches.add(BracketMatch(2, 6, null, null))
      }

      _tournamentBracket.value = matches

      val entity = TournamentEntity(
        roomCode = code,
        name = name,
        dateString = repository.getTodayDateString(),
        playerCount = playerCount,
        isCompleted = false
      )
      repository.saveTournament(entity)
      navigateTo(AppScreen.TOURNAMENT_BRACKET)
    }
  }

  fun recordTournamentMatchOutcome(matchIndex: Int, score1: Int, score2: Int) {
    val currentMatches = _tournamentBracket.value.toMutableList()
    val match = currentMatches.getOrNull(matchIndex) ?: return

    val p1 = match.player1 ?: return
    val p2 = match.player2 ?: return
    val winner = if (score1 >= score2) p1 else p2

    val completedMatch = match.copy(
      score1 = score1,
      score2 = score2,
      winner = winner,
      isCompleted = true
    )
    currentMatches[matchIndex] = completedMatch

    if (currentMatches.size == 3) {
      val finalMatch = currentMatches[2]
      if (matchIndex == 0) {
        currentMatches[2] = finalMatch.copy(player1 = winner)
      } else if (matchIndex == 1) {
        currentMatches[2] = finalMatch.copy(player2 = winner)
      } else if (matchIndex == 2) {
        _tournamentWinner.value = winner
        viewModelScope.launch {
          if (winner.isUser) {
            repository.recordTournamentWin(_activeTournamentName.value)
            autoSaveIfSignedIn()
          }
        }
      }
    } else if (currentMatches.size == 7) {
      when (matchIndex) {
        0 -> currentMatches[4] = currentMatches[4].copy(player1 = winner)
        1 -> currentMatches[4] = currentMatches[4].copy(player2 = winner)
        2 -> currentMatches[5] = currentMatches[5].copy(player1 = winner)
        3 -> currentMatches[5] = currentMatches[5].copy(player2 = winner)
        4 -> currentMatches[6] = currentMatches[6].copy(player1 = winner)
        5 -> currentMatches[6] = currentMatches[6].copy(player2 = winner)
        6 -> {
          _tournamentWinner.value = winner
          viewModelScope.launch {
            if (winner.isUser) {
              repository.recordTournamentWin(_activeTournamentName.value)
              autoSaveIfSignedIn()
            }
          }
        }
      }
    }

    _tournamentBracket.value = currentMatches
  }

  fun onGoogleSignInSuccess(accountLabel: String) {
    _signedInAccount.value = accountLabel
    viewModelScope.launch {
      val profile = userProfile.value ?: repository.checkAndInitializeProfile()
      _syncStatus.value = "Guardando tus datos..."
      val result = firestoreSync.syncUserProfileToCloud(profile)
      _syncStatus.value = if (result.isSuccess) {
        "✓ ¡Sesión iniciada! Tus datos y récords se guardan automáticamente."
      } else {
        "✓ Cuenta conectada. Tus datos están respaldados en tu dispositivo."
      }
    }
  }

  fun onGoogleSignInError(message: String) {
    _syncStatus.value = message
  }

  fun onGoogleSignOut() {
    _signedInAccount.value = null
    _syncStatus.value = "Sesión cerrada. Tus datos locales siguen guardados en este dispositivo."
  }

  private suspend fun autoSaveIfSignedIn() {
    if (_signedInAccount.value != null) {
      val profile = repository.checkAndInitializeProfile()
      val result = firestoreSync.syncUserProfileToCloud(profile)
      if (result.isSuccess) {
        _syncStatus.value = "✓ Progreso guardado automáticamente en tu cuenta"
      }
    }
  }
}
