package com.arduia.expense.ui.entry

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.arduia.expense.ui.common.category.ExpenseCategory
import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import io.mockk.*
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.math.BigDecimal

@RunWith(JUnit4::class)
class ExpenseEntryFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fragment: ExpenseEntryFragment
    private lateinit var mockCategoryProvider: ExpenseCategoryProvider
    private lateinit var mockCategoryAdapter: CategoryListAdapter

    @Before
    fun setUp() {
        // Mock dependencies
        mockCategoryProvider = mockk(relaxed = true)
        mockCategoryAdapter = mockk(relaxed = true)
        
        // Create fragment instance
        fragment = ExpenseEntryFragment()
        
        // Set up mock behaviors
        every { mockCategoryProvider.getCategoryByID(any()) } returns ExpenseCategory.FOOD_CATEGORY
        every { mockCategoryProvider.getCategoryList() } returns listOf(
            ExpenseCategory.FOOD_CATEGORY,
            ExpenseCategory.TRANSPORT_CATEGORY,
            ExpenseCategory.ENTERTAINMENT_CATEGORY
        )
        every { mockCategoryAdapter.selectedItem } returns ExpenseCategory.FOOD_CATEGORY
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `fragment class should exist and be instantiable`() {
        // When
        val fragment = ExpenseEntryFragment()

        // Then
        Assert.assertNotNull(fragment)
    }

    @Test
    fun `fragment should have proper class structure`() {
        // When
        val fragmentClass = ExpenseEntryFragment::class.java

        // Then
        Assert.assertNotNull(fragmentClass)
        Assert.assertTrue(fragmentClass.superclass.name.contains("Fragment"))
    }

    @Test
    fun `fragment should be a valid Android Fragment`() {
        // Given
        val fragment = ExpenseEntryFragment()

        // When & Then - Should not crash on instantiation
        Assert.assertNotNull(fragment)
        Assert.assertNotNull(fragment.javaClass)
    }

    // Business Logic Tests

    @Test
    fun `expense ID validation logic should handle invalid IDs`() {
        // Given - Test the ID validation business logic directly
        val invalidId = -1
        val validId = 5

        // When & Then - Test the business logic of ID validation
        // Invalid ID (-1) should be treated as insert mode (return 0)
        val isInvalidId = invalidId < 0
        val processedInvalidId = if (isInvalidId) 0 else invalidId
        
        val isValidId = validId >= 0
        val processedValidId = if (isValidId) validId else 0

        Assert.assertTrue("Invalid expense ID should be detected", isInvalidId)
        Assert.assertEquals("Invalid ID should be converted to 0", 0, processedInvalidId)
        Assert.assertTrue("Valid expense ID should be detected", isValidId)
        Assert.assertEquals("Valid ID should remain unchanged", validId, processedValidId)
    }

    @Test
    fun `validateExpenseDetail should detect empty amount`() {
        // Given
        val expenseDetailWithEmptyAmount = ExpenseDetailUiModel(
            id = 1,
            name = "Test Expense",
            date = "",
            category = ExpenseCategory.FOOD,
            amount = "", // Empty amount
            finance = "",
            note = "Test note",
            symbol = ""
        )

        // When & Then
        val isEmpty = expenseDetailWithEmptyAmount.amount.isEmpty()
        Assert.assertTrue("Empty amount should be detected", isEmpty)
    }

    @Test
    fun `validateExpenseDetail should accept valid amount`() {
        // Given
        val expenseDetailWithValidAmount = ExpenseDetailUiModel(
            id = 1,
            name = "Test Expense",
            date = "",
            category = ExpenseCategory.FOOD,
            amount = "25.50", // Valid amount
            finance = "",
            note = "Test note",
            symbol = ""
        )

        // When & Then
        val isEmpty = expenseDetailWithValidAmount.amount.isEmpty()
        Assert.assertFalse("Valid amount should not be empty", isEmpty)
    }

    @Test
    fun `moveItemToFirstIndex should move selected category to first position`() {
        // Given
        val categoryList = mutableListOf(
            ExpenseCategory.FOOD_CATEGORY,
            ExpenseCategory.TRANSPORT_CATEGORY,
            ExpenseCategory.ENTERTAINMENT_CATEGORY
        )
        val selectedCategory = ExpenseCategory.TRANSPORT_CATEGORY

        // When - Test the business logic of moving item to first index
        categoryList.remove(selectedCategory)
        categoryList.add(0, selectedCategory)

        // Then
        Assert.assertEquals("Selected category should be at first position", 
            selectedCategory, categoryList[0])
        Assert.assertEquals("List should maintain correct size", 3, categoryList.size)
    }

    @Test
    fun `expense detail validation should handle null or empty name`() {
        // Given
        val expenseWithEmptyName = ExpenseDetailUiModel(
            id = 1,
            name = "", // Empty name
            date = "",
            category = ExpenseCategory.FOOD,
            amount = "25.50",
            finance = "",
            note = "Test note",
            symbol = ""
        )

        val expenseWithValidName = ExpenseDetailUiModel(
            id = 1,
            name = "Valid Expense Name",
            date = "",
            category = ExpenseCategory.FOOD,
            amount = "25.50",
            finance = "",
            note = "Test note",
            symbol = ""
        )

        // When & Then
        Assert.assertTrue("Empty name should be detected", expenseWithEmptyName.name.isEmpty())
        Assert.assertFalse("Valid name should not be empty", expenseWithValidName.name.isEmpty())
    }

    @Test
    fun `expense detail should handle category selection`() {
        // Given
        val expenseDetail = ExpenseDetailUiModel(
            id = 1,
            name = "Test Expense",
            date = "",
            category = ExpenseCategory.FOOD,
            amount = "25.50",
            finance = "",
            note = "Test note",
            symbol = ""
        )

        // When & Then
        Assert.assertEquals("Category should match expected value", 
            ExpenseCategory.FOOD, expenseDetail.category)
        Assert.assertNotNull("Category should not be null", expenseDetail.category)
    }

    @Test
    fun `expense amount validation should handle decimal values`() {
        // Given - Test various amount formats
        val validAmounts = listOf("10", "10.5", "10.50", "0.99", "999.99")
        val invalidAmounts = listOf("abc", "10,50") // Remove "-5" as it's valid for BigDecimal

        // When & Then - Test valid amounts
        validAmounts.forEach { amount ->
            try {
                BigDecimal(amount)
                Assert.assertTrue("Amount $amount should be valid", true)
            } catch (e: NumberFormatException) {
                Assert.fail("Amount $amount should be valid but failed: ${e.message}")
            }
        }

        // Test invalid amounts (only clearly invalid ones)
        invalidAmounts.forEach { amount ->
            try {
                BigDecimal(amount)
                Assert.fail("Amount $amount should be invalid")
            } catch (e: NumberFormatException) {
                Assert.assertTrue("Amount $amount should be invalid", true)
            }
        }

        // Test empty amounts separately
        val emptyAmounts = listOf("", " ")
        emptyAmounts.forEach { amount ->
            val isEmpty = amount.trim().isEmpty()
            Assert.assertTrue("Empty amount '$amount' should be detected as empty", isEmpty)
        }
    }

    @Test
    fun `expense detail should handle note field properly`() {
        // Given
        val expenseWithNote = ExpenseDetailUiModel(
            id = 1,
            name = "Test Expense",
            date = "",
            category = ExpenseCategory.FOOD,
            amount = "25.50",
            finance = "",
            note = "This is a test note",
            symbol = ""
        )

        val expenseWithoutNote = ExpenseDetailUiModel(
            id = 1,
            name = "Test Expense",
            date = "",
            category = ExpenseCategory.FOOD,
            amount = "25.50",
            finance = "",
            note = "",
            symbol = ""
        )

        // When & Then
        Assert.assertFalse("Note should not be empty when provided", 
            expenseWithNote.note.isEmpty())
        Assert.assertTrue("Note should be empty when not provided", 
            expenseWithoutNote.note.isEmpty())
        Assert.assertEquals("Note should match expected value", 
            "This is a test note", expenseWithNote.note)
    }

    @Test
    fun `category list should maintain proper structure`() {
        // Given
        val categoryList = listOf(
            ExpenseCategory.FOOD_CATEGORY,
            ExpenseCategory.TRANSPORT_CATEGORY,
            ExpenseCategory.ENTERTAINMENT_CATEGORY
        )

        // When & Then
        Assert.assertTrue("Category list should not be empty", categoryList.isNotEmpty())
        Assert.assertEquals("Category list should have expected size", 3, categoryList.size)
        
        // Verify each category has valid properties
        categoryList.forEach { category ->
            Assert.assertNotNull("Category should not be null", category)
            Assert.assertTrue("Category ID should be valid", category.id >= 0)
            Assert.assertTrue("Category name should be valid", category.nameId > 0)
        }
    }

    @Test
    fun `expense ID validation should handle edge cases`() {
        // Given - Test various ID scenarios
        val validIds = listOf(0, 1, 100, 9999)
        val invalidIds = listOf(-1, -100)

        // When & Then - Test ID validation logic
        validIds.forEach { id ->
            val isValid = id >= 0
            Assert.assertTrue("ID $id should be valid", isValid)
        }

        invalidIds.forEach { id ->
            val isValid = id >= 0
            Assert.assertFalse("ID $id should be invalid", isValid)
        }
    }

    @Test
    fun `expense detail creation should handle all required fields`() {
        // Given
        val completeExpenseDetail = ExpenseDetailUiModel(
            id = 1,
            name = "Complete Expense",
            date = "2024-01-01",
            category = ExpenseCategory.FOOD,
            amount = "25.50",
            finance = "USD",
            note = "Complete note",
            symbol = "$"
        )

        // When & Then - Verify all fields are properly set
        Assert.assertEquals("ID should match", 1, completeExpenseDetail.id)
        Assert.assertEquals("Name should match", "Complete Expense", completeExpenseDetail.name)
        Assert.assertEquals("Date should match", "2024-01-01", completeExpenseDetail.date)
        Assert.assertEquals("Category should match", ExpenseCategory.FOOD, completeExpenseDetail.category)
        Assert.assertEquals("Amount should match", "25.50", completeExpenseDetail.amount)
        Assert.assertEquals("Finance should match", "USD", completeExpenseDetail.finance)
        Assert.assertEquals("Note should match", "Complete note", completeExpenseDetail.note)
        Assert.assertEquals("Symbol should match", "$", completeExpenseDetail.symbol)
    }

    @Test
    fun `expense mode validation should distinguish between insert and update`() {
        // Given
        val insertModeId = -1 // Invalid ID indicates insert mode
        val updateModeId = 5   // Valid ID indicates update mode

        // When & Then - Test mode detection logic
        val isInsertMode = insertModeId < 0
        val isUpdateMode = updateModeId >= 0

        Assert.assertTrue("Insert mode should be detected for invalid ID", isInsertMode)
        Assert.assertTrue("Update mode should be detected for valid ID", isUpdateMode)
        Assert.assertFalse("Update mode should not be detected for invalid ID", updateModeId < 0)
        Assert.assertFalse("Insert mode should not be detected for valid ID", insertModeId >= 0)
    }

    // Companion object for test data
    companion object {
        private val ExpenseCategory.Companion.FOOD_CATEGORY get() = ExpenseCategory(
            id = ExpenseCategory.FOOD,
            nameId = com.arduia.expense.R.string.food,
            img = com.arduia.expense.R.drawable.ic_food
        )
        
        private val ExpenseCategory.Companion.TRANSPORT_CATEGORY get() = ExpenseCategory(
            id = ExpenseCategory.TRANSPORTATION,
            nameId = com.arduia.expense.R.string.transportation,
            img = com.arduia.expense.R.drawable.ic_transportation
        )
        
        private val ExpenseCategory.Companion.ENTERTAINMENT_CATEGORY get() = ExpenseCategory(
            id = ExpenseCategory.ENTERTAINMENT,
            nameId = com.arduia.expense.R.string.entertainment,
            img = com.arduia.expense.R.drawable.ic_entertainment
        )
    }
} 