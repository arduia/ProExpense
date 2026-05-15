package com.arduia.expense.ui.component.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.designsystem.theme.ThemePreviews

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryPicker(
    categories: List<CategoryUiModel>,
    selectedId: Int?,
    onCategorySelect: (CategoryUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid2),
        verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid1),
    ) {
        categories.forEach { category ->
            CategoryChip(
                category = category,
                selected = category.id == selectedId,
                onSelect = onCategorySelect,
            )
        }
    }
}

private val previewCategories = listOf(
    CategoryUiModel(id = 1, name = "Food"),
    CategoryUiModel(id = 2, name = "Transport"),
    CategoryUiModel(id = 3, name = "Health"),
    CategoryUiModel(id = 4, name = "Shopping"),
    CategoryUiModel(id = 5, name = "Entertainment"),
    CategoryUiModel(id = 6, name = "Bills"),
)

@ThemePreviews
@Composable
private fun PreviewCategoryPickerSelected() {
    ProExpenseTheme {
        CategoryPicker(categories = previewCategories, selectedId = 1, onCategorySelect = {})
    }
}

@Preview(name = "None selected")
@Composable
private fun PreviewCategoryPickerNoneSelected() {
    ProExpenseTheme {
        CategoryPicker(categories = previewCategories, selectedId = null, onCategorySelect = {})
    }
}
