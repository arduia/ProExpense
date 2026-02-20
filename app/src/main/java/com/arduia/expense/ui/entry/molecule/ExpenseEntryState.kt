package com.arduia.expense.ui.entry.molecule

import com.arduia.expense.ui.common.category.ExpenseCategory
import com.arduia.expense.ui.entry.ExpenseEntryMode
import java.util.Calendar

data class ExpenseEntryState(
    val mode: ExpenseEntryMode = ExpenseEntryMode.INSERT,
    val name: String = "",
    val amount: String = "",
    val note: String = "",
    val selectedCategory: ExpenseCategory? = null,
    val categories: List<ExpenseCategory> = emptyList(),
    val currencySymbol: String = "",
    val entryDateTimeMillis: Long = System.currentTimeMillis(),
    val isRepeatEnabled: Boolean = false,
    val amountError: String? = null,
    val showDatePicker: Calendar? = null,
    val showTimePicker: Calendar? = null,
    val navigateBack: Boolean = false,
    val showSnackbar: String? = null
) {
    val isSaveEnabled: Boolean
        get() = amount.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0.0
}
