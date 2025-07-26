package com.arduia.expense.data.update

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.testing.WorkManagerTestInitHelper
import com.arduia.expense.data.ProExpenseServerRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.ext.getAppVersionCode
import com.arduia.expense.data.local.UpdateStatusDataModel
import com.arduia.expense.data.network.CheckUpdateDto
import com.arduia.expense.model.Result
import com.arduia.expense.model.getDataOrError
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class CheckAboutUpdateWorkerTest {

    private lateinit var worker: CheckAboutUpdateWorker
    private lateinit var mockContext: Context
    private lateinit var mockWorkerParams: WorkerParameters
    private lateinit var mockSettingRepo: SettingsRepository
    private lateinit var mockServerRepository: ProExpenseServerRepository

    @Before
    fun setup() {
        mockContext = mockk()
        mockWorkerParams = mockk()
        mockSettingRepo = mockk()
        mockServerRepository = mockk()

        // Setup default mocks
        every { mockContext.getAppVersionCode() } returns 100
        
        // Initialize WorkManager for testing
        WorkManagerTestInitHelper.initializeTestWorkManager(mockContext)
    }

    @Test
    fun `doWork should return success when no update is needed`() = runTest {
        // Given
        val versionCode = 100
        val mockResponse = mockk<CheckUpdateDto.Response>()
        every { mockResponse.isShouldUpdate } returns false
        
        val mockResult = mockk<Result<CheckUpdateDto.Response>>()
        every { mockResult.getDataOrError() } returns mockResponse
        
        every { mockContext.getAppVersionCode() } returns versionCode
        coEvery { mockServerRepository.getAboutUpdateSync(any()) } returns mockResult
        coEvery { mockSettingRepo.setUpdateStatus(any()) } returns Unit

        worker = CheckAboutUpdateWorker(
            mockContext,
            mockWorkerParams,
            mockSettingRepo,
            mockServerRepository
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Success)
        
        coVerify { 
            mockServerRepository.getAboutUpdateSync(match {
                it.versionCode == versionCode && it.key == "test"
            })
            mockSettingRepo.setUpdateStatus(UpdateStatusDataModel.STATUS_NO_UPDATE)
        }
    }

    @Test
    fun `doWork should return success when normal update is available`() = runTest {
        // Given
        val versionCode = 100
        val updateInfo = "New version available"
        val mockResponse = mockk<CheckUpdateDto.Response>()
        every { mockResponse.isShouldUpdate } returns true
        every { mockResponse.isCriticalUpdate } returns false
        every { mockResponse.info } returns updateInfo
        
        val mockResult = mockk<Result<CheckUpdateDto.Response>>()
        every { mockResult.getDataOrError() } returns mockResponse
        
        every { mockContext.getAppVersionCode() } returns versionCode
        coEvery { mockServerRepository.getAboutUpdateSync(any()) } returns mockResult
        coEvery { mockSettingRepo.setAboutUpdate(any()) } returns Unit
        coEvery { mockSettingRepo.setUpdateStatus(any()) } returns Unit

        worker = CheckAboutUpdateWorker(
            mockContext,
            mockWorkerParams,
            mockSettingRepo,
            mockServerRepository
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Success)
        
        coVerify { 
            mockSettingRepo.setAboutUpdate(updateInfo)
            mockSettingRepo.setUpdateStatus(UpdateStatusDataModel.STATUS_NORMAL_UPDATE)
        }
    }

    @Test
    fun `doWork should return success when critical update is available`() = runTest {
        // Given
        val versionCode = 100
        val updateInfo = "Critical security update"
        val mockResponse = mockk<CheckUpdateDto.Response>()
        every { mockResponse.isShouldUpdate } returns true
        every { mockResponse.isCriticalUpdate } returns true
        every { mockResponse.info } returns updateInfo
        
        val mockResult = mockk<Result<CheckUpdateDto.Response>>()
        every { mockResult.getDataOrError() } returns mockResponse
        
        every { mockContext.getAppVersionCode() } returns versionCode
        coEvery { mockServerRepository.getAboutUpdateSync(any()) } returns mockResult
        coEvery { mockSettingRepo.setAboutUpdate(any()) } returns Unit
        coEvery { mockSettingRepo.setUpdateStatus(any()) } returns Unit

        worker = CheckAboutUpdateWorker(
            mockContext,
            mockWorkerParams,
            mockSettingRepo,
            mockServerRepository
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Success)
        
        coVerify { 
            mockSettingRepo.setAboutUpdate(updateInfo)
            mockSettingRepo.setUpdateStatus(UpdateStatusDataModel.STATUS_CRITICAL_UPDATE)
        }
    }

    @Test
    fun `doWork should return success when update is needed but no info provided`() = runTest {
        // Given
        val versionCode = 100
        val mockResponse = mockk<CheckUpdateDto.Response>()
        every { mockResponse.isShouldUpdate } returns true
        every { mockResponse.isCriticalUpdate } returns false
        every { mockResponse.info } returns null
        
        val mockResult = mockk<Result<CheckUpdateDto.Response>>()
        every { mockResult.getDataOrError() } returns mockResponse
        
        every { mockContext.getAppVersionCode() } returns versionCode
        coEvery { mockServerRepository.getAboutUpdateSync(any()) } returns mockResult
        coEvery { mockSettingRepo.setUpdateStatus(any()) } returns Unit

        worker = CheckAboutUpdateWorker(
            mockContext,
            mockWorkerParams,
            mockSettingRepo,
            mockServerRepository
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Success)
        
        coVerify { 
            mockSettingRepo.setUpdateStatus(UpdateStatusDataModel.STATUS_NO_UPDATE)
        }
        
        coVerify(exactly = 0) { 
            mockSettingRepo.setAboutUpdate(any())
        }
    }

    @Test
    fun `doWork should return failure when exception occurs`() = runTest {
        // Given
        val versionCode = 100
        
        every { mockContext.getAppVersionCode() } returns versionCode
        coEvery { mockServerRepository.getAboutUpdateSync(any()) } throws RuntimeException("Network error")
        coEvery { mockSettingRepo.setUpdateStatus(any()) } returns Unit

        worker = CheckAboutUpdateWorker(
            mockContext,
            mockWorkerParams,
            mockSettingRepo,
            mockServerRepository
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Failure)
        
        coVerify { 
            mockSettingRepo.setUpdateStatus(UpdateStatusDataModel.STATUS_NO_UPDATE)
        }
    }

    @Test
    fun `doWork should handle getAppVersionCode exception gracefully`() = runTest {
        // Given
        every { mockContext.getAppVersionCode() } throws RuntimeException("Version code error")
        coEvery { mockSettingRepo.setUpdateStatus(any()) } returns Unit

        worker = CheckAboutUpdateWorker(
            mockContext,
            mockWorkerParams,
            mockSettingRepo,
            mockServerRepository
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Failure)
        
        coVerify { 
            mockSettingRepo.setUpdateStatus(UpdateStatusDataModel.STATUS_NO_UPDATE)
        }
    }
}