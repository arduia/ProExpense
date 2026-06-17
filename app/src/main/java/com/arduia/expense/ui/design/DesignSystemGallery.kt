package com.arduia.expense.ui.design

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DesignSystemGallery(
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = "Design System",
            style = ProExpenseTheme.typography.screenTitle,
            color = colors.ink,
        )
        Text(
            text = "AMOUNT · USD",
            style = ProExpenseTheme.typography.eyebrow,
            color = colors.muted,
        )
        Text(
            text = "$1,240",
            style = ProExpenseTheme.typography.displayAmount,
            color = colors.primary,
        )

        ProExpenseCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Context header",
                style = ProExpenseTheme.typography.eyebrow,
                color = colors.muted,
            )
            Text(
                text = "$80.90",
                style = ProExpenseTheme.typography.sectionHead,
                color = colors.ink,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ProExpenseButton(text = "Primary", onClick = {})
            ProExpenseButton(text = "Sage", onClick = {}, variant = ProExpenseButtonVariant.Sage)
            ProExpenseButton(text = "Secondary", onClick = {}, variant = ProExpenseButtonVariant.Secondary)
            ProExpenseButton(text = "Ghost", onClick = {}, variant = ProExpenseButtonVariant.Ghost)
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DefaultCategories.all.take(4).forEach { category ->
                CategoryChip(
                    label = category.name,
                    selected = category == DefaultCategories.Food,
                    onClick = {},
                    accent = category.accent,
                )
            }
        }

        DayHeader(title = "Today · May 25", total = "$80.90")

        TransactionRow(
            note = "Lunch with M.",
            meta = "Food · 12:30 PM",
            amount = "$12.40",
            category = DefaultCategories.Food,
        )
        TransactionRow(
            note = "Movie · Dune",
            meta = "Entertainment · 08:10 PM",
            amount = "$18.00",
            category = DefaultCategories.Entertainment,
            eventTag = "Bali Trip",
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun DesignSystemGalleryPreview() {
    ProExpenseTheme {
        ProExpensePaperBackground {
            DesignSystemGallery()
        }
    }
}
