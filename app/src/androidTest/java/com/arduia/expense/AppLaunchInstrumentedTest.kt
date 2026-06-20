package com.arduia.expense

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppLaunchInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun mainActivity_launchesWithoutCrashing() {
        activityRule.scenario.onActivity { activity ->
            check(activity.application is ExpenseApplication)
        }
    }
}
