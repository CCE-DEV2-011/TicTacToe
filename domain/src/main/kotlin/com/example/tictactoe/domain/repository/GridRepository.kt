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

package com.example.tictactoe.domain.repository

import com.example.tictactoe.domain.model.GridError
import com.example.tictactoe.domain.model.RequestResult
import com.example.tictactoe.domain.model.Symbol

typealias Grid = List<List<Symbol?>>

interface GridRepository {

    val grid: Grid

    /**
     * Plays a move on the grid at the specified row and column with the given symbol.
     *
     * @param row The row index where the move is to be played.
     * @param col The column index where the move is to be played.
     * @param symbol The symbol to place on the grid (e.g., X or O).
     *
     * @return A [RequestResult] containing the updated grid or an error if the move
     */
    fun playMove(row: Int, col: Int, symbol: Symbol): RequestResult<Grid, GridError>

    /**
     * Resets the game grid to its initial empty state.
     *
     * @return A [RequestResult] containing the reset grid or an error if the operation fails.
     */
    fun resetGrid(): RequestResult<Grid, GridError>

}
