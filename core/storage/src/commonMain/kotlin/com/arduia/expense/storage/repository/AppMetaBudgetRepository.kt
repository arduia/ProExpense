package com.arduia.expense.storage.repository

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.storage.catchingResult

class AppMetaBudgetRepository(
    private val store: AppMetaLocalStore,
) : BudgetRepository {
    override suspend fun getMonthlyBudget(): Result<Money?> =
        catchingResult {
            val snapshot = store.read()
            snapshot.monthlyBudgetCents?.let { cents ->
                Money(Amount(cents), CurrencyCode(snapshot.homeCurrencyCode))
            }
        }

    override suspend fun setMonthlyBudget(money: Money?): Result<Unit> =
        catchingResult {
            store.update { it.copy(monthlyBudgetCents = money?.amount?.valueInCents) }
            Unit
        }
}
