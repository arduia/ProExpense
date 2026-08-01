package com.arduia.expense.shell.home

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.DefaultCategoryRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.RecordChangeSignal
import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.RecordPageFilter
import com.arduia.expense.data.Result
import com.arduia.expense.data.SettlementSummary
import com.arduia.expense.data.SharedCostInput
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.feature.currency.CurrencyRepository
import com.arduia.expense.feature.currency.CurrencySettings
import kotlinx.coroutines.flow.MutableStateFlow

/** Fakes at the repository boundary — the ViewModel is exercised through the real contracts. */
class FakeFinanceRecordRepository : FinanceRecordRepository {
    val flow = MutableStateFlow<List<FinanceRecord>>(emptyList())

    override suspend fun getAll(): Result<List<FinanceRecord>> = Result.Success(flow.value)

    override suspend fun getById(id: RecordId): Result<FinanceRecord?> = Result.Success(flow.value.find { it.id == id })

    override suspend fun upsert(record: FinanceRecord): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: RecordId): Result<Unit> = Result.Success(Unit)

    override fun observeAll() = flow

    override suspend fun verifyIntegrity(id: RecordId): Result<Boolean> = Result.Success(true)

    override suspend fun getRecordsPage(
        filter: RecordPageFilter,
        cursor: RecordPageCursor?,
        limit: Int,
    ): Result<List<FinanceRecord>> = Result.Success(flow.value.take(limit))

    override suspend fun existsByCategory(categoryId: CategoryId): Result<Boolean> =
        Result.Success(flow.value.any { it.categoryId == categoryId })

    override fun observeChangeSignal() = MutableStateFlow(RecordChangeSignal(flow.value.size.toLong(), 0L))
}

class FakeCategoryRepository : CategoryRepository {
    val flow = MutableStateFlow<List<Category>>(emptyList())

    override suspend fun getAll(): Result<List<Category>> = Result.Success(flow.value)

    override suspend fun upsert(category: Category): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: CategoryId): Result<Unit> = Result.Success(Unit)

    override suspend fun reorder(orderedIds: List<CategoryId>): Result<Unit> = Result.Success(Unit)

    override fun observeAll() = flow
}

class FakeEventRepository : EventRepository {
    val flow = MutableStateFlow<List<Event>>(emptyList())

    override suspend fun getAll(): Result<List<Event>> = Result.Success(flow.value)

    override suspend fun getById(id: EventId): Result<Event?> = Result.Success(flow.value.find { it.id == id })

    override suspend fun upsert(event: Event): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: EventId): Result<Unit> = Result.Success(Unit)

    override fun observeAll() = flow

    override suspend fun getSpent(id: EventId): Result<Money> = Result.Success(Money(Amount(0), CurrencyCode("USD")))
}

class FakeDebtRepository : DebtRepository {
    val flow = MutableStateFlow<List<Debt>>(emptyList())

    override suspend fun getAll(): Result<List<Debt>> = Result.Success(flow.value)

    override suspend fun getById(id: DebtId): Result<Debt?> = Result.Success(flow.value.find { it.id == id })

    override suspend fun upsert(debt: Debt): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: DebtId): Result<Unit> = Result.Success(Unit)

    override suspend fun findByPersonName(personName: String): Result<List<Debt>> =
        Result.Success(flow.value.filter { it.personName == personName })

    override fun observeAll() = flow
}

class FakeSharedCostRepository : SharedCostRepository {
    val flow = MutableStateFlow<List<SharedCost>>(emptyList())

    override suspend fun create(input: SharedCostInput): Result<SharedCost> = Result.Error("not used")

    override suspend fun getAll(): Result<List<SharedCost>> = Result.Success(flow.value)

    override suspend fun getById(id: SharedCostId): Result<SharedCost?> = Result.Success(flow.value.find { it.id == id })

    override suspend fun update(sharedCost: SharedCost): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: SharedCostId): Result<Unit> = Result.Success(Unit)

    override suspend fun archive(id: SharedCostId): Result<Unit> = Result.Success(Unit)

    override suspend fun getSettlement(sharedCostId: SharedCostId): Result<SettlementSummary> = Result.Error("not used")

    override fun observeAll() = flow
}

class FakeBudgetRepository(
    var budget: Result<Money?> = Result.Success(null),
) : BudgetRepository {
    override suspend fun getMonthlyBudget(): Result<Money?> = budget

    override suspend fun setMonthlyBudget(money: Money?): Result<Unit> = Result.Success(Unit)
}

class FakeDefaultCategoryRepository(
    var defaultCategoryId: Result<String?> = Result.Success(null),
) : DefaultCategoryRepository {
    override suspend fun getDefaultCategoryId(): Result<String?> = defaultCategoryId

    override suspend fun setDefaultCategoryId(categoryId: String?): Result<Unit> = Result.Success(Unit)
}

class FakeCurrencyRepository(
    var settings: Result<CurrencySettings> = Result.Success(CurrencySettings(CurrencyCode("USD"))),
) : CurrencyRepository {
    override suspend fun getSettings(): Result<CurrencySettings> = settings

    override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> = Result.Success(Unit)
}

/** Test data builders — cents in, domain out. */
fun testRecord(
    id: String,
    amountCents: Long,
    recordedAtEpochMillis: Long,
    type: RecordType = RecordType.EXPENSE,
    categoryId: String = "food",
    note: String? = null,
    link: RecordLink = RecordLink.None,
    currency: String = "USD",
): FinanceRecord =
    FinanceRecord(
        id = RecordId(id),
        money = Money(Amount(amountCents), CurrencyCode(currency)),
        homeCurrencyMoney = Money(Amount(amountCents), CurrencyCode("USD")),
        categoryId = CategoryId(categoryId),
        type = type,
        note = note,
        recordedAtEpochMillis = recordedAtEpochMillis,
        link = link,
    )
