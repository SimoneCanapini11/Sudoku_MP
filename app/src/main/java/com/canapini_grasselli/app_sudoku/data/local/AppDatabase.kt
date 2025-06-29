package com.canapini_grasselli.app_sudoku.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [SudokuGameEntity::class],
    version = 2 // Versione per migrazione dati
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sudokuGameDao(): SudokuGameDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS sudoku_games_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        gridJson TEXT NOT NULL,
                        selectedRow INTEGER NOT NULL DEFAULT -1,
                        selectedCol INTEGER NOT NULL DEFAULT -1,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        mistakes INTEGER NOT NULL DEFAULT 0,
                        difficulty TEXT NOT NULL DEFAULT 'medium',
                        solutionJson TEXT NOT NULL,
                        timerSeconds INTEGER NOT NULL DEFAULT 0,
                        hintLeft INTEGER NOT NULL DEFAULT 3,
                        isPaused INTEGER NOT NULL DEFAULT 0,
                        isNotesActive INTEGER NOT NULL DEFAULT 0,
                        timestamp INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}
                    )
                """)

                // Copia i dati dalla vecchia tabella
                db.execSQL("""
                    INSERT INTO sudoku_games_new (
                        id, gridJson, isCompleted, mistakes, difficulty, 
                        solutionJson, timerSeconds, hintLeft, timestamp
                    )
                    SELECT id, gridJson, isCompleted, mistakes, difficulty, 
                           solutionJson, timerSeconds, hintLeft, timestamp
                    FROM sudoku_games
                """)

                // Rimuovi la vecchia tabella
                db.execSQL("DROP TABLE sudoku_games")

                // Rinomina la nuova tabella
                db.execSQL("ALTER TABLE sudoku_games_new RENAME TO sudoku_games")
            }
        }

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sudoku_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration(false)
                    .build().also { INSTANCE = it }
            }
        }
    }
}