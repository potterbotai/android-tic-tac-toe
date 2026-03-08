package com.potterbot.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.potterbot.tictactoe.ui.theme.AndroidTicTacToeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            AndroidTicTacToeTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TicTacToeScreen(
                        darkTheme = darkTheme,
                        onToggleTheme = { darkTheme = !darkTheme }
                    )
                }
            }
        }
    }
}

enum class Cell(val symbol: String) {
    X("X"),
    O("O"),
    Empty("")
}

data class ScoreKeeper(
    val xWins: Int = 0,
    val oWins: Int = 0,
    val draws: Int = 0
)

@Composable
fun TicTacToeScreen(
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val board = remember { mutableStateListOf<Cell>().apply { repeat(9) { add(Cell.Empty) } } }
    var currentPlayer by remember { mutableStateOf(Cell.X) }
    var winner by remember { mutableStateOf<Cell?>(null) }
    var scores by remember { mutableStateOf(ScoreKeeper()) }

    fun resetBoard(nextStarter: Cell = Cell.X) {
        for (index in board.indices) {
            board[index] = Cell.Empty
        }
        currentPlayer = nextStarter
        winner = null
    }

    fun handleMove(index: Int) {
        if (board[index] != Cell.Empty || winner != null) return
        board[index] = currentPlayer
        val result = detectWinner(board)
        if (result != null) {
            winner = result
            scores = if (result == Cell.X) {
                scores.copy(xWins = scores.xWins + 1)
            } else {
                scores.copy(oWins = scores.oWins + 1)
            }
        } else if (board.none { it == Cell.Empty }) {
            scores = scores.copy(draws = scores.draws + 1)
            winner = Cell.Empty
        } else {
            currentPlayer = if (currentPlayer == Cell.X) Cell.O else Cell.X
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameHeader(darkTheme = darkTheme, onToggleTheme = onToggleTheme)
        Spacer(modifier = Modifier.height(24.dp))
        Scoreboard(scores = scores)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = when (winner) {
                Cell.X -> "X wins!"
                Cell.O -> "O wins!"
                Cell.Empty -> "It’s a draw"
                else -> "${currentPlayer.symbol} to play"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        BoardGrid(board = board, onCellTapped = ::handleMove)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { resetBoard(if (winner == Cell.O) Cell.O else Cell.X) },
            colors = ButtonDefaults.buttonColors()
        ) {
            Text(text = if (winner == null) "Reset" else "Play again")
        }
    }
}

private val winConditions = listOf(
    listOf(0, 1, 2),
    listOf(3, 4, 5),
    listOf(6, 7, 8),
    listOf(0, 3, 6),
    listOf(1, 4, 7),
    listOf(2, 5, 8),
    listOf(0, 4, 8),
    listOf(2, 4, 6)
)

private fun detectWinner(board: List<Cell>): Cell? {
    winConditions.forEach { line ->
        val (a, b, c) = line
        if (
            board[a] != Cell.Empty &&
            board[a] == board[b] &&
            board[a] == board[c]
        ) {
            return board[a]
        }
    }
    return null
}

@Composable
private fun GameHeader(darkTheme: Boolean, onToggleTheme: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "PotterBot Tic-Tac-Toe",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "A hot-seat Compose demo",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        IconButton(onClick = onToggleTheme) {
            Icon(
                imageVector = if (darkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                contentDescription = "Toggle theme"
            )
        }
    }
}

@Composable
private fun Scoreboard(scores: ScoreKeeper) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ScoreItem(label = "X", value = scores.xWins, color = MaterialTheme.colorScheme.primary)
        ScoreItem(label = "Draw", value = scores.draws, color = MaterialTheme.colorScheme.secondary)
        ScoreItem(label = "O", value = scores.oWins, color = MaterialTheme.colorScheme.tertiary)
    }
}

@Composable
private fun ScoreItem(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
        Text(text = value.toString(), style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun BoardGrid(board: List<Cell>, onCellTapped: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        repeat(3) { rowIndex ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(3) { columnIndex ->
                    val position = rowIndex * 3 + columnIndex
                    BoardCell(
                        symbol = board[position].symbol,
                        onClick = { onCellTapped(position) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BoardCell(symbol: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(enabled = symbol.isBlank(), onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewTicTacToe() {
    AndroidTicTacToeTheme {
        TicTacToeScreen(darkTheme = false, onToggleTheme = {})
    }
}
