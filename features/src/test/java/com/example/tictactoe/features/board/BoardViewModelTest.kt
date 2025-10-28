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

import com.example.tictactoe.domain.model.RequestResult.Success
import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.usecase.ResetGridUseCase
import com.example.tictactoe.features.board.BoardViewModel.BoardUiState
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.After
import org.junit.Before
import kotlin.test.Test

class BoardViewModelTest {

    @MockK private lateinit var resetGridUseCase: ResetGridUseCase

    private lateinit var viewModel: BoardViewModel

    @Before
    fun setUp() {
        resetGridUseCase = mockk()
    }

    @After
    fun tearDown() {
        confirmVerified(resetGridUseCase)
    }

    @Test
    fun `initialization - should set initial UI state`() {
        // GIVEN
        // THIS DATA
        val initialGrid = List(3) { List<Symbol?>(3) { null } }
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
        )

        // THEN
        // THIS SHOULD HAVE HAPPENED
        verifySequence {
            resetGridUseCase()
        }

        // THIS SHOULD BE
        viewModel shouldHaveState initialUiState
    }

    private infix fun BoardViewModel.shouldHaveState(expectedState: BoardUiState) {
        uiState.value shouldBe expectedState
    }

}
