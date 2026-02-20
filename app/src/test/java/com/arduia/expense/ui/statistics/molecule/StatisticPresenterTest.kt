package com.arduia.expense.ui.statistics.molecule

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.model.Result
import com.arduia.expense.ui.common.filter.Sorting
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.statistics.CategoryAnalyzer
import com.arduia.expense.ui.statistics.CategoryStatisticUiModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

class StatisticPresenterTest {

    private lateinit var expenseRepo: ExpenseRepository
    private lateinit var categoryAnalyzer: CategoryAnalyzer
    private lateinit var dateRangeFormatter: DateRangeFormatter

    @Before
    fun setup() {
        expenseRepo = mockk(relaxed = true)
        categoryAnalyzer = mockk(relaxed = true)
        dateRangeFormatter = mockk(relaxed = true)

        coEvery { expenseRepo.getExpenseTotalCount() } returns flowOf(Result.Success(0))
        coEvery { expenseRepo.getMaxAndMiniDateRange() } returns flowOf(Result.Error(RuntimeException("No data")))
    }

    @Test
    fun `initial state is correct when no data exists`() = runTest {
        val presenter = StatisticPresenter(expenseRepo, categoryAnalyzer, dateRangeFormatter)
        val events = MutableSharedFlow<StatisticEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            // Drop initial loading renders
            skipItems(1)
            val state = awaitItem()
            
            assertEquals(true, state.isEmptyExpenseData)
            assertEquals(0, state.categoryStatisticList.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter dialog shown on FilterIconClicked and hidden on Dismissed`() = runTest {
        val presenter = StatisticPresenter(expenseRepo, categoryAnalyzer, dateRangeFormatter)
        val events = MutableSharedFlow<StatisticEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            // Read until settled
            skipItems(2)
            
            events.emit(StatisticEvent.FilterIconClicked)
            val showState = awaitItem()
            assertEquals(true, showState.showFilterDialogForInfo != null)

            events.emit(StatisticEvent.FilterDialogDismissed)
            val hideState = awaitItem()
            assertEquals(null, hideState.showFilterDialogForInfo)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
