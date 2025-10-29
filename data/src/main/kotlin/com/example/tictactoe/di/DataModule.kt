package com.example.tictactoe.di

import com.example.tictactoe.data.GridRepositoryImpl
import com.example.tictactoe.domain.repository.GridRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::GridRepositoryImpl) { bind<GridRepository>() }
}
