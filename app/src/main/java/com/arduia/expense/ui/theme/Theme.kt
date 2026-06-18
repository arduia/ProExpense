package com.arduia.expense.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val ProExpenseLightColorScheme = lightColorScheme(
    primary = ProExpensePalette.Blue500,
    onPrimary = ProExpensePalette.Card,
    primaryContainer = ProExpensePalette.Blue100,
    onPrimaryContainer = ProExpensePalette.Blue700,
    secondary = ProExpensePalette.Blue700,
    onSecondary = ProExpensePalette.Card,
    tertiary = ProExpensePalette.Tag,
    onTertiary = ProExpensePalette.Card,
    background = ProExpensePalette.Paper,
    onBackground = ProExpensePalette.Ink,
    surface = ProExpensePalette.Surface,
    onSurface = ProExpensePalette.OnSurface,
    surfaceVariant = ProExpensePalette.SurfaceVariant,
    onSurfaceVariant = ProExpensePalette.OnSurfaceVariant,
    outline = ProExpensePalette.Outline,
    outlineVariant = ProExpensePalette.OutlineVariant,
    error = ProExpensePalette.Red400,
    onError = ProExpensePalette.Card,
)

// Entry point for the design-system tokens. `ProExpenseTheme.colors`, `.typography`,
// `.shapes`, and `.motion` read the active values from the composition.
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
    CompositionLocalProvider(
        LocalProExpenseColors provides ProExpenseLightColors,
        LocalProExpenseTypography provides ProExpenseTypography(),
        LocalProExpenseShapes provides ProExpenseShapes(),
        LocalProExpenseMotion provides ProExpenseMotion(),
    ) {
        MaterialTheme(
            colorScheme = ProExpenseLightColorScheme,
            typography = ProExpenseMaterialTypography,
            shapes = ProExpenseMaterialShapes,
            content = content,
        )
    }
}
