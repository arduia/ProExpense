# UI Layer Architecture - ProExpense Best Practices

## Overview

ProExpense uses **ViewModel + LiveData + Jetpack Compose hybrid** approach - combining traditional Fragment/XML layouts with modern Compose capabilities.

---

## ViewModel Architecture

### What It Does

- Holds and manages UI state
- Survives configuration changes (rotation)
- Communicates with repositories for data
- Exposes state via LiveData for observation

**File:** `/app/src/main/java/com/arduia/expense/ui/home/HomeViewModel.kt`

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val currencyRepository: CurrencyRepository,
    private val expenseMapper: ExpenseUiModelMapper
) : ViewModel() {
    
    private val _uiState = MutableLiveData<HomeUiState>()
    val uiState: LiveData<HomeUiState> = _uiState
    
    private val _currentCurrency = MutableLiveData<String>()
    val currentCurrency: LiveData<String> = _currentCurrency
    
    init {
        loadCurrency()
        loadExpenses()
    }
    
    private fun loadCurrency() {
        viewModelScope.launch {
            currencyRepository.getSelectedCacheCurrency()
                .onSuccess { currency ->
                    _currentCurrency.value = currency.code
                }
        }
    }
    
    fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
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
            try {
                expenseRepository.deleteExpense(
                    ExpenseEnt(...) // Map from UI model back to entity
                )
                loadExpenses()  // Refresh list
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message)
            }
        }
    }
}
```

### Key Patterns

| Pattern | Use Case | Example |
|---------|----------|---------|
| **MutableLiveData + LiveData** | Exposable mutable state | `_uiState` (private), `uiState` (public) |
| **Sealed Classes** | Type-safe UI states | `HomeUiState.Loading`, `.Success`, `.Error` |
| **viewModelScope** | Auto-cancel coroutines | `viewModelScope.launch { ... }` |
| **Single Responsibility** | Each ViewModel manages one screen | `HomeViewModel`, `ExpenseDetailViewModel` |

---

## LiveData for UI State

### UI State Pattern

Define all possible UI states as sealed class:

```kotlin
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val expenses: List<ExpenseUiModel>) : HomeUiState()
    data class Error(val message: String?) : HomeUiState()
    object Empty : HomeUiState()
}
```

### Observation in Fragment

```kotlin
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding = FragmentHomeBinding.bind(view)
        
        // Observe UI state
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HomeUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
                is HomeUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter.submitList(state.expenses)
                }
                is HomeUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showErrorDialog(state.message)
                }
                is HomeUiState.Empty -> {
                    binding.emptyStateView.visibility = View.VISIBLE
                }
            }
        }
        
        // Observe currency changes
        viewModel.currentCurrency.observe(viewLifecycleOwner) { currency ->
            binding.currencyLabel.text = currency
        }
    }
}
```

---

## Navigation Component

Type-safe navigation using Safe Args:

### Define Routes in Navigation Graph

**File:** `res/navigation/nav_graph.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph"
    app:startDestination="@id/homeFragment">

    <fragment
        android:id="@+id/homeFragment"
        android:name="com.arduia.expense.ui.home.HomeFragment"
        android:label="Home">
        <action
            android:id="@+id/action_home_to_detail"
            app:destination="@id/expenseDetailFragment" />
    </fragment>

    <fragment
        android:id="@+id/expenseDetailFragment"
        android:name="com.arduia.expense.ui.detail.ExpenseDetailFragment"
        android:label="Expense Detail">
        <argument
            android:name="expenseId"
            app:argType="integer"
            android:defaultValue="0" />
    </fragment>
</navigation>
```

### Navigate from Fragment

```kotlin
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter.setOnItemClickListener { expense ->
            val action = HomeFragmentDirections.actionHomeToDetail(expense.id)
            findNavController().navigate(action)
        }
    }
}
```

### Receive Arguments

```kotlin
class ExpenseDetailFragment : Fragment() {
    private val args: ExpenseDetailFragmentArgs by navArgs()
    private val viewModel: ExpenseDetailViewModel by viewModels {
        ExpenseDetailViewModelFactory(args.expenseId)
    }
}
```

---

## Base Classes and Reuse

### NavBaseFragment

Reduces boilerplate by providing common functionality:

**File:** `/app/src/main/java/com/arduia/expense/ui/NavBaseFragment.kt`

```kotlin
abstract class NavBaseFragment(@LayoutRes layoutResId: Int) : Fragment(layoutResId) {
    
    protected fun navigate(action: Int) {
        findNavController().navigate(action)
    }
    
    protected fun navigateUp() {
        findNavController().navigateUp()
    }
    
    protected fun showErrorDialog(message: String?) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message ?: "Unknown error")
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }
    
    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
