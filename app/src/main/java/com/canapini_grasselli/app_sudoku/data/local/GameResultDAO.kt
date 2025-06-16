package com.canapini_grasselli.app_sudoku.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultDAO {

    @Insert
    suspend fun insertResult(result: GameResult)

    @Query("SELECT COUNT(*) FROM game_results")
    fun countGamesPlayed(): Flow<Int>

    @Query("SELECT COUNT(*) FROM game_results WHERE completed = 1")
    fun countGamesWon(): Flow<Int>

    @Query("SELECT MIN(timeInSeconds) FROM game_results WHERE completed = 1 AND timeInSeconds > 0")
    fun getBestTime(): Flow<Int?>

    @Query("SELECT * FROM game_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<GameResult>>
}