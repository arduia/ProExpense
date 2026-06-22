package com.arduia.expense.fake

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.data.UserProfile
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord

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
