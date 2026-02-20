package com.arduia.expense.ui.expenselogs.molecule

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.data.local.DateRangeDataModel
import com.arduia.expense.domain.filter.ExpenseDateRange
import com.arduia.expense.model.Result
import com.arduia.expense.ui.common.filter.DateRangeSortingEnt
import com.arduia.expense.ui.common.filter.Sorting
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.expenselogs.ExpenseMode
import com.arduia.expense.ui.home.ExpenseDetailUiModelMapperFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

class ExpenseLogsPresenterTest {

    private lateinit var expenseRepo: ExpenseRepository
    private lateinit var currencyRepo: CurrencyRepository
    private lateinit var dateRangeFormatter: DateRangeFormatter
    private lateinit var expenseDetailMapperFactory: ExpenseDetailUiModelMapperFactory

    @Before
    fun setup() {
        expenseRepo = mockk()
        currencyRepo = mockk()
        dateRangeFormatter = mockk()
        expenseDetailMapperFactory = mockk()

        // Default Mocks
        coEvery { expenseRepo.getExpenseTotalCount() } returns flowOf(Result.Success(10))
        val minDate = Date().time - 100000
        val maxDate = Date().time
        coEvery { expenseRepo.getMaxAndMiniDateRange() } returns flowOf(
            Result.Success(DateRangeDataModel(maxDate = maxDate, minDate = minDate))
        )
        every { dateRangeFormatter.format(any(), any()) } returns "Formatted Date"
        coEvery { currencyRepo.getSelectedCacheCurrency() } returns flowOf(
            Result.Success(CurrencyDto(rank = 1, name = "US Dollar", code = "USD", symbol = "$", number = "840"))
        )
    }

    private fun createPresenter() = ExpenseLogsPresenter(
        expenseRepo, currencyRepo, dateRangeFormatter, expenseDetailMapperFactory
    )

    @Test
    fun `initial state resolves properly based on repository`() = runTest {
        val presenter = createPresenter()
        val events = MutableSharedFlow<ExpenseLogsEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            var state = awaitItem()
            while (!state.isFilterEnabled || state.filterDateRangeText.isEmpty()) {
                state = awaitItem()
            }

            assertEquals(ExpenseMode.NORMAL, state.mode)
            assertEquals(true, state.isFilterEnabled)
            assertEquals("Formatted Date . DESC", state.filterDateRangeText)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `swipe selection changes mode and count`() = runTest {
        val presenter = createPresenter()
        val events = MutableSharedFlow<ExpenseLogsEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            var state = awaitItem()
            while (!state.isFilterEnabled || state.filterDateRangeText.isEmpty()) {
                state = awaitItem()
            }

            events.emit(ExpenseLogsEvent.SwipeSelectionCountChanged(2))
            val selectedState = awaitItem()
            
            assertEquals(2, selectedState.selectedCount)
            assertEquals(ExpenseMode.SELECTION, selectedState.mode)

            events.emit(ExpenseLogsEvent.SelectionCloseClicked)
            val normalState = awaitItem()
            assertEquals(0, normalState.selectedCount)
            assertEquals(ExpenseMode.NORMAL, normalState.mode)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `delete button shows confirm dialog`() = runTest {
        val presenter = createPresenter()
        val events = MutableSharedFlow<ExpenseLogsEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            var state = awaitItem()
            while (!state.isFilterEnabled || state.filterDateRangeText.isEmpty()) {
                state = awaitItem()
            }

            events.emit(ExpenseLogsEvent.SwipeSelectionCountChanged(5))
            val selectedState = awaitItem()
            assertEquals(5, selectedState.selectedCount)

            events.emit(ExpenseLogsEvent.DeleteButtonClicked)
            val confirmState = awaitItem()
            assertEquals(5, confirmState.showMultiDeleteConfirmCount)

            events.emit(ExpenseLogsEvent.MultiDeleteDismissed)
            val dismissedState = awaitItem()
            assertEquals(null, dismissedState.showMultiDeleteConfirmCount)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
