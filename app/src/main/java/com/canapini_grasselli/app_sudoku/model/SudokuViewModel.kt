package com.canapini_grasselli.app_sudoku.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canapini_grasselli.app_sudoku.data.local.GameRepository
import com.canapini_grasselli.app_sudoku.data.local.SudokuGameMapper
import com.canapini_grasselli.app_sudoku.data.preferences.ThemePreferences
import com.canapini_grasselli.app_sudoku.data.remote.SudokuApiClient
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
    val gamesCompleted: Int = 0,
    val bestTimeEasy: Int = 0,
    val bestTimeMedium: Int = 0,
    val bestTimeHard: Int = 0
)

class StatisticsViewModel : ViewModel() {
    private val _statistics = MutableStateFlow(Statistics())
    val statistics: StateFlow<Statistics> = _statistics.asStateFlow()

    // Qui va la logica per caricare le statistiche reali
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

class SudokuViewModel (private val repository: GameRepository) : ViewModel() {

    private val _gameState = MutableStateFlow(SudokuGame())
    val gameState: StateFlow<SudokuGame> = _gameState.asStateFlow()
    private var _canLoadGame = MutableStateFlow(false)
    val canLoadGame: StateFlow<Boolean> = _canLoadGame.asStateFlow()

    private var timerJob: Job? = null

    init {
        generateNewGame()
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
            repository.saveSudokuGame(gameEntity)
            _canLoadGame.value = !currentGame.isCompleted
        }
    }

    private fun saveCompletedGame() {
        viewModelScope.launch {
            val currentGame = _gameState.value.copy(isCompleted = true)
            val gameEntity = SudokuGameMapper.fromDomain(currentGame)
            repository.saveSudokuGame(gameEntity)
            _canLoadGame.value = false
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
            val newGrid = currentState.grid.map { rowList ->
                rowList.toMutableList()
            }.toMutableList()

            val newCell = newGrid[row][col].copy(value = number)
            newGrid[row][col] = newCell

            // Valida la mossa
            val isValid = isValidMove(newGrid, row, col, number)
            newGrid[row][col] = newCell.copy(isValid = isValid)

            val newMistakes = if (!isValid && number != 0) {
                currentState.mistakes + 1
            } else {
                currentState.mistakes
            }

            val isCompleted = checkIfCompleted(newGrid)

            _gameState.value = currentState.copy(
                grid = newGrid,
                mistakes = newMistakes,
                isCompleted = isCompleted
            )

            // Se il gioco è completato
            if (isCompleted) {
                stopTimer()
                saveCompletedGame()
                viewModelScope.launch {
                    val completedGame = _gameState.value
                    val gameEntity = SudokuGameMapper.fromDomain(completedGame)
                    repository.saveSudokuGame(gameEntity)
                    _canLoadGame.value = false
                }
            }
        }
    }

    fun clearCell() {
        setNumber(0)
    }

    fun generateNewGame() {
        viewModelScope.launch {
                stopTimer() // Ferma il timer esistente
                val response = SudokuApiClient.service.getSudoku()
                val apiGrid = response.newboard.grids.first()
                val values = apiGrid.value
                val solution = apiGrid.solution
                val difficulty = apiGrid.difficulty

                // Trasforma la griglia dell’API nel modello di dati SudokuCell
                val newGrid = values.map { row ->
                    row.map { value ->
                        SudokuCell(
                            value = if (value == 0) 0 else value,
                            isFixed = value != 0
                        )
                    }
                }

                _gameState.value = SudokuGame(
                    grid = newGrid,
                    selectedRow = -1,
                    selectedCol = -1,
                    isCompleted = false,
                    mistakes = 0,
                    difficulty = difficulty,
                    solution = solution,
                    hintLeft = 3,
                    timerSeconds = 0  // Reset timer
                )
                startTimer()
                checkSavedGame()
        }
    }



    private fun isValidMove(grid: List<List<SudokuCell>>, row: Int, col: Int, num: Int): Boolean {
        if (num == 0) return true

        // Controllo riga
        for (c in 0..8) {
            if (c != col && grid[row][c].value == num) return false
        }

        // Controllo colonna
        for (r in 0..8) {
            if (r != row && grid[r][col].value == num) return false
        }

        // Controllo box 3x3
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if ((r != row || c != col) && grid[r][c].value == num) return false
            }
        }

        return true
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

        // Aggiorna il valore nella cella selezionata
        val tempGrid = currentState.grid.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                if (r == row && c == col) {
                    cell.copy(value = number, isValid = true, isFixed = true, notes = 0)
                } else {
                    cell
                }
            }
        }

        // Trova celle con lo stesso numero nella stessa riga, colonna e box 3x3 (esclude la cella suggerita)
        val conflictingCells = mutableListOf<Pair<Int, Int>>()

        // Riga
        for (c in 0 until 9) {
            if (c != col && tempGrid[row][c].value == number && !tempGrid[row][c].isFixed) {
                conflictingCells.add(Pair(row, c))
            }
        }
        // Colonna
        for (r in 0 until 9) {
            if (r != row && tempGrid[r][col].value == number && !tempGrid[r][col].isFixed) {
                conflictingCells.add(Pair(r, col))
            }
        }
        // Box 3x3
        val startRow = (row / 3) * 3
        val startCol = (col / 3) * 3
        for (r in startRow until startRow + 3) {
            for (c in startCol until startCol + 3) {
                if ((r != row || c != col) &&
                    tempGrid[r][c].value == number &&
                    !tempGrid[r][c].isFixed
                ) {
                    conflictingCells.add(Pair(r, c))
                }
            }
        }

        // Aggiorna le celle in conflitto per diventare rosse
        val newGrid = tempGrid.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                if (conflictingCells.contains(Pair(r, c))) {
                    cell.copy(isValid = false)
                } else {
                    cell
                }
            }
        }

        val newMistakes = currentState.mistakes + conflictingCells.size

        val isCompleted = checkIfCompleted(newGrid)

        _gameState.value = currentState.copy(
            grid = newGrid,
            mistakes = newMistakes,
            isCompleted = isCompleted,
            hintLeft = currentState.hintLeft - 1
        )

        if (isCompleted) {
            stopTimer()
            saveCompletedGame()
            viewModelScope.launch {
                val completedGame = _gameState.value
                val gameEntity = SudokuGameMapper.fromDomain(completedGame)
                repository.saveSudokuGame(gameEntity)
                _canLoadGame.value = false
            }
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

    //Pulisci il timer quando il ViewModel viene distrutto
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
