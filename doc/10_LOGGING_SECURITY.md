# Logging & Security - ProExpense Best Practices

## Logging with Timber

### Why Timber?

✅ **Conditional Logging**: Different behavior for debug vs release  
✅ **Pluggable**: Easy to swap implementations (file logging, Crashlytics, etc.)  
✅ **Tag Management**: Automatic source location tracking  
✅ **Clean API**: `Timber.d()`, `Timber.e()` instead of `Log.d()`  

---

## Setup

**File:** `/app/src/main/java/com/arduia/expense/ExpenseApplication.kt`

```kotlin
class ExpenseApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize DI
        // ...
        
        // Initialize Timber
        if (BuildConfig.DEBUG) {
            // Debug logging enabled in development
            Timber.plant(Timber.DebugTree())
        } else {
            // Production: Silent or use Crashlytics
            // Timber.plant(CrashlyticsTree())
        }
    }
}
```

### Debug vs Release

```kotlin
if (BuildConfig.DEBUG) {
    // Expensive logging, verbose output
    Timber.plant(Timber.DebugTree())
} else {
    // Silent or minimal (protect user privacy)
    // Timber.plant(CrashlyticsTree())
}
```

---

## Strategic Logging Locations

### 1. ViewModels - State Changes

**File:** `/app/src/main/java/com/arduia/expense/ui/home/HomeViewModel.kt`

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    
    fun loadExpenses() {
        viewModelScope.launch {
            Timber.d("Loading expenses...")
            
            expenseRepository.getRecentExpense()
                .onSuccess { expenses ->
                    Timber.d("Loaded ${expenses.size} expenses")
                    _uiState.value = HomeUiState.Success(expenses)
                }
                .onError { error ->
                    Timber.e(error, "Failed to load expenses")
                    _uiState.value = HomeUiState.Error(error.message)
                }
        }
    }
}
```

### 2. Repositories - Data Operations

**File:** `/app/src/main/java/com/arduia/expense/data/ExpenseRepositoryImpl.kt`

```kotlin
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {
    
    override suspend fun insertExpense(expenseEnt: ExpenseEnt) {
        withContext(Dispatchers.IO) {
            try {
                Timber.d("Inserting expense: ${expenseEnt.name}")
                expenseDao.insert(expenseEnt)
                Timber.d("Expense inserted successfully")
            } catch (e: Exception) {
                Timber.e(e, "Failed to insert expense")
                throw RepositoryException(e)
            }
        }
    }
    
    override fun getExpenseAll(): FlowResult<List<ExpenseEnt>> {
        return expenseDao.getExpenseAll()
            .onEach { list ->
                Timber.d("Retrieved ${list.size} expenses from database")
            }
            .map { SuccessResult(it) }
            .catch { 
                Timber.e(it, "Error retrieving expenses")
                ErrorResult(RepositoryException(it)) 
            }
    }
}
```

### 3. Workers - Background Tasks

**File:** `/app/src/main/java/com/arduia/expense/data/backup/ImportWorker.kt`

```kotlin
@HiltWorker
class ImportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val expenseRepository: ExpenseRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            Timber.d("Starting import work...")
            
            val importUri = inputData.getString("import_uri") ?: return Result.failure()
            Timber.d("Import URI: $importUri")
            
            val expenses = backupRepository.importExpenses(importUri)
            Timber.d("Parsed ${expenses.size} expenses from file")
            
            expenseRepository.insertExpenseAll(expenses)
            Timber.d("Successfully imported and stored expenses")
            
            Result.success(workDataOf("import_count" to expenses.size))
        } catch (e: Exception) {
            Timber.e(e, "Import failed")
            Result.retry()
        }
    }
}
```

---

## Log Levels

### Timber Log Levels

```kotlin
// Verbose - detailed information for debugging
Timber.v("User action: expense deleted")

// Debug - development debugging
Timber.d("Loaded ${expenses.size} expenses from database")

// Info - important business events
Timber.i("User changed language to Spanish")

// Warning - potential problems
Timber.w("Database query took ${duration}ms")

// Error - failures (with exceptions)
Timber.e(exception, "Failed to import file")

// Assert - should never happen
Timber.wtf("Invalid state reached: ${expense.amount} < 0")
```

---

## Security Best Practices

### 1. No Sensitive Data Logging

```kotlin
// BAD - Logs user PII
Timber.d("User ${user.email} logged in")

// GOOD - Log only IDs
Timber.d("User logged in: ${user.id}")

// BAD - Logs API responses with sensitive data
Timber.d("API response: $response")

// GOOD - Log only status
Timber.d("API request succeeded: ${response.code()}")

// BAD - Logs full exceptions with stack traces in production
Timber.e(exception, "Network error")

// GOOD - Minimal info in production
if (BuildConfig.DEBUG) {
    Timber.e(exception, "Network error")
} else {
    Timber.e("Network error occurred")
}
```

### 2. Input Validation

```kotlin
class ImportWorker @AssistedInject constructor(...) : CoroutineWorker(...) {
    
    override suspend fun doWork(): Result {
        return try {
            // Always validate external input
            val importUri = inputData.getString("import_uri") 
                ?: return Result.failure()
            
            val uri = Uri.parse(importUri)
            if (!isValidUri(uri)) {
                Timber.e("Invalid import URI provided")
                return Result.failure()
            }
            
            // Safe to use validated URI
            val expenses = backupRepository.importExpenses(uri)
            // ...
        } catch (e: Exception) {
            Timber.e(e, "Import failed")
            Result.retry()
        }
    }
    
