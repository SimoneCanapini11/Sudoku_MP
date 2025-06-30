package com.canapini_grasselli.app_sudoku.data.local

import androidx.room.*

@Dao
interface SudokuGameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: SudokuGameEntity)

    @Query("SELECT * FROM sudoku_games WHERE isCompleted = 0 ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastGame(): SudokuGameEntity?

    // Estrai tutte le partite
    @Query("SELECT * FROM sudoku_games")
    suspend fun getAllGames(): List<SudokuGameEntity>

    @Query("SELECT * FROM sudoku_games WHERE isCompleted = 1")
    suspend fun getCompletedGames(): List<SudokuGameEntity>
}