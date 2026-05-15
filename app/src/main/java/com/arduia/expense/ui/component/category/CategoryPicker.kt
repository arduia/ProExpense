package com.arduia.expense.ui.component.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme

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

@Preview
@Composable
private fun PreviewCategoryPicker() {
    ProExpenseTheme {
        CategoryPicker(
            categories = listOf(
                CategoryUiModel(id = 1, name = "Food"),
                CategoryUiModel(id = 2, name = "Transport"),
                CategoryUiModel(id = 3, name = "Health"),
                CategoryUiModel(id = 4, name = "Shopping"),
            ),
            selectedId = 1,
            onCategorySelect = {},
        )
    }
}
