package com.arduia.expense.ui.splash

import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashUiState(
    val isLoading: Boolean = true
)

sealed class SplashUiEffect {
    object NavigateToHome : SplashUiEffect()
    object NavigateToOnboarding : SplashUiEffect()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : BaseViewModel<SplashUiState, SplashUiEffect>(SplashUiState()) {

    private val splashDuration = 1000L

    init {
        checkUserAndGo()
    }

    private fun checkUserAndGo() {
        viewModelScope.launch(Dispatchers.IO) {
            val isFirstTimeUser = settingsRepository.getFirstUser().awaitValueOrError()
            delay(splashDuration)
            if (isFirstTimeUser) {
                sendEffect(SplashUiEffect.NavigateToOnboarding)
            } else {
                sendEffect(SplashUiEffect.NavigateToHome)
            }
        }
    }
}
