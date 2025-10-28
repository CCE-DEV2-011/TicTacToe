package com.example.tictactoe.features.di

import com.example.tictactoe.features.board.BoardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featuresModule = module {
    viewModelOf(::BoardViewModel)
}
