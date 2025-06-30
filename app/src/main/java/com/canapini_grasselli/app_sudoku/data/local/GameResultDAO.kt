package com.canapini_grasselli.app_sudoku.data.local

import androidx.room.*

@Dao
interface SudokuGameDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGame(game: SudokuGameEntity)

    @Update
    suspend fun updateGame(game: SudokuGameEntity)

    @Query("SELECT * FROM sudoku_games WHERE isCompleted = 0 ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastGame(): SudokuGameEntity?

    // Queries per le statistiche
    @Query("SELECT COUNT(*) FROM sudoku_games")
    suspend fun getGamesPlayed(): Int

    @Query("SELECT COUNT(*) FROM sudoku_games WHERE isCompleted = 1")
    suspend fun getGamesWon(): Int

    @Query("SELECT COUNT(*) FROM sudoku_games WHERE isCompleted = 1 AND mistakes = 0")
    suspend fun getPerfectWins(): Int

    // Best times per difficoltà
    @Query("SELECT MIN(timerSeconds) FROM sudoku_games WHERE isCompleted = 1 AND difficulty = 'Easy'")
    suspend fun getBestTimeEasy(): Int?

    @Query("SELECT MIN(timerSeconds) FROM sudoku_games WHERE isCompleted = 1 AND difficulty = 'Medium'")
    suspend fun getBestTimeMedium(): Int?

    @Query("SELECT MIN(timerSeconds) FROM sudoku_games WHERE isCompleted = 1 AND difficulty = 'Hard'")
    suspend fun getBestTimeHard(): Int?
}