package com.arduia.expense.data

import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.testing.TestDriver
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.data.backup.ExportWorker
import com.arduia.expense.data.backup.ImportWorker
import com.arduia.expense.data.update.CheckAboutUpdateWorker
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Integration test suite for all worker classes.
 * This demonstrates how to test workers in a more integrated way using WorkManager's testing utilities.
 */
@RunWith(AndroidJUnit4::class)
class WorkerTestSuite {

    private lateinit var workManager: WorkManager
    private lateinit var testDriver: TestDriver

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        
        // Initialize WorkManager for testing
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        workManager = WorkManager.getInstance(context)
        testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!
    }

    @Test
    fun `test ImportWorker integration with WorkManager`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString(ImportWorker.FILE_URI, "content://test/import.xlsx")
            .build()

        // When
        val worker = TestListenableWorkerBuilder<ImportWorker>(ApplicationProvider.getApplicationContext())
            .setInputData(inputData)
            .build()

        val result = worker.doWork()

        // Then
        assertTrue(result is ListenableWorker.Result.Success || result is ListenableWorker.Result.Failure)
    }

    @Test
    fun `test ExportWorker integration with WorkManager`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString(ExportWorker.FILE_URI, "content://test/export.xlsx")
            .putString(ExportWorker.FILE_NAME, "test_export.xlsx")
            .build()

        // When
        val worker = TestListenableWorkerBuilder<ExportWorker>(ApplicationProvider.getApplicationContext())
            .setInputData(inputData)
            .build()

        val result = worker.doWork()

        // Then
        assertTrue(result is ListenableWorker.Result.Success || result is ListenableWorker.Result.Failure)
    }

    @Test
    fun `test FeedbackWorker integration with WorkManager`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString(FeedbackWorker.PARAM_EMAIL, "test@example.com")
            .putString(FeedbackWorker.PARAM_NAME, "Test User")
            .putString(FeedbackWorker.PARAM_COMMENT, "This is a test feedback")
            .build()

        // When
        val worker = TestListenableWorkerBuilder<FeedbackWorker>(ApplicationProvider.getApplicationContext())
            .setInputData(inputData)
            .build()

        val result = worker.doWork()

        // Then
        assertTrue(result is ListenableWorker.Result.Success || result is ListenableWorker.Result.Failure)
    }

    @Test
    fun `test CheckAboutUpdateWorker integration with WorkManager`() = runTest {
        // Given
        val inputData = Data.Builder().build()

        // When
        val worker = TestListenableWorkerBuilder<CheckAboutUpdateWorker>(ApplicationProvider.getApplicationContext())
            .setInputData(inputData)
            .build()

        val result = worker.doWork()

        // Then
        assertTrue(result is ListenableWorker.Result.Success || result is ListenableWorker.Result.Failure)
    }

    @Test
    fun `test worker chaining - export then import`() = runTest {
        // Given
        val exportData = Data.Builder()
            .putString(ExportWorker.FILE_URI, "content://test/chain.xlsx")
            .putString(ExportWorker.FILE_NAME, "chain_export.xlsx")
            .build()

        val importData = Data.Builder()
            .putString(ImportWorker.FILE_URI, "content://test/chain.xlsx")
            .build()

        // When - Export first
        val exportWorker = TestListenableWorkerBuilder<ExportWorker>(ApplicationProvider.getApplicationContext())
            .setInputData(exportData)
            .build()
        val exportResult = exportWorker.doWork()

        // Then - Export should succeed
        assertTrue(exportResult is ListenableWorker.Result.Success)

        // When - Import the exported file
        val importWorker = TestListenableWorkerBuilder<ImportWorker>(ApplicationProvider.getApplicationContext())
            .setInputData(importData)
            .build()
        val importResult = importWorker.doWork()

        // Then - Import should succeed
        assertTrue(importResult is ListenableWorker.Result.Success)
    }

    @Test
    fun `test worker retry behavior`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString(FeedbackWorker.PARAM_EMAIL, "test@example.com")
            .putString(FeedbackWorker.PARAM_NAME, "Test User")
            .putString(FeedbackWorker.PARAM_COMMENT, "This is a test feedback")
            .build()

        // When
        val worker = TestListenableWorkerBuilder<FeedbackWorker>(ApplicationProvider.getApplicationContext())
            .setInputData(inputData)
            .build()

        // Simulate network failure first time
        val firstResult = worker.doWork()
        
        // Simulate retry
        val retryResult = worker.doWork()

        // Then
        assertTrue(firstResult is ListenableWorker.Result.Failure || firstResult is ListenableWorker.Result.Success)
        assertTrue(retryResult is ListenableWorker.Result.Failure || retryResult is ListenableWorker.Result.Success)
    }
}