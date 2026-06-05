# Architecture Patterns - ProExpense Best Practices

## Overview

ProExpense follows **MVVM (Model-View-ViewModel) with Repository Pattern**, a proven Android architecture that separates concerns into three layers: UI, business logic, and data access.

---

## 1. MVVM Architecture

### What It Is

MVVM divides the application into three distinct layers:

- **View (UI Layer)**: Fragments, Activities, Compose screens - displays data, handles user interactions
- **ViewModel**: Holds and manages UI state, survives configuration changes, calls repositories
- **Model (Data/Domain Layer)**: Business logic, repositories, domain models - independent of UI framework

### Why Use It

✅ **Testability**: Each layer can be tested independently  
✅ **Reusability**: ViewModels and repositories are reusable across multiple UI components  
✅ **Maintainability**: Clear separation makes code easier to navigate and update  
✅ **Lifecycle-Aware**: ViewModels survive configuration changes (screen rotation)  

### Implementation Example

**ViewModel** - `/app/src/main/java/com/arduia/expense/ui/home/HomeViewModel.kt`
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val currencyRepository: CurrencyRepository,
    private val expenseMapper: ExpenseUiModelMapper
) : ViewModel() {
    
    private val _uiState = MutableLiveData<HomeUiState>()
    val uiState: LiveData<HomeUiState> = _uiState
    
    fun loadExpenses() {
        viewModelScope.launch {
            expenseRepository.getRecentExpense()
                .onSuccess { expenses ->
                    _uiState.value = HomeUiState.Success(
                        expenses.map { expenseMapper.map(it) }
                    )
                }
                .onError { error ->
                    _uiState.value = HomeUiState.Error(error.message)
                }
        }
    }
}
```

**Fragment** - Observes ViewModel state and reacts to changes
```kotlin
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HomeUiState.Success -> updateUI(state.expenses)
                is HomeUiState.Error -> showError(state.message)
                is HomeUiState.Loading -> showLoading()
            }
        }
        
        viewModel.loadExpenses()
    }
}
```

---

## 2. Repository Pattern

### What It Is

Repository provides a **single point of access** to data, abstracting the underlying data sources (Room database, Network API, SharedPreferences).

### Why Use It

✅ **Data Source Abstraction**: UI doesn't know if data comes from DB, network, or cache  
✅ **Testability**: Easy to mock repositories in unit tests  
✅ **Flexibility**: Switch data sources without changing UI code  
✅ **Single Responsibility**: Repository handles all data logic  

### Implementation Example

**Interface** - `/app/src/main/java/com/arduia/expense/data/ExpenseRepository.kt`
```kotlin
interface ExpenseRepository {
    suspend fun insertExpense(expenseEnt: ExpenseEnt)
    fun getExpenseAll(): FlowResult<List<ExpenseEnt>>
    fun getExpenseRange(
        startTime: Long, 
        endTime: Long, 
        offset: Int, 
        limit: Int
    ): FlowResult<List<ExpenseEnt>>
    suspend fun updateExpense(expenseEnt: ExpenseEnt)
    suspend fun deleteExpense(expenseEnt: ExpenseEnt)
}
```

**Implementation** - `/app/src/main/java/com/arduia/expense/data/ExpenseRepositoryImpl.kt`
```kotlin
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {
    
    override suspend fun insertExpense(expenseEnt: ExpenseEnt) {
        withContext(Dispatchers.IO) {
            expenseDao.insert(expenseEnt)
        }
    }
    
    override fun getExpenseAll(): FlowResult<List<ExpenseEnt>> {
        return expenseDao.getExpenseAll()
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }
            .flowOn(Dispatchers.IO)
    }
    
    override fun getExpenseRange(
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
}
```

---

## 3. Result Type Pattern

### What It Is

A **sealed class** that represents three possible states of an async operation:
- `Success<T>`: Operation completed with data
- `Error`: Operation failed with exception
- `Loading`: Operation is in progress

### Why Use It

✅ **Type Safety**: Compiler enforces handling all cases  
✅ **No Exceptions**: Errors are values, not exceptions  
✅ **Explicit State**: Always clear what state you're in  
✅ **Functional**: Works well with reactive streams (Flow)  

### Implementation Example

**Result Type** - `/app/src/main/java/com/arduia/expense/model/Result.kt`
```kotlin
sealed class Result<out R> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

