# Worker Test Implementation Guide

## Quick Start Implementation

This guide provides practical examples and code snippets for implementing comprehensive tests for the worker classes.

## 1. Test Setup and Dependencies

### 1.1 Required Dependencies

Add these to your `app/build.gradle.kts`:

```kotlin
dependencies {
    // Existing dependencies...
    
    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    testImplementation("org.robolectric:robolectric:4.10.3")
    testImplementation("androidx.work:work-testing:2.8.0")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test:runner:1.5.2")
    testImplementation("androidx.test:rules:1.5.0")
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("app.cash.turbine:turbine:1.0.0")
}
```

### 1.2 Test Utilities Setup

Create `app/src/test/java/com/arduia/expense/data/utils/WorkerTestUtils.kt`:

```kotlin
package com.arduia.expense.data.utils

import android.content.Context
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.testing.WorkManagerTestInitHelper
import io.mockk.mockk
import org.junit.Before
import org.robolectric.RuntimeEnvironment

object WorkerTestUtils {
    
    fun createMockContext(): Context = RuntimeEnvironment.getApplication()
    
    fun createMockWorkerParams(inputData: Data = Data.EMPTY): WorkerParameters {
        return mockk<WorkerParameters> {
            every { inputData } returns inputData
            every { taskExecutor } returns mockk()
            every { backgroundExecutor } returns mockk()
        }
    }
    
    fun setupWorkManagerTest() {
        WorkManagerTestInitHelper.initializeTestWorkManager(
            RuntimeEnvironment.getApplication()
        )
    }
    
    fun createTestData(vararg pairs: Pair<String, Any>): Data {
        val builder = Data.Builder()
        pairs.forEach { (key, value) ->
            when (value) {
                is String -> builder.putString(key, value)
                is Int -> builder.putInt(key, value)
                is Boolean -> builder.putBoolean(key, value)
                is Long -> builder.putLong(key, value)
                is Float -> builder.putFloat(key, value)
                is Double -> builder.putDouble(key, value)
                is ByteArray -> builder.putByteArray(key, value)
            }
        }
        return builder.build()
    }
}
```

## 2. ImportWorker Test Implementation

### 2.1 Complete Test Class

```kotlin
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
import com.arduia.expense.data.utils.WorkerTestUtils.createMockContext
import com.arduia.expense.data.utils.WorkerTestUtils.createMockWorkerParams
import com.arduia.expense.data.utils.WorkerTestUtils.createTestData
import com.arduia.expense.data.utils.WorkerTestUtils.setupWorkManagerTest
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
    private lateinit var mockExcelBackup: ExcelBackup
    private lateinit var mockContentResolver: ContentResolver

    @Before
    fun setup() {
        setupWorkManagerTest()
        
        mockContext = createMockContext()
        mockExcelBackup = mockk()
        mockContentResolver = mockk()
        
        // Mock context to return content resolver
        every { mockContext.contentResolver } returns mockContentResolver
        
        // Create worker with mocked dependencies
        worker = ImportWorker(mockContext, mockWorkerParams)
    }

    @Test
    fun `should import valid Excel file successfully`() = runTest {
        // Given
        val fileUri = "content://test/excel_file.xlsx"
        val inputData = createTestData("FILE_URI" to fileUri)
        val mockInputStream = ByteArrayInputStream("test data".toByteArray())
        val expectedImportCount = 5
        
        mockWorkerParams = createMockWorkerParams(inputData)
        
        every { mockContentResolver.openInputStream(any()) } returns mockInputStream
        coEvery { mockExcelBackup.import(any()) } returns expectedImportCount
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.SUCCESS, result)
        verify { mockContentResolver.openInputStream(any()) }
        coVerify { mockExcelBackup.import(any()) }
    }

    @Test
    fun `should fail when file URI is invalid`() = runTest {
        // Given
        val invalidUri = "invalid_uri"
        val inputData = createTestData("FILE_URI" to invalidUri)
        
        mockWorkerParams = createMockWorkerParams(inputData)
        
        every { mockContentResolver.openInputStream(any()) } throws IllegalArgumentException("Invalid URI")
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.FAILURE, result)
        verify { mockContentResolver.openInputStream(any()) }
    }

    @Test
    fun `should fail when file is corrupted`() = runTest {
        // Given
        val fileUri = "content://test/corrupted_file.xlsx"
        val inputData = createTestData("FILE_URI" to fileUri)
        val mockInputStream = ByteArrayInputStream("corrupted data".toByteArray())
        
        mockWorkerParams = createMockWorkerParams(inputData)
        
        every { mockContentResolver.openInputStream(any()) } returns mockInputStream
        coEvery { mockExcelBackup.import(any()) } throws BackupException("File is corrupted")
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.FAILURE, result)
        verify { mockContentResolver.openInputStream(any()) }
        coVerify { mockExcelBackup.import(any()) }
    }

    @Test
    fun `should handle empty Excel file gracefully`() = runTest {
        // Given
        val fileUri = "content://test/empty_file.xlsx"
        val inputData = createTestData("FILE_URI" to fileUri)
        val mockInputStream = ByteArrayInputStream(ByteArray(0))
        
        mockWorkerParams = createMockWorkerParams(inputData)
        
        every { mockContentResolver.openInputStream(any()) } returns mockInputStream
        coEvery { mockExcelBackup.import(any()) } returns 0
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.SUCCESS, result)
        verify { mockContentResolver.openInputStream(any()) }
        coVerify { mockExcelBackup.import(any()) }
    }

    @Test
    fun `should fail when storage permission is denied`() = runTest {
        // Given
        val fileUri = "content://test/restricted_file.xlsx"
        val inputData = createTestData("FILE_URI" to fileUri)
        
        mockWorkerParams = createMockWorkerParams(inputData)
        
        every { mockContentResolver.openInputStream(any()) } throws SecurityException("Permission denied")
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.FAILURE, result)
        verify { mockContentResolver.openInputStream(any()) }
    }
}
```

