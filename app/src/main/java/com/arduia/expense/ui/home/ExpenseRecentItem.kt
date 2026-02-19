package com.arduia.expense.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import com.arduia.design.components.ProExpenseCard
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

@Composable
fun ExpenseRecentItem(
    categoryName: String = "Education",
    amount: String = "900",
    currencySymbol: String = "$",
    date: String = "Today",
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface) // Selectable background if needed
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle Icon
            ProExpenseCard(
                modifier = Modifier
                    .size(55.dp)
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp), // Margins from XML
                shape = MaterialTheme.shapes.medium, // Circular
                containerColor = Color(0xFFB3E5FC), // Light Blue from XML #b3e5fc
                elevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_household),
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp),
                        tint = Color.DarkGray // dark_gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Name and Date
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelMedium, // Caption
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Amount
            Row(
                modifier = Modifier.padding(end = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleMedium, // CurrencySmall
                     fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = currencySymbol,
                    style = MaterialTheme.typography.bodySmall, // Body2 used for symbol
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseRecentItemPreview() {
    ProExpenseTheme {
        ExpenseRecentItem()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ExpenseRecentItemDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        ExpenseRecentItem()
    }
}
