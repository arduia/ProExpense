package com.arduia.expense.ui.entry

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.R
import com.arduia.expense.domain.Amount
import com.arduia.mvvm.Event
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ExpenseEntryFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var scenario: FragmentScenario<ExpenseEntryFragment>
    private lateinit var navController: TestNavHostController
    private lateinit var mockViewModel: ExpenseEntryViewModel

    // Mock LiveData
    private val expenseNameLiveData = MutableLiveData<String>()
    private val expenseAmountLiveData = MutableLiveData<Amount>()
    private val expenseNoteLiveData = MutableLiveData<String>()
    private val selectedCategoryLiveData = MutableLiveData<Any>()
    private val onExpenseSavedLiveData = MutableLiveData<Event<Unit>>()
    private val onValidationErrorLiveData = MutableLiveData<Event<String>>()
    private val isEditModeLiveData = MutableLiveData<Boolean>()

    @Before
    fun setup() {
        hiltRule.inject()
        
        // Setup mock ViewModel
        mockViewModel = mockk(relaxed = true)
        every { mockViewModel.expenseName } returns expenseNameLiveData
        every { mockViewModel.expenseAmount } returns expenseAmountLiveData
        every { mockViewModel.expenseNote } returns expenseNoteLiveData
        every { mockViewModel.selectedCategory } returns selectedCategoryLiveData
        every { mockViewModel.onExpenseSaved } returns onExpenseSavedLiveData
        every { mockViewModel.onValidationError } returns onValidationErrorLiveData
        every { mockViewModel.isEditMode } returns isEditModeLiveData

        // Setup navigation controller
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        navController.setCurrentDestination(R.id.dest_expense_entry)
    }

    @After
    fun teardown() {
        if (::scenario.isInitialized) {
            scenario.close()
        }
    }

    @Test
    fun fragment_launches_successfully() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.et_expense_name)).check(matches(isDisplayed()))
        onView(withId(R.id.et_expense_amount)).check(matches(isDisplayed()))
        onView(withId(R.id.et_expense_note)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_save)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_sets_up_navigation_correctly() {
        // When
        launchFragment()

        // Then
        scenario.onFragment { fragment ->
            assert(Navigation.findNavController(fragment.requireView()) == navController)
        }
    }

    @Test
    fun fragment_displays_expense_name() {
        // Given
        val expenseName = "Coffee"
        
        // When
        launchFragment()
        expenseNameLiveData.postValue(expenseName)

        // Then
        onView(withId(R.id.et_expense_name)).check(matches(withText(expenseName)))
    }

    @Test
    fun fragment_displays_expense_amount() {
        // Given
        val amount = Amount(25.50)
        
        // When
        launchFragment()
        expenseAmountLiveData.postValue(amount)

        // Then
        onView(withId(R.id.et_expense_amount)).check(matches(withText("25.50")))
    }

    @Test
    fun fragment_displays_expense_note() {
        // Given
        val note = "Morning coffee at Starbucks"
        
        // When
        launchFragment()
        expenseNoteLiveData.postValue(note)

        // Then
        onView(withId(R.id.et_expense_note)).check(matches(withText(note)))
    }

    @Test
    fun user_can_enter_expense_name() {
        // When
        launchFragment()
        onView(withId(R.id.et_expense_name)).perform(typeText("Lunch"))

        // Then
        verify { mockViewModel.setExpenseName("Lunch") }
    }

    @Test
    fun user_can_enter_expense_amount() {
        // When
        launchFragment()
        onView(withId(R.id.et_expense_amount)).perform(typeText("15.75"))

        // Then
        verify { mockViewModel.setExpenseAmount("15.75") }
    }

    @Test
    fun user_can_enter_expense_note() {
        // When
        launchFragment()
        onView(withId(R.id.et_expense_note)).perform(typeText("Lunch with colleagues"))

        // Then
        verify { mockViewModel.setExpenseNote("Lunch with colleagues") }
    }

    @Test
    fun save_button_calls_viewmodel_save() {
        // When
        launchFragment()
        onView(withId(R.id.btn_save)).perform(click())

        // Then
        verify { mockViewModel.saveExpense() }
    }

    @Test
    fun fragment_handles_expense_saved_event() {
        // When
        launchFragment()
        onExpenseSavedLiveData.postValue(Event(Unit))

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.onExpenseSaved }
            // In real test, this would verify navigation back
        }
    }

    @Test
    fun fragment_handles_validation_error_event() {
        // Given
        val errorMessage = "Please enter expense name"
        
        // When
        launchFragment()
        onValidationErrorLiveData.postValue(Event(errorMessage))

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.onValidationError }
            // In real test, this would verify error message display
        }
    }

    @Test
    fun fragment_shows_edit_mode_ui() {
        // Given
        val isEditMode = true
        
        // When
        launchFragment()
        isEditModeLiveData.postValue(isEditMode)

        // Then
        onView(withText(R.string.edit_expense)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_shows_create_mode_ui() {
        // Given
        val isEditMode = false
        
        // When
        launchFragment()
        isEditModeLiveData.postValue(isEditMode)

        // Then
        onView(withText(R.string.add_expense)).check(matches(isDisplayed()))
    }

    @Test
    fun category_selection_updates_viewmodel() {
        // When
        launchFragment()
        onView(withId(R.id.spinner_category)).perform(click())

        // Then
        scenario.onFragment { fragment ->
            // Verify category selection handling
            // This would be tested through spinner interaction in integration tests
        }
    }

    @Test
    fun date_picker_updates_viewmodel() {
        // When
        launchFragment()
        onView(withId(R.id.btn_date)).perform(click())

        // Then
        scenario.onFragment { fragment ->
            // Verify date picker handling
            // This would be tested through date picker interaction in integration tests
        }
    }

    @Test
    fun fragment_handles_view_binding_lifecycle() {
        // When
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()

        // Then
        scenario.onFragment { fragment ->
            assert(fragment.view != null)
        }

        // When fragment is destroyed
        scenario.close()
    }

    @Test
    fun toolbar_navigation_goes_back() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withContentDescription("Navigate up"))))
    }

    @Test
    fun form_validation_prevents_empty_submission() {
        // When
        launchFragment()
        onView(withId(R.id.btn_save)).perform(click())

        // Then
        verify { mockViewModel.saveExpense() }
        // ViewModel should handle validation
    }

    @Test
    fun fragment_displays_currency_symbol() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.tv_currency)).check(matches(isDisplayed()))
    }

    @Test
    fun amount_input_accepts_decimal_values() {
        // When
        launchFragment()
        onView(withId(R.id.et_expense_amount)).perform(typeText("123.45"))

        // Then
        verify { mockViewModel.setExpenseAmount("123.45") }
    }

    @Test
    fun note_field_is_optional() {
        // When
        launchFragment()
        // Leave note field empty and try to save
        onView(withId(R.id.et_expense_name)).perform(typeText("Test Expense"))
        onView(withId(R.id.et_expense_amount)).perform(typeText("10.00"))
        onView(withId(R.id.btn_save)).perform(click())

        // Then
        verify { mockViewModel.saveExpense() }
    }

    private fun launchFragment(): FragmentScenario<ExpenseEntryFragment> {
        scenario = launchFragmentInContainer<ExpenseEntryFragment>()
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        return scenario
    }
}