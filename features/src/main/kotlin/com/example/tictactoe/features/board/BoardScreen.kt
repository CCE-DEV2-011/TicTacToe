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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
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
import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.repository.Grid
import com.example.tictactoe.features.board.BoardViewModel.BoardUiState
import com.example.tictactoe.ui.Dimens.Padding
import com.example.tictactoe.ui.Dimens.Size
import com.example.tictactoe.ui.R
import com.example.tictactoe.ui.VerticalSpacer
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
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        callback = viewModel,
        modifier = modifier,
    )
}

@Composable
private fun BoardView(
    uiState: BoardUiState,
    callback: BoardCallback,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier
        .fillMaxSize()
        .padding(Padding.Screen),
    horizontalAlignment = CenterHorizontally,
    verticalArrangement = Center,
) {
    HeaderView(uiState)
    VerticalSpacer(Padding.XXLarge)
    BoardContentView(uiState, callback)
}

@Composable
private fun HeaderView(
    uiState: BoardUiState,
) {
    val currentPlayerText = when (uiState.currentPlayerSymbol) {
        Symbol.X -> stringResource(R.string.current_player_x)
        Symbol.O -> stringResource(R.string.current_player_o)
    }
    CellIcon(uiState.currentPlayerSymbol, stringResource(R.string.current_player_x), stringResource(R.string.current_player_o))
    Text(currentPlayerText, style = MaterialTheme.typography.titleLarge, color = White)
}

@Composable
private fun BoardContentView(
    uiState: BoardUiState,
    callback: BoardCallback,
) = Column {
    RowView(uiState, 0, callback)
    BoardHorizontalDivider()
    RowView(uiState, 1, callback)
    BoardHorizontalDivider()
    RowView(uiState, 2, callback)
}

@Composable
private fun RowView(
    uiState: BoardUiState,
    row: Int,
    callback: BoardCallback,
) = Row {
    CellView(uiState.grid[row][0], row, 0, callback)
    BoardVerticalDivider()
    CellView(uiState.grid[row][1], row, 1, callback)
    BoardVerticalDivider()
    CellView(uiState.grid[row][2], row, 2, callback)
}

@Composable
private fun CellView(
    symbol: Symbol?,
    row: Int,
    col: Int,
    callback: BoardCallback,
) = Box(
    modifier = Modifier
        .size(Size.BoardCell)
        .clickable { callback.onCellClicked(row, col) },
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

@Suppress("kotlin:S3776") // Complexity OK for a preview function
@Composable
private fun PreviewBoard(
    initialGrid: Grid,
) = TicTacToeTheme {
    var uiState by remember {
        mutableStateOf(
            BoardUiState(
                grid = initialGrid,
                currentPlayerSymbol = Symbol.X,
            ),
        )
    }

    val callback = object : BoardCallback {
        override fun onCellClicked(row: Int, col: Int) {
            if (uiState.grid[row][col] == null) {
                val newSymbol = if (uiState.currentPlayerSymbol == Symbol.X) Symbol.X else Symbol.O
                uiState = uiState.copy(
                    currentPlayerSymbol = if (uiState.currentPlayerSymbol == Symbol.X) Symbol.O else Symbol.X,
                    grid = uiState.grid.mapIndexed { r, rowList ->
                        rowList.mapIndexed { c, cell ->
                            if (r == row && c == col) newSymbol else cell
                        }
                    },
                )
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        BoardView(
            uiState = uiState,
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
