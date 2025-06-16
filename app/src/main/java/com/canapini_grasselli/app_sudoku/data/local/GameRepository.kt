package com.canapini_grasselli.app_sudoku.data.local

import kotlinx.coroutines.flow.Flow

class GameRepository(private val dao: GameResultDAO) {

    suspend fun saveResult(result: GameResult) = dao.insertResult(result)

    fun getGamesPlayed(): Flow<Int> = dao.countGamesPlayed()

    fun getGamesWon(): Flow<Int> = dao.countGamesWon()

    fun getBestTime(): Flow<Int?> = dao.getBestTime()

    fun getAllResults(): Flow<List<GameResult>> = dao.getAllResults()
}