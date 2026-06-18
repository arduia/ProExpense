package com.arduia.expense.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Color tokens — mirrors pro-expense-finance-tracker/project/DESIGN-SYSTEM.md §1.
// A cool Material-blue core, three signal hues (green / yellow / red), a single
// warm orange reserved for event tags, and a calibrated neutral ramp.
object ProExpensePalette {
    // Primary · Material blue
    val Blue100 = Color(0xFFB3E5FC)
    val Blue200 = Color(0xFF81D4FA)
    val Blue300 = Color(0xFF4FC3F7)
    val Blue500 = Color(0xFF039BE5)
    val Blue700 = Color(0xFF0288D1)
    val Blue50 = Color(0xFFE1F5FE)

    // Success · Green
    val Green300 = Color(0xFF81C784)
    val Green400 = Color(0xFF66BB6A)
    val Green500 = Color(0xFF4CAF50)

    // Highlight · Yellow
    val Yellow300 = Color(0xFFFFF176)
    val Yellow500 = Color(0xFFFFEB3B)

    // Destructive · Red
    val Red300 = Color(0xFFE57373)
    val Red400 = Color(0xFFEF5350)

    // Tag accent · Orange
    val TagTint = Color(0xFFFFE0B2)
    val TagSoft = Color(0xFFFFB74D)
    val Tag = Color(0xFFFB8C00)
    val TagDeep = Color(0xFFEF6C00)

    // Neutrals
    val Gray100 = Color(0xFFF5F5F5)
    val Gray200 = Color(0xFFEEEEEE)
    val Gray300 = Color(0xFFE0E0E0)
    val Gray500 = Color(0xFF9E9E9E)
    val Gray600 = Color(0xFF757575)
    val DarkGray = Color(0xFF212121)
    val Black = Color(0xFF000000)

    // Semantic neutrals
    val Card = Color(0xFFFFFFFF)
    val Paper = Color(0xFFF5F5F5)
    val PaperWarm = Color(0xFFFFFDF6)
    val Ink = Color(0xFF212121)
    val Ink2 = Color(0xFF424242)
    val Ink3 = Color(0xFF757575)
    val Muted = Color(0xFF9E9E9E)
    val Muted2 = Color(0xFFBDBDBD)
    val Line = Color(0x1A212121)
    val Line2 = Color(0x0F212121)
    val LineStrong = Color(0xFFE0E0E0)
    val Scrim = Color(0x6B2B1F17)
    val SheetHandle = Color(0x2E2B1F17)

    // Material (M3) chrome tokens — used by the onboarding / profile flow
    // (android-frame.jsx · MD palette).
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFEEF1F4)
    val SurfaceDim = Color(0xFFF3F4F6)
    val Outline = Color(0xFFC2C7CE)
    val OutlineVariant = Color(0xFFDFE3E8)
    val OnSurface = Color(0xFF1A1C1E)
    val OnSurfaceVariant = Color(0xFF5A5F66)
}

@Immutable
data class ProExpenseColors(
    val primary: Color = ProExpensePalette.Blue500,
    val primaryDeep: Color = ProExpensePalette.Blue700,
    val primarySoft: Color = ProExpensePalette.Blue300,
    val primaryTint: Color = ProExpensePalette.Blue100,
    val primaryContainer: Color = ProExpensePalette.Blue100,
    val success: Color = ProExpensePalette.Green500,
    val successSoft: Color = ProExpensePalette.Green400,
    val highlight: Color = ProExpensePalette.Yellow500,
    val highlightSoft: Color = ProExpensePalette.Yellow300,
    val danger: Color = ProExpensePalette.Red400,
    val dangerSoft: Color = ProExpensePalette.Red300,
    val tag: Color = ProExpensePalette.Tag,
    val tagSoft: Color = ProExpensePalette.TagSoft,
    val tagTint: Color = ProExpensePalette.TagTint,
    val tagDeep: Color = ProExpensePalette.TagDeep,
    val card: Color = ProExpensePalette.Card,
    val paper: Color = ProExpensePalette.Paper,
    val paperWarm: Color = ProExpensePalette.PaperWarm,
    val ink: Color = ProExpensePalette.Ink,
    val ink2: Color = ProExpensePalette.Ink2,
    val ink3: Color = ProExpensePalette.Ink3,
    val muted: Color = ProExpensePalette.Muted,
    val muted2: Color = ProExpensePalette.Muted2,
    val line: Color = ProExpensePalette.Line,
    val line2: Color = ProExpensePalette.Line2,
    val lineStrong: Color = ProExpensePalette.LineStrong,
    val navBackground: Color = ProExpensePalette.Card,
    val navText: Color = ProExpensePalette.DarkGray,
    val scrim: Color = ProExpensePalette.Scrim,
    val sheetHandle: Color = ProExpensePalette.SheetHandle,
    // Material chrome
    val surface: Color = ProExpensePalette.Surface,
    val surfaceVariant: Color = ProExpensePalette.SurfaceVariant,
    val surfaceDim: Color = ProExpensePalette.SurfaceDim,
    val outline: Color = ProExpensePalette.Outline,
    val outlineVariant: Color = ProExpensePalette.OutlineVariant,
    val onSurface: Color = ProExpensePalette.OnSurface,
    val onSurfaceVariant: Color = ProExpensePalette.OnSurfaceVariant,
    val onPrimary: Color = Color(0xFFFFFFFF),
)

val LocalProExpenseColors = staticCompositionLocalOf { ProExpenseColors() }

val ProExpenseLightColors = ProExpenseColors()
