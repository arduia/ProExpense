package com.arduia.expense.ui.settings.molecule

import com.arduia.expense.ui.about.AboutUpdateUiModel

data class SettingsState(
    val selectedLanguageId: String = "",
    val currencyValue: String = "",
    val isNewVersionAvailable: Boolean = false,
    val showThemeDialogForMode: Int? = null,
    val restartRequired: Boolean = false,
    val showAboutUpdate: AboutUpdateUiModel? = null
)
