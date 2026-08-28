package com.arduia.expense.shell

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.ClearDataRepository
import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.LocaleRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.RecordChangeSignal
import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.RecordPageFilter
import com.arduia.expense.data.Result
import com.arduia.expense.data.SettlementSummary
import com.arduia.expense.data.SharedCostInput
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.data.ThemeMode
import com.arduia.expense.data.ThemeRepository
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
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.feature.auth.PinAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * In-memory repository fakes shared by every ViewModel test in this module.
 *
 * Per the testing contract, fakes sit at the repository boundary rather than mocking storage
 * internals. They are stateful — a write is visible to the next read and re-emits on the observed
 * flow — so a test can assert what a ViewModel actually persisted, not just that it called through.
 */

const val USD = "USD"

fun money(cents: Long): Money = Money(Amount(cents), CurrencyCode(USD))

class FakeRecords(
    records: List<FinanceRecord> = emptyList(),
) : FinanceRecordRepository {
    private val flow = MutableStateFlow(records)

    val current: List<FinanceRecord> get() = flow.value

    override fun observeAll(): Flow<List<FinanceRecord>> = flow

    override suspend fun getAll(): Result<List<FinanceRecord>> = Result.Success(flow.value)

    override suspend fun getById(id: RecordId): Result<FinanceRecord?> = Result.Success(flow.value.firstOrNull { it.id == id })

    override suspend fun upsert(record: FinanceRecord): Result<Unit> {
        flow.value = flow.value.filterNot { it.id == record.id } + record
        return Result.Success(Unit)
    }

    override suspend fun delete(id: RecordId): Result<Unit> {
        flow.value = flow.value.filterNot { it.id == id }
        return Result.Success(Unit)
    }

    override suspend fun verifyIntegrity(id: RecordId): Result<Boolean> = Result.Success(true)

    override suspend fun getRecordsPage(
        filter: RecordPageFilter,
        cursor: RecordPageCursor?,
        limit: Int,
    ): Result<List<FinanceRecord>> = Result.Success(flow.value.take(limit))

    override suspend fun existsByCategory(categoryId: CategoryId): Result<Boolean> =
        Result.Success(flow.value.any { it.categoryId == categoryId })

    override fun observeChangeSignal(): Flow<RecordChangeSignal> = flowOf(RecordChangeSignal(flow.value.size.toLong(), 0L))
}

class FakeCategories(
    categories: List<Category> = listOf(category("food", "Food"), category("travel", "Travel")),
) : CategoryRepository {
    private val flow = MutableStateFlow(categories)

    val current: List<Category> get() = flow.value

    override suspend fun getAll(): Result<List<Category>> = Result.Success(flow.value)

    override suspend fun upsert(category: Category): Result<Unit> {
        flow.value = flow.value.filterNot { it.id == category.id } + category
        return Result.Success(Unit)
    }

    override suspend fun delete(id: CategoryId): Result<Unit> {
        flow.value = flow.value.filterNot { it.id == id }
        return Result.Success(Unit)
    }

    override suspend fun reorder(orderedIds: List<CategoryId>): Result<Unit> {
        flow.value =
            flow.value.map { category ->
                val index = orderedIds.indexOf(category.id)
                if (index >= 0) category.copy(sortOrder = index) else category
            }
        return Result.Success(Unit)
    }

    override fun observeAll(): Flow<List<Category>> = flow

    companion object {
        fun category(
            id: String,
            name: String,
            isCustom: Boolean = false,
            sortOrder: Int = 0,
        ) = Category(id = CategoryId(id), name = name, isCustom = isCustom, sortOrder = sortOrder, iconId = id)
    }
}

class FakeProfile(
    private var name: String = "Maya",
    private var complete: Boolean = true,
    private val failOnComplete: Boolean = false,
) : ProfileRepository {
    override suspend fun setDisplayName(name: String): Result<Unit> {
        this.name = name
        return Result.Success(Unit)
    }

    override suspend fun getDisplayName(): Result<String> = Result.Success(name)

    override suspend fun isOnboardingComplete(): Result<Boolean> = Result.Success(complete)

    override suspend fun setOnboardingComplete(): Result<Unit> =
        if (failOnComplete) {
            Result.Error("disk full")
        } else {
            complete = true
            Result.Success(Unit)
        }
}

