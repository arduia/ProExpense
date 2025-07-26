package com.arduia.expense.data

import android.content.Context
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.testing.WorkManagerTestInitHelper
import com.arduia.expense.data.network.FeedbackDto
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FeedbackWorkerTest {

    private lateinit var worker: FeedbackWorker
    private lateinit var mockContext: Context
    private lateinit var mockWorkerParams: WorkerParameters
    private lateinit var mockServerRepo: ProExpenseServerRepository
    private lateinit var mockRepo: ExpenseRepository

    @Before
    fun setup() {
        mockContext = mockk()
        mockWorkerParams = mockk()
        mockServerRepo = mockk()
        mockRepo = mockk()

        // Setup default mocks
        every { mockWorkerParams.inputData } returns Data.EMPTY
        
        // Initialize WorkManager for testing
        WorkManagerTestInitHelper.initializeTestWorkManager(mockContext)
    }

    @Test
    fun `doWork should return failure when comment is empty`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString(FeedbackWorker.PARAM_NAME, "John Doe")
            .putString(FeedbackWorker.PARAM_EMAIL, "john@example.com")
            .putString(FeedbackWorker.PARAM_COMMENT, "")
            .build()
        every { mockWorkerParams.inputData } returns inputData

        worker = FeedbackWorker(
            mockContext,
            mockWorkerParams,
            mockServerRepo,
            mockRepo
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Failure)
    }

    @Test
    fun `doWork should return failure when comment is missing`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString(FeedbackWorker.PARAM_NAME, "John Doe")
            .putString(FeedbackWorker.PARAM_EMAIL, "john@example.com")
            .build()
        every { mockWorkerParams.inputData } returns inputData

        worker = FeedbackWorker(
            mockContext,
            mockWorkerParams,
            mockServerRepo,
            mockRepo
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Failure)
    }

    @Test
    fun `doWork should return success when feedback is sent successfully`() = runTest {
        // Given
        val name = "John Doe"
        val email = "john@example.com"
        val comment = "Great app!"
        val inputData = Data.Builder()
            .putString(FeedbackWorker.PARAM_NAME, name)
            .putString(FeedbackWorker.PARAM_EMAIL, email)
            .putString(FeedbackWorker.PARAM_COMMENT, comment)
            .build()
        
        val expectedRequest = FeedbackDto.Request(name, email, comment, "TestKey")
        
        every { mockWorkerParams.inputData } returns inputData
        coEvery { mockServerRepo.postFeedback(any()) } returns flowOf(Unit)

        worker = FeedbackWorker(
            mockContext,
            mockWorkerParams,
            mockServerRepo,
            mockRepo
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Success)
        
        coVerify { 
            mockServerRepo.postFeedback(match {
                it.name == name &&
                it.email == email &&
                it.comment == comment &&
                it.key == "TestKey"
            })
        }
    }

    @Test
    fun `doWork should handle empty name and email gracefully`() = runTest {
        // Given
        val comment = "Great app!"
        val inputData = Data.Builder()
            .putString(FeedbackWorker.PARAM_COMMENT, comment)
            .build()
        
        every { mockWorkerParams.inputData } returns inputData
        coEvery { mockServerRepo.postFeedback(any()) } returns flowOf(Unit)

        worker = FeedbackWorker(
            mockContext,
            mockWorkerParams,
            mockServerRepo,
            mockRepo
        )

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is androidx.work.ListenableWorker.Result.Success)
        
        coVerify { 
            mockServerRepo.postFeedback(match {
                it.name == "" &&
                it.email == "" &&
                it.comment == comment &&
                it.key == "TestKey"
            })
        }
    }

    @Test
    fun `doWork should handle server repository exceptions gracefully`() = runTest {
        // Given
        val comment = "Great app!"
        val inputData = Data.Builder()
            .putString(FeedbackWorker.PARAM_COMMENT, comment)
            .build()
        
        every { mockWorkerParams.inputData } returns inputData
        coEvery { mockServerRepo.postFeedback(any()) } throws RuntimeException("Network error")

        worker = FeedbackWorker(
            mockContext,
            mockWorkerParams,
            mockServerRepo,
            mockRepo
        )

        // When & Then
        assertThrows(RuntimeException::class.java) {
            runTest { worker.doWork() }
        }
    }

    @Test
    fun `getFeedbackRequest should return null when comment is empty`() {
        // Given
        val inputData = Data.Builder()
            .putString(FeedbackWorker.PARAM_COMMENT, "")
            .build()
        every { mockWorkerParams.inputData } returns inputData

        worker = FeedbackWorker(
            mockContext,
            mockWorkerParams,
            mockServerRepo,
            mockRepo
        )

        // When
        val request = worker.getFeedbackRequest()

        // Then
        assertNull(request)
    }

    @Test
    fun `getFeedbackRequest should return correct request when all parameters are provided`() {
        // Given
        val name = "John Doe"
        val email = "john@example.com"
        val comment = "Great app!"
        val inputData = Data.Builder()
            .putString(FeedbackWorker.PARAM_NAME, name)
            .putString(FeedbackWorker.PARAM_EMAIL, email)
            .putString(FeedbackWorker.PARAM_COMMENT, comment)
            .build()
        every { mockWorkerParams.inputData } returns inputData

        worker = FeedbackWorker(
            mockContext,
            mockWorkerParams,
            mockServerRepo,
            mockRepo
        )

        // When
        val request = worker.getFeedbackRequest()

        // Then
        assertNotNull(request)
        assertEquals(name, request!!.name)
        assertEquals(email, request.email)
        assertEquals(comment, request.comment)
        assertEquals("TestKey", request.key)
    }
}