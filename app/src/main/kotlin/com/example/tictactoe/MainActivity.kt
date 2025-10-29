package com.example.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tictactoe.di.allModules
import com.example.tictactoe.features.board.BoardScreen
import com.example.tictactoe.ui.theme.TicTacToeTheme
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicTacToeTheme {
                KoinApplication(
                    application = {
                        modules(allModules)
                        androidContext(androidContext = this@MainActivity)
                    },
                ) {
                    BoardScreen()
                }
            }
        }
    }
}
