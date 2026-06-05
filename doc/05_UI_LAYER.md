# Compose UI Layer - KMP Best Practices

## Overview

ProExpense uses **Jetpack Compose** for UI - a declarative, reactive UI framework that works identically across iOS, Android, and Web via KMP.

---

## ViewModel + StateFlow Architecture

### What It Is

- **ViewModel**: Holds and manages UI state
- **StateFlow**: Reactive state container that emits updates
- **Compose Screen**: Renders state and handles user input

### Why Use It

✅ **Reactive**: UI automatically updates when state changes  
✅ **Testable**: ViewModel tested independently of UI  
✅ **Predictable**: Single source of truth for state  
✅ **Platform Independent**: Same ViewModel across iOS, Android, Web  

### Implementation Example

**ViewModel** - `shared/viewmodel/src/commonMain/kotlin/HomeViewModel.kt`

```kotlin
class HomeViewModel(
    private val expenseRepository: ExpenseRepository,
    private val currencyRepository: CurrencyRepository,
    private val expenseMapper: ExpenseUiModelMapper
) {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadExpenses() {
        viewModelScope.launch {
            expenseRepository.getRecentExpense()
                .onSuccess { expenses ->
                    _uiState.value = HomeUiState.Success(
                        expenses.map { expenseMapper.map(it) }
                    )
                }
                .onError { error ->
                    _uiState.value = HomeUiState.Error(error.message)
                }
        }
    }
    
    fun deleteExpense(expense: ExpenseUiModel) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(
                expenseMapper.mapBackToDomain(expense)
            )
            loadExpenses()
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val expenses: List<ExpenseUiModel>) : HomeUiState()
    data class Error(val message: String?) : HomeUiState()
    object Empty : HomeUiState()
}
```

**Compose Screen** - Observes and renders state

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is HomeUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        is HomeUiState.Success -> {
            ExpenseListContent(
                expenses = state.expenses,
                onExpenseClick = { /* navigate */ },
                onExpenseDelete = { viewModel.deleteExpense(it) }
            )
        }
        
        is HomeUiState.Error -> {
            ErrorMessage(message = state.message)
        }
        
        is HomeUiState.Empty -> {
            EmptyStateView()
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.loadExpenses()
    }
}
```

---

## Compose Patterns

### Unidirectional Data Flow (UDF)

```
User Action → ViewModel → State Update → Recompose → UI
```

```kotlin
// ViewModel exposes state and events
class ExpenseViewModel(private val repo: ExpenseRepository) {
    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()
    
    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            _state.value = State.Adding
            repo.insertExpense(expense)
            _state.value = State.Success
        }
    }
}

// Compose observes state and calls ViewModel methods
@Composable
fun ExpenseForm(viewModel: ExpenseViewModel) {
    var name by remember { mutableStateOf("") }
    
    Button(onClick = {
        viewModel.addExpense(
            Expense(name = name, ...)
        )
    }) {
        Text("Save")
    }
}
```

### Composable Functions

```kotlin
@Composable
fun ExpenseItem(
    expense: ExpenseUiModel,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { /* navigate */ }
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = expense.category,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Text(
            text = "$${expense.amount}",
            style = MaterialTheme.typography.bodyLarge
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, "Delete")
        }
    }
}
```

### State Management with remember

```kotlin
@Composable
fun ExpenseDialog(onSave: (Expense) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Expense") },
            text = {
                Column {
                    TextField(value = name, onValueChange = { name = it })
                    TextField(value = amount, onValueChange = { amount = it })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSave(Expense(name, amount.toFloat()))
                        showDialog = false
                    }
                ) {
                    Text("Save")
                }
            }
        )
    }
    
    Button(onClick = { showDialog = true }) {
        Text("Add Expense")
    }
}
```

### Effects and Side Effects

```kotlin
@Composable
fun ExpenseListScreen(viewModel: ExpenseViewModel) {
    val state by viewModel.state.collectAsState()
    
    // Load data when screen enters composition
    LaunchedEffect(Unit) {
        viewModel.loadExpenses()
    }
    
    // Update when filter changes
    LaunchedEffect(selectedCategory) {
        viewModel.filterByCategory(selectedCategory)
    }
    
    // Cleanup resources on disposal
    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanup()
        }
    }
}
```

---

## Navigation with Compose

```kotlin
@Composable
fun NavHost(
    navController: NavHostController,
    startDestination: String = Routes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToDetail = { expenseId ->
                    navController.navigate("${Routes.DETAIL}/$expenseId")
                }
            )
        }
        
        composable("${Routes.DETAIL}/{expenseId}") { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId")?.toIntOrNull() ?: 0
            ExpenseDetailScreen(expenseId)
        }
    }
}

