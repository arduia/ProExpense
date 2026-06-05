# ProExpense KMP Compose - Best Practices Checklist

Use this checklist when writing new code for the KMP Compose multi-module architecture. See detailed guides in `/doc/` for full explanations.

---

## 🏗️ Multi-Module Architecture

When organizing code across shared and platform modules:

- [ ] **Code in `commonMain`**: Domain, data, viewmodel (shared across all platforms)
- [ ] **Code in `androidMain`**: Android-specific DI, implementations
- [ ] **Code in `iosMain`**: iOS-specific DI, implementations
- [ ] **Code in `webMain`**: Web-specific DI, implementations
- [ ] **Use `expect/actual`**: For platform-specific interfaces
- [ ] **No platform imports in shared code**: Only use Kotlin stdlib, kotlinx libraries
- [ ] **Single Compose UI codebase**: No platform-specific UI code

**Module structure:**
```
shared/
├── domain/        # Pure domain logic, entities
├── data/          # Repositories, data sources
├── viewmodel/     # ViewModels, UI state
├── ui/            # Compose screens (shared)
├── di/            # Koin modules
└── utils/         # Utilities, expect/actual
```

---

## 🏛️ Architecture & Patterns

When creating new features:

- [ ] **Separate into layers**: Domain → Data → ViewModel → UI
- [ ] **Use MVVM**: ViewModel for state, StateFlow for observation
- [ ] **Implement Repository interface**: No direct DAO/API calls from UI
- [ ] **Use Result type**: `Result<T>` instead of exceptions for async
- [ ] **Use Value Objects**: `Amount`, not `Long`; `DateRange`, not `Pair<Long, Long>`
- [ ] **Apply Builder pattern**: For complex object construction
- [ ] **Create Mappers**: Domain → DTO/ViewModel transformation
- [ ] **Validate at construction**: Fail fast in domain models
- [ ] **Use multimodule imports**: Share code across platforms via `shared` module

**File locations:**
- Domain: `shared/domain/src/commonMain/`
- Data: `shared/data/src/commonMain/`
- ViewModel: `shared/viewmodel/src/commonMain/`
- UI: `shared/ui/src/commonMain/`

---

## 💉 Dependency Injection (Koin)

When registering dependencies:

- [ ] **Use `single { }`** for singletons (DB, repos, HTTP client)
- [ ] **Use `factory { }`** for new instances (ViewModels, mappers)
- [ ] **Use constructor injection**: Pass dependencies to constructors
- [ ] **Define interfaces**: Abstract repositories, use Koin to bind implementations
- [ ] **Organize in modules**: One module per feature
- [ ] **Load all modules**: In startKoin() block
- [ ] **Platform modules last**: Override bindings per platform
- [ ] **No annotations needed**: Pure Kotlin, no @Inject/@Provides

**File location:** `shared/di/src/commonMain/kotlin/`

---

## ⚡ Async & Reactive (Coroutines + Flow)

When handling async operations:

- [ ] **Use `suspend` for one-shot operations**: `suspend fun insertExpense()`
- [ ] **Use `Flow<T>` for streaming data**: `fun getExpenses(): Flow<List<Expense>>`
- [ ] **Wrap in `FlowResult<T>`**: `Flow<Result<T>>` for complete async handling
- [ ] **Use `.flowOn(Dispatchers.Default)`**: Specify thread for DB/network
- [ ] **Use `viewModelScope`**: Auto-cancels on ViewModel destruction
- [ ] **Use `.onSuccess()/.onError()`**: Handle Result states
- [ ] **Use `runTest {}`** for testing coroutines
- [ ] **No blocking operations**: Always use suspend functions

---

## 🗄️ Database (SQLDelight)

When working with SQLDelight:

- [ ] **Write SQL queries in .sq files**: Strong-typed generated queries
- [ ] **Create index on date columns**: For efficient range queries
- [ ] **Use parameterized queries**: `WHERE id = :id` (SQL injection safe)
- [ ] **Return `Flow<T>` from queries**: For reactive updates
- [ ] **Use transactions**: For multiple operations
- [ ] **Implement `expect/actual` drivers**: For platform-specific SQLite drivers
- [ ] **Convert DB rows to domain models**: Use mapper extensions
- [ ] **Store money as Long**: Integer cents, not decimal

