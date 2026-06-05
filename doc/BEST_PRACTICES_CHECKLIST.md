# ProExpense Best Practices - Quick Reference Checklist

Use this checklist when writing new code to apply ProExpense best practices. See the detailed guides in `/doc/` for full explanations.

---

## 🏗️ Architecture & Patterns

When creating new features:

- [ ] **Separate into layers**: Data → Domain → UI
- [ ] **Use MVVM**: ViewModel for state, LiveData for observation
- [ ] **Implement Repository interface**: No direct DAO calls from UI
- [ ] **Use Result type**: `Result<T>` instead of exceptions for async operations
- [ ] **Use Value Objects**: `Amount`, not `Long`; `DateRange`, not `Pair<Long, Long>`
- [ ] **Apply Builder pattern**: For complex object construction
- [ ] **Create Mappers**: Domain → UI transformation via `Mapper<I, O>`
- [ ] **Validate at construction**: Fail fast in domain models

**File locations:**
- Domain: `/app/src/main/java/com/arduia/expense/domain/`
- Data: `/app/src/main/java/com/arduia/expense/data/`
- UI: `/app/src/main/java/com/arduia/expense/ui/`

---

## 💉 Dependency Injection (Hilt)

When registering dependencies:

- [ ] **Use `@HiltViewModel`** for ViewModels
- [ ] **Use `@Inject constructor`** for constructor injection
- [ ] **Use `@Provides`** for factory methods
- [ ] **Use `@Binds`** for interface → implementation binding
- [ ] **Use correct scopes**: `@Singleton` for DB/repos, `@Factory` for mappers
- [ ] **Organize in modules**: One module per feature (Database, Repository, Mapper, etc.)
- [ ] **Avoid circular dependencies**: Restructure if A needs B and B needs A
- [ ] **Test with `@HiltAndroidTest`**: For instrumented tests

**File location:** `/app/src/main/java/com/arduia/expense/di/`

---

## ⚡ Async & Reactive (Coroutines + Flow)

When handling async operations:

- [ ] **Use `suspend` for one-shot operations**: `suspend fun insertExpense(exp: ExpenseEnt)`
- [ ] **Use `Flow<T>` for streaming data**: `fun getExpenses(): Flow<List<ExpenseEnt>>`
- [ ] **Wrap in `FlowResult<T>`**: `FlowResult<List<ExpenseEnt>> = Flow<Result<T>>`
- [ ] **Use `.flowOn(Dispatchers.IO)`**: Specify thread for database/network
- [ ] **Use `viewModelScope`**: Auto-cancels on ViewModel destruction
- [ ] **Use `.onSuccess()`, `.onError()`, `.onLoading()`**: Handle Result states
- [ ] **Observe with `viewLifecycleOwner`**: Prevents memory leaks
- [ ] **Use `runTest {}`**: For testing suspend functions

---

## 🗄️ Database (Room)

When working with Room:

- [ ] **Use `@Entity` for database models**: Suffix with `*Ent`
- [ ] **Use `*Dao` for data access**: Abstract interfaces, suspend functions
- [ ] **Return `Flow<T>` from queries**: For reactive updates
- [ ] **Use `@TypeConverter`** for complex types: `Amount` → `Long`
- [ ] **Index frequently queried columns**: `@ColumnInfo(index = true)`
- [ ] **Use parameterized queries**: `@Query("WHERE id = :id")` prevents SQL injection
- [ ] **Add migrations for schema changes**: `addMigrations(MIGRATION_X_Y)`
- [ ] **Use version control**: Increment `@Database(version = X)`

**File location:** `/app/src/main/java/com/arduia/expense/data/local/`

---

## 🎨 UI Layer (ViewModel + LiveData)

When building UI:

- [ ] **Use sealed class for UI state**: `HomeUiState.Loading`, `.Success`, `.Error`
- [ ] **Use LiveData in ViewModel**: `MutableLiveData<T>` (private), `LiveData<T>` (public)
- [ ] **Expose immutable LiveData**: `private val _state = MutableLiveData()`, `val state: LiveData = _state`
- [ ] **Use `by viewModels()`** in Fragment: Hilt injection with survival over config changes
- [ ] **Observe with LiveData**: `viewModel.state.observe(viewLifecycleOwner) { ... }`
- [ ] **Clear binding in `onDestroyView()`**: Prevent memory leaks
- [ ] **Use Safe Args**: Type-safe navigation
- [ ] **Create UI Mappers**: Domain → UI transformation

