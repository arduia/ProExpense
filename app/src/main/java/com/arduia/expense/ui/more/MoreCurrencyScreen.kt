package com.arduia.expense.ui.more

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.preview.MoreCurrencyItemUi
import com.arduia.expense.ui.preview.previewMoreCurrencies
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun MoreCurrencyScreen(
    items: List<MoreCurrencyItemUi>,
    selectedCode: String,
    onSelect: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space8, bottom = dimens.space24),
            verticalArrangement = Arrangement.spacedBy(dimens.space8),
        ) {
            Text(
                text = stringResource(R.string.more_currency_subtitle),
                style = typography.body,
                color = colors.onSurfaceMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimens.space8),
            )
            items.forEach { item ->
                MoreCurrencyCard(
                    item = item,
                    selected = item.code == selectedCode,
                    onClick = { onSelect(item.code) },
                )
            }
        }
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
            onSelect = {},
            onBack = {},
        )
    }
}
