package com.arduia.expense.feature.debt.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.debt.ui.DebtFlow

interface DebtFeatureEntry {
    @Composable
    fun DebtOverlay(
        onDismiss: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class DebtFeatureEntryImpl : DebtFeatureEntry {
    @Composable
    override fun DebtOverlay(onDismiss: () -> Unit, modifier: Modifier) {
        DebtFlow(onDismiss = onDismiss, modifier = modifier)
    }
}

object DebtFeatureUi : DebtFeatureEntry by DebtFeatureEntryImpl()
