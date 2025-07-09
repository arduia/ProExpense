package com.arduia.expense.ui.entry

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.domain.Amount
import com.arduia.expense.ui.entry.*
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
class ExpenseEntryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var expenseRepository: ExpenseRepository

    @Mock
    private lateinit var expenseUpdateDataUiModelMapper: ExpenseUpdateDataUiModelMapper

    private lateinit var viewModel: ExpenseEntryViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Setup default mocks
        whenever(expenseRepository.getCategoryList()).thenReturn(flowOf(emptyList()))
        whenever(expenseRepository.getExpenseByID(any())).thenReturn(flowOf(null))
        
        viewModel = ExpenseEntryViewModel(
            expenseRepository = expenseRepository,
            expenseUpdateDataUiModelMapper = expenseUpdateDataUiModelMapper
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialization sets default values correctly`() = runTest {
        // When
        val observer = mock<Observer<ExpenseEntryMode>>()
        viewModel.entryMode.observeForever(observer)

        // Then
        verify(observer).onChanged(ExpenseEntryMode.Create)
        verify(expenseRepository).getCategoryList()
    }

    @Test
    fun `setEntryMode updates mode correctly`() = runTest {
        // Given
        val observer = mock<Observer<ExpenseEntryMode>>()
        viewModel.entryMode.observeForever(observer)

        // When
        viewModel.setEntryMode(ExpenseEntryMode.Update(123))

        // Then
        verify(observer).onChanged(ExpenseEntryMode.Update(123))
    }

    @Test
    fun `setAmount updates amount correctly`() = runTest {
        // Given
        val observer = mock<Observer<String>>()
        viewModel.amount.observeForever(observer)

        // When
        viewModel.setAmount("100.50")

        // Then
        verify(observer).onChanged("100.50")
    }

    @Test
    fun `setNote updates note correctly`() = runTest {
        // Given
        val observer = mock<Observer<String>>()
        viewModel.note.observeForever(observer)

        // When
        viewModel.setNote("Coffee expense")

        // Then
        verify(observer).onChanged("Coffee expense")
    }

    @Test
    fun `setCategoryId updates category correctly`() = runTest {
        // Given
        val observer = mock<Observer<Int>>()
        viewModel.categoryId.observeForever(observer)

        // When
        viewModel.setCategoryId(5)

        // Then
        verify(observer).onChanged(5)
    }

    @Test
    fun `setDate updates date correctly`() = runTest {
        // Given
        val observer = mock<Observer<Long>>()
        viewModel.date.observeForever(observer)
        val testDate = System.currentTimeMillis()

        // When
        viewModel.setDate(testDate)

        // Then
        verify(observer).onChanged(testDate)
    }

    @Test
    fun `saveExpense with valid data in create mode saves successfully`() = runTest {
        // Given
        viewModel.setAmount("50.00")
        viewModel.setCategoryId(1)
        viewModel.setNote("Lunch")
        viewModel.setDate(System.currentTimeMillis())
        
        whenever(expenseRepository.insertNewExpense(any())).thenReturn(Unit)

        // When
        viewModel.saveExpense()

        // Then
        verify(expenseRepository).insertNewExpense(any())
    }

    @Test
    fun `saveExpense with valid data in update mode updates successfully`() = runTest {
        // Given
        val expenseId = 123
        viewModel.setEntryMode(ExpenseEntryMode.Update(expenseId))
        viewModel.setAmount("75.00")
        viewModel.setCategoryId(2)
        viewModel.setNote("Dinner")
        viewModel.setDate(System.currentTimeMillis())
        
        whenever(expenseRepository.updateExpense(any())).thenReturn(Unit)

        // When
        viewModel.saveExpense()

        // Then
        verify(expenseRepository).updateExpense(any())
    }

    @Test
    fun `saveExpense with invalid amount shows error`() = runTest {
        // Given
        viewModel.setAmount("")
        viewModel.setCategoryId(1)
        
        val observer = mock<Observer<String>>()
        viewModel.validationError.observeForever(observer)

        // When
        viewModel.saveExpense()

        // Then
        verify(observer).onChanged("Amount is required")
        verify(expenseRepository, never()).insertNewExpense(any())
    }

    @Test
    fun `saveExpense with invalid category shows error`() = runTest {
        // Given
        viewModel.setAmount("50.00")
        viewModel.setCategoryId(-1)
        
        val observer = mock<Observer<String>>()
        viewModel.validationError.observeForever(observer)

        // When
        viewModel.saveExpense()

        // Then
        verify(observer).onChanged("Category is required")
        verify(expenseRepository, never()).insertNewExpense(any())
    }

    @Test
    fun `saveExpense with zero amount shows error`() = runTest {
        // Given
        viewModel.setAmount("0.00")
        viewModel.setCategoryId(1)
        
        val observer = mock<Observer<String>>()
        viewModel.validationError.observeForever(observer)

        // When
        viewModel.saveExpense()

        // Then
        verify(observer).onChanged("Amount must be greater than zero")
        verify(expenseRepository, never()).insertNewExpense(any())
    }

    @Test
    fun `saveExpense with negative amount shows error`() = runTest {
        // Given
        viewModel.setAmount("-10.00")
        viewModel.setCategoryId(1)
        
        val observer = mock<Observer<String>>()
        viewModel.validationError.observeForever(observer)

        // When
        viewModel.saveExpense()

        // Then
        verify(observer).onChanged("Amount must be greater than zero")
        verify(expenseRepository, never()).insertNewExpense(any())
    }

    @Test
    fun `loadExpenseForUpdate loads data correctly`() = runTest {
        // Given
        val expenseId = 123
        val mockExpense = createMockExpense(expenseId, "Coffee", 5.0, 1, "Morning coffee")
        val mockUiModel = createMockExpenseUpdateUiModel("5.0", 1, "Morning coffee")
        
        whenever(expenseRepository.getExpenseByID(expenseId)).thenReturn(flowOf(mockExpense))
        whenever(expenseUpdateDataUiModelMapper.map(mockExpense)).thenReturn(mockUiModel)

        // When
        viewModel.loadExpenseForUpdate(expenseId)

        // Then
        verify(expenseRepository).getExpenseByID(expenseId)
        verify(expenseUpdateDataUiModelMapper).map(mockExpense)
    }

    @Test
    fun `deleteExpense removes expense successfully`() = runTest {
        // Given
        val expenseId = 123
        viewModel.setEntryMode(ExpenseEntryMode.Update(expenseId))
        whenever(expenseRepository.deleteExpenseByID(expenseId)).thenReturn(Unit)

        // When
        viewModel.deleteExpense()

        // Then
        verify(expenseRepository).deleteExpenseByID(expenseId)
    }

    @Test
    fun `deleteExpense in create mode does nothing`() = runTest {
        // Given
        viewModel.setEntryMode(ExpenseEntryMode.Create)

        // When
        viewModel.deleteExpense()

        // Then
        verify(expenseRepository, never()).deleteExpenseByID(any())
    }

    @Test
    fun `categoryList updates when repository data changes`() = runTest {
        // Given
        val mockCategories = listOf(
            createMockCategory(1, "Food"),
            createMockCategory(2, "Transport")
        )
        whenever(expenseRepository.getCategoryList()).thenReturn(flowOf(mockCategories))

        // When
        val observer = mock<Observer<List<Any>>>()
        viewModel.categoryList.observeForever(observer)

        // Then
        verify(observer).onChanged(mockCategories)
    }

    @Test
    fun `isLockMode updates lock state correctly`() = runTest {
        // Given
        val observer = mock<Observer<LockMode>>()
        viewModel.isLockMode.observeForever(observer)

        // When
        viewModel.setLockMode(LockMode.Locked)

        // Then
        verify(observer).onChanged(LockMode.Locked)
    }

    @Test
    fun `repository save exceptions are handled gracefully`() = runTest {
        // Given
        viewModel.setAmount("50.00")
        viewModel.setCategoryId(1)
        whenever(expenseRepository.insertNewExpense(any())).thenThrow(RuntimeException("Database error"))
        
        val errorObserver = mock<Observer<String>>()
        viewModel.errorMessage.observeForever(errorObserver)

        // When
        viewModel.saveExpense()

        // Then
        verify(errorObserver).onChanged("Failed to save expense")
    }

    @Test
    fun `large amount values are handled correctly`() = runTest {
        // Given
        val largeAmount = "999999.99"
        viewModel.setAmount(largeAmount)
        viewModel.setCategoryId(1)
        whenever(expenseRepository.insertNewExpense(any())).thenReturn(Unit)

        // When
        viewModel.saveExpense()

        // Then
        verify(expenseRepository).insertNewExpense(any())
    }

    // Helper methods for creating mock objects
    private fun createMockExpense(id: Int, name: String, amount: Double, categoryId: Int, note: String) = mock<Any> {
        on { this.id } doReturn id
        on { this.name } doReturn name
        on { this.amount } doReturn Amount(amount)
        on { this.categoryId } doReturn categoryId
        on { this.note } doReturn note
    }

    private fun createMockCategory(id: Int, name: String) = mock<Any> {
        on { this.id } doReturn id
        on { this.name } doReturn name
    }

    private fun createMockExpenseUpdateUiModel(amount: String, categoryId: Int, note: String) = 
        ExpenseUpdateDataUiModel(amount, categoryId, note)
}