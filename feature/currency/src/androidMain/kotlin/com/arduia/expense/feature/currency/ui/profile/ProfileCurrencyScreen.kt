package com.arduia.expense.feature.currency.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.design.ProFilledButton
import com.arduia.expense.ui.design.ProLinearProgress
import com.arduia.expense.ui.design.ProRadio
import com.arduia.expense.ui.design.ProTextButton
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProfileCurrencyScreen(
    selectedCode: String,
    onCurrencySelected: (String) -> Unit,
    onStartTracking: () -> Unit,
    onShowAllCurrencies: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        ProTopBar(
            title = "Home currency",
            trailing = {
                ProTextButton(
                    text = "All",
                    onClick = onShowAllCurrencies,
                    modifier = Modifier.padding(end = dims.space8),
                )
            },
        )
        ProLinearProgress(
            progress = 1f,
            modifier = Modifier.padding(horizontal = dims.space16),
        )

        Text(
            text = "All entries default to this. You can still log in any currency per-expense.",
            style = typography.body,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = dims.space24, vertical = dims.space20),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dims.space12),
            verticalArrangement = Arrangement.spacedBy(dims.space2),
        ) {
            profileCurrencyOptions.forEach { currency ->
                CurrencyRow(
                    currency = currency,
                    selected = currency.code == selectedCode,
                    onClick = { onCurrencySelected(currency.code) },
                )
            }
        }

        ProFilledButton(
            text = "Start tracking",
            onClick = onStartTracking,
            modifier = Modifier
                .padding(horizontal = dims.space20)
                .padding(top = dims.space12, bottom = dims.space22),
        )
    }
}

@Composable
private fun CurrencyRow(
    currency: ProfileCurrencyOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shapes.listRow)
            .background(if (selected) colors.primaryContainer else colors.surface)
            .clickable(interactionSource = interaction, indication = ripple(), onClick = onClick)
            .padding(horizontal = dims.space12, vertical = dims.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dims.space16),
    ) {
        Box(
            modifier = Modifier
                .size(dims.currencyBadgeSize)
                .clip(CircleShape)
                .background(if (selected) colors.surface else colors.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = currency.symbol,
                style = typography.currencySymbol,
                color = if (selected) colors.primaryDeep else colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = currency.code,
                style = typography.currencyCode,
                color = colors.onSurface,
            )
            Text(
                text = currency.name,
                style = typography.currencyName,
                color = colors.onSurfaceVariant,
            )
        }
        ProRadio(selected = selected)
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun ProfileCurrencyScreenPreview() {
    ProExpenseTheme {
        ProfileCurrencyScreen(
            selectedCode = "USD",
            onCurrencySelected = {},
            onStartTracking = {},
            onShowAllCurrencies = {},
        )
    }
}
