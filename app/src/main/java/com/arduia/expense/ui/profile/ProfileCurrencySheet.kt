package com.arduia.expense.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.IconSearch
import com.arduia.expense.ui.design.ProLinearProgress
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Currency picker bottom sheet — mirrors `AndProfileCurrencySheet`.
 */
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
    val scrimInteraction = remember { MutableInteractionSource() }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = scrimInteraction,
                    indication = null,
                    onClick = onDismiss,
                )
                .background(colors.scrim),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(dims.sheetMaxHeight)
                .clip(shapes.sheet)
                .background(colors.surface),
        ) {
            Box(
                modifier = Modifier
                    .padding(top = dims.sheetHandlePaddingTop, bottom = dims.sheetHandlePaddingBottom)
                    .align(Alignment.CenterHorizontally)
                    .size(width = dims.sheetHandleWidth, height = dims.sheetHandleHeight)
                    .clip(shapes.pill)
                    .background(colors.outlineVariant),
            )

            Text(
                text = "All currencies",
                style = typography.sheetTitle,
                color = colors.onSurface,
                modifier = Modifier.padding(horizontal = dims.space24),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dims.space16, vertical = dims.space8)
                    .height(dims.searchFieldHeight)
                    .clip(shapes.pill)
                    .background(colors.surfaceVariant)
                    .padding(horizontal = dims.space18),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dims.space12),
            ) {
                IconSearch(color = colors.onSurfaceVariant)
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    textStyle = typography.fieldValue.copy(color = colors.onSurface),
                    cursorBrush = SolidColor(colors.primary),
                    singleLine = true,
                    decorationBox = { inner ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search currency…",
                                style = typography.fieldValue,
                                color = colors.onSurfaceVariant,
                            )
                        }
                        inner()
                    },
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = dims.space8, vertical = dims.space6),
                verticalArrangement = Arrangement.spacedBy(dims.space2),
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
}

/** Full-screen host for the currency-sheet design artboard (dimmed base + sheet). */
@Composable
fun ProfileCurrencySheetPreviewHost(
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors

    Box(modifier = modifier.fillMaxSize().background(colors.surface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ProTopBar(title = "Home currency")
            ProLinearProgress(
                progress = 1f,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
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
            .padding(horizontal = dims.space8, vertical = dims.space6)
            .clip(shapes.listRow)
            .clickable(interactionSource = interaction, indication = ripple(), onClick = onClick)
            .padding(horizontal = dims.space16, vertical = dims.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dims.space16),
    ) {
        Box(
            modifier = Modifier
                .size(dims.currencyBadgeSize)
                .clip(CircleShape)
                .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = currency.symbol,
                style = typography.currencySymbol,
                color = colors.onSurfaceVariant,
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
                color = colors.onSurfaceVariant,
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
