# Domain Modeling & Value Objects - ProExpense Best Practices

## Overview

Domain models represent **business concepts** with embedded business logic. They're independent of frameworks and databases, making them the true heart of the application.

---

## Value Objects

### What They Are

Immutable objects that represent a meaningful concept in your domain. **Identity comes from their values, not object identity.**

```kotlin
// Same Amount value, same object
val price1 = Amount.createFromStore(10000)  // $100.00
val price2 = Amount.createFromStore(10000)  // $100.00
// price1 == price2 (same value means equal)
```

### Why Use Them

✅ **Type Safety**: Amount is different from String, even though both could be Long  
✅ **Business Logic**: Business rules encapsulated in the value object  
✅ **Immutability**: No accidental mutations  
✅ **Self-Documenting**: Code reads like business language  

---

## Amount: Currency-Safe Value Object

**File:** `/app/src/main/java/com/arduia/expense/domain/Amount.kt`

### The Problem

Floating-point arithmetic loses precision:
```kotlin
// Floating point precision loss
val price = 0.1 + 0.2  // Not exactly 0.3!
val total = price * 1000  // Errors accumulate
```

### The Solution: Store as Integer (Cents)

```kotlin
class Amount: ExpenseStore(DataStoreExchangeRate) {
    
    companion object {
        // User provides decimal (BigDecimal) amount
        fun createFromActual(actual: BigDecimal) = Amount().apply {
            // Multiply by 100 to convert to cents, store as Long
            val storeValue = actual.multiply(BigDecimal(rate.getRate()))
                .setScale(0, RoundingMode.FLOOR)
            setStore(storeValue.longValueExact())
        }
        
        // Create from internal stored value (cents)
        fun createFromStore(store: Long) = Amount().apply {
            setStore(store)
        }
    }
}
```

### How It Works

**Internal Storage:** Long (integer) representing cents
```
$99.99 → 9999 (cents) → stored as 9999L
$5.00  → 500 (cents)  → stored as 500L
$0.01  → 1 (cent)     → stored as 1L
```

**No precision loss** because we're using integer arithmetic!

### Arithmetic Operations

```kotlin
operator fun Amount.times(multiplier: Amount): Amount {
    val left = this.getStore()      // 5000 ($50.00)
    val right = multiplier.getStore() // 200 ($2.00)
    val result = left * right        // 10,000,000
    this.setStore(result)
    return this
}

operator fun Amount.plus(amount: Amount): Amount {
    val result = this.getStore() + amount.getStore()
    setStore(result)
    return this
}
```

### Usage

```kotlin
// No thinking about internal representation - just business logic
val itemPrice = Amount.createFromActual(BigDecimal("99.99"))
val quantity = Amount.createFromActual(BigDecimal("5"))
val total = itemPrice * quantity  // Clean, intuitive code

// Display to user
val displayValue = total.getActualAsFloat()  // 499.95f
```

---

## ExpenseStore: Generic Exchange Pattern

**File:** `/app/src/main/java/com/arduia/expense/domain/ExpenseStore.kt`

Base class enabling **currency conversion logic**:

```kotlin
abstract class ExpenseStore(rate: Rate<Long>) : Store<BigDecimal, Long>(rate) {
    
    protected var storeValue: Long = 0
    
    // Convert stored value back to user-facing value
    override fun getActual(): BigDecimal {
        validateRateOrError(rate)
        return BigDecimal(storeValue.toDouble() / rate.getRate())
    }
    
    // Get internal stored value (cents)
    override fun getStore() = storeValue
    
    // Set internal stored value
    override fun setStore(value: Long) {
        this.storeValue = value
    }
}

// Exchange rate: multiply by 100 to convert to cents
object DataStoreExchangeRate: Rate<Long> {
    override fun getRate(): Long = 100L
}
```

**Reusable Pattern:**
```kotlin
// Could be used for any value conversion
class Temperature(rate: Rate<Int>) : Store<Float, Int>(rate)  // Celsius ↔ Fahrenheit
class Distance(rate: Rate<Int>) : Store<Double, Int>(rate)    // Miles ↔ Kilometers
```

