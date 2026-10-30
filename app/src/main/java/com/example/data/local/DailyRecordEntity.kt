package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_records")
data class DailyRecordEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val dateString: String,
  val totalDailyScore: Int,
  val best10sDiffMs: Long,
  val bestTapScore: Int,
  val gamesPlayed: Int,
  val peakRank: String
)
