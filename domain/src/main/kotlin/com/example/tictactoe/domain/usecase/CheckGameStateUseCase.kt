package com.example.tictactoe.domain.usecase

import com.example.tictactoe.domain.model.GameState
import com.example.tictactoe.domain.model.Move
import com.example.tictactoe.domain.model.RequestResult
import com.example.tictactoe.domain.model.Symbol
import com.example.tictactoe.domain.repository.Grid

fun interface CheckGameStateUseCase {

    /**
     * Checks the current state of the game after a move.
     * The game can be in one of the following states:
     * - [GameState.XWins]: If player X has won.
     * - [GameState.OWins]: If player O has won.
     * - [GameState.Draw]: If the grid is full and there is no winner.
     * - [GameState.InProgress]: If the game is still ongoing.
     *
     * @param grid The current [Grid] value.
     * @param move The last [Move] played.
     *
     * @return The current [GameState].
     */
    operator fun invoke(grid: Grid, move: Move): RequestResult.Success<GameState>
}

class CheckGameStateUseCaseImpl : CheckGameStateUseCase {
    override fun invoke(grid: Grid, move: Move) = RequestResult.Success(
        if (isWinning(grid, move)) {
            // If any winning condition is met
            when (move.symbol) {
                Symbol.X -> GameState.XWins(grid)
                Symbol.O -> GameState.OWins(grid)
            }
        } else if (isDraw(grid)) {
            // If the grid is full and no winner, it's a draw
            GameState.Draw(grid)
        } else {
            // Otherwise, game is still in progress
            GameState.InProgress(grid)
        },
    )

    /**
     * Checks if the last move resulted in a win either by completing a row, column, or diagonal.
     */
    private fun isWinning(grid: Grid, move: Move): Boolean = checkRow(grid, move) ||
        checkColumn(grid, move) ||
        checkMainDiagonal(grid, move) ||
        checkAntiDiagonal(grid, move)

    /**
     * Checks if the move completed its row with the same symbol.
     */
    private fun checkRow(grid: Grid, move: Move): Boolean = grid[move.row].all { it == move.symbol }

    /**
     * Checks if the move completed its column with the same symbol.
     */
    private fun checkColumn(grid: Grid, move: Move): Boolean = grid.all { it[move.col] == move.symbol }

    /**
     * Checks if the move completed the main diagonal with the same symbol.
     */
    private fun checkMainDiagonal(grid: Grid, move: Move): Boolean {
        val n = grid.size
        return move.isOnMainDiagonal() && (0 until n).all { grid[it][it] == move.symbol }
    }

    /**
     * Checks if the move completed the anti-diagonal with the same symbol.
     */
    private fun checkAntiDiagonal(grid: Grid, move: Move): Boolean {
        val n = grid.size
        return move.isOnAntiDiagonal(n) && (0 until n).all { grid[it][n - 1 - it] == move.symbol }
    }

    /**
     * Checks if the move is on the main diagonal.
     */
    private fun Move.isOnMainDiagonal(): Boolean = row == col

    /**
     * Checks if the move is on the anti-diagonal.
     */
    private fun Move.isOnAntiDiagonal(gridSize: Int): Boolean = row + col == gridSize - 1

    /**
     * Checks if the grid is full (no null cells).
     */
    private fun isDraw(grid: Grid): Boolean = grid.none { row -> row.any { it == null } }

}
