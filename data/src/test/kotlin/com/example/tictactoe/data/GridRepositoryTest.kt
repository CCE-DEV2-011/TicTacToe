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

import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.repository.GridRepository
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class GridRepositoryTest {

    private lateinit var repository: GridRepository

    private val emptyGrid: MutableList<MutableList<Symbol?>> = MutableList(3) { MutableList(3) { null } }

    @Test
    fun `init - repo grid should be empty`() {
        // GIVEN
        // THIS DATA
        val expected = emptyGrid

        // WHEN
        repository = GridRepositoryImpl()

        // THEN
        repository shouldHaveGrid expected
    }

    private infix fun GridRepository.shouldHaveGrid(expected: List<List<Symbol?>>) = grid shouldBe expected

}
