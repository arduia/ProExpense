package com.arduia.expense.ui.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun EmptyStateIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Canvas(
        modifier =
            modifier
                .width(dimens.emptyIllustrationWidth)
                .height(dimens.emptyIllustrationHeight),
    ) {
        // Artwork coordinates are authored in dp against emptyIllustration{Width,Height};
        // scale by density (px per dp) so the wallet fills the box on every screen, not just mdpi.
        val sx = size.width / dimens.emptyIllustrationWidth.value
        val sy = size.height / dimens.emptyIllustrationHeight.value
        val s = sx

        fun pt(
            x: Float,
            y: Float,
        ) = Offset(x * sx, y * sy)

        drawOval(
            color = colors.primaryTint.copy(alpha = 0.55f),
            topLeft = pt(30f, 108f),
            size = Size(120f * sx, 18f * sy),
        )

        val walletPath =
            Path().apply {
                moveTo(42f * sx, 48f * sy)
                lineTo(138f * sx, 48f * sy)
                quadraticTo(148f * sx, 48f * sy, 148f * sx, 58f * sy)
                lineTo(148f * sx, 98f * sy)
                quadraticTo(148f * sx, 108f * sy, 138f * sx, 108f * sy)
                lineTo(42f * sx, 108f * sy)
                quadraticTo(32f * sx, 108f * sy, 32f * sx, 98f * sy)
                lineTo(32f * sx, 58f * sy)
                quadraticTo(32f * sx, 48f * sy, 42f * sx, 48f * sy)
                close()
            }
        drawPath(walletPath, color = colors.surface)
        drawPath(walletPath, color = colors.onSurface, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))

        drawRoundRect(
            color = colors.primary,
            topLeft = pt(96f, 68f),
            size = Size(34f * sx, 24f * sy),
            cornerRadius = CornerRadius(6f * s, 6f * s),
        )
        drawCircle(color = colors.surface, radius = 5.5f * s, center = pt(113f, 80f))

        drawCircle(color = colors.primary, radius = 14f * s, center = pt(90f, 34f))
        drawCircle(
            color = colors.surface,
            radius = 10f * s,
            center = pt(90f, 34f),
            style = Stroke(width = 2f * s),
        )
        drawLine(
            color = colors.primary,
            start = pt(90f, 28f),
            end = pt(90f, 40f),
            strokeWidth = 2f * s,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun EmptyStateAddHint(
    prefix: String,
    suffix: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.space4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = prefix,
            style = typography.caption,
            color = colors.onSurfaceMuted,
        )
        AddHintChip()
        Text(
            text = suffix,
            style = typography.caption,
            color = colors.onSurfaceMuted,
        )
    }
}

@Composable
fun EmptyStateContent(
    title: String,
    subtitle: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    addHintPrefix: String? = null,
    addHintSuffix: String? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val titleStyle =
        typography.bodySemiBold.copy(
            fontSize = typography.sectionHead.fontSize,
            lineHeight = typography.sectionHead.lineHeight,
            letterSpacing = typography.sectionHead.letterSpacing,
        )

    Column(
        modifier = modifier.padding(horizontal = dimens.space8),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EmptyStateIllustration()
        Text(
            text = title,
            style = titleStyle,
            color = colors.onSurface,
            modifier = Modifier.padding(top = dimens.space18),
        )
        Text(
            text = subtitle,
            style = typography.body,
            color = colors.onSurfaceMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = dimens.space8),
        )
        ProPillButton(
            text = actionLabel,
            onClick = onActionClick,
            leading = {
                ProIcon(
                    glyph = ProIconGlyph.Plus,
                    contentDescription = null,
                    tint = colors.onPrimaryWarm,
                    size = dimens.iconInline,
                )
            },
            modifier = Modifier.padding(top = dimens.space20),
        )
        if (addHintPrefix != null && addHintSuffix != null) {
            EmptyStateAddHint(
                prefix = addHintPrefix,
                suffix = addHintSuffix,
                modifier = Modifier.padding(top = dimens.space10),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun EmptyStateIllustrationPreview() {
    ProExpenseTheme {
        EmptyStateIllustration(
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun EmptyStateContentPreview() {
    ProExpenseTheme {
        EmptyStateContent(
            title = "No expenses yet",
            subtitle = "Start by logging your first one — it takes about five seconds.",
            actionLabel = "Log your first expense",
            onActionClick = {},
            addHintPrefix = "or tap ",
            addHintSuffix = " below",
            modifier = Modifier.padding(vertical = 24.dp),
        )
    }
}
