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
class StatisticsScreenshotTest {

    @Test
    fun statisticsLayout() {
        inflateLayout(R.layout.fragment_statistic)
            .measureAndCapture("src/test/snapshots/statistics/statistics_layout.png")
    }

    @Test
    fun filterDialogLayout() {
        inflateLayout(R.layout.filter_expense_dialog)
            .measureAndCapture("src/test/snapshots/statistics/filter_dialog.png", heightDp = 350)
    }
}