    private fun isValidUri(uri: Uri): Boolean {
        return uri.scheme in listOf("file", "content")
            && uri.lastPathSegment?.endsWith(".xlsx") == true
    }
}
```

### 3. API Keys & Secrets

**Load from external file (never hardcode):**

**File:** `api.properties` (in gitignore)
```properties
API_KEY=your_secret_key_here
API_BASE_URL=https://api.example.com
```

**In build.gradle.kts:**
```kotlin
val apiProperties = Properties().apply {
    load(FileInputStream(rootProject.file("api.properties")))
}

android {
    defaultConfig {
        buildConfigField("String", "API_KEY", "\"${apiProperties.getProperty("API_KEY")}\"")
        buildConfigField("String", "API_BASE_URL", "\"${apiProperties.getProperty("API_BASE_URL")}\"")
    }
}
```

**Usage:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideApiService(): ExpenseApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return retrofit.create(ExpenseApi::class.java)
    }
}
```

### 4. ProGuard Configuration

**File:** `/app/proguard-rules.pro`

```proguard
# Preserve database entities for reflection
-keep class com.arduia.expense.data.local.** { *; }

# Preserve ViewModel constructors for Hilt
-keep class com.arduia.expense.ui.**ViewModel { 
    public <init>(***); 
}

# Preserve Worker constructors for Hilt
-keep class com.arduia.expense.data.**Worker { 
    public <init>(android.content.Context, androidx.work.WorkerParameters); 
}

# Preserve models used in Room
-keep class com.arduia.expense.data.local.** { *; }

# Preserve serialization classes
-keep class com.google.gson.** { *; }

# Keep line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
```

---

## Data Handling

### Database Security

```kotlin
@Database(
    entities = [ExpenseEnt::class, BackupEnt::class],
    version = 6
)
abstract class ProExpenseDatabase : RoomDatabase() {
    // No sensitive data in Room
    // All financial data is properly encrypted by Android OS
}

// Good: Parameterized queries prevent SQL injection
@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: Int): ExpenseEnt?  // Parameter binding
    
    // Bad: String interpolation (don't do this)
    // @Query("SELECT * FROM expenses WHERE id = $id")
}
```

### File Handling

```kotlin
// Validate before processing files
fun isValidExpenseFile(uri: Uri): Boolean {
    return uri.scheme in listOf("file", "content")
        && uri.lastPathSegment?.endsWith(".xlsx") == true
        && getFileSize(uri) < MAX_FILE_SIZE  // 10MB limit
}

// Use ContentResolver for safer file access
val inputStream = context.contentResolver.openInputStream(uri)
    ?: throw IOException("Cannot open file")

// Read in chunks, not all at once
val buffer = ByteArray(8192)
var bytesRead: Int
while (inputStream.read(buffer).also { bytesRead = it } != -1) {
    processChunk(buffer.copyOf(bytesRead))
}
```

### User Data Privacy

```kotlin
// Don't log user PII
// BAD
Timber.d("User email: ${user.email}, Phone: ${user.phone}")

// GOOD
Timber.d("User loaded successfully")

// Don't store unnecessary data
@Entity(tableName = "users")
data class UserEnt(
    @PrimaryKey val userId: Int,
    val name: String,  // Only what's necessary
    // No sensitive data
)

// Encrypt sensitive fields
@Entity(tableName = "backup_keys")
data class BackupKeyEnt(
    @PrimaryKey val keyId: String,
    @ColumnInfo(name = "encrypted_key")
    val encryptedKey: String  // Encrypted in DB
)
```

---

## Testing Security

### Test for Input Validation

```kotlin
@Test
fun testImportValidatesUri() {
    // Should reject invalid URIs
    val worker = ImportWorker(context, params)
    
    val invalidUri = "invalid://uri"
    val result = worker.importExpenses(invalidUri)
    
    assert(result is Result.Failure)
}

@Test
fun testImportRejectsLargeFiles() {
    // Should reject files over size limit
    val largeFile = createFile(MAX_FILE_SIZE + 1)
    
    val result = worker.importExpenses(largeFile.uri)
    
    assert(result is Result.Failure)
}
```

---

## Best Practices

### ✅ DO:

1. **Use Timber** for all logging
   ```kotlin
   Timber.d("message")
   Timber.e(exception, "error")
   ```

2. **Validate external input**
   ```kotlin
   if (!isValidUri(uri)) return Result.failure()
   ```

3. **Protect sensitive data**
   ```kotlin
   // Don't log emails, passwords, tokens
   Timber.d("User authenticated")
   ```

4. **Use parameterized queries**
   ```kotlin
   @Query("WHERE id = :id")  // Safe
   ```

5. **Load secrets from properties**
   ```kotlin
   API_KEY=value  // In gitignore
   ```

### ❌ DON'T:

1. **Log sensitive data**
   ```kotlin
   // BAD
   Timber.d("User: ${user.email}, Password: ${password}")
   ```

2. **Hardcode secrets**
   ```kotlin
   // BAD
   const val API_KEY = "secret123"
   ```

3. **Use string interpolation in SQL**
   ```kotlin
   // BAD
   @Query("SELECT * FROM expenses WHERE id = $id")
   ```

4. **Log full exceptions in production**
   ```kotlin
   // BAD - exposes internals
   Timber.e(exception, "Error: ${exception.stackTrace}")
   ```

5. **Skip input validation**
   ```kotlin
   // BAD
   val uri = Uri.parse(userInput)  // No validation
   ```

---

## Reuse in New Architecture

✅ **Timber logging** strategy applies to all code  
✅ **Security practices** are framework-agnostic  
✅ **Input validation** principles remain constant  
✅ **Data privacy** rules don't change with architecture  
✅ **ProGuard rules** carry forward without modification

**Key: Security is not an afterthought - build it in from the start**
