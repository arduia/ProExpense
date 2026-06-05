# Shared Utilities & Extensions - ProExpense Best Practices

## Overview

ProExpense extracts common utilities into the **`shared` module** to eliminate duplication and provide a consistent API across the app.

---

## Shared Module Structure

**Location:** `/shared/src/main/java/com/arduia/core/`

```
shared/
├── arch/
│   └── Mapper.kt           # Base mapper interface
├── content/
│   └── Context.kt          # Context utilities
├── extension/
│   ├── Dimen.kt           # DPI conversions
│   ├── Drawable.kt        # Drawable utilities
│   └── View.kt            # View extensions
├── lang/
│   └── LocaleUpdate.kt    # Language switching
└── performance/
    └── Duration.kt        # Timing utilities
```

---

## Dimension Converters

**File:** `/shared/src/main/java/com/arduia/core/extension/Dimen.kt`

Converting between pixels, density-independent pixels (dp), and scaled pixels (sp):

```kotlin
// Get display density
fun Context.dp(value: Int): Int {
    return (value * resources.displayMetrics.density).toInt()
}

fun Context.px(value: Int): Int {
    return (value / resources.displayMetrics.density).toInt()
}

fun Context.pxS(value: Int): Int {
    return (value / resources.displayMetrics.scaledDensity).toInt()
}

fun Context.sp(value: Int): Float {
    return value * resources.displayMetrics.scaledDensity
}
```

### Usage

```kotlin
class HomeFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Convert 16dp to pixels
        val size = requireContext().dp(16)
        imageView.layoutParams.width = size
    }
}
```

---

## View Visibility Extensions

**File:** `/shared/src/main/java/com/arduia/core/extension/View.kt`

Consistent visibility state management:

```kotlin
fun View.asVisible() {
    visibility = View.VISIBLE
}

fun View.asInvisible() {
    visibility = View.INVISIBLE
}

fun View.asGone() {
    visibility = View.GONE
}

// Conditional visibility
fun View.setVisibleOrGone(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}
```

### Usage

```kotlin
// Show/hide loading indicator
loadingView.asGone()
contentView.asVisible()

// Conditional visibility
emptyStateView.setVisibleOrGone(expenses.isEmpty())
```

---

## Drawable Utilities

**File:** `/shared/src/main/java/com/arduia/core/extension/Drawable.kt`

Safe, reusable drawable color manipulation:

```kotlin
fun Context.getDrawable(@DrawableRes resId: Int, @ColorInt color: Int): Drawable? {
    return ContextCompat.getDrawable(this, resId)?.apply {
        setTint(color)
    }
}

fun Context.setDrawableColor(drawable: Drawable?, @ColorInt color: Int): Drawable? {
    return drawable?.apply { setTint(color) }
}
```

### Usage

```kotlin
val icon = requireContext().getDrawable(
    R.drawable.ic_expense,
    ContextCompat.getColor(requireContext(), R.color.primary)
)
imageView.setImageDrawable(icon)
```

---

## Context Utilities

**File:** `/shared/src/main/java/com/arduia/core/content/Context.kt`

App version and system information:

```kotlin
fun Context.getApplicationVersionCode(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION")
        packageManager.getPackageInfo(packageName, 0).versionCode
    }
}

fun Context.getApplicationVersionName(): String? {
    return try {
        packageManager.getPackageInfo(packageName, 0).versionName
    } catch (e: Exception) {
        null
    }
}
```

### Usage

```kotlin
class MainViewModel @Inject constructor(
    @ApplicationContext val context: Context
) : ViewModel() {
    
    fun checkAppVersion() {
        val versionCode = context.getApplicationVersionCode()
        val versionName = context.getApplicationVersionName()
        
        Timber.d("App version: $versionName ($versionCode)")
    }
}
```

---

## Locale & Language Management

**File:** `/shared/src/main/java/com/arduia/core/lang/LocaleUpdate.kt`

