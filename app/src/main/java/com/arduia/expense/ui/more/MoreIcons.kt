package com.arduia.expense.ui.more

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

// Stroke icons for the More / Settings flow, drawn on a normalised 0..1 grid with rounded caps and
// joins — one stroke language at ~1.6px weight (24px grid) per design/DESIGN-SYSTEM.md §3.

private const val DefaultStroke = 0.085f

private fun stroke(width: Float) = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)

@Composable
fun ChevronRightGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = DefaultStroke,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.40f, h * 0.28f)
            lineTo(w * 0.64f, h * 0.50f)
            lineTo(w * 0.40f, h * 0.72f)
        }
        drawPath(path, color, style = stroke(w * strokeWidthFraction))
    }
}

@Composable
fun ChevronLeftGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.10f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.62f, h * 0.26f)
            lineTo(w * 0.36f, h * 0.50f)
            lineTo(w * 0.62f, h * 0.74f)
        }
        drawPath(path, color, style = stroke(w * strokeWidthFraction))
    }
}

@Composable
fun PlusGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.10f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = w * strokeWidthFraction
        drawLine(color, Offset(w * 0.5f, h * 0.22f), Offset(w * 0.5f, h * 0.78f), sw, StrokeCap.Round)
        drawLine(color, Offset(w * 0.22f, h * 0.5f), Offset(w * 0.78f, h * 0.5f), sw, StrokeCap.Round)
    }
}

/** Reports — three rising bars. */
@Composable
fun BarChartGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.095f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = w * strokeWidthFraction
        drawLine(color, Offset(w * 0.28f, h * 0.72f), Offset(w * 0.28f, h * 0.56f), sw, StrokeCap.Round)
        drawLine(color, Offset(w * 0.5f, h * 0.72f), Offset(w * 0.5f, h * 0.40f), sw, StrokeCap.Round)
        drawLine(color, Offset(w * 0.72f, h * 0.72f), Offset(w * 0.72f, h * 0.28f), sw, StrokeCap.Round)
    }
}

/** Debt tracker — two overlapping coins. */
@Composable
fun CoinsGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.08f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val s = stroke(w * strokeWidthFraction)
        drawCircle(color, radius = w * 0.20f, center = Offset(w * 0.40f, h * 0.40f), style = s)
        drawCircle(color, radius = w * 0.20f, center = Offset(w * 0.60f, h * 0.60f), style = s)
    }
}

/** Shared costs — a node splitting into two branches. */
@Composable
fun SplitGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.08f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val s = stroke(w * strokeWidthFraction)
        val r = w * 0.10f
        drawCircle(color, radius = r, center = Offset(w * 0.30f, h * 0.50f), style = s)
        drawCircle(color, radius = r, center = Offset(w * 0.72f, h * 0.30f), style = s)
        drawCircle(color, radius = r, center = Offset(w * 0.72f, h * 0.70f), style = s)
        drawLine(color, Offset(w * 0.40f, h * 0.46f), Offset(w * 0.62f, h * 0.32f), w * strokeWidthFraction, StrokeCap.Round)
        drawLine(color, Offset(w * 0.40f, h * 0.54f), Offset(w * 0.62f, h * 0.68f), w * strokeWidthFraction, StrokeCap.Round)
    }
}

/** Category list — a tag with a punch hole. */
@Composable
fun TagGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.08f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val s = stroke(w * strokeWidthFraction)
        val path = Path().apply {
            moveTo(w * 0.50f, h * 0.24f)
            lineTo(w * 0.76f, h * 0.24f)
            lineTo(w * 0.76f, h * 0.50f)
            lineTo(w * 0.50f, h * 0.76f)
            lineTo(w * 0.24f, h * 0.50f)
            close()
        }
        drawPath(path, color, style = s)
        drawCircle(color, radius = w * 0.05f, center = Offset(w * 0.62f, h * 0.38f), style = s)
    }
}

/** Currency — a circle with a plus, mirroring the add-currency glyph in the spec. */
@Composable
fun CirclePlusGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.075f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = w * strokeWidthFraction
        drawCircle(color, radius = w * 0.30f, center = Offset(w * 0.5f, h * 0.5f), style = stroke(sw))
        drawLine(color, Offset(w * 0.5f, h * 0.35f), Offset(w * 0.5f, h * 0.65f), sw, StrokeCap.Round)
        drawLine(color, Offset(w * 0.35f, h * 0.5f), Offset(w * 0.65f, h * 0.5f), sw, StrokeCap.Round)
    }
}

/** Monthly budget — a note with ruled lines. */
@Composable
fun DocLinesGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.075f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = w * strokeWidthFraction
        val s = stroke(sw)
        val path = Path().apply {
            moveTo(w * 0.30f, h * 0.22f)
            lineTo(w * 0.70f, h * 0.22f)
            lineTo(w * 0.70f, h * 0.78f)
            lineTo(w * 0.30f, h * 0.78f)
            close()
        }
        drawPath(path, color, style = s)
        drawLine(color, Offset(w * 0.39f, h * 0.40f), Offset(w * 0.61f, h * 0.40f), sw, StrokeCap.Round)
        drawLine(color, Offset(w * 0.39f, h * 0.52f), Offset(w * 0.61f, h * 0.52f), sw, StrokeCap.Round)
        drawLine(color, Offset(w * 0.39f, h * 0.64f), Offset(w * 0.52f, h * 0.64f), sw, StrokeCap.Round)
    }
}

