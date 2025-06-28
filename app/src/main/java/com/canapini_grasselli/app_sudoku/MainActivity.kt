package com.canapini_grasselli.app_sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.canapini_grasselli.app_sudoku.di.AppViewModelProvider
import com.canapini_grasselli.app_sudoku.model.SudokuViewModel
import com.canapini_grasselli.app_sudoku.model.ThemeViewModel
import com.canapini_grasselli.app_sudoku.ui.theme.App_SudokuTheme
import com.canapini_grasselli.app_sudoku.ui.navigation.Navigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides this
            ) {
                val themeViewModel: ThemeViewModel = viewModel()
                val currentTheme by themeViewModel.currentTheme.collectAsState()
                val sudokuViewModel: SudokuViewModel = viewModel(factory = AppViewModelProvider.Factory)

                App_SudokuTheme(appTheme = currentTheme)
                {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Navigation(
                            sudokuViewModel = sudokuViewModel,
                            themeViewModel = themeViewModel)
                    }
                }
            }
        }
    }
}

//continue game: salvare partita anche quando l'app viene chiusa da tasto home + clear, warning gameRepository
//Bug: quando premo pause, poi menu per uscire, la pausa va in play e il tempo scorre
//Gestire evento partita terminata
//mantieni stato partita in background (?)
//Inserire la possibilità di mettere in pausa e quando si riapre l'applicazione l'ultima partita messa in pausa viene ricaricata
/*
1.      Una interfaccia utente ben fatta!
2.      Un database dei risultati precedenti
·       Partire giocate
·       Partite vinte (terminate)
·       Miglior Tempo
L'interfaccia dovrà inoltre implementare il massimo degli aiuti possibili all'utente tra cui almeno uno:
·       Appunti utente per ognuna delle 81 celle
*/