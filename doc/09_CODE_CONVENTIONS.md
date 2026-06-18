# Code Conventions & Style - ProExpense Best Practices

## Overview

Consistent code conventions make the codebase predictable and easier to navigate. ProExpense follows Kotlin best practices with Android-specific conventions.

---

## Naming Conventions

### Classes

**Type-Based Suffixes:**

| Suffix | Purpose | Example | Location |
|--------|---------|---------|----------|
| `ViewModel` | UI state management | `HomeViewModel` | `ui/` |
| `Fragment` | UI screens | `HomeFragment` | `ui/` |
| `Activity` | Activities | `MainActivity` | `ui/` |
| `Repository` | Data access (interface) | `ExpenseRepository` | `data/` |
| `*Impl` | Repository implementation | `ExpenseRepositoryImpl` | `data/` |
| `Dao` | Database access objects | `ExpenseDao` | `data/local/` |
| `*Ent` | Database entities | `ExpenseEnt` | `data/local/` |
| `*Dto` | Network DTOs | `ExpenseVersionDto` | `data/network/` |
| `*UiModel` | UI presentation model | `ExpenseUiModel` | `ui/model/` |
| `*Mapper` | Data transformation | `ExpenseUiModelMapper` | `ui/mapper/` |
| `*Worker` | Background tasks | `ImportWorker` | `data/` |
| `*Provider` | Abstraction provider | `ExpenseCategoryProvider` | `ui/common/` |
| `*Formatter` | Data formatting | `ExpenseDateFormatter` | `ui/common/formatter/` |

### PascalCase for All Classes

```kotlin
// Classes
class HomeViewModel { }
class ExpenseRepositoryImpl { }
class AmountTypeConverter { }

// Interfaces
interface ExpenseRepository { }
interface Mapper<I, O> { }

// Data Classes
data class ExpenseEnt { }
data class ExpenseUiModel { }

// Sealed Classes
sealed class Result<out T> { }
sealed class HomeUiState { }
```

---

## Properties & Fields

### Naming Pattern

```kotlin
// Private mutable - underscore prefix
private val _uiState = MutableLiveData<UiState>()

// Public immutable - no prefix
val uiState: LiveData<UiState> = _uiState

// Regular properties - no prefix
private val repository: ExpenseRepository
val expenses: List<Expense>
```

### Val vs Var

**Prefer `val` (immutable):**

```kotlin
// GOOD - immutable
val name = "Coffee"
val items = listOf(1, 2, 3)

// BAD - mutable when not needed
var name = "Coffee"
var items = mutableListOf(1, 2, 3)
```

**Use `var` only when necessary:**

```kotlin
// GOOD - tracking mutable state
private var currentPage = 0

// GOOD - LiveData mutation
private val _state = MutableLiveData<State>()
_state.value = newState
```

---

## Constants

**UPPER_CASE_WITH_UNDERSCORES:**

```kotlin
companion object {
    private const val TABLE_NAME = "expenses"
    private const val COLUMN_ID = "id"
    private const val DEFAULT_PAGE_SIZE = 30
    private const val PREF_KEY_LANGUAGE = "language"
}

// At top level (not in companion)
const val MAX_EXPENSE_AMOUNT = 999_999_999.99
const val MIN_EXPENSE_AMOUNT = 0.01
```

---

## Function Naming

**Verb + Noun Pattern:**

```kotlin
// Actions
fun loadExpenses() { }
fun deleteExpense(expense: Expense) { }
fun updateUI(state: UiState) { }
fun saveSettings(settings: Settings) { }

// Queries (return boolean)
fun isEmpty(): Boolean { }
fun isExpenseValid(expense: Expense): Boolean { }
fun canDelete(): Boolean { }

// Getters (return value)
fun getExpense(id: Int): Expense { }
fun getCategoryName(id: Int): String { }

// Suspend functions
suspend fun insertExpense(expense: Expense) { }
suspend fun fetchRemoteExpenses(): List<Expense> { }
```

---

## Data Classes for Models

Use data classes for models:

