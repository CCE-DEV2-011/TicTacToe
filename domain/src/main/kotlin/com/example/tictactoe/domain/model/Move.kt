package com.example.tictactoe.domain.model

data class Move(
    val row: Int,
    val col: Int,
    val symbol: Symbol,
)
