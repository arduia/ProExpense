# Dependency Injection with Koin - KMP Best Practices

## Overview

ProExpense uses **Koin** - a lightweight service locator and DI framework that works seamlessly across KMP platforms. Koin is the standard choice for KMP projects due to its simplicity and platform independence.

---

## Why Koin for KMP?

✅ **Platform Independent**: Works on iOS, Android, and Web  
✅ **Lightweight**: No code generation, minimal runtime overhead  
✅ **Familiar Syntax**: Easy to learn and understand  
✅ **Type Safe**: Compile-time safety with Kotlin's type system  
✅ **Lazy Loading**: Deferred instantiation of dependencies  
✅ **Shared Modules**: Define DI graph once, use across platforms  

---

## Core Concepts

### 1. Scoping: Controlling Lifetime

**Single** - Single instance for entire app lifetime
```kotlin
single {  // Only ONE instance exists
    ExpenseDatabase(db = get())  // get() resolves dependencies
}
```

**Factory** - New instance each time
```kotlin
factory {
    ExpenseMapper()  // Fresh instance on each call
}
```

### 2. Modules: DI Graph Organization

Define your dependency graph in modules:

```kotlin
// shared/di/src/commonMain/kotlin/AppModule.kt
val sharedModule = module {
    // Single instances for app lifetime
    single { ExpenseDatabase() }
    single { HttpClient { /* config */ } }
    single { ExpenseRepository(get()) }  // get() injects dependency
    
    // Factory instances (new each time)
    factory { ExpenseMapper() }
    factory { HomeViewModel(get(), get()) }  // Multiple dependencies
}
```

### 3. Platform-Specific Modules

Define platform-specific dependencies in their modules:

```kotlin
// shared/di/src/androidMain/kotlin/AndroidModule.kt
val androidModule = module {
    single<FileManager> { AndroidFileManager(context) }
}

// shared/di/src/iosMain/kotlin/IosModule.kt
val iosModule = module {
    single<FileManager> { IosFileManager() }
}
```

---

## KMP Module Organization

ProExpense organizes DI into **feature-scoped modules** in the shared module:

**Location:** `shared/di/src/commonMain/kotlin/`

### Key Modules:

| Module | Purpose | Scope |
|--------|---------|-------|
| `DatabaseModule` | SQLDelight database, queries | Singleton |
| `RepositoryModule` | All repository bindings | Singleton |
| `NetworkModule` | Ktor HTTP client, API services | Singleton |
| `MapperModule` | Model transformation (DTO → ViewModel) | Factory |
| `FormatterModule` | Date/number formatters | Factory |
| `ViewModelModule` | All ViewModels | Factory |
| `CacheModule` | In-memory cache layer | Singleton |

### Platform-Specific Modules:

| Module | Platform | Purpose |
|--------|----------|---------|
| `AndroidModule` | Android only | Android-specific implementations |
| `IosModule` | iOS only | iOS-specific implementations |
| `WebModule` | Web only | Web-specific implementations |

---

## DatabaseModule Example (KMP)

**File:** `shared/di/src/commonMain/kotlin/DatabaseModule.kt`

```kotlin
val databaseModule = module {
    // Shared SQLDelight database (available on all platforms)
    single {
        ExpenseDatabase(
            driver = get()  // Platform-specific SQLite driver
        )
    }
    
    // Access queries directly
    single {
        get<ExpenseDatabase>().expenseQueries
    }
    
    single {
        get<ExpenseDatabase>().settingsQueries
    }
}

// Platform-specific driver setup
val androidDatabaseModule = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = ExpenseDatabase.Schema,
            context = androidContext(),
            name = "expense.db"
        )
    }
}

val iosDatabaseModule = module {
    single<SqlDriver> {
        NativeSqliteDriver(
            schema = ExpenseDatabase.Schema,
            name = "expense.db"
        )
    }
}
```

---

## ViewModel Injection (KMP Compose)

**The Core Pattern:**

```kotlin
// Shared ViewModel (available on all platforms)
class HomeViewModel(
    private val expenseRepository: ExpenseRepository,
    private val currencyRepository: CurrencyRepository,
    private val expenseMapper: ExpenseUiModelMapper
) {
    // Constructor injection - all parameters from Koin
    // No framework-specific annotations needed
}

val viewModelModule = module {
    factory {
        HomeViewModel(
            expenseRepository = get(),
            currencyRepository = get(),
            expenseMapper = get()
        )
    }
}
```

