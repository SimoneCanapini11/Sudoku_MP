package com.canapini_grasselli.app_sudoku.data.local

import com.canapini_grasselli.app_sudoku.model.SudokuGame

object SudokuGameMapper {
    private val converters = Converters()

    fun fromDomain(game: SudokuGame): SudokuGameEntity =
        SudokuGameEntity(
            id = game.id,
            gridJson = converters.fromGridToJson(game.grid),
            isCompleted = game.isCompleted,
            mistakes = game.mistakes,
            difficulty = game.difficulty,
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
            difficulty = entity.difficulty,
            solution = converters.toSolution(entity.solutionJson),
            timerSeconds = entity.timerSeconds,
            hintLeft = entity.hintLeft,
            timestamp = entity.timestamp
        )
}