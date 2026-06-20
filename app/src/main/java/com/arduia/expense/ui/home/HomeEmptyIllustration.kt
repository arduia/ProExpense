package com.arduia.expense.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

private const val IllustrationWidthDp = 180f
private const val IllustrationHeightDp = 140f

@Composable
fun HomeEmptyIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors

    Canvas(modifier) {
        val sx = size.width / IllustrationWidthDp
        val sy = size.height / IllustrationHeightDp
        val s = sx

        fun pt(x: Float, y: Float) = Offset(x * sx, y * sy)

        drawOval(
            color = colors.primaryTint.copy(alpha = 0.55f),
            topLeft = pt(30f, 108f),
            size = Size(120f * sx, 18f * sy),
        )

        val walletPath = Path().apply {
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

@Preview(showBackground = true, widthDp = 200, heightDp = 160)
@Composable
private fun HomeEmptyIllustrationPreview() {
    ProExpenseTheme {
        HomeEmptyIllustration(
            modifier = Modifier
                .width(IllustrationWidthDp.dp)
                .height(IllustrationHeightDp.dp),
        )
    }
}
