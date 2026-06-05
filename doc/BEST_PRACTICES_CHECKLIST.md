# Best Practices - Quick Reference Checklist

Use this checklist when writing code. These are **principles-based**, not tool-specific, and apply regardless of framework.

---

## 🏗️ Architecture & Patterns

When creating new features:

- [ ] **Separate into layers**: Domain → Data → ViewModel → UI
- [ ] **Use MVVM**: ViewModel for state, separate from business logic
- [ ] **Implement Repository interface**: Abstract all data access
- [ ] **Use Result type**: Never throw from repositories
- [ ] **Use Value Objects**: `Amount`, not `Long`; `DateRange`, not `Pair`
- [ ] **Apply Builder pattern**: For complex object construction
- [ ] **Create Mappers**: Transform between layer representations
- [ ] **Validate at construction**: Fail fast in domain models
- [ ] **Depend on interfaces**: Not concrete implementations

**Layer Structure:**
```
Domain: Pure business logic, entities, validation
Data: Repositories, data sources, mappers
ViewModel: State management, use cases
UI: Display and interaction
```

---

## 📦 Domain Modeling

When defining business logic:

- [ ] **Use immutable models**: `val`, not `var`
- [ ] **Create Value Objects**: `Amount`, `DateRange` with logic
- [ ] **Validate in constructors**: `init { validate(...) }`
- [ ] **Overload operators**: For intuitive use (`Amount + Amount`)
- [ ] **Use sealed classes**: For type-safe alternatives
- [ ] **Store money as integer**: Avoid floating-point errors
- [ ] **No framework imports**: Pure Kotlin only
- [ ] **Document business rules**: Comments for non-obvious logic

**Pattern:**
```kotlin
data class Expense(
    val id: Int,
    val name: String,
    val amount: Amount,      // Value object, not Double
    val category: Int,
    val createdDate: Long
)

class Amount(val storeValue: Long) {
    operator fun plus(other: Amount) = Amount(storeValue + other.storeValue)
}
```

---

## 📊 Data Layer

When working with repositories:

- [ ] **Define interfaces first**: Contract before implementation
- [ ] **Return Result types**: Never throw exceptions
- [ ] **Support multiple sources**: DB, network, cache
- [ ] **Map to domain models**: Don't leak implementation details
- [ ] **Validate input**: At repository boundary
- [ ] **Use async/await pattern**: Suspend functions, not callbacks
- [ ] **Cache strategically**: Improve performance
- [ ] **Handle errors gracefully**: Network, database, parsing errors

**Pattern:**
```kotlin
interface ExpenseRepository {
    suspend fun insertExpense(expense: Expense): Result<Unit>
    fun getExpenseAll(): Flow<Result<List<Expense>>>
}

class ExpenseRepositoryImpl(...) : ExpenseRepository {
    override suspend fun insertExpense(expense: Expense) = try {
        dataSource.insert(expense)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
```

---

## 🎯 ViewModel & State Management

When managing UI state:

- [ ] **Use immutable state**: StateFlow, not mutable properties
- [ ] **One ViewModel per screen**: Single responsibility
- [ ] **Expose as read-only**: Private mutable, public immutable
- [ ] **Use sealed classes**: For UI states (Loading, Success, Error)
- [ ] **Call repositories only**: Never access data layer directly from UI
- [ ] **Handle all Result states**: Loading, success, error
- [ ] **Independent of framework**: Pure Kotlin, testable

**Pattern:**
```kotlin
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val expenses: List<Expense>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(private val repository: ExpenseRepository) {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadExpenses() {
        // Update state based on repository result
    }
}
```

---

## 🧪 Testing

When writing tests:

- [ ] **Test behavior, not implementation**: "What" not "how"
- [ ] **Use AAA pattern**: Arrange → Act → Assert
- [ ] **Mock external dependencies**: Database, network, services
- [ ] **Test error cases**: Not just happy path
- [ ] **Keep tests focused**: One thing per test
- [ ] **Descriptive names**: Test name explains expected behavior
- [ ] **Test pure functions first**: Domain logic has no dependencies
- [ ] **Use in-memory implementations**: For data layer tests

**Coverage Goals:**
- Domain: 90%+ (pure logic, easy to test)
- Data: 80%+ (core logic with mocks)
- ViewModel: 70%+ (state management)
- UI: 50%+ (framework-dependent)

