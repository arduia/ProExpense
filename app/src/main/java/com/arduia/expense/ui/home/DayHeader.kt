package com.arduia.expense.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun DayHeader(
    title: String,
    totalLabel: String?,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dims.space8),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = typography.sectionHead,
            color = colors.onSurface,
        )
        if (totalLabel != null) {
            Text(
                text = totalLabel,
                style = typography.monoCaption,
                color = colors.muted,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 370)
@Composable
private fun DayHeaderPreview() {
    ProExpenseTheme {
        DayHeader(title = "Today", totalLabel = "$42.40")
    }
}
