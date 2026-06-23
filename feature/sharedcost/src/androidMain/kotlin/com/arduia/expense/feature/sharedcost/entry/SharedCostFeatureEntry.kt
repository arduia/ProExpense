package com.arduia.expense.feature.sharedcost.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.sharedcost.ui.SharedCostsFlow
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostHistoryItemUi
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedHistoryItems

interface SharedCostFeatureEntry {
    @Composable
    fun SharedCostsOverlay(
        onDismiss: () -> Unit,
        modifier: Modifier = Modifier,
        savedToastMessage: String? = null,
        onSaved: () -> Unit = {},
        history: List<SharedCostHistoryItemUi> = previewSharedHistoryItems,
        onCreateSplit: (title: String, totalCents: Long, names: List<String>, customShareCents: List<Long>?) -> Unit =
            { _, _, _, _ -> },
    )
}

internal class SharedCostFeatureEntryImpl : SharedCostFeatureEntry {
    @Composable
    override fun SharedCostsOverlay(
        onDismiss: () -> Unit,
        modifier: Modifier,
        savedToastMessage: String?,
        onSaved: () -> Unit,
        history: List<SharedCostHistoryItemUi>,
        onCreateSplit: (title: String, totalCents: Long, names: List<String>, customShareCents: List<Long>?) -> Unit,
    ) {
        SharedCostsFlow(
            onDismiss = onDismiss,
            modifier = modifier,
            savedToastMessage = savedToastMessage,
            onSaved = onSaved,
            history = history,
            onCreateSplit = onCreateSplit,
        )
    }
}

object SharedCostFeatureUi : SharedCostFeatureEntry by SharedCostFeatureEntryImpl()
