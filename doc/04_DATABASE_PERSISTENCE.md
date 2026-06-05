# Database & Persistence Layer - ProExpense Best Practices

## Overview

ProExpense uses **Room** - a SQLite abstraction library that provides compile-time SQL verification, type safety, and seamless coroutines integration.

---

## Why Room?

✅ **Type-Safe Queries**: SQL verified at compile-time  
✅ **Coroutine Support**: Native `Flow<T>` queries  
✅ **Migrations**: Handle schema changes across versions  
✅ **Type Converters**: Serialize complex types to primitives  
✅ **Single Source of Truth**: Local database as source of truth  

---

## Database Setup

**File:** `/app/src/main/java/com/arduia/expense/data/local/ProExpenseDatabase.kt`

```kotlin
@Database(
    entities = [ExpenseEnt::class, BackupEnt::class],
    version = 6,  // Current schema version
    exportSchema = true
)
@TypeConverters(AmountTypeConverter::class)
abstract class ProExpenseDatabase : RoomDatabase() {
    
    abstract fun expenseDao(): ExpenseDao
    abstract fun backupDao(): BackupDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun preferenceStorageDao(): PreferenceStorageDao
    
    companion object {
        private var instance: ProExpenseDatabase? = null
        
        @Synchronized
        fun getInstance(context: Context): ProExpenseDatabase {
            return instance ?: Room.databaseBuilder(
                context.applicationContext,
                ProExpenseDatabase::class.java,
                "pro_expense_db"
            )
            .addMigrations(MIGRATION_3_4, MIGRATION_4_6)
            .build()
            .also { instance = it }
        }
    }
}
```

---

## Entity Models

### Naming Convention

Use `*Ent` suffix to distinguish database entities from UI models:
- `ExpenseEnt` - Database entity
- `ExpenseUiModel` - UI presentation model
- `ExpenseDto` - Network data transfer object

### Expense Entity

**File:** `/app/src/main/java/com/arduia/expense/data/local/ExpenseEnt.kt`

```kotlin
@Entity(tableName = "expenses")
data class ExpenseEnt(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val expenseId: Int = 0,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "amount")
    val amount: Amount,  // Custom type - converted via TypeConverter

    @ColumnInfo(name = "category")
    val category: Int,

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "created_date")
    val createdDate: Long,  // Indexed for range queries

    @ColumnInfo(name = "modified_date")
    val modifiedDate: Long
)
```

**Key Points:**
- `@PrimaryKey`: Unique identifier
- `@ColumnInfo`: Maps property to database column
- `autoGenerate`: Database auto-increments ID
- Indexes on `created_date` and `modified_date` for efficient date-range queries

### Type Converters

**File:** `/app/src/main/java/com/arduia/expense/data/local/TypeConverters.kt`

```kotlin
class AmountTypeConverter {
    @TypeConverter
    fun fromAmount(amount: Amount?): Long? {
        return amount?.getStore()
    }

    @TypeConverter
    fun toAmount(value: Long?): Amount? {
        return value?.let { Amount.createFromStore(it) }
    }
}
```

**Usage in Database:**
```kotlin
@Database(
    entities = [...],
    version = 6
)
@TypeConverters(AmountTypeConverter::class)  // Applied to all entities
abstract class ProExpenseDatabase : RoomDatabase()
```

---

## Data Access Objects (DAO)

**Pattern:** One DAO interface per entity

### Expense DAO

**File:** `/app/src/main/java/com/arduia/expense/data/local/ExpenseDao.kt`

