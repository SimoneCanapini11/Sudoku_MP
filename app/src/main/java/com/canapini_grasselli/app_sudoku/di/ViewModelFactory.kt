package com.canapini_grasselli.app_sudoku.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.canapini_grasselli.app_sudoku.data.local.AppDatabase
import com.canapini_grasselli.app_sudoku.data.local.GameRepository
import com.canapini_grasselli.app_sudoku.model.SudokuViewModel
import com.canapini_grasselli.app_sudoku.data.preferences.ThemePreferences
import com.canapini_grasselli.app_sudoku.model.ThemeViewModel

//Usata per fornire l'istanza del repository al ViewModel (dependency injection)
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val context = this.requireContext()
            val database = AppDatabase.getDatabase(context)
            val repository = GameRepository(
                dao = database.sudokuGameDao(),
                database = database
            )
            SudokuViewModel(repository)
        }
        // Factory per ThemeViewModel
        initializer {
            val context = this.requireContext()
            val themePreferences = ThemePreferences(context)
            ThemeViewModel(themePreferences)
        }
    }
}

private fun CreationExtras.requireContext(): Context {
    return (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? Context)
        ?: throw IllegalStateException("Application Context not found")
}