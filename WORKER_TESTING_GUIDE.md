# Worker Classes Unit Testing Guide

## Overview

This guide provides comprehensive strategies for testing the 4 worker classes in the expense tracking application:

1. **ImportWorker** - Imports expense data from Excel files
2. **ExportWorker** - Exports expense data to Excel files  
3. **FeedbackWorker** - Sends user feedback to server
4. **CheckAboutUpdateWorker** - Checks for app updates

## Testing Strategy

### 1. Unit Testing Approach

Each worker class follows these testing principles:

- **Isolation**: Mock all dependencies (repositories, services, Android components)
- **Coverage**: Test success paths, failure scenarios, and edge cases
- **Verification**: Verify correct method calls and data transformations
- **Error Handling**: Test exception scenarios and graceful degradation

### 2. Testing Framework Stack

```kotlin
// Core testing dependencies
testImplementation("junit:junit:4.13.1")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("io.mockk:mockk-android:1.13.8")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
testImplementation("androidx.arch.core:core-testing:2.1.0")
testImplementation("org.robolectric:robolectric:4.10.3")

// WorkManager testing
androidTestImplementation("androidx.work:work-testing:2.8.0")
```

## Test Structure

### Basic Test Class Template

```kotlin
class WorkerTest {
    
    @Before
    fun setup() {
        // Initialize mocks
        mockContext = mockk()
        mockWorkerParams = mockk()
        mockDependency = mockk()
        
        // Setup default behaviors
        every { mockWorkerParams.inputData } returns Data.EMPTY
    }
    
    @Test
    fun `test success scenario`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString("KEY", "value")
            .build()
        every { mockWorkerParams.inputData } returns inputData
        
        // When
        val result = worker.doWork()
        
        // Then
        assertTrue(result is ListenableWorker.Result.Success)
        coVerify { mockDependency.methodCall() }
    }
}
```

## Individual Worker Test Patterns

### 1. ImportWorker Testing

**Key Test Scenarios:**
- Missing FILE_URI parameter
- ContentResolver cannot open input stream
- Successful import with count returned
- BackupException handling
- General exception handling

**Mock Dependencies:**
- `ContentResolver` - For file access
- `ExcelBackup` - For import operations

**Verification Points:**
- Correct file URI parsing
- Import count in output data
- Exception handling and failure responses

### 2. ExportWorker Testing

**Key Test Scenarios:**
- Missing FILE_URI or FILE_NAME parameters
- ContentResolver cannot open output stream
- Successful export with backup logging
- Repository exception handling
- Excel backup exception handling

**Mock Dependencies:**
- `ContentResolver` - For file access
- `BackupRepository` - For backup logging
- `ExcelBackup` - For export operations

**Verification Points:**
- Backup entity creation and updates
- Export count tracking
- Worker ID management

### 3. FeedbackWorker Testing

**Key Test Scenarios:**
- Missing or empty comment parameter
- Successful feedback submission
- Empty email/name handling
- Server repository exception handling

**Mock Dependencies:**
- `ProExpenseServerRepository` - For network calls
- `ExpenseRepository` - For local data

**Verification Points:**
- FeedbackDto.Request construction
- Network call execution
- Parameter validation

### 4. CheckAboutUpdateWorker Testing

**Key Test Scenarios:**
- No update available
- Normal update with info
- Critical update with info
- Update available but no info
- Server repository exceptions
- Settings repository exceptions
- Version code retrieval exceptions

**Mock Dependencies:**
- `SettingsRepository` - For local settings
- `ProExpenseServerRepository` - For update checks

**Verification Points:**
- Update status setting
- Update info storage
- Version code retrieval

## Integration Testing

### WorkManager Integration Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class WorkerIntegrationTest {
    
    @Before
    fun setup() {
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        workManager = WorkManager.getInstance(context)
    }
    
    @Test
    fun `test worker chaining`() = runTest {
        // Test export -> import workflow
        val exportResult = exportWorker.doWork()
        val importResult = importWorker.doWork()
        
        assertTrue(exportResult is Result.Success)
        assertTrue(importResult is Result.Success)
    }
}
```

### Test Data Builders

```kotlin
object TestDataBuilder {
    
    fun createImportInputData(fileUri: String) = Data.Builder()
        .putString(ImportWorker.FILE_URI, fileUri)
        .build()
    
