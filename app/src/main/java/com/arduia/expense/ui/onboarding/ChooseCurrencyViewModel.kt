package com.arduia.expense.ui.onboarding

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.model.*
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.event
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class ChooseCurrencyViewModel @ViewModelInject constructor(
    private val currencyRep: CurrencyRepository,
    private val settingRepo: SettingsRepository,
    private val currencyMapper: Mapper<CurrencyDto, CurrencyVo>
) : ViewModel() {

    private val _currencies = BaseLiveData<List<CurrencyVo>>()
    val currencies get() = _currencies.asLiveData()

    private val _onError = EventLiveData<String>()
    val onError get() = _onError.asLiveData()

    private val _isLoading = BaseLiveData<Boolean>()
    val isLoading get() = _isLoading.asLiveData()

    private val searchKey = BaseLiveData<String>("")

    init {
        observeCurrencyLists()
        searchCurrency("")
    }


    fun searchCurrency(key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchKey post key
        }
    }

    fun selectCurrency(currency: CurrencyVo) {
        viewModelScope.launch(Dispatchers.IO) {
            settingRepo.setSelectedCurrencyNumber(currency.number)
        }
    }


    private fun observeCurrencyLists() {
        currencyRep.getCurrencies()
            .flowOn(Dispatchers.IO)
            .onEach {
                _isLoading post (it is LoadingResult)
            }
            .combine(searchKey.asFlow()) { currencyResult, search ->

                if (currencyResult is Result.Success) {
                    if (search.isEmpty()) {
                        return@combine currencyResult
                    }

                    val filterList = currencyResult.data.filter {
                        val tmp =
                            it.toString().toLowerCase(Locale.ENGLISH)
                                .indexOf(search.toLowerCase(Locale.ENGLISH))

                        return@filter (tmp != -1)
                    }

                    SuccessResult(filterList)
                } else currencyResult
            }
            .onEach {
                if (it is ErrorResult) {
                    _onError post event(it.exception.message ?: "Error")
                }
            }
            .combine(settingRepo.getSelectedCurrencyNumber()) { currencyResult, selectedNumResult ->
                if (selectedNumResult !is SuccessResult) return@combine listOf<CurrencyVo>()
                if (currencyResult !is SuccessResult) return@combine listOf<CurrencyVo>()

                val selectedNumber = selectedNumResult.data
                currencyResult.data.map(currencyMapper::map)
                    .map { vo ->
                        if (vo.number == selectedNumber) {
                            return@map CurrencyVo(
                                vo.name,
                                vo.symbol,
                                vo.number,
                                View.VISIBLE
                            )
                        } else vo
                    }
            }
            .onEach(_currencies::postValue)
            .launchIn(viewModelScope)
    }

}