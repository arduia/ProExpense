package com.arduia.expense.feature.sync.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.sync.ui.SyncConnectFlow

interface SyncFeatureEntry {
    @Composable
    fun ConnectFlow(
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class SyncFeatureEntryImpl : SyncFeatureEntry {
    @Composable
    override fun ConnectFlow(
        onBack: () -> Unit,
        modifier: Modifier,
    ) {
        SyncConnectFlow(onBack = onBack, modifier = modifier)
    }
}

object SyncFeatureUi : SyncFeatureEntry by SyncFeatureEntryImpl()
