package com.arduia.expense.feature.sharedcost.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.sharedcost.ui.SharedCostsFlow

interface SharedCostFeatureEntry {
    @Composable
    fun SharedCostsOverlay(
        onDismiss: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class SharedCostFeatureEntryImpl : SharedCostFeatureEntry {
    @Composable
    override fun SharedCostsOverlay(onDismiss: () -> Unit, modifier: Modifier) {
        SharedCostsFlow(onDismiss = onDismiss, modifier = modifier)
    }
}

object SharedCostFeatureUi : SharedCostFeatureEntry by SharedCostFeatureEntryImpl()
