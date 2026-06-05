# Dependency Injection with Hilt - ProExpense Best Practices

## Overview

ProExpense uses **Hilt** - a dependency injection framework built on top of Dagger that dramatically simplifies DI setup for Android. Hilt handles the complex boilerplate of creating and managing component hierarchies.

---

## Why Hilt?

✅ **Less Boilerplate**: Automatic component creation vs manual Dagger setup  
✅ **Lifecycle-Aware**: Built-in Android scopes (singleton, activity, fragment, etc.)  
✅ **Easy Testing**: `@HiltAndroidTest` for instrumented tests  
✅ **Type Safety**: Compile-time graph validation  
✅ **Constructor Injection**: Just add `@Inject` to constructors  

---

## Core Concepts

### 1. Scoping: Controlling Lifetime

**@Singleton** - Single instance for entire app lifetime
```kotlin
@Singleton  // Only ONE instance exists
class ProExpenseDatabase @Inject constructor(context: Context) {
    // Created once, reused everywhere
}
```

**@ActivityComponent** - New instance per Activity
```kotlin
@ActivityComponent
class ActivityViewModel @Inject constructor() {
    // Fresh instance for each activity
}
```

### 2. Provides: Factory Methods

When constructor injection isn't possible, use `@Provides`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(context: Context): ProExpenseDatabase {
        // Custom creation logic if needed
        return ProExpenseDatabase.getInstance(context)
    }
}
```

### 3. Binds: Interface to Implementation

Map interfaces to their implementations:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractRepoModule {
    
    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        impl: ExpenseRepositoryImpl
    ): ExpenseRepository
    
    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        impl: CurrencyRepositoryImpl
    ): CurrencyRepository
}
```

---

## Module Organization

ProExpense organizes DI into **feature-scoped modules** - one module per logical feature:

**Location:** `/app/src/main/java/com/arduia/expense/di/`

### Key Modules:

| Module | Purpose | Scope |
|--------|---------|-------|
| `DatabaseModule.kt` | Room database, DAOs, InvalidationTracker | Singleton |
| `AbstractRepoModule.kt` | All repository bindings | Singleton |
| `RepositoryModule.kt` | Context utilities, CacheDao | Singleton |
| `NetworkModule.kt` | Retrofit, HTTP clients, API services | Singleton |
| `AbstractMapperModule.kt` | UI mappers (DTO → ViewModel) | Factory |
| `AbstractDomainModule.kt` | Domain use cases, validators | Factory |
| `BackgroundModule.kt` | WorkManager | Singleton |
| `BackupModule.kt` | Backup/restore logic builders | Factory |
| `FormatModule.kt` | Date/number formatters | Factory |
| `NavHostModule.kt` | Navigation setup | Activity |
| `AdapterModule.kt` | RecyclerView adapters | Factory |
| `AnimationModule.kt` | Animation utilities | Factory |

---

## DatabaseModule Example

**File:** `/app/src/main/java/com/arduia/expense/di/DatabaseModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideProExpenseDatabase(context: Context): ProExpenseDatabase {
        return ProExpenseDatabase.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideExpenseDao(database: ProExpenseDatabase): ExpenseDao {
        return database.expenseDao()
    }
    
    @Provides
    @Singleton
    fun provideCurrencyDao(database: ProExpenseDatabase): CurrencyDao {
        return database.currencyDao()
    }
    
    @Provides
    @Singleton
    fun provideBackupDao(database: ProExpenseDatabase): BackupDao {
        return database.backupDao()
    }
    
    @Provides
    @Singleton
    fun provideInvalidationTracker(
        database: ProExpenseDatabase
    ): InvalidationTracker {
        return database.invalidationTracker
    }
}
```

---

## ViewModel Injection

**The Core Pattern:**

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val currencyRepository: CurrencyRepository,
    private val expenseMapper: ExpenseUiModelMapper
) : ViewModel() {
    // Hilt automatically injects all constructor parameters
    // Based on DI configuration in modules
}
```

**Benefits:**
- `@HiltViewModel` tells Hilt this is a ViewModel
- `@Inject constructor` on primary constructor
- All parameters are automatically resolved from the DI graph
- Fragment/Activity gets it via `by viewModels()` or `by activityViewModels()`

**In Fragment:**
```kotlin
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    // Hilt provides the instance, with all dependencies resolved
}
```

---

## Repository Injection Pattern

**Interface Definition:**
```kotlin
interface ExpenseRepository {
    fun getExpenseAll(): FlowResult<List<ExpenseEnt>>
    suspend fun insertExpense(expenseEnt: ExpenseEnt)
    // ... more methods
}
```

**Implementation:**
```kotlin
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {
    // Constructor injection - DAO automatically provided
    override fun getExpenseAll(): FlowResult<List<ExpenseEnt>> {
        // Use the injected DAO
        return expenseDao.getExpenseAll()
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }
    }
}
```

**Binding in Module:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractRepoModule {
    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        impl: ExpenseRepositoryImpl
    ): ExpenseRepository
    // Now ExpenseRepository resolves to ExpenseRepositoryImpl
}
```

**Usage in ViewModel:**
```kotlin
@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository  // Automatically injected!
) : ViewModel() {
    // No need to manually create ExpenseRepositoryImpl or ExpenseDao
}
```

---

## Mapper Injection

Mappers transform data from one form to another (domain → UI model).

**Mapper Pattern:**
```kotlin
class ExpenseUiModelMapper @Inject constructor(
    private val categoryProvider: ExpenseCategoryProvider,
    private val dateFormatter: ExpenseDateFormatter
) : Mapper<ExpenseEnt, ExpenseUiModel> {
    
    override fun map(input: ExpenseEnt): ExpenseUiModel {
        return ExpenseUiModel(
            id = input.expenseId,
            name = input.name,
            amount = input.amount.getActualAsFloat(),
            category = categoryProvider.getCategoryNameByID(input.category),
            date = dateFormatter.format(input.createdDate),
            note = input.note
        )
    }
}
```

