package com.example.tictactoe.domain.model

enum class Symbol {
    X,
    O,
    ;

    fun toggle(): Symbol = when (this) {
        X -> O
        O -> X
    }
}
