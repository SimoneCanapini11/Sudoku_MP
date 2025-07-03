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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    private lateinit var sudokuViewModel: SudokuViewModel
    private lateinit var lifecycleObserver: LifecycleEventObserver

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

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
            val windowSize = calculateWindowSizeClass(this)

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
                            themeViewModel = themeViewModel,
                            windowSize = windowSize
                        )
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


//gestione chiamata offline api
//gestione errore chiamata api (toast?)
//Errore salvataggio dati a fine partita
//eliminare hints left e difficulty in sudoku screen

