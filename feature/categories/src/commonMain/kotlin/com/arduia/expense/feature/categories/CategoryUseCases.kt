package com.arduia.expense.feature.categories

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId

/** Creates a new custom category or renames an existing one (design plan §CategoriesViewModel). */
class SaveCategoryUseCase(
    private val categoryRepository: CategoryRepository,
    private val nowEpochMillis: () -> Long,
) {
    suspend operator fun invoke(categories: List<Category>, editingId: String?, name: String) {
        val existing = editingId?.let { id -> categories.firstOrNull { it.id.value == id } }
        val customCount = categories.count { it.isCustom }
        categoryRepository.upsert(
            Category(
                id = CategoryId(existing?.id?.value ?: newCategoryId(name)),
                name = name,
                isCustom = true,
                sortOrder = existing?.sortOrder ?: customCount,
            ),
        )
    }

    private fun newCategoryId(name: String): String =
        name.trim().lowercase() + "-" + nowEpochMillis()
}

class DeleteCategoryUseCase(private val categoryRepository: CategoryRepository) {
    suspend operator fun invoke(id: String) {
        categoryRepository.delete(CategoryId(id))
    }
}

/** Reorders custom categories while keeping default categories pinned first. */
class ReorderCategoriesUseCase(private val categoryRepository: CategoryRepository) {
    suspend operator fun invoke(defaultIds: List<String>, reorderedCustomIds: List<String>) {
        val orderedIds = (defaultIds + reorderedCustomIds).map(::CategoryId)
        categoryRepository.reorder(orderedIds)
    }
}
