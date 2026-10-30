package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
  @PrimaryKey val id: Int = 1,
  val nickname: String = "Jugador 1",
  val avatar: String = "🔥",
  val level: Int = 1,
  val xp: Int = 0,
  val diamonds: Int = 120,
  val totalWins: Int = 0,
  val totalLosses: Int = 0,
  val dailyScore: Int = 0,
  val lastActiveDate: String = "",
  val streakDays: Int = 1,
  val best10sDiffMs: Long = 9999L,
  val bestTapPerSec: Float = 0f,
  val tournamentTrophies: Int = 0
)
