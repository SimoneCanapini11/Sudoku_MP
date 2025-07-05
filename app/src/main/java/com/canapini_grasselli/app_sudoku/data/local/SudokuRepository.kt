package com.canapini_grasselli.app_sudoku.data.local

import android.util.Log
import com.canapini_grasselli.app_sudoku.data.remote.SudokuApiClient
import com.canapini_grasselli.app_sudoku.model.SudokuGame
import com.canapini_grasselli.app_sudoku.views.fromDifficultyResourceToString
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

//Per Cache
class SudokuRepository {
    private val easyGames = mutableListOf<SudokuGame>()
    private val mediumGames = mutableListOf<SudokuGame>()
    private val hardGames = mutableListOf<SudokuGame>()

    private val easyFetchMutex = Mutex()
    private val mediumFetchMutex = Mutex()
    private val hardFetchMutex = Mutex()

    private val preloadMutex = Mutex()

    private val preloadSize = 2
    private val batchSize = 20 // Numero di griglie per chiamata API
    private val maxPreloadAttempts = 3

    private var isPreloading = false
    private var isFetchingEasy = false
    private var isFetchingMedium = false
    private var isFetchingHard = false

    suspend fun preloadGames() {
        preloadMutex.withLock {
            if (isPreloading) {
                return
            }

            isPreloading = true

            try {
                var attempts = 0
                while (needsMoreGames() && attempts < maxPreloadAttempts) {
                    attempts++

                    fetchAndCacheGames()
                    kotlinx.coroutines.delay(500)
                }
            } catch (e: Exception) {
                Log.e("SudokuRepository", "Error during preload: ${e.message}", e)
            } finally {
                isPreloading = false
            }
        }
    }

    private fun needsMoreGames(): Boolean {
        val needsMore = easyGames.size < preloadSize ||
                mediumGames.size < preloadSize ||
                hardGames.size < preloadSize
        return needsMore
    }

    private suspend fun fetchAndCacheGames() {
        try {
            val query = "{newboard(limit:$batchSize){grids{value,solution,difficulty},results,message}}"
            val response = SudokuApiClient.service.getMultipleSudoku(query)

            var cached = 0
            response.newboard.grids.forEach { apiGrid ->
                val game = apiGrid.toSudokuGame()
                if (cacheGameByDifficulty(game)) {
                    cached++
                }
            }
        } catch (e: Exception) {
            Log.e("SudokuRepository", "Error fetching batch games: ${e.message}", e)
            kotlinx.coroutines.delay(1000)
        }
    }

    private fun cacheGameByDifficulty(game: SudokuGame): Boolean {
        return when (val difficultyString = game.difficulty.fromDifficultyResourceToString()) {
            "easy" -> if (easyGames.size < preloadSize) {
                easyGames.add(game)
                true
            } else {
                false
            }
            "medium" -> if (mediumGames.size < preloadSize) {
                mediumGames.add(game)
                true
            } else {
                false
            }
            "hard" -> if (hardGames.size < preloadSize) {
                hardGames.add(game)
                true
            } else {
                false
            }
            else -> {
                Log.w("SudokuRepository", "Unknown difficulty: $difficultyString")
                false
            }
        }
    }

    // Ottiene una griglia della difficoltà richiesta
    suspend fun getGameWithDifficulty(difficulty: String): SudokuGame {

        // Usa un mutex specifico per ogni difficoltà per evitare chiamate concorrenti
        val mutex = when (difficulty.lowercase()) {
            "easy" -> easyFetchMutex
            "medium" -> mediumFetchMutex
            "hard" -> hardFetchMutex
            else -> throw IllegalArgumentException("Invalid difficulty: $difficulty")
        }

        return mutex.withLock {
            val games = when (difficulty.lowercase()) {
                "easy" -> easyGames
                "medium" -> mediumGames
                "hard" -> hardGames
                else -> throw IllegalArgumentException("Invalid difficulty: $difficulty")
            }

            if (games.isNotEmpty()) {
                val game = games.removeAt(0)

                if (games.size <= 1 && !isPreloading) {
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        preloadGames()
                    }
                }

                return@withLock game
            }

            val newGame = fetchGameWithDifficulty(difficulty)

            if (games.isNotEmpty()) {
                games.add(newGame)
                return@withLock games.removeAt(0)
            }
            return@withLock newGame
        }
    }

    private suspend fun fetchGameWithDifficulty(difficulty: String): SudokuGame {
        // Controlla se è già in corso un fetch per questa difficoltà
        val isFetching = when (difficulty.lowercase()) {
            "easy" -> isFetchingEasy
            "medium" -> isFetchingMedium
            "hard" -> isFetchingHard
            else -> false
        }

        if (isFetching) {
            kotlinx.coroutines.delay(1000)

            val games = when (difficulty.lowercase()) {
                "easy" -> easyGames
                "medium" -> mediumGames
                "hard" -> hardGames
                else -> throw IllegalArgumentException("Invalid difficulty: $difficulty")
            }

            if (games.isNotEmpty()) {
                return games.removeAt(0)
            }
        }

        // Imposta il flag per questa difficoltà
        when (difficulty.lowercase()) {
            "easy" -> isFetchingEasy = true
            "medium" -> isFetchingMedium = true
            "hard" -> isFetchingHard = true
        }

        var attempts = 0
        val maxAttempts = 5

        try {
            while (attempts < maxAttempts) {
                attempts++
                try {
                    val query = "{newboard(limit:$batchSize){grids{value,solution,difficulty},results,message}}"

                    val response = SudokuApiClient.service.getMultipleSudoku(query)

                    val targetGrid = response.newboard.grids.find { apiGrid ->
                        apiGrid.toSudokuGame().difficulty.fromDifficultyResourceToString() == difficulty.lowercase()
                    }

                    if (targetGrid != null) {

                        // Cache gli altri giochi ricevuti
                        response.newboard.grids.filter { it != targetGrid }.forEach { otherGrid ->
                            cacheGameByDifficulty(otherGrid.toSudokuGame())
                        }

                        return targetGrid.toSudokuGame()
                    }

                    // Se non trova la difficoltà richiesta, cache tutto quello che ha ricevuto
                    response.newboard.grids.forEach { apiGrid ->
                        cacheGameByDifficulty(apiGrid.toSudokuGame())
                    }

                } catch (e: Exception) {
                    Log.e("SudokuRepository", "Error in direct fetch attempt $attempts: ${e.message}", e)

                }
            }

            throw Exception("Unable to find a game with difficulty $difficulty after $maxAttempts attempts")

        } finally {
            // Rimuovi il flag alla fine
            when (difficulty.lowercase()) {
                "easy" -> isFetchingEasy = false
                "medium" -> isFetchingMedium = false
                "hard" -> isFetchingHard = false
            }
        }
    }

}