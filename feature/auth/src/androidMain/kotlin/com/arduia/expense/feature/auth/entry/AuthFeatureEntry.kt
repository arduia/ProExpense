package com.arduia.expense.feature.auth.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.auth.ui.PinSetupFlow

interface AuthFeatureEntry {
    @Composable
    fun PinSetupFlow(
        onDismiss: () -> Unit,
        modifier: Modifier,
    )

    @Composable
    fun PinLockFlow(
        onUnlocked: () -> Unit,
        modifier: Modifier,
    )

    @Composable
    fun PinVerifyFlow(
        headingRes: Int,
        helperRes: Int,
        onVerified: () -> Unit,
        onCancel: () -> Unit,
        modifier: Modifier,
    )

    @Composable
    fun PinChangeFlow(
        onDone: () -> Unit,
        onCancel: () -> Unit,
        modifier: Modifier,
    )
}

internal class AuthFeatureEntryImpl : AuthFeatureEntry {
    @Composable
    override fun PinSetupFlow(onDismiss: () -> Unit, modifier: Modifier) {
        com.arduia.expense.feature.auth.ui.PinSetupFlow(onDismiss = onDismiss, modifier = modifier)
    }

    @Composable
    override fun PinLockFlow(onUnlocked: () -> Unit, modifier: Modifier) {
        com.arduia.expense.feature.auth.ui.PinLockFlow(onUnlocked = onUnlocked, modifier = modifier)
    }

    @Composable
    override fun PinVerifyFlow(
        headingRes: Int,
        helperRes: Int,
        onVerified: () -> Unit,
        onCancel: () -> Unit,
        modifier: Modifier,
    ) {
        com.arduia.expense.feature.auth.ui.PinVerifyFlow(
            headingRes = headingRes,
            helperRes = helperRes,
            onVerified = onVerified,
            onCancel = onCancel,
            modifier = modifier,
        )
    }

    @Composable
    override fun PinChangeFlow(onDone: () -> Unit, onCancel: () -> Unit, modifier: Modifier) {
        com.arduia.expense.feature.auth.ui.PinChangeFlow(onDone = onDone, onCancel = onCancel, modifier = modifier)
    }
}

object AuthFeatureUi : AuthFeatureEntry by AuthFeatureEntryImpl()
