package com.arduia.expense.ui.onboarding

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arduia.expense.ui.common.LanguageProvider
import com.arduia.expense.ui.vto.LanguageVto
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChooseLanguageViewModel @ViewModelInject constructor (
    private val langRep: LanguageProvider
): ViewModel(){

    private val _languages = BaseLiveData<List<LanguageVto>>()
    val language get() = _languages.asLiveData()

    private var selectedId: String? =null

    private var searchKey: String = ""

    init {
        updateLanguages()
    }

    fun searchLang(key: String){
        searchKey = key
        updateLanguages()
    }

    fun selectLang(id: String){
        selectedId = id
        updateLanguages()
    }

    private fun updateLanguages(){
        viewModelScope.launch(Dispatchers.IO){
            _languages post langRep.getAvailableLanguages()
                .filter {
                    if(searchKey.isEmpty()) return@filter true
                    it.name.contains(searchKey)
                }
                .map {
                    if(selectedId == null) return@map it

                    if(selectedId == it.id)  return@map LanguageVto(
                             id = it.id,
                             name = it.name,
                             flag = it.flag,
                             isSelectedVisible = View.VISIBLE
                         )
                    else it
                }
        }
    }

}