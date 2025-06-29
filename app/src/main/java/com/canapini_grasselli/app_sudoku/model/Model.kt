package com.canapini_grasselli.app_sudoku.model


data class SudokuCell(
    val value: Int = 0,
    val isFixed: Boolean = false,
    val isValid: Boolean = true,
    val notes: Int = 0
)

data class SudokuGame(
    val grid: List<List<SudokuCell>> = List(9) { List(9) { SudokuCell() } },
    val selectedRow: Int = -1,
    val selectedCol: Int = -1,
    val isCompleted: Boolean = false,
    val mistakes: Int = 0,
    val difficulty: String = "",
    val solution: List<List<Int>> = emptyList(),
    val timerSeconds: Int = 0,
    val hintLeft: Int = 3,
    val isPaused: Boolean = false,
    val isNotesActive: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class SudokuAPIResponse(
    val newboard: NewBoard
) {
    data class NewBoard(
        val grids: List<Grid>
    )
    data class Grid(
        val value: List<List<Int>>,
        val solution: List<List<Int>>,
        val difficulty: String
    )
}