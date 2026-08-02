package com.arduia.expense.shell

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.RecordChangeSignal
import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.RecordPageFilter
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordType
import com.arduia.expense.shared.currentEpochMillis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Backbone coverage for Home's projection.
 *
 * Traceability: US-HOME-1 (month-to-date spend header, budget progress) and US-HOME-2 (recent
 * entries list is capped, not the full history).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val now = currentEpochMillis()

    private fun record(
        id: String,
        cents: Long,
        type: RecordType = RecordType.EXPENSE,
        atEpochMillis: Long = now,
    ): FinanceRecord {
        val money = Money(Amount(cents), CurrencyCode("USD"))
        return FinanceRecord(
            id = RecordId(id),
            money = money,
            homeCurrencyMoney = money,
            categoryId = CategoryId("food"),
            type = type,
            note = "Lunch $id",
            recordedAtEpochMillis = atEpochMillis,
        )
    }

    private fun TestScope.viewModel(
        records: List<FinanceRecord>,
        budgetCents: Long? = null,
    ): HomeViewModel =
        HomeViewModel(
            financeRecordRepository = FakeRecordRepository(records),
            categoryRepository = FakeCategoryRepository(),
            profileRepository = FakeProfile(),
            budgetRepository = FakeBudgetRepository(budgetCents),
            currencySettingsRepository = FakeCurrencySettings(),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

    @Test
    fun `month spend sums expenses only and ignores income`() =
        runTest {
            val vm =
                viewModel(
                    listOf(
                        record("a", cents = 1_500),
                        record("b", cents = 2_500),
                        record("c", cents = 9_900, type = RecordType.INCOME),
                    ),
                )

            advanceUntilIdle()

            assertEquals("$40", vm.uiState.value.monthSpend)
        }

    @Test
    fun `recent rows are capped at the home limit`() =
        runTest {
            val many = (1..20).map { record("r$it", cents = 100) }

            val vm = viewModel(many)
            advanceUntilIdle()

            val rowCount =
                vm.uiState.value.dayGroups
                    .sumOf { it.rows.size }
            assertEquals(RECENT_HOME_LIMIT, rowCount)
        }

    @Test
    fun `budget summary reports over-budget when spend exceeds the budget`() =
        runTest {
            val vm = viewModel(listOf(record("a", cents = 15_000)), budgetCents = 10_000)

            advanceUntilIdle()

            val summary = assertNotNull(vm.uiState.value.budgetSummary)
            assertTrue(summary.isOverBudget)
            assertEquals(1f, summary.progress)
        }

    @Test
    fun `no budget configured yields no budget summary`() =
        runTest {
            val vm = viewModel(listOf(record("a", cents = 15_000)), budgetCents = null)

            advanceUntilIdle()

            assertNull(vm.uiState.value.budgetSummary)
        }

    @Test
    fun `empty history resolves to the empty state rather than staying loading`() =
        runTest {
            val vm = viewModel(emptyList())

            advanceUntilIdle()

            assertTrue(vm.uiState.value.isEmpty)
        }
}

private class FakeRecordRepository(
    records: List<FinanceRecord>,
) : FinanceRecordRepository {
    private val flow = MutableStateFlow(records)

    override fun observeAll(): Flow<List<FinanceRecord>> = flow

    override suspend fun getAll(): Result<List<FinanceRecord>> = Result.Success(flow.value)

    override suspend fun getById(id: RecordId): Result<FinanceRecord?> = Result.Success(flow.value.firstOrNull { it.id == id })

    override suspend fun upsert(record: FinanceRecord): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: RecordId): Result<Unit> = Result.Success(Unit)

    override suspend fun verifyIntegrity(id: RecordId): Result<Boolean> = Result.Success(true)

    override suspend fun getRecordsPage(
        filter: RecordPageFilter,
        cursor: RecordPageCursor?,
        limit: Int,
    ): Result<List<FinanceRecord>> = Result.Success(flow.value.take(limit))

    override suspend fun existsByCategory(categoryId: CategoryId): Result<Boolean> = Result.Success(false)

    override fun observeChangeSignal(): Flow<RecordChangeSignal> = flowOf(RecordChangeSignal(flow.value.size.toLong(), 0L))
}

private class FakeCategoryRepository : CategoryRepository {
    private val categories =
        listOf(
            Category(
                id = CategoryId("food"),
                name = "Food",
                iconId = "food",
                sortOrder = 0,
            ),
        )

    override suspend fun getAll(): Result<List<Category>> = Result.Success(categories)

    override suspend fun upsert(category: Category): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: CategoryId): Result<Unit> = Result.Success(Unit)

    override suspend fun reorder(orderedIds: List<CategoryId>): Result<Unit> = Result.Success(Unit)

    override fun observeAll(): Flow<List<Category>> = flowOf(categories)
}

private class FakeProfile : ProfileRepository {
    override suspend fun setDisplayName(name: String): Result<Unit> = Result.Success(Unit)

    override suspend fun getDisplayName(): Result<String> = Result.Success("Maya")

    override suspend fun isOnboardingComplete(): Result<Boolean> = Result.Success(true)

    override suspend fun setOnboardingComplete(): Result<Unit> = Result.Success(Unit)
}

private class FakeBudgetRepository(
    private val budgetCents: Long?,
) : BudgetRepository {
    override suspend fun getMonthlyBudget(): Result<Money?> = Result.Success(budgetCents?.let { Money(Amount(it), CurrencyCode("USD")) })

    override suspend fun setMonthlyBudget(money: Money?): Result<Unit> = Result.Success(Unit)
}

private class FakeCurrencySettings : CurrencySettingsRepository {
    override suspend fun getHomeCurrency(): Result<CurrencyCode?> = Result.Success(CurrencyCode("USD"))

    override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> = Result.Success(Unit)
}
