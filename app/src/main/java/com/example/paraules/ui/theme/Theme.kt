package com.example.paraules.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ParaulesColorScheme = lightColorScheme(
    primary = ActionGreen,
    onPrimary = Color.White,
    secondary = ActionYellow,
    onSecondary = TitleGreen,
    tertiary = ActionCoral,
    onTertiary = TitleGreen,
    background = ScreenBackground,
    onBackground = TitleGreen,
    surface = PanelGreen,
    onSurface = Leaf
)

@Composable
fun ParaulesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ParaulesColorScheme,
        typography = AppTypography,
        content = content
    )
}