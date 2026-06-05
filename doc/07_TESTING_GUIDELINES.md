# Testing Guidelines - Best Practices

## Overview

Good testing practices apply regardless of the framework or tool. This guide documents testing principles and strategies that remain valuable in any architecture.

---

## Testing Pyramid

```
        UI/Integration Tests (few)
       /                        \
      /   Component Tests        \
     /      (medium)              \
    /                              \
   /____   Unit Tests (many)   _____\
  
Strategy: Many unit tests, fewer integration tests, minimal UI tests
```

---

## Unit Testing

### What to Test

**Test behavior, not implementation:**

```kotlin
// GOOD - Tests behavior
@Test
fun whenAmountIsNegative_validationFails() {
    val result = AmountValidator.validate(-100)
    assert(result is ValidationError)
}

// BAD - Tests implementation detail
@Test
fun whenCreatingAmount_storeValueIsSet() {
    val amount = Amount.createFromStore(100)
    assert(amount.storeValue == 100)  // Implementation detail
}
```

### AAA Pattern: Arrange-Act-Assert

```kotlin
@Test
fun testExpenseInsertion() {
    // Arrange: Set up test data and mocks
    val expense = createTestExpense()
    val repository = mockRepository()
    
    // Act: Call the function being tested
    repository.insertExpense(expense)
    
    // Assert: Verify the result
    verify(repository).wasCalledWith(expense)
}
```

### Mock External Dependencies

```kotlin
// Mock the repository for ViewModel tests
val mockRepository = createMock<ExpenseRepository>()
every { mockRepository.getExpenses() } returns mockFlow

val viewModel = HomeViewModel(mockRepository)
// Test ViewModel behavior without touching real repository
```

### Test Error Cases

```kotlin
@Test
fun whenRepositoryFails_viewModelShowsError() {
    val mockRepository = createMock<ExpenseRepository>()
    every { mockRepository.getExpenses() } throws Exception("Network error")
    
    val viewModel = HomeViewModel(mockRepository)
    viewModel.loadExpenses()
    
    // Verify error state
    assert(viewModel.uiState.value is UiState.Error)
}
```

---

## Testing Layers

### Domain Layer Tests

Test **business logic** and **validation** - no mocking needed:

```kotlin
@Test
fun testAmountAddition() {
    val amount1 = Amount.createFromStore(100)  // $1.00
    val amount2 = Amount.createFromStore(250)  // $2.50
    val sum = amount1 + amount2
    
    assert(sum.toFloat() == 3.50f)
}

@Test
fun testExpenseDateRangeValidation() {
    val startDate = 100L
    val endDate = 50L  // End before start
    
    val result = DateRange.validate(startDate, endDate)
    assert(result is ValidationError)
}

@Test
fun testFilterBuilderConstruction() {
    val filter = FilterCriteria.Builder()
        .setDateRange(100, 200)
        .setCategories(listOf(1, 2))
        .build()
    
    assert(filter.startDate == 100)
    assert(filter.endDate == 200)
}
```

### Data Layer Tests

Test **repository logic** with **mocked data sources**:

```kotlin
@Test
fun testRepositoryMapsDataCorrectly() {
    // Arrange: Mock data source
    val mockDataSource = mockDataSource()
    every { mockDataSource.getExpenses() } returns testExpenses
    
    val repository = ExpenseRepositoryImpl(mockDataSource)
    
    // Act
    val result = repository.getExpenses().first()
    
    // Assert: Data was mapped correctly
    assert(result is Result.Success)
    assert((result as Result.Success).data.size == testExpenses.size)
}

@Test
fun testRepositoryHandlesErrors() {
    // Arrange: Mock error
    val mockDataSource = mockDataSource()
    every { mockDataSource.getExpenses() } throws Exception("DB error")
    
    val repository = ExpenseRepositoryImpl(mockDataSource)
    
    // Act
    val result = repository.getExpenses().first()
    
    // Assert: Error handled
    assert(result is Result.Error)
}
```

### ViewModel Tests

Test **state management** with **mocked repository**:

```kotlin
@Test
fun testViewModelLoadsExpenses() {
    // Arrange
    val mockRepository = mockRepository()
    every { mockRepository.getExpenses() } returns flowOf(
        Result.Success(testExpenses)
    )
    
    val viewModel = HomeViewModel(mockRepository)
    
    // Act
    viewModel.loadExpenses()
    
    // Assert
    val state = viewModel.uiState.value
    assert(state is UiState.Success)
    assert((state as UiState.Success).expenses == testExpenses)
}
```

