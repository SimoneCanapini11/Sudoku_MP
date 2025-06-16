package com.canapini_grasselli.app_sudoku.views

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
import androidx.compose.ui.res.painterResource
import java.util.Locale

@Composable
fun SudokuScreen(viewModel: SudokuViewModel = viewModel()) {
    val gameState by viewModel.gameState.collectAsState()

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
            onMenu = { /* TODO: menu */ }   // ----Aggiungi qui la logica per il menu
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
            iconRes = R.drawable.pause, //---- pause/play
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
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false,
    badgeText: String? = null,
    enabled: Boolean = true
) {
    //val backgroundColor = if (isActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
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
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier
                    .size(48.dp)
                    .padding(top = 8.dp)
            )
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

// Funzione helper
private fun isInSameBox(row1: Int, col1: Int, row2: Int, col2: Int): Boolean {
    if (row2 == -1 || col2 == -1) return false
    return (row1 / 3 == row2 / 3) && (col1 / 3 == col2 / 3)
}