typealias SuccessResult<T> = Result.Success<T>
typealias ErrorResult = Result.Error
typealias LoadingResult = Result.Loading
typealias FlowResult<T> = Flow<Result<T>>
```

**Usage in Repository** - Returns `FlowResult<T>` instead of throwing exceptions
```kotlin
fun getExpenseAll(): FlowResult<List<ExpenseEnt>> {
    return expenseDao.getExpenseAll()
        .map { SuccessResult(it) }  // Wrap in Success
        .catch { ErrorResult(RepositoryException(it)) }  // Catch as Error
}
```

**Usage in ViewModel** - Handle all three states
```kotlin
expenseRepository.getExpenseAll()
    .onSuccess { expenses ->
        _uiState.value = UiState.Success(expenses)
    }
    .onError { error ->
        _uiState.value = UiState.Error(error.message)
    }
    .onLoading {
        _uiState.value = UiState.Loading
    }
    .launchIn(viewModelScope)
```

---

## 4. Value Objects (Domain Models)

### What It Is

Immutable objects that represent **meaningful business concepts** with embedded business logic. Example: `Amount` represents money with currency conversion logic.

### Why Use It

✅ **Business Logic**: Encapsulates rules about the domain  
✅ **Type Safety**: Prevents invalid states (e.g., negative amounts)  
✅ **Self-Documenting**: Code reads like business language  
✅ **Operator Overloading**: Makes operations intuitive  

### Implementation Example

**Amount Value Object** - `/app/src/main/java/com/arduia/expense/domain/Amount.kt`
```kotlin
class Amount: ExpenseStore(DataStoreExchangeRate) {
    companion object {
        // Create from user-facing BigDecimal value
        fun createFromActual(actual: BigDecimal) = Amount().apply {
            val storeValue = actual.multiply(BigDecimal(rate.getRate()))
                .setScale(0, RoundingMode.FLOOR)
            setStore(storeValue.longValueExact())
        }
        
        // Create from internal stored value
        fun createFromStore(store: Long) = Amount().apply {
            setStore(store)
        }
    }
}

// Operator overloading for intuitive arithmetic
operator fun Amount.times(multiplier: Amount): Amount {
    val result = this.getStore() * multiplier.getStore()
    this.setStore(result)
    return this
}

operator fun Amount.plus(amount: Amount): Amount {
    val result = this.getStore() + amount.getStore()
    setStore(result)
    return this
}
```

**Usage**: No need to think about internal storage, just use it like normal money
```kotlin
val price = Amount.createFromActual(BigDecimal("99.99"))
val quantity = Amount.createFromActual(BigDecimal("5"))
val total = price * quantity  // Operator overloading makes it intuitive
```

---

## 5. Builder Pattern

### What It Is

Constructs complex objects step-by-step using a fluent API. Useful when an object has many optional parameters or complex initialization logic.

### Why Use It

✅ **Readability**: Fluent API is clear and self-documenting  
✅ **Flexibility**: Optional parameters without multiple constructors  
✅ **Immutability**: Can build immutable objects  
✅ **Validation**: Validate at construction time  

### Implementation Example

**Filter Builder** - `/app/src/main/java/com/arduia/expense/domain/filter/ExpenseLogFilterInfo.kt`
```kotlin
data class ExpenseLogFilterInfo(
    val dateRangeLimit: DateRange,
    val dateRangeSelected: DateRange,
    val sorting: Sorting
) {
    class Builder {
        private var limit: DateRange = ExpenseDateRange(0, 0)
        private var selected: DateRange = limit
        private var sorting = Sorting.DESC

        fun setDateLimit(range: DateRange): Builder {
            this.limit = range
            return this
        }

        fun setSelectedLimit(range: DateRange): Builder {
            this.selected = range
            return this
        }

        fun setSorting(sorting: Sorting): Builder {
            this.sorting = sorting
            return this
        }

        fun build() = ExpenseLogFilterInfo(
            dateRangeLimit = limit,
            dateRangeSelected = selected,
            sorting = sorting
        )
    }
}
```

**Usage**: Fluent and readable
```kotlin
val filter = ExpenseLogFilterInfo.Builder()
    .setDateLimit(ExpenseDateRange(startDate, endDate))
    .setSelectedLimit(ExpenseDateRange(selectedStart, selectedEnd))
    .setSorting(Sorting.DESC)
    .build()
