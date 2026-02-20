package com.arduia.expense.ui.home.molecule

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.model.SuccessResult
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.home.ExpenseDetailUiModelMapperFactory
import com.arduia.expense.ui.home.ExpenseRateCalculator
import com.arduia.expense.ui.home.ExpenseUiModelMapperFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.text.NumberFormat
import com.arduia.expense.utils.MainDispatcherRule

class HomePresenterTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var currencyRepository: CurrencyRepository
    private lateinit var expenseVoMapperFactory: ExpenseUiModelMapperFactory
    private lateinit var expenseDetailMapperFactory: ExpenseDetailUiModelMapperFactory
    private lateinit var repo: ExpenseRepository
    private lateinit var currencyFormatter: NumberFormat
    private lateinit var dateRangeFormatter: DateRangeFormatter
    private lateinit var calculatorFactory: ExpenseRateCalculator.Factory
    private lateinit var calculator: ExpenseRateCalculator
    private lateinit var presenter: HomePresenter

    @Before
    fun setup() {
        currencyRepository = mockk(relaxed = true)
        expenseVoMapperFactory = mockk(relaxed = true)
        expenseDetailMapperFactory = mockk(relaxed = true)
        repo = mockk(relaxed = true)
        currencyFormatter = mockk(relaxed = true)
        dateRangeFormatter = mockk(relaxed = true)
        calculatorFactory = mockk(relaxed = true)
        calculator = mockk(relaxed = true)

        every { calculatorFactory.create(any()) } returns calculator
        every { calculator.getRates() } returns flowOf(mapOf(1 to 10))
        
        coEvery { currencyRepository.getSelectedCacheCurrency() } returns flowOf(SuccessResult(CurrencyDto(1, "US Dollar", "USD", "$", "840")))
        coEvery { repo.getWeekExpenses() } returns flowOf(SuccessResult(emptyList()))
        coEvery { repo.getRecentExpense() } returns flowOf(SuccessResult(emptyList()))

        every { dateRangeFormatter.format(any(), any()) } returns "DEC 1 - DEC 7"
        
        presenter = HomePresenter(
            currencyRepository,
            expenseVoMapperFactory,
            expenseDetailMapperFactory,
            repo,
            currencyFormatter,
            dateRangeFormatter,
            calculatorFactory
        )
    }

    @Test
    fun `initial state sets default values and date range`() = runTest {
        val events = MutableSharedFlow<HomeEvent>()
        
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            val initialState = awaitItem()
            
            // Wait for background LaunchedEffects to update based on flow emits
            val loadedState = awaitItem() 

            assertEquals("DEC 1 - DEC 7", loadedState.graphUiModel?.dateRange)
            assertFalse(loadedState.openDrawer)
            assertFalse(loadedState.navigateToLogs)
        }
    }

    @Test
    fun `NavIconClicked event sets openDrawer to true`() = runTest {
        val events = MutableSharedFlow<HomeEvent>()
        
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            awaitItem() // Ignore initial
            awaitItem() // Ignore background emit

            events.emit(HomeEvent.NavIconClicked)
            
            val stateAfterEvent = awaitItem()
            assertTrue(stateAfterEvent.openDrawer)
        }
    }

    @Test
    fun `MoreLogsClicked event sets navigateToLogs to true`() = runTest {
        val events = MutableSharedFlow<HomeEvent>()
        
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            awaitItem() // Ignore initial
            awaitItem() // Ignore background emit

            events.emit(HomeEvent.MoreLogsClicked)
            
            val stateAfterEvent = awaitItem()
            assertTrue(stateAfterEvent.navigateToLogs)
        }
    }

    @Test
    fun `AddExpenseClicked event sets navigateToEntryId to -1`() = runTest {
        val events = MutableSharedFlow<HomeEvent>()
        
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            awaitItem() // Ignore initial
            awaitItem() // Ignore background

            events.emit(HomeEvent.AddExpenseClicked)
            
            val stateAfterEvent = awaitItem()
            assertEquals(-1, stateAfterEvent.navigateToEntryId)
            assertEquals(null, stateAfterEvent.showDetailDialogFor)
        }
    }

    @Test
    fun `EditExpenseClicked event sets navigateToEntryId to expenseId`() = runTest {
        val events = MutableSharedFlow<HomeEvent>()
        
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            awaitItem() // Ignore initial
            awaitItem() // Ignore background

            events.emit(HomeEvent.EditExpenseClicked(123))
            
            val stateAfterEvent = awaitItem()
            assertEquals(123, stateAfterEvent.navigateToEntryId)
            assertEquals(null, stateAfterEvent.showDetailDialogFor)
        }
    }

    @Test
    fun `RecentItemClicked event fetches detail and shows dialog`() = runTest {
        val expenseEnt = mockk<com.arduia.expense.data.local.ExpenseEnt>(relaxed = true)
        val detailUiModel = mockk<com.arduia.expense.ui.common.expense.ExpenseDetailUiModel>(relaxed = true)
        coEvery { repo.getExpense(99) } returns flowOf(SuccessResult(expenseEnt))
        every { expenseDetailMapperFactory.create(any()) } returns mockk {
            every { map(expenseEnt) } returns detailUiModel
        }

        val events = MutableSharedFlow<HomeEvent>()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            awaitItem() // Ignore initial
            awaitItem() // Ignore background

            val uiModel = com.arduia.expense.ui.expenselogs.ExpenseUiModel(99, "", "", 0, "", "", "")
            events.emit(HomeEvent.RecentItemClicked(uiModel))
            
            val stateAfterEvent = awaitItem()
            assertEquals(detailUiModel, stateAfterEvent.showDetailDialogFor)
        }
    }

    @Test
    fun `DeleteExpensePrepared shows delete confirm dialog`() = runTest {
        val events = MutableSharedFlow<HomeEvent>()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            awaitItem() // Ignore initial
            awaitItem() // Ignore background

            events.emit(HomeEvent.DeleteExpensePrepared(42))
            
            val stateAfterEvent = awaitItem()
            assertEquals(1, stateAfterEvent.showDeleteConfirmFor?.itemTotal)
            assertEquals(null, stateAfterEvent.showDetailDialogFor)
        }
    }

    @Test
    fun `DeleteExpenseConfirmed deletes and shows snackbar`() = runTest {
        coEvery { repo.deleteExpenseById(any()) } returns Unit
        val events = MutableSharedFlow<HomeEvent>()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            awaitItem() // Ignore initial
            awaitItem() // Ignore background

            // First prepare
            events.emit(HomeEvent.DeleteExpensePrepared(42))
            awaitItem()

            // Then confirm
            events.emit(HomeEvent.DeleteExpenseConfirmed)
            val stateAfterEvent = awaitItem()
            
            assertEquals("Item Deleted", stateAfterEvent.showSnackbar)
            assertEquals(null, stateAfterEvent.showDeleteConfirmFor)
        }
    }

    @Test
    fun `NavigationHandled resets navigation states`() = runTest {
        val events = MutableSharedFlow<HomeEvent>()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            awaitItem() // Ignore initial
            awaitItem() // Ignore background
            
            // Set some true states
            events.emit(HomeEvent.NavIconClicked)
            awaitItem()
            
            events.emit(HomeEvent.NavigationHandled)
            val resetState = awaitItem()
            
            assertFalse(resetState.openDrawer)
            assertFalse(resetState.navigateToLogs)
            assertEquals(null, resetState.navigateToEntryId)
        }
    }

    @Test
    fun `Dialog Dismissed events reset dialog states`() = runTest {
        val expenseEnt = mockk<com.arduia.expense.data.local.ExpenseEnt>(relaxed = true)
        val detailUiModel = mockk<com.arduia.expense.ui.common.expense.ExpenseDetailUiModel>(relaxed = true)
        coEvery { repo.getExpense(99) } returns flowOf(SuccessResult(expenseEnt))
        every { expenseDetailMapperFactory.create(any()) } returns mockk {
            every { map(expenseEnt) } returns detailUiModel
        }
        coEvery { repo.deleteExpenseById(any()) } returns Unit

        val events = MutableSharedFlow<HomeEvent>()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            awaitItem() // Ignore initial
            awaitItem() // Ignore background
            
            // 1. Show Detail
            val uiModel = com.arduia.expense.ui.expenselogs.ExpenseUiModel(99, "", "", 0, "", "", "")
            events.emit(HomeEvent.RecentItemClicked(uiModel))
            awaitItem() // State with showDetailDialogFor != null
            
            events.emit(HomeEvent.DetailDialogDismissed)
            val state1 = awaitItem()
            assertEquals(null, state1.showDetailDialogFor)

            // 2. Show Delete Confirm
            events.emit(HomeEvent.DeleteExpensePrepared(42))
            awaitItem() // State with showDeleteConfirmFor != null

            events.emit(HomeEvent.DeleteDialogDismissed)
            val state2 = awaitItem()
            assertEquals(null, state2.showDeleteConfirmFor)
            
            // 3. Show Snackbar
            events.emit(HomeEvent.DeleteExpensePrepared(42))
            awaitItem()
            events.emit(HomeEvent.DeleteExpenseConfirmed)
            awaitItem() // State with showSnackbar != null

            events.emit(HomeEvent.SnackbarShown)
            val state3 = awaitItem()
            assertEquals(null, state3.showSnackbar)
        }
    }
}
