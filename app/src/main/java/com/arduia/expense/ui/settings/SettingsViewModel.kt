package com.arduia.expense.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.model.Result
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SettingsViewModel @ViewModelInject constructor(
    private val settingsRepository: SettingsRepository,
    private val currencyRepo: CurrencyRepository
) : ViewModel() {

    private val _selectedLanguage = BaseLiveData<String>()
    val selectedLanguage get() = _selectedLanguage.asLiveData()

    private val _currencyValue = BaseLiveData<String>()
    val currencyValue get() = _currencyValue.asLiveData()

    init {
        observeSelectedLanguage()
        observeSelectedCurrency()
    }

    private fun observeSelectedLanguage() {
        currencyRepo.getSelectedCacheCurrency()
            .flowOn(Dispatchers.IO)
            .onEach {
                if(it !is Result.Success) return@onEach
                _currencyValue post it.data.symbol
            }
            .launchIn(viewModelScope)
    }

    private fun observeSelectedCurrency(){
        settingsRepository.getSelectedLanguage()
            .flowOn(Dispatchers.IO)
            .onEach {
                if(it !is  Result.Success) return@onEach
                _selectedLanguage post it.data
            }
            .launchIn(viewModelScope)
    }
}
