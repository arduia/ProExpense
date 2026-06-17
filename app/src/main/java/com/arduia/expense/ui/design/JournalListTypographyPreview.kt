package com.arduia.expense.ui.design

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun JournalListTypographyPreview(
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "DIARY VIEW",
            style = typography.eyebrow,
            color = colors.muted,
        )
        Text(
            text = "Journal",
            style = typography.screenTitle,
            color = colors.ink,
        )
        DayHeader(title = "Today · May 25", total = "$42")
        TransactionRow(
            note = "Lunch with M.",
            meta = "Food · 12:30 PM",
            amount = "$12.40",
            category = DefaultCategories.Food,
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 320)
@Composable
private fun JournalListTypographyPreviewPreview() {
    ProExpenseTheme {
        ProExpensePaperBackground {
            JournalListTypographyPreview()
        }
    }
}
