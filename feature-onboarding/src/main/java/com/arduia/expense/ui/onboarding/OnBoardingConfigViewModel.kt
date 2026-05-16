package com.arduia.expense.ui.onboarding

import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnBoardingConfigUiState(
    val currentPage: Int = 0
)

sealed class OnBoardingConfigUiEffect {
    object Restart : OnBoardingConfigUiEffect()
    object Continue : OnBoardingConfigUiEffect()
}

@HiltViewModel
class OnBoardingConfigViewModel @Inject constructor(private val settingRepo: SettingsRepository) :
    BaseViewModel<OnBoardingConfigUiState, OnBoardingConfigUiEffect>(OnBoardingConfigUiState()) {

    fun finishedConfig() {
        viewModelScope.launch(Dispatchers.IO) {
            settingRepo.setFirstUser(false)
            sendEffect(OnBoardingConfigUiEffect.Restart)
        }
    }

    fun continued() {
        sendEffect(OnBoardingConfigUiEffect.Continue)
    }
}