    fun createExportInputData(fileUri: String, fileName: String) = Data.Builder()
        .putString(ExportWorker.FILE_URI, fileUri)
        .putString(ExportWorker.FILE_NAME, fileName)
        .build()
    
    fun createFeedbackInputData(email: String, name: String, comment: String) = Data.Builder()
        .putString(FeedbackWorker.PARAM_EMAIL, email)
        .putString(FeedbackWorker.PARAM_NAME, name)
        .putString(FeedbackWorker.PARAM_COMMENT, comment)
        .build()
}
```

## Best Practices

### 1. Test Naming Convention

Use descriptive test names that explain the scenario:

```kotlin
@Test
fun `doWork should return success when import completes with valid data`() = runTest {
    // Test implementation
}

@Test
fun `doWork should return failure when required parameter is missing`() = runTest {
    // Test implementation
}
```

### 2. Mock Setup Patterns

```kotlin
// Setup mocks with realistic behavior
every { mockContext.contentResolver } returns mockContentResolver
every { mockWorkerParams.inputData } returns inputData
every { mockWorkerParams.id } returns UUID.randomUUID()

// Use coEvery for suspend functions
coEvery { mockRepository.method() } returns expectedValue
coEvery { mockRepository.method() } throws Exception("Error message")
```

### 3. Verification Patterns

```kotlin
// Verify method calls with specific parameters
coVerify { 
    mockRepository.insertBackup(match { 
        it.name == expectedName && it.isCompleted == false 
    })
}

// Verify no unexpected calls
verify(exactly = 1) { mockRepository.method() }
verify(exactly = 0) { mockRepository.shouldNotBeCalled() }
```

### 4. Exception Testing

```kotlin
@Test
fun `should handle specific exception type`() = runTest {
    // Given
    coEvery { mockService.method() } throws BackupException("Import failed")
    
    // When
    val result = worker.doWork()
    
    // Then
    assertTrue(result is ListenableWorker.Result.Failure)
}
```

## Running Tests

### Unit Tests
```bash
./gradlew test
```

### Android Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Specific Test Class
```bash
./gradlew test --tests ImportWorkerTest
```

## Coverage Goals

Aim for the following coverage targets:

- **Line Coverage**: 90%+
- **Branch Coverage**: 85%+
- **Function Coverage**: 95%+

### Coverage Report
```bash
./gradlew testDebugUnitTestCoverage
```

## Continuous Integration

### GitHub Actions Example

```yaml
name: Worker Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Run Unit Tests
        run: ./gradlew test
      - name: Run Android Tests
        run: ./gradlew connectedAndroidTest
      - name: Generate Coverage Report
        run: ./gradlew testDebugUnitTestCoverage
```

## Troubleshooting

### Common Issues

1. **MockK Verification Failures**
   - Ensure using `coVerify` for suspend functions
   - Check parameter matching with `match` or `any()`

2. **Coroutine Testing Issues**
   - Use `runTest` for suspend function testing
   - Ensure proper test dispatcher setup

3. **WorkManager Testing Issues**
   - Initialize WorkManager with `WorkManagerTestInitHelper`
   - Use `TestListenableWorkerBuilder` for worker creation

### Debug Tips

```kotlin
// Enable MockK debugging
mockkStatic(Log::class)
every { Log.d(any(), any()) } answers { println("MockK: ${secondArg<String>()}") }

// Use slot to capture arguments
val capturedData = slot<Data>()
coVerify { mockRepository.method(capture(capturedData)) }
println("Captured data: ${capturedData.captured}")
```

## Future Enhancements

1. **Property-Based Testing**: Use property-based testing for edge cases
2. **Performance Testing**: Add performance benchmarks for worker operations
3. **Stress Testing**: Test workers under high load conditions
4. **Memory Leak Testing**: Ensure workers don't cause memory leaks
5. **Network Simulation**: Test network failure scenarios

## Conclusion

This testing approach ensures:
- **Reliability**: Workers handle all scenarios gracefully
- **Maintainability**: Tests serve as documentation
- **Confidence**: Safe refactoring with comprehensive test coverage
- **Quality**: Early detection of regressions and issues

Follow these patterns consistently across all worker classes to maintain high code quality and reliability.