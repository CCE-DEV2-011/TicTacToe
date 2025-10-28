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

package com.example.tictactoe.data

import com.example.tictactoe.domain.model.GridError
import com.example.tictactoe.domain.model.Move
import com.example.tictactoe.domain.model.RequestResult
import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.repository.Grid
import com.example.tictactoe.domain.repository.GridRepository

class GridRepositoryImpl : GridRepository {

    override var grid: MutableList<MutableList<Symbol?>> = buildEmptyGrid()
        private set

    override fun resetGrid(): RequestResult.Success<Grid> {
        grid = buildEmptyGrid()
        return RequestResult.Success(grid)
    }

    override fun playMove(move: Move): RequestResult<Grid, GridError> = when {
        move.row !in 0..2 || move.col !in 0..2 -> RequestResult.Error(GridError.OUT_OF_BOUNDS) // out of bounds
        grid[move.row][move.col] != null -> RequestResult.Error(GridError.CELL_ALREADY_TAKEN) // cell already taken
        else -> playValidMove(move)
    }

    private fun playValidMove(move: Move): RequestResult.Success<Grid> {
        grid[move.row][move.col] = move.symbol
        return RequestResult.Success(grid)
    }

    private fun buildEmptyGrid() = MutableList(3) { MutableList<Symbol?>(3) { null } }

}
