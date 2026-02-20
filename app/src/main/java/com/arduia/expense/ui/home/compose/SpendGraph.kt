package com.arduia.expense.ui.home.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.design.theme.md_theme_light_primary
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

@Composable
fun SpendGraph(
    rates: Map<Int, Int>, // day (1-7) -> rate (0-100)
    modifier: Modifier = Modifier,
    graphColor: Color = md_theme_light_primary, // Or use MaterialTheme.colorScheme.primary
    dayColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val textMeasurer = rememberTextMeasurer()
    val layoutDirection = LocalLayoutDirection.current

    // Day names (SU, MO, TU, etc.)
    val dayNames = remember {
        val calendar = Calendar.getInstance()
        (1..7).map { day ->
            calendar.set(Calendar.DAY_OF_WEEK, day)
            // Get short day name and convert to uppercase, take first two letters
            DateFormatSymbols.getInstance(Locale.ENGLISH).shortWeekdays[day].take(2).uppercase()
        }
    }

    Canvas(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        val width = size.width
        val height = size.height

        val dayNameHeight = 30.dp.toPx()
        val graphHeight = height - dayNameHeight
        val bottomY = graphHeight

        val segmentWidth = width / 7f
        val segmentHalf = segmentWidth / 2f

        val dayTextStyle = TextStyle(
            color = dayColor,
            fontSize = 12.sp
        )

        val labelTextStyle = TextStyle(
            color = graphColor,
            fontSize = 15.sp
        )

        // Draw day names
        val dayPositionY = height - (dayNameHeight / 2)
        dayNames.forEachIndexed { index, name ->
            val day = index + 1
            val dayPositionX = if (layoutDirection == LayoutDirection.Rtl) {
                width - ((day * segmentWidth) - segmentHalf)
            } else {
                (day * segmentWidth) - segmentHalf
            }
            
            val textLayoutResult = textMeasurer.measure(text = name, style = dayTextStyle)
            val textWidth = textLayoutResult.size.width
            val textHeight = textLayoutResult.size.height
            
            drawText(
                textMeasurer = textMeasurer,
                text = name,
                style = dayTextStyle,
                topLeft = Offset(dayPositionX - (textWidth / 2f), dayPositionY - (textHeight / 2f))
            )
        }

        // Draw Graph Line
        val linePath = Path()
        var isFirstPoint = true
        var highestPointDay = -1
        var highestRate = -1f

        val validRates = (1..7).mapNotNull { day ->
            val rate = rates[day] ?: return@mapNotNull null
            if (rate !in 0..100) return@mapNotNull null
            
            // Calculate scaled rate similar to original: rate % 101 / 110f
            val calculatedRate = (rate % 101) / 110f
            Triple(day, rate, calculatedRate)
        }

        validRates.forEach { (day, originalRate, calculatedRate) ->
            val xPosition = if (layoutDirection == LayoutDirection.Rtl) {
                width - ((day * segmentWidth) - segmentHalf)
            } else {
                (day * segmentWidth) - segmentHalf
            }
            val yPosition = bottomY - (calculatedRate * graphHeight)

            // Draw point circle
            drawCircle(
                color = dayColor,
                radius = 1.5.dp.toPx(),
                center = Offset(xPosition, yPosition)
            )

            if (isFirstPoint) {
                linePath.moveTo(xPosition, yPosition)
                isFirstPoint = false
            } else {
                linePath.lineTo(xPosition, yPosition)
            }

            if (calculatedRate > highestRate) {
                highestRate = calculatedRate
                highestPointDay = day
            }
        }

        drawPath(
            path = linePath,
            color = graphColor,
            style = Stroke(
                width = 1.dp.toPx(),
                join = StrokeJoin.Round,
                cap = StrokeCap.Round
            )
        )

        // Draw Highest Vertical Line & Label
        if (highestPointDay != -1 && highestRate >= 0) {
            val commonX = if (layoutDirection == LayoutDirection.Rtl) {
                width - ((highestPointDay * segmentWidth) - segmentHalf)
            } else {
                (highestPointDay * segmentWidth) - segmentHalf
            }
            val startY = bottomY
            val endY = bottomY - (highestRate * graphHeight)

            val dotedPath = Path()
            dotedPath.moveTo(commonX, startY)
            dotedPath.lineTo(commonX, endY)

            drawPath(
                path = dotedPath,
                color = dayColor,
                style = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 5.dp.toPx()), 0f)
                )
            )

            // Label Position
            val labelTextSize = 15.dp.toPx()
            val labelPositionX = commonX + (labelTextSize)
            val labelPositionY = endY - (labelTextSize * 1.5f)

            // Check if there is space for label
            val betweenTopY = labelPositionY
            val betweenRightX = width - labelPositionX

            if (betweenTopY >= labelTextSize && betweenRightX >= (labelTextSize * 2)) {
                
                val percentageText = "${(highestRate * 100).toInt()}%" // Need original rate or calculated? Original logic used calculated * 100, which is slightly off from exact rate, but we follow original. Actually original uses point.rate * 100, where point.rate was calculatedRate.
                
                // Use the original calculatedRate multiplier to match legacy UI
                 val textToDraw = "${(highestRate * 100).toInt()}%"
                
                drawText(
                    textMeasurer = textMeasurer,
                    text = textToDraw,
                    style = labelTextStyle,
                    topLeft = Offset(labelPositionX, labelPositionY)
                )
            }
        }
    }
}
