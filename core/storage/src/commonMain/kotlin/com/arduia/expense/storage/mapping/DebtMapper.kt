package com.arduia.expense.storage.mapping

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.Money
import com.arduia.expense.storage.db.Debt_record as DebtRow

internal fun DebtRow.toDomain(): Debt =
    Debt(
        id = DebtId(id),
        personName = person_name,
        money = Money(Amount(amount_cents), CurrencyCode(currency_code)),
        direction = direction.toDebtDirectionFromCode(),
        dueEpochMillis = due_epoch_millis,
        isSettled = is_settled != 0L,
    )

/**
 * Enum codec for DebtDirection — explicit stable codes (OWED_TO_ME=0, I_OWE=1).
 * Never use Kotlin .ordinal as that changes when enum members are reordered.
 */
internal fun Long.toDebtDirectionFromCode(): DebtDirection = when (this) {
    0L -> DebtDirection.OWED_TO_ME
    1L -> DebtDirection.I_OWE
    else -> error("Unknown DebtDirection code: $this")
}

internal fun DebtDirection.toCode(): Long = when (this) {
    DebtDirection.OWED_TO_ME -> 0L
    DebtDirection.I_OWE -> 1L
}