**Benefits:**
- Pure Kotlin, no annotations needed
- Same ViewModel works on iOS, Android, Web
- Koin resolves dependencies automatically
- Factory scope creates new instance per use

**In Compose Screen:**
```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    // Koin provides the instance, with all dependencies resolved
    val uiState by viewModel.uiState.collectAsState()
    // Use state...
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

## Service Injection (Shared Services)

**Setup for shared services across platforms:**

```kotlin
// Shared service (works on all platforms)
class BackupService(
    private val expenseRepository: ExpenseRepository,
    private val fileManager: FileManager
) {
    suspend fun importExpenses(uri: String): Result<List<Expense>> {
        return try {
            val expenses = fileManager.readExpensesFromFile(uri)
            expenseRepository.insertExpenseAll(expenses)
            Result.Success(expenses)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

val serviceModule = module {
    single {
        BackupService(
            expenseRepository = get(),
            fileManager = get()
        )
    }
}
```

**Key Benefits:**
- Pure Kotlin, no framework-specific annotations
- Same service runs on iOS, Android, Web
- Easy to test with mock dependencies

---

## KMP Multi-Module Architecture

ProExpense is organized as a KMP project with shared and platform-specific modules:

```
shared/                           # Shared across all platforms
├── domain/                        # Domain models, entities, repositories (interfaces)
├── data/                          # Data sources, repository implementations
├── viewmodel/                     # ViewModels, UI state
├── di/
│   ├── commonMain/               # Shared DI modules
│   ├── androidMain/              # Android-specific DI
│   └── iosMain/                  # iOS-specific DI
└── utils/                        # Shared utilities

androidApp/                       # Android-specific
├── main/
│   └── ui/                       # Compose screens (shared composables)
└── build.gradle.kts

iosApp/                          # iOS-specific
└── iosApp/
    └── UI/                       # Compose screens (shared composables)

webApp/                          # Web-specific
└── src/
    └── ui/                       # Compose screens (shared composables)
```

**Each platform initializes Koin with all modules:**

```kotlin
// Platform-agnostic initialization
fun startKoin() {
    startKoin {
        modules(
            databaseModule,
            repositoryModule,
            networkModule,
            mapperModule,
            viewModelModule,
            formatterModule
        )
    }
}

// Platform-specific modules added per platform
// Android
startKoin {
    modules(androidModule, androidDatabaseModule)
}

// iOS
startKoin {
    modules(iosModule, iosDatabaseModule)
}
```

---

## Testing with Koin

### Unit Tests (Mock Repository)

```kotlin
class HomeViewModelTest {
    
    @RelaxedMockK
    private lateinit var expenseRepository: ExpenseRepository
    
    private lateinit var viewModel: HomeViewModel
    
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = HomeViewModel(
            expenseRepository = expenseRepository
        )
    }
    
    @Test
    fun testLoadExpenses() = runTest {
        coEvery { 
            expenseRepository.getRecentExpense() 
        } returns flowOf(Result.Success(mockExpenseList))
        
        viewModel.loadExpenses()
        
        coVerify { expenseRepository.getRecentExpense() }
    }
}
```

### Shared Tests (No Platform-Specific Setup)

```kotlin
class ExpenseRepositoryTest {
    
    private lateinit var expenseRepository: ExpenseRepository
    
    @Before
    fun setUp() {
        // Use in-memory database for testing
        val testModule = module {
            single {
                InMemoryExpenseDatabase()
            }
            single { ExpenseRepositoryImpl(get()) }
        }
        
        startKoin { modules(testModule) }
        expenseRepository = get()
    }
    
    @Test
    fun testInsertAndRetrieveExpense() = runTest {
        val expense = Expense(
            id = 1,
            name = "Test Expense",
            amount = Amount.createFromStore(100),
            category = 1,
            note = null,
            createdDate = Clock.System.now().toEpochMilliseconds(),
            modifiedDate = Clock.System.now().toEpochMilliseconds()
        )
        
        expenseRepository.insertExpense(expense)
        val retrieved = expenseRepository.getExpenseAll().first()
        
        assert(retrieved.isNotEmpty())
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
