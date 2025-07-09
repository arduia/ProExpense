package com.arduia.expense.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.domain.Amount
import com.arduia.expense.ui.home.*
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
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var expenseRepository: ExpenseRepository

    @Mock
    private lateinit var expenseRateCalculator: ExpenseRateCalculator

    @Mock
    private lateinit var expenseUiModelMapper: ExpenseUiModelMapper

    @Mock
    private lateinit var expenseDetailUiModelMapper: ExpenseDetailUiModelMapper

    @Mock
    private lateinit var expenseDayNameProvider: ExpenseDayNameProvider

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Setup default mocks
        whenever(expenseRepository.getRecentExpenseList()).thenReturn(flowOf(emptyList()))
        whenever(expenseRepository.getTotalExpenseAmount()).thenReturn(flowOf(Amount.ZERO))
        whenever(expenseRepository.getTodayExpenseAmount()).thenReturn(flowOf(Amount.ZERO))
        whenever(expenseRepository.getWeeklyExpenseAmount()).thenReturn(flowOf(Amount.ZERO))
        whenever(expenseRepository.getMonthlyExpenseAmount()).thenReturn(flowOf(Amount.ZERO))
        whenever(expenseRepository.getWeeklyExpenseList()).thenReturn(flowOf(emptyList()))
        
        viewModel = HomeViewModel(
            expenseRepository = expenseRepository,
            expenseRateCalculator = expenseRateCalculator,
            expenseUiModelMapper = expenseUiModelMapper,
            expenseDetailUiModelMapper = expenseDetailUiModelMapper,
            expenseDayNameProvider = expenseDayNameProvider
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialization loads all data successfully`() = runTest {
        // Verify all repository methods are called during initialization
        verify(expenseRepository).getRecentExpenseList()
        verify(expenseRepository).getTotalExpenseAmount()
        verify(expenseRepository).getTodayExpenseAmount()
        verify(expenseRepository).getWeeklyExpenseAmount()
        verify(expenseRepository).getMonthlyExpenseAmount()
        verify(expenseRepository).getWeeklyExpenseList()
    }

    @Test
    fun `recentExpenseList updates when repository data changes`() = runTest {
        // Given
        val mockExpenses = listOf(
            createMockExpense(1, "Coffee", 5.0),
            createMockExpense(2, "Lunch", 12.0)
        )
        val mockUiModels = mockExpenses.map { createMockUiModel(it.id) }
        
        whenever(expenseRepository.getRecentExpenseList()).thenReturn(flowOf(mockExpenses))
        whenever(expenseUiModelMapper.map(any())).thenReturn(mockUiModels.first(), *mockUiModels.drop(1).toTypedArray())

        // When
        val observer = mock<Observer<List<Any>>>()
        viewModel.recentExpenseList.observeForever(observer)

        // Then
        verify(observer).onChanged(mockUiModels)
    }

    @Test
    fun `totalExpenseAmount updates correctly`() = runTest {
        // Given
        val expectedAmount = Amount(100.0)
        whenever(expenseRepository.getTotalExpenseAmount()).thenReturn(flowOf(expectedAmount))

        // When
        val observer = mock<Observer<Amount>>()
        viewModel.totalExpenseAmount.observeForever(observer)

        // Then
        verify(observer).onChanged(expectedAmount)
    }

    @Test
    fun `todayExpenseAmount updates correctly`() = runTest {
        // Given
        val expectedAmount = Amount(25.0)
        whenever(expenseRepository.getTodayExpenseAmount()).thenReturn(flowOf(expectedAmount))

        // When
        val observer = mock<Observer<Amount>>()
        viewModel.todayExpenseAmount.observeForever(observer)

        // Then
        verify(observer).onChanged(expectedAmount)
    }

    @Test
    fun `weeklyExpenseAmount updates correctly`() = runTest {
        // Given
        val expectedAmount = Amount(75.0)
        whenever(expenseRepository.getWeeklyExpenseAmount()).thenReturn(flowOf(expectedAmount))

        // When
        val observer = mock<Observer<Amount>>()
        viewModel.weeklyExpenseAmount.observeForever(observer)

        // Then
        verify(observer).onChanged(expectedAmount)
    }

    @Test
    fun `monthlyExpenseAmount updates correctly`() = runTest {
        // Given
        val expectedAmount = Amount(300.0)
        whenever(expenseRepository.getMonthlyExpenseAmount()).thenReturn(flowOf(expectedAmount))

        // When
        val observer = mock<Observer<Amount>>()
        viewModel.monthlyExpenseAmount.observeForever(observer)

        // Then
        verify(observer).onChanged(expectedAmount)
    }

    @Test
    fun `weeklyGraphUiModel updates when weekly data changes`() = runTest {
        // Given
        val mockWeeklyExpenses = listOf(
            createMockWeeklyExpense(1, 10.0),
            createMockWeeklyExpense(2, 15.0)
        )
        val mockGraphModels = mockWeeklyExpenses.map { createMockWeeklyGraphUiModel(it.id) }
        
        whenever(expenseRepository.getWeeklyExpenseList()).thenReturn(flowOf(mockWeeklyExpenses))
        whenever(expenseDetailUiModelMapper.map(any())).thenReturn(mockGraphModels.first(), *mockGraphModels.drop(1).toTypedArray())

        // When
        val observer = mock<Observer<List<WeeklyGraphUiModel>>>()
        viewModel.weeklyGraphUiModel.observeForever(observer)

        // Then
        verify(observer).onChanged(mockGraphModels)
    }

    @Test
    fun `repository exceptions are handled gracefully`() = runTest {
        // Given
        whenever(expenseRepository.getRecentExpenseList()).thenThrow(RuntimeException("Database error"))

        // When/Then - Should not crash
        val observer = mock<Observer<List<Any>>>()
        viewModel.recentExpenseList.observeForever(observer)
        
        // Verify observer is called with empty list or error state
        verify(observer).onChanged(any())
    }

    @Test
    fun `mapper exceptions are handled gracefully`() = runTest {
        // Given
        val mockExpenses = listOf(createMockExpense(1, "Coffee", 5.0))
        whenever(expenseRepository.getRecentExpenseList()).thenReturn(flowOf(mockExpenses))
        whenever(expenseUiModelMapper.map(any())).thenThrow(RuntimeException("Mapping error"))

        // When/Then - Should not crash
        val observer = mock<Observer<List<Any>>>()
        viewModel.recentExpenseList.observeForever(observer)
        
        // Should handle the error gracefully
        verify(observer).onChanged(any())
    }

    // Helper methods for creating mock objects
    private fun createMockExpense(id: Int, name: String, amount: Double) = mock<Any> {
        on { this.id } doReturn id
        on { this.name } doReturn name
        on { this.amount } doReturn Amount(amount)
    }

    private fun createMockUiModel(id: Int) = mock<Any> {
        on { this.id } doReturn id
    }

    private fun createMockWeeklyExpense(id: Int, amount: Double) = mock<Any> {
        on { this.id } doReturn id
        on { this.amount } doReturn Amount(amount)
    }

    private fun createMockWeeklyGraphUiModel(id: Int) = mock<WeeklyGraphUiModel> {
        on { this.id } doReturn id
    }
}