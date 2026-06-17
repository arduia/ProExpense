package com.arduia.expense.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

// Stroke icons drawn on a 24×24 grid at ~1.8px weight with rounded caps — see design/DESIGN-SYSTEM.md §3.

@Composable
fun UserGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.09f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * strokeWidthFraction, cap = StrokeCap.Round, join = StrokeJoin.Round)

        drawCircle(
            color = color,
            radius = w * 0.18f,
            center = Offset(w * 0.5f, h * 0.30f),
            style = stroke,
        )
        // Shoulders — top half of an ellipse below the head.
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.18f, h * 0.52f),
            size = Size(w * 0.64f, h * 0.62f),
            style = stroke,
        )
    }
}

@Composable
fun SearchGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.085f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * strokeWidthFraction, cap = StrokeCap.Round)

        drawCircle(
            color = color,
            radius = w * 0.28f,
            center = Offset(w * 0.42f, h * 0.42f),
            style = stroke,
        )
        drawLine(
            color = color,
            start = Offset(w * 0.64f, h * 0.64f),
            end = Offset(w * 0.84f, h * 0.84f),
            strokeWidth = w * strokeWidthFraction,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun CloseGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.085f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = w * strokeWidthFraction
        drawLine(
            color = color,
            start = Offset(w * 0.26f, h * 0.26f),
            end = Offset(w * 0.74f, h * 0.74f),
            strokeWidth = sw,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(w * 0.74f, h * 0.26f),
            end = Offset(w * 0.26f, h * 0.74f),
            strokeWidth = sw,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun CheckGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.11f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.20f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.72f)
            lineTo(w * 0.80f, h * 0.30f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = w * strokeWidthFraction, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}
