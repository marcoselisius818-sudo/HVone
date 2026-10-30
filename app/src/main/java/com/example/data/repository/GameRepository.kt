package com.example.data.repository

import com.example.data.local.DailyRecordDao
import com.example.data.local.DailyRecordEntity
import com.example.data.local.MatchDao
import com.example.data.local.MatchHistoryEntity
import com.example.data.local.TournamentDao
import com.example.data.local.TournamentEntity
import com.example.data.local.UserDao
import com.example.data.local.UserProfileEntity
import com.example.data.model.GameType
import com.example.data.model.LeaderboardEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.random.Random

class GameRepository(
  private val userDao: UserDao,
  private val matchDao: MatchDao,
  private val tournamentDao: TournamentDao,
  private val dailyRecordDao: DailyRecordDao
) {
  val userProfileFlow: Flow<UserProfileEntity?> = userDao.getUserProfileFlow()
  val recentMatchesFlow: Flow<List<MatchHistoryEntity>> = matchDao.getRecentMatches(50)
  val tournamentListFlow: Flow<List<TournamentEntity>> = tournamentDao.getAllTournaments()
  val dailyRecordsFlow: Flow<List<DailyRecordEntity>> = dailyRecordDao.getAllDailyRecords()

  private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

  fun getTodayDateString(): String = dateFormat.format(Date())

  suspend fun checkAndInitializeProfile(): UserProfileEntity {
    val existing = userDao.getUserProfile()
    val today = getTodayDateString()

    if (existing == null) {
      val initial = UserProfileEntity(
        id = 1,
        nickname = "Jugador 1",
        avatar = "🔥",
        level = 1,
        xp = 50,
        diamonds = 150,
        totalWins = 3,
        totalLosses = 1,
        dailyScore = 2450,
        lastActiveDate = today,
        streakDays = 1,
        best10sDiffMs = 120L,
        bestTapPerSec = 8.5f,
        tournamentTrophies = 1
      )
      userDao.insertOrUpdateProfile(initial)
      return initial
    } else {
      // Check for daily reset
      if (existing.lastActiveDate.isNotEmpty() && existing.lastActiveDate != today) {
        // Archive yesterday's score into daily history
        val archive = DailyRecordEntity(
          dateString = existing.lastActiveDate,
          totalDailyScore = existing.dailyScore,
          best10sDiffMs = existing.best10sDiffMs,
          bestTapScore = (existing.bestTapPerSec * 10).toInt(),
          gamesPlayed = existing.totalWins + existing.totalLosses,
          peakRank = if (existing.dailyScore > 5000) "Top #5 Campeón" else "Top #20 Jugador"
        )
        dailyRecordDao.insertDailyRecord(archive)

        // Reset daily score and increment streak
        val updated = existing.copy(
          dailyScore = 0,
          lastActiveDate = today,
          streakDays = existing.streakDays + 1
        )
        userDao.insertOrUpdateProfile(updated)
        return updated
      }
      return existing
    }
  }

  suspend fun updateProfile(
    nickname: String,
    avatar: String
  ) {
    val current = userDao.getUserProfile() ?: checkAndInitializeProfile()
    val updated = current.copy(
      nickname = nickname,
      avatar = avatar
    )
    userDao.insertOrUpdateProfile(updated)
  }

  suspend fun recordMatch(
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
    val today = getTodayDateString()
    val match = MatchHistoryEntity(
      gameType = gameType.name,
      score = score,
      scoreDetail = scoreDetail,
      opponentName = opponentName,
      isWin = isWin,
      isTournament = isTournament,
      tournamentName = tournamentName,
      timestamp = System.currentTimeMillis(),
      dateString = today
    )
    matchDao.insertMatch(match)

    // Update profile stats
    val profile = userDao.getUserProfile() ?: checkAndInitializeProfile()
    val xpEarned = if (isWin) 80 else 30
    val diamondsEarned = if (isWin) 25 else 10
    val newXp = profile.xp + xpEarned
    val newLevel = (newXp / 250) + 1

    var bestDiff = profile.best10sDiffMs
    if (diffMs != null && diffMs < bestDiff) {
      bestDiff = diffMs
    }

    var bestTap = profile.bestTapPerSec
    if (tapsPerSec != null && tapsPerSec > bestTap) {
      bestTap = tapsPerSec
    }

    val updatedProfile = profile.copy(
      xp = newXp,
      level = newLevel,
      diamonds = profile.diamonds + diamondsEarned,
      totalWins = if (isWin) profile.totalWins + 1 else profile.totalWins,
      totalLosses = if (!isWin) profile.totalLosses + 1 else profile.totalLosses,
      dailyScore = profile.dailyScore + score,
      lastActiveDate = today,
      best10sDiffMs = bestDiff,
      bestTapPerSec = bestTap
    )
    userDao.insertOrUpdateProfile(updatedProfile)
  }

  suspend fun recordTournamentWin(tournamentName: String) {
    val profile = userDao.getUserProfile() ?: checkAndInitializeProfile()
    val updated = profile.copy(
      tournamentTrophies = profile.tournamentTrophies + 1,
      xp = profile.xp + 350,
      diamonds = profile.diamonds + 150,
      dailyScore = profile.dailyScore + 2000
    )
    userDao.insertOrUpdateProfile(updated)
  }

  suspend fun saveTournament(tournament: TournamentEntity): Long {
    return tournamentDao.insertOrUpdateTournament(tournament)
  }

  suspend fun findTournamentByCode(code: String): TournamentEntity? {
    return tournamentDao.getTournamentByCode(code.trim().uppercase())
  }

  fun getGlobalLeaderboard(
    userScore: Int,
    userName: String,
    userAvatar: String,
    period: String, // "HOY", "SEMANAL", "HISTORICO"
    matches: List<MatchHistoryEntity> = emptyList()
  ): List<LeaderboardEntry> {
    val today = getTodayDateString()
    val filteredMatches = when (period) {
      "HOY" -> matches.filter { it.dateString == today }
      else -> matches
    }

    val currentAdjustedScore = when (period) {
      "HOY" -> userScore
      "SEMANAL" -> userScore * 3
      else -> userScore * 7
    }

    val userEntry = LeaderboardEntry(
      rank = 1,
      username = userName,
      handle = "",
      avatar = userAvatar,
      score = currentAdjustedScore,
      isCurrentUser = true,
      countryFlag = "🏆",
      badge = if (currentAdjustedScore > 2000) "Leyenda" else "Tú",
      trendDelta = 0
    )

    val opponentAvatars = listOf("⚡", "🔥", "🎯", "⭐", "🚀", "👑", "🦁", "💎", "👾", "🦄")
    val realOpponents = filteredMatches
      .filter {
        it.opponentName.isNotBlank() &&
        it.opponentName != userName &&
        !it.opponentName.startsWith("Solo", ignoreCase = true) &&
        !it.opponentName.equals("Adivina el Número", ignoreCase = true) &&
        !it.opponentName.equals("Desafío 10s", ignoreCase = true) &&
        !it.opponentName.equals("Adivina la Palabra", ignoreCase = true)
      }
      .groupBy { it.opponentName }
      .map { (oppName, oppMatches) ->
        val oppScore = oppMatches.sumOf { m ->
          if (!m.isWin) m.score else (m.score * 0.75).toInt()
        }
        val avatar = opponentAvatars[Math.abs(oppName.hashCode()) % opponentAvatars.size]
        LeaderboardEntry(
          rank = 0,
          username = oppName,
          handle = "",
          avatar = avatar,
          score = oppScore,
          isCurrentUser = false,
          countryFlag = "🎮",
          badge = if (oppScore > 1500) "Pro Local" else "Rival",
          trendDelta = 0
        )
      }

    val all = (listOf(userEntry) + realOpponents).sortedByDescending { it.score }
    return all.mapIndexed { index, entry ->
      entry.copy(rank = index + 1)
    }
  }
}
