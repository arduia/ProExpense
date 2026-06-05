# Testing Guidelines - ProExpense Best Practices

## Overview

ProExpense has **60+ tests** covering unit tests, integration tests, and instrumented tests. Testing is built-in from the start, not an afterthought.

---

## Testing Pyramid

```
        🧪 E2E / UI Tests (Small)
       /                         \
      /   Instrumented Tests      \
     /          (Medium)           \
    /                               \
   /___   Unit Tests (Large)   _____\
  
Strategy: Many unit tests, fewer integration tests, minimal UI tests
```

---

## Unit Tests

### Test Dependencies

```gradle
testImplementation "junit:junit:4.13.2"
testImplementation "io.mockk:mockk:1.13.8"
testImplementation "org.mockito:mockito-core:4.11.0"
testImplementation "androidx.arch.core:core-testing:2.2.0"  // InstantTaskExecutorRule
testImplementation "org.robolectric:robolectric:4.10.3"
testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4"
```

### ViewModel Testing

**File:** `/app/src/test/java/com/arduia/expense/ui/MainViewModelTest.kt`

```kotlin
class HomeViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()  // LiveData testing
    
    @RelaxedMockK
    private lateinit var expenseRepository: ExpenseRepository
    
    @RelaxedMockK
    private lateinit var currencyRepository: CurrencyRepository
    
    @RelaxedMockK
    private lateinit var expenseMapper: ExpenseUiModelMapper
    
    private lateinit var viewModel: HomeViewModel
    
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = HomeViewModel(
            expenseRepository,
            currencyRepository,
            expenseMapper
        )
    }
    
    @Test
    fun testLoadExpensesSuccess() {
        // Arrange: Setup mock to return success
        val mockExpenses = listOf(
            ExpenseEnt(1, "Coffee", Amount.createFromStore(100), 1, null, 0, 0),
            ExpenseEnt(2, "Lunch", Amount.createFromStore(500), 2, null, 0, 0)
        )
        coEvery { expenseRepository.getRecentExpense() } returns 
            flowOf(Result.Success(mockExpenses))
        
        val mockUiModels = listOf(
            ExpenseUiModel(1, "Coffee", "Drinks", 1.0f, "Today", null),
            ExpenseUiModel(2, "Lunch", "Food", 5.0f, "Today", null)
        )
        coEvery { expenseMapper.map(any()) } returnsMany mockUiModels
        
        // Act
        viewModel.loadExpenses()
        
        // Assert
        val state = viewModel.uiState.value
        assert(state is HomeUiState.Success)
        assert((state as HomeUiState.Success).expenses.size == 2)
    }
    
    @Test
    fun testLoadExpensesError() {
        // Arrange
        coEvery { expenseRepository.getRecentExpense() } returns 
            flowOf(Result.Error(Exception("Network error")))
        
        // Act
        viewModel.loadExpenses()
        
        // Assert
        val state = viewModel.uiState.value
        assert(state is HomeUiState.Error)
    }
    
    @Test
    fun testDeleteExpense() {
        // Arrange
        coEvery { expenseRepository.deleteExpense(any()) } returns Unit
        coEvery { expenseRepository.getRecentExpense() } returns 
            flowOf(Result.Success(emptyList()))
        
        // Act
        viewModel.deleteExpense(mockExpenseUiModel)
        
        // Assert
        coVerify { expenseRepository.deleteExpense(any()) }
    }
}
```

### Repository Testing

**File:** `/app/src/test/java/com/arduia/expense/data/ExpenseRepositoryTest.kt`

