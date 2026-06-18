package com.arduia.expense.feature.logging.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.logging.AmountInputLogic
import com.arduia.expense.feature.logging.customLogCategories
import com.arduia.expense.feature.logging.defaultLogCategories
import com.arduia.expense.ui.design.IconClose
import com.arduia.expense.ui.theme.ProExpenseTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddAmountScreen(
    amountInput: String,
    currencyCode: String,
    currencySymbol: String,
    selectedCategoryId: String,
    showZeroError: Boolean,
    canProceed: Boolean,
    onKey: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onCancel: () -> Unit,
    onNext: () -> Unit,
    onQuickSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val display = AmountInputLogic.toDisplay(amountInput)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.space18, vertical = dims.space14),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onCancel) {
                IconClose(color = colors.onSurfaceVariant, size = dims.iconSizeNav)
            }
            Text(
                text = "New expense",
                style = typography.sectionHead,
                color = colors.onSurface,
            )
            Spacer(modifier = Modifier.size(dims.space32))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dims.homeHeaderPaddingH),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Amount · $currencyCode",
                style = typography.eyebrow,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(bottom = dims.space8),
            )
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = currencySymbol,
                    style = typography.displayAmount.copy(fontSize = typography.displayAmount.fontSize * 0.47f),
                    color = if (display.isPlaceholder) colors.onSurfaceVariant else colors.primary,
                    modifier = Modifier.padding(top = dims.space8, end = dims.space2),
                )
                Text(
                    text = buildString {
                        append(display.whole)
                        if (display.decimal != null) {
                            append('.')
                            append(display.decimal.padEnd(2, '0').take(2))
                        }
                    },
                    style = typography.displayAmount,
                    color = if (display.isPlaceholder) colors.onSurfaceVariant else colors.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
            if (showZeroError) {
                Text(
                    text = "Amount must be greater than $currencySymbol" + "0",
                    style = typography.body,
                    color = colors.primary,
                    modifier = Modifier.padding(top = dims.space6),
                )
            }

            CategorySection(
                title = "Default",
                categoryIds = defaultLogCategories.map { it.id },
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = onCategorySelected,
                modifier = Modifier.padding(top = dims.space28),
            )
            CategorySection(
                title = "Custom",
                categoryIds = customLogCategories.map { it.id },
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = onCategorySelected,
                modifier = Modifier.padding(top = dims.space14),
            )
        }

        NumericKeypad(
            canProceed = canProceed,
            onKey = onKey,
            onSave = onQuickSave,
            onNext = onNext,
            modifier = Modifier.padding(bottom = dims.space32),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategorySection(
    title: String,
    categoryIds: List<String>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = typography.eyebrow,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(bottom = dims.space8),
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(dims.space6),
            verticalArrangement = Arrangement.spacedBy(dims.space6),
        ) {
            categoryIds.forEach { id ->
                val category = (defaultLogCategories + customLogCategories).first { it.id == id }
                LogCategoryChip(
                    category = category,
                    selected = selectedCategoryId == id,
                    onClick = { onCategorySelected(id) },
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun AddAmountScreenPreview() {
    ProExpenseTheme {
        AddAmountScreen(
            amountInput = "12.50",
            currencyCode = "USD",
            currencySymbol = "$",
            selectedCategoryId = "food",
            showZeroError = false,
            canProceed = true,
            onKey = {},
            onCategorySelected = {},
            onCancel = {},
            onNext = {},
            onQuickSave = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun AddAmountScreenZeroPreview() {
    ProExpenseTheme {
        AddAmountScreen(
            amountInput = "",
            currencyCode = "USD",
            currencySymbol = "$",
            selectedCategoryId = "food",
            showZeroError = true,
            canProceed = false,
            onKey = {},
            onCategorySelected = {},
            onCancel = {},
            onNext = {},
            onQuickSave = {},
        )
    }
}
