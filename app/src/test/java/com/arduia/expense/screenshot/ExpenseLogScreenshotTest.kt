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
class ExpenseLogScreenshotTest {

    @Test
    fun expenseLogLayout() {
        inflateLayout(R.layout.fragment_expense_logs)
            .measureAndCapture("src/test/snapshots/expense_log/expense_log_layout.png")
    }

    @Test
    fun expenseLogEmptyState() {
        inflateLayout(R.layout.layout_no_expense_logs)
            .measureAndCapture("src/test/snapshots/expense_log/expense_log_empty.png", heightDp = 200)
    }
}
