package com.canapini_grasselli.app_sudoku.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [SudokuGameEntity::class],
    version = 1 // Versione per migrazione dati
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sudokuGameDao(): SudokuGameDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sudoku_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}