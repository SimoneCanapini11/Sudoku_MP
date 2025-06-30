package com.canapini_grasselli.app_sudoku.data.local

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(private val dao: SudokuGameDao, private val database: AppDatabase ) {

    suspend fun saveSudokuGame(game: SudokuGameEntity) {
        withContext(Dispatchers.IO) {
            try {
                if (!game.isCompleted) {
                    dao.deleteUncompletedGames()
                }
                dao.insertGame(game)
                forceWrite()
            } catch (e: Exception) {
                Log.e("GameRepository", "Error saving game", e)
            }
        }
    }


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

    // Metodi per le statistiche
    suspend fun getGamesPlayed(): Int = dao.getGamesPlayed()

    suspend fun getGamesWon(): Int = dao.getGamesWon()

    suspend fun getPerfectWins(): Int = dao.getPerfectWins()

    suspend fun getBestTimeEasy(): Int = dao.getBestTimeEasy() ?: 0

    suspend fun getBestTimeMedium(): Int = dao.getBestTimeMedium() ?: 0

    suspend fun getBestTimeHard(): Int = dao.getBestTimeHard() ?: 0
}