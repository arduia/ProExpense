package com.arduia.expense.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val settingRepo: SettingsRepository,
    private val currencyRepo: CurrencyRepository
) : ViewModel() {

    init {
        observeAndCacheSelectedCurrency()
    }

    private fun observeAndCacheSelectedCurrency() {
        settingRepo.getSelectedCurrencyNumber()
            .flowOn(Dispatchers.IO)
            .onEach {
                if (it !is Result.Success) return@onEach
                saveCurrencyData(it.data)
            }
            .launchIn(viewModelScope)
    }

    private fun saveCurrencyData(data: String) {
        viewModelScope.launch(Dispatchers.IO) {
            currencyRepo.setSelectedCacheCurrency(data)
        }
    }
}