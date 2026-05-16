package com.arduia.expense.ui.component.dialog

sealed class AppTheme {
    object Light : AppTheme()
    object Dark : AppTheme()
    object System : AppTheme()
}
