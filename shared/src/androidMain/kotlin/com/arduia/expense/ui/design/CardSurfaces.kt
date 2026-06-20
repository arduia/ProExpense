package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun SpentTodayCard(
    eyebrow: String,
    amount: String,
    modifier: Modifier = Modifier,
    showSparkline: Boolean = true,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.card
    val cardElevation = ProExpenseTheme.elevation.card.firstOrNull()

    Column(
        modifier = modifier
            .then(
                if (cardElevation != null) {
                    Modifier.shadow(
                        elevation = cardElevation.blur,
                        shape = shape,
                        spotColor = cardElevation.color,
                        ambientColor = cardElevation.color,
                    )
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .border(BorderStroke(1.dp, colors.line), shape)
            .background(colors.surface)
            .padding(dimens.cardPadding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(
                    text = eyebrow.uppercase(),
                    style = typography.tabTimestamp,
                    color = colors.onSurfaceMuted,
                )
                Text(
                    text = amount,
                    style = typography.summaryAmount,
                    color = colors.onSurface,
                    modifier = Modifier.padding(top = dimens.space4),
                )
            }
            if (showSparkline) {
                SparklinePlaceholder(
                    modifier = Modifier
                        .size(width = 86.dp, height = 40.dp)
                        .padding(top = dimens.space4),
                )
            }
        }
    }
}

@Composable
private fun SparklinePlaceholder(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(2f, size.height * 0.75f)
            lineTo(size.width * 0.16f, size.height * 0.55f)
            lineTo(size.width * 0.30f, size.height * 0.65f)
            lineTo(size.width * 0.44f, size.height * 0.35f)
            lineTo(size.width * 0.58f, size.height * 0.45f)
            lineTo(size.width * 0.72f, size.height * 0.25f)
            lineTo(size.width * 0.86f, size.height * 0.35f)
            lineTo(size.width * 0.98f, size.height * 0.15f)
        }
        drawPath(
            path = path,
            color = colors.primary,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1.6.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round,
            ),
        )
        drawCircle(
            color = colors.primary,
            radius = 3.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(size.width * 0.98f, size.height * 0.15f),
        )
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun SpentTodayCardPreview() {
    ProExpenseTheme {
        SpentTodayCard(
            eyebrow = "Spent today",
            amount = "$42.00",
            modifier = Modifier.padding(16.dp),
        )
    }
}
