package com.example.tictactoe.domain.usecase

import com.example.tictactoe.domain.model.RequestResult
import com.example.tictactoe.domain.repository.Grid
import com.example.tictactoe.domain.repository.GridRepository

fun interface ResetGridUseCase {
    operator fun invoke(): RequestResult.Success<Grid>
}

class ResetGridUseCaseImpl(
    private val repository: GridRepository,
) : ResetGridUseCase {

    override fun invoke() = repository.resetGrid()

}
