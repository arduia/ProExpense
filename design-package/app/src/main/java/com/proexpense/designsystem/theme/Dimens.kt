package com.proexpense.designsystem.theme

import androidx.compose.ui.unit.dp

/* Spacing & sizing tokens — base rhythm 8dp. See tokens.md §3. */
object Dimens {
    val xs = 6.dp
    val s = 8.dp
    val m = 10.dp
    val md = 12.dp
    val l = 16.dp
    val xl = 22.dp
    val xxl = 26.dp

    val cardPadding = 18.dp
    val screenPadding = 22.dp
    val rowPaddingV = 12.dp
    val rowPaddingH = 8.dp

    val badge = 38.dp
    val badgeLarge = 48.dp
    val fab = 64.dp
    val navIcon = 25.dp
    val minTouch = 44.dp
}

/* Elevation is expressed as drop shadow only (no M3 tonal tint). */
object ProElevation {
    val card = 6.dp     // soft shadow blur
    val fab = 8.dp
    val sheet = 12.dp
    val toast = 10.dp
}
