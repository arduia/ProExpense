package com.arduia.expense.ui.theme

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
import com.arduia.expense.shared.R

@OptIn(ExperimentalTextApi::class)
private fun variableFont(resId: Int, weight: FontWeight): Font = Font(
    resId = resId,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

private val ManropeFamily = FontFamily(
    variableFont(R.font.manrope_variable, FontWeight.Light),
    variableFont(R.font.manrope_variable, FontWeight.Normal),
    variableFont(R.font.manrope_variable, FontWeight.Medium),
    variableFont(R.font.manrope_variable, FontWeight.SemiBold),
    variableFont(R.font.manrope_variable, FontWeight.Bold),
    variableFont(R.font.manrope_variable, FontWeight.ExtraBold),
)

private val GeistMonoFamily = FontFamily(
    variableFont(R.font.geist_mono_variable, FontWeight.Normal),
    variableFont(R.font.geist_mono_variable, FontWeight.Medium),
    variableFont(R.font.geist_mono_variable, FontWeight.SemiBold),
)

private val InstrumentSerifFamily = FontFamily(
    Font(R.font.instrument_serif_regular, FontWeight.Normal),
    Font(R.font.instrument_serif_italic, FontWeight.Normal, FontStyle.Italic),
)

@Immutable
data class ProTypography(
    val displayAmount: TextStyle,
    val summaryAmount: TextStyle,
    val listAmount: TextStyle,
    val screenTitle: TextStyle,
    val sectionHead: TextStyle,
    val displayFlourish: TextStyle,
    val body: TextStyle,
    val bodyMedium: TextStyle,
    val bodySemiBold: TextStyle,
    val caption: TextStyle,
    val eyebrow: TextStyle,
    val button: TextStyle,
    val tabTimestamp: TextStyle,
    val keypadKey: TextStyle,
    val navLabel: TextStyle,
    val searchField: TextStyle,
    val sansFamily: FontFamily,
    val serifFamily: FontFamily,
    val monoFamily: FontFamily,
)

val LocalProTypography = staticCompositionLocalOf { ProDefaultTypography }

internal fun proLineHeight(fontSizeSp: Float, multiplier: Float) =
    (fontSizeSp * multiplier).sp

val ProDefaultTypography = ProTypography(
    displayAmount = TextStyle(
        fontFamily = InstrumentSerifFamily,
        fontSize = 64.sp,
        lineHeight = proLineHeight(64f, 1f),
        letterSpacing = (-0.025).em,
        fontWeight = FontWeight.Normal,
    ),
    summaryAmount = TextStyle(
        fontFamily = InstrumentSerifFamily,
        fontSize = 40.sp,
        lineHeight = proLineHeight(40f, 1f),
        letterSpacing = (-0.02).em,
        fontWeight = FontWeight.Normal,
    ),
    listAmount = TextStyle(
        fontFamily = InstrumentSerifFamily,
        fontSize = 18.sp,
        lineHeight = proLineHeight(18f, 1f),
        letterSpacing = (-0.01).em,
        fontWeight = FontWeight.Normal,
    ),
    screenTitle = TextStyle(
        fontFamily = InstrumentSerifFamily,
        fontSize = 32.sp,
        lineHeight = proLineHeight(32f, 1f),
        letterSpacing = (-0.015).em,
        fontWeight = FontWeight.Normal,
    ),
    sectionHead = TextStyle(
        fontFamily = InstrumentSerifFamily,
        fontSize = 18.sp,
        lineHeight = proLineHeight(18f, 1.1f),
        letterSpacing = (-0.01).em,
        fontWeight = FontWeight.Normal,
    ),
    displayFlourish = TextStyle(
        fontFamily = InstrumentSerifFamily,
        fontSize = 32.sp,
        lineHeight = proLineHeight(32f, 1f),
        letterSpacing = (-0.015).em,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
    ),
    body = TextStyle(
        fontFamily = ManropeFamily,
        fontSize = 14.sp,
        lineHeight = proLineHeight(14f, 1.4f),
        fontWeight = FontWeight.Normal,
    ),
    bodyMedium = TextStyle(
        fontFamily = ManropeFamily,
        fontSize = 14.sp,
        lineHeight = proLineHeight(14f, 1.4f),
        fontWeight = FontWeight.Medium,
    ),
    bodySemiBold = TextStyle(
        fontFamily = ManropeFamily,
        fontSize = 14.sp,
        lineHeight = proLineHeight(14f, 1.4f),
        fontWeight = FontWeight.SemiBold,
    ),
    caption = TextStyle(
        fontFamily = ManropeFamily,
        fontSize = 11.5.sp,
        lineHeight = proLineHeight(11.5f, 1.4f),
        fontWeight = FontWeight.Normal,
    ),
    eyebrow = TextStyle(
        fontFamily = GeistMonoFamily,
        fontSize = 11.sp,
        lineHeight = proLineHeight(11f, 1.3f),
        letterSpacing = 0.10.em,
        fontWeight = FontWeight.Medium,
    ),
    button = TextStyle(
        fontFamily = ManropeFamily,
        fontSize = 14.sp,
        lineHeight = proLineHeight(14f, 1.4f),
        letterSpacing = (-0.005).em,
        fontWeight = FontWeight.SemiBold,
    ),
    tabTimestamp = TextStyle(
        fontFamily = GeistMonoFamily,
        fontSize = 12.sp,
        lineHeight = proLineHeight(12f, 1.3f),
        letterSpacing = 0.04.em,
        fontWeight = FontWeight.Normal,
        fontFeatureSettings = "tnum",
    ),
    keypadKey = TextStyle(
        fontFamily = InstrumentSerifFamily,
        fontSize = 22.sp,
        lineHeight = proLineHeight(22f, 1f),
        fontWeight = FontWeight.Normal,
    ),
    navLabel = TextStyle(
        fontFamily = ManropeFamily,
        fontSize = 10.sp,
        lineHeight = proLineHeight(10f, 1.2f),
        fontWeight = FontWeight.Medium,
    ),
    searchField = TextStyle(
        fontFamily = ManropeFamily,
        fontSize = 13.sp,
        lineHeight = proLineHeight(13f, 1.4f),
        fontWeight = FontWeight.Normal,
    ),
    sansFamily = ManropeFamily,
    serifFamily = InstrumentSerifFamily,
    monoFamily = GeistMonoFamily,
)