## 3. ExportWorker Test Implementation

### 3.1 Complete Test Class

```kotlin
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
import com.arduia.expense.data.utils.WorkerTestUtils.createMockContext
import com.arduia.expense.data.utils.WorkerTestUtils.createMockWorkerParams
import com.arduia.expense.data.utils.WorkerTestUtils.createTestData
import com.arduia.expense.data.utils.WorkerTestUtils.setupWorkManagerTest
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
    private lateinit var mockExcelBackup: ExcelBackup
    private lateinit var mockBackupRepository: BackupRepository
    private lateinit var mockContentResolver: ContentResolver

    @Before
    fun setup() {
        setupWorkManagerTest()
        
        mockContext = createMockContext()
        mockExcelBackup = mockk()
        mockBackupRepository = mockk()
        mockContentResolver = mockk()
        
        every { mockContext.contentResolver } returns mockContentResolver
        
        worker = ExportWorker(mockContext, mockWorkerParams)
    }

    @Test
    fun `should export data to Excel file successfully`() = runTest {
        // Given
        val fileUri = "content://test/export_file.xlsx"
        val inputData = createTestData("FILE_URI" to fileUri)
        val mockOutputStream = ByteArrayOutputStream()
        val testBackupData = listOf(
            BackupEnt(id = 1, name = "Test Backup 1", date = Date()),
            BackupEnt(id = 2, name = "Test Backup 2", date = Date())
        )
        
        mockWorkerParams = createMockWorkerParams(inputData)
        
        every { mockContentResolver.openOutputStream(any()) } returns mockOutputStream
        coEvery { mockBackupRepository.getAllBackupData() } returns flowOf(testBackupData)
        coEvery { mockExcelBackup.export(any(), any()) } returns Unit
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.SUCCESS, result)
        verify { mockContentResolver.openOutputStream(any()) }
        coVerify { mockBackupRepository.getAllBackupData() }
        coVerify { mockExcelBackup.export(any(), any()) }
    }

    @Test
    fun `should fail when storage is full`() = runTest {
        // Given
        val fileUri = "content://test/export_file.xlsx"
        val inputData = createTestData("FILE_URI" to fileUri)
        
        mockWorkerParams = createMockWorkerParams(inputData)
        
        every { mockContentResolver.openOutputStream(any()) } throws OutOfMemoryError("Storage full")
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.FAILURE, result)
        verify { mockContentResolver.openOutputStream(any()) }
    }

    @Test
    fun `should export empty dataset gracefully`() = runTest {
        // Given
        val fileUri = "content://test/empty_export.xlsx"
        val inputData = createTestData("FILE_URI" to fileUri)
        val mockOutputStream = ByteArrayOutputStream()
        
        mockWorkerParams = createMockWorkerParams(inputData)
        
        every { mockContentResolver.openOutputStream(any()) } returns mockOutputStream
        coEvery { mockBackupRepository.getAllBackupData() } returns flowOf(emptyList())
        coEvery { mockExcelBackup.export(any(), any()) } returns Unit
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.SUCCESS, result)
        verify { mockContentResolver.openOutputStream(any()) }
        coVerify { mockBackupRepository.getAllBackupData() }
        coVerify { mockExcelBackup.export(any(), any()) }
    }
}
```

## 4. FeedbackWorker Test Implementation

### 4.1 Complete Test Class

