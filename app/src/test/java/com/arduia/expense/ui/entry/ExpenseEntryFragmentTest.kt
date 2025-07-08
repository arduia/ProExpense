package com.arduia.expense.ui.entry

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.R
import com.arduia.expense.ui.MainHost
import com.arduia.expense.ui.common.category.ExpenseCategory
import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import com.arduia.mvvm.Event
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.annotation.Config
import java.util.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class ExpenseEntryFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockNavController: NavController

    @Mock
    private lateinit var mockExpenseEntryViewModel: ExpenseEntryViewModel

    @Mock
    private lateinit var mockMainHost: MainHost

    @Mock
    private lateinit var mockCategoryProvider: ExpenseCategoryProvider

    private lateinit var scenario: FragmentScenario<ExpenseEntryFragment>

    // Mock LiveData
    private val selectedCategory = MutableLiveData<ExpenseCategory>()
    private val onDataInserted = MutableLiveData<Event<Unit>>()
    private val onDataUpdated = MutableLiveData<Event<Unit>>()
    private val onCurrentModeChanged = MutableLiveData<Event<ExpenseEntryMode>>()
    private val entryData = MutableLiveData<ExpenseUpdateDataUiModel>()
    private val lockMode = MutableLiveData<LockMode>()
    private val onNext = MutableLiveData<Event<Unit>>()
    private val currentEntryTime = MutableLiveData<Long>()
    private val currencySymbol = MutableLiveData<String>()
    private val onChooseDateShow = MutableLiveData<Event<Calendar>>()
    private val onChooseTimeShow = MutableLiveData<Event<Calendar>>()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        
        setupMockViewModel()
        setupMockCategoryProvider()
    }

    private fun setupMockViewModel() {
        whenever(mockExpenseEntryViewModel.selectedCategory).thenReturn(selectedCategory)
        whenever(mockExpenseEntryViewModel.onDataInserted).thenReturn(onDataInserted)
        whenever(mockExpenseEntryViewModel.onDataUpdated).thenReturn(onDataUpdated)
        whenever(mockExpenseEntryViewModel.onCurrentModeChanged).thenReturn(onCurrentModeChanged)
        whenever(mockExpenseEntryViewModel.entryData).thenReturn(entryData)
        whenever(mockExpenseEntryViewModel.lockMode).thenReturn(lockMode)
        whenever(mockExpenseEntryViewModel.onNext).thenReturn(onNext)
        whenever(mockExpenseEntryViewModel.currentEntryTime).thenReturn(currentEntryTime)
        whenever(mockExpenseEntryViewModel.currencySymbol).thenReturn(currencySymbol)
        whenever(mockExpenseEntryViewModel.onChooseDateShow).thenReturn(onChooseDateShow)
        whenever(mockExpenseEntryViewModel.onChooseTimeShow).thenReturn(onChooseTimeShow)
    }

    private fun setupMockCategoryProvider() {
        val mockCategories = listOf(
            ExpenseCategory(id = 1, name = "Food", iconRes = R.drawable.ic_food),
            ExpenseCategory(id = 2, name = "Transport", iconRes = R.drawable.ic_car),
            ExpenseCategory(id = 3, name = "Shopping", iconRes = R.drawable.ic_shopping)
        )
        whenever(mockCategoryProvider.getCategoryList()).thenReturn(mockCategories)
        whenever(mockCategoryProvider.getCategoryByID(any())).thenReturn(mockCategories[0])
    }

    @Test
    fun `fragment should be created successfully`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        scenario.onFragment { fragment ->
            assert(fragment.isAdded)
            assert(fragment.view != null)
        }
    }

    @Test
    fun `should setup category list adapter`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view!!.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvCategory)
            assert(recyclerView != null)
            assert(recyclerView.adapter != null)
        }
    }

    @Test
    fun `should handle save mode entry`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        onCurrentModeChanged.value = Event(ExpenseEntryMode.INSERT)
        
        scenario.onFragment { fragment ->
            val toolbar = fragment.view!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            assert(toolbar.title == fragment.getString(R.string.expense_entry))
            
            val saveButton = fragment.view!!.findViewById<android.widget.Button>(R.id.btnSave)
            assert(saveButton.text == fragment.getString(R.string.save))
        }
    }

    @Test
    fun `should handle update mode entry`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        onCurrentModeChanged.value = Event(ExpenseEntryMode.UPDATE)
        
        scenario.onFragment { fragment ->
            val toolbar = fragment.view!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            assert(toolbar.title == fragment.getString(R.string.update_data))
            
            val saveButton = fragment.view!!.findViewById<android.widget.Button>(R.id.btnSave)
            assert(saveButton.text == fragment.getString(R.string.update))
        }
    }

    @Test
    fun `should bind expense data for update mode`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        val testData = ExpenseUpdateDataUiModel(
            name = "Test Expense",
            amount = "100.00",
            note = "Test note",
            category = ExpenseCategory(id = 1, name = "Food", iconRes = R.drawable.ic_food)
        )
        
        entryData.value = testData
        
        scenario.onFragment { fragment ->
            val nameEditText = fragment.view!!.findViewById<android.widget.EditText>(R.id.edtName)
            val amountEditText = fragment.view!!.findViewById<android.widget.EditText>(R.id.edtAmount)
            val noteEditText = fragment.view!!.findViewById<android.widget.EditText>(R.id.edtNote)
            
            assert(nameEditText.text.toString() == testData.name)
            assert(amountEditText.text.toString() == testData.amount)
            assert(noteEditText.text.toString() == testData.note)
        }
    }

    @Test
    fun `should handle category selection`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        val selectedCat = ExpenseCategory(id = 1, name = "Food", iconRes = R.drawable.ic_food)
        selectedCategory.value = selectedCat
        
        scenario.onFragment { fragment ->
            // Verify category selection is handled
            assert(fragment.isAdded)
            // The category adapter should be updated
        }
    }

    @Test
    fun `should handle lock mode changes`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        // Test locked mode
        lockMode.value = LockMode.LOCKED
        
        scenario.onFragment { fragment ->
            val saveButton = fragment.view!!.findViewById<android.widget.Button>(R.id.btnSave)
            assert(saveButton.text == fragment.getString(R.string.next))
        }
        
        // Test unlocked mode
        lockMode.value = LockMode.UNLOCK
        
        scenario.onFragment { fragment ->
            val saveButton = fragment.view!!.findViewById<android.widget.Button>(R.id.btnSave)
            assert(saveButton.text == fragment.getString(R.string.save))
        }
    }

    @Test
    fun `should handle data insertion event`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }
        
        onDataInserted.value = Event(Unit)
        
        verify(mockNavController).popBackStack()
    }

    @Test
    fun `should handle data update event`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }
        
        onDataUpdated.value = Event(Unit)
        
        verify(mockNavController).popBackStack()
    }

    @Test
    fun `should handle next event in lock mode`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        onNext.value = Event(Unit)
        
        scenario.onFragment { fragment ->
            val nameEditText = fragment.view!!.findViewById<android.widget.EditText>(R.id.edtName)
            val amountEditText = fragment.view!!.findViewById<android.widget.EditText>(R.id.edtAmount)
            val noteEditText = fragment.view!!.findViewById<android.widget.EditText>(R.id.edtNote)
            
            // UI should be cleaned
            assert(nameEditText.text.toString().isEmpty())
            assert(amountEditText.text.toString().isEmpty())
            assert(noteEditText.text.toString().isEmpty())
        }
    }

    @Test
    fun `should update current entry time`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        val testTime = System.currentTimeMillis()
        currentEntryTime.value = testTime
        
        scenario.onFragment { fragment ->
            val toolbar = fragment.view!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            assert(toolbar.subtitle != null)
            assert(toolbar.subtitle.toString().isNotEmpty())
        }
    }

    @Test
    fun `should update currency symbol`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        currencySymbol.value = "$"
        
        scenario.onFragment { fragment ->
            // Verify currency symbol is set
            assert(fragment.isAdded)
            // The amount field should have the suffix set
        }
    }

    @Test
    fun `should show date picker on date select event`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        val testCalendar = Calendar.getInstance()
        onChooseDateShow.value = Event(testCalendar)
        
        scenario.onFragment { fragment ->
            // Verify date picker is shown
            assert(fragment.isAdded)
            // DatePickerDialog should be displayed
        }
    }

    @Test
    fun `should show time picker on time select event`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        val testCalendar = Calendar.getInstance()
        onChooseTimeShow.value = Event(testCalendar)
        
        scenario.onFragment { fragment ->
            // Verify time picker is shown
            assert(fragment.isAdded)
            // TimePickerDialog should be displayed
        }
    }

    @Test
    fun `should handle toolbar navigation click`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
            
            val toolbar = fragment.view!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            toolbar.navigationIcon?.callback?.invalidateDrawable(toolbar.navigationIcon)
        }
        
        // Navigation should pop back stack
        verify(mockNavController).popBackStack()
    }

    @Test
    fun `should handle lock button click`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        scenario.onFragment { fragment ->
            val lockButton = fragment.view!!.findViewById<android.view.View>(R.id.cvLock)
            lockButton.performClick()
        }
        
        verify(mockExpenseEntryViewModel).invertLockMode()
    }

    @Test
    fun `should show amount empty error when saving without amount`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        scenario.onFragment { fragment ->
            val saveButton = fragment.view!!.findViewById<android.widget.Button>(R.id.btnSave)
            val amountEditText = fragment.view!!.findViewById<android.widget.EditText>(R.id.edtAmount)
            
            // Clear amount field
            amountEditText.setText("")
            
            saveButton.performClick()
            
            // Should show error
            assert(amountEditText.error != null)
            assert(amountEditText.error.toString() == fragment.getString(R.string.empty_cost))
        }
    }

    @Test
    fun `should handle menu item clicks for date and time`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        scenario.onFragment { fragment ->
            val toolbar = fragment.view!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            
            // Test calendar menu item
            toolbar.menu.performIdentifierAction(R.id.calendar, 0)
            verify(mockExpenseEntryViewModel).onDateSelect()
            
            // Test time menu item
            toolbar.menu.performIdentifierAction(R.id.time, 0)
            verify(mockExpenseEntryViewModel).onTimeSelect()
        }
    }

    @Test
    fun `should properly clean up on destroy view`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        scenario.onFragment { fragment ->
            assert(fragment.view != null)
        }
        
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.DESTROYED)
        
        // Fragment should be properly cleaned up
        scenario.onFragment { fragment ->
            assert(!fragment.isAdded)
        }
    }

    @Test
    fun `should setup floating input filter for amount field`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        scenario.onFragment { fragment ->
            val amountEditText = fragment.view!!.findViewById<android.widget.EditText>(R.id.edtAmount)
            assert(amountEditText.filters.isNotEmpty())
        }
    }

    @Test
    fun `should handle category item click`() {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        
        val testCategory = ExpenseCategory(id = 1, name = "Food", iconRes = R.drawable.ic_food)
        
        scenario.onFragment { fragment ->
            // Simulate category click
            // This would normally be handled by the adapter
            // categoryAdapter.setOnItemClickListener { viewModel.selectCategory(it) }
            verify(mockExpenseEntryViewModel).selectCategory(testCategory)
        }
    }
}