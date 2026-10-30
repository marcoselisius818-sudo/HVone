package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DailyRecordEntity
import com.example.data.local.MatchHistoryEntity
import com.example.data.local.TournamentEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.BracketMatch
import com.example.data.model.GameType
import com.example.data.model.LeaderboardEntry
import com.example.data.model.TournamentParticipant
import com.example.data.repository.GameRepository
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

  // Navigation state with back stack
  private val _screenStack = MutableStateFlow(listOf(AppScreen.HOME))
  val currentScreen: StateFlow<AppScreen> = MutableStateFlow(AppScreen.HOME)

  private val _currentScreen = MutableStateFlow(AppScreen.HOME)
  val screen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  // Profile
  val userProfile: StateFlow<UserProfileEntity?> = repository.userProfileFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Matches
  val recentMatches: StateFlow<List<MatchHistoryEntity>> = repository.recentMatchesFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Tournaments
  val savedTournaments: StateFlow<List<TournamentEntity>> = repository.tournamentListFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Daily Records
  val dailyRecords: StateFlow<List<DailyRecordEntity>> = repository.dailyRecordsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // AI & Cloud services
  private val aiCoach = com.example.data.ai.TikTokAiCoach()
  val firestoreSync = com.example.data.cloud.FirestoreSyncManager(application)

  private val _aiCoachMessage = MutableStateFlow<String?>(null)
  val aiCoachMessage: StateFlow<String?> = _aiCoachMessage.asStateFlow()

  private val _isAiLoading = MutableStateFlow(false)
  val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

  private val _syncStatus = MutableStateFlow<String?>(null)
  val syncStatus: StateFlow<String?> = _syncStatus.asStateFlow()

  // Leaderboard period
  private val _leaderboardPeriod = MutableStateFlow("HOY")
  val leaderboardPeriod: StateFlow<String> = _leaderboardPeriod.asStateFlow()

  // Active Tournament
  private val _activeTournamentName = MutableStateFlow("Torneo TikTok")
  val activeTournamentName: StateFlow<String> = _activeTournamentName.asStateFlow()

  private val _activeTournamentCode = MutableStateFlow("TK-1000")
  val activeTournamentCode: StateFlow<String> = _activeTournamentCode.asStateFlow()

  private val _tournamentBracket = MutableStateFlow<List<BracketMatch>>(emptyList())
  val tournamentBracket: StateFlow<List<BracketMatch>> = _tournamentBracket.asStateFlow()

  private val _tournamentWinner = MutableStateFlow<TournamentParticipant?>(null)
  val tournamentWinner: StateFlow<TournamentParticipant?> = _tournamentWinner.asStateFlow()

  private val _activeMatchToPlay = MutableStateFlow<BracketMatch?>(null)
  val activeMatchToPlay: StateFlow<BracketMatch?> = _activeMatchToPlay.asStateFlow()

  init {
    viewModelScope.launch {
      repository.checkAndInitializeProfile()
    }
  }

  fun navigateTo(screen: AppScreen) {
    val stack = _screenStack.value.toMutableList()
    stack.add(screen)
    _screenStack.value = stack
    _currentScreen.value = screen
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
      navigateBack()
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
      // First player is always current user
      participants.add(TournamentParticipant(1, profile.nickname, profile.avatar, isUser = true))

      val fallbackAvatars = listOf("🔥", "⚡", "👑", "🚀", "💎", "⭐", "🦄", "🎯")
      for (i in 2..playerCount) {
        val customName = customNames.getOrNull(i - 1)
        val pName = if (!customName.isNullOrBlank()) customName else "Jugador $i"
        val pAvatar = fallbackAvatars[(i - 1) % fallbackAvatars.size]
        participants.add(TournamentParticipant(i, pName, pAvatar, isUser = false))
      }

      // Generate Bracket (Single elimination)
      // Round 0: Cuartos / Semis depending on count
      val matches = mutableListOf<BracketMatch>()
      if (playerCount == 4) {
        matches.add(BracketMatch(0, 0, participants[0], participants[1]))
        matches.add(BracketMatch(0, 1, participants[2], participants[3]))
        matches.add(BracketMatch(1, 2, null, null)) // Final
      } else {
        // 8 players
        for (m in 0 until 4) {
          matches.add(BracketMatch(0, m, participants[m * 2], participants[m * 2 + 1]))
        }
        matches.add(BracketMatch(1, 4, null, null)) // Semi 1
        matches.add(BracketMatch(1, 5, null, null)) // Semi 2
        matches.add(BracketMatch(2, 6, null, null)) // Final
      }

      _tournamentBracket.value = matches

      // Save initial tournament entity
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

  fun setMatchToPlay(match: BracketMatch) {
    _activeMatchToPlay.value = match
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

    // Propagate to next round
    if (currentMatches.size == 3) {
      // 4-player tournament: match 0 & 1 feed into Final (index 2)
      val finalMatch = currentMatches[2]
      if (matchIndex == 0) {
        currentMatches[2] = finalMatch.copy(player1 = winner)
      } else if (matchIndex == 1) {
        currentMatches[2] = finalMatch.copy(player2 = winner)
      } else if (matchIndex == 2) {
        // Tournament completed!
        _tournamentWinner.value = winner
        viewModelScope.launch {
          if (winner.isUser) {
            repository.recordTournamentWin(_activeTournamentName.value)
          }
        }
      }
    } else if (currentMatches.size == 7) {
      // 8-player tournament
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
            }
          }
        }
      }
    }

    _tournamentBracket.value = currentMatches
  }

  fun requestAiCoachFeedback(gameTitle: String, scoreDetail: String, isWin: Boolean, opponent: String) {
    viewModelScope.launch {
      _isAiLoading.value = true
      _aiCoachMessage.value = null
      val nickname = userProfile.value?.nickname ?: "TikTokGamer"
      val result = aiCoach.getMatchCoachReview(gameTitle, scoreDetail, isWin, nickname, opponent)
      _isAiLoading.value = false
      _aiCoachMessage.value = result.getOrElse { "Coach IA: ¡Sigue practicando duro para volverte viral!" }
    }
  }

  fun requestViralChallenge() {
    viewModelScope.launch {
      _isAiLoading.value = true
      _aiCoachMessage.value = null
      val result = aiCoach.generateCustomViralChallenge()
      _isAiLoading.value = false
      _aiCoachMessage.value = result.getOrElse { "🔥 Reto Viral: ¡Jueguen el Desafío 10s con los ojos vendados!" }
    }
  }

  fun dismissAiCoachMessage() {
    _aiCoachMessage.value = null
  }

  fun syncWithCloud() {
    viewModelScope.launch {
      val profile = userProfile.value ?: return@launch
      _syncStatus.value = "Sincronizando con Firestore..."
      val result = firestoreSync.syncUserProfileToCloud(profile)
      _syncStatus.value = if (result.isSuccess) {
        "✓ ¡Perfil y récord sincronizados con éxito en Firebase!"
      } else {
        "Nota: Inicia sesión con Google para sincronizar tus marcadores en la nube."
      }
    }
  }
}
