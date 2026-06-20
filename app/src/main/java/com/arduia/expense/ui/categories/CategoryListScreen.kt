package com.arduia.expense.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.arduia.expense.ui.design.GenericTextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.design.CategoryListCard
import com.arduia.expense.ui.design.CategoryListDivider
import com.arduia.expense.ui.design.CategoryListRow
import com.arduia.expense.ui.design.CategorySectionHeader
import com.arduia.expense.ui.design.CreateCategoryButton
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.ProTopBarAction
import com.arduia.expense.ui.preview.previewCategoryHandoffState
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

data class CategoryRowState(
    val id: String,
    val label: String,
)

data class CategoryListScreenState(
    val defaultCategories: List<CategoryRowState>,
    val customCategories: List<CategoryRowState>,
)

@Composable
fun CategoryListScreenContent(
    state: CategoryListScreenState,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onCreateCategoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val customCountLabel = stringResource(
        R.string.categories_custom_count,
        state.customCategories.size,
    )

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
            backLabel = stringResource(R.string.more_hub),
            action = ProTopBarAction.Add,
            onAction = onAddClick,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.space18, vertical = dimens.space16),
            verticalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            CategorySectionHeader(
                title = stringResource(R.string.categories_default_header),
                trailing = stringResource(R.string.categories_locked_hint),
            )
            CategoryListCard {
                state.defaultCategories.forEachIndexed { index, category ->
                    if (index > 0) {
                        CategoryListDivider()
                    }
                    CategoryListRow(
                        categoryId = category.id,
                        label = category.label,
                        showLockedTag = true,
                    )
                }
            }

            CategorySectionHeader(
                title = stringResource(R.string.categories_custom_header),
                trailing = customCountLabel,
            )
            CategoryListCard {
                state.customCategories.forEachIndexed { index, category ->
                    if (index > 0) {
                        CategoryListDivider()
                    }
                    CategoryListRow(
                        categoryId = category.id,
                        label = category.label,
                        showDragHandle = true,
                    )
                }
            }

            CreateCategoryButton(
                label = stringResource(R.string.create_new_category),
                onClick = onCreateCategoryClick,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = colors.lineStrong,
                        shape = ProExpenseTheme.shapes.card,
                    ),
            )

            Text(
                text = stringResource(R.string.categories_order_hint),
                style = typography.caption,
                color = colors.onSurfaceMuted,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = dimens.space8),
            )
        }
    }
}

@Composable
fun AddCategoryDialog(
    name: String,
    duplicateError: Boolean,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.add_category),
                style = typography.sectionHead,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.dimensions.space8)) {
                GenericTextField(
                    value = name,
                    onValueChange = onNameChange,
                    placeholder = stringResource(R.string.category_name_hint),
                )
                if (duplicateError) {
                    Text(
                        text = stringResource(R.string.category_duplicate_error),
                        style = typography.caption,
                        color = colors.danger,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = name.isNotBlank()) {
                Text(stringResource(R.string.add_category))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Preview(
    name = "Categories — handoff",
    widthDp = ProArtboard.HANDOFF_WIDTH_DP,
    heightDp = ProArtboard.HANDOFF_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun CategoryListPreview() {
    ProExpenseTheme {
        CategoryListScreenContent(
            state = previewCategoryHandoffState,
            onBack = {},
            onAddClick = {},
            onCreateCategoryClick = {},
        )
    }
}
