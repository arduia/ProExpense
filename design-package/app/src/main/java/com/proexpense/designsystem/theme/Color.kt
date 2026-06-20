package com.proexpense.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/* ─────────────────────────────────────────────────────────────────────────
 * Pro Expense — raw palette (mirrors proto-brand.css)
 * ───────────────────────────────────────────────────────────────────────── */

// Primary — Material blue
val Blue100 = Color(0xFFB3E5FC)
val Blue200 = Color(0xFF81D4FA)
val Blue300 = Color(0xFF4FC3F7)
val Blue500 = Color(0xFF039BE5)
val Blue700 = Color(0xFF0288D1)

// Success — green
val Green300 = Color(0xFF81C784)
val Green400 = Color(0xFF66BB6A)
val Green500 = Color(0xFF4CAF50)
val GreenTint = Color(0xFFC8E6C9)

// Highlight — yellow
val Yellow300 = Color(0xFFFFF176)
val Yellow500 = Color(0xFFFFEB3B)

// Destructive — red
val Red300 = Color(0xFFE57373)
val Red400 = Color(0xFFEF5350)
val RedTint = Color(0xFFFFCDD2)

// Tag accent — orange
val Tag = Color(0xFFFB8C00)
val TagDeep = Color(0xFFEF6C00)
val TagSoft = Color(0xFFFFB74D)
val TagTint = Color(0xFFFFE0B2)

// Neutrals
val Gray100 = Color(0xFFF5F5F5)
val Gray200 = Color(0xFFEEEEEE)
val Gray300 = Color(0xFFE0E0E0)
val Gray500 = Color(0xFF9E9E9E)
val Gray600 = Color(0xFF757575)
val DarkGray = Color(0xFF212121)
val Ink2 = Color(0xFF424242)
val Muted2 = Color(0xFFBDBDBD)
val White = Color(0xFFFFFFFF)

/** Warm off-white used as on-color for filled buttons (never pure white). */
val OnFilled = Color(0xFFFFFDF6)

// Lines (low-alpha ink)
val Line = DarkGray.copy(alpha = 0.10f)
val Line2 = DarkGray.copy(alpha = 0.06f)

// Scrim for modal sheets
val Scrim = Color(0xFF2B1F17).copy(alpha = 0.42f)

// Bottom-nav inactive (iOS secondary label)
val NavInactive = Color(0xFF8E8E93)

/* ─────────────────────────────────────────────────────────────────────────
 * Extended brand colors that have no Material 3 ColorScheme slot.
 * Access via LocalProColors.current (see Theme.kt).
 * ───────────────────────────────────────────────────────────────────────── */
@Immutable
data class ProColors(
    val success: Color = Green500,
    val successSoft: Color = Green300,
    val successTint: Color = GreenTint,
    val highlight: Color = Yellow500,
    val highlightSoft: Color = Yellow300,
    val tag: Color = Tag,
    val tagDeep: Color = TagDeep,
    val tagTint: Color = TagTint,
    val ink2: Color = Ink2,
    val ink3: Color = Gray600,
    val muted: Color = Gray500,
    val muted2: Color = Muted2,
    val line: Color = Line,
    val line2: Color = Line2,
    val lineStrong: Color = Gray300,
    val onFilled: Color = OnFilled,
    val navInactive: Color = NavInactive,
    val scrim: Color = Scrim,
)
