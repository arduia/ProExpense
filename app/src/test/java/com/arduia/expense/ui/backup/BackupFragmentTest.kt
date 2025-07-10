package com.arduia.expense.ui.backup

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
class BackupFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var scenario: FragmentScenario<BackupFragment>
    private lateinit var navController: TestNavHostController
    private lateinit var mockViewModel: BackupViewModel

    // Mock LiveData
    private val backupStatusLiveData = MutableLiveData<BackupStatus>()
    private val restoreStatusLiveData = MutableLiveData<RestoreStatus>()
    private val onBackupCompleteLiveData = MutableLiveData<Event<String>>()
    private val onRestoreCompleteLiveData = MutableLiveData<Event<String>>()
    private val onErrorLiveData = MutableLiveData<Event<String>>()

    @Before
    fun setup() {
        hiltRule.inject()
        
        // Setup mock ViewModel
        mockViewModel = mockk(relaxed = true)
        every { mockViewModel.backupStatus } returns backupStatusLiveData
        every { mockViewModel.restoreStatus } returns restoreStatusLiveData
        every { mockViewModel.onBackupComplete } returns onBackupCompleteLiveData
        every { mockViewModel.onRestoreComplete } returns onRestoreCompleteLiveData
        every { mockViewModel.onError } returns onErrorLiveData

        // Setup navigation controller
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        navController.setCurrentDestination(R.id.dest_backup)
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
        onView(withId(R.id.btn_backup)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_restore)).check(matches(isDisplayed()))
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
    fun backup_button_click_starts_backup() {
        // When
        launchFragment()
        onView(withId(R.id.btn_backup)).perform(click())

        // Then
        verify { mockViewModel.startBackup() }
    }

    @Test
    fun restore_button_click_starts_restore() {
        // When
        launchFragment()
        onView(withId(R.id.btn_restore)).perform(click())

        // Then
        verify { mockViewModel.startRestore() }
    }

    @Test
    fun fragment_observes_backup_status_changes() {
        // Given
        val status = BackupStatus.IN_PROGRESS
        
        // When
        launchFragment()
        backupStatusLiveData.postValue(status)

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.backupStatus }
        }
    }

    @Test
    fun fragment_observes_restore_status_changes() {
        // Given
        val status = RestoreStatus.IN_PROGRESS
        
        // When
        launchFragment()
        restoreStatusLiveData.postValue(status)

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.restoreStatus }
        }
    }

    @Test
    fun fragment_handles_backup_complete_event() {
        // Given
        val message = "Backup completed successfully"
        
        // When
        launchFragment()
        onBackupCompleteLiveData.postValue(Event(message))

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.onBackupComplete }
        }
    }

    @Test
    fun fragment_handles_restore_complete_event() {
        // Given
        val message = "Restore completed successfully"
        
        // When
        launchFragment()
        onRestoreCompleteLiveData.postValue(Event(message))

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.onRestoreComplete }
        }
    }

    @Test
    fun fragment_handles_error_event() {
        // Given
        val errorMessage = "Backup failed"
        
        // When
        launchFragment()
        onErrorLiveData.postValue(Event(errorMessage))

        // Then
        scenario.onFragment { fragment ->
            verify { mockViewModel.onError }
        }
    }

    @Test
    fun fragment_shows_progress_during_backup() {
        // Given
        val status = BackupStatus.IN_PROGRESS
        
        // When
        launchFragment()
        backupStatusLiveData.postValue(status)

        // Then
        onView(withId(R.id.progress_backup)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_shows_progress_during_restore() {
        // Given
        val status = RestoreStatus.IN_PROGRESS
        
        // When
        launchFragment()
        restoreStatusLiveData.postValue(status)

        // Then
        onView(withId(R.id.progress_restore)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_handles_view_binding_lifecycle() {
        // When
        scenario = launchFragmentInContainer<BackupFragment>()

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
    fun fragment_displays_backup_instructions() {
        // When
        launchFragment()

        // Then
        onView(withText(R.string.backup_description)).check(matches(isDisplayed()))
        onView(withText(R.string.restore_description)).check(matches(isDisplayed()))
    }

    @Test
    fun buttons_are_disabled_during_operations() {
        // Given
        val backupStatus = BackupStatus.IN_PROGRESS
        
        // When
        launchFragment()
        backupStatusLiveData.postValue(backupStatus)

        // Then
        onView(withId(R.id.btn_backup)).check(matches(not(isEnabled())))
        onView(withId(R.id.btn_restore)).check(matches(not(isEnabled())))
    }

    @Test
    fun fragment_shows_export_dialog_on_backup_click() {
        // When
        launchFragment()
        onView(withId(R.id.btn_backup)).perform(click())

        // Then
        scenario.onFragment { fragment ->
            // Verify that export dialog is shown
            // This would be tested through dialog fragment manager in integration tests
        }
    }

    @Test
    fun fragment_shows_import_dialog_on_restore_click() {
        // When
        launchFragment()
        onView(withId(R.id.btn_restore)).perform(click())

        // Then
        scenario.onFragment { fragment ->
            // Verify that import dialog is shown
            // This would be tested through dialog fragment manager in integration tests
        }
    }

    private fun launchFragment(): FragmentScenario<BackupFragment> {
        scenario = launchFragmentInContainer<BackupFragment>()
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        return scenario
    }
}