**File locations:**
- Queries: `shared/data/src/commonMain/sqldelight/`
- Generated: Auto-generated in build output
- Driver: `shared/data/src/[platform]Main/kotlin/`

---

## 🎨 Compose UI Layer

When building UI:

- [ ] **Use sealed class for UI state**: `HomeUiState.Loading`, `.Success`, `.Error`
- [ ] **Use StateFlow in ViewModel**: `MutableStateFlow<UiState>` → exposed `StateFlow`
- [ ] **Collect state in Compose**: `val state by viewModel.state.collectAsState()`
- [ ] **Use @Composable functions**: For reusable UI components
- [ ] **Extract small composables**: For performance and reusability
- [ ] **Use LaunchedEffect**: For side effects (data loading)
- [ ] **Keep ViewModels pure Kotlin**: No Compose imports
- [ ] **Test Composables**: Use `createComposeRule()`

**File locations:**
- ViewModels: `shared/viewmodel/src/commonMain/kotlin/`
- Screens: `shared/ui/src/commonMain/kotlin/`
- Components: `shared/ui/src/commonMain/kotlin/common/`

---

## 📦 Domain Modeling

When defining business logic:

- [ ] **Use immutable data classes**: `val`, not `var`
- [ ] **Create Value Objects**: `Amount`, `DateRange` with logic
- [ ] **Validate in constructors**: `init { validate(...) }`
- [ ] **Overload operators**: `Amount + Amount`, `Amount * Number`
- [ ] **Use sealed classes for types**: `Result<T>`, state enums
- [ ] **Store money as Long (cents)**: No floating-point errors
- [ ] **No framework dependencies**: Pure Kotlin only
- [ ] **Document business rules**: Comments for non-obvious logic

**File location:** `shared/domain/src/commonMain/kotlin/`

---

## 🧪 Testing (KMP)

When writing tests:

- [ ] **Test behavior, not implementation**: "What" not "how"
- [ ] **Mock external dependencies**: Use `@RelaxedMockK`
- [ ] **Use `runTest {}`** for coroutines
- [ ] **Use in-memory database**: For repository tests
- [ ] **Follow AAA pattern**: Arrange → Act → Assert
- [ ] **Test error cases**: Not just happy path
- [ ] **Test Composables**: Use `createComposeRule()`
- [ ] **Verify with `coVerify`**: For suspend functions

**File locations:**
- Shared tests: `shared/*/src/commonTest/`
- Platform tests: `androidApp/src/test/`, `iosApp/src/test/`

---

## 🛠️ KMP-Specific Patterns

When using KMP features:

- [ ] **Use `expect/actual`** for platform differences
  ```kotlin
  // commonMain
  expect class FileManager
  
  // androidMain
  actual class FileManager { ... }
  
  // iosMain
  actual class FileManager { ... }
  ```

- [ ] **Use `@HotReload`** for development
- [ ] **Multiplatform objects**: Use `@Serializable` for data classes
- [ ] **Resource access**: Use expect/actual, not context
- [ ] **Date/Time**: Use `kotlinx-datetime` (KMP compatible)
- [ ] **Testing**: Same tests run on all platforms

---

## 📝 Code Style

When writing code:

- [ ] **Use type-based suffixes**: `ViewModel`, `Repository`, `Mapper`
- [ ] **PascalCase** for classes
- [ ] **UPPER_CASE** for constants
- [ ] **Prefer `val`** over `var`
- [ ] **Private mutable with underscore**: `private val _state = MutableStateFlow()`
- [ ] **Data classes** for models
- [ ] **Sealed classes** for type-safe alternatives
- [ ] **Document WHY**: Not WHAT

---

## 🚀 Security

When handling data:

- [ ] **No hardcoded secrets**: Use config, environment variables, or BuildConfig
- [ ] **Load from properties**: API keys in BuildConfig/plist
- [ ] **Validate all input**: URIs, file sizes, user input
- [ ] **Never log PII**: No emails, passwords, tokens
- [ ] **Use parameterized SQL**: Prevent SQL injection
- [ ] **Expect/Actual for secure storage**: KeyStore/Keychain per platform
- [ ] **No sensitive data in logs**: Only safe IDs

---

## 📊 Logging

When adding logging:

- [ ] **Use `Logger` expect/actual**: Platform-specific implementations
- [ ] **Log in ViewModels**: State changes
- [ ] **Log in Repositories**: Data operations
- [ ] **No sensitive data**: Only log safe information
- [ ] **Appropriate log levels**: `d()` for debug, `e()` for errors

