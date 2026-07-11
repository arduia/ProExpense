package com.arduia.expense.storage.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.storage.catchingResult
import com.arduia.expense.storage.db.DebtQueries
import com.arduia.expense.storage.mapping.toCode
import com.arduia.expense.storage.mapping.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** Pragmatic reuse of existing default categories — mirrors Shared Costs' "shopping" default. The
 * row's badge is link-driven (see [RecordLink.ToDebt]), not category-driven, so this only affects
 * category-filter bucketing in Journal/Reports, never how the row itself renders. */
private const val DEBT_LENT_DEFAULT_CATEGORY_ID = "shopping"
private const val DEBT_OWED_DEFAULT_CATEGORY_ID = "gift"

class SqlDelightDebtRepository(
    private val queries: DebtQueries,
    private val financeRecordRepository: FinanceRecordRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : DebtRepository {
    override suspend fun getAll(): Result<List<Debt>> =
        withContext(dispatcher) {
            catchingResult { queries.selectAllDebts().executeAsList().map { it.toDomain() } }
        }

    override suspend fun getById(id: DebtId): Result<Debt?> =
        withContext(dispatcher) {
            catchingResult { queries.selectDebtById(id.value).executeAsOneOrNull()?.toDomain() }
        }

    override suspend fun upsert(debt: Debt): Result<Unit> =
        withContext(dispatcher) {
            catchingResult {
                queries.insertDebt(
                    id = debt.id.value,
                    person_name = debt.personName,
                    amount_cents = debt.money.amount.valueInCents,
                    currency_code = debt.money.currency.code,
                    direction = debt.direction.toCode(),
                    due_epoch_millis = debt.dueEpochMillis,
                    is_settled = if (debt.isSettled) 1L else 0L,
                    note = debt.note,
                    recorded_at = debt.recordedAtEpochMillis,
                    record_as_transaction = if (debt.recordAsTransaction) 1L else 0L,
                )
                if (debt.recordAsTransaction) {
                    financeRecordRepository.upsert(debt.toFinanceRecord())
                } else {
                    // No-op if none exists yet — also handles a toggle-on -> toggle-off edit by
                    // removing a previously-created linked record.
                    financeRecordRepository.delete(RecordId(debt.id.value))
                }
                Unit
            }
        }

    override suspend fun delete(id: DebtId): Result<Unit> =
        withContext(dispatcher) {
            catchingResult {
                queries.deleteDebt(id.value)
                // No-op if this debt never had record_as_transaction set.
                financeRecordRepository.delete(RecordId(id.value))
                Unit
            }
        }

    override suspend fun findByPersonName(personName: String): Result<List<Debt>> =
        withContext(dispatcher) {
            catchingResult {
                queries.selectDebtsByPerson(personName).executeAsList().map { it.toDomain() }
            }
        }

    override fun observeAll(): Flow<List<Debt>> =
        queries
            .selectAllDebts()
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.mapNotNull { runCatching { it.toDomain() }.getOrNull() } }

    /**
     * Direction decides cash-flow sign: [DebtDirection.OWED_TO_ME] means money left the user's
     * pocket to lend it out (EXPENSE); [DebtDirection.I_OWE] means money came in by borrowing it
     * (INCOME). Reuses the [DebtId] as the [RecordId] so repeated upserts always target the same
     * record instead of accumulating duplicates — same trick as Shared Costs.
     */
    private fun Debt.toFinanceRecord(): FinanceRecord =
        FinanceRecord(
            id = RecordId(id.value),
            money = money,
            homeCurrencyMoney = money,
            categoryId =
                CategoryId(
                    if (direction == DebtDirection.OWED_TO_ME) {
                        DEBT_LENT_DEFAULT_CATEGORY_ID
                    } else {
                        DEBT_OWED_DEFAULT_CATEGORY_ID
                    },
                ),
            type = if (direction == DebtDirection.OWED_TO_ME) RecordType.EXPENSE else RecordType.INCOME,
            note = if (direction == DebtDirection.OWED_TO_ME) "Lent to $personName" else "Borrowed from $personName",
            recordedAtEpochMillis = recordedAtEpochMillis,
            link = RecordLink.ToDebt(id),
        )
}