object Routes {
    const val HOME = "home"
    const val DETAIL = "detail"
}
```

---

## List Performance with LazyColumn

```kotlin
@Composable
fun ExpenseList(
    expenses: List<ExpenseUiModel>,
    onExpenseClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(
            count = expenses.size,
            key = { expenses[it].id }  // Key for recomposition
        ) { index ->
            ExpenseItem(
                expense = expenses[index],
                onDelete = { /* ... */ }
            )
        }
    }
}
```

---

## Testing Compose UI

```kotlin
@get:Rule
val composeTestRule = createComposeRule()

@Test
fun testExpenseItemDisplaysCorrectly() {
    composeTestRule.setContent {
        ExpenseItem(
            expense = ExpenseUiModel(
                id = 1,
                name = "Coffee",
                amount = 5.99f,
                category = "Food"
            ),
            onDelete = {}
        )
    }
    
    composeTestRule
        .onNodeWithText("Coffee")
        .assertIsDisplayed()
    
    composeTestRule
        .onNodeWithText("$5.99")
        .assertIsDisplayed()
}

@Test
fun testExpenseFormSavesOnButtonClick() {
    composeTestRule.setContent {
        ExpenseForm(
            onSave = { expense ->
                assert(expense.name == "Lunch")
            }
        )
    }
    
    composeTestRule
        .onNodeWithText("Name")
        .performTextInput("Lunch")
    
    composeTestRule
        .onNodeWithText("Save")
        .performClick()
}
```

---

## Best Practices

### ✅ DO:

1. **Use StateFlow** for reactive state
   ```kotlin
   val state: StateFlow<UiState> = _state.asStateFlow()
   ```

2. **Define UI states as sealed classes**
   ```kotlin
   sealed class UiState {
       object Loading : UiState()
       data class Success(val data: T) : UiState()
       data class Error(val message: String) : UiState()
   }
   ```

3. **Keep ViewModels pure Kotlin**
   ```kotlin
   // No Compose imports, no platform-specific code
   class ViewModel(private val repo: Repository)
   ```

4. **Use Composable functions** for reusability
   ```kotlin
   @Composable fun ExpenseItem(expense: Expense)
   ```

5. **Extract smaller Composables** for performance
   ```kotlin
   @Composable fun ItemContent() { ... }
   ```

### ❌ DON'T:

1. **Store state in Composable functions**
   ```kotlin
   // BAD
   var expenses: List<Expense>? = null
   
   // GOOD
   val state by viewModel.state.collectAsState()
   ```

2. **Suspend functions in Composables**
   ```kotlin
   // BAD
   val data = repository.getData()
   
   // GOOD
   LaunchedEffect(Unit) {
       viewModel.loadData()
   }
   ```

3. **Complex logic in Composables**
   ```kotlin
   // BAD
   if (expense.amount > 100 && ...) { ... }
   
   // GOOD
   val isExpensive = viewModel.isExpensive(expense)
   ```

4. **Recomposition without keys** in lists
   ```kotlin
   // BAD
   items(expenses) { ExpenseItem(it) }
   
   // GOOD
   items(expenses, key = { it.id }) { ExpenseItem(it) }
   ```

---

## KMP Compose Best Practices

✅ **Single Compose codebase** works on iOS, Android, Web  
✅ **Pure Kotlin ViewModels** with no framework imports  
✅ **StateFlow** for reactive state across all platforms  
✅ **Shared composables** eliminate UI duplication  
✅ **Platform-specific Composables** only when necessary  

**Key: Compose is the single source of UI truth across all platforms**
