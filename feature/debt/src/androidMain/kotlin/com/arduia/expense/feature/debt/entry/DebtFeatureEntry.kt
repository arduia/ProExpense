package com.arduia.expense.feature.debt.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.debt.ui.DebtFlow
import com.arduia.expense.feature.debt.ui.preview.DebtDetailUiState
import com.arduia.expense.feature.debt.ui.preview.DebtListUiState
import com.arduia.expense.feature.debt.ui.preview.DebtSide
import com.arduia.expense.feature.debt.ui.preview.previewDebtLent
import com.arduia.expense.feature.debt.ui.preview.previewDebtOwe

interface DebtFeatureEntry {
    @Composable
    fun DebtOverlay(
        onDismiss: () -> Unit,
        modifier: Modifier = Modifier,
        lentList: DebtListUiState = previewDebtLent,
        oweList: DebtListUiState = previewDebtOwe,
        detailFor: (String) -> DebtDetailUiState? = { null },
        onCreateRecord: (side: DebtSide, person: String, amountRaw: String) -> Unit = { _, _, _ -> },
        onMarkSettled: (String) -> Unit = {},
        onDeleteRecord: (String) -> Unit = {},
    )
}

internal class DebtFeatureEntryImpl : DebtFeatureEntry {
    @Composable
    override fun DebtOverlay(
        onDismiss: () -> Unit,
        modifier: Modifier,
        lentList: DebtListUiState,
        oweList: DebtListUiState,
        detailFor: (String) -> DebtDetailUiState?,
        onCreateRecord: (side: DebtSide, person: String, amountRaw: String) -> Unit,
        onMarkSettled: (String) -> Unit,
        onDeleteRecord: (String) -> Unit,
    ) {
        DebtFlow(
            onDismiss = onDismiss,
            modifier = modifier,
            lentList = lentList,
            oweList = oweList,
            detailFor = detailFor,
            onCreateRecord = onCreateRecord,
            onMarkSettled = onMarkSettled,
            onDeleteRecord = onDeleteRecord,
        )
    }
}

object DebtFeatureUi : DebtFeatureEntry by DebtFeatureEntryImpl()
