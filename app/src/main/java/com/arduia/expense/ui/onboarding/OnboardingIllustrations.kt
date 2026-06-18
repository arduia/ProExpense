package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.theme.ProExpensePalette
import com.arduia.expense.ui.theme.ProExpenseSans
import com.arduia.expense.ui.theme.ProExpenseTheme

// Onboarding illustrations — composed scene drawings translated from
// pro-expense-finance-tracker/project/onboarding-illustrations.jsx.
// Flat, rounded, ~2px dark-gray line work, blue fills, white surfaces over a
// soft blue-50 backdrop. Authored on a 240×200 grid.

private object Illo {
    val ink = ProExpensePalette.Ink
    val blue = ProExpensePalette.Primary
    val blueDeep = ProExpensePalette.PrimaryDeep
    val blue300 = ProExpensePalette.PrimarySoft
    val blue100 = ProExpensePalette.PrimaryTint
    val blue50 = ProExpensePalette.FoodTint
    val white = ProExpensePalette.Surface
    val gray = ProExpensePalette.Muted
    val grayLine = ProExpensePalette.Muted2
}

private const val VB_W = 240f
private const val VB_H = 200f

/** Scaled drawing helper that maps the 240×200 viewBox onto the canvas. */
private class IlloDraw(val ds: DrawScope, val tm: TextMeasurer) {
    val sx = ds.size.width / VB_W
    val sy = ds.size.height / VB_H
    val s = sx // uniform scale for radii / stroke widths

    private fun pt(x: Float, y: Float) = Offset(x * sx, y * sy)

    fun fillCircle(cx: Float, cy: Float, r: Float, color: Color) =
        ds.drawCircle(color, r * s, pt(cx, cy))

    fun strokeCircle(cx: Float, cy: Float, r: Float, color: Color, w: Float) =
        ds.drawCircle(color, r * s, pt(cx, cy), style = Stroke(w * s))

    fun line(
        x1: Float, y1: Float, x2: Float, y2: Float, color: Color, w: Float,
        cap: StrokeCap = StrokeCap.Round, dash: Boolean = false,
    ) = ds.drawLine(
        color, pt(x1, y1), pt(x2, y2), w * s, cap,
        pathEffect = if (dash) PathEffect.dashPathEffect(floatArrayOf(4f * s, 4f * s)) else null,
    )

    fun fillRoundRect(x: Float, y: Float, w: Float, h: Float, r: Float, color: Color) =
        ds.drawRoundRect(
            color, pt(x, y), Size(w * sx, h * sy),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * s, r * s),
        )

    fun strokeRoundRect(x: Float, y: Float, w: Float, h: Float, r: Float, color: Color, sw: Float) =
        ds.drawRoundRect(
            color, pt(x, y), Size(w * sx, h * sy),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * s, r * s),
            style = Stroke(sw * s),
        )

    fun ellipse(cx: Float, cy: Float, rx: Float, ry: Float, color: Color) =
        ds.drawOval(color, pt(cx - rx, cy - ry), Size(rx * 2 * sx, ry * 2 * sy))

    fun arcStroke(cx: Float, cy: Float, r: Float, start: Float, sweep: Float, color: Color, w: Float) =
        ds.drawArc(
            color, start, sweep, useCenter = false,
            topLeft = pt(cx - r, cy - r), size = Size(r * 2 * sx, r * 2 * sy),
            style = Stroke(w * s, cap = StrokeCap.Round),
        )

    fun arcFill(cx: Float, cy: Float, r: Float, start: Float, sweep: Float, color: Color) =
        ds.drawArc(
            color, start, sweep, useCenter = true,
            topLeft = pt(cx - r, cy - r), size = Size(r * 2 * sx, r * 2 * sy),
            style = Fill,
        )

    /** Polyline / polygon from (x,y) pairs in viewBox units. */
    fun poly(
        points: List<Pair<Float, Float>>, fill: Color? = null,
        stroke: Color? = null, sw: Float = 3f, close: Boolean = true,
    ) {
        val path = Path()
        points.forEachIndexed { i, (x, y) ->
            if (i == 0) path.moveTo(x * sx, y * sy) else path.lineTo(x * sx, y * sy)
        }
        if (close) path.close()
        fill?.let { ds.drawPath(path, it, style = Fill) }
        stroke?.let { ds.drawPath(path, it, style = Stroke(sw * s, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)) }
    }

    enum class Anchor { Start, Middle, End }

    fun text(cx: Float, cy: Float, str: String, size: Float, color: Color, weight: FontWeight, anchor: Anchor = Anchor.Middle) {
        val layout = tm.measure(
            AnnotatedString(str),
            style = TextStyle(fontFamily = ProExpenseSans, fontWeight = weight, fontSize = (size * s / ds.density).sp, color = color),
        )
        val w = layout.size.width
        val h = layout.size.height
        val left = when (anchor) {
            Anchor.Start -> cx * sx
            Anchor.Middle -> cx * sx - w / 2f
            Anchor.End -> cx * sx - w
        }
        ds.drawText(layout, color, topLeft = Offset(left, cy * sy - h / 2f))
    }
}

