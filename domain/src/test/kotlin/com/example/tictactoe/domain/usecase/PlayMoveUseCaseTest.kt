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
import com.example.tictactoe.domain.model.GridError
import com.example.tictactoe.domain.model.Move
import com.example.tictactoe.domain.model.RequestResult
import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.repository.Grid
import com.example.tictactoe.domain.repository.GridRepository
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import kotlin.enums.EnumEntries
import kotlin.test.Test

@RunWith(JUnitParamsRunner::class)
class PlayMoveUseCaseTest {

    @MockK private lateinit var repository: GridRepository
    @MockK private lateinit var checkGameStateUseCase: CheckGameStateUseCase

    private lateinit var useCase: PlayMoveUseCase

    @Before
    fun setup() {
        repository = mockk()
        checkGameStateUseCase = mockk()

        useCase = PlayMoveUseCaseImpl(repository, checkGameStateUseCase)
    }

    @After
    fun tearDown() {
        confirmVerified(repository, checkGameStateUseCase)
    }

    @Test
    fun `invoke - repo success - should call repository and return checkState result`() {
        // GIVEN
        // THIS DATA
        val grid = mockk<Grid>()
        val repoResult = RequestResult.Success(grid)
        val move = Move(row = 1, col = 2, symbol = Symbol.X)
        val expectedStateResult = mockk<RequestResult.Success<GameState>>()

        // THIS BEHAVIOR
        every { repository.playMove(move) } returns repoResult
        every { checkGameStateUseCase(grid, move) } returns expectedStateResult

        // WHEN
        val result = useCase(move)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        verifySequence {
            repository.playMove(move)
            checkGameStateUseCase(grid, move)
        }

        // THIS SHOULD BE
        result shouldBe expectedStateResult
    }

    @Suppress("unused", "UnusedPrivateMember")
    private fun getRepoFailureParams(): EnumEntries<GridError> = GridError.entries

    @Test
    @Parameters(method = "getRepoFailureParams")
    fun `invoke - repo failure - should call repository and return failure`(error: GridError) {
        // GIVEN
        // THIS DATA
        val repoResult: RequestResult<Grid, GridError> = RequestResult.Error(error)
        val move = Move(row = 0, col = 1, symbol = Symbol.O)

        // THIS BEHAVIOR
        every { repository.playMove(move) } returns repoResult

        // WHEN
        val result = useCase(move)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        verify {
            repository.playMove(move)
        }

        // THIS SHOULD BE
        result shouldBe repoResult
    }

}
