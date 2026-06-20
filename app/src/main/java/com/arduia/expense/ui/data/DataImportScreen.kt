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
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.ProfileNameField
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun DataImportScreenContent(
    importText: String,
    onImportTextChange: (String) -> Unit,
    importPassword: String,
    onImportPasswordChange: (String) -> Unit,
    onBack: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier,
    resultMessage: String? = null,
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
            title = stringResource(R.string.import_data),
            onBack = onBack,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.space18, vertical = dimens.space24),
        ) {
            Text(
                text = stringResource(R.string.import_description),
                style = typography.body,
                color = colors.onSurfaceMuted,
            )
            ProfileNameField(
                value = importText,
                onValueChange = onImportTextChange,
                placeholder = stringResource(R.string.import_paste_hint),
                modifier = Modifier.padding(top = dimens.space16),
            )
            ProfileNameField(
                value = importPassword,
                onValueChange = onImportPasswordChange,
                placeholder = stringResource(R.string.import_password_hint),
                modifier = Modifier.padding(top = dimens.space12),
            )
            if (resultMessage != null) {
                Text(
                    text = resultMessage,
                    style = typography.caption,
                    color = colors.success,
                    modifier = Modifier.padding(top = dimens.space12),
                )
            }
            ProButton(
                text = stringResource(R.string.import_data),
                onClick = onImport,
                enabled = importText.isNotBlank() && importPassword.isNotBlank(),
                size = ProButtonSize.Lg,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space24),
            )
        }
    }
}

@Preview(
    name = "Data import",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DataImportPreview() {
    ProExpenseTheme {
        DataImportScreenContent(
            importText = "id,amountCents,categoryId,note,recordedAt",
            onImportTextChange = {},
            importPassword = "",
            onImportPasswordChange = {},
            onBack = {},
            onImport = {},
        )
    }
}