@Composable
private fun IlloCanvas(modifier: Modifier, draw: IlloDraw.() -> Unit) {
    val tm = rememberTextMeasurer()
    Canvas(modifier) { IlloDraw(this, tm).draw() }
}

private fun IlloDraw.backdrop() {
    ellipse(120f, 108f, 92f, 78f, Illo.blue50)
}

// 01 — WELCOME: an open notebook with a coin and a friendly check.
@Composable
fun IlloWelcome(modifier: Modifier = Modifier) = IlloCanvas(modifier) {
    backdrop()
    fillCircle(186f, 44f, 9f, Illo.blue100)
    fillCircle(44f, 150f, 6f, Illo.blue100)
    // notebook
    fillRoundRect(62f, 58f, 116f, 92f, 10f, Illo.white)
    strokeRoundRect(62f, 58f, 116f, 92f, 10f, Illo.ink, 3f)
    line(120f, 58f, 120f, 150f, Illo.ink, 3f, cap = StrokeCap.Butt)
    line(74f, 80f, 108f, 80f, Illo.grayLine, 3f)
    line(74f, 94f, 108f, 94f, Illo.grayLine, 3f)
    line(74f, 108f, 100f, 108f, Illo.grayLine, 3f)
    // hello check
    fillCircle(150f, 92f, 16f, Illo.blue)
    poly(listOf(143f to 92f, 148f to 97f, 157f to 87f), stroke = Illo.white, sw = 3.4f, close = false)
    line(132f, 118f, 166f, 118f, Illo.grayLine, 3f)
    line(132f, 130f, 156f, 130f, Illo.grayLine, 3f)
    // coin
    fillCircle(58f, 138f, 18f, Illo.blue300)
    strokeCircle(58f, 138f, 18f, Illo.ink, 3f)
    text(58f, 138f, "$", 22f, Illo.white, FontWeight.Bold)
    // sparkle
    line(180f, 120f, 180f, 132f, Illo.blue, 3f)
    line(174f, 126f, 186f, 126f, Illo.blue, 3f)
}

// 02 — QUICK LOG: a phone with a fast amount entry and a big +.
@Composable
fun IlloQuickLog(modifier: Modifier = Modifier) = IlloCanvas(modifier) {
    backdrop()
    line(30f, 78f, 56f, 78f, Illo.blue100, 5f)
    line(24f, 100f, 52f, 100f, Illo.blue100, 5f)
    line(32f, 122f, 56f, 122f, Illo.blue100, 5f)
    // phone
    fillRoundRect(84f, 46f, 86f, 124f, 16f, Illo.white)
    strokeRoundRect(84f, 46f, 86f, 124f, 16f, Illo.ink, 3f)
    // blue header (rounded top only — draw rounded then square off the bottom)
    fillRoundRect(84f, 46f, 86f, 40f, 16f, Illo.blue)
    fillRoundRect(84f, 70f, 86f, 16f, 0f, Illo.blue)
    text(127f, 66f, "$12", 20f, Illo.white, FontWeight.Bold)
    // category rows
    fillRoundRect(96f, 98f, 62f, 10f, 5f, Illo.blue100)
    fillRoundRect(96f, 114f, 44f, 10f, 5f, Illo.gray.copy(alpha = 0.4f))
    // big plus button
    fillCircle(150f, 150f, 22f, Illo.blue)
    strokeCircle(150f, 150f, 22f, Illo.white, 3f)
    line(150f, 140f, 150f, 160f, Illo.white, 3.6f)
    line(140f, 150f, 160f, 150f, Illo.white, 3.6f)
    // lightning
    poly(
        listOf(64f to 150f, 54f to 166f, 62f to 166f, 58f to 180f, 72f to 160f, 63f to 160f),
        fill = Illo.blue300, stroke = Illo.ink, sw = 2.4f,
    )
}

// 03 — SHARED COSTS: a bill split between three people.
@Composable
fun IlloSharedCosts(modifier: Modifier = Modifier) = IlloCanvas(modifier) {
    backdrop()
    // receipt with zig-zag bottom
    poly(
        listOf(
            98f to 48f, 148f to 48f, 148f to 110f,
            141f to 105f, 134f to 110f, 127f to 105f, 120f to 110f,
            113f to 105f, 106f to 110f, 99f to 105f, 98f to 110f,
        ),
        fill = Illo.white, stroke = Illo.ink, sw = 3f,
    )
    line(104f, 60f, 140f, 60f, Illo.grayLine, 3f)
    line(104f, 72f, 132f, 72f, Illo.grayLine, 3f)
    text(120f, 94f, "$120", 16f, Illo.blue, FontWeight.Bold)
    // three people fanning out from the receipt
    val people = listOf(60f to Illo.blue, 120f to Illo.blue300, 180f to Illo.blueDeep)
    people.forEach { (px, color) ->
        line(120f, 118f, px, 150f, Illo.blue, 2.6f, dash = true)
        fillCircle(px, 160f, 12f, color)
        strokeCircle(px, 160f, 12f, Illo.ink, 3f)
        fillCircle(px, 156f, 4f, Illo.white)
        arcFill(px, 168f, 7f, 180f, 180f, Illo.white)
        fillRoundRect(px - 16f, 176f, 32f, 13f, 6f, Illo.white)
        strokeRoundRect(px - 16f, 176f, 32f, 13f, 6f, Illo.ink, 2f)
        text(px, 183f, "$40", 8f, Illo.ink, FontWeight.Bold)
    }
}

