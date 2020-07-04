package com.arduia.expense.ui.entry

import com.arduia.expense.ui.vto.ExpenseCategory

class ExpenseUpdateDataVto(val id: Int,
                           val name: String,
                           val date: Long,
                           val category: ExpenseCategory,
                           val amount: String,
                           val note: String)
