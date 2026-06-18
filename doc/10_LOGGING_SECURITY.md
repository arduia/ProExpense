# Logging & Security - Best Practices

## Overview

Security and logging best practices are **independent of tools and frameworks**. These principles apply to any architecture or implementation.

---

## Logging Best Practices

### Strategic Logging Locations

**Log at boundaries and important state changes:**

1. **ViewModel State Changes**
   ```kotlin
   class HomeViewModel(...) {
       fun loadExpenses() {
           log("Loading expenses")
           // ... load data
           log("Loaded ${expenses.size} expenses")
       }
   }
   ```

2. **Repository Data Operations**
   ```kotlin
   class ExpenseRepository(...) {
       fun insertExpense(expense: Expense) {
           log("Inserting expense: ${expense.name}")
           // ... insert
           log("Insert successful")
       }
   }
   ```

3. **Error Handling**
   ```kotlin
   fun riskyOperation() {
       try {
           doWork()
           log("Operation succeeded")
       } catch (e: Exception) {
           logError("Operation failed", e)
           // Handle error
       }
   }
   ```

### Log Levels

Use appropriate levels for different information:

```kotlin
// Debug - Detailed development information
log.debug("Variable x = $x, computed y = $y")

// Info - Important business events  
log.info("User session started")

// Warning - Potential problems
log.warning("Database query took ${duration}ms")

// Error - Failures with exceptions
log.error("Request failed", exception)
```

### NO Sensitive Data in Logs

```kotlin
// ❌ BAD - Logs PII
log("User ${user.email} logged in")

// ✅ GOOD - Only safe information
log("User ${user.id} logged in")

// ❌ BAD - Logs full response with secrets
log("API response: $response")

// ✅ GOOD - Only status
log("API request succeeded: ${response.statusCode()}")

// ❌ BAD - Logs full exception in production
log.error("Error occurred", exception)

// ✅ GOOD - Conditional detail
if (isDevelopment) {
    log.error("Error occurred", exception)
} else {
    log.error("An error occurred")
}
```

---

## Security Best Practices

### 1. Never Hardcode Secrets

```kotlin
// ❌ BAD - Secrets in code
const val API_KEY = "sk_live_abc123xyz"
const val DB_PASSWORD = "securePassword123"

// ✅ GOOD - Load from config/environment
val apiKey = getConfigValue("API_KEY")
val dbPassword = getEnvironmentVariable("DB_PASSWORD")
```

### 2. Validate External Input

```kotlin
// ❌ BAD - No validation
fun importData(uri: String) {
    val data = readFile(uri)
    processData(data)
}

// ✅ GOOD - Validate first
fun importData(uri: String): Result<Unit> {
    if (!isValidUri(uri)) {
        return Result.Error("Invalid URI")
    }
    
    if (getFileSize(uri) > MAX_SIZE) {
        return Result.Error("File too large")
    }
    
    val data = readFile(uri)
    return processData(data)
}

private fun isValidUri(uri: String): Boolean {
    return uri.isNotBlank() &&
           (uri.startsWith("file://") || uri.startsWith("content://")) &&
           uri.endsWith(".csv")
}
```

### 3. Use Result Type for Error Handling

```kotlin
// ❌ BAD - Throws exceptions
fun getUser(id: Int): User {
    if (id <= 0) throw IllegalArgumentException()
    return database.getUser(id)  // Can throw
}

// ✅ GOOD - Returns Result
fun getUser(id: Int): Result<User> {
    if (id <= 0) {
        return Result.Error("Invalid user ID")
    }
    
    return try {
        Result.Success(database.getUser(id))
    } catch (e: Exception) {
        Result.Error(e)
    }
}
```

### 4. Input Validation at Boundaries

```kotlin
// ✅ GOOD - Validate at entry points
interface ExpenseRepository {
    fun insertExpense(expense: Expense): Result<Unit>
}

class ExpenseRepositoryImpl(...) : ExpenseRepository {
    override fun insertExpense(expense: Expense): Result<Unit> {
        // Validate immediately
        if (expense.amount < 0) {
            return Result.Error("Amount cannot be negative")
        }
        
        if (expense.name.isBlank()) {
            return Result.Error("Name cannot be empty")
        }
        
        // Safe to proceed
        return tryInsert(expense)
    }
}
```

### 5. Parameterized Queries (No String Concatenation)

```kotlin
// ❌ BAD - SQL injection risk
fun getExpense(id: Int) {
    return database.query("SELECT * FROM expense WHERE id = $id")
}

// ✅ GOOD - Parameterized
fun getExpense(id: Int) {
    return database.query("SELECT * FROM expense WHERE id = ?", listOf(id))
    // Or with named parameters
    return database.query("SELECT * FROM expense WHERE id = :id", mapOf("id" to id))
}
```

### 6. Data Privacy - Store Only What's Needed

```kotlin
// ❌ BAD - Stores sensitive information
data class User(
    val id: Int,
    val email: String,      // Sensitive
    val passwordHash: String, // NEVER store
    val phone: String       // Sensitive
)

// ✅ GOOD - Only necessary fields
data class User(
    val id: Int,
    val isAuthenticated: Boolean
)
```

### 7. Principle of Least Privilege

```kotlin
// ❌ BAD - Function can do too much
class AdminService {
    fun deleteAllUsers() { /* ... */ }
    fun modifySystemConfig() { /* ... */ }
}

// ✅ GOOD - Limited responsibility
interface UserRepository {
    fun deleteUser(id: Int): Result<Unit>
}

interface SystemConfig {
    fun getConfig(key: String): String
}
```

---

## Common Security Mistakes

| Mistake | Risk | Prevention |
|---------|------|-----------|
| Hardcoded secrets | Exposed in code repo | Use environment variables |
| No input validation | Invalid/malicious data | Validate at boundaries |
| Throwing exceptions | Leaks stack traces | Use Result type |
| Logging PII | Privacy breach | Only log safe data |
| String concatenation SQL | SQL injection | Use parameterized queries |
| Storing passwords | Account compromise | Never store, hash if needed |
| No API authentication | Unauthorized access | Require auth tokens |
| Trusting user input | Exploits possible | Always validate |

---

## Security Validation Checklist

Before committing code:

- [ ] **No hardcoded secrets**: API keys, passwords, tokens in code?
- [ ] **External input validated**: All user/file input checked?
- [ ] **No sensitive logging**: PII in logs?
- [ ] **Errors handled safely**: Exceptions or Result type?
- [ ] **Queries parameterized**: Any string concatenation in queries?
- [ ] **Data stored minimally**: Only what's necessary?
- [ ] **Access controlled**: Permission checks in place?
- [ ] **Dependencies updated**: Known vulnerabilities patched?

---

## Principles That Carry Forward

✅ **Never hardcode secrets** - Always load from config  
✅ **Validate external input** - Don't trust user data  
✅ **Log strategically** - Help debugging without leaking secrets  
✅ **Use Result types** - Explicit error handling  
✅ **Parameterize queries** - Prevent injection attacks  
✅ **Store minimally** - Only what's necessary  
✅ **Principle of least privilege** - Limited responsibilities  

**Key**: Security must be built in from the start, not added later.
