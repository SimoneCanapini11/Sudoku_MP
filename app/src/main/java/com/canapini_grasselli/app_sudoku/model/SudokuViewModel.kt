package com.canapini_grasselli.app_sudoku.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SudokuViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(SudokuGame())
    val gameState: StateFlow<SudokuGame> = _gameState.asStateFlow()

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
                val difficulty = apiGrid.difficulty

                // Trasforma la griglia dell’API nel modello di dati
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
                    difficulty = difficulty
                )
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

    private fun checkIfCompleted(grid: List<List<SudokuCell>>): Boolean {   //-------da fare con API (solution)
        for (row in 0..8) {
            for (col in 0..8) {
                val cell = grid[row][col]
                if (cell.value == 0 || !cell.isValid) return false
            }
        }
        return true
    }
}
