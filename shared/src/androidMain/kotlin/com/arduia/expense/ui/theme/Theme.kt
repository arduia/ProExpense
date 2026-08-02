package com.arduia.expense.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun ProExpenseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: ProColors = if (darkTheme) ProDarkColors else ProLightColors,
    typography: ProTypography = ProDefaultTypography,
    dimensions: ProDimens = ProDefaultDimens,
    shapes: ProShapes = ProDefaultShapes,
    motion: ProMotion = ProDefaultMotion,
    elevation: ProElevation = proElevation(colors),
    content: @Composable () -> Unit,
) {
    val materialColors = colors.toMaterialColorScheme(darkTheme)
    CompositionLocalProvider(
        LocalProColors provides colors,
        LocalProTypography provides typography,
        LocalProDimens provides dimensions,
        LocalProShapes provides shapes,
        LocalProMotion provides motion,
        LocalProElevation provides elevation,
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = typography.toMaterialTypography(),
            content = content,
        )
    }
}

object ProExpenseTheme {
    val colors: ProColors
        @Composable
        @ReadOnlyComposable
        get() = LocalProColors.current

    val typography: ProTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalProTypography.current

    val dimensions: ProDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalProDimens.current

    val shapes: ProShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalProShapes.current

    val motion: ProMotion
        @Composable
        @ReadOnlyComposable
        get() = LocalProMotion.current

    val elevation: ProElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalProElevation.current
}

fun ProColors.toMaterialColorScheme(darkTheme: Boolean = false) =
    if (darkTheme) toMaterialDarkColorScheme() else toMaterialLightColorScheme()

private fun ProColors.toMaterialLightColorScheme() =
    lightColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryTint,
        onPrimaryContainer = primaryDeep,
        secondary = primaryDeep,
        onSecondary = onPrimary,
        secondaryContainer = primaryTint,
        onSecondaryContainer = onSurface,
        tertiary = tag,
        onTertiary = onPrimary,
        tertiaryContainer = tagTint,
        onTertiaryContainer = tagDeep,
        background = paper,
        onBackground = onSurface,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = paperAlt,
        onSurfaceVariant = onSurfaceVariant,
        error = danger,
        onError = onPrimary,
        errorContainer = dangerTint,
        onErrorContainer = danger,
        outline = lineStrong,
        outlineVariant = line,
        scrim = scrim,
    )

private fun ProColors.toMaterialDarkColorScheme() =
    darkColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryTint,
        onPrimaryContainer = primarySoft,
        secondary = primaryDeep,
        onSecondary = onPrimary,
        secondaryContainer = primaryTint,
        onSecondaryContainer = onSurface,
        tertiary = tag,
        onTertiary = onPrimary,
        tertiaryContainer = tagTint,
        onTertiaryContainer = tagSoft,
        background = paper,
        onBackground = onSurface,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = paperAlt,
        onSurfaceVariant = onSurfaceVariant,
        error = danger,
        onError = onPrimary,
        errorContainer = dangerTint,
        onErrorContainer = dangerSoft,
        outline = lineStrong,
        outlineVariant = line,
        scrim = scrim,
    )
