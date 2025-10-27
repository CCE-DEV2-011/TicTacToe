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
import io.kotest.matchers.shouldBe
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(JUnitParamsRunner::class)
class GridRepositoryTest {

    private lateinit var repository: GridRepository

    private val emptyGrid: Grid = buildEmptyGrid()

    // region INIT

    @Test
    fun `init - repo grid should be empty`() {
        // GIVEN
        // THIS DATA
        val expected = emptyGrid

        // WHEN
        repository = GridRepositoryImpl()

        // THEN
        // THIS SHOULD BE
        repository shouldHaveGrid expected
    }

    // endregion INIT

    // region MOVE

    @Suppress("unused", "UnusedPrivateMember")
    private fun getFirstMoveParams(): List<Array<Int>> = (0..2).flatMap { row ->
        (0..2).map { col ->
            arrayOf(row, col)
        }
    }

    @Test
    @Parameters(method = "getFirstMoveParams")
    fun `first move - valid - should return success with correct grid`(
        row: Int,
        col: Int,
    ) {
        // GIVEN
        // THIS SETUP
        createRepoAndDisregardInit()

        // THIS DATA
        val expectedGrid = buildEmptyGrid { grid ->
            grid[row][col] = Symbol.X
        }
        val expected = RequestResult.Success(expectedGrid)

        // WHEN
        val result = repository.playMove(row, col, Symbol.X)

        // THEN
        // THIS SHOULD BE
        repository shouldHaveGrid expectedGrid
        result shouldBe expected
    }

    @Suppress("unused", "UnusedPrivateMember")
    private fun getOutOfBoundsMoveParams(): List<Array<Int>> = listOf(
        arrayOf(-1, 0),
        arrayOf(0, -1),
        arrayOf(-1, -1),
        arrayOf(3, 0),
        arrayOf(0, 3),
        arrayOf(3, 3),
    )

    @Test
    @Parameters(method = "getOutOfBoundsMoveParams")
    fun `move - out of bounds - should return error and not update grid`(
        row: Int,
        col: Int,
    ) {
        // GIVEN
        // THIS SETUP
        createRepoAndDisregardInit()

        // THIS DATA
        val expectedGrid = emptyGrid
        val expected = RequestResult.Error(GridError.OUT_OF_BOUNDS)

        // WHEN
        val result = repository.playMove(row, col, Symbol.X)

        // THEN
        // THIS SHOULD BE
        repository shouldHaveGrid expectedGrid
        result shouldBe expected
    }

    @Suppress("unused", "UnusedPrivateMember", "UNCHECKED_CAST")
    private fun getCellTakenParams(): List<Array<Any>> = getFirstMoveParams().flatMap { pos ->
        listOf(
            arrayOf(pos[0], pos[1], Symbol.X),
            arrayOf(pos[0], pos[1], Symbol.O),
        )
    }

    @Test
    @Parameters(method = "getCellTakenParams")
    fun `second move - cell already taken - should return error and not update grid`(
        row: Int,
        col: Int,
        firstSymbol: Symbol,
    ) {
        // GIVEN
        // THIS SETUP
        createRepoAndDisregardInit()
        repository.playMove(row, col, firstSymbol) // Fill the cell

        // THIS DATA
        val symbolToPlay = if (firstSymbol == Symbol.X) Symbol.O else Symbol.X
        val expectedGrid = buildEmptyGrid { grid ->
            grid[row][col] = firstSymbol
        }
        val expected = RequestResult.Error(GridError.CELL_ALREADY_TAKEN)

        // WHEN
        val result = repository.playMove(row, col, symbolToPlay)

        // THEN
        // THIS SHOULD BE
        repository shouldHaveGrid expectedGrid
        result shouldBe expected
    }

    @Test
    @Parameters(method = "getFirstMoveParams")
    fun `second move - valid - should return success with correct grid`(
        firstMoveRow: Int,
        firstMoveCol: Int,
    ) {
        // GIVEN
        // THIS SETUP
        createRepoAndDisregardInit()
        repository.playMove(firstMoveRow, firstMoveCol, Symbol.X) // First move

        // THIS DATA
        val secondMoveRow = (0..2).first { it != firstMoveRow }
        val secondMoveCol = (0..2).first { it != firstMoveCol }
        val expectedGrid = buildEmptyGrid { grid ->
            grid[firstMoveRow][firstMoveCol] = Symbol.X
            grid[secondMoveRow][secondMoveCol] = Symbol.O
        }
        val expected = RequestResult.Success(expectedGrid)

        // WHEN
        val result = repository.playMove(secondMoveRow, secondMoveCol, Symbol.O)

        // THEN
        // THIS SHOULD BE
        repository shouldHaveGrid expectedGrid
        result shouldBe expected
    }

    // endregion MOVE

    private fun createRepoAndDisregardInit() {
        repository = GridRepositoryImpl()
    }

    private infix fun GridRepository.shouldHaveGrid(expected: List<List<Symbol?>>) = grid shouldBe expected

    private fun buildEmptyGrid(block: (MutableList<MutableList<Symbol?>>) -> Unit = {}): MutableList<MutableList<Symbol?>> = MutableList(3) {
        MutableList<Symbol?>(3) { null }
    }.apply { block(this) }

}
