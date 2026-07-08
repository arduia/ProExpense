package com.arduia.expense.feature.currency.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.currency.R
import com.arduia.expense.feature.currency.ui.preview.MoreCurrencyItemUi
import com.arduia.expense.shared.CurrencyCatalog
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

val allCurrencyItems: List<MoreCurrencyItemUi> = CurrencyCatalog.ALL.map {
    MoreCurrencyItemUi(it.code, it.name, it.symbol)
}

@Composable
fun CurrencySettingsFlow(
    selectedCode: String,
    onSelect: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    var pendingCode by remember { mutableStateOf<String?>(null) }
    val pendingItem = pendingCode?.let { code -> allCurrencyItems.firstOrNull { it.code == code } }

    Box(modifier = modifier.fillMaxSize()) {
        MoreCurrencyScreen(
            items = allCurrencyItems,
            selectedCode = selectedCode,
            onSave = { code -> pendingCode = code },
            onBack = onBack,
        )

        ProAlertDialog(
            visible = pendingItem != null,
            icon = ProIconGlyph.Check,
            iconTint = colors.primary,
            iconBackground = colors.primaryTint,
            title = stringResource(R.string.more_currency_confirm_title),
            body = AnnotatedString(
                stringResource(
                    R.string.more_currency_confirm_body,
                    pendingItem?.name.orEmpty(),
                    pendingItem?.code.orEmpty(),
                ),
            ),
            confirmLabel = stringResource(R.string.more_currency_confirm_action),
            onConfirm = {
                pendingCode?.let(onSelect)
                pendingCode = null
            },
            dismissLabel = stringResource(R.string.more_currency_confirm_cancel),
            onDismiss = { pendingCode = null },
        )
    }
}

@Preview(
    name = "Currency settings flow",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun CurrencySettingsFlowPreview() {
    ProExpenseTheme {
        CurrencySettingsFlow(
            selectedCode = "USD",
            onSelect = {},
            onBack = {},
        )
    }
}
