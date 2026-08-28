package com.arduia.expense.shell

import com.arduia.expense.data.Result
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.UNCATEGORIZED_CATEGORY_ID
import com.arduia.expense.feature.categories.DeleteCategoryUseCase
import com.arduia.expense.feature.categories.ReorderCategoriesUseCase
import com.arduia.expense.feature.categories.SaveCategoryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Backbone coverage for category management.
 *
 * Traceability: US-CAT-1 (create a custom category), US-CAT-2 (rename), and US-CAT-3 (deleting a
 * category reassigns its records to Uncategorized rather than orphaning them).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {
    private fun TestScope.viewModel(
        categories: FakeCategories = FakeCategories(),
        records: FakeRecords = FakeRecords(),
    ): CategoriesViewModel =
        CategoriesViewModel(
            categoryRepository = categories,
            saveCategory = SaveCategoryUseCase(categories) { 1_000L },
            deleteCategory = DeleteCategoryUseCase(categories, records),
            reorderCategories = ReorderCategoriesUseCase(categories),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

    @Test
    fun `the list loads and reports its rows`() =
        runTest {
            val vm = viewModel()

            advanceUntilIdle()

            assertFalse(vm.uiState.value.isLoading)
            assertEquals(
                listOf("Food", "Travel"),
                vm.uiState.value.rows
                    .map { it.name },
            )
        }

    @Test
    fun `creating a custom category persists it and closes the editor`() =
        runTest {
            val categories = FakeCategories()
            val vm = viewModel(categories)
            advanceUntilIdle()

            vm.openCreate()
            vm.onNameChange("  Coffee  ")
            vm.save()
            advanceUntilIdle()

            val created = assertNotNull(categories.current.firstOrNull { it.name == "Coffee" })
            assertTrue(created.isCustom)
            assertFalse(vm.uiState.value.isEditorOpen)
        }

    @Test
    fun `a blank name cannot be saved`() =
        runTest {
            val vm = viewModel()
            advanceUntilIdle()

            vm.openCreate()
            vm.onNameChange("   ")

            assertFalse(vm.uiState.value.canSave)
        }

    @Test
    fun `editing preloads the row and renames in place`() =
        runTest {
            val categories = FakeCategories()
            val vm = viewModel(categories)
            advanceUntilIdle()

            vm.openEdit(categoryId = "food")
            assertEquals("Food", vm.uiState.value.draftName)

            vm.onNameChange("Groceries")
            vm.save()
            advanceUntilIdle()

            assertEquals("Groceries", categories.current.first { it.id.value == "food" }.name)
            // Renaming must not create a second row.
            assertEquals(2, categories.current.size)
        }

    @Test
    fun `a default category cannot be deleted but a custom one can`() =
        runTest {
            val categories =
                FakeCategories(
                    listOf(
                        FakeCategories.category("food", "Food"),
                        FakeCategories.category("coffee", "Coffee", isCustom = true, sortOrder = 1),
                    ),
                )
            val vm = viewModel(categories)
            advanceUntilIdle()

            vm.openEdit(categoryId = "food")
            assertFalse(vm.uiState.value.canDeleteEditing)

            vm.openEdit(categoryId = "coffee")
            assertTrue(vm.uiState.value.canDeleteEditing)
        }

    @Test
    fun `deleting a category reassigns its records to uncategorized`() =
        runTest {
            val categories =
                FakeCategories(listOf(FakeCategories.category("coffee", "Coffee", isCustom = true)))
            val records =
                FakeRecords(
                    listOf(
                        financeRecord(id = "r1", cents = 500, categoryId = "coffee"),
                    ),
                )
            val vm = viewModel(categories, records)
            advanceUntilIdle()

            vm.delete(categoryId = "coffee")
            advanceUntilIdle()

            assertNull(categories.current.firstOrNull { it.id.value == "coffee" })
            assertEquals(
                UNCATEGORIZED_CATEGORY_ID,
                records.current
                    .single()
                    .categoryId.value,
            )
        }

    @Test
    fun `an income category round-trips its type`() =
        runTest {
            val categories = FakeCategories(emptyList())
            val vm = viewModel(categories)
            advanceUntilIdle()

            vm.openCreate()
            vm.onNameChange("Salary")
            vm.onTypeChange(isIncome = true)
            vm.save()
            advanceUntilIdle()

            assertEquals(RecordType.INCOME, categories.current.single().type)
            assertTrue(
                vm.uiState.value.rows
                    .single()
                    .isIncome,
            )
        }

    @Test
    fun `reordering persists the new sort order`() =
        runTest {
            val categories = FakeCategories()
            val vm = viewModel(categories)
            advanceUntilIdle()

            vm.reorder(orderedIds = listOf("travel", "food"))
            advanceUntilIdle()

            assertEquals(
                listOf("Travel", "Food"),
                vm.uiState.value.rows
                    .map { it.name },
            )
        }

    @Test
    fun `closing the editor drops the in-progress draft`() =
        runTest {
            val categories = FakeCategories()
            val vm = viewModel(categories)
            advanceUntilIdle()

            vm.openCreate()
            vm.onNameChange("Abandoned")
            vm.closeEditor()

            assertFalse(vm.uiState.value.isEditorOpen)
            assertNull(vm.uiState.value.editingId)
            assertTrue((categories.getAll() as Result.Success).data.none { it.name == "Abandoned" })
        }

    private fun financeRecord(
        id: String,
        cents: Long,
        categoryId: String,
    ) = FinanceRecord(
        id = RecordId(id),
        money = money(cents),
        homeCurrencyMoney = money(cents),
        categoryId = CategoryId(categoryId),
        type = RecordType.EXPENSE,
        note = null,
        recordedAtEpochMillis = 0L,
    )
}
