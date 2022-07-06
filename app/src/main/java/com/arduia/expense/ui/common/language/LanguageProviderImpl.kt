package com.arduia.expense.ui.common.language

import com.arduia.expense.R
import java.lang.Exception
import javax.inject.Inject

class LanguageProviderImpl @Inject constructor(): LanguageProvider {

    override fun getLanguageVtoByID(id: String): LanguageUiModel {
         return languageList.find { it.id  == id} ?:throw Exception("Language id $id not found!")
    }

    override fun getAvailableLanguages() = languageList

    init {
        init()
    }

    private fun init(){
        languageList = getAllLanguages()
    }

    private fun getAllLanguages() = mutableListOf<LanguageUiModel>().apply {
        add(LanguageUiModel("my", R.drawable.flag_myanmar, "Myanmar(Burma)"))
        add(LanguageUiModel("en", R.drawable.flag_united_states, "United States(English)"))
        add(LanguageUiModel("cn", R.drawable.flag_china, "China (Chinese)"))
        add(LanguageUiModel("ger", R.drawable.flag_austria, "Austria (German)"))
    }

    companion object{
        private var languageList = emptyList<LanguageUiModel>()
    }
}
