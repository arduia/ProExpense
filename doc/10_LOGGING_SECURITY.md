# Logging & Security - KMP Best Practices

## Overview

ProExpense uses platform-specific logging via the **expect/actual pattern** - a single logging interface implemented differently on each platform (iOS uses os.log, Android uses Timber, etc.).

---

## KMP Logging with Expect/Actual

### Shared Logger Interface

**File:** `shared/utils/src/commonMain/kotlin/Logger.kt`

```kotlin
expect object Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, exception: Throwable? = null)
    fun w(tag: String, message: String)
}
```

### Android Implementation

**File:** `shared/utils/src/androidMain/kotlin/Logger.kt`

```kotlin
actual object Logger {
    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
    
    actual fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }
    
    actual fun e(tag: String, message: String, exception: Throwable?) {
        if (exception != null) {
            Timber.tag(tag).e(exception, message)
        } else {
            Timber.tag(tag).e(message)
        }
    }
    
    actual fun w(tag: String, message: String) {
        Timber.tag(tag).w(message)
    }
}
```

### iOS Implementation

**File:** `shared/utils/src/iosMain/kotlin/Logger.kt`

```kotlin
actual object Logger {
    actual fun d(tag: String, message: String) {
        os_log(.debug, log: OSLog.default, "%{public}@: %{public}@", tag, message)
    }
    
    actual fun e(tag: String, message: String, exception: Throwable?) {
        let errorMsg = exception?.message ?? message
        os_log(.error, log: OSLog.default, "%{public}@: %{public}@", tag, errorMsg)
    }
    
    actual fun w(tag: String, message: String) {
        os_log(.default, log: OSLog.default, "%{public}@: %{public}@", tag, message)
    }
}
```

### Usage in Shared Code

```kotlin
class ExpenseRepository(private val database: ExpenseDatabase) {
    
    suspend fun insertExpense(expense: Expense) {
        Logger.d("ExpenseRepo", "Inserting expense: ${expense.name}")
        database.expenseQueries.insert(...)
        Logger.d("ExpenseRepo", "Inserted successfully")
    }
    
    suspend fun deleteExpense(id: Int) {
        try {
            database.expenseQueries.delete(id)
            Logger.d("ExpenseRepo", "Deleted expense $id")
        } catch (e: Exception) {
            Logger.e("ExpenseRepo", "Failed to delete", e)
            throw RepositoryException(e)
        }
    }
}
```

---

## Logging Best Practices

### Strategic Logging Locations

**ViewModels - State Changes:**
```kotlin
class HomeViewModel(private val repository: ExpenseRepository) {
    fun loadExpenses() {
        viewModelScope.launch {
            Logger.d("HomeViewModel", "Loading expenses...")
            
            repository.getRecentExpense()
                .onSuccess { expenses ->
                    Logger.d("HomeViewModel", "Loaded ${expenses.size} expenses")
                    _uiState.value = HomeUiState.Success(expenses)
                }
                .onError { error ->
                    Logger.e("HomeViewModel", "Failed to load", error)
                    _uiState.value = HomeUiState.Error(error.message)
                }
        }
    }
}
```

**Repositories - Data Operations:**
```kotlin
class ExpenseRepository(private val database: ExpenseDatabase) {
    
    fun getExpenseAll(): FlowResult<List<Expense>> {
        return database.expenseQueries.selectAll()
            .asFlow()
            .onEach { Logger.d("ExpenseRepo", "Loaded ${it.size} expenses") }
            .map { Result.Success(it.map(Mapper::toExpense)) }
            .catch { 
                Logger.e("ExpenseRepo", "Error loading expenses", it)
                emit(Result.Error(it as Exception)) 
            }
    }
}
```

---

## Security Best Practices

### No Sensitive Data Logging

```kotlin
// BAD - Logs user PII
Logger.d("Auth", "User ${user.email} logged in")

// GOOD - Log only safe information
Logger.d("Auth", "User logged in: ${user.id}")

// BAD - Logs API responses with sensitive data
Logger.d("Network", "Response: $response")

// GOOD - Log only status
Logger.d("Network", "API request succeeded: ${response.code()}")

// BAD - Full exception details in production
Logger.e("Error", "Exception occurred", exception)

// GOOD - Minimal info in production
if (isDevelopment) {
    Logger.e("Error", "Exception occurred", exception)
} else {
    Logger.e("Error", "An error occurred")
}
```

### Input Validation

