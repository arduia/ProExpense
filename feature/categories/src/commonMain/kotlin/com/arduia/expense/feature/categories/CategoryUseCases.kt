package com.arduia.expense.feature.categories

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.UNCATEGORIZED_CATEGORY_ID

/** Creates a new custom category or renames an existing one (design plan §CategoriesViewModel). */
class SaveCategoryUseCase(
    private val categoryRepository: CategoryRepository,
    private val nowEpochMillis: () -> Long,
) {
    suspend operator fun invoke(
        categories: List<Category>,
        editingId: String?,
        name: String,
        iconId: String = "",
        type: RecordType = RecordType.EXPENSE,
        colorId: String = "",
    ) {
        val existing = editingId?.let { id -> categories.firstOrNull { it.id.value == id } }
        val nextSortOrder = (categories.maxOfOrNull { it.sortOrder } ?: -1) + 1
        categoryRepository.upsert(
            Category(
                id = CategoryId(existing?.id?.value ?: newCategoryId(name)),
                name = name,
                isCustom = true,
                sortOrder = existing?.sortOrder ?: nextSortOrder,
                iconId = iconId,
                colorId = colorId,
                type = type,
            ),
        )
    }

    private fun newCategoryId(name: String): String = name.trim().lowercase() + "-" + nowEpochMillis()
}

/** Deleting a category reassigns its linked records to Uncategorized before removing it. */
class DeleteCategoryUseCase(
    private val categoryRepository: CategoryRepository,
    private val financeRecordRepository: FinanceRecordRepository,
) {
    suspend operator fun invoke(id: String) {
        val records = (financeRecordRepository.getAll() as? Result.Success)?.data.orEmpty()
        records.filter { it.categoryId.value == id }.forEach { record ->
            financeRecordRepository.upsert(record.copy(categoryId = CategoryId(UNCATEGORIZED_CATEGORY_ID)))
        }
        categoryRepository.delete(CategoryId(id))
    }
}

/** Persists a user-chosen order across all categories — default and custom can be freely interleaved. */
class ReorderCategoriesUseCase(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(orderedIds: List<String>) {
        categoryRepository.reorder(orderedIds.map(::CategoryId))
    }
}
