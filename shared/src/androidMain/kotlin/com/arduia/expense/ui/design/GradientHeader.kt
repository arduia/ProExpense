package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Full-bleed 165° hero gradient (Blue Banking canvas) with a faint decorative ring offset past
 * the top-right corner. Sits at the top of Home/Journal/Budget/More, replacing a flat app bar
 * with the brand-forward header every core screen in the canvas is built around.
 */
@Composable
fun ProGradientHeader(
    modifier: Modifier = Modifier,
    // Canvas's VBHeader takes a per-screen `pb` (padding-bottom, default 20, e.g. More overrides
    // to 26) rather than one fixed value — screens with more header content need more room below
    // it before the sheet starts. Defaults to the plain top-padding value for screens that don't
    // need extra room (matches the original fixed-padding behavior).
    bottomPadding: Dp = ProExpenseTheme.dimensions.space16,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val brush =
        Brush.linearGradient(
            colors = colors.heroGradientStops,
            start = Offset(0f, 0f),
            end = Offset.Infinite,
        )

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(brush)
                .drawWithContent {
                    drawContent()
                    val ringRadius = size.width * 0.55f
                    drawCircle(
                        color = Color.White.copy(alpha = 0.09f),
                        radius = ringRadius,
                        center = Offset(x = size.width + ringRadius * 0.35f, y = -ringRadius * 0.4f),
                        style = Stroke(width = 1.5.dp.toPx()),
                    )
                }.statusBarsPadding()
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space16, bottom = bottomPadding),
    ) {
        content()
    }
}

/**
 * The header's title block: eyebrow (white 70% opacity, mono) + title (white, Prompt SemiBold),
 * matching every VBHeader instance in the canvas. [eyebrow] is optional (Splash has none).
 */
@Composable
fun ProGradientHeaderTitle(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
) {
    val typography = ProExpenseTheme.typography
    Column(modifier = modifier) {
        if (eyebrow != null) {
            Text(
                text = eyebrow,
                style = typography.caption,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
        Text(
            text = title,
            style = typography.heroGreeting,
            color = Color.White,
        )
    }
}

/**
 * Content sheet that overlaps a [ProGradientHeader] by [overlap] — 26dp top radius (Blue
 * Banking). Background is [ProColors.paper] so cards laid on top read as elevated surfaces.
 */
@Composable
fun ProSheetSurface(
    modifier: Modifier = Modifier,
    overlap: Dp = 14.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val shapes = ProExpenseTheme.shapes
    val dimens = ProExpenseTheme.dimensions

    // Modifier.padding rejects negative values, so the overlap is a paint-time offset instead —
    // it shifts the sheet up over the header without shrinking the space this Column reports to
    // its own parent, leaving a few dp of same-colored (paper-on-paper) slack below. Acceptable
    // for a linear-flow layout; a pixel-exact overlap would need a custom Layout/Box overlay.
    //
    // background(color, shape) — not clip(shape).background(color) — deliberately: clip() also
    // clips *content*, not just the background fill. Canvas's VBSheet sets border-radius without
    // overflow:hidden, so a child floated further upward with its own negative offset (Home's
    // spend card, marginTop:-30 on top of this -14 overlap) paints its own rounded corners over
    // the header, unclipped. clip() here would flatten that child's top edge to a hard cut right
    // at this Column's own top boundary — invisible floating, square instead of rounded corners.
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .offset(y = -overlap)
                .background(colors.paper, shapes.sheet)
                .padding(horizontal = dimens.screenPadding, vertical = dimens.space16),
        content = content,
    )
}

@Preview(
    name = "ProGradientHeader",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = 220,
    showBackground = true,
)
@Composable
private fun ProGradientHeaderPreview() {
    ProExpenseTheme {
        ProGradientHeader {
            ProGradientHeaderTitle(title = "Hi, Maya", eyebrow = "WED · MAY 25")
        }
    }
}

@Preview(
    name = "ProGradientHeader — dark",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = 220,
    showBackground = true,
)
@Composable
private fun ProGradientHeaderDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        ProGradientHeader {
            ProGradientHeaderTitle(title = "Hi, Maya", eyebrow = "WED · MAY 25")
        }
    }
}

@Preview(
    name = "ProSheetSurface",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = 300,
    showBackground = true,
)
@Composable
private fun ProSheetSurfacePreview() {
    ProExpenseTheme {
        Column {
            ProGradientHeader {
                ProGradientHeaderTitle(title = "Hi, Maya", eyebrow = "WED · MAY 25")
            }
            ProSheetSurface {
                Text(
                    text = "Sheet content",
                    style = ProExpenseTheme.typography.sectionHead,
                    color = ProExpenseTheme.colors.onSurface,
                )
            }
        }
    }
}
