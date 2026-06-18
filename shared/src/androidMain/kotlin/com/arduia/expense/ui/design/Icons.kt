package com.arduia.expense.ui.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Stroke icon set — DESIGN-SYSTEM.md §3. Drawn on a 24×24 grid with rounded
// caps/joins, 2px weight at the icon's nominal size. Only the glyphs the
// onboarding flow needs are defined here; extend as new screens land.

private fun DrawScope.strokePolyline(pts: List<Offset>, color: Color, weight: Float) {
    val sx = size.width / 24f
    val sy = size.height / 24f
    val w = weight * sx
    for (i in 0 until pts.size - 1) {
        drawLine(
            color,
            Offset(pts[i].x * sx, pts[i].y * sy),
            Offset(pts[i + 1].x * sx, pts[i + 1].y * sy),
            strokeWidth = w,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun IconArrowLeft(color: Color, modifier: Modifier = Modifier, size: Dp = 18.dp, weight: Float = 2f) {
    Canvas(modifier.then(Modifier.iconSize(size))) {
        val sx = this.size.width / 24f
        val sy = this.size.height / 24f
        // shaft
        drawLine(color, Offset(20f * sx, 12f * sy), Offset(5f * sx, 12f * sy), weight * sx, StrokeCap.Round)
        // head
        strokePolyline(listOf(Offset(11f, 6f), Offset(5f, 12f), Offset(11f, 18f)), color, weight)
    }
}

@Composable
fun IconChevronRight(color: Color, modifier: Modifier = Modifier, size: Dp = 18.dp, weight: Float = 2f) {
    Canvas(modifier.then(Modifier.iconSize(size))) {
        strokePolyline(listOf(Offset(9f, 6f), Offset(15f, 12f), Offset(9f, 18f)), color, weight)
    }
}

@Composable
fun IconChevronLeft(color: Color, modifier: Modifier = Modifier, size: Dp = 18.dp, weight: Float = 2f) {
    Canvas(modifier.then(Modifier.iconSize(size))) {
        strokePolyline(listOf(Offset(15f, 6f), Offset(9f, 12f), Offset(15f, 18f)), color, weight)
    }
}

@Composable
fun IconUser(color: Color, modifier: Modifier = Modifier, size: Dp = 22.dp, weight: Float = 1.8f) {
    Canvas(modifier.then(Modifier.iconSize(size))) {
        val sx = this.size.width / 24f
        val sy = this.size.height / 24f
        val w = weight * sx
        drawCircle(color, 4f * sx, Offset(12f * sx, 8f * sy), style = Stroke(width = w))
        strokePolyline(
            listOf(Offset(5f, 20f), Offset(5f, 17f), Offset(12f, 14f), Offset(19f, 17f), Offset(19f, 20f)),
            color,
            weight,
        )
    }
}

@Composable
fun IconSearch(color: Color, modifier: Modifier = Modifier, size: Dp = 20.dp, weight: Float = 1.8f) {
    Canvas(modifier.then(Modifier.iconSize(size))) {
        val sx = this.size.width / 24f
        val w = weight * sx
        drawCircle(color, 7f * sx, Offset(10f * sx, 10f * sx), style = Stroke(width = w))
        drawLine(
            color,
            Offset(15.5f * sx, 15.5f * sx),
            Offset(20f * sx, 20f * sx),
            strokeWidth = w,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun IconCheck(color: Color, modifier: Modifier = Modifier, size: Dp = 20.dp, weight: Float = 2.4f) {
    Canvas(modifier.then(Modifier.iconSize(size))) {
        strokePolyline(listOf(Offset(5f, 12.5f), Offset(10f, 17f), Offset(19f, 7f)), color, weight)
    }
}

private fun Modifier.iconSize(size: Dp): Modifier = this.size(size)
