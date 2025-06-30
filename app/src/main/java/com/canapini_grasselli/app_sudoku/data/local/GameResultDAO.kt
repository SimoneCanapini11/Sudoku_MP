package com.canapini_grasselli.app_sudoku.data.local

import androidx.room.*

@Dao
interface SudokuGameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: SudokuGameEntity)

    @Query("SELECT * FROM sudoku_games WHERE isCompleted = 0 ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastGame(): SudokuGameEntity?

    // Query modificate per le statistiche
    @Query("SELECT COUNT(DISTINCT timestamp) FROM sudoku_games")
    suspend fun getGamesPlayed(): Int

    @Query("SELECT COUNT(DISTINCT timestamp) FROM sudoku_games WHERE isCompleted = 1")
    suspend fun getGamesWon(): Int

    @Query("SELECT COUNT(DISTINCT timestamp) FROM sudoku_games WHERE isCompleted = 1 AND mistakes = 0")
    suspend fun getPerfectWins(): Int

    // Query modificate per i best times
    @Query("SELECT MIN(timerSeconds) FROM sudoku_games WHERE isCompleted = 1 AND difficulty = 'easy'")
    suspend fun getBestTimeEasy(): Int?

    @Query("SELECT MIN(timerSeconds) FROM sudoku_games WHERE isCompleted = 1 AND difficulty = 'medium'")
    suspend fun getBestTimeMedium(): Int?

    @Query("SELECT MIN(timerSeconds) FROM sudoku_games WHERE isCompleted = 1 AND difficulty = 'hard'")
    suspend fun getBestTimeHard(): Int?
}