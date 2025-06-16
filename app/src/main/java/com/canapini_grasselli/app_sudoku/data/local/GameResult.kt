package com.canapini_grasselli.app_sudoku.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.canapini_grasselli.app_sudoku.model.SudokuCell
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "sudoku_games")
data class SudokuGameEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gridJson: String,           // Griglia serializzata in JSON
    val isCompleted: Boolean,
    val mistakes: Int,
    val difficulty: String,
    val solutionJson: String,       // Soluzione serializzata in JSON
    val timerSeconds: Int,
    val hintLeft: Int,
    val timestamp: Long = System.currentTimeMillis()
)

class Converters {
    @TypeConverter
    fun fromGridToJson(value: List<List<SudokuCell>>): String =
        Gson().toJson(value)

    @TypeConverter
    fun fromJsonToGrid(value: String): List<List<SudokuCell>> =
        Gson().fromJson(value, object : TypeToken<List<List<SudokuCell>>>() {}.type)

    @TypeConverter
    fun fromSolution(value: List<List<Int>>): String =
        Gson().toJson(value)

    @TypeConverter
    fun toSolution(value: String): List<List<Int>> =
        Gson().fromJson(value, object : TypeToken<List<List<Int>>>() {}.type)
}