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

package com.example.tictactoe.domain.model

import com.example.tictactoe.domain.repository.Grid

sealed interface Failure

sealed interface RequestResult<out D, out E : Failure> {
    data class Success<D>(val data: D) : RequestResult<D, Nothing>
    data class Error<E : Failure>(val error: E) : RequestResult<Nothing, E>
}

enum class GridError : Failure {
    CELL_ALREADY_TAKEN,
    OUT_OF_BOUNDS,
}

sealed interface GameState {
    val grid: Grid
    val currentPlayerSymbol: Symbol

    data class InProgress(override val grid: Grid, override val currentPlayerSymbol: Symbol = Symbol.X) : GameState
    data class Draw(override val grid: Grid, override val currentPlayerSymbol: Symbol) : GameState
    data class XWins(override val grid: Grid) : GameState {
        override val currentPlayerSymbol: Symbol = Symbol.X
    }

    data class OWins(override val grid: Grid) : GameState {
        override val currentPlayerSymbol: Symbol = Symbol.O
    }
}
