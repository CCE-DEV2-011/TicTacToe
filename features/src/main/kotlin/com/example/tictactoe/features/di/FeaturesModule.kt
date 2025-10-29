package com.example.tictactoe.features.di

import com.example.tictactoe.features.board.BoardViewModel
import com.example.tictactoe.features.provider.AndroidResourceProvider
import com.example.tictactoe.features.provider.ResourceProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featuresModule = module {
    viewModelOf(::BoardViewModel)
    single<ResourceProvider> { AndroidResourceProvider(androidContext()) }
}
