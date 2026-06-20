package com.arduia.expense.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ProShadowLayer(
    val offsetX: Dp,
    val offsetY: Dp,
    val blur: Dp,
    val spread: Dp,
    val color: Color,
)

@Immutable
data class ProElevation(
    val card: List<ProShadowLayer>,
    val sheet: List<ProShadowLayer>,
    val toast: List<ProShadowLayer>,
    val nav: List<ProShadowLayer>,
    val fab: List<ProShadowLayer>,
)

val LocalProElevation = staticCompositionLocalOf { ProDefaultElevation }

val ProDefaultElevation = ProElevation(
    card = listOf(
        ProShadowLayer(0.dp, 1.dp, 0.dp, 0.dp, Color(0x08212121)),
        ProShadowLayer(0.dp, 6.dp, 16.dp, 0.dp, Color(0x0A212121)),
    ),
    sheet = listOf(
        ProShadowLayer(0.dp, (-8).dp, 24.dp, 0.dp, Color(0x26000000)),
    ),
    toast = listOf(
        ProShadowLayer(0.dp, 8.dp, 18.dp, 0.dp, Color(0x2E000000)),
    ),
    nav = listOf(
        ProShadowLayer(0.dp, (-6).dp, 24.dp, 0.dp, Color(0x1A000000)),
    ),
    fab = listOf(
        ProShadowLayer(0.dp, 4.dp, 10.dp, 0.dp, Color(0x40039BE5)),
    ),
)
