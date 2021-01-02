package com.arduia.expense.ui.splash


import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.model.Result
import com.arduia.expense.model.awaitValueOrError
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.EventUnit
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @ViewModelInject
constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _firstTimeEvent = EventLiveData<Unit>()
    val firstTimeEvent = _firstTimeEvent.asLiveData()

    private val _normalUserEvent = EventLiveData<Unit>()
    val normalUserEvent = _normalUserEvent.asLiveData()

    private val splashDuration = 1000L

    init {
        checkUserAndGo()
    }

    private fun checkUserAndGo() {
        viewModelScope.launch(Dispatchers.IO) {
            val isFirstTimeUser = settingsRepository.getFirstUser().awaitValueOrError()
            delay(splashDuration)
            if (isFirstTimeUser) {
                _firstTimeEvent post EventUnit
            } else {
                _normalUserEvent post EventUnit
            }
        }
    }

}
