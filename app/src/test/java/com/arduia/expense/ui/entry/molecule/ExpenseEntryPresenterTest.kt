package com.arduia.expense.ui.entry.molecule

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.domain.Amount
import com.arduia.expense.model.Result
import com.arduia.expense.ui.common.category.ExpenseCategory
import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import com.arduia.expense.ui.entry.ExpenseEntryMode
import com.arduia.expense.ui.entry.ExpenseUpdateDataUiModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class ExpenseEntryPresenterTest {

    private lateinit var expenseRepo: ExpenseRepository
    private lateinit var currencyRepo: CurrencyRepository
    private lateinit var mapper: Mapper<ExpenseEnt, ExpenseUpdateDataUiModel>
    private lateinit var categoryProvider: ExpenseCategoryProvider

    private val foodCategory = ExpenseCategory(ExpenseCategory.FOOD, com.arduia.expense.R.string.food, com.arduia.expense.R.drawable.ic_food)
    private val transportCategory = ExpenseCategory(ExpenseCategory.TRANSPORTATION, com.arduia.expense.R.string.transportation, com.arduia.expense.R.drawable.ic_transportation)

    @Before
    fun setup() {
        expenseRepo = mockk(relaxed = true)
        currencyRepo = mockk()
        mapper = mockk()
        categoryProvider = mockk()

        every { categoryProvider.getCategoryList() } returns listOf(foodCategory, transportCategory)
        every { categoryProvider.getCategoryByID(ExpenseCategory.FOOD) } returns foodCategory
        coEvery { currencyRepo.getSelectedCacheCurrency() } returns flowOf(
            Result.Success(CurrencyDto(rank = 1, name = "US Dollar", code = "USD", symbol = "$", number = "840"))
        )
    }

    private fun createPresenter() = ExpenseEntryPresenter(
        expenseRepo, currencyRepo, mapper, categoryProvider
    )

    @Test
    fun `initial state has INSERT mode and default category`() = runTest {
        val presenter = createPresenter()
        val events = MutableSharedFlow<ExpenseEntryEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            var state = awaitItem()

            // Wait for categories to load
            while (state.categories.isEmpty()) {
                state = awaitItem()
            }

            assertEquals(ExpenseEntryMode.INSERT, state.mode)
            assertEquals("", state.name)
            assertEquals("", state.amount)
            assertNotNull(state.selectedCategory)
            assertEquals(ExpenseCategory.FOOD, state.selectedCategory?.id)
            assertTrue(state.categories.isNotEmpty())
            assertFalse(state.isRepeatEnabled)
            assertNull(state.amountError)
            assertFalse(state.navigateBack)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `save with empty amount sets amountError`() = runTest {
        val presenter = createPresenter()
        val events = MutableSharedFlow<ExpenseEntryEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            var state = awaitItem()
            while (state.categories.isEmpty()) {
                state = awaitItem()
            }

            events.emit(ExpenseEntryEvent.SaveClicked)
            val errorState = awaitItem()

            assertEquals("Empty Cost", errorState.amountError)
            assertFalse(errorState.navigateBack)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `save with valid data navigates back`() = runTest {
        val presenter = createPresenter()
        val events = MutableSharedFlow<ExpenseEntryEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            var state = awaitItem()
            while (state.categories.isEmpty()) {
                state = awaitItem()
            }

            events.emit(ExpenseEntryEvent.AmountChanged("25.50"))
            awaitItem() // amount changed

            events.emit(ExpenseEntryEvent.SaveClicked)
            val savedState = awaitItem()

            assertTrue(savedState.navigateBack)
            assertNull(savedState.amountError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `repeat mode save resets fields and shows snackbar`() = runTest {
        val presenter = createPresenter()
        val events = MutableSharedFlow<ExpenseEntryEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            var state = awaitItem()
            while (state.categories.isEmpty()) {
                state = awaitItem()
            }

            events.emit(ExpenseEntryEvent.RepeatToggled(true))
            awaitItem()

            events.emit(ExpenseEntryEvent.NameChanged("Coffee"))
            awaitItem()

            events.emit(ExpenseEntryEvent.AmountChanged("3.50"))
            awaitItem()

            events.emit(ExpenseEntryEvent.SaveClicked)
            val afterSave = awaitItem()

            // In repeat mode: fields reset, snackbar shown, NOT navigated back
            assertEquals("", afterSave.name)
            assertEquals("", afterSave.amount)
            assertEquals("Saved!", afterSave.showSnackbar)
            assertFalse(afterSave.navigateBack)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `date select clicked shows date picker`() = runTest {
        val presenter = createPresenter()
        val events = MutableSharedFlow<ExpenseEntryEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            var state = awaitItem()
            while (state.categories.isEmpty()) {
                state = awaitItem()
            }

            events.emit(ExpenseEntryEvent.DateSelectClicked)
            val pickerState = awaitItem()

            assertNotNull(pickerState.showDatePicker)
            assertNull(pickerState.showTimePicker)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `time select clicked shows time picker`() = runTest {
        val presenter = createPresenter()
        val events = MutableSharedFlow<ExpenseEntryEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            var state = awaitItem()
            while (state.categories.isEmpty()) {
                state = awaitItem()
            }

            events.emit(ExpenseEntryEvent.TimeSelectClicked)
            val pickerState = awaitItem()

            assertNull(pickerState.showDatePicker)
            assertNotNull(pickerState.showTimePicker)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
