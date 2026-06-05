# Database & Persistence (SQLDelight) - KMP Best Practices

## Overview

ProExpense uses **SQLDelight** - a SQL-based database library that works seamlessly across KMP platforms (iOS, Android, Web). SQLDelight generates type-safe Kotlin code from SQL queries.

---

## Why SQLDelight for KMP?

✅ **Cross-Platform**: Works on iOS, Android, and Web  
✅ **Type-Safe**: Generates Kotlin code from SQL queries  
✅ **SQL Native**: Write actual SQL, not ORM abstractions  
✅ **No Code Generation**: Queries generated at compile-time  
✅ **Shared Database Logic**: Single implementation across platforms  

---

## Database Setup

**File:** `shared/data/src/commonMain/sqldelight/com/arduia/expense/Expense.sq`

```sql
CREATE TABLE expense (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  amount INTEGER NOT NULL,  -- Stored in cents (Long)
  category INTEGER NOT NULL,
  note TEXT,
  created_date INTEGER NOT NULL,
  modified_date INTEGER NOT NULL
);

CREATE INDEX idx_created_date ON expense(created_date);
CREATE INDEX idx_modified_date ON expense(modified_date);

-- Queries
selectAll:
SELECT * FROM expense ORDER BY created_date DESC;

selectById:
SELECT * FROM expense WHERE id = :id;

selectByDateRange:
SELECT * FROM expense 
WHERE created_date BETWEEN :startTime AND :endTime 
ORDER BY created_date DESC 
LIMIT :limit OFFSET :offset;

selectCount:
SELECT COUNT(*) FROM expense;

insert:
INSERT INTO expense(name, amount, category, note, created_date, modified_date)
VALUES (?, ?, ?, ?, ?, ?);

update:
UPDATE expense 
SET name = ?, amount = ?, category = ?, note = ?, modified_date = ?
WHERE id = ?;

delete:
DELETE FROM expense WHERE id = ?;

deleteAll:
DELETE FROM expense;
```

---

## Database Driver Setup

**Platform-specific driver configuration:**

### Android

**File:** `shared/data/src/androidMain/kotlin/DatabaseDriver.kt`

```kotlin
actual fun createExpenseDatabase(context: Any): ExpenseDatabase {
    val driver = AndroidSqliteDriver(
        schema = ExpenseDatabase.Schema,
        context = context as Context,
        name = "expense.db",
        onCreate = { it.execSQL("PRAGMA foreign_keys = ON") }
    )
    return ExpenseDatabase(driver)
}
```

### iOS

**File:** `shared/data/src/iosMain/kotlin/DatabaseDriver.kt`

```kotlin
actual fun createExpenseDatabase(context: Any): ExpenseDatabase {
    val driver = NativeSqliteDriver(
        schema = ExpenseDatabase.Schema,
        name = "expense.db",
        onConfigure = { it.execSQL("PRAGMA foreign_keys = ON") }
    )
    return ExpenseDatabase(driver)
}
```

### Web

```kotlin
actual fun createExpenseDatabase(context: Any): ExpenseDatabase {
    val driver = WasmSqliteDriver("expense.db")
    return ExpenseDatabase(driver)
}
```

### Shared

**File:** `shared/data/src/commonMain/kotlin/DatabaseDriver.kt`

```kotlin
expect fun createExpenseDatabase(context: Any): ExpenseDatabase
```

---

## Using Queries in Repositories

**File:** `shared/data/src/commonMain/kotlin/ExpenseRepositoryImpl.kt`

```kotlin
class ExpenseRepositoryImpl(
    private val database: ExpenseDatabase
) : ExpenseRepository {
    
    private val queries = database.expenseQueries
    
    override suspend fun insertExpense(expense: Expense) {
        withContext(Dispatchers.Default) {
            queries.insert(
                name = expense.name,
                amount = expense.amount.storeValue,
                category = expense.category.toLong(),
                note = expense.note,
                created_date = expense.createdDate,
                modified_date = expense.modifiedDate
            )
        }
    }
    
    override fun getExpenseAll(): FlowResult<List<Expense>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows ->
                val expenses = rows.map { it.toExpense() }
                Result.Success(expenses)
            }
            .catch {
                emit(Result.Error(it as Exception))
            }
    }
    
    override fun getExpenseRange(
        startTime: Long,
        endTime: Long,
        limit: Long,
        offset: Long
    ): FlowResult<List<Expense>> {
        return queries.selectByDateRange(startTime, endTime, limit, offset)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows ->
                val expenses = rows.map { it.toExpense() }
                Result.Success(expenses)
            }
            .catch {
                emit(Result.Error(it as Exception))
            }
    }
    
    override suspend fun updateExpense(expense: Expense) {
        withContext(Dispatchers.Default) {
            queries.update(
                id = expense.id.toLong(),
                name = expense.name,
                amount = expense.amount.storeValue,
                category = expense.category.toLong(),
                note = expense.note,
                modified_date = expense.modifiedDate
            )
        }
    }
    
    override suspend fun deleteExpense(id: Int) {
        withContext(Dispatchers.Default) {
            queries.delete(id.toLong())
        }
    }
}

// Extension function to map database row to domain model
private fun ExpenseRow.toExpense(): Expense {
    return Expense(
        id = id.toInt(),
        name = name,
        amount = Amount.createFromStore(amount),
        category = category.toInt(),
        note = note,
        createdDate = created_date,
        modifiedDate = modified_date
    )
}
```

