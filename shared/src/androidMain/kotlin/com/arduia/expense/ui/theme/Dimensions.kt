package com.arduia.expense.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Spacing and sizing tokens — DESIGN-SYSTEM.md §5–§8 (Android Material rendition).
@Immutable
data class ProExpenseDimensions(
    // Spacing scale
    val space2: Dp = 2.dp,
    val space4: Dp = 4.dp,
    val space5: Dp = 5.dp,
    val space6: Dp = 6.dp,
    val space7: Dp = 7.dp,
    val space8: Dp = 8.dp,
    val space10: Dp = 10.dp,
    val space12: Dp = 12.dp,
    val space14: Dp = 14.dp,
    val space15: Dp = 15.dp,
    val space16: Dp = 16.dp,
    val space18: Dp = 18.dp,
    val space20: Dp = 20.dp,
    val space22: Dp = 22.dp,
    val space24: Dp = 24.dp,
    val space28: Dp = 28.dp,
    val space30: Dp = 30.dp,
    val space32: Dp = 32.dp,
    val space44: Dp = 44.dp,

    // Chrome heights
    val topBarHeight: Dp = 56.dp,
    val buttonFilledHeight: Dp = 56.dp,
    val buttonTextHeight: Dp = 40.dp,
    val fieldMinHeight: Dp = 56.dp,
    val searchFieldHeight: Dp = 48.dp,

    // Touch targets & icons
    val iconButtonSize: Dp = 48.dp,
    val iconSizeNav: Dp = 24.dp,
    val iconSizeDefault: Dp = 18.dp,
    val iconSizeSearch: Dp = 20.dp,
    val iconSizeCheck: Dp = 20.dp,
    val iconSizeUser: Dp = 22.dp,
    val rippleRadiusIconButton: Dp = 24.dp,

    // Borders
    val borderWidth: Dp = 1.4.dp,
    val borderWidthFocused: Dp = 2.dp,
    val radioBorderWidth: Dp = 2.dp,

    // Buttons
    val buttonShadowElevation: Dp = 6.dp,
    val buttonFilledPaddingH: Dp = 24.dp,
    val buttonTextPaddingH: Dp = 12.dp,
    val buttonContentGap: Dp = 8.dp,
    val buttonTextContentGap: Dp = 4.dp,

    // Fields
    val fieldPaddingH: Dp = 16.dp,
    val fieldPaddingHWithLeading: Dp = 14.dp,
    val fieldLeadingGap: Dp = 12.dp,
    val fieldPaddingVertical: Dp = 15.dp,
    val fieldLabelPaddingH: Dp = 4.dp,
    val fieldLabelOffsetX: Dp = 14.dp,
    val fieldLabelOffsetXWithLeading: Dp = 44.dp,
    val fieldLabelOffsetY: Dp = (-8).dp,
    val fieldHelperPaddingTop: Dp = 5.dp,

    // Progress
    val progressBarHeight: Dp = 4.dp,
    val progressCircleSize: Dp = 36.dp,
    val progressCircleStroke: Dp = 3.5.dp,

    // Radio
    val radioOuterSize: Dp = 22.dp,
    val radioInnerSize: Dp = 11.dp,

    // Page indicator
    val pageDotSize: Dp = 7.dp,
    val pageDotActiveWidth: Dp = 22.dp,
    val pageDotSpacing: Dp = 7.dp,

    // Sheet
    val sheetHandleWidth: Dp = 32.dp,
    val sheetHandleHeight: Dp = 4.dp,
    val sheetMaxHeight: Dp = 520.dp,
    val sheetHandlePaddingTop: Dp = 12.dp,
    val sheetHandlePaddingBottom: Dp = 14.dp,

    // List rows
    val currencyBadgeSize: Dp = 40.dp,
    val listRowRadius: Dp = 14.dp,

    // Splash
    val splashProgressBottom: Dp = 116.dp,
    val splashBrandingBottom: Dp = 54.dp,
    val splashBrandingGap: Dp = 3.dp,

    // Onboarding
    val onboardingIllustrationWidth: Dp = 240.dp,
    val onboardingIllustrationHeight: Dp = 200.dp,
    val onboardingIllustrationFrameWidth: Dp = 280.dp,
    val onboardingBodyMaxWidth: Dp = 280.dp,
)

val LocalProExpenseDimensions = staticCompositionLocalOf { ProExpenseDimensions() }