Switch app language at runtime:

```kotlin
fun Context.updateResource(language: String): Context {
    return ContextCompat.getMainExecutor(this)
        .applyLocale(language, this)
}

private fun Executor.applyLocale(language: String, context: Context): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)
    
    val config = context.resources.configuration
    config.setLocale(locale)
    
    return context.createConfigurationContext(config)
}
```

### Usage

```kotlin
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
            context.updateResource(language)
            // Restart activity to apply changes
        }
    }
}
```

---

## Performance Profiling

**File:** `/shared/src/main/java/com/arduia/core/performance/Duration.kt`

Measure operation timing for performance analysis:

```kotlin
class Duration {
    private val startTime = System.nanoTime()
    
    fun printDurationNano(): Long {
        val duration = System.nanoTime() - startTime
        Timber.d("Duration: ${duration}ns")
        return duration
    }
    
    fun printDurationMilli(): Long {
        val duration = (System.nanoTime() - startTime) / 1_000_000
        Timber.d("Duration: ${duration}ms")
        return duration
    }
}
```

### Usage

```kotlin
fun heavyOperation() {
    val duration = Duration()
    
    // Do expensive work
    repository.loadAllExpenses()
    
    duration.printDurationMilli()  // Logs duration to Logcat
}
```

---

## Base Mapper Interface

**File:** `/shared/src/main/java/com/arduia/core/arch/Mapper.kt`

Foundation for all data transformation:

```kotlin
interface Mapper<I, O> {
    fun map(input: I): O
}
```

### Implementation Pattern

```kotlin
class ExpenseUiModelMapper @Inject constructor(
    private val categoryProvider: ExpenseCategoryProvider,
    private val dateFormatter: ExpenseDateFormatter
) : Mapper<ExpenseEnt, ExpenseUiModel> {
    
    override fun map(input: ExpenseEnt): ExpenseUiModel {
        return ExpenseUiModel(
            id = input.expenseId,
            name = input.name,
            amount = input.amount.getActualAsFloat(),
            category = categoryProvider.getCategoryNameByID(input.category),
            date = dateFormatter.format(input.createdDate),
            note = input.note
        )
    }
}
```

### Benefits

- **Type-safe**: Generic interface enforces input/output types
- **Testable**: Easy to mock
- **Reusable**: One mapper per transformation
- **Composable**: Can chain mappers

---

## Formatter Hierarchy

Specialized formatters for different contexts:

### Base Interface

```kotlin
interface DateFormatter {
    fun format(timestamp: Long): String
}
```

### Multiple Implementations

```kotlin
// Exact date: "Jan 15, 2024"
class ExpenseDateFormatter @Inject constructor(
    private val context: Context
) : DateFormatter {
    override fun format(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(calendar.time)
    }
}

// Relative date: "2 hours ago"
class ExpenseRecentDateFormatter @Inject constructor() : DateFormatter {
    override fun format(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diffMs = now - timestamp
        
        return when {
            diffMs < 60_000 -> "Just now"
            diffMs < 60_000 * 60 -> "${diffMs / 60_000} minutes ago"
            diffMs < 60_000 * 60 * 24 -> "${diffMs / (60_000 * 60)} hours ago"
            else -> "${diffMs / (60_000 * 60 * 24)} days ago"
        }
    }
}

// Date range: "Jan 1 - Jan 31"
class MonthDateRangeFormatter @Inject constructor() {
    fun format(startTime: Long, endTime: Long): String {
        val start = SimpleDateFormat("MMM dd", Locale.getDefault())
            .format(Date(startTime))
        val end = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            .format(Date(endTime))
        return "$start - $end"
    }
}
```

