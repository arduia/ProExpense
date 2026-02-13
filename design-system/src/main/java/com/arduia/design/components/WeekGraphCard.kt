package com.arduia.design.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.theme.ProExpenseTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.ui.res.stringResource
import com.arduia.design.R

import com.arduia.design.theme.statistic_progress_blue

@Composable
fun WeekGraphCard(
    dateRange: String,
    days: List<String> = listOf("SU", "MO", "TU", "WE", "TH", "FR", "SA"),
    values: List<Float>, // Normalized values 0.0 to 1.0 (empty or zeros for null state)
    modifier: Modifier = Modifier
) {
    StandardCard(
        modifier = modifier
            .fillMaxWidth(),
        elevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.label_expenses_in_this_week),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = dateRange.uppercase(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 2.dp, bottom = 24.dp)
            )

            // Simple pseudo-graph implementation matching the screenshot style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEachIndexed { index, day ->
                    val value = values.getOrElse(index) { 0f }.coerceIn(0f, 1f)
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        if (value > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.3f)
                                    .fillMaxHeight(value)
                                    .background(
                                        color = statistic_progress_blue.copy(alpha = 0.8f),
                                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                    )
                            )
                        } else {
                            // Empty state placeholder height if needed, but screenshot shows nothing
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            color = statistic_progress_blue.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewWeekGraphCard() {
    ProExpenseTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            WeekGraphCard(
                dateRange = "Feb 8 - 15",
                values = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeekGraphCard(
                dateRange = "Feb 8 - 15",
                values = listOf(0.2f, 0.5f, 0.8f, 0.3f, 1.0f, 0.6f, 0.4f)
            )
        }
    }
}
