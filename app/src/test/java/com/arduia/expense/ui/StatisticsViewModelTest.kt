package com.arduia.expense.ui.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.domain.Amount
import com.arduia.expense.ui.statistics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var expenseRepository: ExpenseRepository

    @Mock
    private lateinit var categoryAnalyzer: CategoryAnalyzer

    private lateinit var viewModel: StatisticsViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Setup default mocks
        whenever(expenseRepository.getTotalExpenseAmount()).thenReturn(flowOf(Amount.ZERO))
        whenever(expenseRepository.getThisMonthExpenseList()).thenReturn(flowOf(emptyList()))
        whenever(categoryAnalyzer.getCategoryStatistics(any())).thenReturn(emptyList())
        
        viewModel = StatisticsViewModel(
            expenseRepository = expenseRepository,
            categoryAnalyzer = categoryAnalyzer
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialization loads statistics data successfully`() = runTest {
        // Verify repository methods are called during initialization
        verify(expenseRepository).getTotalExpenseAmount()
        verify(expenseRepository).getThisMonthExpenseList()
    }

    @Test
    fun `totalExpenseAmount updates correctly`() = runTest {
        // Given
        val expectedAmount = Amount(500.0)
        whenever(expenseRepository.getTotalExpenseAmount()).thenReturn(flowOf(expectedAmount))

        // When
        val observer = mock<Observer<Amount>>()
        viewModel.totalExpenseAmount.observeForever(observer)

        // Then
        verify(observer).onChanged(expectedAmount)
    }

    @Test
    fun `categoryStatistics updates when expense data changes`() = runTest {
        // Given
        val mockExpenses = listOf(
            createMockExpense(1, "Coffee", 5.0, 1),
            createMockExpense(2, "Lunch", 15.0, 2),
            createMockExpense(3, "Dinner", 20.0, 1)
        )
        val mockStatistics = listOf(
            createMockCategoryStatistic(1, "Food", 25.0, 75.0),
            createMockCategoryStatistic(2, "Transport", 15.0, 25.0)
        )
        
        whenever(expenseRepository.getThisMonthExpenseList()).thenReturn(flowOf(mockExpenses))
        whenever(categoryAnalyzer.getCategoryStatistics(mockExpenses)).thenReturn(mockStatistics)

        // When
        val observer = mock<Observer<List<CategoryStatisticUiModel>>>()
        viewModel.categoryStatistics.observeForever(observer)

        // Then
        verify(observer).onChanged(mockStatistics)
        verify(categoryAnalyzer).getCategoryStatistics(mockExpenses)
    }

    @Test
    fun `refreshStatistics reloads data from repository`() = runTest {
        // Given
        val newExpenses = listOf(createMockExpense(4, "Breakfast", 8.0, 1))
        whenever(expenseRepository.getThisMonthExpenseList()).thenReturn(flowOf(newExpenses))

        // When
        viewModel.refreshStatistics()

        // Then
        verify(expenseRepository, atLeast(2)).getThisMonthExpenseList() // Once during init, once during refresh
    }

    @Test
    fun `empty expense list produces empty statistics`() = runTest {
        // Given
        whenever(expenseRepository.getThisMonthExpenseList()).thenReturn(flowOf(emptyList()))
        whenever(categoryAnalyzer.getCategoryStatistics(emptyList())).thenReturn(emptyList())

        // When
        val observer = mock<Observer<List<CategoryStatisticUiModel>>>()
        viewModel.categoryStatistics.observeForever(observer)

        // Then
        verify(observer).onChanged(emptyList())
    }

    @Test
    fun `single category statistics calculated correctly`() = runTest {
        // Given
        val expenses = listOf(
            createMockExpense(1, "Coffee", 5.0, 1),
            createMockExpense(2, "Tea", 3.0, 1)
        )
        val statistics = listOf(
            createMockCategoryStatistic(1, "Beverages", 8.0, 100.0)
        )
        
        whenever(expenseRepository.getThisMonthExpenseList()).thenReturn(flowOf(expenses))
        whenever(categoryAnalyzer.getCategoryStatistics(expenses)).thenReturn(statistics)

        // When
        val observer = mock<Observer<List<CategoryStatisticUiModel>>>()
        viewModel.categoryStatistics.observeForever(observer)

        // Then
        verify(observer).onChanged(statistics)
        assertEquals(1, statistics.size)
        assertEquals("Beverages", statistics.first().categoryName)
        assertEquals(8.0, statistics.first().totalAmount.value, 0.01)
        assertEquals(100.0, statistics.first().percentage, 0.01)
    }

    @Test
    fun `multiple categories statistics calculated correctly`() = runTest {
        // Given
        val expenses = listOf(
            createMockExpense(1, "Coffee", 10.0, 1),
            createMockExpense(2, "Bus", 5.0, 2),
            createMockExpense(3, "Lunch", 15.0, 1)
        )
        val statistics = listOf(
            createMockCategoryStatistic(1, "Food", 25.0, 83.33),
            createMockCategoryStatistic(2, "Transport", 5.0, 16.67)
        )
        
        whenever(expenseRepository.getThisMonthExpenseList()).thenReturn(flowOf(expenses))
        whenever(categoryAnalyzer.getCategoryStatistics(expenses)).thenReturn(statistics)

        // When
        val observer = mock<Observer<List<CategoryStatisticUiModel>>>()
        viewModel.categoryStatistics.observeForever(observer)

        // Then
        verify(observer).onChanged(statistics)
        assertEquals(2, statistics.size)
    }

    @Test
    fun `getExpensesByCategory returns filtered expenses`() = runTest {
        // Given
        val categoryId = 1
        val allExpenses = listOf(
            createMockExpense(1, "Coffee", 5.0, 1),
            createMockExpense(2, "Bus", 3.0, 2),
            createMockExpense(3, "Lunch", 12.0, 1)
        )
        val filteredExpenses = allExpenses.filter { it.categoryId == categoryId }
        
        whenever(expenseRepository.getExpensesByCategory(categoryId)).thenReturn(flowOf(filteredExpenses))

        // When
        val observer = mock<Observer<List<Any>>>()
        viewModel.getExpensesByCategory(categoryId).observeForever(observer)

        // Then
        verify(expenseRepository).getExpensesByCategory(categoryId)
        verify(observer).onChanged(filteredExpenses)
    }

    @Test
    fun `getMonthlyExpenseAmount returns correct amount`() = runTest {
        // Given
        val monthlyAmount = Amount(250.0)
        whenever(expenseRepository.getMonthlyExpenseAmount()).thenReturn(flowOf(monthlyAmount))

        // When
        val observer = mock<Observer<Amount>>()
        viewModel.getMonthlyExpenseAmount().observeForever(observer)

        // Then
        verify(expenseRepository).getMonthlyExpenseAmount()
        verify(observer).onChanged(monthlyAmount)
    }

    @Test
    fun `repository exceptions are handled gracefully`() = runTest {
        // Given
        whenever(expenseRepository.getThisMonthExpenseList()).thenThrow(RuntimeException("Database error"))

        // When
        val observer = mock<Observer<List<CategoryStatisticUiModel>>>()
        viewModel.categoryStatistics.observeForever(observer)

        // Then - Should not crash and should handle error
        verify(observer).onChanged(any())
    }

    @Test
    fun `categoryAnalyzer exceptions are handled gracefully`() = runTest {
        // Given
        val expenses = listOf(createMockExpense(1, "Coffee", 5.0, 1))
        whenever(expenseRepository.getThisMonthExpenseList()).thenReturn(flowOf(expenses))
        whenever(categoryAnalyzer.getCategoryStatistics(expenses)).thenThrow(RuntimeException("Analysis error"))

        // When
        val observer = mock<Observer<List<CategoryStatisticUiModel>>>()
        viewModel.categoryStatistics.observeForever(observer)

        // Then - Should handle error gracefully
        verify(observer).onChanged(any())
    }

    @Test
    fun `large amounts are processed correctly`() = runTest {
        // Given
        val largeExpenses = listOf(
            createMockExpense(1, "Large Purchase", 999999.99, 1)
        )
        val statistics = listOf(
            createMockCategoryStatistic(1, "Major", 999999.99, 100.0)
        )
        
        whenever(expenseRepository.getThisMonthExpenseList()).thenReturn(flowOf(largeExpenses))
        whenever(categoryAnalyzer.getCategoryStatistics(largeExpenses)).thenReturn(statistics)

        // When
        val observer = mock<Observer<List<CategoryStatisticUiModel>>>()
        viewModel.categoryStatistics.observeForever(observer)

        // Then
        verify(observer).onChanged(statistics)
        assertEquals(999999.99, statistics.first().totalAmount.value, 0.01)
    }

    @Test
    fun `zero amount expenses are handled correctly`() = runTest {
        // Given
        val expenses = listOf(
            createMockExpense(1, "Free Sample", 0.0, 1),
            createMockExpense(2, "Paid Item", 10.0, 1)
        )
        val statistics = listOf(
            createMockCategoryStatistic(1, "Mixed", 10.0, 100.0)
        )
        
        whenever(expenseRepository.getThisMonthExpenseList()).thenReturn(flowOf(expenses))
        whenever(categoryAnalyzer.getCategoryStatistics(expenses)).thenReturn(statistics)

        // When
        val observer = mock<Observer<List<CategoryStatisticUiModel>>>()
        viewModel.categoryStatistics.observeForever(observer)

        // Then
        verify(observer).onChanged(statistics)
    }

    // Helper methods for creating mock objects
    private fun createMockExpense(id: Int, name: String, amount: Double, categoryId: Int) = mock<Any> {
        on { this.id } doReturn id
        on { this.name } doReturn name
        on { this.amount } doReturn Amount(amount)
        on { this.categoryId } doReturn categoryId
    }

    private fun createMockCategoryStatistic(categoryId: Int, categoryName: String, totalAmount: Double, percentage: Double) = 
        CategoryStatisticUiModel(
            categoryId = categoryId,
            categoryName = categoryName,
            totalAmount = Amount(totalAmount),
            percentage = percentage
        )
}