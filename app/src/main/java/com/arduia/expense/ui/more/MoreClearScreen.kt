package com.arduia.expense.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.preview.MoreClearOptionUi
import com.arduia.expense.ui.preview.previewMoreClearOptions
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun MoreClearScreen(
    options: List<MoreClearOptionUi>,
    checkedIds: Set<String>,
    onToggle: (String) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
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
        Box(modifier = Modifier.padding(horizontal = dimens.screenPadding)) {
            ProTopBar(
                title = stringResource(R.string.more_clear_title),
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
            verticalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            Text(
                text = stringResource(R.string.more_clear_subtitle),
                style = typography.body,
                color = colors.onSurfaceMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimens.space4),
            )
            options.forEach { option ->
                MoreClearCard(
                    option = option,
                    checked = option.id in checkedIds,
                    onToggle = { onToggle(option.id) },
                )
            }
        }

        ProButton(
            text = stringResource(R.string.more_clear_action),
            onClick = onClear,
            variant = ProButtonVariant.Danger,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
            enabled = checkedIds.isNotEmpty(),
            modifier = Modifier
                .padding(horizontal = dimens.screenPadding)
                .padding(bottom = dimens.space18),
        )
    }
}

@Preview(
    name = "More — clear data",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreClearPreview() {
    ProExpenseTheme {
        MoreClearScreen(
            options = previewMoreClearOptions,
            checkedIds = setOf("expenses"),
            onToggle = {},
            onClear = {},
            onBack = {},
        )
    }
}
