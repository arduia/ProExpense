package com.arduia.expense.ui

import com.arduia.expense.ui.backup.BackupFragmentTest
import com.arduia.expense.ui.common.delete.DeleteConfirmFragmentTest
import com.arduia.expense.ui.entry.ExpenseEntryFragmentTest
import com.arduia.expense.ui.home.HomeFragmentTest
import com.arduia.expense.ui.splash.SplashFragmentTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test suite for all Fragment unit tests.
 * This runs all Fragment tests together for comprehensive testing.
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    SplashFragmentTest::class,
    NavBaseFragmentTest::class,
    HomeFragmentTest::class,
    ExpenseEntryFragmentTest::class,
    BackupFragmentTest::class,
    DeleteConfirmFragmentTest::class
)
class FragmentTestSuite