### Dependency Injection

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class FormatterModule {
    
    @Binds
    abstract fun bindExpenseDateFormatter(
        formatter: ExpenseDateFormatter
    ): DateFormatter
}
```

---

## Category Provider Pattern

Interface-based category access:

```kotlin
interface ExpenseCategoryProvider {
    fun getCategoryList(): List<Category>
    fun getCategoryDrawableByID(id: Int): Int
    fun getCategoryColorByID(id: Int): Int
    fun getCategoryNameByID(id: Int): String
    fun getIndexByCategory(category: Int): Int
}

class ExpenseCategoryProviderImpl @Inject constructor(
    private val context: Context
) : ExpenseCategoryProvider {
    
    private val categories = listOf(
        Category(1, "Food", R.drawable.ic_food, R.color.orange),
        Category(2, "Transport", R.drawable.ic_car, R.color.blue),
        Category(3, "Entertainment", R.drawable.ic_movie, R.color.purple)
    )
    
    override fun getCategoryList() = categories
    
    override fun getCategoryDrawableByID(id: Int): Int {
        return categories.find { it.id == id }?.icon ?: R.drawable.ic_default
    }
    
    override fun getCategoryColorByID(id: Int): Int {
        return ContextCompat.getColor(
            context,
            categories.find { it.id == id }?.color ?: R.color.gray
        )
    }
    
    override fun getCategoryNameByID(id: Int): String {
        return categories.find { it.id == id }?.name ?: "Unknown"
    }
    
    override fun getIndexByCategory(category: Int): Int {
        return categories.indexOfFirst { it.id == category }
    }
}
```

---

## Calendar Extension Functions

**File:** `/shared/src/main/java/com/arduia/core/extension/CalendarExt.kt`

Convenient date range manipulation:

```kotlin
fun Calendar.setDayAsStart(): Long {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return timeInMillis
}

fun Calendar.setDayAsEnd(): Long {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
    return timeInMillis
}

fun Calendar.getMonthStart(): Long {
    set(Calendar.DAY_OF_MONTH, 1)
    return setDayAsStart()
}

fun Calendar.getMonthEnd(): Long {
    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    return setDayAsEnd()
}
```

### Usage

```kotlin
val calendar = Calendar.getInstance()
val monthStart = calendar.getMonthStart()
val monthEnd = calendar.getMonthEnd()

// Query expenses for this month
val expenses = repository.getExpenseRange(monthStart, monthEnd, 0, 100)
```

---

## Best Practices

### ✅ DO:

1. **Use extensions** for common operations
   ```kotlin
   view.asVisible()
   context.dp(16)
   ```

2. **Create formatters** for consistent formatting
   ```kotlin
   @Inject val dateFormatter: DateFormatter
   ```

3. **Use interfaces** for flexible implementations
   ```kotlin
   interface ExpenseCategoryProvider
   ```

4. **Share via DI** - not static utilities
   ```kotlin
   @Inject val provider: ExpenseCategoryProvider
   ```

5. **Keep utilities pure** - no side effects
   ```kotlin
   fun map(input: I): O  // Pure transformation
   ```

### ❌ DON'T:

1. **Duplicate formatting code**
   ```kotlin
   // BAD
   SimpleDateFormat(...).format(...)  // Multiple places
   
   // GOOD
   @Inject val formatter: DateFormatter
   formatter.format(...)
   ```

2. **Use static utilities**
   ```kotlin
   // BAD
   Utils.getDrawable(...)
   
   // GOOD
   @Inject val provider: DrawableProvider
   ```

3. **Put app-specific logic in shared**
   ```kotlin
   // BAD - shared should not know about Expense
   shared/ExpenseFormatter.kt
   
   // GOOD
   app/ExpenseFormatter.kt
   ```

---

## Reuse in New Architecture

✅ **Shared utilities** are architecture-agnostic  
✅ **Formatter pattern** applies to any UI framework  
✅ **Mapper interface** works regardless of transformation source  
✅ **DI injection** of utilities remains valid  
✅ **Extension functions** improve code readability everywhere

**Key: Shared module should never depend on app, only on Android framework**
