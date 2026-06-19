package com.arduia.expense.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.MoreHubRow
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun MoreHubScreenContent(
    onReportsClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onCurrencyClick: () -> Unit,
    onExportClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimens.space18, vertical = dimens.space24),
        verticalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Text(
            text = stringResource(R.string.more_hub),
            style = typography.screenTitle,
            color = colors.onSurface,
        )
        MoreHubRow(
            title = stringResource(R.string.more_reports),
            subtitle = stringResource(R.string.more_reports_sub),
            icon = ProIconGlyph.FeatReports,
            iconTint = colors.primaryDeep,
            iconBackground = colors.primaryTint,
            onClick = onReportsClick,
        )
        MoreHubRow(
            title = stringResource(R.string.more_categories),
            subtitle = stringResource(R.string.more_categories_sub),
            icon = ProIconGlyph.CatDefault,
            iconTint = colors.onSurface,
            iconBackground = colors.paperAlt,
            onClick = onCategoriesClick,
        )
        MoreHubRow(
            title = stringResource(R.string.more_currency),
            subtitle = stringResource(R.string.more_currency_sub),
            icon = ProIconGlyph.Sparkle,
            iconTint = colors.highlightDeep,
            iconBackground = colors.highlightSoft,
            onClick = onCurrencyClick,
        )
        MoreHubRow(
            title = stringResource(R.string.more_export),
            subtitle = stringResource(R.string.more_export_sub),
            icon = ProIconGlyph.Note,
            iconTint = colors.success,
            iconBackground = colors.successTint,
            onClick = onExportClick,
        )
        MoreHubRow(
            title = stringResource(R.string.more_clear),
            subtitle = stringResource(R.string.more_clear_sub),
            icon = ProIconGlyph.Close,
            iconTint = colors.danger,
            iconBackground = colors.dangerTint,
            onClick = onClearClick,
        )
    }
}

@Preview(
    name = "More hub",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreHubPreview() {
    ProExpenseTheme {
        MoreHubScreenContent(
            onReportsClick = {},
            onCategoriesClick = {},
            onCurrencyClick = {},
            onExportClick = {},
            onClearClick = {},
        )
    }
}
