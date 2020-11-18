package com.arduia.expense.ui.onboarding

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class ChooseCurrencyViewModel @ViewModelInject constructor(
    private val currencyRep: CurrencyRepository,
    private val settingRepo: SettingsRepository,
    private val currencyMapper: Mapper<CurrencyDto, CurrencyVo>
) :
    ViewModel() {

    private val _currencies = BaseLiveData<List<CurrencyVo>>()
    val currencies get() = _currencies.asLiveData()

    init {
        observeSelectedCurrency()
    }

    private val vmCacheCurrencies = mutableListOf<CurrencyVo>()
    private var searchKey = ""

    fun searchCurrency(key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchKey = key
            _currencies post vmCacheCurrencies
                .filter {
                    it.toString().toUpperCase(Locale.ROOT)
                        .contains(searchKey.toUpperCase(Locale.ROOT))
                }
        }
    }

    fun selectCurrency(currency: CurrencyVo) {
        viewModelScope.launch(Dispatchers.IO) {
            settingRepo.setSelectedCurrencyNumber(currency.number)
            currencyRep.setSelectedCacheCurrency(currency.number)
        }
    }

    private fun updateCurrencies(selectedNum: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val currencies = currencyRep.getCurrencies()
                .map(currencyMapper::map)
                .map {
                    if (it.number == selectedNum) {
                        return@map CurrencyVo(it.name, it.symbol, it.number, View.VISIBLE)
                    } else it
                }

            if (vmCacheCurrencies.isEmpty()) {
                vmCacheCurrencies.addAll(currencies)
            }

            _currencies post currencies.filter {
                if(searchKey.isEmpty()) return@filter true
                it.toString().toUpperCase(Locale.ROOT).contains(searchKey.toUpperCase(Locale.ROOT))
            }
        }
    }

    private fun observeSelectedCurrency() {
        settingRepo.getSelectedCurrencyNumber()
            .flowOn(Dispatchers.IO)
            .onEach {
                updateCurrencies(selectedNum = it)
                Timber.d("observe -> $it selected")
            }
            .launchIn(viewModelScope)
    }

}