```kotlin
@RunWith(RobolectricTestRunner::class)
class ExpenseRepositoryTest {
    
    @RelaxedMockK
    private lateinit var expenseDao: ExpenseDao
    
    private lateinit var repository: ExpenseRepository
    
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = ExpenseRepositoryImpl(expenseDao)
    }
    
    @Test
    fun testGetExpenseAll() = runTest {
        // Arrange
        val mockExpenses = listOf(
            ExpenseEnt(1, "Coffee", Amount.createFromStore(100), 1, null, 0, 0)
        )
        coEvery { expenseDao.getExpenseAll() } returns flowOf(mockExpenses)
        
        // Act
        val result = repository.getExpenseAll().first()
        
        // Assert
        assert(result is Result.Success)
        assert((result as Result.Success).data.size == 1)
    }
    
    @Test
    fun testInsertExpense() = runTest {
        // Arrange
        val expense = ExpenseEnt(
            expenseId = 1,
            name = "Test",
            amount = Amount.createFromStore(100),
            category = 1,
            note = null,
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis()
        )
        coEvery { expenseDao.insert(any()) } returns Unit
        
        // Act
        repository.insertExpense(expense)
        
        // Assert
        coVerify { expenseDao.insert(expense) }
    }
}
```

### Coroutine Testing

```kotlin
@Test
fun testAsyncOperation() = runTest {
    // runTest provides StandardTestDispatcher
    // Suspending functions run synchronously in test
    
    val result = suspendingFunction()
    assert(result == expected)
    
    // Can use advanceUntilIdle() to process pending work
    advanceUntilIdle()
}
```

---

## Instrumented Tests

### Setup with Hilt

**Dependencies:**
```gradle
androidTestImplementation "com.google.dagger:hilt-android-testing:2.57"
kspAndroidTest "com.google.dagger:hilt-compiler:2.57"
androidTestImplementation "androidx.test:runner:1.5.2"
androidTestImplementation "androidx.test.espresso:espresso-core:3.5.1"
```

### Database Integration Test

**File:** `/app/src/androidTest/java/com/arduia/expense/data/ExpenseRepositoryIntegrationTest.kt`

```kotlin
@HiltAndroidTest
class ExpenseRepositoryIntegrationTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var database: ProExpenseDatabase
    
    @Inject
    lateinit var repository: ExpenseRepository
    
    @Before
    fun setUp() {
        hiltRule.inject()  // Must call before using injected fields
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun testInsertAndRetrieveExpense() = runTest {
        // Arrange
        val expense = ExpenseEnt(
            expenseId = 1,
            name = "Test Expense",
            amount = Amount.createFromStore(100),
            category = 1,
            note = null,
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis()
        )
        
        // Act
        repository.insertExpense(expense)
        
        // Assert
        val retrieved = repository.getExpenseAll()
            .map { (it as? Result.Success)?.data ?: emptyList() }
            .first()
        
        assert(retrieved.isNotEmpty())
        assert(retrieved[0].name == "Test Expense")
    }
    
    @Test
    fun testUpdateExpense() = runTest {
        // Arrange
        val original = ExpenseEnt(...)
        repository.insertExpense(original)
        
        val updated = original.copy(name = "Updated")
        
        // Act
        repository.updateExpense(updated)
        
        // Assert
        val result = repository.getExpenseById(1)
        assert(result?.name == "Updated")
    }
}
```

### Fragment Testing

```kotlin
@HiltAndroidTest
class HomeFragmentTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule
    val fragmentRule = launchFragmentInContainer<HomeFragment>()
    
    @Test
    fun testFragmentLaunchesSuccessfully() {
        // Fragment launches and renders without crashing
    }
    
    @Test
    fun testExpenseDisplayed() {
        onView(withId(R.id.expenseList)).check(matches(isDisplayed()))
    }
}
```

---

## Mocking Strategies

### MockK Syntax

```kotlin
// Relaxed mocks - default return values
@RelaxedMockK
private lateinit var repository: ExpenseRepository

// Setup return value
coEvery { repository.getExpenseAll() } returns flowOf(Result.Success(list))

// Verify call was made
coVerify { repository.getExpenseAll() }

// Verify never called
coVerify(inverse = true) { repository.deleteExpense(any()) }

// Capture arguments
val slot = slot<ExpenseEnt>()
coEvery { repository.insertExpense(capture(slot)) } returns Unit
repository.insertExpense(expense)
assert(slot.captured.name == "Coffee")
```

### Common Patterns

