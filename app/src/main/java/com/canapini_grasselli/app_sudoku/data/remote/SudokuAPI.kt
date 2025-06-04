package com.canapini_grasselli.app_sudoku.data.remote

import com.canapini_grasselli.app_sudoku.model.SudokuAPIResponse
import retrofit2.http.GET

interface SudokuAPI {
    @GET("api/dosuku")
    suspend fun getSudoku(): SudokuAPIResponse
}