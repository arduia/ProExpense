package com.arduia.expense.ui.onboarding

import android.view.View
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.model.*
import com.arduia.expense.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class ChooseCurrencyUiState(
    val currencies: List<CurrencyUiModel> = emptyList(),
    val isLoading: Boolean = false
)

sealed class ChooseCurrencyUiEffect {
    data class ShowError(val message: String) : ChooseCurrencyUiEffect()
}

@HiltViewModel
class ChooseCurrencyViewModel @Inject constructor(
    private val currencyRep: CurrencyRepository,
    private val settingRepo: SettingsRepository,
    private val currencyMapper: Mapper<CurrencyDto, CurrencyUiModel>
) : BaseViewModel<ChooseCurrencyUiState, ChooseCurrencyUiEffect>(ChooseCurrencyUiState()) {

    private val searchKey = MutableStateFlow("")

    init {
        observeCurrencyLists()
    }

    fun searchCurrency(key: String) {
        searchKey.value = key
    }

    fun selectCurrency(currency: CurrencyUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            settingRepo.setSelectedCurrencyNumber(currency.number)
        }
    }

    private fun observeCurrencyLists() {
        currencyRep.getCurrencies()
            .flowOn(Dispatchers.IO)
            .onEach { setState { copy(isLoading = it is LoadingResult) } }
            .combine(searchKey) { currencyResult, search ->
                if (currencyResult is Result.Success) {
                    if (search.isEmpty()) return@combine currencyResult
                    val filterList = currencyResult.data.filter {
                        it.toString().lowercase(Locale.ENGLISH)
                            .indexOf(search.lowercase(Locale.ENGLISH)) != -1
                    }
                    SuccessResult(filterList)
                } else currencyResult
            }
            .onEach {
                if (it is ErrorResult) {
                    sendEffect(ChooseCurrencyUiEffect.ShowError(it.exception.message ?: "Error"))
                }
            }
            .combine(settingRepo.getSelectedCurrencyNumber()) { currencyResult, selectedNumResult ->
                if (selectedNumResult !is SuccessResult) return@combine listOf<CurrencyUiModel>()
                if (currencyResult !is SuccessResult) return@combine listOf<CurrencyUiModel>()
                val selectedNumber = selectedNumResult.data
                currencyResult.data.map(currencyMapper::map).map { vo ->
                    if (vo.number == selectedNumber) CurrencyUiModel(vo.name, vo.symbol, vo.number, View.VISIBLE)
                    else vo
                }
            }
            .onEach { currencies -> setState { copy(currencies = currencies) } }
            .launchIn(viewModelScope)
    }
}