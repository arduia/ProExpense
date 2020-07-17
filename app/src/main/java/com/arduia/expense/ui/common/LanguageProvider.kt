package com.arduia.expense.ui.common

import com.arduia.expense.ui.vto.LanguageVto

interface LanguageProvider {

    fun getLanguageVtoByID(id: String): LanguageVto

    fun getAvailableLanguages(): List<LanguageVto>

}