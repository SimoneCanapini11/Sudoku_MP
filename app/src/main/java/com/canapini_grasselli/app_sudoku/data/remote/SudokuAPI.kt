package com.canapini_grasselli.app_sudoku.data.remote

import com.canapini_grasselli.app_sudoku.model.SudokuAPIResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SudokuAPI {
    //Ottiene griglie multiple
    @GET("api/dosuku")
    suspend fun getMultipleSudoku(@Query("query") query: String): SudokuAPIResponse
}