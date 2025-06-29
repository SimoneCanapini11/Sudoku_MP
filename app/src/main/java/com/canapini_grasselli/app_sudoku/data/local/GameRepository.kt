package com.canapini_grasselli.app_sudoku.data.local

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(private val dao: SudokuGameDao, private val database: AppDatabase ) {

    suspend fun saveSudokuGame(game: SudokuGameEntity) = dao.insertGame(game)

    suspend fun getLastSudokuGame(): SudokuGameEntity? = dao.getLastGame()

    suspend fun forceWrite() {
        withContext(Dispatchers.IO) {
            try {
                database.runInTransaction { }
            } catch (e: Exception) {
                Log.e("GameRepository", "Error writing to database", e)
            }
        }
    }
}