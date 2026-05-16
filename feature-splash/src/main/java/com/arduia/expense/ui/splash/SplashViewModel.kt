package com.arduia.expense.ui.splash


import androidx.lifecycle.*
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.model.Result
import com.arduia.expense.model.awaitValueOrError
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.EventUnit
import com.arduia.mvvm.post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SplashDestination { Home, Onboarding }

@HiltViewModel
class SplashViewModel @Inject
constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _firstTimeEvent = EventLiveData<Unit>()
    val firstTimeEvent = _firstTimeEvent.asLiveData()

    private val _normalUserEvent = EventLiveData<Unit>()
    val normalUserEvent = _normalUserEvent.asLiveData()

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    private val splashDuration = 1000L

    init {
        checkUserAndGo()
    }

    private fun checkUserAndGo() {
        viewModelScope.launch(Dispatchers.IO) {
            val isFirstTimeUser = settingsRepository.getFirstUser().awaitValueOrError()
            delay(splashDuration)
            if (isFirstTimeUser) {
                _destination.value = SplashDestination.Onboarding
                _firstTimeEvent post EventUnit
            } else {
                _destination.value = SplashDestination.Home
                _normalUserEvent post EventUnit
            }
        }
    }

}
