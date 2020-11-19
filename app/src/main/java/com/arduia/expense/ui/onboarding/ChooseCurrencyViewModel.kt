package com.arduia.expense.ui.onboarding

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.model.Result
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
        currencyRep.getCurrencies()
            .flowOn(Dispatchers.IO)
            .onEach {
                when (it) {
                    is Result.Loading -> Unit
                    is Result.Error -> Unit
                    is Result.Success -> {
                        it.data.map(currencyMapper::map)
                            .map { vo ->
                                if (vo.number == selectedNum) {
                                    return@map CurrencyVo(
                                        vo.name,
                                        vo.symbol,
                                        vo.number,
                                        View.VISIBLE
                                    )
                                } else vo
                            }.filter { vo ->
                                if (searchKey.isEmpty()) return@filter true
                                vo.toString().toUpperCase(Locale.ROOT)
                                    .contains(searchKey.toUpperCase(Locale.ROOT))
                            }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeSelectedCurrency() {
        settingRepo.getSelectedCurrencyNumber()
            .flowOn(Dispatchers.IO)
            .onEach {
                when(it){
                    is Result.Loading -> Unit
                    is Result.Error -> Unit
                    is Result.Success -> {
                        updateCurrencies(selectedNum = it.data)
                        Timber.d("observe -> ${it.data} selected")
                    }
                }

            }
            .launchIn(viewModelScope)
    }

}