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

@Composable
fun IconClose(color: Color, modifier: Modifier = Modifier, size: Dp = 20.dp, weight: Float = 1.8f) {
    Canvas(modifier.then(Modifier.iconSize(size))) {
        strokePolyline(listOf(Offset(6f, 6f), Offset(18f, 18f)), color, weight)
        strokePolyline(listOf(Offset(18f, 6f), Offset(6f, 18f)), color, weight)
    }
}

@Composable
fun IconPlus(color: Color, modifier: Modifier = Modifier, size: Dp = 24.dp, weight: Float = 2f) {
    Canvas(modifier.then(Modifier.iconSize(size))) {
        val sx = this.size.width / 24f
        val w = weight * sx
        drawLine(color, Offset(12f * sx, 5f * sx), Offset(12f * sx, 19f * sx), w, StrokeCap.Round)
        drawLine(color, Offset(5f * sx, 12f * sx), Offset(19f * sx, 12f * sx), w, StrokeCap.Round)
    }
}

@Composable
fun IconBell(color: Color, modifier: Modifier = Modifier, size: Dp = 18.dp, weight: Float = 1.6f) {
    Canvas(modifier.then(Modifier.iconSize(size))) {
        val sx = this.size.width / 24f
        val sy = this.size.height / 24f
        val w = weight * sx
        strokePolyline(listOf(Offset(6f, 16f), Offset(6f, 11f), Offset(12f, 5f), Offset(18f, 11f), Offset(18f, 16f), Offset(19.5f, 18f), Offset(4.5f, 18f), Offset(6f, 16f)), color, weight)
        drawLine(color, Offset(10f * sx, 20f * sy), Offset(14f * sx, 20f * sy), w, StrokeCap.Round)
    }
}

@Composable
fun IconCalendar(color: Color, modifier: Modifier = Modifier, size: Dp = 18.dp, weight: Float = 1.7f) {
    Canvas(modifier.then(Modifier.iconSize(size))) {
        val sx = this.size.width / 24f
        val sy = this.size.height / 24f
        val w = weight * sx
        drawRoundRect(
            color = color,
            topLeft = Offset(4f * sx, 5f * sy),
            size = androidx.compose.ui.geometry.Size(16f * sx, 15f * sy),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f * sx, 2f * sy),
            style = Stroke(width = w),
        )
        drawLine(color, Offset(4f * sx, 10f * sy), Offset(20f * sx, 10f * sy), w, StrokeCap.Round)
        drawLine(color, Offset(9f * sx, 3f * sy), Offset(9f * sx, 7f * sy), w, StrokeCap.Round)
        drawLine(color, Offset(15f * sx, 3f * sy), Offset(15f * sx, 7f * sy), w, StrokeCap.Round)
    }
}

@Composable
fun IconClock(color: Color, modifier: Modifier = Modifier, size: Dp = 12.dp, weight: Float = 1.8f) {
    Canvas(modifier.then(Modifier.iconSize(size))) {
        val sx = this.size.width / 24f
        val w = weight * sx
        drawCircle(color, 8f * sx, Offset(12f * sx, 12f * sx), style = Stroke(width = w))
        drawLine(color, Offset(12f * sx, 12f * sx), Offset(12f * sx, 8f * sx), w, StrokeCap.Round)
        drawLine(color, Offset(12f * sx, 12f * sx), Offset(15f * sx, 14f * sx), w, StrokeCap.Round)
    }
}

@Composable
fun IconNote(color: Color, modifier: Modifier = Modifier, size: Dp = 18.dp, weight: Float = 1.6f) {
    Canvas(modifier.then(Modifier.iconSize(size))) {
        val sx = this.size.width / 24f
        val sy = this.size.height / 24f
        val w = weight * sx
        strokePolyline(listOf(Offset(6f, 4f), Offset(15f, 4f), Offset(19f, 8f), Offset(19f, 20f), Offset(6f, 20f), Offset(6f, 4f)), color, weight)
        drawLine(color, Offset(15f * sx, 4f * sy), Offset(15f * sx, 8f * sy), w, StrokeCap.Round)
        drawLine(color, Offset(15f * sx, 8f * sy), Offset(19f * sx, 8f * sy), w, StrokeCap.Round)
        drawLine(color, Offset(9f * sx, 13f * sy), Offset(15f * sx, 13f * sy), w, StrokeCap.Round)
        drawLine(color, Offset(9f * sx, 17f * sy), Offset(13f * sx, 17f * sy), w, StrokeCap.Round)
    }
}

@Composable
fun IconHome(color: Color, modifier: Modifier = Modifier, size: Dp = 22.dp, weight: Float = 1.6f) {
    Canvas(modifier.then(Modifier.iconSize(size))) {
        strokePolyline(listOf(Offset(4f, 11.5f), Offset(12f, 5f), Offset(20f, 11.5f), Offset(20f, 19f), Offset(16f, 19f), Offset(16f, 13f), Offset(8f, 13f), Offset(8f, 19f), Offset(4f, 19f), Offset(4f, 11.5f)), color, weight)
    }
}

private fun Modifier.iconSize(size: Dp): Modifier = this.size(size)