```kotlin
suspend fun importExpenses(uri: String): Result<List<Expense>> {
    // Always validate external input
    if (!isValidUri(uri)) {
        Logger.w("Import", "Invalid import URI provided")
        return Result.Error(Exception("Invalid URI"))
    }
    
    try {
        val expenses = parseExpensesFromFile(uri)
        Logger.d("Import", "Successfully imported ${expenses.size} expenses")
        return Result.Success(expenses)
    } catch (e: Exception) {
        Logger.e("Import", "Import failed", e)
        return Result.Error(e)
    }
}

private fun isValidUri(uri: String): Boolean {
    return uri.isNotBlank()
        && (uri.startsWith("file://") || uri.startsWith("content://"))
        && uri.endsWith(".csv")
}
```

### API Keys & Secrets

Load secrets from environment or configuration, never hardcode:

```kotlin
// shared/data/src/commonMain/kotlin/ApiConfig.kt
expect object ApiConfig {
    val apiBaseUrl: String
    val apiKey: String
}

// shared/data/src/androidMain/kotlin/ApiConfig.kt
actual object ApiConfig {
    actual val apiBaseUrl: String = BuildConfig.API_BASE_URL
    actual val apiKey: String = BuildConfig.API_KEY
}

// shared/data/src/iosMain/kotlin/ApiConfig.kt
actual object ApiConfig {
    actual val apiBaseUrl: String = Bundle.main.infoDictionary?["API_BASE_URL"] as? String ?: ""
    actual val apiKey: String = Bundle.main.infoDictionary?["API_KEY"] as? String ?: ""
}

// Usage
val httpClient = HttpClient {
    defaultRequest {
        url(ApiConfig.apiBaseUrl)
        headers["Authorization"] = "Bearer ${ApiConfig.apiKey}"
    }
}
```

### SQL Injection Prevention

SQLDelight prevents SQL injection by using parameterized queries:

```sql
-- GOOD - Parameterized query
selectByDateRange:
SELECT * FROM expense 
WHERE created_date BETWEEN :startTime AND :endTime 
ORDER BY created_date DESC;

-- BAD - Never concatenate queries (not possible with SQLDelight)
-- SELECT * FROM expense WHERE id = $id
```

### Data Privacy

Never store unnecessary sensitive data:

```kotlin
// BAD - Stores sensitive user data
@Entity
data class UserEnt(
    val userId: Int,
    val email: String,  // Sensitive
    val password: String,  // NEVER store this
    val phone: String  // Sensitive
)

// GOOD - Only store necessary data
@Entity
data class UserEnt(
    val userId: Int,
    val isAuthenticated: Boolean  // Not sensitive
)
```

---

## Log Levels

### When to Use Each Level

```kotlin
// Verbose - Detailed technical information (rarely used)
Logger.d("DetailedTag", "Variable x = $x, computed y = $y")

// Debug - Development information (debug builds)
Logger.d("MyFeature", "Loaded 50 items from database")

// Info - Important business events (keep in production)
Logger.d("Auth", "User session started")

// Warning - Potential problems (production warning)
Logger.w("Database", "Query took ${duration}ms (expected < 100ms)")

// Error - Failures (with exceptions)
Logger.e("Network", "Request failed with timeout", exception)

// Assert - Should never happen (critical bugs)
Logger.e("Validation", "Amount cannot be negative: $amount")
```

---

## Best Practices

### ✅ DO:

1. **Use expect/actual for platform-specific logging**
   ```kotlin
   expect object Logger { ... }
   actual object Logger { ... }  // per platform
   ```

2. **Validate all external input**
   ```kotlin
   if (!isValidUri(uri)) return error()
   ```

3. **Protect sensitive data**
   ```kotlin
   // Log IDs not emails
   Logger.d("Auth", "User ${user.id}")
   ```

4. **Use appropriate log levels**
   ```kotlin
   Logger.d("tag", "debug info")
   Logger.e("tag", "error", exception)
   ```

5. **Load secrets from config**
   ```kotlin
   val key = ApiConfig.apiKey
   ```

### ❌ DON'T:

1. **Log sensitive data**
   ```kotlin
   // BAD
   Logger.d("Auth", "Password: ${password}")
   ```

2. **Hardcode secrets**
   ```kotlin
   // BAD
   const val API_KEY = "secret123"
   ```

3. **Log full exceptions in production**
   ```kotlin
   // BAD
   Logger.e("Error", exception.stackTrace.toString())
   ```

4. **Skip input validation**
   ```kotlin
   // BAD
   val uri = Uri.parse(userInput)  // No validation
   ```

5. **Store passwords in database**
   ```kotlin
   // BAD
   @Entity data class User(val password: String)
   ```

---

## KMP Security Best Practices

✅ **Expect/Actual for secure storage** - Different per platform  
✅ **Never hardcode secrets** - Use config/environment  
✅ **Validate all external input** - Files, URIs, user data  
✅ **Don't log sensitive data** - User IDs only, not emails  
✅ **Use platform-native security** - KeyStore on Android, Keychain on iOS  

**Key: Security is not optional - build it in from the start**
