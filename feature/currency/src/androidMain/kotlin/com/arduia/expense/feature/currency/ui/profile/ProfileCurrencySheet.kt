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
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.BottomSheet
import com.arduia.expense.ui.design.IconClose
import com.arduia.expense.ui.design.ProfileEyebrow
import com.arduia.expense.ui.design.ProfileSetupHeader
import com.arduia.expense.ui.design.SearchField
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProfileCurrencySheet(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCurrencySelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes
    val filtered = profileCurrencyOptions.filter {
        searchQuery.isBlank() ||
            it.code.contains(searchQuery, ignoreCase = true) ||
            it.name.contains(searchQuery, ignoreCase = true)
    }

    BottomSheet(onDismiss = onDismiss, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.space24, vertical = dims.space8),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "All currencies",
                style = typography.sheetTitle,
                color = colors.onSurface,
            )
            Box(
                modifier = Modifier
                    .size(dims.iconButtonSize)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false),
                        onClick = onDismiss,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                IconClose(color = colors.onSurfaceMuted, size = dims.iconSizeDefault)
            }
        }

        SearchField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = "Search currency…",
            modifier = Modifier
                .padding(horizontal = dims.space16)
                .padding(bottom = dims.space8),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dims.space16, vertical = dims.space8),
            verticalArrangement = Arrangement.spacedBy(dims.space8),
        ) {
            filtered.forEach { currency ->
                SheetCurrencyRow(
                    currency = currency,
                    onClick = { onCurrencySelected(currency.code) },
                )
            }
        }
    }
}

@Composable
fun ProfileCurrencySheetPreviewHost(
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dims = ProExpenseTheme.dimensions

    Box(modifier = modifier.fillMaxSize().background(colors.paper)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ProfileSetupHeader(step = 2, totalSteps = 2, onSkip = {})
            Column(
                modifier = Modifier
                    .padding(horizontal = dims.space24)
                    .padding(top = dims.space8),
            ) {
                ProfileEyebrow(step = 2, totalSteps = 2)
                Text(
                    text = "Pick your home currency",
                    style = ProExpenseTheme.typography.screenTitle,
                    color = colors.onSurface,
                    modifier = Modifier.padding(top = dims.space12),
                )
            }
            Box(modifier = Modifier.weight(1f))
        }
        ProfileCurrencySheet(
            searchQuery = "",
            onSearchQueryChange = {},
            onCurrencySelected = {},
            onDismiss = {},
        )
    }
}

@Composable
private fun SheetCurrencyRow(
    currency: ProfileCurrencyOption,
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
            .border(1.dp, colors.line, shapes.listRow)
            .background(colors.surface)
            .clickable(interactionSource = interaction, indication = ripple(), onClick = onClick)
            .padding(horizontal = dims.space16, vertical = dims.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dims.space16),
    ) {
        Box(
            modifier = Modifier
                .size(dims.currencyBadgeSize)
                .clip(CircleShape)
                .background(colors.paperAlt),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = currency.symbol,
                style = typography.currencySymbol,
                color = colors.onSurfaceMuted,
                textAlign = TextAlign.Center,
            )
        }
        Column {
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
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun ProfileCurrencySheetPreview() {
    ProExpenseTheme {
        ProfileCurrencySheetPreviewHost()
    }
}