/** PIN authentication — an eye. */
@Composable
fun EyeGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.08f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val s = stroke(w * strokeWidthFraction)
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.50f)
            cubicTo(w * 0.34f, h * 0.30f, w * 0.66f, h * 0.30f, w * 0.82f, h * 0.50f)
            cubicTo(w * 0.66f, h * 0.70f, w * 0.34f, h * 0.70f, w * 0.18f, h * 0.50f)
            close()
        }
        drawPath(path, color, style = s)
        drawCircle(color, radius = w * 0.10f, center = Offset(w * 0.5f, h * 0.5f), style = s)
    }
}

/** Language — a globe. */
@Composable
fun GlobeGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.07f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val s = stroke(w * strokeWidthFraction)
        drawCircle(color, radius = w * 0.30f, center = Offset(w * 0.5f, h * 0.5f), style = s)
        drawLine(color, Offset(w * 0.20f, h * 0.50f), Offset(w * 0.80f, h * 0.50f), w * strokeWidthFraction, StrokeCap.Round)
        // Meridian — a vertical ellipse approximated with two arcs.
        drawArc(color, 90f, 180f, false, topLeft = Offset(w * 0.36f, h * 0.20f), size = Size(w * 0.28f, h * 0.60f), style = s)
        drawArc(color, 270f, 180f, false, topLeft = Offset(w * 0.36f, h * 0.20f), size = Size(w * 0.28f, h * 0.60f), style = s)
    }
}

/** Theme — a split contrast circle. */
@Composable
fun ThemeGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.075f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val s = stroke(w * strokeWidthFraction)
        val center = Offset(w * 0.5f, h * 0.5f)
        val r = w * 0.30f
        drawCircle(color, radius = r, center = center, style = s)
        val fill = Path().apply {
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(center = center, radius = r),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 180f,
                forceMoveTo = true,
            )
            close()
        }
        drawPath(fill, color)
    }
}

/** Export — a document with a folded corner. */
@Composable
fun FileGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.075f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val s = stroke(w * strokeWidthFraction)
        val path = Path().apply {
            moveTo(w * 0.32f, h * 0.20f)
            lineTo(w * 0.58f, h * 0.20f)
            lineTo(w * 0.70f, h * 0.32f)
            lineTo(w * 0.70f, h * 0.80f)
            lineTo(w * 0.32f, h * 0.80f)
            close()
        }
        drawPath(path, color, style = s)
        val fold = Path().apply {
            moveTo(w * 0.58f, h * 0.20f)
            lineTo(w * 0.58f, h * 0.32f)
            lineTo(w * 0.70f, h * 0.32f)
        }
        drawPath(fold, color, style = s)
    }
}

/** Clear data — a trash can. */
@Composable
fun TrashGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.075f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = w * strokeWidthFraction
        val s = stroke(sw)
        drawLine(color, Offset(w * 0.26f, h * 0.30f), Offset(w * 0.74f, h * 0.30f), sw, StrokeCap.Round)
        drawLine(color, Offset(w * 0.42f, h * 0.30f), Offset(w * 0.42f, h * 0.22f), sw, StrokeCap.Round)
        drawLine(color, Offset(w * 0.58f, h * 0.30f), Offset(w * 0.58f, h * 0.22f), sw, StrokeCap.Round)
        drawLine(color, Offset(w * 0.42f, h * 0.22f), Offset(w * 0.58f, h * 0.22f), sw, StrokeCap.Round)
        val body = Path().apply {
            moveTo(w * 0.32f, h * 0.30f)
            lineTo(w * 0.36f, h * 0.78f)
            lineTo(w * 0.64f, h * 0.78f)
            lineTo(w * 0.68f, h * 0.30f)
        }
        drawPath(body, color, style = s)
    }
}

// ---- Bottom-nav glyphs ----

@Composable
fun HomeGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.08f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val s = stroke(w * strokeWidthFraction)
        val roof = Path().apply {
            moveTo(w * 0.22f, h * 0.48f)
            lineTo(w * 0.5f, h * 0.24f)
            lineTo(w * 0.78f, h * 0.48f)
        }
        drawPath(roof, color, style = s)
        val body = Path().apply {
            moveTo(w * 0.30f, h * 0.44f)
            lineTo(w * 0.30f, h * 0.76f)
            lineTo(w * 0.70f, h * 0.76f)
            lineTo(w * 0.70f, h * 0.44f)
        }
        drawPath(body, color, style = s)
    }
}

@Composable
fun WalletGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.08f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val s = stroke(w * strokeWidthFraction)
        val body = Path().apply {
            moveTo(w * 0.26f, h * 0.32f)
            lineTo(w * 0.74f, h * 0.32f)
            lineTo(w * 0.74f, h * 0.72f)
            lineTo(w * 0.26f, h * 0.72f)
            close()
        }
        drawPath(body, color, style = s)
        drawCircle(color, radius = w * 0.045f, center = Offset(w * 0.63f, h * 0.52f), style = s)
    }
}

@Composable
fun JournalGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.08f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = w * strokeWidthFraction
        val s = stroke(sw)
        val body = Path().apply {
            moveTo(w * 0.30f, h * 0.24f)
            lineTo(w * 0.70f, h * 0.24f)
            lineTo(w * 0.70f, h * 0.76f)
            lineTo(w * 0.30f, h * 0.76f)
            close()
        }
        drawPath(body, color, style = s)
        drawLine(color, Offset(w * 0.30f, h * 0.36f), Offset(w * 0.70f, h * 0.36f), sw, StrokeCap.Round)
    }
}

@Composable
fun MoreDotsGlyph(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.08f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val r = w * 0.06f
        drawCircle(color, radius = r, center = Offset(w * 0.30f, h * 0.5f))
        drawCircle(color, radius = r, center = Offset(w * 0.50f, h * 0.5f))
        drawCircle(color, radius = r, center = Offset(w * 0.70f, h * 0.5f))
    }
}
