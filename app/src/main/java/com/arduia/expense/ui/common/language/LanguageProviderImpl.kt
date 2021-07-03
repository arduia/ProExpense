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
        add(LanguageUiModel("my", R.drawable.flag_myanmar, "Burmese"))
        add(LanguageUiModel("cn", R.drawable.flag_china, "Chinese (China)"))
        add(LanguageUiModel("de", R.drawable.flag_china, "German (Germany)"))
        add(LanguageUiModel("en", R.drawable.flag_united_states, "English (United States)"))
        add(LanguageUiModel("ja", R.drawable.flag_japan, "Japanese"))
        add(LanguageUiModel("pt-br", R.drawable.flag_brazil, "Portuguese (Brazil)"))
        add(LanguageUiModel("ru", R.drawable.flag_russia, "Russian"))
 
    }

    companion object{
        private var languageList = emptyList<LanguageUiModel>()
    }
}
