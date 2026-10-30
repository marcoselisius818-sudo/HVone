package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val gameType: String,
  val score: Int,
  val scoreDetail: String,
  val opponentName: String,
  val isWin: Boolean,
  val isTournament: Boolean,
  val tournamentName: String? = null,
  val timestamp: Long = System.currentTimeMillis(),
  val dateString: String
)
