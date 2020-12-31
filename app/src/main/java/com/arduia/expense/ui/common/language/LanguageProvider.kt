package com.arduia.expense.ui.common.language

interface LanguageProvider {

    fun getLanguageVtoByID(id: String): LanguageVto

    fun getAvailableLanguages(): List<LanguageVto>

}