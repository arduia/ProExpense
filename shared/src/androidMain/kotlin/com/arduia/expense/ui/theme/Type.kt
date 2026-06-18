package com.arduia.expense.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.arduia.expense.shared.R

private val ManropeFamily = FontFamily(
    Font(R.font.manrope_variable, FontWeight.Light),
    Font(R.font.manrope_variable, FontWeight.Normal),
    Font(R.font.manrope_variable, FontWeight.Medium),
    Font(R.font.manrope_variable, FontWeight.SemiBold),
    Font(R.font.manrope_variable, FontWeight.Bold),
    Font(R.font.manrope_variable, FontWeight.ExtraBold),
)

private val InstrumentSerifFamily = FontFamily(
    Font(R.font.instrument_serif_regular, FontWeight.Normal),
    Font(R.font.instrument_serif_italic, FontWeight.Normal, FontStyle.Italic),
)

private val GeistMonoFamily = FontFamily(
    Font(R.font.geist_mono_variable, FontWeight.Normal),
    Font(R.font.geist_mono_variable, FontWeight.Medium),
    Font(R.font.geist_mono_variable, FontWeight.SemiBold),
)

@Immutable
data class ProTypography(
    val displayAmount: TextStyle,
    val screenTitle: TextStyle,
    val sectionHead: TextStyle,
    val body: TextStyle,
    val bodyMedium: TextStyle,
    val bodySemiBold: TextStyle,
    val caption: TextStyle,
    val eyebrow: TextStyle,
    val button: TextStyle,
    val tabTimestamp: TextStyle,
    val keypadKey: TextStyle,
    val sansFamily: FontFamily,
    val serifFamily: FontFamily,
    val monoFamily: FontFamily,
)

val LocalProTypography = staticCompositionLocalOf { ProDefaultTypography }

private fun lineHeight(fontSizeSp: Float, multiplier: Float) =
    (fontSizeSp * multiplier).sp

val ProDefaultTypography = ProTypography(
    displayAmount = TextStyle(
        fontFamily = InstrumentSerifFamily,
        fontSize = 64.sp,
        lineHeight = lineHeight(64f, 1f),
        letterSpacing = (-0.025).em,
        fontWeight = FontWeight.Normal,
    ),
    screenTitle = TextStyle(
        fontFamily = InstrumentSerifFamily,
        fontSize = 32.sp,
        lineHeight = lineHeight(32f, 1f),
        letterSpacing = (-0.015).em,
        fontWeight = FontWeight.Normal,
    ),
    sectionHead = TextStyle(
        fontFamily = InstrumentSerifFamily,
        fontSize = 18.sp,
        lineHeight = lineHeight(18f, 1.1f),
        letterSpacing = (-0.01).em,
        fontWeight = FontWeight.Normal,
    ),
    body = TextStyle(
        fontFamily = ManropeFamily,
        fontSize = 14.sp,
        lineHeight = lineHeight(14f, 1.4f),
        fontWeight = FontWeight.Normal,
    ),
    bodyMedium = TextStyle(
        fontFamily = ManropeFamily,
        fontSize = 14.sp,
        lineHeight = lineHeight(14f, 1.4f),
        fontWeight = FontWeight.Medium,
    ),
    bodySemiBold = TextStyle(
        fontFamily = ManropeFamily,
        fontSize = 14.sp,
        lineHeight = lineHeight(14f, 1.4f),
        fontWeight = FontWeight.SemiBold,
    ),
    caption = TextStyle(
        fontFamily = ManropeFamily,
        fontSize = 11.5.sp,
        lineHeight = lineHeight(11.5f, 1.4f),
        fontWeight = FontWeight.Normal,
    ),
    eyebrow = TextStyle(
        fontFamily = GeistMonoFamily,
        fontSize = 11.sp,
        lineHeight = lineHeight(11f, 1.3f),
        letterSpacing = 0.10.em,
        fontWeight = FontWeight.Medium,
    ),
    button = TextStyle(
        fontFamily = ManropeFamily,
        fontSize = 14.sp,
        lineHeight = lineHeight(14f, 1.4f),
        letterSpacing = (-0.005).em,
        fontWeight = FontWeight.SemiBold,
    ),
    tabTimestamp = TextStyle(
        fontFamily = GeistMonoFamily,
        fontSize = 12.sp,
        lineHeight = lineHeight(12f, 1.3f),
        letterSpacing = 0.08.em,
        fontWeight = FontWeight.Normal,
    ),
    keypadKey = TextStyle(
        fontFamily = InstrumentSerifFamily,
        fontSize = 22.sp,
        lineHeight = lineHeight(22f, 1f),
        fontWeight = FontWeight.Normal,
    ),
    sansFamily = ManropeFamily,
    serifFamily = InstrumentSerifFamily,
    monoFamily = GeistMonoFamily,
)
