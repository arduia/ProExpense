package com.arduia.expense.feature.reports.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.reports.ui.ReportsFlow
import com.arduia.expense.feature.reports.ui.preview.ReportsUiState

interface ReportsFeatureEntry {
    @Composable
    fun ReportsFlow(
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
        empty: Boolean = false,
        onLogFirstExpense: () -> Unit = {},
        periods: List<ReportsUiState>? = null,
    )
}

internal class ReportsFeatureEntryImpl : ReportsFeatureEntry {
    @Composable
    override fun ReportsFlow(
        onBack: () -> Unit,
        modifier: Modifier,
        empty: Boolean,
        onLogFirstExpense: () -> Unit,
        periods: List<ReportsUiState>?,
    ) {
        if (periods == null) {
            ReportsFlow(
                onBack = onBack,
                modifier = modifier,
                empty = empty,
                onLogFirstExpense = onLogFirstExpense,
            )
        } else {
            ReportsFlow(
                onBack = onBack,
                modifier = modifier,
                empty = empty,
                onLogFirstExpense = onLogFirstExpense,
                periods = periods,
            )
        }
    }
}

object ReportsFeatureUi : ReportsFeatureEntry by ReportsFeatureEntryImpl()