```kotlin
package com.arduia.expense.data

import android.content.Context
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.testing.WorkManagerTestInitHelper
import com.arduia.expense.data.network.FeedbackDto
import com.arduia.expense.data.utils.WorkerTestUtils.createMockContext
import com.arduia.expense.data.utils.WorkerTestUtils.createMockWorkerParams
import com.arduia.expense.data.utils.WorkerTestUtils.createTestData
import com.arduia.expense.data.utils.WorkerTestUtils.setupWorkManagerTest
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
        setupWorkManagerTest()
        
        mockContext = createMockContext()
        mockServerRepo = mockk()
        mockRepo = mockk()
        
        worker = FeedbackWorker(mockContext, mockWorkerParams)
    }

    @Test
    fun `should send feedback successfully`() = runTest {
        // Given
        val feedbackMessage = "Great app! Love the new features."
        val feedbackData = FeedbackDto(message = feedbackMessage)
        val inputData = createTestData("FEEDBACK_MESSAGE" to feedbackMessage)
        
        mockWorkerParams = createMockWorkerParams(inputData)
        
        coEvery { mockRepo.getFeedbackData() } returns flowOf(feedbackData)
        coEvery { mockServerRepo.sendFeedback(any()) } returns Unit
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.SUCCESS, result)
        coVerify { mockRepo.getFeedbackData() }
        coVerify { mockServerRepo.sendFeedback(any()) }
    }

    @Test
    fun `should fail when network is unavailable`() = runTest {
        // Given
        val feedbackMessage = "Test feedback"
        val inputData = createTestData("FEEDBACK_MESSAGE" to feedbackMessage)
        
        mockWorkerParams = createMockWorkerParams(inputData)
        
        coEvery { mockRepo.getFeedbackData() } returns flowOf(FeedbackDto(message = feedbackMessage))
        coEvery { mockServerRepo.sendFeedback(any()) } throws Exception("Network unavailable")
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.FAILURE, result)
        coVerify { mockRepo.getFeedbackData() }
        coVerify { mockServerRepo.sendFeedback(any()) }
    }

    @Test
    fun `should handle very long feedback messages`() = runTest {
        // Given
        val longMessage = "A".repeat(10000) // Very long message
        val inputData = createTestData("FEEDBACK_MESSAGE" to longMessage)
        
        mockWorkerParams = createMockWorkerParams(inputData)
        
        coEvery { mockRepo.getFeedbackData() } returns flowOf(FeedbackDto(message = longMessage))
        coEvery { mockServerRepo.sendFeedback(any()) } returns Unit
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.SUCCESS, result)
        coVerify { mockRepo.getFeedbackData() }
        coVerify { mockServerRepo.sendFeedback(any()) }
    }
}
```

## 5. CheckAboutUpdateWorker Test Implementation

### 5.1 Complete Test Class

```kotlin
package com.arduia.expense.data.update

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.testing.WorkManagerTestInitHelper
import com.arduia.expense.data.ProExpenseServerRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.ext.getAppVersionCode
import com.arduia.expense.data.local.UpdateStatusDataModel
import com.arduia.expense.data.network.CheckUpdateDto
import com.arduia.expense.data.utils.WorkerTestUtils.createMockContext
import com.arduia.expense.data.utils.WorkerTestUtils.createMockWorkerParams
import com.arduia.expense.data.utils.WorkerTestUtils.setupWorkManagerTest
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
    private lateinit var mockServerRepo: ProExpenseServerRepository
    private lateinit var mockSettingsRepo: SettingsRepository

    @Before
    fun setup() {
        setupWorkManagerTest()
        
        mockContext = createMockContext()
        mockServerRepo = mockk()
        mockSettingsRepo = mockk()
        
        worker = CheckAboutUpdateWorker(mockContext, mockWorkerParams)
    }

    @Test
    fun `should check for updates successfully`() = runTest {
        // Given
        val currentVersion = 100
        val updateData = CheckUpdateDto(
            hasUpdate = true,
            versionCode = 101,
            versionName = "1.0.1",
            downloadUrl = "https://example.com/app.apk"
        )
        
        every { mockContext.getAppVersionCode() } returns currentVersion
        coEvery { mockServerRepo.checkForUpdate() } returns updateData
        coEvery { mockSettingsRepo.updateUpdateStatus(any()) } returns Unit
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.SUCCESS, result)
        verify { mockContext.getAppVersionCode() }
        coVerify { mockServerRepo.checkForUpdate() }
        coVerify { mockSettingsRepo.updateUpdateStatus(any()) }
    }

    @Test
    fun `should handle no updates available`() = runTest {
        // Given
        val currentVersion = 100
        val updateData = CheckUpdateDto(
            hasUpdate = false,
            versionCode = 100,
            versionName = "1.0.0",
            downloadUrl = null
        )
        
        every { mockContext.getAppVersionCode() } returns currentVersion
        coEvery { mockServerRepo.checkForUpdate() } returns updateData
        coEvery { mockSettingsRepo.updateUpdateStatus(any()) } returns Unit
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.SUCCESS, result)
        verify { mockContext.getAppVersionCode() }
        coVerify { mockServerRepo.checkForUpdate() }
        coVerify { mockSettingsRepo.updateUpdateStatus(any()) }
    }

    @Test
    fun `should fail when network is unavailable`() = runTest {
        // Given
        val currentVersion = 100
        
        every { mockContext.getAppVersionCode() } returns currentVersion
        coEvery { mockServerRepo.checkForUpdate() } throws Exception("Network unavailable")
        
        // When
        val result = worker.doWork()
        
        // Then
        assertEquals(Result.FAILURE, result)
        verify { mockContext.getAppVersionCode() }
        coVerify { mockServerRepo.checkForUpdate() }
    }
}
```

