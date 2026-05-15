package com.arduia.expense.ui.component.category

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme

data class CategoryUiModel(
    val id: Int,
    val name: String,
    val iconResId: Int? = null,
)

@Composable
fun CategoryChip(
    category: CategoryUiModel,
    selected: Boolean,
    onSelect: (CategoryUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        modifier = modifier,
        selected = selected,
        onClick = { onSelect(category) },
        label = { Text(text = category.name) },
    )
}

@Preview
@Composable
private fun PreviewCategoryChip() {
    ProExpenseTheme {
        CategoryChip(
            category = CategoryUiModel(id = 1, name = "Food & Drink"),
            selected = true,
            onSelect = {},
        )
    }
}
