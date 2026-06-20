package com.arduia.expense.feature.logging

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Category
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryListViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Test
    fun onAddCategory_rejectsDuplicateNames() = runTest(dispatcher) {
        val repository = FakeCategoryRepository(
            listOf(Category(id = "food", name = "Food")),
        )
        val viewModel = CategoryListViewModel(repository, this)

        advanceUntilIdle()
        viewModel.onNewCategoryChange("Food")
        viewModel.onAddCategory()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.duplicateError)
        assertEquals(1, repository.categories.size)
    }

    @Test
    fun onAddCategory_enforcesTwentyCharacterLimit() = runTest(dispatcher) {
        val repository = FakeCategoryRepository(emptyList())
        val viewModel = CategoryListViewModel(repository, this)

        viewModel.onNewCategoryChange("A".repeat(25))
        assertEquals(20, viewModel.uiState.value.newCategoryName.length)
    }
}

private class FakeCategoryRepository(
    initial: List<Category>,
) : com.arduia.expense.data.CategoryRepository {
    val categories = initial.toMutableList()

    override suspend fun getAll(): Result<List<Category>> = Result.Success(categories.toList())

    override suspend fun upsert(category: Category): Result<Unit> {
        categories.removeAll { it.id == category.id }
        categories.add(category)
        return Result.Success(Unit)
    }

    override suspend fun delete(id: String): Result<Unit> {
        categories.removeAll { it.id == id }
        return Result.Success(Unit)
    }
}