```

---

## 6. Adapter/Factory Pattern

### What It Is

Separates object **creation** from the objects themselves. Used for mappers (adapting domain models to UI models) and factories (creating complex objects).

### Why Use It

✅ **Separation of Concerns**: Creation logic separate from business logic  
✅ **Reusability**: Same mapper can transform objects in multiple places  
✅ **Testability**: Easy to mock mappers  
✅ **Flexibility**: Change how objects are created without changing consumers  

### Implementation Example

**Mapper Interface** - `/shared/src/main/java/com/arduia/core/arch/Mapper.kt`
```kotlin
interface Mapper<I, O> {
    fun map(input: I): O
}
```

**Expense Mapper Implementation**
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

**Usage in ViewModel**
```kotlin
@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val expenseMapper: ExpenseUiModelMapper
) : ViewModel() {
    
    fun loadExpenses() {
        viewModelScope.launch {
            expenseRepository.getExpenseAll()
                .onSuccess { entities ->
                    // Mapper transforms domain -> UI model
                    val uiModels = entities.map { expenseMapper.map(it) }
                    updateUI(uiModels)
                }
        }
    }
}
```

---

## 7. Multi-Layer Data Access

### What It Is

Data can come from **multiple sources**: local database (Room), network API (Retrofit), or cached preferences (DataStore). Repository abstracts which source is used.

### Why Use It

✅ **Offline Support**: App works without network using cached data  
✅ **Performance**: Cache reduces unnecessary API calls  
✅ **Flexibility**: Switch strategies (cache-first, network-first, etc.)  

### Sources in ProExpense

| Source | Use Case | Class |
|--------|----------|-------|
| **Room Database** | Local persistent storage | `ProExpenseDatabase`, `ExpenseDao` |
| **Retrofit API** | Fetch updates, feedback | `ProExpenseServerRepository` |
| **DataStore Preferences** | User settings, app state | `SettingsRepository` |
| **Files (Backup)** | Import/export data | `ImportWorker`, `ExportWorker` |

**Example: Layered Data Access**
```kotlin
class CurrencyRepositoryImpl @Inject constructor(
    private val currencyDao: CurrencyDao,
    private val currencyPreferenceDao: PreferenceStorageDao
) : CurrencyRepository {
    
    // Check local database first (fastest)
    override fun getCurrencies(): FlowResult<List<CurrencyDto>> {
        return currencyDao.getCurrencyAll()
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }
    }
    
    // Store preference in local storage
    override suspend fun setSelectedCacheCurrency(num: String) {
        withContext(Dispatchers.IO) {
            currencyPreferenceDao.setSelectedCurrency(num)
        }
    }
    
    // Get previously selected currency (with fallback)
    override fun getSelectedCacheCurrency(): FlowResult<CurrencyDto> {
        return currencyPreferenceDao.getSelectedCurrency()
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }
    }
}
```

---

## Key Takeaways

| Pattern | When to Use | File Example |
|---------|------------|--------------|
| **MVVM** | Always - core architecture | `HomeViewModel.kt`, `HomeFragment.kt` |
| **Repository** | For all data access | `ExpenseRepository.kt` |
| **Result Type** | For async operations returning data | `Result.kt` |
| **Value Objects** | For meaningful domain concepts | `Amount.kt` |
| **Builder** | For complex object construction | `ExpenseLogFilterInfo.Builder` |
| **Mapper** | For model transformation | `ExpenseUiModelMapper` |
| **Multi-Layer Data** | When multiple sources needed | `CurrencyRepositoryImpl` |

---

## Reuse in New Architecture

✅ These patterns are **architecture-agnostic** - apply them regardless of UI framework (Compose, XML, or hybrid)  
✅ **Result Type** is especially valuable for type-safe error handling  
✅ **Value Objects** encode business rules that shouldn't change  
✅ **Repository** remains the gateway to all data, regardless of UI implementation