---

## 📋 New Feature Checklist

### When Adding a New Feature

1. **Plan Architecture**
   - [ ] Design domain models
   - [ ] Plan data layer (repository, SQL queries)
   - [ ] Plan ViewModel and UI state
   - [ ] Plan Compose screens

2. **Implement Domain Layer** (start here)
   - [ ] Create value objects
   - [ ] Create domain entities
   - [ ] Add validation
   - [ ] Create filters/models if needed

3. **Implement Data Layer**
   - [ ] Write SQL queries (.sq file)
   - [ ] Create Repository interface
   - [ ] Implement repository
   - [ ] Create mappers (DB → Domain)

4. **Implement ViewModel**
   - [ ] Create ViewModel class
   - [ ] Define UI State sealed class
   - [ ] Create state management methods
   - [ ] Create mappers (Domain → UI)

5. **Implement UI Layer**
   - [ ] Create Compose screens
   - [ ] Create reusable components
   - [ ] Wire state collection
   - [ ] Wire navigation

6. **Dependency Injection**
   - [ ] Add DI module entries
   - [ ] Bind repository to interface
   - [ ] Register ViewModels
   - [ ] Register mappers if needed

7. **Testing**
   - [ ] Write domain tests
   - [ ] Write repository tests (mocked/in-memory DB)
   - [ ] Write ViewModel tests
   - [ ] Write Compose UI tests

8. **Security & Logging**
   - [ ] Add Logger calls
   - [ ] Validate external input
   - [ ] No sensitive data logging

9. **Final Checks**
   - [ ] Run `./gradlew testCommonTest`
   - [ ] No Android imports in commonMain
   - [ ] No expect/actual logic in commonMain
   - [ ] Commit with clear message

---

## 🐛 Debugging Checklist

### When Something Breaks

1. **Check test failure message** - Tests are the source of truth
2. **Check commonMain imports** - No `android.*` or `UIKit`
3. **Check expect/actual** - Is implementation present for all platforms?
4. **Check SQL query** - Is the SQLDelight query correct?
5. **Check repository logic** - Is error handling right?
6. **Check ViewModel state** - Is state being set correctly?
7. **Check Compose collection** - Is `collectAsState()` called?
8. **Check DI binding** - Is implementation registered?

---

## ⚠️ Common KMP Mistakes

| Mistake | Problem | Solution |
|---------|---------|----------|
| Android imports in shared | Doesn't compile on iOS/Web | Use expect/actual |
| Direct DAO in ViewModel | Can't test, ties to platform | Use Repository interface |
| Mutable state in Composables | Recomposes unpredictably | Use StateFlow in ViewModel |
| Blocking DB calls | Freezes UI | Use suspend functions |
| No input validation | Crashes/exploits | Validate all external data |
| Hardcoded secrets | Security breach | Use BuildConfig/plist |
| Forgetting migrations | Data loss | Add SQLDelight migrations |
| No platform modules | Can't override implementations | Use androidMain, iosMain |

---

## 📚 Quick Links

- **01_ARCHITECTURE_PATTERNS.md** - MVVM, Repository, multimodule
- **02_DEPENDENCY_INJECTION.md** - Koin setup, modules
- **03_REACTIVE_PROGRAMMING.md** - Coroutines, Flow
- **04_DATABASE_PERSISTENCE.md** - SQLDelight setup
- **05_UI_LAYER.md** - Compose, StateFlow, navigation
- **06_DOMAIN_MODELING.md** - Value objects, validation
- **07_TESTING_GUIDELINES.md** - Unit and integration tests
- **09_CODE_CONVENTIONS.md** - Naming, organization
- **10_LOGGING_SECURITY.md** - Logging, expect/actual, security
- **11_CICD_WORKFLOW.md** - Build, versions, deployment

---

## 🎯 Golden Rule

**Code should be so clear that the next person reading it asks "why was this built this way?" not "what does this code do?"**

---

## 📞 Questions?

1. Check the relevant guide in `/doc/`
2. Look at existing code for patterns
3. Ask team members
4. Check tests for usage examples

---

**Last Updated:** June 2026  
**ProExpense KMP Version:** 1.0.0  
**Architecture:** Multi-Module KMP with Compose
