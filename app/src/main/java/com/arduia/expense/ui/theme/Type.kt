package com.arduia.expense.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.arduia.expense.R

// Font binaries and stacks: design/FONTS.md · source files: design/fonts/
// The design's `--sans` (body, buttons, list rows) is the iOS system font (SF Pro). SF Pro can't
// ship on Android, so the sans role uses Inter — an OFL-licensed SF-Pro-like humanist UI face.
// Inter and Geist Mono ship as single variable-font binaries, so every weight is registered against
// the one resource with an explicit `wght` axis (applied on API 26+). Instrument Serif is a static
// face used for display only; the system handles serif glyph fallback.

@OptIn(ExperimentalTextApi::class)
private fun variableFont(resId: Int, weight: FontWeight): Font = Font(
    resId = resId,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

val ProExpenseSerif = FontFamily(
    Font(R.font.instrument_serif_regular, FontWeight.Normal),
    Font(R.font.instrument_serif_italic, FontWeight.Normal, FontStyle.Italic),
)

val ProExpenseSans = FontFamily(
    variableFont(R.font.inter_regular, FontWeight.Light),
    variableFont(R.font.inter_regular, FontWeight.Normal),
    variableFont(R.font.inter_regular, FontWeight.Medium),
    variableFont(R.font.inter_regular, FontWeight.SemiBold),
    variableFont(R.font.inter_regular, FontWeight.Bold),
    variableFont(R.font.inter_regular, FontWeight.ExtraBold),
)

val ProExpenseMono = FontFamily(
    variableFont(R.font.geist_mono_regular, FontWeight.Normal),
    variableFont(R.font.geist_mono_regular, FontWeight.Medium),
    variableFont(R.font.geist_mono_regular, FontWeight.SemiBold),
)

@Immutable
data class ProExpenseTypography(
    val displayAmount: TextStyle = TextStyle(
        fontFamily = ProExpenseSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 64.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.025).em,
    ),
    val screenTitle: TextStyle = TextStyle(
        fontFamily = ProExpenseSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.015).em,
    ),
    val sectionHead: TextStyle = TextStyle(
        fontFamily = ProExpenseSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 19.8.sp,
        letterSpacing = (-0.01).em,
    ),
    val rowAmount: TextStyle = TextStyle(
        fontFamily = ProExpenseSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 18.sp,
        letterSpacing = (-0.01).em,
    ),
    val body: TextStyle = TextStyle(
        fontFamily = ProExpenseSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 19.6.sp,
    ),
    val bodyEmphasis: TextStyle = TextStyle(
        fontFamily = ProExpenseSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 19.6.sp,
    ),
    val caption: TextStyle = TextStyle(
        fontFamily = ProExpenseSans,
        fontWeight = FontWeight.Normal,
        fontSize = 11.5.sp,
        lineHeight = 16.1.sp,
    ),
    val tagline: TextStyle = TextStyle(
        fontFamily = ProExpenseSans,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.85.sp,
    ),
    val eyebrow: TextStyle = TextStyle(
        fontFamily = ProExpenseMono,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.3.sp,
        letterSpacing = 0.1.em,
    ),
    val monoCaption: TextStyle = TextStyle(
        fontFamily = ProExpenseMono,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 15.6.sp,
        letterSpacing = 0.08.em,
    ),
    val buttonSmall: TextStyle = TextStyle(
        fontFamily = ProExpenseSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        letterSpacing = (-0.005).em,
    ),
    val buttonMedium: TextStyle = TextStyle(
        fontFamily = ProExpenseSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = (-0.005).em,
    ),
    val buttonLarge: TextStyle = TextStyle(
        fontFamily = ProExpenseSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 15.sp,
        letterSpacing = (-0.005).em,
    ),
    val onboardingTitle: TextStyle = TextStyle(
        fontFamily = ProExpenseSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 38.sp,
        lineHeight = 39.9.sp,
        letterSpacing = (-0.02).em,
    ),
    val onboardingBody: TextStyle = TextStyle(
        fontFamily = ProExpenseSans,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 21.75.sp,
    ),
    val onboardingNav: TextStyle = TextStyle(
        fontFamily = ProExpenseSans,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 13.sp,
    ),
    val onboardingNavEmphasis: TextStyle = TextStyle(
        fontFamily = ProExpenseSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 13.sp,
    ),
)

val LocalProExpenseTypography = staticCompositionLocalOf { ProExpenseTypography() }

val ProExpenseMaterialTypography = Typography(
    displayLarge = ProExpenseTypography().displayAmount,
    headlineLarge = ProExpenseTypography().screenTitle,
    titleLarge = ProExpenseTypography().sectionHead,
    bodyMedium = ProExpenseTypography().body,
    bodySmall = ProExpenseTypography().caption,
    labelSmall = ProExpenseTypography().eyebrow,
)
