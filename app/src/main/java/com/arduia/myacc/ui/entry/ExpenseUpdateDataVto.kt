package com.arduia.myacc.ui.entry

import com.arduia.myacc.ui.vto.ExpenseCategory

class ExpenseUpdateDataVto(val id: Int,
                           val name: String,
                           val date: Long,
                           val category: ExpenseCategory,
                           val amount: String,
                           val note: String)