package com.arduia.expense.feature.currency.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.arduia.expense.ui.design.IconCheck
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProfileEyebrow
import com.arduia.expense.ui.design.ProfileSetupHeader
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProfileCurrencyScreen(
    selectedCode: String,
    onCurrencySelected: (String) -> Unit,
    onStartTracking: () -> Unit,
    onShowAllCurrencies: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        ProfileSetupHeader(step = 2, totalSteps = 2, onSkip = onSkip)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.space24)
                .padding(top = dims.space8),
        ) {
            ProfileEyebrow(step = 2, totalSteps = 2)
            Text(
                text = "Pick your home currency",
                style = typography.screenTitle,
                color = colors.onSurface,
                modifier = Modifier.padding(top = dims.space12),
            )
            Text(
                text = "All entries default to this. You can still log in any currency per-expense.",
                style = typography.subtitle,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dims.space12),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.space24, vertical = dims.space12),
            horizontalArrangement = Arrangement.End,
        ) {
            ProButton(
                text = "All",
                onClick = onShowAllCurrencies,
                variant = ProButtonVariant.Ghost,
                size = ProButtonSize.Md,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dims.space20),
            verticalArrangement = Arrangement.spacedBy(dims.space8),
        ) {
            profileCurrencyOptions.forEach { currency ->
                CurrencyRow(
                    currency = currency,
                    selected = currency.code == selectedCode,
                    onClick = { onCurrencySelected(currency.code) },
                )
            }
        }

        ProButton(
            text = "Start tracking",
            onClick = onStartTracking,
            fillMaxWidth = true,
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
            .background(if (selected) colors.primaryTint.copy(alpha = 0.35f) else colors.surface)
            .border(
                width = dims.buttonBorderWidth,
                color = if (selected) colors.primary else colors.line,
                shape = shapes.listRow,
            )
            .clickable(interactionSource = interaction, indication = ripple(), onClick = onClick)
            .padding(horizontal = dims.space16, vertical = dims.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dims.space16),
    ) {
        Box(
            modifier = Modifier
                .size(dims.currencyBadgeSize)
                .clip(CircleShape)
                .background(if (selected) colors.surface else colors.paperAlt),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = currency.symbol,
                style = typography.currencySymbol,
                color = if (selected) colors.primaryDeep else colors.onSurfaceMuted,
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
                color = colors.onSurfaceMuted,
            )
        }
        if (selected) {
            IconCheck(color = colors.primary, size = dims.iconSizeDefault)
        }
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
            onSkip = {},
        )
    }
}
