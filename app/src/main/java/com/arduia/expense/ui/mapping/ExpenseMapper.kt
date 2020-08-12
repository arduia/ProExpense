package com.arduia.expense.ui.mapping

import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.entry.ExpenseUpdateDataVto
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto

interface ExpenseMapper {

    fun mapToVto(expenseEnt: ExpenseEnt): ExpenseVto

    fun mapToDetailVto(expenseEnt: ExpenseEnt): ExpenseDetailsVto

    fun mapToUpdateDetailVto(expenseEnt: ExpenseEnt): ExpenseUpdateDataVto

}