**File location:** `/app/src/main/java/com/arduia/expense/ui/`

---

## 📦 Domain Modeling

When defining business logic:

- [ ] **Use immutable data classes**: `val`, not `var`
- [ ] **Create Value Objects**: `Amount`, `DateRange` with logic
- [ ] **Validate in constructors**: `init { validate(...) }`
- [ ] **Overload operators**: `Amount + Amount`, `Amount * Number`
- [ ] **Use sealed classes for types**: `Result<T>`, state enums
- [ ] **Store money as Long (cents)**: No floating-point errors
- [ ] **No framework dependencies**: Domain should be pure Kotlin
- [ ] **Document business rules**: Comments for non-obvious logic

**File location:** `/app/src/main/java/com/arduia/expense/domain/`

---

## 🧪 Testing

When writing tests:

- [ ] **Test behavior, not implementation**: "What" not "how"
- [ ] **Mock external dependencies**: Use `@RelaxedMockK`
- [ ] **Use `runTest {}`** for coroutines: StandardTestDispatcher
- [ ] **Use `InstantTaskExecutorRule`** for LiveData: Execute synchronously
- [ ] **Follow AAA pattern**: Arrange → Act → Assert
- [ ] **Test error cases**: Not just happy path
- [ ] **Use `@HiltAndroidTest`** for integration tests
- [ ] **Verify with `coVerify`**: For suspend functions

**File locations:**
- Unit tests: `/app/src/test/java/com/arduia/expense/`
- Instrumented: `/app/src/androidTest/java/com/arduia/expense/`

---

## 🛠️ Shared Utilities

When adding reusable code:

- [ ] **Create extension functions**: `View.asVisible()`, `context.dp(16)`
- [ ] **Use base Mapper interface**: `Mapper<I, O>`
- [ ] **Create formatters**: `DateFormatter` with multiple implementations
- [ ] **Use provider pattern**: `ExpenseCategoryProvider` interface
- [ ] **Inject utilities via DI**: Not static methods
- [ ] **Keep utilities pure**: No side effects or context dependence
- [ ] **Document extensions**: Especially non-obvious ones

**File location:** `/shared/src/main/java/com/arduia/core/`

---

## 📝 Code Style

When writing code:

- [ ] **Use type-based suffixes**: `ViewModel`, `Repository`, `Mapper`, `Dao`, `Formatter`
- [ ] **Use PascalCase** for classes
- [ ] **Use UPPER_CASE** for constants: `const val TABLE_NAME = "expenses"`
- [ ] **Prefer `val`** over `var`: Immutability by default
- [ ] **Private mutable with underscore**: `private val _state = MutableLiveData()`
- [ ] **Data classes** for models: Automatic `equals()`, `toString()`, `copy()`
- [ ] **Sealed classes** for type-safe alternatives
- [ ] **Use View Binding**: Not synthetic imports
- [ ] **Document WHY**: Not WHAT (code should be self-documenting)

---

## 📊 Logging & Security

When adding logging and handling data:

- [ ] **Use `Timber.d()`** for debug logs: Conditional in release
- [ ] **Use `Timber.e(exception, "message")`** for errors
- [ ] **Never log PII**: No emails, passwords, tokens
- [ ] **Validate external input**: URIs, file sizes, user input
- [ ] **Load secrets from properties**: `api.properties`, not hardcoded
- [ ] **Use parameterized SQL queries**: Prevents injection
- [ ] **Encrypt sensitive data**: Don't store raw secrets
- [ ] **ProGuard in release**: `minifyEnabled = true`

---

## 🚀 CI/CD

When preparing for deployment:

- [ ] **Run tests locally**: `./gradlew test` before push
- [ ] **Update version semantically**: `1.0.0-beta08` → `1.0.0`
- [ ] **Use version catalog**: `gradle/libs.versions.toml`
- [ ] **Enable ProGuard**: Obfuscate release builds
- [ ] **Use KSP not KAPT**: Faster compilation
- [ ] **Write meaningful commit messages**: Describe the why
- [ ] **Use feature branches**: `feature/...`, `bugfix/...`, `docs/...`
- [ ] **Review PRs carefully**: Code review catches issues

---

## 📋 New Feature Checklist

### When Adding a New Feature

