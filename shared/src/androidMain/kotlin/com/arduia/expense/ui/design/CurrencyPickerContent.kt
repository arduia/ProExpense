package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.arduia.expense.ui.theme.ProExpenseTheme

data class CurrencyOption(
    val code: String,
    val label: String,
)

val defaultCurrencyOptions = listOf(
    CurrencyOption("USD", "US Dollar"),
    CurrencyOption("EUR", "Euro"),
    CurrencyOption("GBP", "British Pound"),
    CurrencyOption("JPY", "Japanese Yen"),
    CurrencyOption("INR", "Indian Rupee"),
    CurrencyOption("AED", "UAE Dirham"),
    CurrencyOption("MMK", "Myanmar Kyat"),
)

@Composable
fun CurrencyPickerContent(
    options: List<CurrencyOption>,
    selectedCode: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        items(items = options, key = { it.code }) { option ->
            val selected = option.code == selectedCode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ProExpenseTheme.shapes.searchField)
                    .background(if (selected) colors.primarySoft else colors.surface)
                    .proClickable(onClick = { onSelected(option.code) }, shape = ProExpenseTheme.shapes.searchField)
                    .padding(horizontal = dimens.space14, vertical = dimens.space12),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = option.code,
                        style = typography.bodySemiBold,
                        color = colors.onSurface,
                    )
                    Text(
                        text = option.label,
                        style = typography.caption,
                        color = colors.onSurfaceMuted,
                    )
                }
                if (selected) {
                    ProIcon(
                        glyph = ProIconGlyph.Check,
                        contentDescription = null,
                        tint = colors.primary,
                    )
                }
            }
        }
    }
}
