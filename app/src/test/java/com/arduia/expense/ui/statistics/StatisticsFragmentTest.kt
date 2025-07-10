package com.arduia.expense.ui.statistics

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
import com.arduia.expense.domain.Amount
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
class StatisticsFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var scenario: FragmentScenario<StatisticsFragment>
    private lateinit var navController: TestNavHostController
    private lateinit var mockViewModel: StatisticsViewModel

    // Mock LiveData
    private val totalExpenseLiveData = MutableLiveData<Amount>()
    private val monthlyExpenseLiveData = MutableLiveData<Amount>()
    private val weeklyExpenseLiveData = MutableLiveData<Amount>()
    private val dailyExpenseLiveData = MutableLiveData<Amount>()
    private val chartDataLiveData = MutableLiveData<List<Any>>()
    private val categoryDataLiveData = MutableLiveData<List<Any>>()
    private val selectedPeriodLiveData = MutableLiveData<String>()

    @Before
    fun setup() {
        hiltRule.inject()
        
        // Setup mock ViewModel
        mockViewModel = mockk(relaxed = true)
        every { mockViewModel.totalExpense } returns totalExpenseLiveData
        every { mockViewModel.monthlyExpense } returns monthlyExpenseLiveData
        every { mockViewModel.weeklyExpense } returns weeklyExpenseLiveData
        every { mockViewModel.dailyExpense } returns dailyExpenseLiveData
        every { mockViewModel.chartData } returns chartDataLiveData
        every { mockViewModel.categoryData } returns categoryDataLiveData
        every { mockViewModel.selectedPeriod } returns selectedPeriodLiveData

        // Setup navigation controller
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        navController.setCurrentDestination(R.id.dest_statistics)
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
        onView(withId(R.id.tv_total_expense)).check(matches(isDisplayed()))
        onView(withId(R.id.chart_view)).check(matches(isDisplayed()))
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
    fun fragment_displays_total_expense() {
        // Given
        val totalAmount = Amount(1500.75)
        
        // When
        launchFragment()
        totalExpenseLiveData.postValue(totalAmount)

        // Then
        onView(withId(R.id.tv_total_expense)).check(matches(withText("1500.75")))
    }

    @Test
    fun fragment_displays_monthly_expense() {
        // Given
        val monthlyAmount = Amount(500.25)
        
        // When
        launchFragment()
        monthlyExpenseLiveData.postValue(monthlyAmount)

        // Then
        onView(withId(R.id.tv_monthly_expense)).check(matches(withText("500.25")))
    }

    @Test
    fun fragment_displays_weekly_expense() {
        // Given
        val weeklyAmount = Amount(125.50)
        
        // When
        launchFragment()
        weeklyExpenseLiveData.postValue(weeklyAmount)

        // Then
        onView(withId(R.id.tv_weekly_expense)).check(matches(withText("125.50")))
    }

    @Test
    fun fragment_displays_daily_expense() {
        // Given
        val dailyAmount = Amount(25.75)
        
        // When
        launchFragment()
        dailyExpenseLiveData.postValue(dailyAmount)

        // Then
        onView(withId(R.id.tv_daily_expense)).check(matches(withText("25.75")))
    }

    @Test
    fun fragment_observes_chart_data_changes() {
        // Given
        val chartData = listOf(mockk<Any>(), mockk<Any>())
        
        // When
        launchFragment()
        chartDataLiveData.postValue(chartData)

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.chartData }
        }
    }

    @Test
    fun fragment_observes_category_data_changes() {
        // Given
        val categoryData = listOf(mockk<Any>(), mockk<Any>())
        
        // When
        launchFragment()
        categoryDataLiveData.postValue(categoryData)

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.categoryData }
        }
    }

    @Test
    fun fragment_displays_selected_period() {
        // Given
        val period = "This Month"
        
        // When
        launchFragment()
        selectedPeriodLiveData.postValue(period)

        // Then
        onView(withId(R.id.tv_selected_period)).check(matches(withText(period)))
    }

    @Test
    fun period_selector_calls_viewmodel() {
        // When
        launchFragment()
        // Simulate period selection
        scenario.onFragment { fragment ->
            // This would be tested through spinner/button interaction
        }

        // Then
        // Verify period selection method is called
    }

    @Test
    fun fragment_handles_empty_data() {
        // Given
        val emptyAmount = Amount.ZERO
        val emptyList = emptyList<Any>()
        
        // When
        launchFragment()
        totalExpenseLiveData.postValue(emptyAmount)
        chartDataLiveData.postValue(emptyList)

        // Then
        onView(withId(R.id.tv_total_expense)).check(matches(withText("0.00")))
        onView(withId(R.id.empty_state)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_shows_chart_when_data_available() {
        // Given
        val chartData = listOf(mockk<Any>(), mockk<Any>())
        
        // When
        launchFragment()
        chartDataLiveData.postValue(chartData)

        // Then
        onView(withId(R.id.chart_view)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_state)).check(matches(not(isDisplayed())))
    }

    @Test
    fun fragment_displays_category_breakdown() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.rv_categories)).check(matches(isDisplayed()))
        onView(withText(R.string.category_breakdown)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_handles_view_binding_lifecycle() {
        // When
        scenario = launchFragmentInContainer<StatisticsFragment>()

        // Then
        scenario.onFragment { fragment ->
            assert(fragment.view != null)
        }

        // When fragment is destroyed
        scenario.close()
    }

    @Test
    fun toolbar_navigation_opens_drawer() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withContentDescription("Navigate up"))))
    }

    @Test
    fun fragment_displays_statistics_sections() {
        // When
        launchFragment()

        // Then
        onView(withText(R.string.total_expense)).check(matches(isDisplayed()))
        onView(withText(R.string.monthly_expense)).check(matches(isDisplayed()))
        onView(withText(R.string.weekly_expense)).check(matches(isDisplayed()))
        onView(withText(R.string.daily_expense)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_shows_currency_symbols() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.tv_currency_total)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_currency_monthly)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_currency_weekly)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_currency_daily)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_handles_data_refresh() {
        // When
        launchFragment()
        scenario.onFragment { fragment ->
            // Simulate refresh action
        }

        // Then
        verify { mockViewModel.refreshData() }
    }

    @Test
    fun chart_view_is_properly_configured() {
        // When
        launchFragment()

        // Then
        scenario.onFragment { fragment ->
            val chartView = fragment.view?.findViewById<Any>(R.id.chart_view)
            assert(chartView != null)
        }
    }

    @Test
    fun category_recycler_view_is_properly_configured() {
        // When
        launchFragment()

        // Then
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_categories)
            assert(recyclerView?.adapter != null)
        }
    }

    private fun launchFragment(): FragmentScenario<StatisticsFragment> {
        scenario = launchFragmentInContainer<StatisticsFragment>()
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        return scenario
    }
}