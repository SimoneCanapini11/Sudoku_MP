package com.canapini_grasselli.app_sudoku.data.local

import androidx.room.*

@Dao
interface SudokuGameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: SudokuGameEntity)

    @Query("SELECT * FROM sudoku_games WHERE isCompleted = 0 ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastGame(): SudokuGameEntity?

    //---query per best time
}