package com.arduia.expense.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = Green500,
    onPrimary = White,
    primaryContainer = Green700,
    secondary = Teal200,
    background = Gray50,
    surface = White,
    onSurface = Gray900,
    onBackground = Gray900,
    error = Red400,
    onError = White,
)

val DarkColorScheme = darkColorScheme(
    primary = Green500,
    onPrimary = Black,
    primaryContainer = Green700,
    secondary = Teal200,
    background = Gray900,
    surface = Color(0xFF1E1E1E),
    onSurface = Gray50,
    onBackground = Gray50,
    error = Red400,
    onError = Black,
)

// Custom semantic colors not covered by Material3 slots
data class ProExpenseColors(
    val positive: androidx.compose.ui.graphics.Color,
    val negative: androidx.compose.ui.graphics.Color,
    val warning: androidx.compose.ui.graphics.Color,
    val divider: androidx.compose.ui.graphics.Color,
    val icon: androidx.compose.ui.graphics.Color,
)

val LightProExpenseColors = ProExpenseColors(
    positive = ColorPositive,
    negative = ColorNegative,
    warning = ColorWarning,
    divider = Gray200,
    icon = Gray700,
)

val DarkProExpenseColors = ProExpenseColors(
    positive = ColorPositive,
    negative = ColorNegative,
    warning = ColorWarning,
    divider = Gray700,
    icon = Gray200,
)
