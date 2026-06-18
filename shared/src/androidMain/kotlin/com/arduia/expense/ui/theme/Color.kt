package com.arduia.expense.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Semantic color tokens — design_handoff_v2 / design-tokens.json → color.*
object ProExpensePalette {
    val Primary = Color(0xFF039BE5)
    val PrimaryDeep = Color(0xFF0288D1)
    val PrimarySoft = Color(0xFF4FC3F7)
    val PrimaryTint = Color(0xFFB3E5FC)
    val OnPrimary = Color(0xFFFFFFFF)

    val Success = Color(0xFF4CAF50)
    val SuccessSoft = Color(0xFF81C784)
    val SuccessTint = Color(0xFFC8E6C9)

    val Highlight = Color(0xFFFFEB3B)
    val HighlightSoft = Color(0xFFFFF176)

    val Danger = Color(0xFFEF5350)
    val DangerSoft = Color(0xFFE57373)
    val DangerTint = Color(0xFFFFCDD2)

    val Tag = Color(0xFFFB8C00)
    val TagDeep = Color(0xFFEF6C00)
    val TagSoft = Color(0xFFFFB74D)
    val TagTint = Color(0xFFFFE0B2)

    val Surface = Color(0xFFFFFFFF)
    val Paper = Color(0xFFF5F5F5)
    val PaperAlt = Color(0xFFEEEEEE)
    val PaperWarm = Color(0xFFFFFDF6)

    val Ink = Color(0xFF212121)
    val Ink2 = Color(0xFF424242)
    val Ink3 = Color(0xFF757575)
    val Muted = Color(0xFF9E9E9E)
    val Muted2 = Color(0xFFBDBDBD)
    val NavInactive = Color(0xFF8E8E93)

    val Line = Color(0x19212121)
    val LineSoft = Color(0x0F212121)
    val LineStrong = Color(0xFFE0E0E0)
    val Scrim = Color(0x6B2B1F17)

    val FoodAccent = Color(0xFF039BE5)
    val FoodTint = Color(0xFFE1F5FE)
    val TransportAccent = Color(0xFF0288D1)
    val TransportTint = Color(0xFFB3E5FC)
    val ShoppingAccent = Color(0xFFEF5350)
    val ShoppingTint = Color(0xFFFFCDD2)
    val BillsAccent = Color(0xFF757575)
    val BillsTint = Color(0xFFEEEEEE)
    val HealthAccent = Color(0xFF4CAF50)
    val HealthTint = Color(0xFFC8E6C9)
    val EntertainmentAccent = Color(0xFF0277BD)
    val EntertainmentTint = Color(0xFF81D4FA)
}

@Immutable
data class ProExpenseColors(
    val primary: Color = ProExpensePalette.Primary,
    val primaryDeep: Color = ProExpensePalette.PrimaryDeep,
    val primarySoft: Color = ProExpensePalette.PrimarySoft,
    val primaryTint: Color = ProExpensePalette.PrimaryTint,
    val onPrimary: Color = ProExpensePalette.OnPrimary,
    val success: Color = ProExpensePalette.Success,
    val successSoft: Color = ProExpensePalette.SuccessSoft,
    val successTint: Color = ProExpensePalette.SuccessTint,
    val highlight: Color = ProExpensePalette.Highlight,
    val highlightSoft: Color = ProExpensePalette.HighlightSoft,
    val danger: Color = ProExpensePalette.Danger,
    val dangerSoft: Color = ProExpensePalette.DangerSoft,
    val dangerTint: Color = ProExpensePalette.DangerTint,
    val tag: Color = ProExpensePalette.Tag,
    val tagSoft: Color = ProExpensePalette.TagSoft,
    val tagTint: Color = ProExpensePalette.TagTint,
    val tagDeep: Color = ProExpensePalette.TagDeep,
    val surface: Color = ProExpensePalette.Surface,
    val paper: Color = ProExpensePalette.Paper,
    val paperAlt: Color = ProExpensePalette.PaperAlt,
    val paperWarm: Color = ProExpensePalette.PaperWarm,
    val ink: Color = ProExpensePalette.Ink,
    val ink2: Color = ProExpensePalette.Ink2,
    val ink3: Color = ProExpensePalette.Ink3,
    val muted: Color = ProExpensePalette.Muted,
    val muted2: Color = ProExpensePalette.Muted2,
    val line: Color = ProExpensePalette.Line,
    val line2: Color = ProExpensePalette.LineSoft,
    val lineStrong: Color = ProExpensePalette.LineStrong,
    val scrim: Color = ProExpensePalette.Scrim,
    val onSurface: Color = ProExpensePalette.Ink,
    val onSurfaceVariant: Color = ProExpensePalette.Ink2,
    val onSurfaceMuted: Color = ProExpensePalette.Ink3,
    // Backward-compatible aliases used by home / quick-log screens
    val card: Color = ProExpensePalette.Surface,
    val primaryContainer: Color = ProExpensePalette.PrimaryTint,
    val surfaceVariant: Color = ProExpensePalette.PaperAlt,
    val outline: Color = ProExpensePalette.LineStrong,
    val outlineVariant: Color = ProExpensePalette.LineStrong,
    val navBackground: Color = ProExpensePalette.Surface,
    val navText: Color = ProExpensePalette.Ink,
    val navInactive: Color = ProExpensePalette.NavInactive,
)

val LocalProExpenseColors = staticCompositionLocalOf { ProExpenseColors() }

val ProExpenseLightColors = ProExpenseColors()
