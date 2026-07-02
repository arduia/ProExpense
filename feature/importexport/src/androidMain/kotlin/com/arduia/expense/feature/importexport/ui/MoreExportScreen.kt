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
import com.arduia.expense.feature.importexport.ui.components.ClearDataCard
import com.arduia.expense.feature.importexport.ui.components.ExportFileRow
import com.arduia.expense.feature.importexport.ui.components.ImportExportGroupCard
import com.arduia.expense.ui.design.PasswordField
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.SegmentedToggle
import com.arduia.expense.feature.importexport.ui.preview.MoreExportFileUi
import com.arduia.expense.feature.importexport.ui.preview.previewMoreExportFiles
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun MoreExportScreen(
    files: List<MoreExportFileUi>,
    onExport: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    password: String = "",
    onPasswordChange: (String) -> Unit = {},
    formatIndex: Int = 0,
    onFormatChange: (Int) -> Unit = {},
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
                title = stringResource(R.string.more_export_title),
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
                    text = stringResource(R.string.more_export_heading),
                    style = typography.profileScreenTitle,
                    color = colors.onSurface,
                )
                Text(
                    text = if (formatIndex == 0) {
                        stringResource(R.string.more_export_subtitle)
                    } else {
                        stringResource(R.string.more_export_subtitle_json)
                    },
                    style = typography.body,
                    color = colors.onSurfaceMuted,
                )
            }

            SegmentedToggle(
                options = listOf(
                    stringResource(R.string.more_export_format_csv),
                    stringResource(R.string.more_export_format_json),
                ),
                selectedIndex = formatIndex,
                onSelected = onFormatChange,
            )

            if (formatIndex == 0) {
                ImportExportGroupCard(items = files) { file ->
                    ExportFileRow(fileName = file.fileName, subtitle = file.subtitle)
                }
            } else {
                ImportExportGroupCard(items = listOf(Unit)) {
                    ExportFileRow(
                        fileName = stringResource(R.string.more_export_json_filename),
                        subtitle = stringResource(R.string.more_export_json_subtitle),
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                PasswordField(
                    value = password,
                    onValueChange = onPasswordChange,
                    placeholder = stringResource(R.string.more_export_password_placeholder),
                )
                Text(
                    text = stringResource(R.string.more_export_password_hint),
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                )
            }
        }

        ProButton(
            text = if (formatIndex == 0) {
                stringResource(R.string.more_export_action)
            } else {
                stringResource(R.string.more_export_action_json)
            },
            onClick = onExport,
            variant = ProButtonVariant.Primary,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
            modifier = Modifier
                .padding(horizontal = dimens.screenPadding)
                .padding(bottom = dimens.space18),
        )
    }
}

@Preview(
    name = "More — data export",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreExportPreview() {
    ProExpenseTheme {
        MoreExportScreen(
            files = previewMoreExportFiles,
            onExport = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "More — data export (password set)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreExportPasswordPreview() {
    ProExpenseTheme {
        MoreExportScreen(
            files = previewMoreExportFiles,
            onExport = {},
            onBack = {},
            password = "s3cret",
        )
    }
}

@Preview(
    name = "More — data export (JSON format)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreExportJsonPreview() {
    ProExpenseTheme {
        MoreExportScreen(
            files = previewMoreExportFiles,
            onExport = {},
            onBack = {},
            formatIndex = 1,
        )
    }
}
