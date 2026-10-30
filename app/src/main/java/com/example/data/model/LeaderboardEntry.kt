package com.example.data.model

data class LeaderboardEntry(
  val rank: Int,
  val username: String,
  val handle: String,
  val avatar: String,
  val score: Int,
  val isCurrentUser: Boolean = false,
  val countryFlag: String = "🌎",
  val badge: String = "Viral",
  val trendDelta: Int = 0
)

data class TournamentParticipant(
  val id: Int,
  val name: String,
  val avatar: String,
  val isUser: Boolean = false
)

data class BracketMatch(
  val roundIndex: Int, // 0 = Cuartos, 1 = Semifinales, 2 = Final
  val matchIndex: Int,
  val player1: TournamentParticipant?,
  val player2: TournamentParticipant?,
  val score1: Int = 0,
  val score2: Int = 0,
  val winner: TournamentParticipant? = null,
  val isCompleted: Boolean = false
)
