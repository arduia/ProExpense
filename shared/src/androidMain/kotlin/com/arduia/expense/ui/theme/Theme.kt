package com.arduia.expense.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val ProExpenseLightColorScheme = lightColorScheme(
    primary = ProExpensePalette.Primary,
    onPrimary = ProExpensePalette.OnPrimary,
    primaryContainer = ProExpensePalette.PrimaryTint,
    onPrimaryContainer = ProExpensePalette.PrimaryDeep,
    secondary = ProExpensePalette.PrimaryDeep,
    onSecondary = ProExpensePalette.OnPrimary,
    tertiary = ProExpensePalette.Tag,
    onTertiary = ProExpensePalette.OnPrimary,
    background = ProExpensePalette.Paper,
    onBackground = ProExpensePalette.Ink,
    surface = ProExpensePalette.Surface,
    onSurface = ProExpensePalette.Ink,
    surfaceVariant = ProExpensePalette.PaperAlt,
    onSurfaceVariant = ProExpensePalette.Ink2,
    outline = ProExpensePalette.LineStrong,
    outlineVariant = ProExpensePalette.LineSoft,
    error = ProExpensePalette.Danger,
    onError = ProExpensePalette.OnPrimary,
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

    val dimensions: ProExpenseDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalProExpenseDimensions.current
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
        LocalProExpenseDimensions provides ProExpenseDimensions(),
    ) {
        MaterialTheme(
            colorScheme = ProExpenseLightColorScheme,
            typography = ProExpenseMaterialTypography,
            shapes = ProExpenseMaterialShapes,
            content = content,
        )
    }
}
