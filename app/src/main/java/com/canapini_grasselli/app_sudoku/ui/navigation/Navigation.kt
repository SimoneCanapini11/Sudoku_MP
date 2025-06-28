package com.canapini_grasselli.app_sudoku.ui.navigation

import android.os.Process
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.canapini_grasselli.app_sudoku.model.NavigationViewModel
import com.canapini_grasselli.app_sudoku.model.ThemeViewModel
import com.canapini_grasselli.app_sudoku.views.HomeScreen
import com.canapini_grasselli.app_sudoku.views.SudokuScreen

@Composable
fun Navigation(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val navigationViewModel: NavigationViewModel = viewModel()
    val navigationEvent by navigationViewModel.navigationEvent.collectAsState()

    // Gestione degli eventi di navigazione
    navigationEvent?.let { event ->
        when (event) {
            NavigationViewModel.NavigationEvent.NavigateToGame -> {
                navController.navigate("game")
            }
            NavigationViewModel.NavigationEvent.NavigateToLoadGame -> {
                navController.navigate("load_game")
            }
            NavigationViewModel.NavigationEvent.NavigateToStats -> {
                navController.navigate("stats")
            }
            NavigationViewModel.NavigationEvent.NavigateToSettings -> {
                navController.navigate("settings")
            }
            NavigationViewModel.NavigationEvent.Exit -> {
                Process.killProcess(Process.myPid())
            }
        }
        navigationViewModel.onNavigationEventHandled()
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToGame = { navigationViewModel.onNewGameClick() },
                onNavigateToLoadGame = { navigationViewModel.onLoadGameClick() },
                onNavigateToStats = { navigationViewModel.onStatsClick() },
                onExit = { navigationViewModel.onExitClick() },
                themeViewModel = themeViewModel  //Passa il themeViewModel alla HomeScreen
            )
        }
        composable("game") {
            SudokuScreen(navController = navController)
        }
        // -----Altri composable per le altre schermate verranno aggiunti qui
    }
}