package com.arduia.expense.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class NavBaseFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockNavigationDrawer: NavigationDrawer

    // Test implementation of NavBaseFragment
    class TestNavBaseFragment : NavBaseFragment() {
        fun getNavigationDrawer(): NavigationDrawer = navigationDrawer
    }

    private lateinit var scenario: FragmentScenario<TestNavBaseFragment>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `fragment should be created successfully`() {
        scenario = launchFragmentInContainer<TestNavBaseFragment>()
        
        scenario.onFragment { fragment ->
            assert(fragment.isAdded)
        }
    }

    @Test
    fun `should throw exception when host activity does not implement NavigationDrawer`() {
        scenario = launchFragmentInContainer<TestNavBaseFragment>()
        
        scenario.onFragment { fragment ->
            try {
                // This should throw an exception since the test activity doesn't implement NavigationDrawer
                fragment.getNavigationDrawer()
                assert(false) { "Expected exception was not thrown" }
            } catch (e: Exception) {
                assert(e.message?.contains("NavigationDrawer") == true)
            }
        }
    }

    @Test
    fun `should properly handle onViewCreated lifecycle`() {
        scenario = launchFragmentInContainer<TestNavBaseFragment>()
        
        scenario.onFragment { fragment ->
            assert(fragment.view != null)
            // Fragment should be properly initialized
            assert(fragment.isAdded)
        }
    }

    @Test
    fun `should properly handle onDestroyView lifecycle`() {
        scenario = launchFragmentInContainer<TestNavBaseFragment>()
        
        // Move to destroyed state
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.DESTROYED)
        
        // Fragment should be properly cleaned up
        // Note: We can't test the private _navDrawer field directly,
        // but we can verify the fragment is properly destroyed
        scenario.onFragment { fragment ->
            assert(!fragment.isAdded)
        }
    }

    @Test
    fun `fragment should have correct inheritance`() {
        scenario = launchFragmentInContainer<TestNavBaseFragment>()
        
        scenario.onFragment { fragment ->
            assert(fragment is androidx.fragment.app.Fragment)
            assert(fragment is NavBaseFragment)
        }
    }
}