---

## Transactions

SQLDelight supports transactions for multiple operations:

```kotlin
suspend fun importExpenses(expenses: List<Expense>) {
    database.transaction {
        for (expense in expenses) {
            queries.insert(
                name = expense.name,
                amount = expense.amount.storeValue,
                category = expense.category.toLong(),
                note = expense.note,
                created_date = expense.createdDate,
                modified_date = expense.modifiedDate
            )
        }
    }
}
```

---

## Schema Migrations

SQLDelight handles migrations automatically with version management:

**File:** `shared/data/src/commonMain/sqldelight/com/arduia/expense/Expense.sq`

```sql
-- Version 1: Initial schema
CREATE TABLE expense (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  amount INTEGER NOT NULL,
  category INTEGER NOT NULL,
  note TEXT,
  created_date INTEGER NOT NULL,
  modified_date INTEGER NOT NULL
);

-- Version 2: Add backup table
CREATE TABLE backup (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  backup_data TEXT NOT NULL,
  created_date INTEGER NOT NULL
);
```

Configure in `build.gradle.kts`:

```kotlin
sqldelight {
    databases {
        create("ExpenseDatabase") {
            packageName = "com.arduia.expense"
            schemaOutputDirectory = file("src/commonMain/sqldelight")
        }
    }
}
```

---

## Type-Safe Queries

SQLDelight generates type-safe Kotlin interfaces from SQL:

```sql
-- SQL query
selectByCategory:
SELECT * FROM expense WHERE category = :categoryId;

-- Generated Kotlin code
data class SelectByCategory(
    val id: Long,
    val name: String,
    val amount: Long,
    val category: Long,
    val note: String?,
    val created_date: Long,
    val modified_date: Long
)

interface ExpenseQueries {
    fun selectByCategory(categoryId: Long): Query<SelectByCategory>
}
```

**Usage in repository:**

```kotlin
fun getExpensesByCategory(categoryId: Int): FlowResult<List<Expense>> {
    return queries.selectByCategory(categoryId.toLong())
        .asFlow()
        .mapToList(Dispatchers.Default)
        .map { rows ->
            val expenses = rows.map { it.toExpense() }
            Result.Success(expenses)
        }
}
```

---

## Testing with In-Memory Database

```kotlin
class ExpenseRepositoryTest {
    
    private lateinit var database: ExpenseDatabase
    private lateinit var repository: ExpenseRepository
    
    @Before
    fun setUp() {
        // Use in-memory driver for testing
        val driver = InMemoryTestDriver()
        ExpenseDatabase.Schema.create(driver)
        database = ExpenseDatabase(driver)
        repository = ExpenseRepositoryImpl(database)
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun testInsertAndRetrieveExpense() = runTest {
        val expense = Expense(
            id = 1,
            name = "Coffee",
            amount = Amount.createFromStore(100),
            category = 1,
            note = null,
            createdDate = Clock.System.now().toEpochMilliseconds(),
            modifiedDate = Clock.System.now().toEpochMilliseconds()
        )
        
        repository.insertExpense(expense)
        
        val retrieved = repository.getExpenseAll().first()
        
        assert((retrieved as? Result.Success)?.data?.size == 1)
    }
}
```

---

## Best Practices

### ✅ DO:

1. **Use SQL for complex queries**
   ```sql
   SELECT * FROM expense WHERE created_date BETWEEN :start AND :end
   ```

2. **Index frequently queried columns**
   ```sql
   CREATE INDEX idx_created_date ON expense(created_date);
   ```

3. **Use transactions for bulk operations**
   ```kotlin
   database.transaction { ... }
   ```

4. **Return Flow for reactive queries**
   ```kotlin
   queries.selectAll().asFlow()
   ```

5. **Convert database types to domain types**
   ```kotlin
   ExpenseRow.toExpense()  // Database → Domain
   ```

### ❌ DON'T:

1. **Skip type converters for complex types**
   ```kotlin
   // GOOD - store as Long (cents)
   amount INTEGER NOT NULL
   
   // BAD - avoid REAL/FLOAT for money
   amount REAL
   ```

2. **Forget indexes on range queries**
   ```sql
   -- BAD
   SELECT * FROM expense WHERE created_date BETWEEN :a AND :b
   
   -- GOOD
   CREATE INDEX idx_created_date ON expense(created_date);
   ```

3. **Return raw database types**
   ```kotlin
   // BAD
   fun getExpense(): Flow<ExpenseRow>
   
   // GOOD
   fun getExpense(): Flow<Expense>
   ```

---

## KMP Database Best Practices

✅ **Single SQL definition** works across platforms  
✅ **Type-safe queries** generated from SQL  
✅ **Shared repository** logic across all platforms  
✅ **Platform-specific drivers** in `androidMain`, `iosMain`, `webMain`  
✅ **Transactions** for consistency across platforms

**Key: Write SQL once, use on all platforms**
