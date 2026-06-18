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

// Typography — mirrors pro-expense-finance-tracker/project/FONTS.md.
// Three families with clear jobs:
//   • Instrument Serif (--serif) — display only: screen titles, day headers, amounts.
//   • Manrope          (--sans)  — all UI: body, buttons, list rows. Weights 300–800.
//   • Geist Mono       (--mono)  — eyebrows, tab labels, timestamps, tabular figures.
// Manrope and Geist Mono ship as single variable-font binaries, so each weight is
// registered against the one resource with an explicit `wght` axis (applied on API 26+).
// Instrument Serif is a static display face (regular + italic).

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
    variableFont(R.font.manrope_variable, FontWeight.Light),
    variableFont(R.font.manrope_variable, FontWeight.Normal),
    variableFont(R.font.manrope_variable, FontWeight.Medium),
    variableFont(R.font.manrope_variable, FontWeight.SemiBold),
    variableFont(R.font.manrope_variable, FontWeight.Bold),
    variableFont(R.font.manrope_variable, FontWeight.ExtraBold),
)

val ProExpenseMono = FontFamily(
    variableFont(R.font.geist_mono_variable, FontWeight.Normal),
    variableFont(R.font.geist_mono_variable, FontWeight.Medium),
    variableFont(R.font.geist_mono_variable, FontWeight.SemiBold),
)

@Immutable
data class ProExpenseTypography(
    // Display · Instrument Serif
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
    // Body · Manrope
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
    // Metadata · Geist Mono
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
    // Buttons · Manrope 600
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
        fontSize = 15.5.sp,
        lineHeight = 15.5.sp,
        letterSpacing = 0.3.sp,
    ),
    // Onboarding (Material rendition) — sans headlines, not serif.
    val onboardingTitle: TextStyle = TextStyle(
        fontFamily = ProExpenseSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 32.2.sp,
    ),
    val onboardingBody: TextStyle = TextStyle(
        fontFamily = ProExpenseSans,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.5.sp,
    ),
    val topBarTitle: TextStyle = TextStyle(
        fontFamily = ProExpenseSans,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 26.sp,
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
