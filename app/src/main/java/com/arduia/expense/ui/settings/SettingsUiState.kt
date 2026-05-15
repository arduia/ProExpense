package com.arduia.expense.ui.settings

import com.arduia.expense.ui.component.dialog.AppTheme

data class SettingsUiState(
    val currentCurrency: String = "",
    val currentLanguage: String = "",
    val currentTheme: AppTheme = AppTheme.System,
    val appVersion: String = "",
)

sealed interface SettingsUiEvent {
    data object NavigateToCurrencyPicker : SettingsUiEvent
    data object NavigateToLanguagePicker : SettingsUiEvent
    data object NavigateToAbout : SettingsUiEvent
    data object NavigateToFeedback : SettingsUiEvent
    data object NavigateToPrivacyPolicy : SettingsUiEvent
    data object ShowThemePicker : SettingsUiEvent
}
