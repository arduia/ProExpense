package com.arduia.expense.feature.auth.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.auth.ui.PinSetupFlow as PinSetupFlowContent

interface AuthFeatureEntry {
    @Composable
    fun PinSetupFlow(
        onDismiss: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class AuthFeatureEntryImpl : AuthFeatureEntry {
    @Composable
    override fun PinSetupFlow(onDismiss: () -> Unit, modifier: Modifier) {
        PinSetupFlowContent(onDismiss = onDismiss, modifier = modifier)
    }
}

object AuthFeatureUi : AuthFeatureEntry by AuthFeatureEntryImpl()
