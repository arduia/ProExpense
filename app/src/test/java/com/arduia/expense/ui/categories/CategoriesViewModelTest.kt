package com.arduia.expense.ui.categories

import com.arduia.expense.domain.Category
import com.arduia.expense.fake.FakeCategoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CategoriesViewModelTest {

    private fun TestScope.viewModel(repo: FakeCategoryRepository) = CategoriesViewModel(
        categoryRepository = repo,
        scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
    )

    // Rule: stored categories split into built-in defaults and custom rows.
    @Test
    fun splitsDefaultsAndCustom() = runTest {
        val repo = FakeCategoryRepository(
            listOf(
                Category("food", "Food", isCustom = false),
                Category("coffee", "Coffee runs", isCustom = true),
            ),
        )
        val vm = viewModel(repo)
        assertEquals(listOf("food"), vm.state.value.list.defaults.map { it.categoryId })
        assertEquals(listOf("coffee"), vm.state.value.list.custom.map { it.categoryId })
    }

    // Rule: create persists a custom category keyed by its icon id.
    @Test
    fun createPersistsCustom() = runTest {
        val repo = FakeCategoryRepository()
        val vm = viewModel(repo)

        vm.create("pet", "Pet care")

        val saved = repo.store["pet"]
        assertEquals("Pet care", saved?.name)
        assertTrue(saved?.isCustom == true)
        assertEquals(listOf("pet"), vm.state.value.list.custom.map { it.categoryId })
    }

    // Rule: editing the icon re-keys the row (old id removed, new id added).
    @Test
    fun updateWithNewIconRekeys() = runTest {
        val repo = FakeCategoryRepository(listOf(Category("coffee", "Coffee", isCustom = true)))
        val vm = viewModel(repo)

        vm.update(oldId = "coffee", iconId = "pet", label = "Coffee")

        assertFalse(repo.store.containsKey("coffee"))
        assertTrue(repo.store.containsKey("pet"))
    }

    // Rule: delete removes the category.
    @Test
    fun deleteRemoves() = runTest {
        val repo = FakeCategoryRepository(listOf(Category("coffee", "Coffee", isCustom = true)))
        val vm = viewModel(repo)

        vm.delete("coffee")

        assertTrue(vm.state.value.list.custom.isEmpty())
    }
}