## 6. Integration Test Implementation

### 6.1 WorkManager Integration Test

```kotlin
package com.arduia.expense.data.integration

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

@RunWith(AndroidJUnit4::class)
class WorkerIntegrationTest {

    private lateinit var workManager: WorkManager
    private lateinit var testDriver: TestDriver

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        
        workManager = WorkManager.getInstance(context)
        testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!
    }

    @Test
    fun `should execute import worker successfully`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString("FILE_URI", "content://test/file.xlsx")
            .build()
        
        val worker = TestListenableWorkerBuilder<ImportWorker>(ApplicationProvider.getApplicationContext())
            .setInputData(inputData)
            .build()
        
        // When
        val result = worker.startWork().get()
        
        // Then
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `should handle work constraints properly`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString("FILE_URI", "content://test/file.xlsx")
            .build()
        
        val worker = TestListenableWorkerBuilder<ImportWorker>(ApplicationProvider.getApplicationContext())
            .setInputData(inputData)
            .build()
        
        // When
        testDriver.setAllConstraintsMet(false)
        val result = worker.startWork().get()
        
        // Then
        assertEquals(ListenableWorker.Result.retry(), result)
    }
}
```

## 7. Performance Test Implementation

### 7.1 Performance Test Example

```kotlin
package com.arduia.expense.data.performance

import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.data.backup.ImportWorker
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
class WorkerPerformanceTest {

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
    }

    @Test
    fun `should complete import within reasonable time`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString("FILE_URI", "content://test/large_file.xlsx")
            .build()
        
        val worker = TestListenableWorkerBuilder<ImportWorker>(ApplicationProvider.getApplicationContext())
            .setInputData(inputData)
            .build()
        
        // When
        val executionTime = measureTimeMillis {
            worker.startWork().get()
        }
        
        // Then
        assertTrue("Import should complete within 5 seconds", executionTime < 5000)
    }
}
```

## 8. Running the Tests

### 8.1 Command Line

```bash
# Run all worker tests
./gradlew test --tests "*WorkerTest"

# Run specific worker tests
./gradlew test --tests "*ImportWorkerTest"
./gradlew test --tests "*ExportWorkerTest"
./gradlew test --tests "*FeedbackWorkerTest"
./gradlew test --tests "*CheckAboutUpdateWorkerTest"

# Run integration tests
./gradlew test --tests "*WorkerIntegrationTest"

# Run performance tests
./gradlew test --tests "*WorkerPerformanceTest"

# Run with coverage
./gradlew testDebugUnitTestCoverage
```

### 8.2 IDE Configuration

In Android Studio:
1. Right-click on test file → "Run Test"
2. Right-click on test method → "Run Test"
3. Use "Run with Coverage" for coverage reports

## 9. Test Data Management

### 9.1 Test Data Factory

```kotlin
package com.arduia.expense.data.utils

import android.net.Uri
import com.arduia.expense.data.network.FeedbackDto
import com.arduia.expense.data.network.CheckUpdateDto
import java.util.*

object TestDataFactory {
    
    fun createValidExcelFile(): Uri = Uri.parse("content://test/valid_file.xlsx")
    
    fun createCorruptedExcelFile(): Uri = Uri.parse("content://test/corrupted_file.xlsx")
    
    fun createLargeExcelFile(): Uri = Uri.parse("content://test/large_file.xlsx")
    
    fun createFeedbackData(message: String = "Test feedback"): FeedbackDto {
        return FeedbackDto(message = message)
    }
    
    fun createUpdateCheckData(
        hasUpdate: Boolean = true,
        versionCode: Int = 101
    ): CheckUpdateDto {
        return CheckUpdateDto(
            hasUpdate = hasUpdate,
            versionCode = versionCode,
            versionName = "1.0.1",
            downloadUrl = if (hasUpdate) "https://example.com/app.apk" else null
        )
    }
    
    fun createLargeString(size: Int): String {
        return "A".repeat(size)
    }
}
```

This implementation guide provides practical, ready-to-use test code that follows Android testing best practices and integrates seamlessly with your existing codebase.