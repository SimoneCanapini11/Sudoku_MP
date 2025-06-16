package com.canapini_grasselli.app_sudoku.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_results")
data class GameResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,        // Quando è stata giocata la partita
    val completed: Boolean,     // true se la partita è stata terminata (vinta)
    val timeInSeconds: Int      // Tempo impiegato a finire la partita (se completata), 0 se non finita
)
