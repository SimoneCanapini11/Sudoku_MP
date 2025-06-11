package com.canapini_grasselli.app_sudoku.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class SudokuViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(SudokuGame())
    val gameState: StateFlow<SudokuGame> = _gameState.asStateFlow()

    private var timerJob: Job? = null

    init {
        generateNewGame()
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

        if (row == -1 || col == -1) return
        if (currentState.grid[row][col].isFixed) return

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

        if (checkIfCompleted(newGrid)) {
            stopTimer()
        }
    }

    fun clearCell() {
        setNumber(0)
    }

    fun generateNewGame() {
        viewModelScope.launch {
            try {
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
                    solution = solution
                )

                startTimer()

            } catch (e: Exception) {
                // Stampa il tipo di eccezione e il messaggio nel logcat
                Log.e("SudokuViewModel", "Errore generazione sudoku", e)
                // oppure, se vuoi solo il messaggio:
                Log.e("SudokuViewModel", "Errore: ${e.javaClass.simpleName}: ${e.message}")
                // oppure stampa direttamente lo stacktrace:
                e.printStackTrace()
            }
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
        val solution = currentState.solution // Se la soluzione non c'è, esci

        if (row == -1 || col == -1) return
        if (currentState.grid[row][col].isFixed) return

        val correctNumber = solution[row][col]
        setHint(correctNumber) // Usa una diversa logica dell'inserimento per suggerire un numero
    }

    private fun setHint(number: Int) {
        val currentState = _gameState.value
        val row = currentState.selectedRow
        val col = currentState.selectedCol

        if (row == -1 || col == -1) return
        if (currentState.grid[row][col].isFixed) return

        // Aggiorna il valore nella cella selezionata
        val tempGrid = currentState.grid.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                if (r == row && c == col) {
                    cell.copy(value = number, isValid = true)
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
            isCompleted = isCompleted
        )

        if (checkIfCompleted(newGrid)) {
            stopTimer()
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

    //Pulisci il timer quando il ViewModel viene distrutto
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
