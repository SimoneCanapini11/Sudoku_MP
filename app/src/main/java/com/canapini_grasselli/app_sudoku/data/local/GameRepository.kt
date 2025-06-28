package com.canapini_grasselli.app_sudoku.data.local

class GameRepository(private val dao: SudokuGameDao) {

    suspend fun saveSudokuGame(game: SudokuGameEntity) = dao.insertGame(game)
    suspend fun getLastSudokuGame(): SudokuGameEntity? = dao.getLastGame()

    suspend fun hasUncompletedGame(): Boolean {
        val lastGame = dao.getLastGame()
        return lastGame != null && !lastGame.isCompleted
    }
}