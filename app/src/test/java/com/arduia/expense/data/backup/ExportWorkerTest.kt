package com.arduia.expense.data.backup

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.testing.WorkManagerTestInitHelper
import com.arduia.backup.ExcelBackup
import com.arduia.backup.task.Result
import com.arduia.backup.task.getDataOrError
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.model.awaitValueOrError
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ExportWorkerTest {

    private lateinit var worker: ExportWorker
    private lateinit var mockContext: Context
    private lateinit var mockWorkerParams: WorkerParameters
    private lateinit var mockContentResolver: ContentResolver
    private lateinit var mockBackupRepo: BackupRepository
    private lateinit var mockExcelBackup: ExcelBackup
    private lateinit var mockOutputStream: OutputStream

    @Before
    fun setup() {
        mockContext = mockk()
        mockWorkerParams = mockk()
        mockContentResolver = mockk()
        mockBackupRepo = mockk()
        mockExcelBackup = mockk()
        mockOutputStream = ByteArrayOutputStream()

        // Setup default mocks
        every { mockContext.contentResolver } returns mockContentResolver
        every { mockWorkerParams.inputData } returns Data.EMPTY
        every { mockWorkerParams.id } returns java.util.UUID.randomUUID()
        
        // Initialize WorkManager for testing
        WorkManagerTestInitHelper.initializeTestWorkManager(mockContext)
    }

    @Test
    fun `doWork should return failure when FILE_URI is missing`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString(ExportWorker.FILE_NAME, "test.xlsx")
            .build()
        every { mockWorkerParams.inputData } returns inputData

        worker = ExportWorker(
            mockContext,
            mockWorkerParams,
            mockContentResolver,
            mockBackupRepo,
            mockExcelBackup
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Failure)
    }

    @Test
    fun `doWork should return failure when FILE_NAME is missing`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString(ExportWorker.FILE_URI, "content://test/file.xlsx")
            .build()
        every { mockWorkerParams.inputData } returns inputData

        worker = ExportWorker(
            mockContext,
            mockWorkerParams,
            mockContentResolver,
            mockBackupRepo,
            mockExcelBackup
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Failure)
    }

    @Test
    fun `doWork should return failure when ContentResolver cannot open output stream`() = runTest {
        // Given
        val fileUri = "content://test/file.xlsx"
        val fileName = "test.xlsx"
        val inputData = Data.Builder()
            .putString(ExportWorker.FILE_URI, fileUri)
            .putString(ExportWorker.FILE_NAME, fileName)
            .build()
        
        every { mockWorkerParams.inputData } returns inputData
        every { mockContentResolver.openOutputStream(any()) } returns null

        worker = ExportWorker(
            mockContext,
            mockWorkerParams,
            mockContentResolver,
            mockBackupRepo,
            mockExcelBackup
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Failure)
    }

    @Test
    fun `doWork should return success when export completes successfully`() = runTest {
        // Given
        val fileUri = "content://test/file.xlsx"
        val fileName = "test.xlsx"
        val exportCount = 200
        val workerId = java.util.UUID.randomUUID()
        val inputData = Data.Builder()
            .putString(ExportWorker.FILE_URI, fileUri)
            .putString(ExportWorker.FILE_NAME, fileName)
            .build()
        
        val mockResult = mockk<Result<Int>>()
        every { mockResult.getDataOrError() } returns exportCount
        
        val mockBackupEnt = mockk<BackupEnt>()
        every { mockBackupEnt.isCompleted = true }
        every { mockBackupEnt.itemTotal = exportCount }
        
        every { mockWorkerParams.inputData } returns inputData
        every { mockWorkerParams.id } returns workerId
        every { mockContentResolver.openOutputStream(any()) } returns mockOutputStream
        every { mockExcelBackup.export(any()) } returns mockResult
        coEvery { mockBackupRepo.insertBackup(any()) } returns Unit
        coEvery { mockBackupRepo.getBackupByWorkerID(any()) } returns flowOf(mockBackupEnt)
        coEvery { mockBackupRepo.updateBackup(any()) } returns Unit

        worker = ExportWorker(
            mockContext,
            mockWorkerParams,
            mockContentResolver,
            mockBackupRepo,
            mockExcelBackup
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Success)
        
        verify { 
            mockContentResolver.openOutputStream(Uri.parse(fileUri))
            mockExcelBackup.export(mockOutputStream)
        }
        
        coVerify { 
            mockBackupRepo.insertBackup(any())
            mockBackupRepo.getBackupByWorkerID(workerId.toString())
            mockBackupRepo.updateBackup(any())
        }
    }

    @Test
    fun `doWork should create backup entity with correct data`() = runTest {
        // Given
        val fileUri = "content://test/file.xlsx"
        val fileName = "test.xlsx"
        val workerId = java.util.UUID.randomUUID()
        val inputData = Data.Builder()
            .putString(ExportWorker.FILE_URI, fileUri)
            .putString(ExportWorker.FILE_NAME, fileName)
            .build()
        
        val mockResult = mockk<Result<Int>>()
        every { mockResult.getDataOrError() } returns 100
        
        val mockBackupEnt = mockk<BackupEnt>()
        every { mockBackupEnt.isCompleted = true }
        every { mockBackupEnt.itemTotal = 100 }
        
        every { mockWorkerParams.inputData } returns inputData
        every { mockWorkerParams.id } returns workerId
        every { mockContentResolver.openOutputStream(any()) } returns mockOutputStream
        every { mockExcelBackup.export(any()) } returns mockResult
        coEvery { mockBackupRepo.insertBackup(any()) } returns Unit
        coEvery { mockBackupRepo.getBackupByWorkerID(any()) } returns flowOf(mockBackupEnt)
        coEvery { mockBackupRepo.updateBackup(any()) } returns Unit

        worker = ExportWorker(
            mockContext,
            mockWorkerParams,
            mockContentResolver,
            mockBackupRepo,
            mockExcelBackup
        )

        // When
        worker.doWork()

        // Then
        coVerify { 
            mockBackupRepo.insertBackup(match {
                it.name == fileName &&
                it.filePath == fileUri &&
                it.workerId == workerId.toString() &&
                !it.isCompleted &&
                it.itemTotal == 0
            })
        }
    }
}