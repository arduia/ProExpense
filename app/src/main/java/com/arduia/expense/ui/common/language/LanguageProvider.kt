package com.arduia.expense.ui.common.language

interface LanguageProvider {

    fun getLanguageVtoByID(id: String): LanguageUiModel

    fun getAvailableLanguages(): List<LanguageUiModel>

}