```kotlin
@Dao
interface ExpenseDao {
    // INSERT
    @Insert
    suspend fun insert(expenseEnt: ExpenseEnt)

    @Insert
    suspend fun insertAll(expenses: List<ExpenseEnt>)

    // READ - Flow variant (reactive)
    @Query("SELECT * FROM expenses ORDER BY created_date DESC")
    fun getExpenseAll(): Flow<List<ExpenseEnt>>

    // READ - Single value
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Int): ExpenseEnt?

    // READ - Range queries with pagination
    @Query("""
        SELECT * FROM expenses 
        WHERE created_date BETWEEN :startTime AND :endTime 
        ORDER BY created_date DESC 
        LIMIT :limit OFFSET :offset
    """)
    fun getExpenseRangeDesc(
        startTime: Long,
        endTime: Long,
        offset: Int,
        limit: Int
    ): Flow<List<ExpenseEnt>>

    @Query("""
        SELECT * FROM expenses 
        WHERE created_date BETWEEN :startTime AND :endTime 
        ORDER BY created_date ASC 
        LIMIT :limit OFFSET :offset
    """)
    fun getExpenseRangeAsc(
        startTime: Long,
        endTime: Long,
        offset: Int,
        limit: Int
    ): Flow<List<ExpenseEnt>>

    // READ - Paging support (Paging2 library)
    @Query("""
        SELECT * FROM expenses 
        WHERE created_date BETWEEN :startTime AND :endTime 
        ORDER BY created_date DESC
    """)
    fun getExpenseRangeDescSource(
        startTime: Long,
        endTime: Long,
        offset: Int,
        limit: Int
    ): DataSource.Factory<Int, ExpenseEnt>

    // UPDATE
    @Update
    suspend fun update(expenseEnt: ExpenseEnt)

    // DELETE
    @Delete
    suspend fun delete(expenseEnt: ExpenseEnt)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Int)

    // AGGREGATION
    @Query("SELECT COUNT(*) FROM expenses")
    fun getExpenseCount(): Flow<Int>

    @Query("SELECT MIN(created_date), MAX(created_date) FROM expenses")
    fun getDateRange(): Flow<Pair<Long, Long>>
}
```

**DAO Best Practices:**
- Suspend for write operations (insert, update, delete)
- Flow for read operations that need to be reactive
- Explicit queries instead of generic get-all
- Parameterized queries to prevent SQL injection

---

## Pagination with Paging2

Room integrates with AndroidX Paging library for efficient large lists:

```kotlin
@Dao
interface ExpenseDao {
    @Query("""
        SELECT * FROM expenses 
        WHERE created_date BETWEEN :startTime AND :endTime 
        ORDER BY created_date DESC
    """)
    fun getExpenseRangeDescSource(
        startTime: Long,
        endTime: Long,
        offset: Int,
        limit: Int
    ): DataSource.Factory<Int, ExpenseEnt>
}
```

**Usage in Repository:**
```kotlin
fun getExpenseSourceAll(): DataSource.Factory<Int, ExpenseEnt> {
    return expenseDao.getExpenseSourceAll()
}
```

**Usage in ViewModel:**
```kotlin
@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    
    val pagedExpenses: LiveData<PagedList<ExpenseEnt>> =
        LivePagedListBuilder(
            expenseRepository.getExpenseSourceAll(),
            30  // Page size
        ).build()
}
```

---

## Schema Migrations

When you change the schema (add/remove columns, change types), Room needs migration instructions.

### Example: MIGRATION_3_4 (Adding Backup Table)

**File:** `/app/src/main/java/com/arduia/expense/data/local/ProExpenseDatabase.kt`

```kotlin
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create new backup table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS backup (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                backup_data TEXT NOT NULL,
                created_date INTEGER NOT NULL
            )
        """)
    }
}
```

### Example: MIGRATION_4_6 (Decimal Support)

```kotlin
val MIGRATION_4_6 = object : Migration(4, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Alter expense table to support decimal storage
        database.execSQL("""
            ALTER TABLE expenses 
            ADD COLUMN amount_decimal REAL DEFAULT 0.0
        """)
    }
}
```

### Registering Migrations

```kotlin
Room.databaseBuilder(context, ProExpenseDatabase::class.java, "pro_expense_db")
    .addMigrations(MIGRATION_3_4, MIGRATION_4_6)
    .build()
```

**Migration Best Practices:**
- One migration per schema change
- Test migrations with actual data
- Document why changes were needed
- Keep migrations in order (3→4, then 4→6)

---

## Flow-Based Reactive Queries

Room queries return `Flow<T>` for reactive updates:

```kotlin
@Dao
interface ExpenseDao {
    // Every time data changes, Flow emits new list
    @Query("SELECT * FROM expenses ORDER BY created_date DESC")
    fun getExpenseAll(): Flow<List<ExpenseEnt>>
}
```

**In Repository:**
```kotlin
override fun getExpenseAll(): FlowResult<List<ExpenseEnt>> {
    return expenseDao.getExpenseAll()
        .map { SuccessResult(it) }  // Wrap each emission
        .catch { ErrorResult(RepositoryException(it)) }
        .flowOn(Dispatchers.IO)
}
```

