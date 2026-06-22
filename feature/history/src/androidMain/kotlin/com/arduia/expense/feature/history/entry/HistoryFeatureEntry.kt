package com.arduia.expense.feature.history.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.history.ui.JournalFlow
import com.arduia.expense.feature.history.ui.preview.JournalDayUi
import com.arduia.expense.feature.history.ui.preview.JournalDetailUiState
import com.arduia.expense.feature.history.ui.preview.previewJournalList
import com.arduia.expense.ui.design.HomeNavTab

interface HistoryFeatureEntry {
    @Composable
    fun JournalTab(
        selectedTab: HomeNavTab,
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        days: List<JournalDayUi> = previewJournalList.days,
        detailFor: (String) -> JournalDetailUiState? = { null },
        onEditRecord: (String) -> Unit = {},
        onDeleteRecord: (String) -> Unit = {},
        modifier: Modifier = Modifier,
    )
}

internal class HistoryFeatureEntryImpl : HistoryFeatureEntry {
    @Composable
    override fun JournalTab(
        selectedTab: HomeNavTab,
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        days: List<JournalDayUi>,
        detailFor: (String) -> JournalDetailUiState?,
        onEditRecord: (String) -> Unit,
        onDeleteRecord: (String) -> Unit,
        modifier: Modifier,
    ) {
        JournalFlow(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onAddClick = onAddClick,
            days = days,
            detailFor = detailFor,
            onEditRecord = onEditRecord,
            onDeleteRecord = onDeleteRecord,
            modifier = modifier,
        )
    }
}

object HistoryFeatureUi : HistoryFeatureEntry by HistoryFeatureEntryImpl()
