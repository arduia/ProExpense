package com.arduia.expense.ui.entry.molecule

import com.arduia.expense.ui.common.category.ExpenseCategory

sealed interface ExpenseEntryEvent {
    // Mode initialization
    data class ChooseSaveMode(val expenseId: Int) : ExpenseEntryEvent
    data class ChooseUpdateMode(val expenseId: Int) : ExpenseEntryEvent

    // Text input changes
    data class NameChanged(val value: String) : ExpenseEntryEvent
    data class AmountChanged(val value: String) : ExpenseEntryEvent
    data class NoteChanged(val value: String) : ExpenseEntryEvent

    // Category selection
    data class CategorySelected(val category: ExpenseCategory) : ExpenseEntryEvent

    // Primary actions
    object SaveClicked : ExpenseEntryEvent
    object UpdateClicked : ExpenseEntryEvent

    // Repeat entry toggle
    data class RepeatToggled(val isChecked: Boolean) : ExpenseEntryEvent

    // Date/time pickers
    object DateSelectClicked : ExpenseEntryEvent
    object TimeSelectClicked : ExpenseEntryEvent
    data class DateSelected(val timeMillis: Long) : ExpenseEntryEvent
    data class TimeSelected(val hour: Int, val minute: Int) : ExpenseEntryEvent
    object DatePickerDismissed : ExpenseEntryEvent
    object TimePickerDismissed : ExpenseEntryEvent

    // One-shot state clears
    object SnackbarShown : ExpenseEntryEvent
    object NavigationHandled : ExpenseEntryEvent
}