class FakeCurrencySettings(
    private var code: String = USD,
) : CurrencySettingsRepository {
    val current: String get() = code

    override suspend fun getHomeCurrency(): Result<CurrencyCode?> = Result.Success(CurrencyCode(code))

    override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> {
        code = currency.code
        return Result.Success(Unit)
    }
}

class FakeBudget(
    private var budgetCents: Long? = null,
) : BudgetRepository {
    override suspend fun getMonthlyBudget(): Result<Money?> = Result.Success(budgetCents?.let { money(it) })

    override suspend fun setMonthlyBudget(money: Money?): Result<Unit> {
        budgetCents = money?.amount?.valueInCents
        return Result.Success(Unit)
    }
}

class FakeEvents(
    events: List<Event> = emptyList(),
) : EventRepository {
    private val flow = MutableStateFlow(events)

    val current: List<Event> get() = flow.value

    override suspend fun getAll(): Result<List<Event>> = Result.Success(flow.value)

    override suspend fun getById(id: EventId): Result<Event?> = Result.Success(flow.value.firstOrNull { it.id == id })

    override suspend fun upsert(event: Event): Result<Unit> {
        flow.value = flow.value.filterNot { it.id == event.id } + event
        return Result.Success(Unit)
    }

    override suspend fun delete(id: EventId): Result<Unit> {
        flow.value = flow.value.filterNot { it.id == id }
        return Result.Success(Unit)
    }

    override fun observeAll(): Flow<List<Event>> = flow

    override suspend fun getSpent(id: EventId): Result<Money> = Result.Success(money(0))
}

class FakeDebts(
    debts: List<Debt> = emptyList(),
) : DebtRepository {
    private val flow = MutableStateFlow(debts)

    val current: List<Debt> get() = flow.value

    override suspend fun getAll(): Result<List<Debt>> = Result.Success(flow.value)

    override suspend fun getById(id: DebtId): Result<Debt?> = Result.Success(flow.value.firstOrNull { it.id == id })

    override suspend fun upsert(debt: Debt): Result<Unit> {
        flow.value = flow.value.filterNot { it.id == debt.id } + debt
        return Result.Success(Unit)
    }

    override suspend fun delete(id: DebtId): Result<Unit> {
        flow.value = flow.value.filterNot { it.id == id }
        return Result.Success(Unit)
    }

    override suspend fun findByPersonName(personName: String): Result<List<Debt>> =
        Result.Success(flow.value.filter { it.personName.equals(personName, ignoreCase = true) })

    override fun observeAll(): Flow<List<Debt>> = flow
}

class FakeSharedCosts(
    sharedCosts: List<SharedCost> = emptyList(),
) : SharedCostRepository {
    private val flow = MutableStateFlow(sharedCosts)

    val current: List<SharedCost> get() = flow.value

    override suspend fun create(input: SharedCostInput): Result<SharedCost> {
        val created =
            SharedCost(
                id = SharedCostId("sc-" + flow.value.size),
                title = input.title,
                total = input.total,
                participants = input.participants,
                splitStrategy = input.splitStrategy,
                recordedAtEpochMillis = input.recordedAtEpochMillis,
                recordAsTransaction = input.recordAsTransaction,
            )
        flow.value = flow.value + created
        return Result.Success(created)
    }

    override suspend fun getAll(): Result<List<SharedCost>> = Result.Success(flow.value)

    override suspend fun getById(id: SharedCostId): Result<SharedCost?> = Result.Success(flow.value.firstOrNull { it.id == id })

    override suspend fun update(sharedCost: SharedCost): Result<Unit> {
        flow.value = flow.value.map { if (it.id == sharedCost.id) sharedCost else it }
        return Result.Success(Unit)
    }

    override suspend fun delete(id: SharedCostId): Result<Unit> {
        flow.value = flow.value.filterNot { it.id == id }
        return Result.Success(Unit)
    }

    override suspend fun archive(id: SharedCostId): Result<Unit> {
        flow.value = flow.value.map { if (it.id == id) it.copy(isArchived = true) else it }
        return Result.Success(Unit)
    }

    override suspend fun getSettlement(sharedCostId: SharedCostId): Result<SettlementSummary> =
        Result.Success(SettlementSummary(sharedCostId, emptyList()))

    override fun observeAll(): Flow<List<SharedCost>> = flow
}