**Pattern:**
```kotlin
@Test
fun testWhenRepositoryFails_viewModelShowsError() {
    // Arrange
    val mockRepo = mockRepository()
    every { mockRepo.getExpenses() } throws Exception()
    
    // Act
    val viewModel = HomeViewModel(mockRepo)
    viewModel.loadExpenses()
    
    // Assert
    assert(viewModel.uiState.value is HomeUiState.Error)
}
```

---

## 🔒 Security

When handling data:

- [ ] **No hardcoded secrets**: API keys, passwords - load from config
- [ ] **Validate all input**: Files, URIs, user data at boundary
- [ ] **No sensitive logging**: No emails, passwords, tokens in logs
- [ ] **Use parameterized queries**: Prevent SQL injection
- [ ] **Handle errors safely**: Use Result type
- [ ] **Store minimally**: Only necessary data
- [ ] **Least privilege**: Limited responsibilities per component

**Pattern:**
```kotlin
// ✅ Validate at boundary
fun insertExpense(expense: Expense): Result<Unit> {
    if (expense.amount <= 0) return Result.Error("Invalid amount")
    if (expense.name.isBlank()) return Result.Error("Name required")
    return processExpense(expense)
}

// ✅ No hardcoded secrets
val apiKey = getConfigValue("API_KEY")

// ✅ No sensitive logging
log("User logged in: ${user.id}")  // Safe
// NOT: log("User logged in: ${user.email}")  // PII
```

---

## 📝 Code Style

When writing code:

- [ ] **Use type-based suffixes**: `ViewModel`, `Repository`, `Mapper`
- [ ] **PascalCase for classes**: `HomeViewModel`, `ExpenseRepository`
- [ ] **UPPER_CASE for constants**: `MAX_AMOUNT`, `DEFAULT_PAGE_SIZE`
- [ ] **Prefer `val` over `var`**: Immutability by default
- [ ] **Data classes for models**: Automatic `equals()`, `toString()`, `copy()`
- [ ] **Sealed classes for variants**: Type-safe alternatives
- [ ] **Meaningful names**: Code should read like business language
- [ ] **Short comments**: Document WHY, not WHAT

**Pattern:**
```kotlin
// Type-based naming
class HomeViewModel { }
interface ExpenseRepository { }
class ExpenseUiMapper : Mapper<Expense, ExpenseUiModel> { }

// Data classes
data class Expense(val id: Int, val name: String)

// Sealed classes
sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val e: Exception) : Result<Nothing>()
}
```

---

## 📋 Code Review Checklist

Before submitting code for review:

- [ ] **Follows architecture**: Correct layer for code
- [ ] **Uses Result types**: No exceptions from repositories
- [ ] **Tests written**: Unit tests for logic
- [ ] **No hardcoded values**: Constants defined, secrets in config
- [ ] **Input validated**: External data checked
- [ ] **Error handling**: All paths covered
- [ ] **No sensitive logging**: No PII in logs
- [ ] **Meaningful names**: Code is self-documenting
- [ ] **No framework leaks**: Domain/data independent of UI
- [ ] **Builds/tests pass**: No breaking changes

---

## ⚠️ Common Mistakes

| Mistake | Impact | Fix |
|---------|--------|-----|
| Business logic in UI | Hard to test, tied to framework | Move to ViewModel |
| Throwing from repository | Exception handling scattered | Use Result type |
| Direct DAO/API from ViewModel | Can't swap implementations | Use Repository interface |
| No input validation | Invalid/malicious data | Validate at boundary |
| Logging PII | Privacy breach | Only log safe data |
| Hardcoded secrets | Security breach | Use config/environment |
| Mutable state | Unpredictable behavior | Use immutable models |
| One giant class | Hard to test | Split by responsibility |

---

## 🎯 Golden Rule

**Code should be so clear that the next person reading it asks "why was this built this way?" not "what does this code do?"**

If you're writing comments explaining WHAT the code does, refactor instead.

---

## 📚 Documentation

For detailed explanations, see:

- **01_ARCHITECTURE_PATTERNS.md** - Architecture principles
- **06_DOMAIN_MODELING.md** - Domain logic and value objects
- **07_TESTING_GUIDELINES.md** - Testing strategies
- **09_CODE_CONVENTIONS.md** - Kotlin style conventions
- **10_LOGGING_SECURITY.md** - Logging and security practices
- **11_CICD_WORKFLOW.md** - Build, versioning, deployment

---

## 📞 Questions?

1. Check the relevant guide in `/doc/`
2. Look at existing code for patterns
3. Ask team members
4. Run tests to verify behavior

---

**Remember: These practices apply to ANY architecture or tool choice. Focus on principles, not frameworks.**
