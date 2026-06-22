package com.arduia.expense.feature.categories.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.categories.ui.CategoryListFlow

interface CategoriesFeatureEntry {
    @Composable
    fun CategoryListFlow(
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class CategoriesFeatureEntryImpl : CategoriesFeatureEntry {
    @Composable
    override fun CategoryListFlow(onBack: () -> Unit, modifier: Modifier) {
        com.arduia.expense.feature.categories.ui.CategoryListFlow(onBack = onBack, modifier = modifier)
    }
}

object CategoriesFeatureUi : CategoriesFeatureEntry by CategoriesFeatureEntryImpl()
