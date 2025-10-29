/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.tictactoe.features.board

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tictactoe.domain.model.GameState
import com.example.tictactoe.domain.model.GameState.InProgress
import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.repository.Grid
import com.example.tictactoe.ui.Dimens.Padding
import com.example.tictactoe.ui.Dimens.Size
import com.example.tictactoe.ui.R
import com.example.tictactoe.ui.atoms.VerticalSpacer
import com.example.tictactoe.ui.theme.Red700
import com.example.tictactoe.ui.theme.Teal700
import com.example.tictactoe.ui.theme.TicTacToeTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    viewModel: BoardViewModel = koinViewModel(),
) {
    BoardView(
        state = viewModel.gameState.collectAsStateWithLifecycle().value,
        snackbarMessage = viewModel.snackbarMessage.collectAsStateWithLifecycle().value,
        callback = viewModel,
        modifier = modifier,
    )
}

@Composable
private fun BoardView(
    state: GameState,
    snackbarMessage: String?,
    callback: BoardCallback,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            val result = snackbarHostState.showSnackbar(message, withDismissAction = true)
            when (result) {
                SnackbarResult.ActionPerformed -> Unit
                SnackbarResult.Dismissed -> callback.dismissSnackbar()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Padding.Screen),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Center,
        ) {
            HeaderView(state)
            VerticalSpacer(Padding.XXLarge)
            BoardContentView(state, callback)
            VerticalSpacer(Padding.XXLarge)
            ResetView(state, callback)
        }
    }
}

@Composable
private fun HeaderView(
    state: GameState,
) {
    val label = when (state) {
        is InProgress -> when (state.currentPlayerSymbol) {
            Symbol.X -> stringResource(R.string.current_player_x)
            Symbol.O -> stringResource(R.string.current_player_o)
        }
        is GameState.Draw -> stringResource(R.string.draw)
        is GameState.XWins -> stringResource(R.string.x_wins)
        is GameState.OWins -> stringResource(R.string.o_wins)
    }
    CellIcon(state.currentPlayerSymbol, stringResource(R.string.current_player_x), stringResource(R.string.current_player_o))
    Text(label, style = typography.titleLarge, color = White)
}

@Composable
private fun BoardContentView(
    state: GameState,
    callback: BoardCallback,
) = Column {
    RowView(state, 0, callback)
    BoardHorizontalDivider()
    RowView(state, 1, callback)
    BoardHorizontalDivider()
    RowView(state, 2, callback)
}

@Composable
private fun RowView(
    state: GameState,
    row: Int,
    callback: BoardCallback,
) = Row {
    val enabled = state is InProgress

    CellView(state.grid[row][0], row, 0, callback, enabled)
    BoardVerticalDivider()
    CellView(state.grid[row][1], row, 1, callback, enabled)
    BoardVerticalDivider()
    CellView(state.grid[row][2], row, 2, callback, enabled)
}

@Composable
private fun CellView(
    symbol: Symbol?,
    row: Int,
    col: Int,
    callback: BoardCallback,
    enabled: Boolean,
) = Box(
    modifier = Modifier
        .size(Size.BoardCell)
        .clickable(enabled) { callback.onCellClicked(row, col) },
    contentAlignment = Alignment.Center,
) {
    symbol?.let { symbol ->
        CellIcon(symbol, "X", "O")
    }
}

@Composable
private fun CellIcon(
    symbol: Symbol,
    xDescription: String,
    oDescription: String,
) {
    val (icon, contentDescription, tint) = when (symbol) {
        Symbol.X -> Triple(R.drawable.ic_x, xDescription, Teal700)
        Symbol.O -> Triple(R.drawable.ic_o, oDescription, Red700)
    }

    Icon(
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        modifier = Modifier.size(Size.CellIcon),
        tint = tint,
    )
}

@Composable
private fun ResetView(
    gameState: GameState,
    callback: BoardCallback,
) {
    val textRes = when (gameState) {
        is InProgress -> R.string.reset_game
        is GameState.Draw, is GameState.XWins, is GameState.OWins -> R.string.new_game
    }

    TextButton(onClick = callback::onResetGameClicked) { Text(stringResource(textRes), style = typography.titleLarge) }
}

@Suppress("kotlin:S3776") // Complexity OK for a preview function
@Composable
private fun PreviewBoard(
    initialGrid: Grid,
) = TicTacToeTheme {
    var state by remember {
        mutableStateOf(InProgress(grid = initialGrid))
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val callback = object : BoardCallback {
        override fun onCellClicked(row: Int, col: Int) {
            if (state.grid[row][col] == null) {
                val newSymbol = if (state.currentPlayerSymbol == Symbol.X) Symbol.X else Symbol.O
                state = state.copy(
                    currentPlayerSymbol = state.currentPlayerSymbol.toggle(),
                    grid = state.grid.mapIndexed { r, rowList ->
                        rowList.mapIndexed { c, cell ->
                            if (r == row && c == col) newSymbol else cell
                        }
                    },
                )
            } else {
                errorMessage = "Cell already taken"
            }
        }

        override fun dismissSnackbar() {
            errorMessage = null
        }

        override fun onResetGameClicked() {
            state = InProgress(grid = List(3) { List(3) { null } })
            errorMessage = null
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        BoardView(
            state = state,
            snackbarMessage = errorMessage,
            modifier = Modifier.padding(innerPadding),
            callback = callback,
        )
    }
}

@Composable
private fun BoardVerticalDivider() = VerticalDivider(thickness = Size.DividerThickness, color = White, modifier = Modifier.height(Size.BoardCell))

@Composable
private fun BoardHorizontalDivider() =
    HorizontalDivider(thickness = Size.DividerThickness, color = White, modifier = Modifier.width(Size.BoardCell * 3 + Size.DividerThickness * 2))

@Preview
@Composable
private fun EmptyBoardPreview() = PreviewBoard(List(3) { List(3) { null } })

@Preview
@Composable
private fun FullBoardPreview() = PreviewBoard(
    initialGrid = listOf(
        listOf(Symbol.X, Symbol.O, Symbol.X),
        listOf(Symbol.O, Symbol.X, Symbol.O),
        listOf(Symbol.X, Symbol.O, Symbol.X),
    ),
)
