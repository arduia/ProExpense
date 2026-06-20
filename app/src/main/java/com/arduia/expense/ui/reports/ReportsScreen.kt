package com.arduia.expense.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.domain.BudgetColorTier
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.ReportsCategoryRow
import com.arduia.expense.ui.design.proIconClickable
import com.arduia.expense.ui.preview.previewReportsCategories
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ReportsScreenContent(
    monthLabel: String,
    totalSpend: String,
    categories: List<Triple<String, String, Pair<String, Float>>>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    showUncategorizedFallback: Boolean = false,
    dailyAverage: String = "",
    budgetColor: BudgetColorTier? = null,
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val spendColor = when (budgetColor) {
        BudgetColorTier.SLIGHTLY_OVER -> colors.highlightDeep
        BudgetColorTier.SIGNIFICANTLY_OVER -> colors.danger
        BudgetColorTier.ON_BUDGET, null -> colors.onSurface
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        ProTopBar(
            title = stringResource(R.string.reports_title),
            onBack = onBack,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.space18, vertical = dimens.space16),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Back,
                    contentDescription = stringResource(R.string.previous_month),
                    tint = colors.muted,
                    modifier = Modifier.proIconClickable(onClick = onPreviousMonth),
                )
                Text(
                    text = monthLabel,
                    style = typography.eyebrow,
                    color = colors.muted,
                )
                ProIcon(
                    glyph = ProIconGlyph.ChevronRight,
                    contentDescription = stringResource(R.string.next_month),
                    tint = colors.muted,
                    modifier = Modifier.proIconClickable(onClick = onNextMonth),
                )
            }
            Text(
                text = stringResource(R.string.reports_total_spend),
                style = typography.body,
                color = colors.onSurfaceMuted,
            )
            Text(
                text = totalSpend,
                style = typography.screenTitle,
                color = spendColor,
            )
            if (dailyAverage.isNotBlank()) {
                Text(
                    text = dailyAverage,
                    style = typography.body,
                    color = colors.onSurfaceMuted,
                )
            }

            if (showUncategorizedFallback) {
                Text(
                    text = stringResource(R.string.reports_uncategorized),
                    style = typography.body,
                    color = colors.onSurfaceMuted,
                    modifier = Modifier.padding(top = dimens.space24),
                )
            } else {
                categories.forEach { (categoryId, label, amountProgress) ->
                    ReportsCategoryRow(
                        categoryId = categoryId,
                        categoryLabel = label,
                        amount = amountProgress.first,
                        progress = amountProgress.second,
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Reports — monthly",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ReportsMonthlyPreview() {
    ProExpenseTheme {
        ReportsScreenContent(
            monthLabel = stringResource(R.string.reports_monthly),
            totalSpend = "$80.90",
            categories = previewReportsCategories,
            dailyAverage = "$2.70/day",
            onBack = {},
        )
    }
}

@Preview(
    name = "Reports — uncategorized",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ReportsUncategorizedPreview() {
    ProExpenseTheme {
        ReportsScreenContent(
            monthLabel = stringResource(R.string.reports_monthly),
            totalSpend = "$0",
            categories = emptyList(),
            onBack = {},
            showUncategorizedFallback = true,
        )
    }
}
