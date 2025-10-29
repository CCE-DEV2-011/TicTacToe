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

import com.example.tictactoe.domain.model.Failure
import com.example.tictactoe.domain.model.GameState
import com.example.tictactoe.domain.model.GameState.InProgress
import com.example.tictactoe.domain.model.GridError
import com.example.tictactoe.domain.model.RequestResult.Error
import com.example.tictactoe.domain.model.RequestResult.Success
import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.usecase.PlayMoveUseCase
import com.example.tictactoe.domain.usecase.ResetGridUseCase
import com.example.tictactoe.features.provider.ResourceProvider
import com.example.tictactoe.ui.R
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
    @MockK private lateinit var resourceProvider: ResourceProvider

    private lateinit var viewModel: BoardViewModel

    @Before
    fun setUp() {
        resetGridUseCase = mockk()
        playMoveUseCase = mockk()
        resourceProvider = mockk()
    }

    @After
    fun tearDown() {
        confirmVerified(resetGridUseCase, playMoveUseCase, resourceProvider)
    }

    @Test
    fun `initialization - should set initial UI state`() {
        // GIVEN
        // THIS DATA
        val initialState = InProgress(emptyGrid)

        // THIS BEHAVIOR
        every { resetGridUseCase() } returns Success(initialState)

        // WHEN
        viewModel = BoardViewModel(resetGridUseCase, playMoveUseCase, resourceProvider)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        verify {
            resetGridUseCase()
        }

        // THIS SHOULD BE
        viewModel shouldHaveState initialState
    }

    private fun createViewModelAndDisregardInit(withFirstMove: Boolean = false) {
        val initialState = InProgress(emptyGrid)
        every { resetGridUseCase() } returns Success(InProgress(emptyGrid))
        viewModel = BoardViewModel(resetGridUseCase, playMoveUseCase, resourceProvider)

        if (withFirstMove) {
            val newGrid = listOf(
                listOf(Symbol.X, null, null),
                List<Symbol?>(3) { null },
                List<Symbol?>(3) { null },
            )
            every { playMoveUseCase(row = 0, col = 0, initialState) } returns Success(InProgress(newGrid, Symbol.O))

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
        val currentState = InProgress(
            grid = listOf(
                listOf(Symbol.X.takeIf { isSecondMove }, null, null),
                List<Symbol?>(3) { null },
                List<Symbol?>(3) { null },
            ),
            currentPlayerSymbol = currentSymbol,
        )
        val newGrid = listOf(
            listOf(Symbol.X.takeIf { isSecondMove }, null, null),
            listOf(null, currentSymbol, null),
            List<Symbol?>(3) { null },
        )
        val expectedUiState = InProgress(
            grid = newGrid,
            currentPlayerSymbol = nextSymbol,
        )
        val playMoveResult = Success(InProgress(newGrid, nextSymbol))

        // THIS BEHAVIOR
        every { playMoveUseCase(row, col, currentState) } returns playMoveResult

        // WHEN
        viewModel.onCellClicked(row, col)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        verify {
            playMoveUseCase(row, col, currentState)
        }
        // THIS SHOULD BE
        viewModel shouldHaveState expectedUiState
    }

    @Suppress("unused", "UnusedPrivateMember")
    private fun getEndOfGameParams() = arrayOf(
        mockk<GameState.XWins>(),
        mockk<GameState.OWins>(),
        mockk<GameState.Draw>(),
    )

    @Test
    @Parameters(method = "getEndOfGameParams")
    fun `onCellClicked - when move is played successfully - win or draw - should update UI state`(
        expectedUiState: GameState,
    ) {
        // GIVEN
        // THIS SETUP
        createViewModelAndDisregardInit(withFirstMove = true)

        // THIS DATA
        val row = 2
        val col = 0
        val playMoveResult = Success(expectedUiState)

        // THIS BEHAVIOR
        every { playMoveUseCase(row, col, any()) } returns playMoveResult

        // WHEN
        viewModel.onCellClicked(row, col)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        verify {
            playMoveUseCase(row, col, any())
        }
        // THIS SHOULD BE
        viewModel shouldHaveState expectedUiState
    }

    @Suppress("unused", "UnusedPrivateMember")
    private fun getMoveErrorParams() = arrayOf(
        GridError.CELL_ALREADY_TAKEN,
        GridError.OUT_OF_BOUNDS,
        mockk<Failure>(),
    )

    @Test
    @Parameters(method = "getMoveErrorParams")
    fun `onCellClick - when move returns error - state should not update and toast should be displayed`(
        failure: Failure,
    ) {
        // GIVEN
        // THIS SETUP
        createViewModelAndDisregardInit()

        // THIS DATA
        val row = 2
        val col = 0
        val playMoveResult = Error(failure)
        val expectedUiState = InProgress(emptyGrid)
        val messageResId = when (failure) {
            GridError.CELL_ALREADY_TAKEN -> R.string.cell_already_taken
            else -> R.string.unexpected_error
        }
        val errorMessage = "errorMessage"

        // THIS BEHAVIOR
        every { playMoveUseCase(row, col, any()) } returns playMoveResult
        every { resourceProvider.getString(messageResId) } returns errorMessage

        // WHEN
        viewModel.onCellClicked(row, col)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        verify {
            playMoveUseCase(row, col, any())
            resourceProvider.getString(messageResId)
        }
        // THIS SHOULD BE
        viewModel shouldHaveState expectedUiState
        viewModel shouldHaveSnackbarMessage errorMessage
    }

    private infix fun BoardViewModel.shouldHaveState(expectedState: GameState) {
        gameState.value shouldBe expectedState
    }

    private infix fun BoardViewModel.shouldHaveSnackbarMessage(message: String) {
        viewModel.snackbarMessage.value shouldBe message
    }

    private val emptyGrid = List(3) { List<Symbol?>(3) { null } }

}
