package com.canapini_grasselli.app_sudoku.data.local

import android.util.Log
import com.canapini_grasselli.app_sudoku.data.remote.SudokuApiClient
import com.canapini_grasselli.app_sudoku.model.SudokuGame
import com.canapini_grasselli.app_sudoku.views.fromDifficultyResourceToString
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


//Per Cache
class SudokuRepository {
    // Cache per ogni livello di difficoltà
    private val easyGames = mutableListOf<SudokuGame>()
    private val mediumGames = mutableListOf<SudokuGame>()
    private val hardGames = mutableListOf<SudokuGame>()

    // Numero di griglie da precaricare per ogni difficoltà
    private val preloadSize = 1

    // Funzione per precaricare le griglie
    suspend fun preloadGames() {
        try {
            coroutineScope {
                repeat(50) {
                    launch {
                        val response = SudokuApiClient.service.getSudoku()
                        val apiGrid = response.newboard.grids.first()
                        val game = apiGrid.toSudokuGame()

                        when (game.difficulty.fromDifficultyResourceToString()) {
                            "easy" -> if (easyGames.size < preloadSize) easyGames.add(game)
                            "medium" -> if (mediumGames.size < preloadSize) mediumGames.add(game)
                            "hard" -> if (hardGames.size < preloadSize) hardGames.add(game)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SudokuRepository", "Error during preload", e)
        }
    }

    // Ottiene una griglia della difficoltà richiesta
    suspend fun getGameWithDifficulty(difficulty: String): SudokuGame {
        val games = when (difficulty.lowercase()) {
            "easy" -> easyGames
            "medium" -> mediumGames
            "hard" -> hardGames
            else -> throw IllegalArgumentException("Invalid difficulty")
        }

        if (games.isNotEmpty()) {
            return games.removeAt(0)
        }

        var attempts = 0
        val maxAttempts = 50

        while (attempts < maxAttempts) {
            attempts++
            try {
                val response = SudokuApiClient.service.getSudoku()
                val apiGrid = response.newboard.grids.first()
                val game = apiGrid.toSudokuGame()

                if (game.difficulty.fromDifficultyResourceToString() == difficulty.lowercase()) {
                    coroutineScope {
                        launch {
                            preloadGames()
                        }
                    }
                    return game
                }
                    when (game.difficulty.fromDifficultyResourceToString()) {
                        "easy" -> if (easyGames.size < preloadSize) easyGames.add(game)
                        "medium" -> if (mediumGames.size < preloadSize) mediumGames.add(game)
                        "hard" -> if (hardGames.size < preloadSize) hardGames.add(game)
                    }
            } catch (e: Exception) {
                Log.e("SudokuRepository", "Error fetching game", e)
            }
        }

        throw Exception("Unable to find a game with difficulty $difficulty after $maxAttempts attempts")
    }
}
