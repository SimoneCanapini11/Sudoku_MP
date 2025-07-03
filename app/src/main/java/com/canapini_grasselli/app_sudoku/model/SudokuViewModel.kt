package com.canapini_grasselli.app_sudoku.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canapini_grasselli.app_sudoku.data.local.GameRepository
import com.canapini_grasselli.app_sudoku.data.local.SudokuGameMapper
import com.canapini_grasselli.app_sudoku.data.preferences.ThemePreferences
import com.canapini_grasselli.app_sudoku.data.local.SudokuRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

data class Statistics(
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val perfectWins: Int = 0,
    val bestTimeEasy: Int = 0,
    val bestTimeMedium: Int = 0,
    val bestTimeHard: Int = 0
)

class StatisticsViewModel(private val repository: GameRepository) : ViewModel() {
    private val _statistics = MutableStateFlow(Statistics())
    val statistics: StateFlow<Statistics> = _statistics.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                val gamesPlayed = repository.getGamesPlayed()
                val gamesWon = repository.getGamesWon()
                val perfectWins = repository.getPerfectWins()
                val bestTimeEasy = repository.getBestTimeEasy()
                val bestTimeMedium = repository.getBestTimeMedium()
                val bestTimeHard = repository.getBestTimeHard()

                _statistics.value = Statistics(
                    gamesPlayed = gamesPlayed,
                    gamesWon = gamesWon,
                    perfectWins = perfectWins,
                    bestTimeEasy = bestTimeEasy,
                    bestTimeMedium = bestTimeMedium,
                    bestTimeHard = bestTimeHard
                )
            } catch (e: Exception) {
                Log.e("StatisticsViewModel", "Error loading statistics", e)
            }
        }
    }

    fun refreshStatistics() {
        loadStatistics()
    }
}

class ThemeViewModel (private val themePreferences: ThemePreferences) : ViewModel() {
    private val _currentTheme = MutableStateFlow(themePreferences.getTheme())
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    fun setTheme(theme: AppTheme) {
        _currentTheme.value = theme
        themePreferences.saveTheme(theme)
    }
}

enum class AppTheme {
    PURPLE,
    GREEN,
    BLUE
}

class NavigationViewModel : ViewModel() {
    // Eventi di navigazione
    sealed class NavigationEvent {
        data object NavigateToNewGame : NavigationEvent()
        data object NavigateToLoadGame : NavigationEvent()
        data object NavigateToStats : NavigationEvent()
        data object Exit : NavigationEvent()
    }

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    fun onNewGameClick() {
        _navigationEvent.value = NavigationEvent.NavigateToNewGame
    }

    fun onLoadGameClick() {
        _navigationEvent.value = NavigationEvent.NavigateToLoadGame
    }

    fun onStatsClick() {
        _navigationEvent.value = NavigationEvent.NavigateToStats
    }

    fun onExitClick() {
        _navigationEvent.value = NavigationEvent.Exit
    }

    // Resetta l'evento dopo che è stato gestito
    fun onNavigationEventHandled() {
        _navigationEvent.value = null
    }
}

class SudokuViewModel (private val repository: GameRepository, private val sudokuRepository: SudokuRepository) : ViewModel() {

