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

package com.example.tictactoe.domain.usecase

import com.example.tictactoe.domain.model.Failure
import com.example.tictactoe.domain.model.GameState
import com.example.tictactoe.domain.model.Move
import com.example.tictactoe.domain.model.RequestResult
import com.example.tictactoe.domain.repository.GridRepository

fun interface PlayMoveUseCase {

    /**
     * Plays a move on the grid at the specified row and column with the given symbol.
     *
     * @param row The row index where the move is to be played
     * @param col The column index where the move is to be played
     * @param gameState The current [GameState] before the move is played
     *
     * @return A [RequestResult] containing the updated [GameState] or a [Failure] if the move fails
     */
    operator fun invoke(row: Int, col: Int, gameState: GameState.InProgress): RequestResult<GameState, Failure>

}

class PlayMoveUseCaseImpl(
    private val repository: GridRepository,
    private val updateGameState: UpdateGameStateUseCase,
) : PlayMoveUseCase {

    override fun invoke(
        row: Int,
        col: Int,
        gameState: GameState.InProgress,
    ): RequestResult<GameState, Failure> {
        val move = Move(row, col, gameState.currentPlayerSymbol)
        return when (val result = repository.playMove(move)) {
            is RequestResult.Success -> updateGameState(result.data, move, gameState)
            is RequestResult.Error -> RequestResult.Error(result.error)
        }
    }

}