```kotlin
// Entity (Database)
@Entity(tableName = "expenses")
data class ExpenseEnt(
    @PrimaryKey val expenseId: Int = 0,
    val name: String?,
    val amount: Amount,
    val category: Int,
    val note: String?,
    val createdDate: Long,
    val modifiedDate: Long
)

// UI Model
data class ExpenseUiModel(
    val id: Int,
    val name: String,
    val amount: Float,
    val category: String,
    val date: String,
    val note: String?
)

// Network DTO
data class ExpenseVersionDto(
    val version: String,
    val releaseNotes: String?,
    val downloadUrl: String
)
```

**Benefits:**
- Automatic `equals()`, `hashCode()`, `toString()`, `copy()`
- Destructuring support
- No boilerplate

---

## Sealed Classes for State

```kotlin
// UI State
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val expenses: List<ExpenseUiModel>) : HomeUiState()
    data class Error(val message: String?) : HomeUiState()
    object Empty : HomeUiState()
}

// Result Type
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

**Usage:**
```kotlin
when (state) {
    is HomeUiState.Loading -> showProgress()
    is HomeUiState.Success -> showExpenses(state.expenses)
    is HomeUiState.Error -> showError(state.message)
    is HomeUiState.Empty -> showEmptyState()
}
```

---

## File Organization

### Structure per File

```kotlin
// File: ExpenseRepository.kt
// 1. Package declaration
package com.arduia.expense.data

// 2. Imports (grouped)
import android.content.Context
import androidx.room.Dao
import com.arduia.expense.domain.Amount
import kotlinx.coroutines.flow.Flow

// 3. Interface/Main class
interface ExpenseRepository {
    suspend fun insertExpense(expenseEnt: ExpenseEnt)
    fun getExpenseAll(): FlowResult<List<ExpenseEnt>>
}

// 4. Implementation or related classes
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {
    // ...
}
```

### Folder Structure

```
app/src/main/java/com/arduia/expense/
├── domain/                 # Business logic
│   ├── Amount.kt
│   ├── ExpenseStore.kt
│   └── filter/
│       ├── DateRange.kt
│       └── ExpenseLogFilterInfo.kt
├── data/                   # Data access
│   ├── ExpenseRepository.kt
│   ├── ExpenseRepositoryImpl.kt
│   ├── local/              # Room/Database
│   │   ├── ProExpenseDatabase.kt
│   │   ├── ExpenseEnt.kt
│   │   └── ExpenseDao.kt
│   ├── network/            # Retrofit/API
│   │   ├── ExpenseVersionDto.kt
│   │   └── FeedbackDto.kt
│   └── ext/                # Extension functions
│       └── Result.kt
├── ui/                     # Presentation
│   ├── MainActivity.kt
│   ├── home/
│   │   ├── HomeFragment.kt
│   │   ├── HomeViewModel.kt
│   │   └── HomeUiState.kt
│   ├── detail/
│   │   ├── ExpenseDetailFragment.kt
│   │   ├── ExpenseDetailViewModel.kt
│   │   └── ExpenseDetailUiState.kt
│   ├── common/
│   │   ├── formatter/
│   │   │   ├── ExpenseDateFormatter.kt
│   │   │   └── ExpenseCurrencyFormatter.kt
│   │   ├── provider/
│   │   │   └── ExpenseCategoryProvider.kt
│   │   └── component/
│   │       ├── MaterialSearchBox.kt
│   │       └── MarginItemDecoration.kt
│   └── mapper/
│       ├── ExpenseUiModelMapper.kt
│       └── BackupUiModelMapper.kt
└── di/                     # Dependency Injection
    ├── DatabaseModule.kt
    ├── RepositoryModule.kt
    ├── AbstractMapperModule.kt
    └── ...
```

---

## Kotlin Best Practices

### Extension Functions

```kotlin
// Add behavior to existing types
fun View.asVisible() {
    visibility = View.VISIBLE
}

// Use receiver scope implicitly
fun <T> Flow<Result<T>>.onSuccess(
    block: suspend (T) -> Unit
): Flow<Result<T>> = onEach { result ->
    if (result is Result.Success) {
        block(result.data)
    }
}
```

### Scope Functions

```kotlin
// apply - configure object, return it
val expense = ExpenseEnt(...).apply {
    name = "Coffee"
    amount = Amount.createFromStore(100)
}

