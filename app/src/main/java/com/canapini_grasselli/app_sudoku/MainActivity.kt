package com.canapini_grasselli.app_sudoku

import android.content.ComponentCallbacks2
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.canapini_grasselli.app_sudoku.di.AppViewModelProvider
import com.canapini_grasselli.app_sudoku.model.SudokuViewModel
import com.canapini_grasselli.app_sudoku.model.ThemeViewModel
import com.canapini_grasselli.app_sudoku.ui.theme.App_SudokuTheme
import com.canapini_grasselli.app_sudoku.ui.navigation.Navigation
import kotlinx.coroutines.runBlocking
import android.content.res.Configuration

class MainActivity : ComponentActivity() {
    private lateinit var sudokuViewModel: SudokuViewModel
    private lateinit var lifecycleObserver: LifecycleEventObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    runBlocking {
                        sudokuViewModel.saveGameOnExit()
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    runBlocking {
                        sudokuViewModel.saveGameOnExit()
                    }
                }
                else -> {}
            }
        }

        // Observer per lifecycle
        lifecycle.addObserver(lifecycleObserver)

        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides this
            ) {
                val themeViewModel: ThemeViewModel = viewModel(factory = AppViewModelProvider.Factory)
                sudokuViewModel = viewModel(factory = AppViewModelProvider.Factory)

                val currentTheme by themeViewModel.currentTheme.collectAsState()

                // Osserva i cambiamenti di stato dell'app
                DisposableEffect(Unit) {
                    val callback = object : ComponentCallbacks2 {
                        override fun onConfigurationChanged(newConfig: Configuration) {}
                        @Deprecated("Deprecated in Java")
                        override fun onLowMemory() {}
                        override fun onTrimMemory(level: Int) {
                            if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
                                runBlocking {
                                    sudokuViewModel.saveGameOnExit()
                                }
                            }
                        }
                    }
                    registerComponentCallbacks(callback)
                    onDispose {
                        unregisterComponentCallbacks(callback)
                    }
                }

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
    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(lifecycleObserver)
        runBlocking {
            sudokuViewModel.saveGameOnExit()
        }
    }
}


//--vedere se funziona migrazione dati su un nuovo dispositivo
//Schermata statistiche (1. best times, 2. dati non salvati per cont game)
//evedenzia numeri uguali a quello inserito
//se un numero viene inserito in modo errato, rimane rosso anche se era nella posizione corretta
// musica nella home
//mantieni stato partita in background (?)

/*
1.      Una interfaccia utente ben fatta!
2.      Un database dei risultati precedenti
·       Partire giocate
·       Partite vinte (terminate)
·       Miglior Tempo
L'interfaccia dovrà inoltre implementare il massimo degli aiuti possibili all'utente tra cui almeno uno:
·       Appunti utente per ognuna delle 81 celle
*/