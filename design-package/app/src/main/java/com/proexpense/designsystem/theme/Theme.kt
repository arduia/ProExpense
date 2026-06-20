package com.proexpense.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/* ─────────────────────────────────────────────────────────────────────────
 * ProExpenseTheme — single light theme.
 * Surfaces are pure white on warm-grey paper; NO M3 tonal elevation
 * (elevation is drawn as soft shadow). Extended brand colors live in
 * LocalProColors.
 * ───────────────────────────────────────────────────────────────────────── */

val LocalProColors = staticCompositionLocalOf { ProColors() }

private val ProLightColorScheme = lightColorScheme(
    primary = Blue500,
    onPrimary = OnFilled,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue700,
    secondary = Blue700,
    onSecondary = OnFilled,
    tertiary = Tag,
    onTertiary = OnFilled,
    tertiaryContainer = TagTint,
    onTertiaryContainer = TagDeep,
    background = Gray100,
    onBackground = DarkGray,
    surface = White,
    onSurface = DarkGray,
    surfaceVariant = Gray200,
    onSurfaceVariant = Gray600,
    outline = Gray300,
    outlineVariant = Line,
    error = Red400,
    onError = OnFilled,
    errorContainer = RedTint,
    scrim = Scrim,
)

object ProTheme {
    val colors: ProColors
        @Composable get() = LocalProColors.current
}

@Composable
fun ProExpenseTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalProColors provides ProColors()) {
        MaterialTheme(
            colorScheme = ProLightColorScheme,
            typography = ProTypography,
            shapes = ProShapes,
            content = content,
        )
    }
}
