package com.arduia.expense.feature.sync.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.arduia.expense.data.Result
import com.arduia.expense.data.SyncAccountRepository
import com.arduia.expense.feature.sync.ConnectDriveAccountUseCase
import com.arduia.expense.feature.sync.DisconnectDriveAccountUseCase
import com.arduia.expense.feature.sync.DriveAuthManager
import com.arduia.expense.feature.sync.DriveAuthManagerImpl
import com.arduia.expense.feature.sync.R
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Stateful entry for "More → Google Drive Sync" (US-SYNC-1/US-SYNC-6). Phase 1 only — "Sync now"
 * shows a not-yet-available message since push/pull (Phase 2/3) isn't implemented yet.
 */
@Composable
fun SyncConnectFlow(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    syncAccountRepository: SyncAccountRepository = koinInject(),
    authManager: DriveAuthManager = koinInject(),
    connectDriveAccount: ConnectDriveAccountUseCase = koinInject(),
    disconnectDriveAccount: DisconnectDriveAccountUseCase = koinInject(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isConnected by remember { mutableStateOf(false) }
    var connectedEmail by remember { mutableStateOf<String?>(null) }
    var lastSyncedAtMillis by remember { mutableStateOf<Long?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDisconnectConfirm by remember { mutableStateOf(false) }
    var isConnecting by remember { mutableStateOf(false) }
    var isDisconnecting by remember { mutableStateOf(false) }
    val syncNotAvailableMessage = stringResource(R.string.sync_status_sync_now_unavailable)
    val connectErrorMessage = stringResource(R.string.sync_connect_error)

    LaunchedEffect(Unit) {
        when (val result = syncAccountRepository.getState()) {
            is Result.Success -> {
                isConnected = result.data.isConnected
                connectedEmail = result.data.connectedAccountEmail
                lastSyncedAtMillis = result.data.lastSyncAtEpochMillis
            }
            is Result.Error -> Unit
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MoreSyncScreen(
            isConnected = isConnected,
            connectedEmail = connectedEmail,
            lastSyncedLabel = lastSyncedAtMillis.toLastSyncedLabel(),
            onConnect = {
                if (!isConnecting) {
                    // Credential Manager / the Authorization API need a foreground Activity, which
                    // a Koin singleton has no way to obtain on its own — the Connect screen is the
                    // one place that can supply it.
                    (authManager as? DriveAuthManagerImpl)?.activity = context as? ComponentActivity
                    isConnecting = true
                    scope.launch {
                        try {
                            when (val result = connectDriveAccount()) {
                                is Result.Success -> {
                                    when (val state = syncAccountRepository.getState()) {
                                        is Result.Success -> {
                                            isConnected = state.data.isConnected
                                            connectedEmail = state.data.connectedAccountEmail
                                        }
                                        is Result.Error -> Unit
                                    }
                                }
                                is Result.Error -> errorMessage = connectErrorMessage
                            }
                        } finally {
                            isConnecting = false
                        }
                    }
                }
            },
            onSyncNow = { errorMessage = syncNotAvailableMessage },
            onDisconnect = { if (!isDisconnecting) showDisconnectConfirm = true },
            onBack = onBack,
            isConnecting = isConnecting,
            isDisconnecting = isDisconnecting,
        )

        ProAlertDialog(
            visible = showDisconnectConfirm,
            icon = ProIconGlyph.Close,
            iconTint = ProExpenseTheme.colors.danger,
            iconBackground = ProExpenseTheme.colors.dangerTint,
            title = stringResource(R.string.sync_disconnect_confirm_title),
            body = AnnotatedString(stringResource(R.string.sync_disconnect_confirm_body)),
            confirmLabel = stringResource(R.string.sync_disconnect_confirm_action),
            onConfirm = {
                showDisconnectConfirm = false
                isDisconnecting = true
                scope.launch {
                    try {
                        disconnectDriveAccount()
                        isConnected = false
                        connectedEmail = null
                        lastSyncedAtMillis = null
                    } finally {
                        isDisconnecting = false
                    }
                }
            },
            dismissLabel = stringResource(R.string.sync_disconnect_confirm_cancel),
            onDismiss = { showDisconnectConfirm = false },
            confirmVariant = ProButtonVariant.Danger,
        )

        ProToastHost(
            message = errorMessage,
            onDismiss = { errorMessage = null },
        )
    }
}

@Composable
private fun Long?.toLastSyncedLabel(): String =
    if (this == null) {
        stringResource(R.string.sync_status_never_synced)
    } else {
        val formatted = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(this))
        stringResource(R.string.sync_status_last_synced, formatted)
    }

@androidx.compose.ui.tooling.preview.Preview(
    name = "Sync connect flow",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SyncConnectFlowPreview() {
    ProExpenseTheme {
        SyncConnectFlow(onBack = {})
    }
}
