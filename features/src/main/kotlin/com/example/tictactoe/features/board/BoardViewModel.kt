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

import androidx.lifecycle.ViewModel
import com.example.tictactoe.domain.model.Failure
import com.example.tictactoe.domain.model.GameState
import com.example.tictactoe.domain.model.GridError
import com.example.tictactoe.domain.model.Move
import com.example.tictactoe.domain.model.RequestResult
import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.repository.Grid
import com.example.tictactoe.domain.usecase.PlayMoveUseCase
import com.example.tictactoe.domain.usecase.ResetGridUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BoardViewModel(
    resetGrid: ResetGridUseCase,
    private val playMove: PlayMoveUseCase,
) : ViewModel(), BoardCallback {

    private val _uiState: MutableStateFlow<BoardUiState>
    val uiState: StateFlow<BoardUiState>
        get() = _uiState.asStateFlow()

    init {
        val initialGrid = resetGrid().data
        _uiState = MutableStateFlow(
            BoardUiState(
                grid = initialGrid,
                currentPlayerSymbol = Symbol.X,
            ),
        )
    }

    override fun onCellClicked(row: Int, col: Int) {
        when (val result = playMove(Move(row, col, _uiState.value.currentPlayerSymbol))) {
            is RequestResult.Success -> handleMoveSuccess(result.data)
            is RequestResult.Error -> handleMoveFailure(result.error)
        }
    }

    private fun handleMoveSuccess(
        gameState: GameState,
    ) {
        _uiState.value = _uiState.value.copy(
            grid = gameState.grid,
            currentPlayerSymbol = when (_uiState.value.currentPlayerSymbol) {
                Symbol.X -> Symbol.O
                Symbol.O -> Symbol.X
            },
        )

        // TODO Toast and disable if draw or win
    }

    private fun handleMoveFailure(error: Failure) {
        when (error) {
            GridError.CELL_ALREADY_TAKEN -> {
                // TODO Toast cell already taken
            }
            else -> {
                // TODO Toast unexpected error
            }
        }
    }

    data class BoardUiState(
        val grid: Grid,
        val currentPlayerSymbol: Symbol,
    )

}

interface BoardCallback {
    fun onCellClicked(row: Int, col: Int)
}