---

## Integration Testing

### Test Mapper Transformations

```kotlin
@Test
fun testExpenseDomainMapper() {
    // Mapper should work correctly
    val dto = ExpenseDto(
        id = 1,
        name = "Coffee",
        amount = 5.99,
        categoryId = 1,
        createdAt = Instant.now()
    )
    
    val mapper = ExpenseDomainMapper()
    val domain = mapper.map(dto)
    
    assert(domain.name == "Coffee")
    assert(domain.amount.toFloat() == 5.99f)
}
```

### Test Database Operations

Use **in-memory or test database**:

```kotlin
@Test
fun testInsertAndRetrieveExpense() {
    // Arrange: Use test database
    val testDatabase = createInMemoryDatabase()
    val repository = ExpenseRepositoryImpl(testDatabase)
    
    val expense = createTestExpense()
    
    // Act
    repository.insertExpense(expense)
    
    // Assert
    val retrieved = repository.getExpenseAll().first()
    assert((retrieved as Result.Success).data.contains(expense))
}
```

---

## UI Testing

### Test Component Behavior

```kotlin
@Test
fun testExpenseItemDisplaysCorrectly() {
    // Test that component renders data correctly
    val expense = createTestExpense()
    
    // Render component (framework-specific)
    val component = ExpenseItem(expense)
    
    // Assert
    assert(component.getText("Coffee") != null)  // Name shown
    assert(component.getText("$5.99") != null)   // Amount shown
}

@Test
fun testExpenseFormSubmitsOnButtonClick() {
    var submitted = false
    val form = ExpenseForm(onSubmit = { submitted = true })
    
    form.clickSaveButton()
    
    assert(submitted)
}
```

---

## Test Organization

```
src/
├── test/             # Unit & integration tests
│   ├── domain/
│   │   └── AmountTest.kt
│   ├── data/
│   │   ├── ExpenseMapperTest.kt
│   │   └── ExpenseRepositoryTest.kt
│   └── viewmodel/
│       └── HomeViewModelTest.kt
└── uiTest/           # UI/component tests (if applicable)
    └── ExpenseItemTest.kt
```

---

## Testing Best Practices

### ✅ DO:

1. **Test behavior, not implementation**
   ```kotlin
   // Test what it does, not how it does it
   ```

2. **Use the AAA pattern**
   ```kotlin
   Arrange → Act → Assert
   ```

3. **Mock external dependencies**
   ```kotlin
   val mockRepo = mockRepository()
   ```

4. **Test error cases**
   ```kotlin
   every { repo.get() } throws Exception()
   ```

5. **Use descriptive test names**
   ```kotlin
   fun testWhenRepositoryFails_viewModelShowsError()
   ```

6. **Keep tests small and focused**
   ```kotlin
   One assertion per test where possible
   ```

7. **Test pure functions first**
   ```kotlin
   // Domain logic has no dependencies
   ```

### ❌ DON'T:

1. **Test private methods**
   ```kotlin
   // Private methods are implementation details
   ```

2. **Create fragile tests**
   ```kotlin
   // Don't test exact error messages
   ```

3. **Use real external services**
   ```kotlin
   // Always mock databases, APIs, etc.
   ```

4. **Skip error case testing**
   ```kotlin
   // Test what happens when things fail
   ```

5. **Write non-deterministic tests**
   ```kotlin
   // Tests should be repeatable and consistent
   ```

---

## Test Coverage Goals

| Layer | Recommendation | Why |
|-------|---|---|
| **Domain** | 90%+ | Pure logic, easy to test, high value |
| **Data/Repositories** | 80%+ | Core business logic, testable with mocks |
| **ViewModel** | 70%+ | State management, test with mocked repo |
| **UI/Components** | 50%+ | UI logic is often framework-specific |

---

## Testing Principles That Carry Forward

✅ **Test behavior, not implementation** - Focus on "what" not "how"  
✅ **Use AAA pattern** - Clear test structure  
✅ **Mock external dependencies** - Test in isolation  
✅ **Test error cases** - Coverage includes failures  
✅ **Keep tests small** - One thing per test  
✅ **Descriptive names** - Tests document expected behavior  
✅ **Pure functions first** - Domain logic is easiest to test  

**Key**: These testing practices apply regardless of framework or language.
