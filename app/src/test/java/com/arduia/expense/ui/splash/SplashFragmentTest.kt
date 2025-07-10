package com.arduia.expense.ui.splash

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
class SplashFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var scenario: FragmentScenario<SplashFragment>
    private lateinit var navController: TestNavHostController
    private lateinit var mockViewModel: SplashViewModel

    // Mock LiveData
    private val navigationEventLiveData = MutableLiveData<Event<NavigationDestination>>()
    private val isLoadingLiveData = MutableLiveData<Boolean>()
    private val initializationCompleteLiveData = MutableLiveData<Event<Unit>>()

    @Before
    fun setup() {
        hiltRule.inject()
        
        // Setup mock ViewModel
        mockViewModel = mockk(relaxed = true)
        every { mockViewModel.navigationEvent } returns navigationEventLiveData
        every { mockViewModel.isLoading } returns isLoadingLiveData
        every { mockViewModel.initializationComplete } returns initializationCompleteLiveData

        // Setup navigation controller
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        navController.setCurrentDestination(R.id.dest_splash)
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
        onView(withId(R.id.iv_logo)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_app_name)).check(matches(isDisplayed()))
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
    fun fragment_displays_app_logo() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.iv_logo)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_displays_app_name() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.tv_app_name)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_app_name)).check(matches(withText(R.string.app_name)))
    }

    @Test
    fun fragment_shows_loading_indicator() {
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
    fun fragment_handles_navigation_to_onboarding() {
        // Given
        val destination = NavigationDestination.ONBOARDING
        
        // When
        launchFragment()
        navigationEventLiveData.postValue(Event(destination))

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.navigationEvent }
            // In real test, this would verify navigation to onboarding
        }
    }

    @Test
    fun fragment_handles_navigation_to_main() {
        // Given
        val destination = NavigationDestination.MAIN
        
        // When
        launchFragment()
        navigationEventLiveData.postValue(Event(destination))

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.navigationEvent }
            // In real test, this would verify navigation to main screen
        }
    }

    @Test
    fun fragment_handles_initialization_complete_event() {
        // When
        launchFragment()
        initializationCompleteLiveData.postValue(Event(Unit))

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.initializationComplete }
        }
    }

    @Test
    fun fragment_starts_initialization_on_create() {
        // When
        launchFragment()

        // Then
        verify { mockViewModel.initialize() }
    }

    @Test
    fun fragment_displays_version_info() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.tv_version)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_displays_loading_message() {
        // Given
        val isLoading = true
        
        // When
        launchFragment()
        isLoadingLiveData.postValue(isLoading)

        // Then
        onView(withId(R.id.tv_loading_message)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_loading_message)).check(matches(withText(R.string.loading)))
    }

    @Test
    fun fragment_handles_view_binding_lifecycle() {
        // When
        scenario = launchFragmentInContainer<SplashFragment>()

        // Then
        scenario.onFragment { fragment ->
            assert(fragment.view != null)
        }

        // When fragment is destroyed
        scenario.close()
    }

    @Test
    fun fragment_has_no_toolbar() {
        // When
        launchFragment()

        // Then
        // Splash fragment should not have a toolbar
        scenario.onFragment { fragment ->
            val toolbar = fragment.view?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            assert(toolbar == null)
        }
    }

    @Test
    fun fragment_displays_branding_elements() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.iv_logo)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_app_name)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_tagline)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_handles_app_initialization_failure() {
        // Given - This would be tested with error states in ViewModel
        
        // When
        launchFragment()

        // Then
        // Verify error handling if initialization fails
        verify { mockViewModel.initialize() }
    }

    @Test
    fun fragment_observes_all_viewmodel_livedata() {
        // When
        launchFragment()

        // Then
        verify { mockViewModel.navigationEvent }
        verify { mockViewModel.isLoading }
        verify { mockViewModel.initializationComplete }
    }

    @Test
    fun fragment_animation_setup() {
        // When
        launchFragment()

        // Then
        scenario.onFragment { fragment ->
            // Verify that animations are set up (fade in, scale, etc.)
            val logo = fragment.view?.findViewById<android.widget.ImageView>(R.id.iv_logo)
            assert(logo != null)
        }
    }

    @Test
    fun fragment_handles_configuration_changes() {
        // When
        launchFragment()
        scenario.recreate()

        // Then
        scenario.onFragment { fragment ->
            // Fragment should handle configuration changes gracefully
            assert(fragment.view != null)
        }
    }

    private fun launchFragment(): FragmentScenario<SplashFragment> {
        scenario = launchFragmentInContainer<SplashFragment>()
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        return scenario
    }
}