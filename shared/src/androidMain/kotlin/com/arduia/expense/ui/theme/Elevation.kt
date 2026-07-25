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
    val identityCard: List<ProShadowLayer>,
    val sheet: List<ProShadowLayer>,
    val toast: List<ProShadowLayer>,
    val nav: List<ProShadowLayer>,
    val fab: List<ProShadowLayer>,
)

val LocalProElevation = staticCompositionLocalOf { proElevation(ProLightColors) }

/**
 * Card shadows are blue-tinted in light (via [ProColors.cardShadowTint]) but neutral in dark —
 * a navy-tinted shadow is invisible against a navy surface, so dark relies on the
 * paper/card surface contrast instead (Blue Banking canvas).
 */
fun proElevation(colors: ProColors): ProElevation {
    val heroShadow = listOf(ProShadowLayer(0.dp, 8.dp, 20.dp, 0.dp, colors.primary.copy(alpha = 0.28f)))
    return ProElevation(
        card =
            listOf(
                ProShadowLayer(0.dp, 1.dp, 0.dp, 0.dp, colors.cardShadowTint.copy(alpha = 0.03f)),
                ProShadowLayer(0.dp, 6.dp, 16.dp, 0.dp, colors.cardShadowTint.copy(alpha = 0.06f)),
            ),
        identityCard = heroShadow,
        sheet =
            listOf(
                ProShadowLayer(0.dp, (-8).dp, 24.dp, 0.dp, Color(0x26000000)),
            ),
        toast =
            listOf(
                ProShadowLayer(0.dp, 8.dp, 18.dp, 0.dp, Color(0x2E000000)),
            ),
        nav =
            listOf(
                ProShadowLayer(0.dp, (-6).dp, 24.dp, 0.dp, Color(0x1A000000)),
            ),
        fab = heroShadow,
    )
}
