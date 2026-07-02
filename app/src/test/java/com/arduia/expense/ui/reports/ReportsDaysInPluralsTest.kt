package com.arduia.expense.ui.reports

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.arduia.expense.feature.reports.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ReportsDaysInPluralsTest {

    private val resources = ApplicationProvider.getApplicationContext<Context>().resources

    @Test
    fun daysIn_singleDay_usesSingularForm() {
        val label = resources.getQuantityString(R.plurals.reports_days_in, 1, 1)

        assertEquals("1 day in", label)
    }

    @Test
    fun daysIn_multipleDays_usesPluralForm() {
        val label = resources.getQuantityString(R.plurals.reports_days_in, 25, 25)

        assertEquals("25 days in", label)
    }
}
