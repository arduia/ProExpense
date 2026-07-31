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

// Prompt ships static weights only (not a variable font) — plain Font entries.
private val PromptFamily =
    FontFamily(
        Font(R.font.prompt_medium, FontWeight.Normal),
        Font(R.font.prompt_medium, FontWeight.Medium),
        Font(R.font.prompt_semibold, FontWeight.SemiBold),
        Font(R.font.prompt_semibold, FontWeight.Bold),
    )

private val GeistMonoFamily =
    FontFamily(
        variableFont(R.font.geist_mono_variable, FontWeight.Normal),
        variableFont(R.font.geist_mono_variable, FontWeight.Medium),
        variableFont(R.font.geist_mono_variable, FontWeight.SemiBold),
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
        fontFamily = PromptFamily,
        fontSizeSp = fontSizeSp,
        lineHeightSp = lineHeightSp,
        letterSpacingEm = letterSpacingEm,
        fontWeight = FontWeight.SemiBold,
    )

@Immutable
data class ProTypography(
    val displayAmount: TextStyle,
    val summaryAmount: TextStyle,
    val detailsAmount: TextStyle,
    val cardAmount: TextStyle,
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
                fontSizeSp = 58f,
                lineHeightSp = 58f,
                letterSpacingEm = -0.02f,
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
        // Compact card amounts (Home's active-event card) — one step down from detailsAmount,
        // matching canvas's VBCardSpendTrip inline amount (22sp).
        cardAmount =
            amountTextStyle(
                fontSizeSp = 22f,
                lineHeightSp = 24f,
                letterSpacingEm = -0.01f,
            ),
        listAmount =
            amountTextStyle(
                fontSizeSp = 15f,
                lineHeightSp = 15f,
                letterSpacingEm = 0f,
            ),
        appBarTitle =
            titleTextStyle(
                fontFamily = PromptFamily,
                fontSizeSp = 16f,
                lineHeightSp = 19.2f,
                fontWeight = FontWeight.SemiBold,
            ),
        heroGreeting =
            titleTextStyle(
                fontFamily = PromptFamily,
                fontSizeSp = 24f,
                lineHeightSp = 26.4f,
                letterSpacingEm = -0.01f,
                fontWeight = FontWeight.SemiBold,
            ),
        heroGreetingEmphasis =
            titleTextStyle(
                fontFamily = PromptFamily,
                fontSizeSp = 24f,
                lineHeightSp = 26.4f,
                letterSpacingEm = -0.01f,
                fontWeight = FontWeight.SemiBold,
            ),
        sheetTitle =
            titleTextStyle(
                fontFamily = PromptFamily,
                fontSizeSp = 22f,
                lineHeightSp = 24f,
                letterSpacingEm = -0.01f,
                fontWeight = FontWeight.SemiBold,
            ),
        sectionHead =
            titleTextStyle(
                fontFamily = PromptFamily,
                fontSizeSp = 15.5f,
                lineHeightSp = 18f,
                letterSpacingEm = 0f,
                fontWeight = FontWeight.SemiBold,
            ),
        screenHeaderTitle =
            titleTextStyle(
                fontFamily = PromptFamily,
                fontSizeSp = 16f,
                lineHeightSp = 19.2f,
                letterSpacingEm = 0f,
                fontWeight = FontWeight.SemiBold,
            ),
        onboardingSlideTitle =
            titleTextStyle(
                fontFamily = PromptFamily,
                fontSizeSp = 38f,
                lineHeightSp = 40f,
                letterSpacingEm = -0.02f,
                fontWeight = FontWeight.SemiBold,
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
                fontFamily = PromptFamily,
                fontSizeSp = 28f,
                lineHeightSp = 30f,
                letterSpacingEm = -0.01f,
                fontWeight = FontWeight.SemiBold,
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
                fontFamily = PromptFamily,
                fontSize = 12.5.sp,
                lineHeight = proLineHeight(12.5f, 1.3f),
                fontWeight = FontWeight.Medium,
            ),
        chipLabelSelected =
            TextStyle(
                fontFamily = PromptFamily,
                fontSize = 12.5.sp,
                lineHeight = proLineHeight(12.5f, 1.3f),
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
                fontSizeSp = 21f,
                lineHeightSp = 21f,
                letterSpacingEm = 0f,
            ),
        navLabel =
            TextStyle(
                fontFamily = PromptFamily,
                fontSize = 10.5.sp,
                lineHeight = proLineHeight(10.5f, 1.2f),
                letterSpacing = 0.01.em,
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
        amountFamily = PromptFamily,
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