---

## Filter Models with Builder Pattern

**File:** `/app/src/main/java/com/arduia/expense/domain/filter/`

### DateRange: Type-Safe Boundaries

```kotlin
interface Range<S, E> {
    val start: S
    val end: E
}

interface DateRange : Range<Long, Long>

class ExpenseDateRange(override val start: Long, override val end: Long) : DateRange {
    init {
        validateDateRange(start, end)  // Validate at construction
    }
}
```

### ExpenseLogFilterInfo: Complex Filter State

```kotlin
data class ExpenseLogFilterInfo(
    val dateRangeLimit: DateRange,      // Min/max available dates
    val dateRangeSelected: DateRange,   // Currently selected range
    val sorting: Sorting                // ASC or DESC
) {
    class Builder {
        private var limit: DateRange = ExpenseDateRange(0, 0)
        private var selected: DateRange = limit
        private var sorting = Sorting.DESC
        
        fun setDateLimit(range: DateRange): Builder {
            this.limit = range
            return this
        }
        
        fun setSelectedLimit(range: DateRange): Builder {
            this.selected = range
            return this
        }
        
        fun setSorting(sorting: Sorting): Builder {
            this.sorting = sorting
            return this
        }
        
        fun build() = ExpenseLogFilterInfo(
            dateRangeLimit = limit,
            dateRangeSelected = selected,
            sorting = sorting
        )
    }
}
```

### Usage

```kotlin
val today = System.currentTimeMillis()
val thirtyDaysAgo = today - (30 * 24 * 60 * 60 * 1000)

val filter = ExpenseLogFilterInfo.Builder()
    .setDateLimit(ExpenseDateRange(thirtyDaysAgo, today))
    .setSelectedLimit(ExpenseDateRange(thirtyDaysAgo, today))
    .setSorting(Sorting.DESC)
    .build()
```

---

## ExpenseLogItemEnt: Core Domain Entity

**File:** `/app/src/main/java/com/arduia/expense/domain/ExpenseLogItemEnt.kt`

```kotlin
data class ExpenseLogItemEnt(
    val expenseId: Int = 0,
    val name: String?,
    val amount: Amount,  // Value object
    val category: Int,   // Category ID
    val note: String?,
    val createdDate: Long,
    val modifiedDate: Long
)
```

**Key Characteristics:**
- Immutable (data class)
- Amount as value object (not Long or Double)
- Timestamps for audit trail
- Represents a real business concept

---

## Domain Validation

### Validating at Construction

```kotlin
class ExpenseDateRange(
    override val start: Long,
    override val end: Long
) : DateRange {
    init {
        // Fail fast if invalid
        validateDateRange(start, end)
    }
}

fun validateDateRange(start: Long, end: Long) {
    if (start > end) {
        throw IllegalArgumentException("Start date cannot be after end date")
    }
}
```

### Preventing Invalid States

```kotlin
// Amount prevents negative rates
private fun validateRateOrError(rate: Rate<Long>) {
    if (rate.getRate() <= 0) {
        throw Exception("Invalid Rate Value (${rate.getRate()}). It should be greater than 0")
    }
}
```

---

## Domain vs Database Models

### Domain Models

- **What**: Business concepts, rules, logic
- **Where**: `/domain/` folder
- **Examples**: `Amount`, `ExpenseLogItemEnt`, `DateRange`
- **Usage**: Business logic, calculations, validation

### Database Entities

- **What**: How data is persisted
- **Where**: `/data/local/` folder
- **Examples**: `ExpenseEnt`, `BackupEnt`
- **Usage**: Room queries, storage

### UI Models

- **What**: How data is displayed
- **Where**: `/ui/` folder as data classes
- **Examples**: `ExpenseUiModel`
- **Usage**: Fragment observation, adapter binding

### Transformation Flow

