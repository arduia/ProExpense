package com.arduia.expense.ui.onboarding

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.model.data
import com.arduia.expense.ui.common.LanguageProvider
import com.arduia.expense.ui.vto.LanguageVto
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.EventUnit
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private var selectedId: String? = null

    private var searchKey: String = ""

    private var initialSelectedId: String? = null

    init {
        observeSelectedLanguage()
    }

    fun searchLang(key: String) {
        searchKey = key
        updateLanguages()
    }

    fun selectLang(lang: LanguageVto) {
        viewModelScope.launch(Dispatchers.IO) {
            settingRepo.setSelectedLanguage(lang.id)
        }
    }

    private fun updateLanguages() {
        viewModelScope.launch(Dispatchers.IO) {
            _languages post langRep.getAvailableLanguages()
                .filter {
                    if (searchKey.isEmpty()) return@filter true
                    it.name.toLowerCase(Locale.ROOT).contains(searchKey.toLowerCase(Locale.ROOT))
                }
                .map {
                    if (selectedId == null) return@map it

                    if (selectedId == it.id) return@map LanguageVto(
                        id = it.id,
                        name = it.name,
                        flag = it.flag,
                        isSelectedVisible = View.VISIBLE
                    )
                    else it
                }
        }
    }

    private fun observeSelectedLanguage() {
        settingRepo.getSelectedLanguage()
            .flowOn(Dispatchers.IO)
            .onEach {
                val id = it.data ?: return@onEach

                setInitialIdIfNotExist(id)
                setSelectedId(id)
                updateLanguages()
            }
            .launchIn(viewModelScope)
    }

    fun onRestart() {

        val isEnable = _isRestartEnable.value ?: return

        if (isEnable) {
            _onRestartAndDismiss post EventUnit
        } else {
            _onDismiss post EventUnit
        }

    }

    private fun setInitialIdIfNotExist(id: String) {
        if (initialSelectedId == null) {
            initialSelectedId = id
        }
    }

    private fun setSelectedId(id: String) {
        selectedId = id
        _isRestartEnable post (selectedId != initialSelectedId)
    }

}