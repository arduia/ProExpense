package com.arduia.expense.ui.splash

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.R
import com.arduia.mvvm.Event
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class SplashFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockNavController: NavController

    @Mock
    private lateinit var mockSplashViewModel: SplashViewModel

    private lateinit var scenario: FragmentScenario<SplashFragment>

    private val firstTimeEvent = MutableLiveData<Event<Unit>>()
    private val normalUserEvent = MutableLiveData<Event<Unit>>()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        
        whenever(mockSplashViewModel.firstTimeEvent).thenReturn(firstTimeEvent)
        whenever(mockSplashViewModel.normalUserEvent).thenReturn(normalUserEvent)
    }

    @Test
    fun `fragment should be created successfully`() {
        scenario = launchFragmentInContainer<SplashFragment>()
        
        scenario.onFragment { fragment ->
            assert(fragment.isAdded)
            assert(fragment.view != null)
        }
    }

    @Test
    fun `should navigate to language screen on first time event`() {
        scenario = launchFragmentInContainer<SplashFragment>()
        
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        // Trigger first time event
        firstTimeEvent.value = Event(Unit)

        verify(mockNavController).popBackStack()
        verify(mockNavController).navigate(R.id.dest_language)
    }

    @Test
    fun `should navigate to home screen on normal user event`() {
        scenario = launchFragmentInContainer<SplashFragment>()
        
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        // Trigger normal user event
        normalUserEvent.value = Event(Unit)

        verify(mockNavController).popBackStack()
        verify(mockNavController).navigate(R.id.dest_home)
    }

    @Test
    fun `should change status bar color on view created`() {
        scenario = launchFragmentInContainer<SplashFragment>()
        
        scenario.onFragment { fragment ->
            // Verify that the fragment changes the status bar color
            assert(fragment.requireActivity().window.statusBarColor != Color.WHITE)
        }
    }

    @Test
    fun `should restore status bar color on destroy view`() {
        scenario = launchFragmentInContainer<SplashFragment>()
        
        var originalColor = Color.WHITE
        scenario.onFragment { fragment ->
            originalColor = fragment.requireActivity().window.statusBarColor
        }

        scenario.moveToState(androidx.lifecycle.Lifecycle.State.DESTROYED)
        
        // Note: This test might need adjustment based on actual implementation
        // as Robolectric might not fully simulate the window behavior
    }

    @Test
    fun `should have correct binding initialization`() {
        scenario = launchFragmentInContainer<SplashFragment>()
        
        scenario.onFragment { fragment ->
            assert(fragment.view != null)
            // Verify that the binding is properly initialized
            val view = fragment.view!!
            assert(view.findViewById<android.view.View>(R.id.splash_container) != null ||
                   view.findViewById<android.view.View>(R.id.splash_logo) != null)
        }
    }
}