```
Database Entity (ExpenseEnt)
        ↓
Domain Entity (ExpenseLogItemEnt via mapper)
        ↓
UI Model (ExpenseUiModel via mapper)
        ↓
Fragment/Compose Display
```

---

## Business Rule Encapsulation

### Wrong: Rules Scattered in Code

```kotlin
// Business rule scattered in multiple places
if (amount > 0) { ... }  // Repository
if (amount > 0) { ... }  // ViewModel
if (amount > 0) { ... }  // Formatter
```

### Right: Rules in Domain

```kotlin
// Amount enforces rate > 0
object DataStoreExchangeRate: Rate<Long> {
    override fun getRate(): Long {
        if (getRate() <= 0) throw Exception("Invalid")
        return 100L
    }
}

// ExpenseDateRange validates dates
class ExpenseDateRange(start: Long, end: Long) {
    init {
        if (start > end) throw Exception("Invalid range")
    }
}
```

---

## Domain Service Pattern

When logic spans multiple value objects:

```kotlin
// Service in domain layer
class ExpenseCalculationService @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {
    suspend fun calculateTotal(
        expenses: List<ExpenseEnt>,
        currency: String
    ): Amount {
        return expenses
            .map { it.amount }
            .fold(Amount.createFromStore(0)) { acc, amount ->
                acc + amount  // Uses Amount's + operator
            }
    }
}
```

**Usage:**
```kotlin
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val expenseCalculationService: ExpenseCalculationService,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    
    fun loadTotalExpenses() {
        viewModelScope.launch {
            val expenses = expenseRepository.getExpenseAllSync().getOrNull() ?: return@launch
            val total = expenseCalculationService.calculateTotal(expenses, "USD")
            
            _totalAmount.value = total.getActualAsFloat()
        }
    }
}
```

---

## Best Practices

### ✅ DO:

1. **Use value objects** for meaningful concepts
   ```kotlin
   class Amount  // Not just Long
   class DateRange  // Not just Pair<Long, Long>
   ```

2. **Encapsulate logic** in domain models
   ```kotlin
   operator fun Amount.times(multiplier: Amount)
   ```

3. **Validate at construction** time
   ```kotlin
   init { validateDateRange(start, end) }
   ```

4. **Keep domain models immutable**
   ```kotlin
   data class Amount(val storeValue: Long)  // val, not var
   ```

5. **Separate concerns** by layer
   ```
   Domain: Business logic
   Data: Persistence
   UI: Display
   ```

### ❌ DON'T:

1. **Primitive types** for domain concepts
   ```kotlin
   // BAD
   val amount: Long
   val dateRange: Pair<Long, Long>
   
   // GOOD
   val amount: Amount
   val dateRange: DateRange
   ```

2. **Mixed concerns** in one model
   ```kotlin
   // BAD - domain + persistence in one class
   @Entity
   data class Expense(
       @PrimaryKey val id: Int,
       val jsonData: String  // Serialization logic mixed in
   )
   
   // GOOD - separate models
   data class ExpenseEnt(...)  // Persistence
   data class ExpenseLogItemEnt(...)  // Domain
   ```

3. **Mutable domain models**
   ```kotlin
   // BAD
   val amount = Amount()
   amount.setStore(100)  // Can change
   
   // GOOD - immutable
   val amount = Amount.createFromStore(100)
   ```

4. **Business logic in UI**
   ```kotlin
   // BAD - calculation in ViewModel
   val total = expenses.sumOf { it.amount * 0.95 }  // 5% discount
   
   // GOOD - in domain service
   val total = expenseService.calculateWithDiscount(expenses, 0.95)
   ```

---

## Reuse in New Architecture

✅ **Domain models are architecture-agnostic** - apply anywhere  
✅ **Value objects** encode immutable business rules  
✅ **Builder pattern** works for complex construction  
✅ **Validation** at construction prevents invalid states  
✅ **Amount class** is a proven pattern for financial apps  

**Key: Domain layer has no dependencies on Data or UI layers**
