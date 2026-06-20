package com.arduia.expense.ui.categories

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
import com.arduia.expense.ui.design.GenericTextField
import com.arduia.expense.ui.design.LogCategoryBadge
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.preview.previewCategoryItems
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun CategoryListScreenContent(
    categories: List<Pair<String, String>>,
    selectedCategoryId: String?,
    onCategorySelected: (String) -> Unit,
    newCategoryName: String,
    onNewCategoryChange: (String) -> Unit,
    duplicateError: Boolean,
    onBack: () -> Unit,
    onAddCategory: () -> Unit,
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
            title = stringResource(R.string.categories_title),
            onBack = onBack,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.space18, vertical = dimens.space16),
            verticalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            categories.forEach { (id, label) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.space12),
                ) {
                    LogCategoryBadge(categoryId = id)
                    Text(
                        text = label,
                        style = typography.bodySemiBold,
                        color = colors.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Text(
                text = stringResource(R.string.add_category),
                style = typography.eyebrow,
                color = colors.primary,
                modifier = Modifier.padding(top = dimens.space16),
            )
            GenericTextField(
                value = newCategoryName,
                onValueChange = onNewCategoryChange,
                placeholder = stringResource(R.string.category_name_hint),
            )
            if (duplicateError) {
                Text(
                    text = stringResource(R.string.category_duplicate_error),
                    style = typography.caption,
                    color = colors.danger,
                )
            }
            ProButton(
                text = stringResource(R.string.add_category),
                onClick = onAddCategory,
                enabled = newCategoryName.isNotBlank(),
                size = ProButtonSize.Md,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(
    name = "Categories — list",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun CategoryListPreview() {
    ProExpenseTheme {
        CategoryListScreenContent(
            categories = previewCategoryItems,
            selectedCategoryId = "food",
            onCategorySelected = {},
            newCategoryName = "",
            onNewCategoryChange = {},
            duplicateError = false,
            onBack = {},
            onAddCategory = {},
        )
    }
}

@Preview(
    name = "Categories — duplicate",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun CategoryDuplicatePreview() {
    ProExpenseTheme {
        CategoryListScreenContent(
            categories = previewCategoryItems,
            selectedCategoryId = null,
            onCategorySelected = {},
            newCategoryName = "Food",
            onNewCategoryChange = {},
            duplicateError = true,
            onBack = {},
            onAddCategory = {},
        )
    }
}
