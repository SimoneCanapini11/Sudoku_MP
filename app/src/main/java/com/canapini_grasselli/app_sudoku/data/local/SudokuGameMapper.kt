package com.canapini_grasselli.app_sudoku.data.local

import com.canapini_grasselli.app_sudoku.model.SudokuAPIResponse
import com.canapini_grasselli.app_sudoku.model.SudokuCell
import com.canapini_grasselli.app_sudoku.model.SudokuGame
import com.canapini_grasselli.app_sudoku.views.fromDifficultyResourceToString
import com.canapini_grasselli.app_sudoku.views.toDifficultyStringResource

object SudokuGameMapper {
    private val converters = Converters()

    fun fromDomain(game: SudokuGame): SudokuGameEntity =
        SudokuGameEntity(
            id = game.id,
            gridJson = converters.fromGridToJson(game.grid),
            isCompleted = game.isCompleted,
            mistakes = game.mistakes,
            difficulty = game.difficulty.fromDifficultyResourceToString(),
            solutionJson = converters.fromSolution(game.solution),
            timerSeconds = game.timerSeconds,
            hintLeft = game.hintLeft,
            timestamp = game.timestamp
        )

    fun toDomain(entity: SudokuGameEntity): SudokuGame =
        SudokuGame(
            id = entity.id,
            grid = converters.fromJsonToGrid(entity.gridJson),
            isCompleted = entity.isCompleted,
            mistakes = entity.mistakes,
            difficulty = entity.difficulty.toDifficultyStringResource(),
            solution = converters.toSolution(entity.solutionJson),
            timerSeconds = entity.timerSeconds,
            hintLeft = entity.hintLeft,
            timestamp = entity.timestamp
        )
}

fun SudokuAPIResponse.Grid.toSudokuGame(): SudokuGame {
    val initialGrid = List(9) { row ->
        List(9) { col ->
            val value = this.value[row][col]
            SudokuCell(
                value = value,
                isFixed = value != 0,
                isValid = true
            )
        }
    }

    return SudokuGame(
        grid = initialGrid,
        solution = this.solution,
        difficulty = this.difficulty.toDifficultyStringResource(),
        selectedRow = -1,
        selectedCol = -1,
        isCompleted = false,
        mistakes = 0,
        hintLeft = 3,
        timerSeconds = 0,
        timestamp = System.currentTimeMillis()
    )
}