package com.canapini_grasselli.app_sudoku.data.local

import kotlinx.coroutines.flow.Flow

class GameRepository(private val dao: SudokuGameDao) {

    suspend fun saveSudokuGame(game: SudokuGameEntity) = dao.insertGame(game)
    suspend fun getLastSudokuGame(): SudokuGameEntity? = dao.getLastGame()
}