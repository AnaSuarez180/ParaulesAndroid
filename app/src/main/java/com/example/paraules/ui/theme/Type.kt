package com.example.paraules.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.paraules.R

val AccessibilityFont = FontFamily(
    Font(R.font.atkinson_hyperlegible_regular, FontWeight.Normal),
    Font(R.font.atkinson_hyperlegible_bold, FontWeight.Bold)
)

private val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = AccessibilityFont),
    displayMedium = baseline.displayMedium.copy(fontFamily = AccessibilityFont),
    displaySmall = baseline.displaySmall.copy(fontFamily = AccessibilityFont),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = AccessibilityFont),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = AccessibilityFont),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = AccessibilityFont),
    titleLarge = baseline.titleLarge.copy(fontFamily = AccessibilityFont),
    titleMedium = baseline.titleMedium.copy(fontFamily = AccessibilityFont),
    titleSmall = baseline.titleSmall.copy(fontFamily = AccessibilityFont),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = AccessibilityFont),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = AccessibilityFont),
    bodySmall = baseline.bodySmall.copy(fontFamily = AccessibilityFont),
    labelLarge = baseline.labelLarge.copy(fontFamily = AccessibilityFont),
    labelMedium = baseline.labelMedium.copy(fontFamily = AccessibilityFont),
    labelSmall = baseline.labelSmall.copy(fontFamily = AccessibilityFont)
)