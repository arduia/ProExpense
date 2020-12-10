package com.arduia.expense.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.model.Result
import com.arduia.expense.model.getDataOrError
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    private val settingsRepository: SettingsRepository,
    private val currencyRepo: CurrencyRepository
) : ViewModel() {

    private val _selectedLanguage = BaseLiveData<String>()
    val selectedLanguage get() = _selectedLanguage.asLiveData()

    private val _currencyValue = BaseLiveData<String>()
    val currencyValue get() = _currencyValue.asLiveData()

    private val _onThemeOpenToChange = EventLiveData<Int>()
    val onThemeOpenToChange get() = _onThemeOpenToChange.asLiveData()

    private val _onThemeChanged = EventLiveData<Unit>()
    val onThemeChanged get() = _onThemeChanged.asLiveData()

    init {
        observeSelectedLanguage()
        observeSelectedCurrency()
    }

    private fun observeSelectedLanguage() {
        currencyRepo.getSelectedCacheCurrency()
            .flowOn(Dispatchers.IO)
            .onEach {
                if (it !is Result.Success) return@onEach
                _currencyValue post it.data.symbol
            }
            .launchIn(viewModelScope)
    }

    private fun observeSelectedCurrency() {
        settingsRepository.getSelectedLanguage()
            .flowOn(Dispatchers.IO)
            .onEach {
                if (it !is Result.Success) return@onEach
                _selectedLanguage post it.data
            }
            .launchIn(viewModelScope)
    }

    fun chooseTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            val mode = settingsRepository.getSelectedThemeModeSync().getDataOrError()
            _onThemeOpenToChange post event(mode)
        }
    }

    fun setThemeMode(mode: Int){
        viewModelScope.launch(Dispatchers.IO){
            settingsRepository.setSelectedThemeMode(mode)
            _onThemeChanged post EventUnit
        }
    }

}
