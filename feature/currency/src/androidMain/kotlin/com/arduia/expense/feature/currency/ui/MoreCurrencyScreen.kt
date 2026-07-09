package com.arduia.expense.feature.currency.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.currency.R
import com.arduia.expense.feature.currency.ui.preview.MoreCurrencyItemUi
import com.arduia.expense.feature.currency.ui.preview.previewMoreCurrencies
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.SearchField
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun MoreCurrencyScreen(
    items: List<MoreCurrencyItemUi>,
    selectedCode: String,
    onSave: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    var searchQuery by remember { mutableStateOf("") }
    var pendingCode by remember(selectedCode) { mutableStateOf(selectedCode) }
    val filteredItems =
        remember(items, searchQuery) {
            items.filter { item ->
                searchQuery.isBlank() ||
                    item.code.contains(searchQuery, ignoreCase = true) ||
                    item.name.contains(searchQuery, ignoreCase = true)
            }
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        Box(modifier = Modifier.padding(horizontal = dimens.screenPadding)) {
            ProTopBar(
                title = stringResource(R.string.more_currency_title),
                onBack = onBack,
                backLabel = stringResource(R.string.more_back),
            )
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = dimens.screenPadding)
                    .padding(top = dimens.space8, bottom = dimens.space8),
            verticalArrangement = Arrangement.spacedBy(dimens.space8),
        ) {
            Text(
                text = stringResource(R.string.more_currency_subtitle),
                style = typography.body,
                color = colors.onSurfaceMuted,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimens.space8),
            )
            SearchField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = stringResource(R.string.search_currency_placeholder),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimens.space4),
            )
            filteredItems.forEach { item ->
                CurrencyCard(
                    item = item,
                    selected = item.code == pendingCode,
                    onClick = { pendingCode = item.code },
                )
            }
        }

        ProButton(
            text = stringResource(R.string.more_currency_save_action),
            onClick = { onSave(pendingCode) },
            variant = ProButtonVariant.Primary,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
            enabled = pendingCode != selectedCode,
            modifier =
                Modifier
                    .padding(horizontal = dimens.screenPadding)
                    .padding(bottom = dimens.space18),
        )
    }
}

@Preview(
    name = "More — currency",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreCurrencyPreview() {
    ProExpenseTheme {
        MoreCurrencyScreen(
            items = previewMoreCurrencies,
            selectedCode = "USD",
            onSave = {},
            onBack = {},
        )
    }
}
