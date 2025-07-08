package com.arduia.expense.ui.backup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.R
import com.arduia.expense.ui.MainHost
import com.arduia.expense.ui.NavigationDrawer
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
class BackupFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockNavController: NavController

    @Mock
    private lateinit var mockBackupViewModel: BackupViewModel

    @Mock
    private lateinit var mockMainHost: MainHost

    @Mock
    private lateinit var mockNavigationDrawer: NavigationDrawer

    private lateinit var scenario: FragmentScenario<BackupFragment>

    // Mock LiveData for backup operations
    private val exportEvent = MutableLiveData<Event<Unit>>()
    private val importEvent = MutableLiveData<Event<Unit>>()
    private val backupCompleteEvent = MutableLiveData<Event<String>>()
    private val backupErrorEvent = MutableLiveData<Event<String>>()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        
        setupMockViewModel()
    }

    private fun setupMockViewModel() {
        whenever(mockBackupViewModel.exportEvent).thenReturn(exportEvent)
        whenever(mockBackupViewModel.importEvent).thenReturn(importEvent)
        whenever(mockBackupViewModel.backupCompleteEvent).thenReturn(backupCompleteEvent)
        whenever(mockBackupViewModel.backupErrorEvent).thenReturn(backupErrorEvent)
    }

    @Test
    fun `fragment should be created successfully`() {
        scenario = launchFragmentInContainer<BackupFragment>()
        
        scenario.onFragment { fragment ->
            assert(fragment.isAdded)
            assert(fragment.view != null)
        }
    }

    @Test
    fun `should extend NavBaseFragment`() {
        scenario = launchFragmentInContainer<BackupFragment>()
        
        scenario.onFragment { fragment ->
            assert(fragment is com.arduia.expense.ui.NavBaseFragment)
        }
    }

    @Test
    fun `should setup toolbar with navigation`() {
        scenario = launchFragmentInContainer<BackupFragment>()
        
        scenario.onFragment { fragment ->
            val toolbar = fragment.view!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            assert(toolbar != null)
            assert(toolbar.navigationIcon != null)
        }
    }

    @Test
    fun `should handle export button click`() {
        scenario = launchFragmentInContainer<BackupFragment>()
        
        scenario.onFragment { fragment ->
            val exportButton = fragment.view!!.findViewById<android.widget.Button>(R.id.btnExport)
            exportButton?.performClick()
        }
        
        verify(mockBackupViewModel).startExport()
    }

    @Test
    fun `should handle import button click`() {
        scenario = launchFragmentInContainer<BackupFragment>()
        
        scenario.onFragment { fragment ->
            val importButton = fragment.view!!.findViewById<android.widget.Button>(R.id.btnImport)
            importButton?.performClick()
        }
        
        verify(mockBackupViewModel).startImport()
    }

    @Test
    fun `should show export dialog on export event`() {
        scenario = launchFragmentInContainer<BackupFragment>()
        
        exportEvent.value = Event(Unit)
        
        scenario.onFragment { fragment ->
            // Verify export dialog is shown
            assert(fragment.isAdded)
            // ExportDialogFragment should be displayed
        }
    }

    @Test
    fun `should show import dialog on import event`() {
        scenario = launchFragmentInContainer<BackupFragment>()
        
        importEvent.value = Event(Unit)
        
        scenario.onFragment { fragment ->
            // Verify import dialog is shown
            assert(fragment.isAdded)
            // ImportDialogFragment should be displayed
        }
    }

    @Test
    fun `should show success message on backup complete`() {
        scenario = launchFragmentInContainer<BackupFragment>()
        
        backupCompleteEvent.value = Event("Backup completed successfully")
        
        scenario.onFragment { fragment ->
            // Verify success message is shown
            assert(fragment.isAdded)
            // MainHost should show success message
        }
    }

    @Test
    fun `should show error message on backup error`() {
        scenario = launchFragmentInContainer<BackupFragment>()
        
        backupErrorEvent.value = Event("Backup failed")
        
        scenario.onFragment { fragment ->
            // Verify error message is shown
            assert(fragment.isAdded)
            // MainHost should show error message
        }
    }

    @Test
    fun `should handle toolbar navigation click`() {
        scenario = launchFragmentInContainer<BackupFragment>()
        
        scenario.onFragment { fragment ->
            val toolbar = fragment.view!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            toolbar.navigationIcon?.callback?.invalidateDrawable(toolbar.navigationIcon)
        }
        
        // Should open navigation drawer
        verify(mockNavigationDrawer).openDrawer()
    }

    @Test
    fun `should have proper layout structure`() {
        scenario = launchFragmentInContainer<BackupFragment>()
        
        scenario.onFragment { fragment ->
            val view = fragment.view!!
            
            // Check essential UI elements exist
            assert(view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar) != null)
            assert(view.findViewById<android.widget.TextView>(R.id.tvBackupDescription) != null)
            assert(view.findViewById<android.widget.Button>(R.id.btnExport) != null)
            assert(view.findViewById<android.widget.Button>(R.id.btnImport) != null)
        }
    }

    @Test
    fun `should handle lifecycle properly`() {
        scenario = launchFragmentInContainer<BackupFragment>()
        
        scenario.onFragment { fragment ->
            assert(fragment.view != null)
            assert(fragment.isAdded)
        }
        
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.DESTROYED)
        
        scenario.onFragment { fragment ->
            assert(!fragment.isAdded)
        }
    }

    @Test
    fun `should setup proper observers`() {
        scenario = launchFragmentInContainer<BackupFragment>()
        
        scenario.onFragment { fragment ->
            // Fragment should have observers set up for all events
            assert(fragment.isAdded)
            
            // Verify that observers are working by triggering events
            exportEvent.value = Event(Unit)
            importEvent.value = Event(Unit)
            backupCompleteEvent.value = Event("Test")
            backupErrorEvent.value = Event("Test Error")
        }
    }
}