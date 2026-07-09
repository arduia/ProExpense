package com.arduia.expense.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.arduia.expense.shared.R

@OptIn(ExperimentalTextApi::class)
private fun variableFont(
    resId: Int,
    weight: FontWeight,
): Font =
    Font(
        resId = resId,
        weight = weight,
        variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
    )

private val ManropeFamily =
    FontFamily(
        variableFont(R.font.manrope_variable, FontWeight.Light),
        variableFont(R.font.manrope_variable, FontWeight.Normal),
        variableFont(R.font.manrope_variable, FontWeight.Medium),
        variableFont(R.font.manrope_variable, FontWeight.SemiBold),
        variableFont(R.font.manrope_variable, FontWeight.Bold),
        variableFont(R.font.manrope_variable, FontWeight.ExtraBold),
    )

private val GeistMonoFamily =
    FontFamily(
        variableFont(R.font.geist_mono_variable, FontWeight.Normal),
        variableFont(R.font.geist_mono_variable, FontWeight.Medium),
        variableFont(R.font.geist_mono_variable, FontWeight.SemiBold),
    )

private val InterFamily =
    FontFamily(
        variableFont(R.font.inter_variable, FontWeight.Normal),
        variableFont(R.font.inter_variable, FontWeight.Medium),
        variableFont(R.font.inter_variable, FontWeight.SemiBold),
        Font(R.font.inter_variable_italic, FontWeight.Normal, FontStyle.Italic),
    )

private val FigmaAlignedPlatform = PlatformTextStyle(includeFontPadding = false)

private val FigmaAlignedLineHeight =
    LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.Both,
    )

/**
 * Strips font padding and re-centers line height so a single glyph (e.g. an avatar initial)
 * sits dead-center inside a fixed box. Use for non-title text rendered in a circle/badge.
 */
fun TextStyle.centeredGlyph(): TextStyle =
    copy(
        platformStyle = FigmaAlignedPlatform,
        lineHeightStyle = FigmaAlignedLineHeight,
    )

@OptIn(ExperimentalTextApi::class)
private fun titleTextStyle(
    fontFamily: FontFamily,
    fontSizeSp: Float,
    lineHeightSp: Float,
    letterSpacingEm: Float = 0f,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
): TextStyle =
    TextStyle(
        fontFamily = fontFamily,
        fontSize = fontSizeSp.sp,
        lineHeight = lineHeightSp.sp,
        letterSpacing = letterSpacingEm.em,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        platformStyle = FigmaAlignedPlatform,
        lineHeightStyle = FigmaAlignedLineHeight,
    )

@OptIn(ExperimentalTextApi::class)
private fun amountTextStyle(
    fontSizeSp: Float,
    lineHeightSp: Float,
    letterSpacingEm: Float,
): TextStyle =
    titleTextStyle(
        fontFamily = InterFamily,
        fontSizeSp = fontSizeSp,
        lineHeightSp = lineHeightSp,
        letterSpacingEm = letterSpacingEm,
        fontWeight = FontWeight.Normal,
    )

@Immutable
data class ProTypography(
    val displayAmount: TextStyle,
    val summaryAmount: TextStyle,
    val detailsAmount: TextStyle,
    val listAmount: TextStyle,
    val appBarTitle: TextStyle,
    val heroGreeting: TextStyle,
    val heroGreetingEmphasis: TextStyle,
    val sheetTitle: TextStyle,
    val sectionHead: TextStyle,
    val screenHeaderTitle: TextStyle,
    val onboardingSlideTitle: TextStyle,
    val onboardingBody: TextStyle,
    val profileScreenTitle: TextStyle,
    val navAction: TextStyle,
    val body: TextStyle,
    val bodyMedium: TextStyle,
    val bodySemiBold: TextStyle,
    /** A step larger and bolder than [body] — for a form's single primary "name" input. */
    val fieldValue: TextStyle,
    val caption: TextStyle,
    val captionMedium: TextStyle,
    val chipLabel: TextStyle,
    val chipLabelSelected: TextStyle,
    val eyebrow: TextStyle,
    val button: TextStyle,
    val tabTimestamp: TextStyle,
    val monoFigure: TextStyle,
    val keypadKey: TextStyle,
    val navLabel: TextStyle,
    val searchField: TextStyle,
    val sansFamily: FontFamily,
    val amountFamily: FontFamily,
    val monoFamily: FontFamily,
)

