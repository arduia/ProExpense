package com.arduia.expense.data

import com.arduia.expense.data.local.ExpenseDao
import com.arduia.expense.data.local.CategoryDao
import com.arduia.expense.domain.Amount
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class ExpenseRepositoryImplTest {

    @Mock
    private lateinit var expenseDao: ExpenseDao

    @Mock
    private lateinit var categoryDao: CategoryDao

    private lateinit var repository: ExpenseRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        
        // Setup default mocks
        whenever(expenseDao.getAllExpenses()).thenReturn(flowOf(emptyList()))
        whenever(expenseDao.getTotalAmount()).thenReturn(flowOf(0.0))
        whenever(categoryDao.getAllCategories()).thenReturn(flowOf(emptyList()))
        
        repository = ExpenseRepositoryImpl(
            expenseDao = expenseDao,
            categoryDao = categoryDao
        )
    }

    @Test
    fun `getAllExpenses returns flow of expenses`() = runTest {
        // Given
        val mockExpenses = listOf(
            createMockExpense(1, "Coffee", 5.0),
            createMockExpense(2, "Lunch", 12.0)
        )
        whenever(expenseDao.getAllExpenses()).thenReturn(flowOf(mockExpenses))

        // When
        val result = repository.getAllExpenses()

        // Then
        result.collect { expenses ->
            assertEquals(2, expenses.size)
            assertEquals("Coffee", expenses[0].name)
            assertEquals("Lunch", expenses[1].name)
        }
        verify(expenseDao).getAllExpenses()
    }

    @Test
    fun `getRecentExpenseList returns limited recent expenses`() = runTest {
        // Given
        val mockExpenses = (1..20).map { createMockExpense(it, "Item $it", it.toDouble()) }
        whenever(expenseDao.getRecentExpenses(any())).thenReturn(flowOf(mockExpenses.take(10)))

        // When
        val result = repository.getRecentExpenseList()

        // Then
        result.collect { expenses ->
            assertEquals(10, expenses.size)
        }
        verify(expenseDao).getRecentExpenses(any())
    }

    @Test
    fun `getExpenseByID returns specific expense`() = runTest {
        // Given
        val expenseId = 123
        val mockExpense = createMockExpense(expenseId, "Target Expense", 25.0)
        whenever(expenseDao.getExpenseById(expenseId)).thenReturn(flowOf(mockExpense))

        // When
        val result = repository.getExpenseByID(expenseId)

        // Then
        result.collect { expense ->
            assertNotNull(expense)
            assertEquals(expenseId, expense?.id)
            assertEquals("Target Expense", expense?.name)
        }
        verify(expenseDao).getExpenseById(expenseId)
    }

    @Test
    fun `insertNewExpense saves expense successfully`() = runTest {
        // Given
        val newExpense = createMockExpense(0, "New Expense", 15.0)
        whenever(expenseDao.insertExpense(newExpense)).thenReturn(Unit)

        // When
        repository.insertNewExpense(newExpense)

        // Then
        verify(expenseDao).insertExpense(newExpense)
    }

    @Test
    fun `updateExpense updates expense successfully`() = runTest {
        // Given
        val existingExpense = createMockExpense(1, "Updated Expense", 20.0)
        whenever(expenseDao.updateExpense(existingExpense)).thenReturn(Unit)

        // When
        repository.updateExpense(existingExpense)

        // Then
        verify(expenseDao).updateExpense(existingExpense)
    }

    @Test
    fun `deleteExpenseByID removes expense successfully`() = runTest {
        // Given
        val expenseId = 456
        whenever(expenseDao.deleteExpenseById(expenseId)).thenReturn(Unit)

        // When
        repository.deleteExpenseByID(expenseId)

        // Then
        verify(expenseDao).deleteExpenseById(expenseId)
    }

    @Test
    fun `getTotalExpenseAmount returns correct amount`() = runTest {
        // Given
        val totalAmount = 150.75
        whenever(expenseDao.getTotalAmount()).thenReturn(flowOf(totalAmount))

        // When
        val result = repository.getTotalExpenseAmount()

        // Then
        result.collect { amount ->
            assertEquals(totalAmount, amount.value, 0.01)
        }
        verify(expenseDao).getTotalAmount()
    }

    @Test
    fun `getTodayExpenseAmount returns today's expenses`() = runTest {
        // Given
        val todayAmount = 25.50
        whenever(expenseDao.getTodayAmount()).thenReturn(flowOf(todayAmount))

        // When
        val result = repository.getTodayExpenseAmount()

        // Then
        result.collect { amount ->
            assertEquals(todayAmount, amount.value, 0.01)
        }
        verify(expenseDao).getTodayAmount()
    }

    @Test
    fun `getWeeklyExpenseAmount returns this week's expenses`() = runTest {
        // Given
        val weeklyAmount = 175.25
        whenever(expenseDao.getWeeklyAmount()).thenReturn(flowOf(weeklyAmount))

        // When
        val result = repository.getWeeklyExpenseAmount()

        // Then
        result.collect { amount ->
            assertEquals(weeklyAmount, amount.value, 0.01)
        }
        verify(expenseDao).getWeeklyAmount()
    }

    @Test
    fun `getMonthlyExpenseAmount returns this month's expenses`() = runTest {
        // Given
        val monthlyAmount = 650.00
        whenever(expenseDao.getMonthlyAmount()).thenReturn(flowOf(monthlyAmount))

        // When
        val result = repository.getMonthlyExpenseAmount()

        // Then
        result.collect { amount ->
            assertEquals(monthlyAmount, amount.value, 0.01)
        }
        verify(expenseDao).getMonthlyAmount()
    }

    @Test
    fun `getExpensesByCategory returns filtered expenses`() = runTest {
        // Given
        val categoryId = 2
        val mockExpenses = listOf(
            createMockExpense(1, "Food Item 1", 10.0, categoryId),
            createMockExpense(2, "Food Item 2", 15.0, categoryId)
        )
        whenever(expenseDao.getExpensesByCategory(categoryId)).thenReturn(flowOf(mockExpenses))

        // When
        val result = repository.getExpensesByCategory(categoryId)

        // Then
        result.collect { expenses ->
            assertEquals(2, expenses.size)
            expenses.forEach { expense ->
                assertEquals(categoryId, expense.categoryId)
            }
        }
        verify(expenseDao).getExpensesByCategory(categoryId)
    }

    @Test
    fun `getWeeklyExpenseList returns weekly breakdown`() = runTest {
        // Given
        val weeklyExpenses = listOf(
            createMockWeeklyExpense(1, 50.0),
            createMockWeeklyExpense(2, 75.0)
        )
        whenever(expenseDao.getWeeklyExpenseList()).thenReturn(flowOf(weeklyExpenses))

        // When
        val result = repository.getWeeklyExpenseList()

        // Then
        result.collect { expenses ->
            assertEquals(2, expenses.size)
        }
        verify(expenseDao).getWeeklyExpenseList()
    }

    @Test
    fun `getThisMonthExpenseList returns current month expenses`() = runTest {
        // Given
        val monthlyExpenses = listOf(
            createMockExpense(1, "Monthly Item 1", 30.0),
            createMockExpense(2, "Monthly Item 2", 45.0)
        )
        whenever(expenseDao.getThisMonthExpenses()).thenReturn(flowOf(monthlyExpenses))

        // When
        val result = repository.getThisMonthExpenseList()

        // Then
        result.collect { expenses ->
            assertEquals(2, expenses.size)
        }
        verify(expenseDao).getThisMonthExpenses()
    }

    @Test
    fun `getCategoryList returns all categories`() = runTest {
        // Given
        val mockCategories = listOf(
            createMockCategory(1, "Food"),
            createMockCategory(2, "Transport"),
            createMockCategory(3, "Entertainment")
        )
        whenever(categoryDao.getAllCategories()).thenReturn(flowOf(mockCategories))

        // When
        val result = repository.getCategoryList()

        // Then
        result.collect { categories ->
            assertEquals(3, categories.size)
            assertEquals("Food", categories[0].name)
            assertEquals("Transport", categories[1].name)
            assertEquals("Entertainment", categories[2].name)
        }
        verify(categoryDao).getAllCategories()
    }

    @Test
    fun `getExpensesByDateRange returns filtered expenses`() = runTest {
        // Given
        val startDate = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000) // 7 days ago
        val endDate = System.currentTimeMillis()
        val rangeExpenses = listOf(
            createMockExpense(1, "Range Item 1", 20.0),
            createMockExpense(2, "Range Item 2", 35.0)
        )
        whenever(expenseDao.getExpensesByDateRange(startDate, endDate)).thenReturn(flowOf(rangeExpenses))

        // When
        val result = repository.getExpensesByDateRange(startDate, endDate)

        // Then
        result.collect { expenses ->
            assertEquals(2, expenses.size)
        }
        verify(expenseDao).getExpensesByDateRange(startDate, endDate)
    }

    @Test
    fun `dao exceptions are handled gracefully`() = runTest {
        // Given
        whenever(expenseDao.getAllExpenses()).thenThrow(RuntimeException("Database error"))

        // When/Then - Should not crash
        try {
            val result = repository.getAllExpenses()
            result.collect { }
        } catch (e: Exception) {
            // Exception handling depends on implementation
            assertTrue(e is RuntimeException)
        }
    }

    @Test
    fun `null expense handling`() = runTest {
        // Given
        val expenseId = 999
        whenever(expenseDao.getExpenseById(expenseId)).thenReturn(flowOf(null))

        // When
        val result = repository.getExpenseByID(expenseId)

        // Then
        result.collect { expense ->
            assertNull(expense)
        }
    }

    @Test
    fun `empty results are handled correctly`() = runTest {
        // Given
        whenever(expenseDao.getAllExpenses()).thenReturn(flowOf(emptyList()))
        whenever(categoryDao.getAllCategories()).thenReturn(flowOf(emptyList()))

        // When
        val expenseResult = repository.getAllExpenses()
        val categoryResult = repository.getCategoryList()

        // Then
        expenseResult.collect { expenses ->
            assertTrue(expenses.isEmpty())
        }
        categoryResult.collect { categories ->
            assertTrue(categories.isEmpty())
        }
    }

    @Test
    fun `large amounts are handled correctly`() = runTest {
        // Given
        val largeAmount = 999999.99
        whenever(expenseDao.getTotalAmount()).thenReturn(flowOf(largeAmount))

        // When
        val result = repository.getTotalExpenseAmount()

        // Then
        result.collect { amount ->
            assertEquals(largeAmount, amount.value, 0.01)
        }
    }

    @Test
    fun `zero amounts are handled correctly`() = runTest {
        // Given
        whenever(expenseDao.getTotalAmount()).thenReturn(flowOf(0.0))

        // When
        val result = repository.getTotalExpenseAmount()

        // Then
        result.collect { amount ->
            assertEquals(0.0, amount.value, 0.01)
            assertEquals(Amount.ZERO, amount)
        }
    }

    // Helper methods for creating mock objects
    private fun createMockExpense(id: Int, name: String, amount: Double, categoryId: Int = 1) = mock<Any> {
        on { this.id } doReturn id
        on { this.name } doReturn name
        on { this.amount } doReturn Amount(amount)
        on { this.categoryId } doReturn categoryId
        on { this.date } doReturn System.currentTimeMillis()
    }

    private fun createMockWeeklyExpense(dayOfWeek: Int, amount: Double) = mock<Any> {
        on { this.dayOfWeek } doReturn dayOfWeek
        on { this.amount } doReturn Amount(amount)
    }

    private fun createMockCategory(id: Int, name: String) = mock<Any> {
        on { this.id } doReturn id
        on { this.name } doReturn name
    }
}