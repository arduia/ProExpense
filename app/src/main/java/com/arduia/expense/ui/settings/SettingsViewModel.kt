package com.arduia.expense.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsViewModel @ViewModelInject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _selectedLanguage = BaseLiveData<String>()
    val selectedLanguage get() = _selectedLanguage.asLiveData()

    init {
        observeSelectedLang()
    }

    private fun observeSelectedLang() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.getSelectedLanguage().collect {
                _selectedLanguage post it
            }
        }
    }
}
