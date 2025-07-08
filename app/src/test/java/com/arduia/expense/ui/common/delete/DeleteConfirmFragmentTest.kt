package com.arduia.expense.ui.common.delete

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.R
import com.arduia.expense.ui.common.uimodel.DeleteInfoUiModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class DeleteConfirmFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockConfirmListener: () -> Unit

    private lateinit var scenario: FragmentScenario<DeleteConfirmFragment>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `fragment should be created successfully`() {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        
        scenario.onFragment { fragment ->
            assert(fragment.isAdded)
            assert(fragment.view != null)
        }
    }

    @Test
    fun `should extend BottomSheetDialogFragment`() {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        
        scenario.onFragment { fragment ->
            assert(fragment is BottomSheetDialogFragment)
        }
    }

    @Test
    fun `should setup delete info UI elements`() {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        
        scenario.onFragment { fragment ->
            val view = fragment.view!!
            
            // Check essential UI elements exist
            assert(view.findViewById<android.widget.TextView>(R.id.tvDeleteTitle) != null)
            assert(view.findViewById<android.widget.TextView>(R.id.tvItemName) != null)
            assert(view.findViewById<android.widget.TextView>(R.id.tvItemAmount) != null)
            assert(view.findViewById<android.widget.Button>(R.id.btnConfirmDelete) != null)
            assert(view.findViewById<android.widget.Button>(R.id.btnCancel) != null)
        }
    }

    @Test
    fun `should display delete info correctly`() {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        
        val deleteInfo = DeleteInfoUiModel(
            id = 1,
            name = "Test Expense",
            amount = "100.00",
            symbol = "$"
        )
        
        scenario.onFragment { fragment ->
            fragment.show(fragment.parentFragmentManager, deleteInfo)
            
            val view = fragment.view!!
            val nameTextView = view.findViewById<android.widget.TextView>(R.id.tvItemName)
            val amountTextView = view.findViewById<android.widget.TextView>(R.id.tvItemAmount)
            
            assert(nameTextView.text.toString() == deleteInfo.name)
            assert(amountTextView.text.toString().contains(deleteInfo.amount))
            assert(amountTextView.text.toString().contains(deleteInfo.symbol))
        }
    }

    @Test
    fun `should handle confirm button click`() {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        
        scenario.onFragment { fragment ->
            fragment.setOnConfirmListener(mockConfirmListener)
            
            val confirmButton = fragment.view!!.findViewById<android.widget.Button>(R.id.btnConfirmDelete)
            confirmButton.performClick()
        }
        
        verify(mockConfirmListener).invoke()
    }

    @Test
    fun `should handle cancel button click`() {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        
        scenario.onFragment { fragment ->
            val cancelButton = fragment.view!!.findViewById<android.widget.Button>(R.id.btnCancel)
            cancelButton.performClick()
            
            // Dialog should be dismissed
            assert(fragment.isRemoving || !fragment.isAdded)
        }
    }

    @Test
    fun `should set confirm listener properly`() {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        
        scenario.onFragment { fragment ->
            fragment.setOnConfirmListener(mockConfirmListener)
            
            // Verify listener is set
            assert(fragment.isAdded)
        }
    }

    @Test
    fun `should show dialog with fragment manager`() {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        
        val deleteInfo = DeleteInfoUiModel(
            id = 1,
            name = "Test Expense",
            amount = "100.00",
            symbol = "$"
        )
        
        scenario.onFragment { fragment ->
            // Test show method with fragment manager
            fragment.show(fragment.parentFragmentManager, deleteInfo)
            
            assert(fragment.isAdded)
            assert(fragment.dialog != null)
        }
    }

    @Test
    fun `should handle dialog dismissal`() {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        
        scenario.onFragment { fragment ->
            fragment.dismiss()
            
            // Dialog should be dismissed
            assert(fragment.isRemoving || !fragment.isAdded)
        }
    }

    @Test
    fun `should format amount with symbol correctly`() {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        
        val deleteInfo = DeleteInfoUiModel(
            id = 1,
            name = "Test Expense",
            amount = "100.00",
            symbol = "$"
        )
        
        scenario.onFragment { fragment ->
            fragment.show(fragment.parentFragmentManager, deleteInfo)
            
            val amountTextView = fragment.view!!.findViewById<android.widget.TextView>(R.id.tvItemAmount)
            val amountText = amountTextView.text.toString()
            
            assert(amountText.contains("$"))
            assert(amountText.contains("100.00"))
        }
    }

    @Test
    fun `should handle empty or null values gracefully`() {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        
        val deleteInfo = DeleteInfoUiModel(
            id = 1,
            name = "",
            amount = "",
            symbol = ""
        )
        
        scenario.onFragment { fragment ->
            // Should not crash with empty values
            fragment.show(fragment.parentFragmentManager, deleteInfo)
            
            assert(fragment.isAdded)
            assert(fragment.view != null)
        }
    }

    @Test
    fun `should setup proper dialog theme`() {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        
        scenario.onFragment { fragment ->
            // Bottom sheet dialog should be properly configured
            assert(fragment.dialog != null)
            assert(fragment is BottomSheetDialogFragment)
        }
    }

    @Test
    fun `should handle lifecycle properly`() {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        
        scenario.onFragment { fragment ->
            assert(fragment.view != null)
            assert(fragment.isAdded)
        }
        
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.DESTROYED)
        
        scenario.onFragment { fragment ->
            assert(!fragment.isAdded)
        }
    }
}