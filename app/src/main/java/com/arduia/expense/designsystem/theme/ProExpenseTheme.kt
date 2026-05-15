package com.arduia.expense.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalProExpenseColors = staticCompositionLocalOf { LightProExpenseColors }

object ProExpenseTheme {
    val colors: ProExpenseColors
        @Composable @ReadOnlyComposable
        get() = LocalProExpenseColors.current

    val spacing: Spacing
        @Composable @ReadOnlyComposable
        get() = LocalSpacing.current
}

@Composable
fun ProExpenseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val proExpenseColors = if (darkTheme) DarkProExpenseColors else LightProExpenseColors

    CompositionLocalProvider(
        LocalProExpenseColors provides proExpenseColors,
        LocalSpacing provides Spacing(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ProExpenseTypography,
            shapes = ProExpenseShapes,
            content = content,
        )
    }
}
