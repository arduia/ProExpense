package com.arduia.expense.feature.categories

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private fun sampleCategory(id: String, name: String = "Food", isCustom: Boolean = true, sortOrder: Int = 0) = Category(
    id = CategoryId(id),
    name = name,
    isCustom = isCustom,
    sortOrder = sortOrder,
)

private class FakeCategoryRepository(
    private val records: MutableMap<String, Category> = mutableMapOf(),
) : CategoryRepository {
    var lastUpsert: Category? = null
    var deletedId: CategoryId? = null
    var lastReorder: List<CategoryId>? = null

    override suspend fun getAll(): Result<List<Category>> = Result.Success(records.values.toList())
    override suspend fun upsert(category: Category): Result<Unit> {
        lastUpsert = category
        records[category.id.value] = category
        return Result.Success(Unit)
    }
    override suspend fun delete(id: CategoryId): Result<Unit> {
        deletedId = id
        records.remove(id.value)
        return Result.Success(Unit)
    }
    override suspend fun reorder(orderedIds: List<CategoryId>): Result<Unit> {
        lastReorder = orderedIds
        return Result.Success(Unit)
    }
    override fun observeAll() = MutableStateFlow(records.values.toList()).asStateFlow()
}

class SaveCategoryUseCaseTest {

    @Test
    fun invoke_createsNewCustomCategoryWithSortOrderAfterExistingCustomOnes() = runTest {
        val repo = FakeCategoryRepository()
        val useCase = SaveCategoryUseCase(repo, nowEpochMillis = { 1_000L })
        val existing = listOf(sampleCategory("a", isCustom = true, sortOrder = 0), sampleCategory("b", isCustom = false, sortOrder = 1))

        useCase(existing, editingId = null, name = "Travel")

        assertEquals("travel-1000", repo.lastUpsert?.id?.value)
        assertEquals(true, repo.lastUpsert?.isCustom)
        assertEquals(1, repo.lastUpsert?.sortOrder)
    }

    @Test
    fun invoke_renamesExistingCategoryPreservingIdAndSortOrder() = runTest {
        val repo = FakeCategoryRepository()
        val useCase = SaveCategoryUseCase(repo, nowEpochMillis = { 1_000L })
        val existing = listOf(sampleCategory("food", name = "Food", sortOrder = 3))

        useCase(existing, editingId = "food", name = "Groceries")

        assertEquals(CategoryId("food"), repo.lastUpsert?.id)
        assertEquals("Groceries", repo.lastUpsert?.name)
        assertEquals(3, repo.lastUpsert?.sortOrder)
    }
}

class DeleteCategoryUseCaseTest {

    @Test
    fun invoke_deletesCategoryById() = runTest {
        val repo = FakeCategoryRepository()
        val useCase = DeleteCategoryUseCase(repo)

        useCase("food")

        assertEquals(CategoryId("food"), repo.deletedId)
    }
}

class ReorderCategoriesUseCaseTest {

    @Test
    fun invoke_pinsDefaultIdsFirstThenCustomIds() = runTest {
        val repo = FakeCategoryRepository()
        val useCase = ReorderCategoriesUseCase(repo)

        useCase(defaultIds = listOf("food", "transport"), reorderedCustomIds = listOf("travel", "gifts"))

        assertEquals(
            listOf(CategoryId("food"), CategoryId("transport"), CategoryId("travel"), CategoryId("gifts")),
            repo.lastReorder,
        )
    }
}
