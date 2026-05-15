package com.arduia.expense.ui.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.designsystem.component.card.DashboardCard
import com.arduia.expense.designsystem.theme.ProExpenseTheme

@Composable
fun SpendGraphCard(
    weeklyData: List<Float>,
    modifier: Modifier = Modifier,
) {
    DashboardCard(modifier = modifier) {
        SpendGraph(data = weeklyData)
    }
}

@Composable
fun SpendGraph(
    data: List<Float>,
    modifier: Modifier = Modifier,
) {
    val barColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
    ) {
        val maxValue = data.maxOrNull()?.takeIf { it > 0f } ?: 1f
        val barWidth = size.width / (data.size * 2f)
        val spacing = barWidth
        data.forEachIndexed { index, value ->
            val x = spacing + index * (barWidth + spacing) + barWidth / 2
            val barHeight = (value / maxValue) * size.height
            drawLine(
                color = trackColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = barWidth,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = barColor,
                start = Offset(x, size.height),
                end = Offset(x, size.height - barHeight),
                strokeWidth = barWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSpendGraphCard() {
    ProExpenseTheme {
        SpendGraphCard(weeklyData = listOf(100f, 250f, 80f, 320f, 150f, 200f, 90f))
    }
}
