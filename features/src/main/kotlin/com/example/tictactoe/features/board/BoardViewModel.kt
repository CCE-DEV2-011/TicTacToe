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

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.example.tictactoe.domain.model.Failure
import com.example.tictactoe.domain.model.GameState
import com.example.tictactoe.domain.model.GridError
import com.example.tictactoe.domain.model.RequestResult
import com.example.tictactoe.domain.usecase.PlayMoveUseCase
import com.example.tictactoe.domain.usecase.ResetGridUseCase
import com.example.tictactoe.features.provider.ResourceProvider
import com.example.tictactoe.ui.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BoardViewModel(
    resetGrid: ResetGridUseCase,
    private val playMove: PlayMoveUseCase,
    private val resourceProvider: ResourceProvider,
) : ViewModel(), BoardCallback {

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    private val _gameState: MutableStateFlow<GameState>
    val gameState: StateFlow<GameState>
        get() = _gameState.asStateFlow()

    private val inProgressState: GameState.InProgress?
        get() = _gameState.value as? GameState.InProgress

    init {
        val initialState = resetGrid().data
        _gameState = MutableStateFlow(initialState)
    }

    override fun onCellClicked(row: Int, col: Int) {
        inProgressState?.let { state ->
            when (val result = playMove(row, col, state)) {
                is RequestResult.Success -> _gameState.value = result.data
                is RequestResult.Error -> handleMoveFailure(result.error)
            }
        } ?: showSnackbarMessage(R.string.unexpected_error)
    }

    private fun handleMoveFailure(error: Failure) {
        when (error) {
            GridError.CELL_ALREADY_TAKEN -> showSnackbarMessage(R.string.cell_already_taken)
            else -> showSnackbarMessage(R.string.unexpected_error)
        }
    }

    private fun showSnackbarMessage(@StringRes messageId: Int) {
        showSnackbarMessage(resourceProvider.getString(messageId))
    }

    private fun showSnackbarMessage(message: String) {
        _snackbarMessage.value = message
    }

    override fun dismissSnackbar() {
        _snackbarMessage.value = null
    }

}

interface BoardCallback {
    fun onCellClicked(row: Int, col: Int)
    fun dismissSnackbar()
}
