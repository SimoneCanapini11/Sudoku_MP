package com.canapini_grasselli.app_sudoku.views

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.canapini_grasselli.app_sudoku.R
import com.canapini_grasselli.app_sudoku.model.SudokuCell
import com.canapini_grasselli.app_sudoku.model.SudokuGame
import com.canapini_grasselli.app_sudoku.model.SudokuViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.canapini_grasselli.app_sudoku.data.local.AppDatabase
import com.canapini_grasselli.app_sudoku.data.local.GameRepository
import com.canapini_grasselli.app_sudoku.di.StatisticsViewModelFactory
import com.canapini_grasselli.app_sudoku.model.AppTheme
import com.canapini_grasselli.app_sudoku.model.StatisticsViewModel
import com.canapini_grasselli.app_sudoku.model.ThemeViewModel
import com.canapini_grasselli.app_sudoku.ui.theme.Blue40
import com.canapini_grasselli.app_sudoku.ui.theme.Green40
import com.canapini_grasselli.app_sudoku.ui.theme.Purple40

//Schermata Home
@Composable
fun HomeScreen(
    onNavigateToGame: (String) -> Unit,
    onNavigateToLoadGame: () -> Unit,
    onNavigateToStats: () -> Unit,
    onExit: () -> Unit,
    themeViewModel: ThemeViewModel,
    viewModel: SudokuViewModel = viewModel()
) {
    var showExitDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }
    val currentTheme by themeViewModel.currentTheme.collectAsState()
    val canLoadGame by viewModel.canLoadGame.collectAsState()
    val loadingState by viewModel.loadingState.collectAsState()

    BackHandler {
        showExitDialog = true
    }

    // Selezione del font
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    stringResource(R.string.theme_selection),
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            },
            text = {
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp))
                {
                    // Tema viola
                    ThemeOption(
                        theme = AppTheme.PURPLE,
                        currentTheme = currentTheme,
                        themeRes = R.string.purple_theme,
                        color = Purple40,
                        onSelect = {
                            themeViewModel.setTheme(AppTheme.PURPLE)
                            showThemeDialog = false
                        }
                    )

                    // Tema verde
                    ThemeOption(
                        theme = AppTheme.GREEN,
                        currentTheme = currentTheme,
                        themeRes = R.string.green_theme,
                        color = Green40,
                        onSelect = {
                            themeViewModel.setTheme(AppTheme.GREEN)
                            showThemeDialog = false
                        }
                    )

                    // Tema blu
                    ThemeOption(
                        theme = AppTheme.BLUE,
                        currentTheme = currentTheme,
                        themeRes = R.string.blue_theme,
                        color = Blue40,
                        onSelect = {
                            themeViewModel.setTheme(AppTheme.BLUE)
                            showThemeDialog = false
                        }
                    )
                }
            },
            confirmButton = { },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    stringResource(R.string.exit_app),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(stringResource(R.string.confirm_exit_message))
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.width(120.dp),
                        onClick = { showExitDialog = false }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        modifier = Modifier.width(120.dp),
                        onClick = {
                            onExit()
                            showExitDialog = false
                        }
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        )
    }

    // Dialog per la selezione della difficoltà
    if (showDifficultyDialog) {
        DifficultySelectionDialog(
            onDifficultySelected = { difficulty ->
                viewModel.generateNewGame(difficulty)
                onNavigateToGame(difficulty)
                showDifficultyDialog = false

            },
            onDismiss = { showDifficultyDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        // Top bar con bottoni Font e Esci
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Bottone Esci
            IconButton(
                onClick = { showExitDialog = true }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_exit_to_app),
                    contentDescription = "Exit_to_app",
                    modifier = Modifier.size(50.dp)
                )
            }

            // Bottone Font
            IconButton(
                onClick = { showThemeDialog = true }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_color),
                    contentDescription = "Font",
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        // Contenuto centrale con logo e bottoni
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.sudoku_title),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo App",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottoni principali
            Button(
                onClick = { showDifficultyDialog = true },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp),
                enabled = loadingState != SudokuViewModel.LoadingState.Preloading
            ) {
                if (loadingState == SudokuViewModel.LoadingState.Preloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.new_sudoku_game), fontSize = 18.sp)
                }
            }

            Button(
                onClick = onNavigateToLoadGame ,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp),
                enabled = canLoadGame, // Disabilita il bottone se non c'è partita salvata
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            ) {
                Text(stringResource(
                    R.string.cont_game),
                    fontSize = 18.sp,
                    color = if (canLoadGame) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                )
            }

            Button(
                onClick = onNavigateToStats ,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.statistics), fontSize = 18.sp)
            }
        }
    }
}


