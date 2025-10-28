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
     * @param move The [Move] to be played on the grid
     *
     * @return A [RequestResult] containing the updated [GameState] or a [Failure] if the move fails
     */
    operator fun invoke(move: Move): RequestResult<GameState, Failure>

}

class PlayMoveUseCaseImpl(
    private val repository: GridRepository,
    private val checkGameState: CheckGameStateUseCase,
) : PlayMoveUseCase {

    override fun invoke(move: Move): RequestResult<GameState, Failure> = when (val result = repository.playMove(move)) {
        is RequestResult.Success -> checkGameState(result.data, move)
        is RequestResult.Error -> RequestResult.Error(result.error)
    }

}
