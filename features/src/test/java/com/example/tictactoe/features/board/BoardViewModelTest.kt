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

import com.example.tictactoe.domain.model.GameState.InProgress
import com.example.tictactoe.domain.model.Move
import com.example.tictactoe.domain.model.RequestResult.Success
import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.usecase.PlayMoveUseCase
import com.example.tictactoe.domain.usecase.ResetGridUseCase
import com.example.tictactoe.features.board.BoardViewModel.BoardUiState
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(JUnitParamsRunner::class)
class BoardViewModelTest {

    @MockK private lateinit var resetGridUseCase: ResetGridUseCase
    @MockK private lateinit var playMoveUseCase: PlayMoveUseCase

    private lateinit var viewModel: BoardViewModel

    @Before
    fun setUp() {
        resetGridUseCase = mockk()
        playMoveUseCase = mockk()
    }

    @After
    fun tearDown() {
        confirmVerified(resetGridUseCase, playMoveUseCase)
    }

    @Test
    fun `initialization - should set initial UI state`() {
        // GIVEN
        // THIS DATA
        val initialGrid = emptyGrid
        val initialSymbol = Symbol.X
        val initialUiState = BoardUiState(
            grid = initialGrid,
            currentPlayerSymbol = initialSymbol,
        )

        // THIS BEHAVIOR
        every { resetGridUseCase() } returns Success(initialGrid)

        // WHEN
        viewModel = BoardViewModel(
            resetGrid = resetGridUseCase,
            playMove = playMoveUseCase,
        )

        // THEN
        // THIS SHOULD HAVE HAPPENED
        verify {
            resetGridUseCase()
        }

        // THIS SHOULD BE
        viewModel shouldHaveState initialUiState
    }

    private fun createViewModelAndDisregardInit(withFirstMove: Boolean = false) {
        every { resetGridUseCase() } returns Success(emptyGrid)
        viewModel = BoardViewModel(
            resetGrid = resetGridUseCase,
            playMove = playMoveUseCase,
        )

        if (withFirstMove) {
            val newGrid = listOf(
                listOf(Symbol.X, null, null),
                List<Symbol?>(3) { null },
                List<Symbol?>(3) { null },
            )
            every { playMoveUseCase(Move(row = 0, col = 0, symbol = Symbol.X)) } returns Success(InProgress(grid = newGrid))

            viewModel.onCellClicked(row = 0, col = 0)
        }

        clearAllMocks()
    }

    @Test
    @Parameters("true", "false")
    fun `onCellClicked - when move is played successfully - no win or draw - should update UI state`(
        isSecondMove: Boolean,
    ) {
        // GIVEN
        // THIS SETUP
        createViewModelAndDisregardInit(isSecondMove)

        // THIS DATA
        val currentSymbol = if (isSecondMove) Symbol.O else Symbol.X
        val nextSymbol = if (currentSymbol == Symbol.X) Symbol.O else Symbol.X
        val row = 1
        val col = 1
        val move = Move(row, col, currentSymbol)
        val newGrid = listOf(
            listOf(Symbol.X.takeIf { isSecondMove }, null, null),
            listOf(null, currentSymbol, null),
            List<Symbol?>(3) { null },
        )
        val expectedUiState = BoardUiState(
            grid = newGrid,
            currentPlayerSymbol = nextSymbol,
        )
        val playMoveResult = Success(InProgress(grid = newGrid))

        // THIS BEHAVIOR
        every { playMoveUseCase(move) } returns playMoveResult

        // WHEN
        viewModel.onCellClicked(row, col)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        verify {
            playMoveUseCase(move)
        }
        // THIS SHOULD BE
        viewModel shouldHaveState expectedUiState
    }

    // TODO Test draw, win and failure cases

    private infix fun BoardViewModel.shouldHaveState(expectedState: BoardUiState) {
        uiState.value shouldBe expectedState
    }

    private val emptyGrid = List(3) { List<Symbol?>(3) { null } }

}
