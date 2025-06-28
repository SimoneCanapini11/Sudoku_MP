package com.canapini_grasselli.app_sudoku.views

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.canapini_grasselli.app_sudoku.model.AppTheme
import com.canapini_grasselli.app_sudoku.model.StatisticsViewModel
import com.canapini_grasselli.app_sudoku.model.ThemeViewModel
import com.canapini_grasselli.app_sudoku.ui.theme.Blue40
import com.canapini_grasselli.app_sudoku.ui.theme.Green40
import com.canapini_grasselli.app_sudoku.ui.theme.Purple40
import java.util.Locale

//Schermata Home
@Composable
fun HomeScreen(
    onNavigateToGame: () -> Unit,
    onNavigateToLoadGame: () -> Unit,
    onNavigateToStats: () -> Unit,
    onExit: () -> Unit,
    themeViewModel: ThemeViewModel, //Per cambiare font
    viewModel: SudokuViewModel = viewModel()
) {
    var showExitDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val currentTheme by themeViewModel.currentTheme.collectAsState()
    val canLoadGame by viewModel.canLoadGame.collectAsState()

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
                Column (modifier = Modifier.fillMaxWidth()) {

                    Spacer(modifier = Modifier.height(10.dp))

                    //Tema viola
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                themeViewModel.setTheme(AppTheme.PURPLE)
                                showThemeDialog = false
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                        RadioButton(
                            selected = currentTheme == AppTheme.PURPLE,
                            onClick = {
                                themeViewModel.setTheme(AppTheme.PURPLE)
                                showThemeDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.purple_theme),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium)
                    }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Purple40, CircleShape)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                        }
                    }

                    //Tema verde
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                themeViewModel.setTheme(AppTheme.GREEN)
                                showThemeDialog = false
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentTheme == AppTheme.GREEN,
                                onClick = {
                                    themeViewModel.setTheme(AppTheme.GREEN)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(R.string.green_theme),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium)
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Green40, CircleShape)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                        }
                    }
                    //Tema blu
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                themeViewModel.setTheme(AppTheme.BLUE)
                                showThemeDialog = false
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentTheme == AppTheme.BLUE,
                                onClick = {
                                    themeViewModel.setTheme(AppTheme.BLUE)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(R.string.blue_theme),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium)
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Blue40, CircleShape)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                        }
                    }
                }
            },
            confirmButton = { }
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
                        onClick = {
                            onExit()
                            showExitDialog = false
                        }
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        modifier = Modifier.width(120.dp),
                        onClick = { showExitDialog = false }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
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
                onClick = onNavigateToGame ,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.new_sudoku_game), fontSize = 18.sp)
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
fun SudokuScreen(viewModel: SudokuViewModel = viewModel(), navController: NavController) {
    val gameState by viewModel.gameState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        // Quando viene premuto il tasto back, mostra il dialog
        showExitDialog = true
        viewModel.togglePause()
    }

    // Mostra il dialogo di conferma se showExitDialog è true
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = {
                showExitDialog = false
                viewModel.togglePause()  // Riprendi il gioco se l'utente tocca fuori dal dialog
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
                    Spacer(modifier = Modifier.width(16.dp)) // Spazio tra i bottoni
                    Button(
                        modifier = Modifier.width(120.dp), // Stessa larghezza del bottone Conferma
                        onClick = { showExitDialog = false
                                    viewModel.togglePause()
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp), // padding top aumentato
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header con statistiche
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
                if (gameState.isCompleted) { //--------------------------------------------Da cambiare
                    //Utilizzo la box per allineare al centro il testo
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.game_completed_you_win),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else { //Se si termina la partita e dopo si cancella uno dei numeri inseriti la partita continua
                    Text(
                        text = stringResource(R.string.errors, gameState.mistakes),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = stringResource(R.string.timer, gameState.timerSeconds.toTimeString()),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Griglia Sudoku
        SudokuGrid(
            gameState = gameState,
            onCellClick = { row, col -> viewModel.selectCell(row, col) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tastiera numerica
        NumberKeyboard(
            onNumberClick = { number -> viewModel.setNumber(number) },
            onClearClick = { viewModel.clearCell() }
        )

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
                    text = stringResource(R.string.hints_left, gameState.hintLeft),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = stringResource(
                        R.string.difficulty,
                        gameState.difficulty.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        }
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Menu di bottoni
        BottomActionBar(
            onNotes = { viewModel.toggleNotes() },
            notesActive = gameState.isNotesActive,
            onHint = { viewModel.generateHint() },
            hintCount = gameState.hintLeft,
            onPause = { viewModel.togglePause() },
            isPaused = gameState.isPaused,
            onMenu = { showExitDialog = true
                       viewModel.togglePause() //Mette il gioco in pausa
            }
        )
    }
}

@Composable
fun SudokuGrid(
    gameState: SudokuGame,
    onCellClick: (Int, Int) -> Unit
) {
    Card(
        modifier = Modifier.aspectRatio(1f),
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
                            SudokuCell(
                                cell = gameState.grid[row][col],
                                isSelected = gameState.selectedRow == row && gameState.selectedCol == col,
                                isInSameRow = gameState.selectedRow == row,
                                isInSameCol = gameState.selectedCol == col,
                                isInSameBox = isInSameBox(row, col, gameState.selectedRow, gameState.selectedCol),
                                onClick = { onCellClick(row, col) },
                                modifier = Modifier.weight(1f)
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
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
            .clickable { onClick() },
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
    onClearClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                        onClick = { onNumberClick(i) }
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
                        onClick = { onNumberClick(i) }
                    )
                }
                Button(
                    onClick = onClearClick,
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.close_50),
                        contentDescription = "X",
                        modifier = Modifier.size(40.dp)

                    )

                }
            }
        }
    }
}