val LocalProTypography = staticCompositionLocalOf { ProDefaultTypography }

internal fun proLineHeight(
    fontSizeSp: Float,
    multiplier: Float,
) = (fontSizeSp * multiplier).sp

val ProDefaultTypography =
    ProTypography(
        displayAmount =
            amountTextStyle(
                fontSizeSp = 64f,
                lineHeightSp = 64f,
                letterSpacingEm = -0.025f,
            ),
        summaryAmount =
            amountTextStyle(
                fontSizeSp = 40f,
                lineHeightSp = 40f,
                letterSpacingEm = -0.02f,
            ),
        detailsAmount =
            amountTextStyle(
                fontSizeSp = 26f,
                lineHeightSp = 28f,
                letterSpacingEm = -0.01f,
            ),
        listAmount =
            amountTextStyle(
                fontSizeSp = 18f,
                lineHeightSp = 18f,
                letterSpacingEm = 0f,
            ),
        appBarTitle =
            titleTextStyle(
                fontFamily = ManropeFamily,
                fontSizeSp = 17f,
                lineHeightSp = 20.4f,
                fontWeight = FontWeight.Medium,
            ),
        heroGreeting =
            titleTextStyle(
                fontFamily = ManropeFamily,
                fontSizeSp = 30f,
                lineHeightSp = 32f,
                letterSpacingEm = -0.015f,
            ),
        heroGreetingEmphasis =
            titleTextStyle(
                fontFamily = InterFamily,
                fontSizeSp = 30f,
                lineHeightSp = 32f,
                letterSpacingEm = -0.015f,
                fontStyle = FontStyle.Italic,
            ),
        sheetTitle =
            titleTextStyle(
                fontFamily = InterFamily,
                fontSizeSp = 22f,
                lineHeightSp = 24f,
                letterSpacingEm = -0.01f,
            ),
        sectionHead =
            titleTextStyle(
                fontFamily = InterFamily,
                fontSizeSp = 18f,
                lineHeightSp = 20f,
                letterSpacingEm = -0.01f,
            ),
        screenHeaderTitle =
            titleTextStyle(
                fontFamily = InterFamily,
                fontSizeSp = 17f,
                lineHeightSp = 20f,
                letterSpacingEm = 0f,
            ),
        onboardingSlideTitle =
            titleTextStyle(
                fontFamily = InterFamily,
                fontSizeSp = 38f,
                lineHeightSp = 40f,
                letterSpacingEm = -0.02f,
            ),
        onboardingBody =
            TextStyle(
                fontFamily = ManropeFamily,
                fontSize = 15.sp,
                lineHeight = proLineHeight(15f, 1.45f),
                fontWeight = FontWeight.Normal,
            ),
        profileScreenTitle =
            titleTextStyle(
                fontFamily = InterFamily,
                fontSizeSp = 28f,
                lineHeightSp = 30f,
                letterSpacingEm = -0.01f,
            ),
        navAction =
            TextStyle(
                fontFamily = ManropeFamily,
                fontSize = 13.sp,
                lineHeight = proLineHeight(13f, 1.4f),
                fontWeight = FontWeight.Medium,
            ),
        body =
            TextStyle(
                fontFamily = ManropeFamily,
                fontSize = 14.sp,
                lineHeight = proLineHeight(14f, 1.4f),
                fontWeight = FontWeight.Normal,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = ManropeFamily,
                fontSize = 14.sp,
                lineHeight = proLineHeight(14f, 1.4f),
                fontWeight = FontWeight.Medium,
            ),
        bodySemiBold =
            TextStyle(
                fontFamily = ManropeFamily,
                fontSize = 14.sp,
                lineHeight = proLineHeight(14f, 1.4f),
                fontWeight = FontWeight.SemiBold,
            ),
        fieldValue =
            TextStyle(
                fontFamily = ManropeFamily,
                fontSize = 16.sp,
                lineHeight = proLineHeight(16f, 1.4f),
                fontWeight = FontWeight.Medium,
            ),
        caption =
            TextStyle(
                fontFamily = ManropeFamily,
                fontSize = 11.5.sp,
                lineHeight = proLineHeight(11.5f, 1.4f),
                fontWeight = FontWeight.Normal,
            ),
        captionMedium =
            TextStyle(
                fontFamily = ManropeFamily,
                fontSize = 11.5.sp,
                lineHeight = proLineHeight(11.5f, 1.4f),
                fontWeight = FontWeight.Medium,
            ),
        chipLabel =
            TextStyle(
                fontFamily = ManropeFamily,
                fontSize = 12.sp,
                lineHeight = proLineHeight(12f, 1.4f),
                fontWeight = FontWeight.Medium,
            ),
        chipLabelSelected =
            TextStyle(
                fontFamily = ManropeFamily,
                fontSize = 12.sp,
                lineHeight = proLineHeight(12f, 1.4f),
                fontWeight = FontWeight.SemiBold,
            ),
        eyebrow =
            TextStyle(
                fontFamily = GeistMonoFamily,
                fontSize = 11.sp,
                lineHeight = proLineHeight(11f, 1.3f),
                letterSpacing = 0.12.em,
                fontWeight = FontWeight.SemiBold,
            ),
        button =
            TextStyle(
                fontFamily = ManropeFamily,
                fontSize = 14.sp,
                lineHeight = proLineHeight(14f, 1.4f),
                letterSpacing = (-0.005).em,
                fontWeight = FontWeight.SemiBold,
            ),
        tabTimestamp =
            TextStyle(
                fontFamily = GeistMonoFamily,
                fontSize = 12.sp,
                lineHeight = proLineHeight(12f, 1.3f),
                letterSpacing = 0.08.em,
                fontWeight = FontWeight.Normal,
                fontFeatureSettings = "tnum",
            ),
        monoFigure =
            TextStyle(
                fontFamily = GeistMonoFamily,
                fontSize = 12.sp,
                lineHeight = proLineHeight(12f, 1.3f),
                letterSpacing = 0.04.em,
                fontWeight = FontWeight.Medium,
                fontFeatureSettings = "tnum",
            ),
        keypadKey =
            amountTextStyle(
                fontSizeSp = 22f,
                lineHeightSp = 22f,
                letterSpacingEm = 0f,
            ),
        navLabel =
            TextStyle(
                fontFamily = GeistMonoFamily,
                fontSize = 10.sp,
                lineHeight = proLineHeight(10f, 1.2f),
                letterSpacing = 0.08.em,
                fontWeight = FontWeight.Medium,
            ),
        searchField =
            TextStyle(
                fontFamily = ManropeFamily,
                fontSize = 13.sp,
                lineHeight = proLineHeight(13f, 1.4f),
                fontWeight = FontWeight.Normal,
            ),
        sansFamily = ManropeFamily,
        amountFamily = InterFamily,
        monoFamily = GeistMonoFamily,
    )

fun ProTypography.toMaterialTypography(): Typography =
    Typography(
        displayLarge = displayAmount,
        headlineMedium = heroGreeting,
        titleLarge = sheetTitle,
        titleMedium = sectionHead,
        bodyLarge = body,
        bodyMedium = bodyMedium,
        bodySmall = caption,
        labelLarge = button,
        labelMedium = eyebrow,
        labelSmall = navLabel,
    )
