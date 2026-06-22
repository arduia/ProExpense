package com.arduia.expense.storage.repository

import com.arduia.expense.domain.Category
import com.arduia.expense.storage.db.DefaultCategories
import com.arduia.expense.storage.db.bootstrap
import com.arduia.expense.storage.getOrFail
import com.arduia.expense.storage.newTestDatabase
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SqlDelightCategoryRepositoryTest {

    private val database = newTestDatabase()
    private val repository = SqlDelightCategoryRepository(database, UnconfinedTestDispatcher())

    // Rule: bootstrap seeds exactly the built-in default categories on a fresh database.
    @Test
    fun freshDatabase_containsSeededDefaults() = runTest {
        val ids = repository.getAll().getOrFail().map { it.id }
        assertEquals(DefaultCategories.all.map { it.id }.toSet(), ids.toSet())
    }

    // Rule: a custom category is persisted and flagged isCustom.
    @Test
    fun upsert_customCategory_isReadBack() = runTest {
        repository.upsert(Category(id = "c_custom", name = "Coffee", isCustom = true)).getOrFail()

        val custom = repository.getAll().getOrFail().single { it.id == "c_custom" }
        assertEquals("Coffee", custom.name)
        assertTrue(custom.isCustom)
    }

    // Rule: defaults are not custom.
    @Test
    fun seededDefaults_areNotCustom() = runTest {
        assertTrue(repository.getAll().getOrFail().none { it.isCustom })
    }

    // Rule: delete removes a category.
    @Test
    fun delete_removesCategory() = runTest {
        repository.delete(DefaultCategories.FOOD_ID).getOrFail()

        assertFalse(repository.getAll().getOrFail().any { it.id == DefaultCategories.FOOD_ID })
    }

    // Rule: bootstrap is idempotent — re-running it never duplicates the seed set.
    @Test
    fun bootstrap_isIdempotent() = runTest {
        database.bootstrap()
        database.bootstrap()

        assertEquals(DefaultCategories.all.size, repository.getAll().getOrFail().size)
    }
}
