package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  fun getUserProfileFlow(): Flow<UserProfileEntity?>

  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  suspend fun getUserProfile(): UserProfileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

  @Query("UPDATE user_profile SET dailyScore = :newScore, lastActiveDate = :date WHERE id = 1")
  suspend fun updateDailyScore(newScore: Int, date: String)

  @Query("UPDATE user_profile SET dailyScore = 0, lastActiveDate = :newDate WHERE id = 1")
  suspend fun resetDailyScore(newDate: String)
}

@Dao
interface MatchDao {
  @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT :limit")
  fun getRecentMatches(limit: Int = 100): Flow<List<MatchHistoryEntity>>

  @Query("SELECT * FROM match_history WHERE gameType = :gameType ORDER BY timestamp DESC")
  fun getMatchesByGame(gameType: String): Flow<List<MatchHistoryEntity>>

  @Query("SELECT * FROM match_history WHERE isTournament = 1 ORDER BY timestamp DESC")
  fun getTournamentMatches(): Flow<List<MatchHistoryEntity>>

  @Insert
  suspend fun insertMatch(match: MatchHistoryEntity)

  @Query("DELETE FROM match_history")
  suspend fun clearAll()
}

@Dao
interface TournamentDao {
  @Query("SELECT * FROM tournaments ORDER BY createdAt DESC")
  fun getAllTournaments(): Flow<List<TournamentEntity>>

  @Query("SELECT * FROM tournaments WHERE roomCode = :code LIMIT 1")
  suspend fun getTournamentByCode(code: String): TournamentEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateTournament(tournament: TournamentEntity): Long

  @Update
  suspend fun updateTournament(tournament: TournamentEntity)
}

@Dao
interface DailyRecordDao {
  @Query("SELECT * FROM daily_records ORDER BY id DESC")
  fun getAllDailyRecords(): Flow<List<DailyRecordEntity>>

  @Query("SELECT * FROM daily_records WHERE dateString = :date LIMIT 1")
  suspend fun getDailyRecord(date: String): DailyRecordEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDailyRecord(record: DailyRecordEntity)
}
