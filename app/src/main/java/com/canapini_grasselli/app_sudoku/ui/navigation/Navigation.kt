package com.canapini_grasselli.app_sudoku.ui.navigation

import android.os.Process
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.canapini_grasselli.app_sudoku.model.NavigationViewModel
import com.canapini_grasselli.app_sudoku.model.SudokuViewModel
import com.canapini_grasselli.app_sudoku.model.ThemeViewModel
import com.canapini_grasselli.app_sudoku.views.HomeScreen
import com.canapini_grasselli.app_sudoku.views.StatisticsScreen
import com.canapini_grasselli.app_sudoku.views.SudokuScreen

@Composable
fun Navigation(themeViewModel: ThemeViewModel, sudokuViewModel: SudokuViewModel) {
    val navController = rememberNavController()
    val navigationViewModel: NavigationViewModel = viewModel()
    val navigationEvent by navigationViewModel.navigationEvent.collectAsState()
    val context = LocalContext.current


    // Gestione degli eventi di navigazione
    navigationEvent?.let { event ->
        when (event) {
            NavigationViewModel.NavigationEvent.NavigateToNewGame -> {
                navController.navigate("game")
            }
            NavigationViewModel.NavigationEvent.NavigateToLoadGame -> {
                navController.navigate("game")
            }
            NavigationViewModel.NavigationEvent.NavigateToStats -> {
                navController.navigate("stats")
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
                onNavigateToGame = {
                    sudokuViewModel.generateNewGame()
                    navigationViewModel.onNewGameClick() },
                onNavigateToLoadGame = {
                    sudokuViewModel.loadLastGame()
                    navigationViewModel.onLoadGameClick()
                },
                onNavigateToStats = { navigationViewModel.onStatsClick() },
                onExit = { navigationViewModel.onExitClick() },
                themeViewModel = themeViewModel,  //Passa il themeViewModel alla HomeScreen
                viewModel = sudokuViewModel
            )
        }
        composable("game") {
            SudokuScreen(
                viewModel = sudokuViewModel,
                navController = navController
                )
        }

        composable("stats") {
            StatisticsScreen(
                onNavigateBack = { navController.navigateUp() },
                context = context
            )
        }
    }
}