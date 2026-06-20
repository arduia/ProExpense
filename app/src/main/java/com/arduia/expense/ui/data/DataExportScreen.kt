package com.arduia.expense.ui.data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.GenericTextField
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun DataExportScreenContent(
    exportPassword: String,
    onExportPasswordChange: (String) -> Unit,
    onBack: () -> Unit,
    onExport: () -> Unit,
    modifier: Modifier = Modifier,
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
        ProTopBar(
            title = stringResource(R.string.export_title),
            onBack = onBack,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.space18, vertical = dimens.space24),
        ) {
            Text(
                text = stringResource(R.string.export_description),
                style = typography.body,
                color = colors.onSurfaceMuted,
            )
            GenericTextField(
                value = exportPassword,
                onValueChange = onExportPasswordChange,
                placeholder = stringResource(R.string.export_password_hint),
                leadingIcon = ProIconGlyph.Eye,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space16),
            )
            ProButton(
                text = stringResource(R.string.export_encrypted),
                onClick = onExport,
                enabled = exportPassword.isNotBlank(),
                size = ProButtonSize.Lg,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space24),
            )
        }
    }
}

@Preview(
    name = "Data export",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DataExportPreview() {
    ProExpenseTheme {
        DataExportScreenContent(
            exportPassword = "",
            onExportPasswordChange = {},
            onBack = {},
            onExport = {},
        )
    }
}
