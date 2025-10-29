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
import com.example.tictactoe.domain.model.RequestResult.Success
import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.repository.GridRepository
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import kotlin.test.Test

class ResetGridUseCaseTest {

    @MockK private lateinit var repo: GridRepository

    private lateinit var useCase: ResetGridUseCase

    @Before
    fun setUp() {
        repo = mockk()

        useCase = ResetGridUseCaseImpl(repo)
    }

    @After
    fun tearDown() {
        confirmVerified(repo)
    }

    @Test
    fun `invoke - should return result from repository`() {
        // GIVEN
        // THIS DATA
        val grid = List(3) { List<Symbol?>(3) { null } }
        val repoResult = Success(grid)
        val expectedResult = Success(GameState.InProgress(grid))

        // THIS BEHAVIOR
        every { repo.resetGrid() } returns repoResult

        // WHEN
        val result = useCase()

        // THEN
        // THIS SHOULD HAVE HAPPENED
        verify {
            repo.resetGrid()
        }

        // THIS SHOULD BE
        result shouldBe expectedResult
    }

}
