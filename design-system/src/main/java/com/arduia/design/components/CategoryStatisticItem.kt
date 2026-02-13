package com.arduia.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.theme.ProExpenseTheme

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import com.arduia.design.theme.statistic_progress_blue

@Composable
fun CategoryStatisticItem(
    categoryName: String,
    percentage: Float, // 0.0 to 1.0
    percentageText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(
            text = categoryName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = { percentage },
                modifier = Modifier
                    .weight(1f)
                    .height(18.dp)
                    .clip(CircleShape),
                color = statistic_progress_blue,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = percentageText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.width(50.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
    }
}

@Preview
@Composable
fun PreviewCategoryStatisticItem() {
    ProExpenseTheme {
        StandardCard {
            CategoryStatisticItem(
                categoryName = "Food & Dining",
                percentage = 0.45f,
                percentageText = "45%"
            )
        }
    }
}
