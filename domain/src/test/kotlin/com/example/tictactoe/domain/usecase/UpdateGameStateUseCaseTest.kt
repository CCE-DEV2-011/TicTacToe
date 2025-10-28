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

import com.example.tictactoe.domain.model.GameState
import com.example.tictactoe.domain.model.Move
import com.example.tictactoe.domain.model.RequestResult
import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.repository.Grid
import io.kotest.matchers.shouldBe
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(JUnitParamsRunner::class)
class UpdateGameStateUseCaseTest {

    private lateinit var useCase: UpdateGameStateUseCase

    @Before
    fun setup() {
        useCase = UpdateGameStateUseCaseImpl()
    }

    // Grid that will still be in progress after placing X on 0,0
    @Suppress("unused", "UnusedPrivateMember")
    private fun getInProgressParams(): Array<Grid> = arrayOf(
        // Empty grid
        List(3) { List(3) { null } },
        // Incomplete lines
        listOf(
            listOf(null, Symbol.X, null),
            listOf(Symbol.X, null, Symbol.X),
            listOf(null, null, Symbol.X),
        ),
        // Mixed symbols lines
        listOf(
            listOf(null, Symbol.X, Symbol.O),
            listOf(Symbol.X, Symbol.O, Symbol.X),
            listOf(Symbol.O, null, Symbol.X),
        ),
    )

    @Test
    @Parameters(method = "getInProgressParams")
    fun `invoke - non-winning, non-full grid - should return IN_PROGRESS state`(
        previousGrid: Grid,
    ) {
        // GIVEN
        // THIS DATA
        val previousState = GameState.InProgress(previousGrid)
        val move = Move(row = 0, col = 0, symbol = Symbol.X)
        val newGrid = previousGrid.map { it.toMutableList() }.toMutableList()
        newGrid[move.row][move.col] = move.symbol
        val expectedState = GameState.InProgress(newGrid, Symbol.O)

        // WHEN
        val result = useCase(newGrid, move, previousState)

        // THEN
        // THIS SHOULD BE
        result shouldBe RequestResult.Success(expectedState)
    }

    // Grids that will end in a draw after placing X on any position
    @Suppress("unused", "UnusedPrivateMember")
    private fun getDrawParams(): Array<Array<Any>> = arrayOf(
        // Row 0
        arrayOf(
            Move(row = 0, col = 0, symbol = Symbol.X),
            listOf(
                listOf(Symbol.X, Symbol.X, Symbol.O),
                listOf(Symbol.O, Symbol.X, Symbol.X),
                listOf(Symbol.X, Symbol.O, Symbol.O),
            ),
        ),
        arrayOf(
            Move(row = 0, col = 1, symbol = Symbol.X),
            listOf(
                listOf(Symbol.O, Symbol.X, Symbol.X),
                listOf(Symbol.X, Symbol.O, Symbol.O),
                listOf(Symbol.O, Symbol.X, Symbol.O),
            ),
        ),
        arrayOf(
            Move(row = 0, col = 2, symbol = Symbol.X),
            listOf(
                listOf(Symbol.O, Symbol.X, Symbol.X),
                listOf(Symbol.X, Symbol.O, Symbol.O),
                listOf(Symbol.X, Symbol.O, Symbol.X),
            ),
        ),
        // Row 1
        arrayOf(
            Move(row = 1, col = 0, symbol = Symbol.X),
            listOf(
                listOf(Symbol.O, Symbol.X, Symbol.X),
                listOf(Symbol.X, Symbol.O, Symbol.O),
                listOf(Symbol.X, Symbol.O, Symbol.X),
            ),
        ),
        arrayOf(
            Move(row = 1, col = 1, symbol = Symbol.X),
            listOf(
                listOf(Symbol.X, Symbol.O, Symbol.X),
                listOf(Symbol.X, Symbol.X, Symbol.O),
                listOf(Symbol.O, Symbol.X, Symbol.O),
            ),
        ),
        arrayOf(
            Move(row = 1, col = 2, symbol = Symbol.X),
            listOf(
                listOf(Symbol.X, Symbol.O, Symbol.X),
                listOf(Symbol.O, Symbol.X, Symbol.X),
                listOf(Symbol.O, Symbol.X, Symbol.O),
            ),
        ),
        // Row 2
        arrayOf(
            Move(row = 2, col = 0, symbol = Symbol.X),
            listOf(
                listOf(Symbol.O, Symbol.X, Symbol.O),
                listOf(Symbol.O, Symbol.X, Symbol.O),
                listOf(Symbol.X, Symbol.O, Symbol.X),
            ),
        ),
        arrayOf(
            Move(row = 2, col = 1, symbol = Symbol.X),
            listOf(
                listOf(Symbol.X, Symbol.O, Symbol.X),
                listOf(Symbol.O, Symbol.X, Symbol.O),
                listOf(Symbol.O, Symbol.X, Symbol.X),
            ),
        ),
        arrayOf(
            Move(row = 2, col = 2, symbol = Symbol.X),
            listOf(
                listOf(Symbol.O, Symbol.O, Symbol.X),
                listOf(Symbol.O, Symbol.X, Symbol.O),
                listOf(Symbol.X, Symbol.O, Symbol.X),
            ),
        ),
    )

