# Architecture Patterns - Best Practices

## Overview

ProExpense follows proven architecture patterns that are **framework and tool-agnostic**. These principles remain valuable regardless of whether you use Koin or Hilt for DI, Room or SQLDelight for database, or Compose or any other UI framework.

---

## 1. MVVM Architecture

### What It Is

MVVM divides the application into three distinct layers:

- **View (UI Layer)**: Displays data and captures user input
- **ViewModel**: Holds and manages UI state, independent of framework
- **Model (Domain/Data Layer)**: Business logic, repositories, domain models

### Why Use It

✅ **Testability**: Each layer tested independently without UI framework  
✅ **Reusability**: Business logic and state management independent of UI  
✅ **Maintainability**: Clear separation makes code easier to navigate  
✅ **Flexibility**: UI framework can change without affecting core logic  

### Core Pattern

```kotlin
// ViewModel: Independent of any framework
class HomeViewModel(
    private val expenseRepository: ExpenseRepository,
    private val expenseMapper: ExpenseUiModelMapper
) {
    // State management (implementation varies: LiveData, StateFlow, MutableState, etc.)
    // private val _uiState = ... // Choose based on framework
    
    fun loadExpenses() {
        // Load data from repository (framework-agnostic)
        // Update state (framework-specific implementation)
    }
}

// Domain entities (pure Kotlin, no framework imports)
data class Expense(
    val id: Int,
    val name: String,
    val amount: Amount,
    val category: Int,
    val createdDate: Long,
    val modifiedDate: Long
)
```

---

## 2. Repository Pattern

### What It Is

Repository provides a **single point of access** to data, abstracting the underlying data sources (database, network, cache). Implementation details are hidden from consumers.

### Why Use It

✅ **Data Source Abstraction**: Business logic doesn't know if data comes from DB, network, or cache  
✅ **Testability**: Easy to mock or use test implementations  
✅ **Flexibility**: Switch implementations without changing business logic  
✅ **Single Responsibility**: All data access logic in one place  

### Interface Pattern

```kotlin
// Define contract (framework-independent)
interface ExpenseRepository {
    suspend fun insertExpense(expense: Expense): Result<Unit>
    fun getExpenseAll(): Flow<Result<List<Expense>>>
    suspend fun deleteExpense(id: Int): Result<Unit>
}

// Implementation hides details
class ExpenseRepositoryImpl(
    private val expenseDataSource: ExpenseDataSource
) : ExpenseRepository {
    override suspend fun insertExpense(expense: Expense): Result<Unit> {
        return try {
            expenseDataSource.insert(expense)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

**Key Principle**: Define interfaces in domain layer, implement in data layer.

---

## 3. Result Type Pattern

### What It Is

A **sealed class** that represents the outcome of an async operation (success, error, or loading) instead of throwing exceptions.

### Why Use It

✅ **Type Safety**: Compiler enforces handling all cases  
✅ **No Exceptions**: Errors are values, composable like normal data  
✅ **Explicit State**: Always clear what state you're in  
✅ **Functional**: Composes well with functional patterns  

### Core Pattern

```kotlin
// Define once, use everywhere
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// Use in repository contracts
interface Repository {
    fun getData(): Flow<Result<List<Data>>>
}

// Handle in UI layer
when (val result = viewModel.state) {
    is Result.Success -> showData(result.data)
    is Result.Error -> showError(result.exception.message)
    is Result.Loading -> showProgress()
}
```

**Key Principle**: Use Result type for all async operations, never throw exceptions from repositories.

---

## 4. Value Objects (Domain Models)

### What It Is

Immutable objects that represent meaningful **business concepts** with embedded business logic. Unlike data transfer objects, value objects have behavior and validation.

### Why Use It

✅ **Business Logic**: Encapsulates rules about the domain  
✅ **Type Safety**: Prevents invalid states (e.g., negative amounts)  
✅ **Self-Documenting**: Code reads like business language  
✅ **Reusability**: Same domain concept used everywhere  

### Core Pattern

```kotlin
// Value object with business logic
class Amount(
    private val storeValue: Long  // Store as cents to avoid precision errors
) {
    companion object {
        fun createFromActual(actual: BigDecimal) = Amount(
            (actual * BigDecimal(100)).toLong()
        )
    }
    
    fun getActual(): BigDecimal = storeValue.toBigDecimal() / BigDecimal(100)
    
    // Operator overloading for intuitive use
    operator fun plus(other: Amount) = Amount(storeValue + other.storeValue)
    operator fun times(multiplier: Int) = Amount(storeValue * multiplier)
}

