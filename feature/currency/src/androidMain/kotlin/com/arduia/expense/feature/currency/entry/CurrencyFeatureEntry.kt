package com.arduia.expense.feature.currency.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.currency.ui.CurrencySettingsFlow

interface CurrencyFeatureEntry {
    @Composable
    fun SettingsFlow(
        selectedCode: String,
        onSelect: (String) -> Unit,
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class CurrencyFeatureEntryImpl : CurrencyFeatureEntry {
    @Composable
    override fun SettingsFlow(
        selectedCode: String,
        onSelect: (String) -> Unit,
        onBack: () -> Unit,
        modifier: Modifier,
    ) {
        CurrencySettingsFlow(
            selectedCode = selectedCode,
            onSelect = onSelect,
            onBack = onBack,
            modifier = modifier,
        )
    }
}

object CurrencyFeatureUi : CurrencyFeatureEntry by CurrencyFeatureEntryImpl()
