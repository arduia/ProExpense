package com.arduia.expense.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val ProExpenseLightColorScheme = lightColorScheme(
    primary = ProExpensePalette.Blue500,
    onPrimary = ProExpensePalette.PaperWarm,
    primaryContainer = ProExpensePalette.Blue100,
    onPrimaryContainer = ProExpensePalette.Blue700,
    secondary = ProExpensePalette.Blue700,
    onSecondary = ProExpensePalette.PaperWarm,
    tertiary = ProExpensePalette.Tag,
    onTertiary = ProExpensePalette.PaperWarm,
    background = ProExpensePalette.Paper,
    onBackground = ProExpensePalette.Ink,
    surface = ProExpensePalette.Card,
    onSurface = ProExpensePalette.Ink,
    surfaceVariant = ProExpensePalette.Gray200,
    onSurfaceVariant = ProExpensePalette.Ink2,
    outline = ProExpensePalette.LineStrong,
    outlineVariant = ProExpensePalette.Line2,
    error = ProExpensePalette.Red400,
    onError = ProExpensePalette.PaperWarm,
)

object ProExpenseTheme {
    val colors: ProExpenseColors
        @Composable
        @ReadOnlyComposable
        get() = LocalProExpenseColors.current

    val typography: ProExpenseTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalProExpenseTypography.current

    val shapes: ProExpenseShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalProExpenseShapes.current

    val motion: ProExpenseMotion
        @Composable
        @ReadOnlyComposable
        get() = LocalProExpenseMotion.current
}

@Composable
fun ProExpenseTheme(
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseLightColors
    val typography = ProExpenseTypography()
    val shapes = ProExpenseShapes()
    val motion = ProExpenseMotion()

    CompositionLocalProvider(
        LocalProExpenseColors provides colors,
        LocalProExpenseTypography provides typography,
        LocalProExpenseShapes provides shapes,
        LocalProExpenseMotion provides motion,
    ) {
        MaterialTheme(
            colorScheme = ProExpenseLightColorScheme,
            typography = ProExpenseMaterialTypography,
            shapes = ProExpenseMaterialShapes,
            content = content,
        )
    }
}