class FakeTheme(
    private var mode: ThemeMode = ThemeMode.SYSTEM,
) : ThemeRepository {
    override suspend fun getThemeMode(): Result<ThemeMode> = Result.Success(mode)

    override suspend fun setThemeMode(mode: ThemeMode): Result<Unit> {
        this.mode = mode
        return Result.Success(Unit)
    }
}

class FakeLocale(
    private var tag: String = "en",
) : LocaleRepository {
    override suspend fun getLanguageTag(): Result<String> = Result.Success(tag)

    override suspend fun setLanguageTag(languageTag: String): Result<Unit> {
        tag = languageTag
        return Result.Success(Unit)
    }
}

class FakeClearData(
    private val records: FakeRecords = FakeRecords(),
) : ClearDataRepository {
    var clearAllCalls: Int = 0
        private set

    override suspend fun clearExpenses(): Result<Unit> = Result.Success(Unit)

    override suspend fun clearEvents(): Result<Unit> = Result.Success(Unit)

    override suspend fun clearDebts(): Result<Unit> = Result.Success(Unit)

    override suspend fun clearSharedCosts(): Result<Unit> = Result.Success(Unit)

    override suspend fun clearAll(): Result<Unit> {
        clearAllCalls++
        records.current.forEach { records.delete(it.id) }
        return Result.Success(Unit)
    }
}

/**
 * PIN state backed by in-memory fields. [correctPin] is what [verifyPin] accepts; a `null` means
 * no PIN is configured.
 */
class FakePinAuth(
    private var correctPin: String? = "123456",
    private var lockoutUntilMs: Long? = null,
    private var stayUnlocked: Boolean = false,
    private val failPinLookup: Boolean = false,
) : PinAuthRepository {
    var securityQuestionId: String? = null
        private set
    var securityAnswer: String? = null
        private set
    var biometricEnrolled: Boolean = false
        private set
    private var failedAttempts = 0L

    val configuredPin: String? get() = correctPin

    override suspend fun isPinConfigured(): Result<Boolean> =
        if (failPinLookup) Result.Error("boom") else Result.Success(correctPin != null)

    override suspend fun setPin(pin: String): Result<Unit> {
        correctPin = pin
        return Result.Success(Unit)
    }

    override suspend fun verifyPin(pin: String): Result<Boolean> = Result.Success(pin == correctPin)

    override suspend fun clearPin(): Result<Unit> {
        correctPin = null
        return Result.Success(Unit)
    }

    override suspend fun setSecurityQuestion(
        questionId: String,
        answer: String,
    ): Result<Unit> {
        securityQuestionId = questionId
        securityAnswer = answer
        return Result.Success(Unit)
    }

    override suspend fun getSecurityQuestionId(): Result<String?> = Result.Success(securityQuestionId)

    override suspend fun verifySecurityAnswer(answer: String): Result<Boolean> = Result.Success(answer == securityAnswer)

    override suspend fun isBiometricEnrolled(): Result<Boolean> = Result.Success(biometricEnrolled)

    override suspend fun enrollBiometric(): Result<Unit> {
        biometricEnrolled = true
        return Result.Success(Unit)
    }

    override suspend fun clearBiometric(): Result<Unit> {
        biometricEnrolled = false
        return Result.Success(Unit)
    }

    override suspend fun isStayUnlockedInBackgroundEnabled(): Result<Boolean> = Result.Success(stayUnlocked)

    override suspend fun setStayUnlockedInBackgroundEnabled(enabled: Boolean): Result<Unit> {
        stayUnlocked = enabled
        return Result.Success(Unit)
    }

    override suspend fun getFailedAttemptCount(): Result<Long> = Result.Success(failedAttempts)

    override suspend fun incrementFailedAttempts(): Result<Unit> {
        failedAttempts++
        return Result.Success(Unit)
    }

    override suspend fun resetFailedAttempts(): Result<Unit> {
        failedAttempts = 0
        return Result.Success(Unit)
    }

    override suspend fun getLockoutUntilMs(): Result<Long?> = Result.Success(lockoutUntilMs)

    override suspend fun setLockoutUntilMs(lockedUntilMs: Long): Result<Unit> {
        lockoutUntilMs = lockedUntilMs
        return Result.Success(Unit)
    }
}
