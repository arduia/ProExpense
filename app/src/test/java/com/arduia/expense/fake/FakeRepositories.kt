package com.arduia.expense.fake

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.data.UserProfile
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.SharedCost

class FakeFinanceRecordRepository(
    initial: List<FinanceRecord> = emptyList(),
) : FinanceRecordRepository {
    val store = linkedMapOf<String, FinanceRecord>().apply { initial.forEach { put(it.id, it) } }

    override suspend fun getAll(): Result<List<FinanceRecord>> =
        Result.Success(store.values.sortedByDescending { it.recordedAtEpochMillis })

    override suspend fun getById(id: String): Result<FinanceRecord?> = Result.Success(store[id])

    override suspend fun upsert(record: FinanceRecord): Result<Unit> {
        store[record.id] = record
        return Result.Success(Unit)
    }

    override suspend fun delete(id: String): Result<Unit> {
        store.remove(id)
        return Result.Success(Unit)
    }
}

class FakeBudgetRepository(private var budget: Amount? = null) : BudgetRepository {
    override suspend fun getMonthlyBudget(): Result<Amount?> = Result.Success(budget)
    override suspend fun setMonthlyBudget(amount: Amount?): Result<Unit> {
        budget = amount
        return Result.Success(Unit)
    }
}

class FakeProfileRepository(
    private var name: String = "",
    private var currency: CurrencyCode = CurrencyCode("USD"),
    private var onboardingCompleted: Boolean = false,
) : ProfileRepository {
    override suspend fun getProfile(): Result<UserProfile> =
        Result.Success(UserProfile(name, currency, onboardingCompleted))

    override suspend fun saveProfile(name: String, homeCurrency: CurrencyCode): Result<Unit> {
        this.name = name
        this.currency = homeCurrency
        return Result.Success(Unit)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean): Result<Unit> {
        onboardingCompleted = completed
        return Result.Success(Unit)
    }
}

class FakeSecurityStateReader(private val pinConfigured: Boolean = false) : SecurityStateReader {
    override suspend fun hasPinConfigured(): Boolean = pinConfigured
}

class FakeEventRepository(initial: List<Event> = emptyList()) : EventRepository {
    val store = linkedMapOf<String, Event>().apply { initial.forEach { put(it.id, it) } }

    override suspend fun getAll(): Result<List<Event>> = Result.Success(store.values.toList())
    override suspend fun getById(id: String): Result<Event?> = Result.Success(store[id])
    override suspend fun upsert(event: Event): Result<Unit> {
        store[event.id] = event
        return Result.Success(Unit)
    }

    override suspend fun delete(id: String): Result<Unit> {
        store.remove(id)
        return Result.Success(Unit)
    }
}

class FakeSharedCostRepository(initial: List<SharedCost> = emptyList()) : SharedCostRepository {
    val store = linkedMapOf<String, SharedCost>().apply { initial.forEach { put(it.id, it) } }

    override suspend fun getAll(): Result<List<SharedCost>> =
        Result.Success(store.values.sortedByDescending { it.recordedAtEpochMillis })

    override suspend fun upsert(sharedCost: SharedCost): Result<Unit> {
        store[sharedCost.id] = sharedCost
        return Result.Success(Unit)
    }

    override suspend fun delete(id: String): Result<Unit> {
        store.remove(id)
        return Result.Success(Unit)
    }
}

class FakeCategoryRepository(initial: List<Category> = emptyList()) : CategoryRepository {
    val store = linkedMapOf<String, Category>().apply { initial.forEach { put(it.id, it) } }

    override suspend fun getAll(): Result<List<Category>> = Result.Success(store.values.toList())
    override suspend fun upsert(category: Category): Result<Unit> {
        store[category.id] = category
        return Result.Success(Unit)
    }

    override suspend fun delete(id: String): Result<Unit> {
        store.remove(id)
        return Result.Success(Unit)
    }
}

class FakeDebtRepository(initial: List<Debt> = emptyList()) : DebtRepository {
    val store = linkedMapOf<String, Debt>().apply { initial.forEach { put(it.id, it) } }

    override suspend fun getAll(): Result<List<Debt>> = Result.Success(store.values.toList())
    override suspend fun getById(id: String): Result<Debt?> = Result.Success(store[id])
    override suspend fun upsert(debt: Debt): Result<Unit> {
        store[debt.id] = debt
        return Result.Success(Unit)
    }

    override suspend fun delete(id: String): Result<Unit> {
        store.remove(id)
        return Result.Success(Unit)
    }

    override suspend fun findByPersonName(personName: String): Result<List<Debt>> =
        Result.Success(store.values.filter { it.personName.equals(personName, ignoreCase = true) })
}