// let - use object in expression
user?.let { 
    loadUserExpenses(it.id)
}

// run - execute block, return result
val total = expenses.map { it.amount }.run {
    fold(Amount.createFromStore(0)) { acc, amount ->
        acc + amount
    }
}

// also - side effects
listOf(1, 2, 3).also { 
    Timber.d("List size: ${it.size}")
}
```

### Destructuring

```kotlin
// Data classes support destructuring
data class Expense(val id: Int, val name: String, val amount: Double)

val expense = Expense(1, "Coffee", 5.0)
val (id, name, amount) = expense

// In loops
expenses.forEach { (id, name, amount) ->
    println("$id: $name = $amount")
}

// In maps
mapOf(1 to "a", 2 to "b").forEach { (key, value) ->
    println("$key: $value")
}
```

### Trailing Lambda

```kotlin
// Normal
viewModel.uiState.observe(viewLifecycleOwner, { state ->
    updateUI(state)
})

// Trailing lambda - cleaner
viewModel.uiState.observe(viewLifecycleOwner) { state ->
    updateUI(state)
}

// Even cleaner with single parameter
viewModel.uiState.observe(viewLifecycleOwner) {
    updateUI(it)
}
```

---

## View Binding (Not Synthetic)

```kotlin
// GOOD - View Binding
class HomeFragment : Fragment(R.layout.fragment_home) {
    private var binding: FragmentHomeBinding? = null
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        
        binding?.expenseList?.adapter = adapter
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

// BAD - Synthetic (deprecated)
class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        expenseList.adapter = adapter  // No longer works, don't use
    }
}
```

---

## Comments

**Only document WHY, not WHAT:**

```kotlin
// GOOD - Explains business reason
// Multiply by 100 to avoid floating-point precision errors
val storeValue = actual.multiply(BigDecimal(rate.getRate()))

// BAD - Obvious from code
// Set visibility to visible
view.visibility = View.VISIBLE

// GOOD - Explains non-obvious behavior
// Room observer emits only when actual data changes, not on subscriptions
flow.distinctUntilChanged()

// GOOD - Documents workaround
// TODO: Replace with WorkManager once min SDK >= 26
val delayMs = 1000
```

---

## Best Practices

### ✅ DO:

1. **Use type-based suffixes**
   ```kotlin
   HomeViewModel
   ExpenseRepositoryImpl
   ExpenseDateFormatter
   ```

2. **Prefer data classes**
   ```kotlin
   data class Expense(...)
   ```

3. **Use sealed classes** for state
   ```kotlin
   sealed class Result<T> { ... }
   ```

4. **Prefer val over var**
   ```kotlin
   val name = "Coffee"  // immutable
   ```

5. **Use extension functions** for readability
   ```kotlin
   view.asVisible()
   context.dp(16)
   ```

### ❌ DON'T:

1. **Hungarian notation**
   ```kotlin
   // BAD
   var strName: String
   var intCount: Int
   var bIsLoading: Boolean
   
   // GOOD
   var name: String
   var count: Int
   var isLoading: Boolean
   ```

2. **Generic names**
   ```kotlin
   // BAD
   var data: Any
   fun process(x: Int): Boolean
   
   // GOOD
   var expenses: List<Expense>
   fun isExpenseValid(expense: Expense): Boolean
   ```

3. **Magic numbers**
   ```kotlin
   // BAD
   if (status == 200) { ... }
   delay(1000)
   
   // GOOD
   const val SUCCESS_STATUS = 200
   const val LOADING_DELAY_MS = 1000
   ```

---

## Reuse in New Architecture

✅ **Naming conventions** apply to all new code  
✅ **File organization** pattern works with any framework  
✅ **Data classes** remain the pattern for models  
✅ **Sealed classes** for state management are universal  
✅ **Kotlin best practices** are framework-agnostic

**Key: Consistency matters more than the specific conventions chosen**