// Domain entity using value objects
data class Expense(
    val id: Int,
    val name: String,
    val amount: Amount,  // Type-safe, not just Long or Double
    val category: Int
)
```

**Key Principle**: Use value objects for domain concepts, not primitive types.

---

## 5. Builder Pattern

### What It Is

Constructs complex objects step-by-step using a fluent API, useful when objects have many optional parameters or complex initialization.

### Why Use It

✅ **Readability**: Fluent API is clear and self-documenting  
✅ **Flexibility**: Optional parameters without multiple constructors  
✅ **Immutability**: Build immutable objects  
✅ **Validation**: Can validate at construction time  

### Core Pattern

```kotlin
// Complex filter object
data class FilterCriteria(
    val startDate: Long,
    val endDate: Long,
    val categories: List<Int>,
    val sortOrder: SortOrder
) {
    class Builder {
        private var startDate: Long = 0
        private var endDate: Long = Long.MAX_VALUE
        private var categories: List<Int> = emptyList()
        private var sortOrder: SortOrder = SortOrder.DESC
        
        fun setDateRange(start: Long, end: Long) = apply {
            this.startDate = start
            this.endDate = end
        }
        
        fun setCategories(cats: List<Int>) = apply {
            this.categories = cats
        }
        
        fun build() = FilterCriteria(startDate, endDate, categories, sortOrder)
    }
}

// Usage
val filter = FilterCriteria.Builder()
    .setDateRange(start, end)
    .setCategories(listOf(1, 2, 3))
    .build()
```

**Key Principle**: Use Builder for complex object construction, not data class copy().

---

## 6. Mapper Pattern

### What It Is

Explicitly transforms data from one representation to another (e.g., database entity → domain model → UI model).

### Why Use It

✅ **Separation of Concerns**: Each layer has its own models  
✅ **Testability**: Mappers are easy to test  
✅ **Reusability**: Same mapper used everywhere  
✅ **Flexibility**: Change representation without affecting consumers  

### Core Pattern

```kotlin
// Define mapper interface (framework-agnostic)
interface Mapper<I, O> {
    fun map(input: I): O
}

// Implement for each transformation
class ExpenseDomainMapper : Mapper<ExpenseDto, Expense> {
    override fun map(input: ExpenseDto) = Expense(
        id = input.id,
        name = input.name,
        amount = Amount.createFromActual(input.amount),
        category = input.categoryId,
        createdDate = input.createdAt.toEpochMilliseconds()
    )
}

class ExpenseUiMapper : Mapper<Expense, ExpenseUiModel> {
    override fun map(input: Expense) = ExpenseUiModel(
        id = input.id,
        name = input.name,
        amount = input.amount.getActual().toFloat(),
        category = categoryName(input.category),
        date = formatDate(input.createdDate)
    )
}
```

**Key Principle**: Never return raw database entities or DTOs from repository. Always map to domain models.

---

## 7. Multi-Layer Data Access

### What It Is

Data can come from **multiple sources** (database, network, cache). Repository abstracts which source is used.

### Why Use It

✅ **Offline Support**: App works without network using cached data  
✅ **Performance**: Cache reduces unnecessary calls  
✅ **Flexibility**: Switch strategies (cache-first, network-first, etc.)  

### Core Pattern

```kotlin
interface ExpenseRepository {
    // Returns data from best available source
    fun getExpenses(): Flow<Result<List<Expense>>>
}

class ExpenseRepositoryImpl(
    private val localDataSource: LocalDataSource,  // Database
    private val remoteDataSource: RemoteDataSource, // Network
    private val cacheDataSource: CacheDataSource    // Memory cache
) : ExpenseRepository {
    override fun getExpenses(): Flow<Result<List<Expense>>> {
        // Strategy: Try cache, fall back to local, sync with remote
        return cacheDataSource.getExpenses()
            .onEmpty { emit(localDataSource.getExpenses()) }
            .onSuccess { syncWithRemote() }
    }
}
```

**Key Principle**: Repository decides data sources, business logic doesn't care.

---

## Key Architecture Principles

| Pattern | Purpose | Applies Everywhere |
|---------|---------|-------------------|
| **MVVM** | Separate UI from business logic | ✅ Yes |
| **Repository** | Abstract data access | ✅ Yes |
| **Result Type** | Handle async operations safely | ✅ Yes |
| **Value Objects** | Represent domain concepts | ✅ Yes |
| **Builder** | Construct complex objects | ✅ Yes |
| **Mapper** | Transform between representations | ✅ Yes |
| **Multi-Layer Data** | Support multiple data sources | ✅ Yes |

---

## Principles that Carry Forward

These architecture patterns are **framework-agnostic** and valuable in any implementation:

✅ **Separation of concerns**: UI ≠ Business Logic ≠ Data Access  
✅ **Dependency inversion**: Depend on interfaces, not implementations  
✅ **Single responsibility**: Each class has one reason to change  
✅ **Open/closed**: Open for extension, closed for modification  
✅ **Interface segregation**: Small, focused interfaces  
✅ **Composition over inheritance**: Build complex behavior from simple pieces  

**Key**: The architecture patterns matter more than the tools used to implement them.
