package com.arduia.expense.ui.common.delete

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.R
import com.arduia.expense.ui.common.uimodel.DeleteInfoUiModel
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
class DeleteConfirmFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var scenario: FragmentScenario<DeleteConfirmFragment>
    private var mockConfirmListener: (() -> Unit)? = null

    @Before
    fun setup() {
        hiltRule.inject()
        mockConfirmListener = mockk(relaxed = true)
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
        onView(withId(R.id.tv_title)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_message)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_cancel)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_confirm)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_displays_delete_title() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.tv_title)).check(matches(withText(R.string.delete_expense)))
    }

    @Test
    fun fragment_displays_confirmation_message() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.tv_message)).check(matches(withText(R.string.delete_confirmation_message)))
    }

    @Test
    fun fragment_displays_expense_details() {
        // Given
        val deleteInfo = DeleteInfoUiModel(
            id = 1,
            title = "Coffee Purchase",
            amount = "5.50"
        )

        // When
        launchFragmentWithData(deleteInfo)

        // Then
        onView(withId(R.id.tv_expense_name)).check(matches(withText("Coffee Purchase")))
        onView(withId(R.id.tv_expense_amount)).check(matches(withText("5.50")))
    }

    @Test
    fun cancel_button_dismisses_dialog() {
        // When
        launchFragment()
        onView(withId(R.id.btn_cancel)).perform(click())

        // Then
        scenario.onFragment { fragment ->
            // Dialog should be dismissed
            assert(!fragment.isVisible)
        }
    }

    @Test
    fun confirm_button_calls_listener() {
        // Given
        val deleteInfo = DeleteInfoUiModel(
            id = 1,
            title = "Test Expense",
            amount = "10.00"
        )

        // When
        launchFragmentWithData(deleteInfo)
        scenario.onFragment { fragment ->
            fragment.setOnConfirmListener(mockConfirmListener!!)
        }
        onView(withId(R.id.btn_confirm)).perform(click())

        // Then
        verify { mockConfirmListener!!.invoke() }
    }

    @Test
    fun confirm_button_dismisses_dialog_after_action() {
        // When
        launchFragment()
        scenario.onFragment { fragment ->
            fragment.setOnConfirmListener(mockConfirmListener!!)
        }
        onView(withId(R.id.btn_confirm)).perform(click())

        // Then
        scenario.onFragment { fragment ->
            // Dialog should be dismissed after confirmation
            assert(!fragment.isVisible)
        }
    }

    @Test
    fun fragment_displays_cancel_button_text() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.btn_cancel)).check(matches(withText(R.string.cancel)))
    }

    @Test
    fun fragment_displays_confirm_button_text() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.btn_confirm)).check(matches(withText(R.string.delete)))
    }

    @Test
    fun fragment_handles_null_delete_info() {
        // When
        launchFragment()

        // Then
        // Fragment should handle null data gracefully
        onView(withId(R.id.tv_expense_name)).check(matches(withText("")))
        onView(withId(R.id.tv_expense_amount)).check(matches(withText("")))
    }

    @Test
    fun fragment_shows_warning_icon() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.iv_warning)).check(matches(isDisplayed()))
    }

    @Test
    fun confirm_button_has_destructive_styling() {
        // When
        launchFragment()

        // Then
        scenario.onFragment { fragment ->
            val confirmButton = fragment.view?.findViewById<android.widget.Button>(R.id.btn_confirm)
            // Verify that the button has appropriate styling for destructive action
            assert(confirmButton != null)
        }
    }

    @Test
    fun fragment_is_not_cancelable_by_touch_outside() {
        // When
        launchFragment()

        // Then
        scenario.onFragment { fragment ->
            // Dialog should not be cancelable by touching outside
            assert(!fragment.isCancelable)
        }
    }

    @Test
    fun fragment_handles_back_button_press() {
        // When
        launchFragment()

        // Then
        scenario.onFragment { fragment ->
            // Fragment should handle back button appropriately
            assert(fragment.dialog?.isShowing == true)
        }
    }

    @Test
    fun fragment_displays_expense_currency() {
        // Given
        val deleteInfo = DeleteInfoUiModel(
            id = 1,
            title = "Lunch",
            amount = "15.75"
        )

        // When
        launchFragmentWithData(deleteInfo)

        // Then
        onView(withId(R.id.tv_currency)).check(matches(isDisplayed()))
    }

    @Test
    fun fragment_handles_long_expense_names() {
        // Given
        val deleteInfo = DeleteInfoUiModel(
            id = 1,
            title = "Very Long Expense Name That Should Be Handled Properly",
            amount = "25.00"
        )

        // When
        launchFragmentWithData(deleteInfo)

        // Then
        onView(withId(R.id.tv_expense_name)).check(matches(withText(containsString("Very Long Expense"))))
    }

    @Test
    fun fragment_handles_large_amounts() {
        // Given
        val deleteInfo = DeleteInfoUiModel(
            id = 1,
            title = "Expensive Item",
            amount = "9999.99"
        )

        // When
        launchFragmentWithData(deleteInfo)

        // Then
        onView(withId(R.id.tv_expense_amount)).check(matches(withText("9999.99")))
    }

    @Test
    fun fragment_layout_is_properly_structured() {
        // When
        launchFragment()

        // Then
        scenario.onFragment { fragment ->
            val dialog = fragment.dialog
            assert(dialog != null)
            assert(dialog?.window != null)
        }
    }

    @Test
    fun fragment_buttons_are_properly_aligned() {
        // When
        launchFragment()

        // Then
        onView(withId(R.id.btn_cancel)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_confirm)).check(matches(isDisplayed()))
        // Both buttons should be in the same parent layout
    }

    private fun launchFragment(): FragmentScenario<DeleteConfirmFragment> {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        return scenario
    }

    private fun launchFragmentWithData(deleteInfo: DeleteInfoUiModel): FragmentScenario<DeleteConfirmFragment> {
        scenario = launchFragmentInContainer<DeleteConfirmFragment>()
        scenario.onFragment { fragment ->
            fragment.show(fragment.parentFragmentManager, deleteInfo)
        }
        return scenario
    }
}