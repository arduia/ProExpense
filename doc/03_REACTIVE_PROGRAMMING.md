# Reactive Programming with Coroutines & Flow - ProExpense Best Practices

## Overview

ProExpense uses **Kotlin Coroutines + Flow** for asynchronous operations. This eliminates callback hell, makes async code look synchronous, and enables reactive data streams.

---

## Why Coroutines + Flow?

✅ **Readable**: Async code reads like synchronous code  
✅ **Non-Blocking**: Suspends instead of blocking threads  
✅ **Structured Concurrency**: Automatic cleanup with scopes  
✅ **Reactive**: Flow enables hot/cold observable streams  
✅ **Lifecycle-Aware**: viewModelScope cancels on ViewModel destruction  

---

## Core Concepts

### 1. Suspend Functions

A function that can be paused and resumed, returning a value:

```kotlin
// Regular function - blocks thread
fun fetchExpenses(): List<ExpenseEnt> {
    // Thread is blocked while waiting for DB
}

// Suspend function - pauses, doesn't block
suspend fun fetchExpenses(): List<ExpenseEnt> {
    // Can be paused without blocking thread
    return expenseDao.getExpenseAll().first()
}
```

**In Repositories:**
```kotlin
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {
    
    suspend fun insertExpense(expenseEnt: ExpenseEnt) {
        withContext(Dispatchers.IO) {  // Switch to IO thread
            expenseDao.insert(expenseEnt)  // Suspend here
        }
    }
}
```

### 2. Flow: Reactive Data Streams

Flow emits multiple values over time, with backpressure handling:

```kotlin
// Returns a single value
suspend fun getSingleExpense(): ExpenseEnt

// Returns a stream of values (reactive)
fun getAllExpenses(): Flow<List<ExpenseEnt>>
```

**In DAOs:**
```kotlin
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY created_date DESC")
    fun getExpenseAll(): Flow<List<ExpenseEnt>>
    // Emits new list every time data changes
}
```

### 3. FlowResult: Combining Flow + Result Type

Wraps Flow with Result type for type-safe error handling:

```kotlin
// Type alias for Flow<Result<T>>
typealias FlowResult<T> = Flow<Result<T>>

// Combine Flow + Result for complete async handling
fun getExpenseAll(): FlowResult<List<ExpenseEnt>> {
    return expenseDao.getExpenseAll()
        .map { SuccessResult(it) }           // Wrap in Success
        .catch { ErrorResult(RepositoryException(it)) }  // Catch as Error
        .flowOn(Dispatchers.IO)             // Execute on IO thread
}
```

---

## Repository Pattern with Coroutines

**File:** `/app/src/main/java/com/arduia/expense/data/ExpenseRepositoryImpl.kt`

### Suspend Functions (One-shot operations)

```kotlin
suspend fun insertExpense(expenseEnt: ExpenseEnt) {
    withContext(Dispatchers.IO) {
        expenseDao.insert(expenseEnt)
    }
}

suspend fun updateExpense(expenseEnt: ExpenseEnt) {
    withContext(Dispatchers.IO) {
        expenseDao.update(expenseEnt)
    }
}

suspend fun deleteExpense(expenseEnt: ExpenseEnt) {
    withContext(Dispatchers.IO) {
        expenseDao.delete(expenseEnt)
    }
}
```

### Flow Functions (Streaming data)

```kotlin
fun getExpenseAll(): FlowResult<List<ExpenseEnt>> {
    return expenseDao.getExpenseAll()
        .map { list ->
            SuccessResult(list)  // Wrap each emission
        }
        .catch { exception ->
            emit(ErrorResult(RepositoryException(exception)))
        }
        .flowOn(Dispatchers.IO)  // Execute on IO thread
}

fun getExpenseRange(
    startTime: Long,
    endTime: Long,
    offset: Int,
    limit: Int
): FlowResult<List<ExpenseEnt>> {
    return expenseDao.getExpenseRangeDesc(startTime, endTime, offset, limit)
        .map { SuccessResult(it) }
        .catch { ErrorResult(RepositoryException(it)) }
        .flowOn(Dispatchers.IO)
}
```

---

## ViewModel Coroutine Patterns

**File:** `/app/src/main/java/com/arduia/expense/ui/home/HomeViewModel.kt`

