package com.canapini_grasselli.app_sudoku.model

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
            val newGrid = generateSudokuPuzzle()
            _gameState.value = SudokuGame(
                grid = newGrid,
                selectedRow = -1,
                selectedCol = -1,
                isCompleted = false,
                mistakes = 0
            )
        }
    }

    private fun generateSudokuPuzzle(): List<List<SudokuCell>> {
        // Genera una griglia completa e valida
        val completeGrid = generateCompleteGrid()

        // Rimuove alcuni numeri per creare il puzzle
        val puzzle = completeGrid.map { row ->
            row.map { cell ->
                if (Random.nextFloat() < 0.6) { // 60% di possibilità di rimuovere
                    SudokuCell(value = 0, isFixed = false)
                } else {
                    SudokuCell(value = cell.value, isFixed = true)
                }
            }
        }

        return puzzle
    }

    private fun generateCompleteGrid(): List<List<SudokuCell>> {
        val grid = Array(9) { Array(9) { 0 } }
        fillGrid(grid)

        return grid.map { row ->
            row.map { value ->
                SudokuCell(value = value, isFixed = true)
            }
        }
    }

    private fun fillGrid(grid: Array<Array<Int>>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] == 0) {
                    val numbers = (1..9).shuffled()
                    for (num in numbers) {
                        if (isValidPlacement(grid, row, col, num)) {
                            grid[row][col] = num
                            if (fillGrid(grid)) {
                                return true
                            }
                            grid[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isValidPlacement(grid: Array<Array<Int>>, row: Int, col: Int, num: Int): Boolean {
        // Controllo riga
        for (c in 0..8) {
            if (grid[row][c] == num) return false
        }

        // Controllo colonna
        for (r in 0..8) {
            if (grid[r][col] == num) return false
        }

        // Controllo box 3x3
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if (grid[r][c] == num) return false
            }
        }

        return true
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
}