**In ViewModel:**
```kotlin
@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    
    val expenses: LiveData<List<ExpenseEnt>> =
        expenseRepository.getExpenseAll()
            .map { result ->
                when (result) {
                    is Result.Success -> result.data
                    is Result.Error -> emptyList()
                    is Result.Loading -> emptyList()
                }
            }
            .asLiveData()
}
```

**Benefit:** UI automatically updates when database changes

---

## Preferences Storage

Store app-level preferences (settings, cache) in the database:

**Entity:**
```kotlin
@Entity(tableName = "preferences")
data class PreferenceEnt(
    @PrimaryKey val key: String,
    @ColumnInfo(name = "value") val value: String,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
```

**DAO:**
```kotlin
@Dao
interface PreferenceStorageDao {
    @Query("SELECT value FROM preferences WHERE key = :key")
    fun getString(key: String): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setString(key: String, value: String)
}
```

**Usage:**
```kotlin
class SettingsRepositoryImpl @Inject constructor(
    private val preferenceDao: PreferenceStorageDao
) : SettingsRepository {
    
    override fun getLanguage(): Flow<String> {
        return preferenceDao.getString("language")
            .map { it ?: "en" }
    }
    
    override suspend fun setLanguage(lang: String) {
        preferenceDao.setString("language", lang)
    }
}
```

---

## Testing Database Layer

### Unit Tests with In-Memory Database

```kotlin
@RunWith(RobolectricTestRunner::class)
class ExpenseRepositoryTest {
    
    private lateinit var database: ProExpenseDatabase
    private lateinit var expenseDao: ExpenseDao
    private lateinit var repository: ExpenseRepository
    
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ProExpenseDatabase::class.java
        ).build()
        
        expenseDao = database.expenseDao()
        repository = ExpenseRepositoryImpl(expenseDao)
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun testInsertAndRetrieveExpense() = runTest {
        val expense = ExpenseEnt(
            expenseId = 1,
            name = "Coffee",
            amount = Amount.createFromStore(100),
            category = 1,
            note = null,
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis()
        )
        
        repository.insertExpense(expense)
        
        val retrieved = repository.getExpenseAll()
            .map { (it as? Result.Success)?.data ?: emptyList() }
            .first()
        
        assert(retrieved.size == 1)
        assert(retrieved[0].name == "Coffee")
    }
}
```

---

## Best Practices

### ✅ DO:

1. **Use Flow** for reactive queries
   ```kotlin
   fun getExpenses(): Flow<List<ExpenseEnt>>
   ```

2. **Suspend for writes**
   ```kotlin
   suspend fun insertExpense(exp: ExpenseEnt)
   ```

3. **Index frequently queried columns**
   ```kotlin
   @ColumnInfo(name = "created_date", index = true)
   ```

4. **Use parameterized queries** (automatic with @Query)
   ```kotlin
   @Query("SELECT * FROM expenses WHERE id = :id")
   suspend fun getById(id: Int): ExpenseEnt?
   ```

5. **Document migrations**
   ```kotlin
   val MIGRATION_3_4 = object : Migration(3, 4) {
       override fun migrate(db: SupportSQLiteDatabase) {
           // Added backup table for restore feature
           db.execSQL(...)
       }
   }
   ```

### ❌ DON'T:

1. **Execute raw SQL** without parameters
   ```kotlin
   // BAD - SQL injection risk
   query("SELECT * FROM expenses WHERE id = $id")
   
   // GOOD - parameterized
   @Query("SELECT * FROM expenses WHERE id = :id")
   ```

2. **Block on database queries**
   ```kotlin
   // BAD
   val expense = runBlocking { getExpense() }
   
   // GOOD
   suspend fun getExpense()
   ```

3. **Forget migrations when schema changes**
   ```kotlin
   // BAD - app crashes on old devices
   
   // GOOD - include migration
   .addMigrations(MIGRATION_X_Y)
   ```

4. **Use Rooms without type converters** for complex types
   ```kotlin
   // BAD
   val amount: Double  // Floating point precision errors
   
   // GOOD
   val amount: Amount  // With TypeConverter to Long
   ```

---

## Reuse in New Architecture

✅ **Room remains the persistence layer** regardless of UI framework  
✅ **DAO patterns** apply to refactored code without change  
✅ **Flow-based queries** work with any reactive UI approach  
✅ **Migrations** carry forward without modification  
✅ **TypeConverters** handle domain model serialization  

**Key: Keep repository as the single interface to persistence**
