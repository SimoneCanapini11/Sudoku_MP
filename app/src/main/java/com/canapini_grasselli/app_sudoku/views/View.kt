package com.canapini_grasselli.app_sudoku.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.canapini_grasselli.app_sudoku.model.SudokuCell
import com.canapini_grasselli.app_sudoku.model.SudokuGame
import com.canapini_grasselli.app_sudoku.model.SudokuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(viewModel: SudokuViewModel = viewModel()) {
    val gameState by viewModel.gameState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                Text(
                    text = "Errori: ${gameState.mistakes}",
                    style = MaterialTheme.typography.titleMedium
                )
                if (gameState.isCompleted) {
                    Text(
                        text = "🎉 COMPLETATO! 🎉",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
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

        // Pulsante nuovo gioco
        Button(
            onClick = { viewModel.generateNewGame() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nuovo Gioco")
        }
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
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        isInSameRow || isInSameCol || isInSameBox -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else -> Color.Transparent
    }

    val textColor = when {
        !cell.isValid -> Color.Red
        cell.isFixed -> Color.Black
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(backgroundColor)
            .border(0.5.dp, Color.Gray)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (cell.value != 0) {
            Text(
                text = cell.value.toString(),
                fontSize = 20.sp,
                fontWeight = if (cell.isFixed) FontWeight.Bold else FontWeight.Normal,
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
                    Text("X", fontSize = 18.sp)
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
        contentPadding = PaddingValues(0.dp) //numero centrato
    ) {
        Text(
            text = number.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// Funzione helper
private fun isInSameBox(row1: Int, col1: Int, row2: Int, col2: Int): Boolean {
    if (row2 == -1 || col2 == -1) return false
    return (row1 / 3 == row2 / 3) && (col1 / 3 == col2 / 3)
}