package com.arduia.expense.data.backup

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.testing.WorkManagerTestInitHelper
import com.arduia.backup.BackupException
import com.arduia.backup.ExcelBackup
import com.arduia.backup.task.Result
import com.arduia.backup.task.getDataOrError
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.io.InputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ImportWorkerTest {

    private lateinit var worker: ImportWorker
    private lateinit var mockContext: Context
    private lateinit var mockWorkerParams: WorkerParameters
    private lateinit var mockContentResolver: ContentResolver
    private lateinit var mockExcelBackup: ExcelBackup
    private lateinit var mockInputStream: InputStream

    @Before
    fun setup() {
        mockContext = mockk()
        mockWorkerParams = mockk()
        mockContentResolver = mockk()
        mockExcelBackup = mockk()
        mockInputStream = ByteArrayInputStream("test data".toByteArray())

        // Setup default mocks
        every { mockContext.contentResolver } returns mockContentResolver
        every { mockWorkerParams.inputData } returns Data.EMPTY
        
        // Initialize WorkManager for testing
        WorkManagerTestInitHelper.initializeTestWorkManager(mockContext)
    }

    @Test
    fun `doWork should return failure when FILE_URI is missing`() = runTest {
        // Given
        val inputData = Data.Builder().build()
        every { mockWorkerParams.inputData } returns inputData

        worker = ImportWorker(
            mockContext,
            mockWorkerParams,
            mockContentResolver,
            mockExcelBackup
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Failure)
    }

    @Test
    fun `doWork should return failure when ContentResolver cannot open input stream`() = runTest {
        // Given
        val fileUri = "content://test/file.xlsx"
        val inputData = Data.Builder()
            .putString(ImportWorker.FILE_URI, fileUri)
            .build()
        
        every { mockWorkerParams.inputData } returns inputData
        every { mockContentResolver.openInputStream(any()) } returns null

        worker = ImportWorker(
            mockContext,
            mockWorkerParams,
            mockContentResolver,
            mockExcelBackup
        )

        // When & Then
        assertThrows(Exception::class.java) {
            runTest { worker.doWork() }
        }
    }

    @Test
    fun `doWork should return success with import count when import succeeds`() = runTest {
        // Given
        val fileUri = "content://test/file.xlsx"
        val importCount = 150
        val inputData = Data.Builder()
            .putString(ImportWorker.FILE_URI, fileUri)
            .build()
        
        val mockResult = mockk<Result<Int>>()
        every { mockResult.getDataOrError() } returns importCount
        
        every { mockWorkerParams.inputData } returns inputData
        every { mockContentResolver.openInputStream(any()) } returns mockInputStream
        every { mockExcelBackup.import(any()) } returns mockResult

        worker = ImportWorker(
            mockContext,
            mockWorkerParams,
            mockContentResolver,
            mockExcelBackup
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Success)
        val successResult = result as androidx.work.ListenableWorker.Result.Success
        assertEquals(importCount, successResult.outputData.getInt(ImportWorker.KEY_IMPORT_COUNT, -1))
        
        verify { 
            mockContentResolver.openInputStream(Uri.parse(fileUri))
            mockExcelBackup.import(mockInputStream)
        }
    }

    @Test
    fun `doWork should return failure when BackupException occurs`() = runTest {
        // Given
        val fileUri = "content://test/file.xlsx"
        val inputData = Data.Builder()
            .putString(ImportWorker.FILE_URI, fileUri)
            .build()
        
        every { mockWorkerParams.inputData } returns inputData
        every { mockContentResolver.openInputStream(any()) } returns mockInputStream
        every { mockExcelBackup.import(any()) } throws BackupException("Import failed")

        worker = ImportWorker(
            mockContext,
            mockWorkerParams,
            mockContentResolver,
            mockExcelBackup
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Failure)
    }

    @Test
    fun `doWork should return failure when general exception occurs`() = runTest {
        // Given
        val fileUri = "content://test/file.xlsx"
        val inputData = Data.Builder()
            .putString(ImportWorker.FILE_URI, fileUri)
            .build()
        
        every { mockWorkerParams.inputData } returns inputData
        every { mockContentResolver.openInputStream(any()) } throws RuntimeException("General error")

        worker = ImportWorker(
            mockContext,
            mockWorkerParams,
            mockContentResolver,
            mockExcelBackup
        )

        // When & Then
        assertThrows(RuntimeException::class.java) {
            runTest { worker.doWork() }
        }
    }
}