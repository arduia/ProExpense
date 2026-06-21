package com.arduia.expense.feature.history.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.history.ui.JournalFlow
import com.arduia.expense.ui.design.HomeNavTab

interface HistoryFeatureEntry {
    @Composable
    fun JournalTab(
        selectedTab: HomeNavTab,
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class HistoryFeatureEntryImpl : HistoryFeatureEntry {
    @Composable
    override fun JournalTab(
        selectedTab: HomeNavTab,
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        modifier: Modifier,
    ) {
        JournalFlow(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onAddClick = onAddClick,
            modifier = modifier,
        )
    }
}

object HistoryFeatureUi : HistoryFeatureEntry by HistoryFeatureEntryImpl()