```kotlin
// Return different values on successive calls
coEvery { repository.getExpenseAll() } returnsMany listOf(
    flowOf(Result.Loading),
    flowOf(Result.Success(listOf(expense)))
)

// Throw exception
coEvery { repository.getExpenseAll() } throws Exception("Network error")

// Any matcher
coEvery { repository.insertExpense(any()) } returns Unit

// Lambda verifier
coVerify { repository.getExpenseAll() }

// Times verification
coVerify(exactly = 1) { repository.getExpenseAll() }
```

---

## Testing Coroutines & Flow

### FlowResult Testing

```kotlin
@Test
fun testFlowResultSuccess() = runTest {
    // Arrange
    coEvery { dao.getExpenseAll() } returns flowOf(listOf(expense))
    
    // Act
    val result = repository.getExpenseAll().first()
    
    // Assert
    assert(result is Result.Success)
    assert((result as Result.Success).data.size == 1)
}

@Test
fun testFlowResultError() = runTest {
    // Arrange
    coEvery { dao.getExpenseAll() } throws Exception("DB error")
    
    // Act & Assert
    val result = repository.getExpenseAll().first()
    assert(result is Result.Error)
}
```

---

## Test Organization

```
src/
├── test/java/
│   └── com/arduia/expense/
│       ├── ui/
│       │   ├── MainViewModelTest.kt
│       │   ├── HomeViewModelTest.kt
│       │   └── ...
│       ├── data/
│       │   ├── ExpenseRepositoryTest.kt
│       │   └── ...
│       └── domain/
│           ├── AmountTest.kt
│           └── ...
└── androidTest/java/
    └── com/arduia/expense/
        ├── data/
        │   └── ExpenseRepositoryIntegrationTest.kt
        └── ui/
            └── HomeFragmentTest.kt
```

---

## Coverage Goals

| Layer | Coverage Target | Example |
|-------|-----------------|---------|
| **Repository** | 80%+ | Success, error, edge cases |
| **ViewModel** | 70%+ | UI state transitions |
| **Domain** | 90%+ | Value objects, validation |
| **Mapper** | 100% | All mappings tested |
| **Fragment** | 50%+ | Navigation, UI updates |

---

## Best Practices

### ✅ DO:

1. **Use runTest {}** for coroutine tests
   ```kotlin
   @Test
   fun testAsync() = runTest { ... }
   ```

2. **Mock external dependencies**
   ```kotlin
   @RelaxedMockK
   private lateinit var repository: ExpenseRepository
   ```

3. **Test error cases**
   ```kotlin
   coEvery { repo.get() } throws Exception()
   ```

4. **Use InstantTaskExecutorRule** for LiveData
   ```kotlin
   @get:Rule
   val instantExecutorRule = InstantTaskExecutorRule()
   ```

5. **Test one thing per test** (AAA pattern)
   ```
   Arrange: Setup mocks
   Act: Call function
   Assert: Verify result
   ```

### ❌ DON'T:

1. **Test implementation details**
   ```kotlin
   // BAD - testing how, not what
   coVerify { dao.getAll() }
   
   // GOOD - testing behavior
   assert(result.isEmpty())
   ```

2. **Use real databases** in unit tests
   ```kotlin
   // BAD
   val db = ProExpenseDatabase.getInstance(context)
   
   // GOOD
   Room.inMemoryDatabaseBuilder(...).build()
   ```

3. **Ignore test failures**
   - Flaky tests indicate design problems
   - Fix the root cause, not the symptom

4. **Test without mocking** external services
   ```kotlin
   // BAD - Network call in test
   val api = Retrofit.create(ExpenseApi::class.java)
   
   // GOOD - Mock it
   @RelaxedMockK
   private lateinit var api: ExpenseApi
   ```

---

## Reuse in New Architecture

✅ **Testing patterns** apply regardless of UI framework  
✅ **Mock strategies** remain valid for new code  
✅ **Repository testing** works with any data source  
✅ **ViewModel testing** applies to Compose too  
✅ **Coroutine testing** pattern `runTest {}` is standard

**Key: Test behavior, not implementation**
