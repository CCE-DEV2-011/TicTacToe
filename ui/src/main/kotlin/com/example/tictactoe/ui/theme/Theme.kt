package com.example.tictactoe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.White

private val LightColorScheme = lightColorScheme(
    primary = White,
    secondary = Teal700,
    tertiary = Red700,
    background = LightGreen,
)

@Composable
fun TicTacToeTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
