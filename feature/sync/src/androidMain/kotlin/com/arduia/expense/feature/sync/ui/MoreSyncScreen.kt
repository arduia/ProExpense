package com.arduia.expense.feature.sync.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.sync.R
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Stateless content for "More → Google Drive Sync" — covers both the not-connected (Connect) and
 * connected (status) states in one screen, following `MoreExportScreen`'s single-file pattern.
 */
@Composable
fun MoreSyncScreen(
    isConnected: Boolean,
    connectedEmail: String?,
    lastSyncedLabel: String,
    onConnect: () -> Unit,
    onSyncNow: () -> Unit,
    onDisconnect: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isConnecting: Boolean = false,
    isDisconnecting: Boolean = false,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        Box(modifier = Modifier.padding(horizontal = dimens.screenPadding)) {
            ProTopBar(
                title = stringResource(R.string.sync_status_title),
                onBack = onBack,
                backLabel = stringResource(R.string.more_back),
            )
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = dimens.screenPadding)
                    .padding(top = dimens.space8),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(dimens.space44 + dimens.space12)
                        .clip(ProExpenseTheme.shapes.card)
                        .background(colors.primaryTint),
                contentAlignment = Alignment.Center,
            ) {
                ProIcon(
                    glyph = if (isConnected) ProIconGlyph.Check else ProIconGlyph.Note,
                    contentDescription = null,
                    tint = colors.primary,
                    size = dimens.iconNav,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                Text(
                    text = stringResource(R.string.sync_connect_heading),
                    style = typography.profileScreenTitle,
                    color = colors.onSurface,
                )
                Text(
                    text = stringResource(R.string.sync_connect_subtitle),
                    style = typography.body,
                    color = colors.onSurfaceMuted,
                )
                Text(
                    text = stringResource(R.string.sync_connect_disclosure),
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                )
            }

            if (isConnected) {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                    Text(
                        text = stringResource(R.string.more_sync_connected_as, connectedEmail.orEmpty()),
                        style = typography.body,
                        color = colors.onSurface,
                    )
                    Text(
                        text = lastSyncedLabel,
                        style = typography.caption,
                        color = colors.onSurfaceMuted,
                    )
                }
            }
        }

        Column(
            modifier =
                Modifier
                    .padding(horizontal = dimens.screenPadding)
                    .padding(bottom = dimens.space18),
            verticalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            if (isConnected) {
                ProButton(
                    text = stringResource(R.string.sync_status_sync_now),
                    onClick = onSyncNow,
                    variant = ProButtonVariant.Primary,
                    size = ProButtonSize.Lg,
                    enabled = !isDisconnecting,
                    fillMaxWidth = true,
                )
                ProButton(
                    text = stringResource(R.string.sync_status_disconnect),
                    onClick = onDisconnect,
                    variant = ProButtonVariant.Danger,
                    size = ProButtonSize.Lg,
                    enabled = !isDisconnecting,
                    loading = isDisconnecting,
                    fillMaxWidth = true,
                )
            } else {
                ProButton(
                    text = stringResource(R.string.sync_connect_action),
                    onClick = onConnect,
                    variant = ProButtonVariant.Primary,
                    size = ProButtonSize.Lg,
                    enabled = !isConnecting,
                    loading = isConnecting,
                    fillMaxWidth = true,
                )
            }
        }
    }
}

@Preview(
    name = "More — Google Drive Sync (not connected)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreSyncScreenNotConnectedPreview() {
    ProExpenseTheme {
        MoreSyncScreen(
            isConnected = false,
            connectedEmail = null,
            lastSyncedLabel = "",
            onConnect = {},
            onSyncNow = {},
            onDisconnect = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "More — Google Drive Sync (connected)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreSyncScreenConnectedPreview() {
    ProExpenseTheme {
        MoreSyncScreen(
            isConnected = true,
            connectedEmail = "maya@example.com",
            lastSyncedLabel = "Never synced yet",
            onConnect = {},
            onSyncNow = {},
            onDisconnect = {},
            onBack = {},
        )
    }
}