    @Test
    @Parameters(method = "getDrawParams")
    fun `invoke - non-winning, full grid - should return DRAW state`(
        move: Move,
        grid: Grid,
    ) {
        // GIVEN
        // THIS DATA
        val expectedState = GameState.Draw(grid, move.symbol)

        // WHEN
        val initialGrid = grid.map { it.toMutableList() }.toMutableList()
        initialGrid[move.row][move.col] = null
        val result = useCase(grid, move, GameState.InProgress(initialGrid, move.symbol))

        // THEN
        // THIS SHOULD BE
        result shouldBe RequestResult.Success(expectedState)
    }

    // Grids that will end in a winning move after placing X, or O on any position
    @Suppress("unused", "UnusedPrivateMember", "NestedBlockDepth")
    fun getWinningParams(): List<Array<Any>> {
        val params = mutableListOf<Array<Any>>()
        val symbols = listOf(Symbol.X, Symbol.O)
        for (symbol in symbols) {
            for (row in 0..2) {
                for (col in 0..2) {
                    // Row win
                    val rowWin = List(3) { r ->
                        List(3) { c ->
                            if (r == row) symbol else null
                        }
                    }
                    params.add(arrayOf(Move(row, col, symbol), rowWin))

                    // Column win
                    val colWin = List(3) { r ->
                        List(3) { c ->
                            if (c == col) symbol else null
                        }
                    }
                    params.add(arrayOf(Move(row, col, symbol), colWin))

                    // Main diagonal win if applicable
                    if (row == col) {
                        val diagWin = List(3) { r ->
                            List(3) { c ->
                                if (r == c) symbol else null
                            }
                        }
                        params.add(arrayOf(Move(row, col, symbol), diagWin))
                    }

                    // Anti-diagonal win if applicable
                    if (row + col == 2) {
                        val antiDiagWin = List(3) { r ->
                            List(3) { c ->
                                if (r + c == 2) symbol else null
                            }
                        }
                        params.add(arrayOf(Move(row, col, symbol), antiDiagWin))
                    }
                }
            }
        }
        return params
    }

    @Test
    @Parameters(method = "getWinningParams")
    fun `invoke - winning move - should return X_WINS or O_WINS state`(
        move: Move,
        grid: Grid,
    ) {
        // GIVEN
        // THIS DATA
        val previousGrid = grid.map { it.toMutableList() }.toMutableList()
        previousGrid[move.row][move.col] = null
        val previousState = GameState.InProgress(previousGrid, move.symbol)
        val expectedState = when (move.symbol) {
            Symbol.X -> GameState.XWins(grid)
            Symbol.O -> GameState.OWins(grid)
        }

        // WHEN
        val result = useCase(grid, move, previousState)

        // THEN
        // THIS SHOULD BE
        result shouldBe RequestResult.Success(expectedState)
    }

}