@Composable
fun NumberButton(
    number: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(50.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp) //padding per centrare il numero
    ) {
        Text(
            text = number.toString(),
            fontSize = 18.sp,
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
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            iconRes = R.drawable.edit_note,
            label = stringResource(R.string.notes),
            onClick = onNotes,
            isActive = notesActive,
            badgeText = if (notesActive) "ON" else "OFF"
        )
        ActionButton(
            iconRes = R.drawable.emoji_objects,
            label = stringResource(R.string.hints),
            onClick = onHint,
            enabled = hintCount > 0
        )
        ActionButton(
            iconRes = R.drawable.pause, //pause/play
            iconContinue = R.drawable.play_arrow,
            label = stringResource(R.string.pause),
            onClick = onPause,
            isActive = isPaused
        )
        ActionButton(
            iconRes = R.drawable.home,
            label = stringResource(R.string.menu),
            onClick = onMenu
        )
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
    enabled: Boolean = true
) {
    val backgroundColor = when {
        !enabled ->  MaterialTheme.colorScheme.secondary // Cambia colore se disabilitato
        isActive -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }
    val textColor = Color.White

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
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
    statisticsViewModel: StatisticsViewModel = viewModel()
) {
    val statistics by statisticsViewModel.statistics.collectAsState()

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
            // Spazio vuoto per centrare il titolo
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Statistiche generali
        StatisticsCard(
            title = stringResource(R.string.general_statistics),
            icon = R.drawable.icon_stats,
            content = {
                // Statistiche partite con dimensioni maggiori
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
                // Statistiche completate normali
                StatisticItem(
                    label = stringResource(R.string.games_completed),
                    value = statistics.gamesCompleted.toString(),
                    icon = R.drawable.icon_check_circle
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
                    color = Color(0xFF4CAF50)
                )
                DifficultyTimeItem(
                    difficulty = stringResource(R.string.medium_difficulty),
                    time = statistics.bestTimeMedium.toTimeString(),
                    color = Color(0xFFFFA000)
                )
                DifficultyTimeItem(
                    difficulty = stringResource(R.string.hard_difficulty),
                    time = statistics.bestTimeHard.toTimeString(),
                    color = Color(0xFFE53935)
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


// Funzione helper
private fun isInSameBox(row1: Int, col1: Int, row2: Int, col2: Int): Boolean {
    if (row2 == -1 || col2 == -1) return false
    return (row1 / 3 == row2 / 3) && (col1 / 3 == col2 / 3)
}