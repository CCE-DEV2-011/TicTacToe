package com.example.tictactoe.domain.usecase

import com.example.tictactoe.domain.model.GameState.InProgress
import com.example.tictactoe.domain.model.RequestResult
import com.example.tictactoe.domain.repository.GridRepository

fun interface ResetGridUseCase {
    operator fun invoke(): RequestResult.Success<InProgress>
}

class ResetGridUseCaseImpl(
    private val repository: GridRepository,
) : ResetGridUseCase {

    override fun invoke(): RequestResult.Success<InProgress> {
        val newGrid = repository.resetGrid().data
        return RequestResult.Success(InProgress(newGrid))
    }

}
