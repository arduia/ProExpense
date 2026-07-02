package com.arduia.expense.feature.importexport.ui

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
import com.arduia.expense.feature.importexport.R
import com.arduia.expense.feature.importexport.ui.components.ExportFileRow
import com.arduia.expense.feature.importexport.ui.components.ImportExportGroupCard
import com.arduia.expense.ui.design.PasswordField
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.feature.importexport.ui.preview.MoreImportUiState
import com.arduia.expense.feature.importexport.ui.preview.previewMoreImportEmpty
import com.arduia.expense.feature.importexport.ui.preview.previewMoreImportError
import com.arduia.expense.feature.importexport.ui.preview.previewMoreImportLocked
import com.arduia.expense.feature.importexport.ui.preview.previewMoreImportPicked
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun MoreImportScreen(
    state: MoreImportUiState,
    onChooseFile: () -> Unit,
    onImport: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onPasswordChange: (String) -> Unit = {},
    onUnlock: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Box(modifier = Modifier.padding(horizontal = dimens.screenPadding)) {
            ProTopBar(
                title = stringResource(R.string.more_import_title),
                onBack = onBack,
                backLabel = stringResource(R.string.more_back),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space8),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            Box(
                modifier = Modifier
                    .size(dimens.space44 + dimens.space12)
                    .clip(ProExpenseTheme.shapes.card)
                    .background(colors.primaryTint),
                contentAlignment = Alignment.Center,
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Note,
                    contentDescription = null,
                    tint = colors.primary,
                    size = dimens.iconNav,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                Text(
                    text = stringResource(R.string.more_import_heading),
                    style = typography.profileScreenTitle,
                    color = colors.onSurface,
                )
                Text(
                    text = stringResource(R.string.more_import_subtitle),
                    style = typography.body,
                    color = colors.onSurfaceMuted,
                )
            }

            if (state.fileName != null) {
                ImportExportGroupCard(items = listOf(state)) { picked ->
                    ExportFileRow(
                        fileName = picked.fileName.orEmpty(),
                        subtitle = picked.errorMessage
                            ?: picked.resultMessage
                            ?: if (picked.needsPassword) {
                                stringResource(R.string.more_import_password_needed)
                            } else {
                                stringResource(R.string.more_import_preview_count, picked.previewCount ?: 0)
                            },
                    )
                }
            }

            if (state.needsPassword) {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                    PasswordField(
                        value = state.password,
                        onValueChange = onPasswordChange,
                        placeholder = stringResource(R.string.more_import_password_placeholder),
                    )
                    ProButton(
                        text = stringResource(R.string.more_import_unlock),
                        onClick = onUnlock,
                        enabled = state.password.isNotBlank(),
                        variant = ProButtonVariant.Primary,
                        size = ProButtonSize.Md,
                        fillMaxWidth = true,
                    )
                }
            }

            ProButton(
                text = stringResource(R.string.more_import_choose_file),
                onClick = onChooseFile,
                variant = ProButtonVariant.Secondary,
                size = ProButtonSize.Md,
                fillMaxWidth = true,
            )
        }

        if (state.previewCount != null && state.errorMessage == null && state.resultMessage == null) {
            ProButton(
                text = stringResource(R.string.more_import_action, state.previewCount),
                onClick = onImport,
                enabled = !state.isImporting,
                variant = ProButtonVariant.Primary,
                size = ProButtonSize.Lg,
                fillMaxWidth = true,
                modifier = Modifier
                    .padding(horizontal = dimens.screenPadding)
                    .padding(bottom = dimens.space18),
            )
        }
    }
}

@Preview(
    name = "More — data import (empty)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreImportEmptyPreview() {
    ProExpenseTheme {
        MoreImportScreen(
            state = previewMoreImportEmpty,
            onChooseFile = {},
            onImport = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "More — data import (file picked)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreImportPickedPreview() {
    ProExpenseTheme {
        MoreImportScreen(
            state = previewMoreImportPicked,
            onChooseFile = {},
            onImport = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "More — data import (error)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreImportErrorPreview() {
    ProExpenseTheme {
        MoreImportScreen(
            state = previewMoreImportError,
            onChooseFile = {},
            onImport = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "More — data import (password-protected zip)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreImportLockedPreview() {
    ProExpenseTheme {
        MoreImportScreen(
            state = previewMoreImportLocked,
            onChooseFile = {},
            onImport = {},
            onBack = {},
        )
    }
}
