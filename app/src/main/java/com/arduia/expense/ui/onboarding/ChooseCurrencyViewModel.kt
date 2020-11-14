package com.arduia.expense.ui.onboarding

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.ui.mapping.CurrencyMapper
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class ChooseCurrencyViewModel @ViewModelInject constructor(
    private val currencyRep: CurrencyRepository,
    private val settingRepo: SettingsRepository,
    private val currencyMapper: Mapper<CurrencyDto, CurrencyVo>
) :
    ViewModel() {

    private val _currencies = BaseLiveData<List<CurrencyVo>>()
    val currencies get() = _currencies.asLiveData()

    private val cacheCurrencies = mutableListOf<CurrencyDto>()

    private var selectedCurrencyNumber = ""
    private var searchKey = ""

    init {
        updateCurrencies()
    }

    fun selectCurrency(currency: CurrencyVo) {
        selectedCurrencyNumber = currency.number
        updateCurrencies()
        saveSelectedCurrency()
    }

    fun searchCurrency(key: String){
        searchKey = key
        updateCurrencies()
    }

    fun saveSelectedCurrency(){
        viewModelScope.launch(Dispatchers.IO){
            if(selectedCurrencyNumber.isEmpty()) throw Exception("Currency is Not Selected!")
            settingRepo.setSelectedCurrencyNumber(selectedCurrencyNumber)
        }
    }

    private fun updateCurrencies() {
        viewModelScope.launch(Dispatchers.IO) {
            if(cacheCurrencies.isEmpty()){
                cacheCurrencies.addAll(currencyRep.getCurrencies())
            }
            _currencies post cacheCurrencies.map(currencyMapper::map).map {
                if(it.number == selectedCurrencyNumber){
                    return@map CurrencyVo(it.name, it.symbol, it.number, View.VISIBLE)
                }else it
            }.filter {

                if(searchKey.isEmpty()) return@filter true

                it.toString().contains(searchKey)
            }
        }
    }

}