### Launch Coroutine in ViewModel

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val expenseMapper: ExpenseUiModelMapper
) : ViewModel() {
    
    private val _uiState = MutableLiveData<HomeUiState>()
    val uiState: LiveData<HomeUiState> = _uiState
    
    fun loadExpenses() {
        viewModelScope.launch {  // Automatically cancelled on ViewModel destruction
            try {
                _uiState.value = HomeUiState.Loading
                
                // Collect first emission from Flow
                expenseRepository.getExpenseAll()
                    .onSuccess { expenses ->
                        _uiState.value = HomeUiState.Success(
                            expenses.map { expenseMapper.map(it) }
                        )
                    }
                    .onError { error ->
                        _uiState.value = HomeUiState.Error(error.message)
                    }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message)
            }
        }
    }
}
```

**Key Points:**
- `viewModelScope` automatically cancels jobs when ViewModel is destroyed
- No memory leaks or dangling coroutines
- `launch` for fire-and-forget operations
- `async` for operations that return values

### Collecting Flow in Fragment

```kotlin
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Observe ViewModel's LiveData state
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HomeUiState.Loading -> showLoadingUI()
                is HomeUiState.Success -> showExpenses(state.expenses)
                is HomeUiState.Error -> showError(state.message)
            }
        }
        
        viewModel.loadExpenses()
    }
}
```

---

## Error Handling in Flows

**Pattern from Result.kt:** `/app/src/main/java/com/arduia/expense/data/ext/Result.kt`

### Operators for Result Type

```kotlin
inline fun <T> Flow<Result<T>>.onSuccess(
    crossinline block: suspend (T) -> Unit
): Flow<Result<T>> = onEach { result ->
    if (result is Result.Success) {
        block(result.data)
    }
}

inline fun <T> Flow<Result<T>>.onError(
    crossinline block: suspend (Exception) -> Unit
): Flow<Result<T>> = onEach { result ->
    if (result is Result.Error) {
        block(result.exception)
    }
}

inline fun <T> Flow<Result<T>>.onLoading(
    crossinline block: suspend () -> Unit
): Flow<Result<T>> = onEach { result ->
    if (result is Result.Loading) {
        block()
    }
}
```

### Usage Example

```kotlin
expenseRepository.getExpenseAll()
    .onSuccess { expenses ->
        // Handle success case
        updateUI(expenses)
    }
    .onError { error ->
        // Handle error case
        showErrorDialog(error.message)
    }
    .onLoading {
        // Show loading indicator
        showProgressBar()
    }
    .launchIn(viewModelScope)  // Launch in scope, auto-cancelled
```

---

## Dispatcher Management

Coroutines use **Dispatchers** to control which thread a coroutine runs on:

### IO Dispatcher (Database, Network)

```kotlin
suspend fun insertExpense(expenseEnt: ExpenseEnt) {
    withContext(Dispatchers.IO) {  // Switch to IO thread for DB access
        expenseDao.insert(expenseEnt)
    }
}
```

### Main Dispatcher (UI Updates)

```kotlin
viewModelScope.launch(Dispatchers.Main) {  // Default, UI updates happen here
    _uiState.value = HomeUiState.Loading
}
```

### Custom for Flow

```kotlin
fun getExpenseAll(): Flow<List<ExpenseEnt>> {
    return expenseDao.getExpenseAll()
        .flowOn(Dispatchers.IO)  // Emit on IO thread
        .map { ... }
}
```

**Best Practice:** Specify IO for slow operations, let Flow/launch handle switching back to Main

---

## LiveData + Flow Integration

Some code uses **LiveData** (older pattern), modern code uses **Flow**. They work together:

### Converting Flow to LiveData

```kotlin
@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    
    // Expose as LiveData for Fragment observation
    val expenses: LiveData<List<ExpenseEnt>> =
        expenseRepository.getExpenseAll()
            .map { result ->
                when (result) {
                    is Result.Success -> result.data
                    is Result.Error -> emptyList()
                    is Result.Loading -> emptyList()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )
            .asLiveData()  // Convert StateFlow to LiveData
}
```

### Observation in Fragment

```kotlin
class ExpenseFragment : Fragment() {
    private val viewModel: ExpenseViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Observe LiveData
        viewModel.expenses.observe(viewLifecycleOwner) { expenses ->
            updateUI(expenses)
        }
    }
}
```

---

## WorkManager for Background Tasks

Long-running operations use **WorkManager** instead of coroutines:

**File:** `/app/src/main/java/com/arduia/expense/data/backup/ImportWorker.kt`

```kotlin
@HiltWorker
class ImportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val expenseRepository: ExpenseRepository,
    private val backupRepository: BackupRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            // Long-running operation (safe to suspend)
            val importUri = inputData.getString("import_uri") ?: return Result.failure()
            val expenses = backupRepository.importExpenses(importUri)
            
            // Use repository to store imported data
            expenseRepository.insertExpenseAll(expenses)
            
            // Notify user of success
            Result.success(
                workDataOf("import_count" to expenses.size)
            )
        } catch (e: Exception) {
            Timber.e(e, "Import failed")
            // Retry automatically
            Result.retry()
        }
    }
}
```

### Scheduling Work

```kotlin
// Schedule one-time work
WorkManager.getInstance(context).enqueueUniqueWork(
    "import_expenses",
    ExistingWorkPolicy.REPLACE,
    OneTimeWorkRequestBuilder<ImportWorker>()
        .setInputData(workDataOf("import_uri" to uri))
        .build()
)

