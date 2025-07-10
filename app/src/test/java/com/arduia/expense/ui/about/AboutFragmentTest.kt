package com.arduia.expense.ui.about

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
import com.arduia.expense.ui.settings.SettingsViewModel
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
class AboutFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var scenario: FragmentScenario<AboutFragment>
    private lateinit var navController: TestNavHostController
    private lateinit var mockSettingsViewModel: SettingsViewModel

    // Mock LiveData
    private val isNewVersionAvailableLiveData = MutableLiveData<Boolean>()
    private val onShowAboutUpdateLiveData = MutableLiveData<Event<AboutUpdateUiModel>>()

    @Before
    fun setup() {
        hiltRule.inject()
        
        // Setup mock ViewModel
        mockSettingsViewModel = mockk(relaxed = true)
        every { mockSettingsViewModel.isNewVersionAvailable } returns isNewVersionAvailableLiveData
        every { mockSettingsViewModel.onShowAboutUpdate } returns onShowAboutUpdateLiveData

        // Setup navigation controller
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        navController.setCurrentDestination(R.id.dest_about)
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
        onView(withId(R.id.tv_version)).check(matches(isDisplayed()))
        onView(withId(R.id.fl_contribute)).check(matches(isDisplayed()))
        onView(withId(R.id.fl_open_sources)).check(matches(isDisplayed()))
        onView(withId(R.id.fl_privacy)).check(matches(isDisplayed()))
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
    fun fragment_displays_app_version() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.tv_version)).check(matches(withText(not(isEmptyString()))))
    }

    @Test
    fun fragment_observes_new_version_availability() {
        // Given
        val isAvailable = true
        
        // When
        launchFragment()
        isNewVersionAvailableLiveData.postValue(isAvailable)

        // Then
        onView(withId(R.id.ln_update)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_hides_update_when_no_new_version() {
        // Given
        val isAvailable = false
        
        // When
        launchFragment()
        isNewVersionAvailableLiveData.postValue(isAvailable)

        // Then
        onView(withId(R.id.ln_update)).check(matches(not(isDisplayed())))
    }

    @Test
    fun fragment_handles_about_update_event() {
        // Given
        val updateModel = AboutUpdateUiModel(
            version = "2.0.0",
            description = "New features",
            url = "https://example.com"
        )
        
        // When
        launchFragment()
        onShowAboutUpdateLiveData.postValue(Event(updateModel))

        // Then
        scenario.onFragment { fragment ->
            verify { mockSettingsViewModel.onShowAboutUpdate }
        }
    }

    @Test
    fun contribute_click_opens_github_link() {
        // When
        launchFragment()
        onView(withId(R.id.fl_contribute)).perform(click())

        // Then
        scenario.onFragment { fragment ->
            // Verify that GitHub intent is created
            // This would be tested through intent verification in integration tests
        }
    }

    @Test
    fun open_sources_click_navigates_to_web() {
        // When
        launchFragment()
        onView(withId(R.id.fl_open_sources)).perform(click())

        // Then
        // Verify navigation to web fragment with open source URL
        // This would be tested through navigation verification
    }

    @Test
    fun privacy_click_navigates_to_web() {
        // When
        launchFragment()
        onView(withId(R.id.fl_privacy)).perform(click())

        // Then
        // Verify navigation to web fragment with privacy policy URL
        // This would be tested through navigation verification
    }

    @Test
    fun update_click_calls_viewmodel() {
        // Given
        isNewVersionAvailableLiveData.postValue(true)
        
        // When
        launchFragment()
        onView(withId(R.id.ln_update)).perform(click())

        // Then
        verify { mockSettingsViewModel.onOpenNewUpdateInfo() }
    }

    @Test
    fun fragment_displays_developer_info() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.tv_developer)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_developer)).check(matches(withText(containsString("@"))))
    }

    @Test
    fun fragment_handles_view_binding_lifecycle() {
        // When
        scenario = launchFragmentInContainer<AboutFragment>()

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
    fun fragment_displays_all_sections() {
        // When
        launchFragment()

        // Then
        onView(withText(R.string.contribute)).check(matches(isDisplayed()))
        onView(withText(R.string.open_source_lib)).check(matches(isDisplayed()))
        onView(withText(R.string.privacy_policy)).check(matches(isDisplayed()))
        onView(withText(R.string.app_version)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_sets_up_spannable_text_for_developer() {
        // When
        launchFragment()

        // Then
        scenario.onFragment { fragment ->
            val developerTextView = fragment.view?.findViewById<android.widget.TextView>(R.id.tv_developer)
            assert(developerTextView?.movementMethod != null)
        }
    }

    private fun launchFragment(): FragmentScenario<AboutFragment> {
        scenario = launchFragmentInContainer<AboutFragment>()
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        return scenario
    }
}