// 04 — EVENT BUDGET: a destination flag and a progress ring.
@Composable
fun IlloEventBudget(modifier: Modifier = Modifier) = IlloCanvas(modifier) {
    backdrop()
    // flag pin
    line(74f, 60f, 74f, 150f, Illo.ink, 3f)
    poly(listOf(74f to 60f, 108f to 60f, 100f to 69f, 108f to 78f, 74f to 78f), fill = Illo.blue, stroke = Illo.ink, sw = 3f)
    // progress ring (62%)
    fillCircle(158f, 104f, 30f, Illo.white)
    strokeCircle(158f, 104f, 30f, Illo.grayLine, 9f)
    arcStroke(158f, 104f, 30f, -90f, 360f * 0.62f, Illo.blue, 9f)
    text(158f, 100f, "62%", 14f, Illo.ink, FontWeight.Bold)
    text(158f, 116f, "of budget", 8f, Illo.gray, FontWeight.Normal)
    // small calendar
    fillRoundRect(58f, 158f, 40f, 32f, 6f, Illo.white)
    strokeRoundRect(58f, 158f, 40f, 32f, 6f, Illo.ink, 3f)
    line(58f, 168f, 98f, 168f, Illo.ink, 3f, cap = StrokeCap.Butt)
    line(68f, 154f, 68f, 162f, Illo.ink, 3f)
    line(88f, 154f, 88f, 162f, Illo.ink, 3f)
    fillCircle(78f, 179f, 4f, Illo.blue)
    // sparkle
    line(150f, 52f, 150f, 62f, Illo.blue300, 3f)
    line(145f, 57f, 155f, 57f, Illo.blue300, 3f)
}

// 05 — JOURNAL: stacked day-cards like a diary.
@Composable
fun IlloJournal(modifier: Modifier = Modifier) = IlloCanvas(modifier) {
    backdrop()
    // back card
    fillRoundRect(74f, 50f, 108f, 44f, 10f, Illo.white.copy(alpha = 0.7f))
    strokeRoundRect(74f, 50f, 108f, 44f, 10f, Illo.grayLine.copy(alpha = 0.7f), 3f)
    // middle card
    fillRoundRect(66f, 74f, 108f, 50f, 10f, Illo.white)
    strokeRoundRect(66f, 74f, 108f, 50f, 10f, Illo.ink, 3f)
    fillCircle(84f, 92f, 7f, Illo.blue100)
    line(98f, 88f, 140f, 88f, Illo.grayLine, 3f)
    text(160f, 90f, "$12", 11f, Illo.blue, FontWeight.Bold, IlloDraw.Anchor.End)
    line(98f, 104f, 124f, 104f, Illo.grayLine, 3f)
    // front card
    fillRoundRect(58f, 104f, 116f, 56f, 12f, Illo.white)
    strokeRoundRect(58f, 104f, 116f, 56f, 12f, Illo.ink, 3f)
    text(70f, 120f, "TODAY", 9f, Illo.ink, FontWeight.Bold, IlloDraw.Anchor.Start)
    fillCircle(78f, 140f, 8f, Illo.blue)
    poly(listOf(74f to 140f, 77f to 143f, 83f to 137f), stroke = Illo.white, sw = 2.4f, close = false)
    line(94f, 136f, 138f, 136f, Illo.grayLine, 3f)
    line(94f, 146f, 120f, 146f, Illo.grayLine, 3f)
    text(162f, 140f, "$42", 12f, Illo.blue, FontWeight.Bold, IlloDraw.Anchor.End)
}

/** The five onboarding illustrations, indexed by slide (0-based). */
val onboardingIllustrations: List<@Composable (Modifier) -> Unit> = listOf(
    { m -> IlloWelcome(m) },
    { m -> IlloQuickLog(m) },
    { m -> IlloSharedCosts(m) },
    { m -> IlloEventBudget(m) },
    { m -> IlloJournal(m) },
)

@Preview(showBackground = true, widthDp = 280, heightDp = 220)
@Composable
private fun OnboardingIllustrationQuickLogPreview() {
  IlloQuickLog(
    Modifier
      .height(ProExpenseTheme.dimensions.onboardingIllustrationHeight)
      .padding(ProExpenseTheme.dimensions.space16),
  )
}