1. **Plan Architecture**
   - [ ] Design domain models
   - [ ] Plan data layer (repository, DAO)
   - [ ] Plan UI layer (ViewModel, Fragment/Compose)

2. **Implement Domain Layer** (start here)
   - [ ] Create value objects (e.g., Amount)
   - [ ] Create domain entities (e.g., ExpenseLogItemEnt)
   - [ ] Add validation in constructors
   - [ ] Create filters/models if needed

3. **Implement Data Layer**
   - [ ] Create/update DAO
   - [ ] Create/update Repository interface
   - [ ] Implement repository
   - [ ] Add migrations if schema changed

4. **Implement UI Layer**
   - [ ] Create ViewModel
   - [ ] Define UI State sealed class
   - [ ] Create mappers (Domain → UI)
   - [ ] Create Fragment/Compose screen
   - [ ] Wire navigation

5. **Dependency Injection**
   - [ ] Add DI module entries
   - [ ] Bind repository to interface
   - [ ] Register mappers

6. **Testing**
   - [ ] Write domain tests (validation, logic)
   - [ ] Write repository tests (mocked DAO)
   - [ ] Write ViewModel tests (mocked repository)
   - [ ] Write integration tests if needed

7. **Logging & Security**
   - [ ] Add strategic Timber logs
   - [ ] Validate external input
   - [ ] No sensitive data logging

8. **CI/CD**
   - [ ] Run `./gradlew test` locally
   - [ ] Commit with descriptive message
   - [ ] Push to feature branch
   - [ ] Create PR with tests passing

---

## 🐛 Debugging Checklist

### When Something Breaks

1. **Check test failure message** - Tests are the source of truth
2. **Check logcat with Timber** - See what happened
3. **Check DAO query** - Is the SQL correct?
4. **Check repository logic** - Is error handling right?
5. **Check ViewModel state** - Is state being set correctly?
6. **Check Fragment observation** - Is observer attached with viewLifecycleOwner?
7. **Check DI binding** - Is the implementation registered?
8. **Check type converters** - Are complex types serialized correctly?

---

## ⚠️ Common Mistakes

| Mistake | Problem | Solution |
|---------|---------|----------|
| Blocking database calls | ANR (crashes) | Use `suspend` + `Dispatchers.IO` |
| Direct DAO in ViewModel | Hard to test | Use Repository interface |
| GlobalScope.launch | Memory leaks | Use `viewModelScope.launch` |
| Logging PII | Security issue | Only log non-sensitive IDs |
| Mutable LiveData exposed | Can be modified externally | Expose immutable `LiveData<T>` |
| No View Binding | Null pointer errors | Use View Binding |
| Hardcoded secrets | Security breach | Load from properties file |
| SQL string concatenation | SQL injection | Use parameterized queries |
| No input validation | Crashes/exploits | Validate all external input |
| Forgetting migrations | Data loss | Add migrations for schema changes |

---

## 📚 Quick Links

- **See full guides**: `/doc/01_ARCHITECTURE_PATTERNS.md` through `/doc/11_CICD_WORKFLOW.md`
- **Repository pattern**: See `/doc/01_ARCHITECTURE_PATTERNS.md`
- **Hilt setup**: See `/doc/02_DEPENDENCY_INJECTION.md`
- **Coroutines**: See `/doc/03_REACTIVE_PROGRAMMING.md`
- **Room**: See `/doc/04_DATABASE_PERSISTENCE.md`
- **UI/ViewModel**: See `/doc/05_UI_LAYER.md`
- **Domain models**: See `/doc/06_DOMAIN_MODELING.md`
- **Testing**: See `/doc/07_TESTING_GUIDELINES.md`
- **Utilities**: See `/doc/08_SHARED_UTILITIES.md`
- **Code style**: See `/doc/09_CODE_CONVENTIONS.md`
- **Logging/Security**: See `/doc/10_LOGGING_SECURITY.md`
- **CI/CD**: See `/doc/11_CICD_WORKFLOW.md`

---

## 🎯 Golden Rule

**Code should be so clear that the next person reading it asks "why was this built this way?" not "what does this code do?"**

If you're writing comments explaining WHAT the code does, refactor instead:
- Better variable names
- Extract to well-named function
- Use domain language

---

## 📞 Questions?

When unsure:
1. Check the relevant guide in `/doc/`
2. Look at existing code for patterns
3. Ask team members
4. Check tests for usage examples

---

**Last Updated:** June 2026  
**ProExpense Version:** 1.0.0
