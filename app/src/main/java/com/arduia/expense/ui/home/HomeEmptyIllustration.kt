package com.arduia.expense.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProExpensePalette
import com.arduia.expense.ui.theme.ProExpenseTheme

// Empty-home wallet illustration — line-art wallet with coin, per flow-01-home-empty.png.

private const val VB_W = 180f
private const val VB_H = 140f

@Composable
fun HomeEmptyIllustration(modifier: Modifier = Modifier) {
    val blue = ProExpensePalette.Primary
    val blueTint = ProExpensePalette.PrimaryTint
    val ink = ProExpensePalette.Ink
    val white = ProExpensePalette.Surface

    Canvas(modifier) {
        val sx = size.width / VB_W
        val sy = size.height / VB_H
        val s = sx

        fun pt(x: Float, y: Float) = Offset(x * sx, y * sy)

        drawOval(
            color = blueTint.copy(alpha = 0.55f),
            topLeft = pt(30f, 108f),
            size = Size(120f * sx, 18f * sy),
        )

        val walletPath = Path().apply {
            moveTo(42f * sx, 48f * sy)
            lineTo(138f * sx, 48f * sy)
            quadraticBezierTo(148f * sx, 48f * sy, 148f * sx, 58f * sy)
            lineTo(148f * sx, 98f * sy)
            quadraticBezierTo(148f * sx, 108f * sy, 138f * sx, 108f * sy)
            lineTo(42f * sx, 108f * sy)
            quadraticBezierTo(32f * sx, 108f * sy, 32f * sx, 98f * sy)
            lineTo(32f * sx, 58f * sy)
            quadraticBezierTo(32f * sx, 48f * sy, 42f * sx, 48f * sy)
            close()
        }
        drawPath(walletPath, color = white)
        drawPath(walletPath, color = ink, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))

        drawRoundRect(
            color = blue,
            topLeft = pt(96f, 68f),
            size = Size(34f * sx, 24f * sy),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f * s, 6f * s),
        )
        drawCircle(color = white, radius = 5.5f * s, center = pt(113f, 80f))

        drawCircle(color = blue, radius = 14f * s, center = pt(90f, 34f))
        drawCircle(color = white, radius = 10f * s, center = pt(90f, 34f), style = Stroke(width = 2f * s))
        drawLine(
            color = blue,
            start = pt(90f, 28f),
            end = pt(90f, 40f),
            strokeWidth = 2f * s,
            cap = StrokeCap.Round,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeEmptyIllustrationPreview() {
    ProExpenseTheme {
        HomeEmptyIllustration(
            modifier = Modifier
                .width(ProExpenseTheme.dimensions.homeEmptyIllustrationWidth)
                .height(ProExpenseTheme.dimensions.homeEmptyIllustrationHeight),
        )
    }
}
