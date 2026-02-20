package com.arduia.expense.ui.settings.molecule

sealed interface SettingsEvent {
    object ChooseThemeClicked : SettingsEvent
    data class ThemeSelected(val mode: Int) : SettingsEvent
    object ThemeDialogDismissed : SettingsEvent
    object OpenNewUpdateInfoClicked : SettingsEvent
    object AboutUpdateDismissed : SettingsEvent
    object RestartHandled : SettingsEvent
}
