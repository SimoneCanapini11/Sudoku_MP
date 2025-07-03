package com.canapini_grasselli.app_sudoku.data.local

import android.util.Log
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.withContext

//Per Database
class GameRepository(private val dao: SudokuGameDao, private val database: AppDatabase ) {

    suspend fun saveSudokuGame(game: SudokuGameEntity) {
        dao.insertGame(game)
    }
    suspend fun updateSudokuGame(game: SudokuGameEntity) {
        dao.updateGame(game)
    }

    suspend fun getLastSudokuGame(): SudokuGameEntity? = dao.getLastGame()

    // Metodi per statistiche
    suspend fun getGamesPlayed(): Int = dao.getGamesPlayed()
    suspend fun getGamesWon(): Int = dao.getGamesWon()
    suspend fun getPerfectWins(): Int = dao.getPerfectWins()
    suspend fun getBestTimeEasy(): Int = dao.getBestTimeEasy() ?: 0
    suspend fun getBestTimeMedium(): Int = dao.getBestTimeMedium() ?: 0
    suspend fun getBestTimeHard(): Int = dao.getBestTimeHard() ?: 0

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
