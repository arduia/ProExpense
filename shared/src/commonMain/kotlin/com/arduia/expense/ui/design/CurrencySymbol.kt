package com.arduia.expense.ui.design

import com.arduia.expense.shared.CurrencyCatalog

fun currencySymbol(code: String): String = CurrencyCatalog.symbolFor(code)