**Binding Multiple Mappers:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractMapperModule {
    
    @Binds
    abstract fun bindExpenseUiModelMapper(
        mapper: ExpenseUiModelMapper
    ): Mapper<ExpenseEnt, ExpenseUiModel>
    
    @Binds
    abstract fun bindBackupUiModelMapper(
        mapper: BackupUiModelMapper
    ): Mapper<BackupEnt, BackupUiModel>
}
```

---

## Worker Injection (Background Tasks)

**Setup:**
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
            // Use injected repositories
            val expenses = backupRepository.importExpenses()
            expenseRepository.insertExpenseAll(expenses)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
```

**Key Differences:**
- `@HiltWorker` instead of `@HiltViewModel`
- `@AssistedInject` for mixed assisted and regular injection
- `@Assisted` for context and parameters

---

## Multi-Module Architecture

ProExpense has **sub-modules** with their own DI:

```
app/
├── currency-store/        (Currency exchange logic)
├── backup/                (Excel export/import)
├── expense-backup/        (Expense-specific backup)
├── week-expense-graph/    (Custom chart view)
└── shared/                (Common utilities)
```

**Each module declares its own Hilt components:**
```kotlin
// In currency-store module
@Module
@InstallIn(SingletonComponent::class)
object CurrencyStoreModule {
    // ...
}

// In backup module
@Module
@InstallIn(SingletonComponent::class)
object BackupModule {
    // ...
}
```

**App module depends on all sub-modules:**
```gradle
dependencies {
    implementation project(':currency-store')
    implementation project(':backup')
    implementation project(':expense-backup')
    implementation project(':week-expense-graph')
    implementation project(':shared')
}
```

---

## Testing with Hilt

### Unit Tests (Mock Repository)

```kotlin
class HomeViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @RelaxedMockK
    private lateinit var expenseRepository: ExpenseRepository
    
    private lateinit var viewModel: HomeViewModel
    
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = HomeViewModel(
            expenseRepository = expenseRepository,
            // ... other mocks
        )
    }
    
    @Test
    fun testLoadExpenses() {
        coEvery { 
            expenseRepository.getExpenseAll() 
        } returns flowOf(SuccessResult(mockExpenseList))
        
        viewModel.loadExpenses()
        
        coVerify { expenseRepository.getExpenseAll() }
    }
}
```

### Instrumented Tests (@HiltAndroidTest)

```kotlin
@HiltAndroidTest
class ExpenseRepositoryTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var expenseRepository: ExpenseRepository
    
    @Before
    fun setUp() {
        hiltRule.inject()  // Inject real repository
    }
    
    @Test
    fun testInsertAndRetrieveExpense() {
        val expense = ExpenseEnt(
            expenseId = 1,
            name = "Test Expense",
            amount = Amount.createFromStore(100),
            category = 1,
            note = null,
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis()
        )
        
        runTest {
            expenseRepository.insertExpense(expense)
            val retrieved = expenseRepository.getExpenseAll().first()
            
            assert(retrieved.isNotEmpty())
        }
    }
}
```

---

## Best Practices

### ✅ DO:

1. **Use constructor injection** whenever possible
   ```kotlin
   class MyClass @Inject constructor(dep1: Dep1, dep2: Dep2)
   ```

2. **Define interfaces** for abstraction
   ```kotlin
   interface ExpenseRepository { ... }
   class ExpenseRepositoryImpl @Inject constructor(...) : ExpenseRepository
   ```

3. **Use appropriate scopes**
   - Singleton: Database, repositories, network clients
   - Factory: Mappers, formatters, utilities
   - Activity/Fragment: UI-specific components

4. **Organize modules by feature**
   - One module per logical feature
   - Group related bindings together

5. **Test with mocks** for unit tests
   ```kotlin
   val mockRepository = mockk<ExpenseRepository>()
   ```

### ❌ DON'T:

1. **Service Locator pattern** (anti-pattern)
   ```kotlin
   // BAD - Don't do this
   val repo = ServiceLocator.getExpenseRepository()
   ```

2. **Create objects manually**
   ```kotlin
   // BAD
   val dao = database.expenseDao()
   val repo = ExpenseRepositoryImpl(dao)
   
   // GOOD
   @Inject lateinit var repo: ExpenseRepository
   ```

3. **Mix scopes incorrectly**
   ```kotlin
   // BAD - Singleton depending on activity scope
   @Provides
   @Singleton
   fun provide(activityComponent: ActivityComponent) { ... }
   ```

4. **Circular dependencies**
   ```kotlin
   // BAD - A needs B, B needs A
   class A @Inject constructor(b: B)
   class B @Inject constructor(a: A)
   ```

---

## Troubleshooting

| Problem | Cause | Solution |
|---------|-------|----------|
| "Cannot find binding for..." | Missing `@Provides`, `@Binds`, or `@Inject` | Add the missing annotation |
| Circular dependency | Classes depend on each other | Restructure to remove cycle |
| Wrong scope | Singleton depends on activity scope | Change scope or restructure |
| Tests fail with Hilt | Forgot `@HiltAndroidTest` or `hiltRule.inject()` | Add annotations and setup |

---

## Reuse in New Architecture

✅ **Hilt works with any UI framework** - Compose, XML fragments, or hybrid  
✅ **Module organization** remains valid regardless of architecture  
✅ **Scoping strategy** applies to both current and new design  
✅ **Testing patterns** transfer directly to refactored code  

**Key: Keep repositories as the sole DI entry point for data access**
