package com.arduia.expense.screenshot

import android.app.Application
import com.arduia.expense.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = Application::class, qualifiers = "w360dp-h720dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ExpenseEntryScreenshotTest {

    @Test
    fun expenseEntryLayout() {
        inflateLayout(R.layout.fragment_expense_entry)
            .measureAndCapture("src/test/snapshots/expense_entry/expense_entry_layout.png")
    }

    @Test
    fun expenseDetailDialogLayout() {
        inflateLayout(R.layout.expense_detail_dialog)
            .measureAndCapture("src/test/snapshots/expense_entry/expense_detail_dialog.png", heightDp = 400)
    }

    @Test
    fun deleteConfirmDialogLayout() {
        inflateLayout(R.layout.fragment_delete_confirm_dialog)
            .measureAndCapture("src/test/snapshots/expense_entry/delete_confirm_dialog.png", heightDp = 300)
    }
}