// Periodic work (check for updates)
WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "check_updates",
    ExistingPeriodicWorkPolicy.KEEP,
    PeriodicWorkRequestBuilder<CheckAboutUpdateWorker>(
        Duration.ofHours(12)  // Every 12 hours
    ).build()
)
```

---

## Testing Async Code

### Unit Test with TestDispatchers

```kotlin
class ExpenseRepositoryTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @RelaxedMockK
    private lateinit var expenseDao: ExpenseDao
    
    private lateinit var repository: ExpenseRepository
    
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = ExpenseRepositoryImpl(expenseDao)
    }
    
    @Test
    fun testGetExpenseAll() = runTest {  // Test scope with StandardTestDispatcher
        val mockExpenses = listOf(
            ExpenseEnt(1, "Coffee", Amount.createFromStore(100), 1, null, 0, 0),
            ExpenseEnt(2, "Lunch", Amount.createFromStore(500), 2, null, 0, 0)
        )
        
        coEvery { expenseDao.getExpenseAll() } returns flowOf(mockExpenses)
        
        val result = repository.getExpenseAll().first()
        
        assert(result is Result.Success)
        assert((result as Result.Success).data.size == 2)
    }
}
```

### Instrumented Test with Hilt

```kotlin
@HiltAndroidTest
class ExpenseRepositoryIntegrationTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var repository: ExpenseRepository
    
    @Before
    fun setUp() {
        hiltRule.inject()
    }
    
    @Test
    fun testInsertAndRetrieveExpense() = runTest {
        val expense = ExpenseEnt(
            expenseId = 1,
            name = "Test",
            amount = Amount.createFromStore(100),
            category = 1,
            note = null,
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis()
        )
        
        repository.insertExpense(expense)
        
        val retrieved = repository.getExpenseAll()
            .map { (it as? Result.Success)?.data ?: emptyList() }
            .first()
        
        assert(retrieved.isNotEmpty())
    }
}
```

---

## Best Practices

### ✅ DO:

1. **Use suspend functions** for one-shot operations
   ```kotlin
   suspend fun insertExpense(exp: ExpenseEnt)
   ```

2. **Use Flow** for streaming data
   ```kotlin
   fun getExpenseAll(): Flow<List<ExpenseEnt>>
   ```

3. **Use FlowResult** for type-safe async
   ```kotlin
   fun getExpenseAll(): FlowResult<List<ExpenseEnt>>
   ```

4. **Use viewModelScope** to manage coroutine lifetime
   ```kotlin
   viewModelScope.launch { ... }
   ```

5. **Handle errors in Flows** with `.catch`
   ```kotlin
   flow.catch { emit(ErrorResult(exception)) }
   ```

### ❌ DON'T:

1. **Block threads** with synchronous calls
   ```kotlin
   // BAD
   val result = blockingGetExpense()
   
   // GOOD
   val result = suspendGetExpense()
   ```

2. **Launch coroutines without scope**
   ```kotlin
   // BAD - can leak
   GlobalScope.launch { ... }
   
   // GOOD - auto-cancelled
   viewModelScope.launch { ... }
   ```

3. **Ignore Dispatcher switching**
   ```kotlin
   // BAD - database on Main thread
   fun getExpense() = expenseDao.getExpense()
   
   // GOOD - explicit IO dispatcher
   fun getExpense() = expenseDao.getExpense()
       .flowOn(Dispatchers.IO)
   ```

4. **Mix callbacks with coroutines**
   ```kotlin
   // BAD - old style callback hell
   api.getExpenses { result ->
       onExpensesLoaded(result)
   }
   
   // GOOD - coroutines
   val expenses = api.getExpenses()
   ```

---

## Reuse in New Architecture

✅ **Coroutines + Flow are architecture-agnostic** - work with any UI framework  
✅ **Suspend functions** remain the contract between layers  
✅ **FlowResult pattern** provides type-safe error handling regardless of UI  
✅ **Dispatcher strategy** applies to refactored code without change  
✅ **WorkManager** unchanged for background tasks  

**Key: Keep repositories as the single point of async operations**
