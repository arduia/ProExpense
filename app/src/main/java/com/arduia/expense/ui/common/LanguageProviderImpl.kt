package com.arduia.expense.ui.common

import com.arduia.expense.R
import com.arduia.expense.ui.vto.LanguageVto
import java.lang.Exception

class LanguageProviderImpl : LanguageProvider{

    override fun getLanguageVtoByID(id: String): LanguageVto {
         return languageList.find { it.id  == id} ?:throw Exception("Language id $id not found!")
    }

    override fun getAvailableLanguages() = languageList

    init {
        init()
    }

    fun init(){
        languageList = getAllLanguages()
    }

    private fun getAllLanguages() = mutableListOf<LanguageVto>().apply {
        add(LanguageVto("my", R.drawable.tmp_myanmar, "Myanmar(Burma)"))
        add(LanguageVto("en", R.drawable.tmp_united_states, "United States(English)"))
    }

    companion object{
        private var languageList = emptyList<LanguageVto>()
    }
}
