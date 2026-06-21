package com.arduia.expense.feature.reports.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.reports.ui.ReportsFlow

interface ReportsFeatureEntry {
    @Composable
    fun ReportsFlow(
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
        empty: Boolean = false,
        onLogFirstExpense: () -> Unit = {},
    )
}

internal class ReportsFeatureEntryImpl : ReportsFeatureEntry {
    @Composable
    override fun ReportsFlow(
        onBack: () -> Unit,
        modifier: Modifier,
        empty: Boolean,
        onLogFirstExpense: () -> Unit,
    ) {
        com.arduia.expense.feature.reports.ui.ReportsFlow(
            onBack = onBack,
            modifier = modifier,
            empty = empty,
            onLogFirstExpense = onLogFirstExpense,
        )
    }
}

object ReportsFeatureUi : ReportsFeatureEntry by ReportsFeatureEntryImpl()
