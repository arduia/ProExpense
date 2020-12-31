package com.arduia.expense.ui.onboarding

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.model.data
import com.arduia.expense.ui.common.LanguageProvider
import com.arduia.expense.ui.vto.LanguageVto
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ChooseLanguageViewModel @ViewModelInject constructor(
    private val langRep: LanguageProvider,
    private val settingRepo: SettingsRepository
) : ViewModel() {

    private val _languages = BaseLiveData<List<LanguageVto>>()
    val language get() = _languages.asLiveData()

    private val _isRestartEnable = BaseLiveData<Boolean>(initValue = true)
    val isRestartEnable get() = _isRestartEnable.asLiveData()

    private val _onRestartAndDismiss = EventLiveData<Unit>()
    val onRestartAndDismiss get() = _onRestartAndDismiss.asLiveData()

    private val _onDismiss = EventLiveData<Unit>()
    val onDismiss get() = _onDismiss.asLiveData()

    private val selectedId = BaseLiveData<String>()

    private val searchKey = BaseLiveData<String>("")

    private var initialSelectedId: String? = null

    init {
        observeSelectedLanguage()
        observeAvailableLanguages()
    }

    fun searchLang(key: String) {
        searchKey post key
    }

    fun selectLang(lang: LanguageVto) {
        viewModelScope.launch {
            settingRepo.setSelectedLanguage(lang.id)
        }
    }

    private fun observeAvailableLanguages() {
        selectedId.asFlow()
            .flowOn(Dispatchers.IO)
            .onEach { selectedId ->
                _isRestartEnable post (selectedId != initialSelectedId)
            }.combine(searchKey.asFlow()) { selectedId, searchKey ->
                langRep.getAvailableLanguages()
                    .filter {
                        if (searchKey.isEmpty()) return@filter true
                        it.name.toLowerCase(Locale.ROOT)
                            .contains(searchKey.toLowerCase(Locale.ROOT))
                    }
                    .map {
                        if (selectedId == it.id) return@map LanguageVto(
                            id = it.id,
                            name = it.name,
                            flag = it.flag,
                            isSelectedVisible = View.VISIBLE
                        )
                        else it
                    }
            }
            .onEach(_languages::postValue)
            .launchIn(viewModelScope)
    }

    private fun observeSelectedLanguage() {
        settingRepo.getSelectedLanguage()
            .flowOn(Dispatchers.IO)
            .onEach {
                val id = it.data ?: return@onEach
                setInitialIdIfNotExist(id)
                selectedId post id
            }
            .launchIn(viewModelScope)
    }

    fun onExit() {
        _onDismiss post EventUnit
    }

    fun onRestart() {
        val selectedLanguageID = selectedId.value
        viewModelScope.launch(Dispatchers.IO) {
            if (initialSelectedId != selectedLanguageID && selectedLanguageID != null) {
                settingRepo.setSelectedLanguage(selectedLanguageID)
            }
            _onRestartAndDismiss post EventUnit
        }
    }

    private fun setInitialIdIfNotExist(id: String) {
        if (initialSelectedId == null) {
            initialSelectedId = id
        }
    }

}