```

### Usage

```kotlin
class HomeFragment : NavBaseFragment(R.layout.fragment_home) {
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Use inherited methods
        btnNavigateToDetail.setOnClickListener {
            navigate(R.id.action_home_to_detail)
        }
    }
}
```

---

## UI Mappers (Domain → UI Model)

Transform domain models to UI-ready models:

```kotlin
class ExpenseUiModelMapper @Inject constructor(
    private val categoryProvider: ExpenseCategoryProvider,
    private val dateFormatter: ExpenseDateFormatter
) : Mapper<ExpenseEnt, ExpenseUiModel> {
    
    override fun map(input: ExpenseEnt): ExpenseUiModel {
        return ExpenseUiModel(
            id = input.expenseId,
            name = input.name,
            amount = input.amount.getActualAsFloat(),  // Convert Amount to Float for display
            categoryName = categoryProvider.getCategoryNameByID(input.category),
            categoryColor = categoryProvider.getCategoryColorByID(input.category),
            formattedDate = dateFormatter.format(input.createdDate),
            note = input.note
        )
    }
}
```

**Benefits:**
- UI doesn't depend on domain models
- Formatting logic centralized
- Easy to test with mock mappers

---

## Paging Integration

Display large lists efficiently with Paging 2:

```kotlin
@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    
    fun getPagedExpenses(
        startDate: Long,
        endDate: Long
    ): LiveData<PagedList<ExpenseEnt>> {
        return LivePagedListBuilder(
            expenseRepository.getExpenseRangeDescSource(startDate, endDate, 0, 30),
            PagedList.Config.Builder()
                .setPageSize(30)
                .setPrefetchDistance(10)
                .setInitialLoadSizeHint(30)
                .build()
        ).build()
    }
}
```

**In Fragment:**
```kotlin
class ExpenseListFragment : Fragment() {
    private val viewModel: ExpenseListViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.getPagedExpenses(startDate, endDate)
            .observe(viewLifecycleOwner) { pagedList ->
                adapter.submitList(pagedList)
            }
    }
}
```

---

## Fragment Lifecycle Best Practices

### Proper State Management

```kotlin
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModels()
    private var binding: FragmentHomeBinding? = null
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding = FragmentHomeBinding.bind(view)
        
        // Observe only when view is created
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Clear binding to prevent memory leaks
        binding = null
    }
}
```

### Common Mistakes

❌ **Don't retain Fragment state manually** - Use ViewModel  
❌ **Don't store Context in ViewModel** - Use Application context from Hilt  
❌ **Don't forget to observe with viewLifecycleOwner** - Prevents leaks  
❌ **Don't call binding after onDestroyView()** - Can cause NPE  

---

## Jetpack Compose Integration

Modern UI code uses Compose, but ProExpense still has Fragment/XML. **Hybrid approach:**

```kotlin
class ComposeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            MaterialTheme {
                ExpenseListScreen(viewModel)
            }
        }
    }
}

@Composable
fun ExpenseListScreen(viewModel: ExpenseListViewModel) {
    val uiState by viewModel.uiState.observeAsState(HomeUiState.Loading)
    
    when (uiState) {
        is HomeUiState.Loading -> CircularProgressIndicator()
        is HomeUiState.Success -> {
            LazyColumn {
                items((uiState as HomeUiState.Success).expenses) { expense ->
                    ExpenseItem(expense)
                }
            }
        }
        is HomeUiState.Error -> Text("Error: ${(uiState as HomeUiState.Error).message}")
    }
}
```

---

## Recycler View Adapters

**Pattern:** Delegate to ListAdapter for diffing

```kotlin
class ExpenseAdapter : ListAdapter<ExpenseUiModel, ExpenseAdapter.ViewHolder>(DIFF_CALLBACK) {
    
    private var onItemClickListener: ((ExpenseUiModel) -> Unit)? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemClickListener
        )
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    fun setOnItemClickListener(listener: (ExpenseUiModel) -> Unit) {
        onItemClickListener = listener
    }
    
    inner class ViewHolder(
        private val binding: ItemExpenseBinding,
        private val clickListener: ((ExpenseUiModel) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: ExpenseUiModel) {
            binding.apply {
                nameTv.text = item.name
                amountTv.text = item.amount.toString()
                dateTv.text = item.formattedDate
                categoryIv.setImageResource(item.categoryIcon)
                
                root.setOnClickListener {
                    clickListener?.invoke(item)
                }
            }
        }
    }
    
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ExpenseUiModel>() {
            override fun areItemsTheSame(old: ExpenseUiModel, new: ExpenseUiModel) =
                old.id == new.id
            
            override fun areContentsTheSame(old: ExpenseUiModel, new: ExpenseUiModel) =
                old == new
        }
    }
}
```

---

## Best Practices

### ✅ DO:

1. **Use sealed classes** for UI states
   ```kotlin
   sealed class UiState { object Loading : UiState() ... }
   ```

2. **Expose immutable LiveData** from ViewModel
   ```kotlin
   private val _state = MutableLiveData<State>()
   val state: LiveData<State> = _state
   ```

3. **Use viewLifecycleOwner** for observation
   ```kotlin
   viewModel.state.observe(viewLifecycleOwner) { ... }
   ```

4. **Map domain → UI models** in mappers
   ```kotlin
   class ExpenseUiModelMapper : Mapper<ExpenseEnt, ExpenseUiModel>
   ```

5. **Clear binding** in onDestroyView
   ```kotlin
   override fun onDestroyView() { binding = null }
   ```

### ❌ DON'T:

1. **Store UI state in Fragment**
   ```kotlin
   // BAD - lost on rotation
   private var expenses: List<Expense> = emptyList()
   
   // GOOD - survives rotation
   val expenses: LiveData<List<Expense>>
   ```

2. **Pass Context to ViewModel**
   ```kotlin
   // BAD
   val context: Context  // Memory leak
   
   // GOOD - use Application context
   @ApplicationContext val context: Context
   ```

3. **Forget lifecycle management**
   ```kotlin
   // BAD - Memory leak
   viewModel.state.observe(this) { ... }
   
   // GOOD
   viewModel.state.observe(viewLifecycleOwner) { ... }
   ```

---

## Reuse in New Architecture

✅ **ViewModel + LiveData pattern** works with any UI framework  
✅ **Navigation Component** remains valid for screen transitions  
✅ **UI state management** approach applies to Compose  
✅ **Mapper pattern** works for any model transformation  
✅ **Lifecycle-aware** scope management is architecture-agnostic
