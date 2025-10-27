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
import com.example.tictactoe.domain.model.RequestResult
import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.repository.Grid
import com.example.tictactoe.domain.repository.GridRepository

class GridRepositoryImpl : GridRepository {

    override var grid: MutableList<MutableList<Symbol?>> = MutableList(3) { MutableList(3) { null } }
        private set

    override fun resetGrid(): RequestResult<Grid, GridError> {
        TODO()
    }

    override fun playMove(row: Int, col: Int, symbol: Symbol): RequestResult<Grid, GridError> = when {
        row !in 0..2 || col !in 0..2 -> RequestResult.Error(GridError.OUT_OF_BOUNDS) // out of bounds
        grid[row][col] != null -> RequestResult.Error(GridError.CELL_ALREADY_TAKEN) // cell already taken
        else -> playValidMove(row, col, symbol)
    }

    private fun playValidMove(row: Int, col: Int, symbol: Symbol): RequestResult.Success<Grid> {
        grid[row][col] = symbol
        return RequestResult.Success(grid)
    }

}
