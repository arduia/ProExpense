package com.arduia.expense.ui.splash


import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.EventUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @ViewModelInject
    constructor(private val settingsRepository: SettingsRepository, private val currencyRep: CurrencyRepository): ViewModel(){

    private val _firstTimeEvent = EventLiveData<Unit>()
    val firstTimeEvent = _firstTimeEvent.asLiveData()

    private val _normalUserEvent = EventLiveData<Unit>()
    val normalUserEvent = _normalUserEvent.asLiveData()

    private val splashDuration = 1000L

    init {
        checkUserAndGo()
    }
    private fun checkUserAndGo(){
        viewModelScope.launch(Dispatchers.IO) {
            delay(splashDuration)
            updateCache()
            when(settingsRepository.getFirstUser().first()){
                true -> {
                    settingsRepository.setSelectedLanguage("en")
                    _firstTimeEvent.postValue(EventUnit)
                }
                false -> _normalUserEvent.postValue(EventUnit)
            }
        }
    }

    private suspend fun updateCache(){
        val selectedCurrency = settingsRepository.getSelectedCurrencyNumber().first()
        currencyRep.setSelectedCacheCurrency(selectedCurrency)
    }

}