    private val _gameState = MutableStateFlow(SudokuGame())
    val gameState: StateFlow<SudokuGame> = _gameState.asStateFlow()
    private var _canLoadGame = MutableStateFlow(false)
    val canLoadGame: StateFlow<Boolean> = _canLoadGame.asStateFlow()
    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Preloading
            try {
                sudokuRepository.preloadGames()
                _loadingState.value = LoadingState.Idle
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error("Error preloading games")
                Log.e("SudokuViewModel", "Error preloading games", e)
            }
        }
        checkSavedGame()
    }

    private fun checkSavedGame() {
        viewModelScope.launch {
            val lastGame = repository.getLastSudokuGame()
            val lastGameDomain = lastGame?.let { SudokuGameMapper.toDomain(it) }
            _canLoadGame.value = lastGameDomain != null && !lastGameDomain.isCompleted
        }
    }

    fun saveGame() {
        viewModelScope.launch {
            val currentGame = _gameState.value
            val gameEntity = SudokuGameMapper.fromDomain(currentGame)

            if (currentGame.id != 0L) {
                repository.updateSudokuGame(gameEntity)
            } else {
                repository.saveSudokuGame(gameEntity)
            }

            _canLoadGame.value = !currentGame.isCompleted
        }
    }

    private fun saveCompletedGame() {
        viewModelScope.launch {
            try {
                Log.d("SudokuViewModel", "Saving completed game")
                val currentGame = _gameState.value.copy(
                    isCompleted = true,
                    timestamp = System.currentTimeMillis()
                )
                val gameEntity = SudokuGameMapper.fromDomain(currentGame)

                if (currentGame.id != 0L) {
                    Log.d("SudokuViewModel", "Updating completed game with ID: ${currentGame.id}")
                    repository.updateSudokuGame(gameEntity)
                } else {
                    Log.d("SudokuViewModel", "Saving new completed game")
                    repository.saveSudokuGame(gameEntity)
                }

                _canLoadGame.value = false
                Log.d("SudokuViewModel", "Game saved successfully")
            } catch (e: Exception) {
                Log.e("SudokuViewModel", "Error saving completed game", e)
            }
        }
    }

    fun loadLastGame() {
        viewModelScope.launch {
            stopTimer()
            val lastGame = repository.getLastSudokuGame()
            lastGame?.let {
                val loadedGame = SudokuGameMapper.toDomain(it)
                _gameState.value = loadedGame.copy(
                    selectedRow = -1,
                    selectedCol = -1,
                    isPaused = false,
                    isNotesActive = false
                )
                if (!loadedGame.isCompleted) {
                    startTimer()
                }
            }
            checkSavedGame()
        }
    }

    suspend fun saveGameOnExit() {
        withContext(Dispatchers.IO) {
            val currentGame = _gameState.value
            if (!currentGame.isCompleted && hasGameStarted(currentGame)) {
                try {
                    val gameEntity = SudokuGameMapper.fromDomain(currentGame.copy(
                        isPaused = true,
                        timestamp = System.currentTimeMillis()
                    ))
                    repository.saveSudokuGame(gameEntity)

                    withContext(Dispatchers.IO) {
                        repository.forceWrite()
                    }

                    checkSavedGame()
                } catch (e: Exception) {
                    Log.e("SudokuViewModel", "Error saving game", e)
                }
            }
        }
    }

    private fun hasGameStarted(game: SudokuGame): Boolean {
        return game.grid.any { row ->
            row.any { cell -> !cell.isFixed && cell.value != 0 }
        }
    }

    fun selectCell(row: Int, col: Int) {
        _gameState.value = _gameState.value.copy(
            selectedRow = row,
            selectedCol = col
        )
    }

    fun setNumber(number: Int) {
        val currentState = _gameState.value
        val row = currentState.selectedRow
        val col = currentState.selectedCol
        val notes = currentState.isNotesActive

        if (currentState.isPaused) return
        if (row == -1 || col == -1) return
        if (currentState.grid[row][col].isFixed) return

        if (notes) {
            setNote(row, col, number)
        } else {
            val newGrid = currentState.grid.map { it.toMutableList() }.toMutableList()

            newGrid[row][col] = newGrid[row][col].copy(value = number)

            val validatedGrid = validateAllCells(newGrid)

            val newMistakes = if (number != 0 && hasConflicts(validatedGrid, row, col, number)) {
                currentState.mistakes + 1
            } else {
                currentState.mistakes
            }

            val isCompleted = checkIfCompleted(validatedGrid)

            _gameState.value = currentState.copy(
                grid = validatedGrid,
                mistakes = newMistakes,
                isCompleted = isCompleted
            )

            // Se il gioco è completato
            if (isCompleted) {
                stopTimer()
                saveCompletedGame()
            }
        }
    }

    private fun validateAllCells(grid: List<List<SudokuCell>>): List<List<SudokuCell>> {
        val newGrid = grid.map { it.toMutableList() }.toMutableList()

        for (i in 0..8) {
            for (j in 0..8) {
                if (!newGrid[i][j].isFixed && newGrid[i][j].value != 0) {
                    val isValid = !hasConflicts(newGrid, i, j, newGrid[i][j].value)
                    newGrid[i][j] = newGrid[i][j].copy(isValid = isValid)
                }
            }
        }

        return newGrid
    }

    private fun hasConflicts(grid: List<List<SudokuCell>>, row: Int, col: Int, num: Int): Boolean {
        if (num == 0) return false
        var hasConflict = false

        // Controllo riga
        for (c in 0..8) {
            if (c != col && grid[row][c].value == num) {
                hasConflict = true
                break
            }
        }

        // Controllo colonna
        if (!hasConflict) {
            for (r in 0..8) {
                if (r != row && grid[r][col].value == num) {
                    hasConflict = true
                    break
                }
            }
        }

        // Controllo box 3x3
        if (!hasConflict) {
            val boxRow = (row / 3) * 3
            val boxCol = (col / 3) * 3
            for (r in boxRow until boxRow + 3) {
                for (c in boxCol until boxCol + 3) {
                    if ((r != row || c != col) && grid[r][c].value == num) {
                        hasConflict = true
                        break
                    }
                }
                if (hasConflict) break
            }
        }

        return hasConflict
    }

    fun clearCell() {
        setNumber(0)
    }

    fun generateNewGame(difficulty: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                stopTimer()

                /*// Salva solo se il gioco corrente è completato
                if (_gameState.value.isCompleted) {
                    saveGame()
                }*/

                val game = sudokuRepository.getGameWithDifficulty(difficulty)
                _gameState.value = game
                startTimer()
                checkSavedGame()
                _loadingState.value = LoadingState.Idle

            } catch (e: Exception) {
                Log.e("SudokuViewModel", "Error generating new game", e)
                _loadingState.value = LoadingState.Error("Could not generate game with difficulty $difficulty. Please try again.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun checkIfCompleted(grid: List<List<SudokuCell>>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                val cell = grid[row][col]
                if (cell.value == 0 || !cell.isValid) return false
            }
        }
        return true
    }

    fun generateHint() {

            val currentState = _gameState.value
            val row = currentState.selectedRow
            val col = currentState.selectedCol
            val solution = currentState.solution

            if (row == -1 || col == -1) return
            if (currentState.grid[row][col].isFixed) return

            val correctNumber = solution[row][col]
            setHint(correctNumber) // Usa una diversa logica dell'inserimento per suggerire un numero

    }

    private fun setHint(number: Int) {
        val currentState = _gameState.value
        val row = currentState.selectedRow
        val col = currentState.selectedCol

        if (currentState.isPaused) return
        if (row == -1 || col == -1) return
        if (currentState.grid[row][col].isFixed) return

        val newGrid = currentState.grid.map { it.toMutableList() }.toMutableList()
        newGrid[row][col] = newGrid[row][col].copy(
            value = number,
            isValid = true,
            isFixed = true,
            notes = 0
        )

        val validatedGrid = validateAllCells(newGrid)

        // Conta i nuovi conflitti creati dall'hint
        val newConflicts = validatedGrid.sumOf { gridRow ->
            gridRow.count { cell ->
                !cell.isFixed && !cell.isValid && cell.value == number
            }
        }

        val isCompleted = checkIfCompleted(validatedGrid)

        _gameState.value = currentState.copy(
            grid = validatedGrid,
            mistakes = currentState.mistakes + newConflicts,
            isCompleted = isCompleted,
            hintLeft = currentState.hintLeft - 1
        )

        if (isCompleted) {
            stopTimer()
            saveCompletedGame()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _gameState.value = _gameState.value.copy(
                    timerSeconds = _gameState.value.timerSeconds + 1
                )
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    fun toggleNotes() {
        val currentState = _gameState.value
        val isNotesActive = !currentState.isNotesActive

        _gameState.value = currentState.copy(
            isNotesActive = isNotesActive
        )
    }

    fun togglePause() {
        val currentState = _gameState.value
        val isPaused = !currentState.isPaused

        _gameState.value = currentState.copy(
            isPaused = isPaused
        )

        if (isPaused) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun setNote(row: Int, col: Int, number: Int) {
        val currentState = _gameState.value
        val currentCell = currentState.grid[row][col]

        // Non permettere appunti se la cella è fissa
        if (currentCell.isFixed) return

        // Crea una nuova griglia per mantenere l'immutabilità
        val newGrid = currentState.grid.map { it.toMutableList() }.toMutableList()

        // Se la nota è uguale al numero del valore della cella, la rimuoviamo (metti 0)
        // altrimenti, impostiamo la nuova nota
        val newNote = if (currentCell.value == number) 0 else number

        // Aggiorna la cella con la nuova nota
        newGrid[row][col] = currentCell.copy(notes = newNote)

        // Aggiorna lo stato del gioco
        _gameState.value = currentState.copy(grid = newGrid)
    }

    sealed class LoadingState {
        data object Idle : LoadingState()
        data object Preloading : LoadingState()
        data class Error(val message: String) : LoadingState()
    }

    //Pulisci il timer quando il ViewModel viene distrutto
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
