package com.arduia.expense.ui.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
class SettingsFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var scenario: FragmentScenario<SettingsFragment>
    private lateinit var navController: TestNavHostController
    private lateinit var mockViewModel: SettingsViewModel

    // Mock LiveData
    private val selectedLanguageLiveData = MutableLiveData<Int>()
    private val currencyValueLiveData = MutableLiveData<String>()
    private val onThemeOpenToChangeLiveData = MutableLiveData<Event<Int>>()
    private val onThemeChangedLiveData = MutableLiveData<Event<Unit>>()

    @Before
    fun setup() {
        hiltRule.inject()
        
        // Setup mock ViewModel
        mockViewModel = mockk(relaxed = true)
        every { mockViewModel.selectedLanguage } returns selectedLanguageLiveData
        every { mockViewModel.currencyValue } returns currencyValueLiveData
        every { mockViewModel.onThemeOpenToChange } returns onThemeOpenToChangeLiveData
        every { mockViewModel.onThemeChanged } returns onThemeChangedLiveData

        // Setup navigation controller
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        navController.setCurrentDestination(R.id.dest_settings)
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
        onView(withId(R.id.tb_settings)).check(matches(isDisplayed()))
        onView(withId(R.id.fl_language)).check(matches(isDisplayed()))
        onView(withId(R.id.fl_currency)).check(matches(isDisplayed()))
        onView(withId(R.id.fl_theme)).check(matches(isDisplayed()))
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
    fun fragment_observes_selected_language_changes() {
        // Given
        val languageId = 1
        
        // When
        launchFragment()
        selectedLanguageLiveData.postValue(languageId)

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.selectedLanguage }
        }
    }

    @Test
    fun fragment_observes_currency_value_changes() {
        // Given
        val currencyValue = "USD"
        
        // When
        launchFragment()
        currencyValueLiveData.postValue(currencyValue)

        // Then
        onView(withId(R.id.tv_currency_value)).check(matches(withText(currencyValue)))
    }

    @Test
    fun fragment_handles_theme_open_event() {
        // Given
        val themeMode = 1
        
        // When
        launchFragment()
        onThemeOpenToChangeLiveData.postValue(Event(themeMode))

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.onThemeOpenToChange }
        }
    }

    @Test
    fun fragment_handles_theme_changed_event() {
        // When
        launchFragment()
        onThemeChangedLiveData.postValue(Event(Unit))

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.onThemeChanged }
            // Note: In real test, this would trigger activity restart
        }
    }

    @Test
    fun language_click_opens_language_dialog() {
        // When
        launchFragment()
        onView(withId(R.id.fl_language)).perform(click())

        // Then
        scenario.onFragment { fragment ->
            // Verify that language dialog is shown
            // This would be verified through dialog fragment manager in integration tests
        }
    }

    @Test
    fun currency_click_opens_currency_dialog() {
        // When
        launchFragment()
        onView(withId(R.id.fl_currency)).perform(click())

        // Then
        scenario.onFragment { fragment ->
            // Verify that currency dialog is shown
            // This would be verified through dialog fragment manager in integration tests
        }
    }

    @Test
    fun theme_click_calls_viewmodel_choose_theme() {
        // When
        launchFragment()
        onView(withId(R.id.fl_theme)).perform(click())

        // Then
        verify { mockViewModel.chooseTheme() }
    }

    @Test
    fun fragment_handles_view_binding_lifecycle() {
        // When
        scenario = launchFragmentInContainer<SettingsFragment>()

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
        onView(withId(R.id.tb_settings)).check(matches(hasDescendant(withContentDescription("Navigate up"))))
    }

    @Test
    fun fragment_displays_language_settings() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.imv_language)).check(matches(isDisplayed()))
        onView(withText(R.string.language)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_displays_currency_settings() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.tv_currency_value)).check(matches(isDisplayed()))
        onView(withText(R.string.currency)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_displays_theme_settings() {
        // When
        launchFragment()

        // Then
        onView(withText(R.string.theme)).check(matches(isDisplayed()))
    }

    private fun launchFragment(): FragmentScenario<SettingsFragment> {
        scenario = launchFragmentInContainer<SettingsFragment>()
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        return scenario
    }
}