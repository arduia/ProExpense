package com.arduia.expense.storage.mapping

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.Money
import com.arduia.expense.storage.db.Debt_record as DebtRow

internal fun DebtRow.toDomain(homeCurrency: CurrencyCode): Debt =
    Debt(
        id = DebtId(id),
        personName = person_name,
        money = Money(Amount(amount_cents), homeCurrency),
        direction = DebtDirection.valueOf(direction),
        dueEpochMillis = due_epoch_millis,
        isSettled = is_settled != 0L,
    )