//Schermata Sudoku
@Composable
fun SudokuScreen(viewModel: SudokuViewModel = viewModel(), navController: NavController, windowSize: WindowWidthSizeClass) {
    val gameState by viewModel.gameState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    var wasPaused by remember { mutableStateOf(false) }

    BackHandler {
        // Quando viene premuto il tasto back, mostra il dialog
        wasPaused = gameState.isPaused  // Salva lo stato corrente
        showExitDialog = true
        if (!gameState.isPaused) {  // Metti in pausa solo se non lo era già
            viewModel.togglePause()
        }
    }

    // Mostra il dialogo di conferma se showExitDialog è true
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = {
                showExitDialog = false
                if (!wasPaused) {
                    viewModel.togglePause()
                }
            },
            title = { Text(stringResource(R.string.return_to_menu),
                            fontWeight = FontWeight.Bold)
                    },
            text = { Text(stringResource(R.string.current_game_saved)) },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.width(120.dp), // Stessa larghezza del bottone Conferma
                        onClick = {
                            showExitDialog = false
                            if (!wasPaused) {
                                viewModel.togglePause()
                            }
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(16.dp)) // Spazio tra i bottoni

                    Button(
                        modifier = Modifier.width(120.dp), // Larghezza fissa per entrambi i bottoni
                        onClick = {
                            viewModel.saveGame() // Salva la partita
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                            showExitDialog = false
                        }
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        )
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()
    ) {
        if (!isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = when (windowSize) {
                            WindowWidthSizeClass.Compact -> 16.dp
                            else -> 24.dp
                        },
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header con statistiche
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (gameState.isCompleted) {
                            Text(
                                text = stringResource(R.string.game_completed_you_win),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.errors, gameState.mistakes),
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = stringResource(
                                        R.string.timer,
                                        gameState.timerSeconds.toTimeString()
                                    ),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Griglia Sudoku
                SudokuGrid(
                    gameState = gameState,
                    onCellClick = { row, col -> viewModel.selectCell(row, col) },
                    windowSize = windowSize
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tastiera numerica (nascosta se il gioco è completato)
                if (!gameState.isCompleted) {
                    NumberKeyboard(
                        onNumberClick = { number -> viewModel.setNumber(number) },
                        onClearClick = { viewModel.clearCell() },
                        windowSize = windowSize
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (gameState.isCompleted)
                                stringResource(
                                    R.string.completion_time,
                                    gameState.timerSeconds.toTimeString()
                                )
                            else
                                stringResource(R.string.hints_left, gameState.hintLeft),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.difficulty) + " ",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = stringResource(id = gameState.difficulty),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                if (gameState.isCompleted) {
                    Spacer(modifier = Modifier.height(26.dp))
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                //Menu di bottoni
                BottomActionBar(
                    onNotes = { viewModel.toggleNotes() },
                    notesActive = gameState.isNotesActive,
                    onHint = { viewModel.generateHint() },
                    hintCount = gameState.hintLeft,
                    onPause = { viewModel.togglePause() },
                    isPaused = gameState.isPaused,
                    onMenu = {
                        wasPaused = gameState.isPaused  // Salva lo stato corrente
                        showExitDialog = true
                        if (!gameState.isPaused) {
                            viewModel.togglePause()
                        }
                    },
                    isCompleted = gameState.isCompleted,
                    onNewGame = {
                        val currentDifficulty = gameState.difficulty.fromDifficultyResourceToString()
                        viewModel.generateNewGame(currentDifficulty) },
                    windowSize = windowSize
                )
            }
        } else {
            // Indicatore di caricamento
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.loading_new_game),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun SudokuGrid(
    gameState: SudokuGame,
    onCellClick: (Int, Int) -> Unit,
    windowSize: WindowWidthSizeClass
) {
    val gridSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.95f
        WindowWidthSizeClass.Medium -> 0.8f
        WindowWidthSizeClass.Expanded -> 0.6f
        else -> 0.95f
    }
    Card(
        modifier = Modifier
            .fillMaxWidth(gridSize)
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            // Griglia delle celle
            Column {
                for (row in 0..8) {
                    Row {
                        for (col in 0..8) {
                            val currentCell = gameState.grid[row][col]

                            val hasSameNumber = if (gameState.selectedRow >= 0 && gameState.selectedCol >= 0) {
                                val selectedCell = gameState.grid[gameState.selectedRow][gameState.selectedCol]
                                selectedCell.value != 0 &&
                                        currentCell.value == selectedCell.value &&
                                        (row != gameState.selectedRow || col != gameState.selectedCol)
                            } else {
                                false
                            }

                            val hasConflict = if (currentCell.value != 0 && !currentCell.isValid) {
                                var found = false

                                // Controllo riga
                                for (c in 0..8) {
                                    if (c != col && gameState.grid[row][c].value == currentCell.value && !gameState.grid[row][c].isValid) {
                                        found = true
                                        break
                                    }
                                }
                                // Controllo colonna
                                if (!found) {
                                    for (r in 0..8) {
                                        if (r != row && gameState.grid[r][col].value == currentCell.value && !gameState.grid[r][col].isValid) {
                                            found = true
                                            break
                                        }
                                    }
                                }
                                // Controllo box 3x3
                                if (!found) {
                                    val boxRow = (row / 3) * 3
                                    val boxCol = (col / 3) * 3
                                    for (r in boxRow until boxRow + 3) {
                                        for (c in boxCol until boxCol + 3) {
                                            if ((r != row || c != col) &&
                                                gameState.grid[r][c].value == currentCell.value &&
                                                !gameState.grid[r][c].isValid) {
                                                found = true
                                                break
                                            }
                                        }
                                        if (found) break
                                    }
                                }
                                found
                            } else {
                                false
                            }

                            SudokuCell(
                                cell = currentCell,
                                isSelected = gameState.selectedRow == row && gameState.selectedCol == col,
                                isInSameRow = gameState.selectedRow == row,
                                isInSameCol = gameState.selectedCol == col,
                                isInSameBox = isInSameBox(row, col, gameState.selectedRow, gameState.selectedCol),
                                hasSameNumber = hasSameNumber,
                                hasConflict = hasConflict,
                                onClick = { onCellClick(row, col) },
                                modifier = Modifier.weight(1f),
                                isGameCompleted = gameState.isCompleted
                            )
                        }
                    }
                }
            }

            // Linee per separare i box 3x3
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellSize = size.width / 9
                val strokeWidth = 6.dp.toPx()

                // Linee verticali
                drawLine(
                    color = Color.Black,
                    start = androidx.compose.ui.geometry.Offset(cellSize * 3, 0f),
                    end = androidx.compose.ui.geometry.Offset(cellSize * 3, size.height),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = Color.Black,
                    start = androidx.compose.ui.geometry.Offset(cellSize * 6, 0f),
                    end = androidx.compose.ui.geometry.Offset(cellSize * 6, size.height),
                    strokeWidth = strokeWidth
                )

                // Linee orizzontali
                drawLine(
                    color = Color.Black,
                    start = androidx.compose.ui.geometry.Offset(0f, cellSize * 3),
                    end = androidx.compose.ui.geometry.Offset(size.width, cellSize * 3),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = Color.Black,
                    start = androidx.compose.ui.geometry.Offset(0f, cellSize * 6),
                    end = androidx.compose.ui.geometry.Offset(size.width, cellSize * 6),
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}

@Composable
fun SudokuCell(
    cell: SudokuCell,
    isSelected: Boolean,
    isInSameRow: Boolean,
    isInSameCol: Boolean,
    isInSameBox: Boolean,
    hasSameNumber: Boolean,
    hasConflict: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isGameCompleted: Boolean = false
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        hasConflict -> Color.Red.copy(alpha = 0.2f)
        hasSameNumber -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        isInSameRow || isInSameCol || isInSameBox -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
        cell.isFixed -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when {
        !cell.isValid -> Color.Red
        cell.isFixed -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 0.5.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
            )
            .then(
                if (!isGameCompleted) Modifier.clickable { onClick() }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (cell.value != 0) {
            Text(
                text = cell.value.toString(),
                fontSize = 24.sp,
                fontWeight = if (cell.isFixed) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }

        // Mostra gli appunti se presenti
        if (cell.notes != 0) {
            Text(
                text = cell.notes.toString(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 2.dp, end = 4.dp)
            )
        }
    }
}

@Composable
fun NumberKeyboard(
    onNumberClick: (Int) -> Unit,
    onClearClick: () -> Unit,
    windowSize: WindowWidthSizeClass
) {
    val buttonSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 45.dp
        WindowWidthSizeClass.Medium -> 60.dp
        WindowWidthSizeClass.Expanded -> 70.dp
        else -> 45.dp
    }

    Card(
        modifier = Modifier.fillMaxWidth(0.95f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Prima riga (1-5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 1..5) {
                    NumberButton(
                        number = i,
                        onClick = { onNumberClick(i) },
                        size = buttonSize
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Seconda riga (6-9 + Clear)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 6..9) {
                    NumberButton(
                        number = i,
                        onClick = { onNumberClick(i) },
                        size = buttonSize
                    )
                }
                Button(
                    onClick = onClearClick,
                    modifier = Modifier.size(buttonSize),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.close_50),
                        contentDescription = "X",
                        modifier = Modifier.size(buttonSize * 0.8f)

                    )

                }
            }
        }
    }
}

@Composable
fun NumberButton(
    number: Int,
    onClick: () -> Unit,
    size: Dp
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(size),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = number.toString(),
            fontSize = (size.value * 0.36f).sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BottomActionBar(
    onNotes: () -> Unit,
    notesActive: Boolean,
    onHint: () -> Unit,
    hintCount: Int,
    onPause: () -> Unit,
    isPaused: Boolean,
    onMenu: () -> Unit,
    isCompleted: Boolean = false,
    onNewGame: () -> Unit = {},
    windowSize: WindowWidthSizeClass
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isCompleted) {
        ActionButton(
            iconRes = R.drawable.edit_note,
            label = stringResource(R.string.notes),
            onClick = onNotes,
            isActive = notesActive,
            badgeText = if (notesActive) "ON" else "OFF",
            windowSize = windowSize
        )
        ActionButton(
            iconRes = R.drawable.emoji_objects,
            label = stringResource(R.string.hints),
            onClick = onHint,
            enabled = hintCount > 0,
            windowSize = windowSize
        )
        ActionButton(
            iconRes = R.drawable.pause, //pause/play
            iconContinue = R.drawable.play_arrow,
            label = stringResource(R.string.pause),
            onClick = onPause,
            isActive = isPaused,
            windowSize = windowSize
        )
        ActionButton(
            iconRes = R.drawable.home,
            label = stringResource(R.string.menu),
            onClick = onMenu,
            windowSize = windowSize
        )
            } else {
            // Bottoni per gioco completato
            ActionButton(
                iconRes = R.drawable.add_circle,
                label = stringResource(R.string.new_sudoku_game),
                onClick = onNewGame,
                windowSize = windowSize,
                isEndGameButton = true
            )
            ActionButton(
                iconRes = R.drawable.home,
                label = stringResource(R.string.menu),
                onClick = onMenu,
                windowSize = windowSize,
                isEndGameButton = true
            )
        }
    }
}

@Composable
fun ActionButton(
    iconRes: Int,
    iconContinue: Int? = null,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false,
    badgeText: String? = null,
    enabled: Boolean = true,
    windowSize: WindowWidthSizeClass,
    isEndGameButton: Boolean = false
) {
    val buttonWidth = when {
        isEndGameButton -> when (windowSize) {
            WindowWidthSizeClass.Compact -> 95.dp
            WindowWidthSizeClass.Medium -> 100.dp
            WindowWidthSizeClass.Expanded -> 120.dp
            else -> 95.dp
        }
        else -> when (windowSize) {
            WindowWidthSizeClass.Compact -> 80.dp
            WindowWidthSizeClass.Medium -> 100.dp
            WindowWidthSizeClass.Expanded -> 120.dp
            else -> 80.dp
        }
    }

    val backgroundColor = when {
        !enabled ->  MaterialTheme.colorScheme.secondary
        isActive -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }

    val textColor = Color.White

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(buttonWidth)
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .then(
                if (enabled) Modifier.clickable { onClick() }
                else Modifier // Non clickable se disabilitato
            )
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .widthIn(min = 64.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(contentAlignment = Alignment.TopEnd) {
            if ((iconContinue != null) && isActive) {
                Image(
                    painter = painterResource(id = iconContinue),
                    contentDescription = label,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(top = 8.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(top = 8.dp)
                )
            }
            if (badgeText != null) {
                Box(
                    modifier = Modifier
                        .offset(x = 8.dp, y = (-6).dp)
                        .background(
                            color = if (isActive) MaterialTheme.colorScheme.primary else Color.DarkGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = textColor
        )
    }
}

@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    context: Context
) {
    val database = AppDatabase.getDatabase(context)
    val repository = GameRepository(database.sudokuGameDao(), database)
    val statisticsViewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(repository)
    )

    val statistics by statisticsViewModel.statistics.collectAsState()

    LaunchedEffect(Unit) {
        statisticsViewModel.refreshStatistics()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        // Header con bottone indietro
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
                    .padding(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_arrow_back),
                    contentDescription = "Back_to_home",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(35.dp)
                )
            }
            Text(
                text = stringResource(R.string.statistics),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 35.sp
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Statistiche generali
        StatisticsCard(
            title = stringResource(R.string.general_statistics),
            icon = R.drawable.icon_stats,
            content = {
                StatisticItem(
                    label = stringResource(R.string.games_played),
                    value = statistics.gamesPlayed.toString(),
                    icon = R.drawable.icon_games_played
                )
                StatisticItem(
                    label = stringResource(R.string.games_won),
                    value = statistics.gamesWon.toString(),
                    icon = R.drawable.icon_trophy
                )
                StatisticItem(
                    label = stringResource(R.string.perfect_wins),
                    value = statistics.perfectWins.toString(),
                    icon = R.drawable.icon_crown
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Migliori tempi
        StatisticsCard(
            title = stringResource(R.string.best_times),
            icon = R.drawable.icon_timer,
            content = {
                DifficultyTimeItem(
                    difficulty = stringResource(R.string.easy_difficulty),
                    time = statistics.bestTimeEasy.toTimeString(),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
                DifficultyTimeItem(
                    difficulty = stringResource(R.string.medium_difficulty),
                    time = statistics.bestTimeMedium.toTimeString(),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
                DifficultyTimeItem(
                    difficulty = stringResource(R.string.hard_difficulty),
                    time = statistics.bestTimeHard.toTimeString(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        )
    }
}

@Composable
private fun StatisticsCard(
    title: String,
    icon: Int,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            content()
        }
    }
}

@Composable
private fun StatisticItem(
    label: String,
    value: String,
    icon: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 19.sp,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Text(
            text = value,
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Composable
private fun ThemeOption(
    theme: AppTheme,
    currentTheme: AppTheme,
    @StringRes themeRes: Int,
    color: Color,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RadioButton(
                selected = currentTheme == theme,
                onClick = onSelect
            )
            Text(
                text = stringResource(themeRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )
        }

        Box(
            modifier = Modifier
                .size(28.dp)
                .background(color, CircleShape)
                .border(0.5.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
        )
    }
}

@Composable
private fun DifficultyTimeItem(
    difficulty: String,
    time: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 2.dp,
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = difficulty,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = time,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun DifficultySelectionDialog(
    onDifficultySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.select_difficulty),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DifficultyOption(
                    difficultyRes = R.string.easy_difficulty,
                    icon = Icons.Default.Star,
                    onClick = { onDifficultySelected("easy") }
                )
                DifficultyOption(
                    difficultyRes = R.string.medium_difficulty,
                    icon = Icons.Default.Star,
                    onClick = { onDifficultySelected("medium") }
                )
                DifficultyOption(
                    difficultyRes = R.string.hard_difficulty,
                    icon = Icons.Default.Star,
                    onClick = { onDifficultySelected("hard") }
                )
            }
        },
        confirmButton = { },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    )
}

@Composable
private fun DifficultyOption(
    @StringRes difficultyRes: Int,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = when (stringResource(id = difficultyRes)) {
                stringResource(R.string.easy_difficulty) -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                stringResource(R.string.medium_difficulty) -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                else -> MaterialTheme.colorScheme.primary
            }
        )
        Text(
            text = stringResource(id = difficultyRes),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp
        )
    }
}


// Funzione helper
private fun isInSameBox(row1: Int, col1: Int, row2: Int, col2: Int): Boolean {
    if (row2 == -1 || col2 == -1) return false
    return (row1 / 3 == row2 / 3) && (col1 / 3 == col2 / 3)
}