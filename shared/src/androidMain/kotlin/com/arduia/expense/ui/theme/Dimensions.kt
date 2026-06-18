package com.arduia.expense.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Layout tokens — design_handoff_v2 / design-tokens.json → dimension.*
@Immutable
data class ProExpenseDimensions(
    val space2: Dp = 2.dp,
    val space4: Dp = 4.dp,
    val space6: Dp = 6.dp,
    val space8: Dp = 8.dp,
    val space10: Dp = 10.dp,
    val space12: Dp = 12.dp,
    val space14: Dp = 14.dp,
    val space16: Dp = 16.dp,
    val space18: Dp = 18.dp,
    val space20: Dp = 20.dp,
    val space22: Dp = 22.dp,
    val space24: Dp = 24.dp,
    val space28: Dp = 28.dp,
    val space30: Dp = 30.dp,
    val space32: Dp = 32.dp,
    val space38: Dp = 38.dp,
    val space44: Dp = 44.dp,

    val topBarHeight: Dp = 56.dp,
    val buttonFilledHeight: Dp = 52.dp,
    val buttonOutlinedHeight: Dp = 48.dp,
    val buttonSmallHeight: Dp = 36.dp,
    val buttonPaddingHSm: Dp = 14.dp,
    val buttonPaddingHLg: Dp = 22.dp,
    val buttonPaddingVSm: Dp = 8.dp,
    val buttonPaddingVMd: Dp = 12.dp,
    val buttonPaddingVLg: Dp = 16.dp,
    val buttonBorderWidth: Dp = 1.4.dp,
    val borderWidth: Dp = 1.4.dp,
    val buttonShadowElevation: Dp = 6.dp,
    val buttonContentGap: Dp = 8.dp,

    val fieldMinHeight: Dp = 56.dp,
    val fieldPaddingH: Dp = 16.dp,
    val fieldPaddingHWithLeading: Dp = 14.dp,
    val fieldLeadingGap: Dp = 12.dp,
    val fieldPaddingVertical: Dp = 16.dp,
    val fieldHelperPaddingTop: Dp = 6.dp,
    val fieldLabelGap: Dp = 8.dp,

    val searchFieldHeight: Dp = 48.dp,
    val iconButtonSize: Dp = 44.dp,
    val iconSizeNav: Dp = 24.dp,
    val iconSizeTabBar: Dp = 25.dp,
    val iconSizeInline: Dp = 16.dp,
    val iconSizeDefault: Dp = 20.dp,
    val rippleRadiusIconButton: Dp = 22.dp,

    val progressCircleSize: Dp = 36.dp,
    val progressCircleStroke: Dp = 3.5.dp,
    val profileProgressBarWidth: Dp = 28.dp,
    val profileProgressBarHeight: Dp = 4.dp,
    val profileProgressDotSize: Dp = 6.dp,
    val profileProgressGap: Dp = 6.dp,

    val pageDotSize: Dp = 7.dp,
    val pageDotActiveWidth: Dp = 22.dp,
    val pageDotSpacing: Dp = 7.dp,

    val sheetHandleWidth: Dp = 36.dp,
    val sheetHandleHeight: Dp = 4.dp,
    val sheetMaxHeightFraction: Float = 0.78f,
    val sheetHandlePaddingTop: Dp = 12.dp,
    val sheetHandlePaddingBottom: Dp = 14.dp,

    val currencyBadgeSize: Dp = 40.dp,
    val listRowRadius: Dp = 14.dp,
    val cardPadding: Dp = 18.dp,

    val splashProgressBottom: Dp = 116.dp,
    val splashBrandingBottom: Dp = 54.dp,
    val splashBrandingGap: Dp = 3.dp,

    val onboardingIllustrationWidth: Dp = 240.dp,
    val onboardingIllustrationHeight: Dp = 200.dp,
    val onboardingIllustrationFrameWidth: Dp = 280.dp,
    val onboardingBodyMaxWidth: Dp = 300.dp,

    val categoryBadgeSize: Dp = 38.dp,
    val categoryChipIconSize: Dp = 14.dp,
    val homeHeaderPaddingH: Dp = 22.dp,
    val homeFabSize: Dp = 64.dp,
    val homeFabOverhang: Dp = 32.dp,
    val bottomNavHeight: Dp = 72.dp,
    val bottomNavChromeHeight: Dp = 104.dp,
    val bottomNavIconBandHeight: Dp = 42.dp,
    val bottomNavLabelPaddingBottom: Dp = 10.dp,
    val bottomNavInsetH: Dp = 16.dp,
    val bottomNavBottomInset: Dp = 12.dp,
    val bottomNavShadowElevation: Dp = 8.dp,
    val homeFabBottomOffset: Dp = 116.dp,
    val keypadKeyHeight: Dp = 52.dp,
    val keypadActionHeight: Dp = 52.dp,
)

val LocalProExpenseDimensions = staticCompositionLocalOf { ProExpenseDimensions() }
