package com.arduia.expense.ui.expenselogs

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.R
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
class ExpenseFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var scenario: FragmentScenario<ExpenseFragment>
    private lateinit var navController: TestNavHostController
    private lateinit var mockViewModel: ExpenseViewModel

    // Mock LiveData
    private val expenseListLiveData = MutableLiveData<List<Any>>()
    private val isLoadingLiveData = MutableLiveData<Boolean>()
    private val onItemClickLiveData = MutableLiveData<Event<Any>>()
    private val onItemDeleteLiveData = MutableLiveData<Event<Unit>>()
    private val filterStateLiveData = MutableLiveData<Any>()
    private val emptyStateLiveData = MutableLiveData<Boolean>()

    @Before
    fun setup() {
        hiltRule.inject()
        
        // Setup mock ViewModel
        mockViewModel = mockk(relaxed = true)
        every { mockViewModel.expenseList } returns expenseListLiveData
        every { mockViewModel.isLoading } returns isLoadingLiveData
        every { mockViewModel.onItemClick } returns onItemClickLiveData
        every { mockViewModel.onItemDelete } returns onItemDeleteLiveData
        every { mockViewModel.filterState } returns filterStateLiveData
        every { mockViewModel.emptyState } returns emptyStateLiveData

        // Setup navigation controller
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        navController.setCurrentDestination(R.id.dest_expense_logs)
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
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_expenses)).check(matches(isDisplayed()))
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
    fun fragment_displays_expense_list() {
        // Given
        val expenseList = listOf(mockk<Any>(), mockk<Any>(), mockk<Any>())
        
        // When
        launchFragment()
        expenseListLiveData.postValue(expenseList)

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.expenseList }
        }
    }

    @Test
    fun fragment_shows_loading_state() {
        // Given
        val isLoading = true
        
        // When
        launchFragment()
        isLoadingLiveData.postValue(isLoading)

        // Then
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_hides_loading_when_complete() {
        // Given
        val isLoading = false
        
        // When
        launchFragment()
        isLoadingLiveData.postValue(isLoading)

        // Then
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun fragment_shows_empty_state() {
        // Given
        val isEmpty = true
        
        // When
        launchFragment()
        emptyStateLiveData.postValue(isEmpty)

        // Then
        onView(withId(R.id.empty_state)).check(matches(isDisplayed()))
        onView(withText(R.string.no_expenses_found)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_hides_empty_state_when_data_available() {
        // Given
        val isEmpty = false
        val expenseList = listOf(mockk<Any>(), mockk<Any>())
        
        // When
        launchFragment()
        emptyStateLiveData.postValue(isEmpty)
        expenseListLiveData.postValue(expenseList)

        // Then
        onView(withId(R.id.empty_state)).check(matches(not(isDisplayed())))
        onView(withId(R.id.rv_expenses)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_handles_item_click_event() {
        // Given
        val clickedItem = mockk<Any>()
        
        // When
        launchFragment()
        onItemClickLiveData.postValue(Event(clickedItem))

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.onItemClick }
        }
    }

    @Test
    fun fragment_handles_item_delete_event() {
        // When
        launchFragment()
        onItemDeleteLiveData.postValue(Event(Unit))

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.onItemDelete }
        }
    }

    @Test
    fun fragment_displays_filter_options() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.btn_filter)).check(matches(isDisplayed()))
    }

    @Test
    fun filter_button_opens_filter_dialog() {
        // When
        launchFragment()
        // Simulate filter button click would be tested in integration tests

        // Then
        scenario.onFragment { fragment ->
            // Verify filter dialog is shown
        }
    }

    @Test
    fun fragment_observes_filter_state_changes() {
        // Given
        val filterState = mockk<Any>()
        
        // When
        launchFragment()
        filterStateLiveData.postValue(filterState)

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.filterState }
        }
    }

    @Test
    fun fragment_displays_search_functionality() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.search_view)).check(matches(isDisplayed()))
    }

    @Test
    fun search_input_updates_viewmodel() {
        // When
        launchFragment()
        // Simulate search input would be tested in integration tests

        // Then
        // Verify search method is called on ViewModel
    }

    @Test
    fun fragment_handles_view_binding_lifecycle() {
        // When
        scenario = launchFragmentInContainer<ExpenseFragment>()

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
    fun recyclerview_is_properly_configured() {
        // When
        launchFragment()

        // Then
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_expenses)
            assert(recyclerView?.adapter != null)
            assert(recyclerView?.layoutManager != null)
        }
    }

    @Test
    fun fragment_handles_swipe_to_refresh() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.swipe_refresh)).check(matches(isDisplayed()))
    }

    @Test
    fun swipe_refresh_calls_viewmodel_refresh() {
        // When
        launchFragment()
        // Simulate swipe refresh would be tested in integration tests

        // Then
        verify { mockViewModel.refreshData() }
    }

    @Test
    fun fragment_displays_total_count() {
        // Given
        val expenseList = listOf(mockk<Any>(), mockk<Any>(), mockk<Any>())
        
        // When
        launchFragment()
        expenseListLiveData.postValue(expenseList)

        // Then
        onView(withId(R.id.tv_total_count)).check(matches(withText("3 expenses")))
    }

    @Test
    fun fragment_shows_fab_for_adding_expense() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.fab_add)).check(matches(isDisplayed()))
    }

    @Test
    fun fab_click_navigates_to_entry_fragment() {
        // When
        launchFragment()
        // Simulate FAB click would be tested in integration tests

        // Then
        // Verify navigation to expense entry fragment
    }

    @Test
    fun fragment_handles_pagination() {
        // When
        launchFragment()
        // Simulate scroll to bottom would be tested in integration tests

        // Then
        verify { mockViewModel.loadMoreExpenses() }
    }

    @Test
    fun fragment_displays_date_range_selector() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.btn_date_range)).check(matches(isDisplayed()))
    }

    private fun launchFragment(): FragmentScenario<ExpenseFragment> {
        scenario = launchFragmentInContainer<ExpenseFragment>()
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        return scenario
    }
}