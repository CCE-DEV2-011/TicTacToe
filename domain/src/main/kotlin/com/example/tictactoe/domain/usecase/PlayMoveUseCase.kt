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
import com.example.tictactoe.domain.model.RequestResult
import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.repository.Grid
import com.example.tictactoe.domain.repository.GridRepository

fun interface PlayMoveUseCase {

    /**
     * Plays a move on the grid at the specified row and column with the given symbol.
     */
    operator fun invoke(row: Int, col: Int, symbol: Symbol): RequestResult<Grid, Failure>

}

class PlayMoveUseCaseImpl(
    private val repository: GridRepository,
) : PlayMoveUseCase {

    override fun invoke(row: Int, col: Int, symbol: Symbol): RequestResult<Grid, Failure> {
        // TODO: Check for win or full grid after the move
        return repository.playMove(row, col, symbol)
    }

}
