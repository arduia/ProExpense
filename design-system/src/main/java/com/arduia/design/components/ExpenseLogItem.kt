package com.arduia.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.R
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.design.theme.currencySmall

import com.arduia.design.theme.category_icon_background
import com.arduia.design.theme.category_icon_tint

@Composable
fun ExpenseLogItem(
    title: String,
    date: String,
    amount: String,
    currencySymbol: String,
    categoryIcon: Int, // Resource ID
    categoryColor: Color = category_icon_background,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    StandardCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        containerColor = MaterialTheme.colorScheme.surface,
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIcon(
                painter = painterResource(id = categoryIcon),
                backgroundColor = categoryColor,
                iconTint = category_icon_tint,
                size = 56.dp,
                iconSize = 30.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " $currencySymbol",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewExpenseLogItem() {
    ProExpenseTheme {
        ExpenseLogItem(
            title = "Sample Name",
            date = "2025 Aug 15",
            amount = "123,500",
            currencySymbol = "USD",
            categoryIcon = R.drawable.ic_launcher_foreground,
            onItemClick = {}
        )
    }
}
