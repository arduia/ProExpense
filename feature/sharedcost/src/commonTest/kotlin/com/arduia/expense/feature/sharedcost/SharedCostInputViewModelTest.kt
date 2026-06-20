/**
 * Domain rules (design plan D11 / Shared Costs):
 * - Even split distributes remainder to first participants.
 * - Custom split must sum exactly to total (over/under messages).
 * - Max 20 people per shared cost.
 * - Zero total shows validation; custom mismatch blocks save.
 */
package com.arduia.expense.feature.sharedcost

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.SharedCost
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SharedCostInputViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun onCalculate_customSplit_persistsCustomShares() = runTest(dispatcher) {
        val repository = FakeSharedCostRepository()
        var calculatedId: String? = null
        val viewModel = SharedCostInputViewModel(
            sharedCostRepository = repository,
            homeCurrencyCode = "USD",
            nowEpochMillis = { 1L },
            scope = this,
            onCalculated = { calculatedId = it },
        )

        viewModel.onAmountChange("100")
        viewModel.onSplitModeChanged(SplitMode.CUSTOM)
        viewModel.onCustomAmountChange(0, "60")
        viewModel.onCustomAmountChange(1, "40")
        viewModel.onCalculate()
        advanceUntilIdle()

        assertNotNull(calculatedId)
        assertEquals(SplitMode.CUSTOM, repository.lastInput?.splitMode)
        assertEquals(listOf(6000L, 4000L), repository.lastInput?.customShareCents)
    }

    @Test
    fun incrementPeople_capsAtTwenty() = runTest(dispatcher) {
        val viewModel = SharedCostInputViewModel(
            sharedCostRepository = FakeSharedCostRepository(),
            homeCurrencyCode = "USD",
            nowEpochMillis = { 1L },
            scope = this,
            onCalculated = {},
        )

        repeat(25) { viewModel.incrementPeople() }

        assertEquals(20, viewModel.uiState.value.peopleCount)
    }

    @Test
    fun onCalculate_zeroTotal_showsValidation() = runTest(dispatcher) {
        val repository = FakeSharedCostRepository()
        val viewModel = SharedCostInputViewModel(
            sharedCostRepository = repository,
            homeCurrencyCode = "USD",
            nowEpochMillis = { 1L },
            scope = this,
            onCalculated = {},
        )

        viewModel.onCalculate()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showZeroValidation)
        assertNull(repository.lastInput)
    }

    @Test
    fun onCalculate_evenSplit_usesNullCustomShares() = runTest(dispatcher) {
        val repository = FakeSharedCostRepository()
        val viewModel = SharedCostInputViewModel(
            sharedCostRepository = repository,
            homeCurrencyCode = "USD",
            nowEpochMillis = { 1L },
            scope = this,
            onCalculated = {},
        )

        viewModel.onAmountChange("100")
        viewModel.onCalculate()
        advanceUntilIdle()

        assertNull(repository.lastInput?.customShareCents)
        assertEquals(SplitMode.EVEN, repository.lastInput?.splitMode)
    }
}

private class FakeSharedCostRepository : SharedCostRepository {
    var lastInput: SharedCostInput? = null

    override suspend fun create(input: SharedCostInput): Result<SharedCost> {
        lastInput = input
        return Result.Success(
            SharedCost(
                id = "shared_1",
                title = input.title,
                totalAmount = input.totalAmount,
                currency = input.currency,
                participants = input.participants,
                recordedAtEpochMillis = input.recordedAtEpochMillis,
                customShareCents = input.customShareCents,
            ),
        )
    }

    override suspend fun getAll(): Result<List<SharedCost>> = Result.Success(emptyList())

    override suspend fun getSettlement(sharedCostId: String): Result<SettlementSummary> =
        Result.Error("unused")
}
