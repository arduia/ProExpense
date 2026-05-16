package com.arduia.expense.ui.settings

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.data.local.UpdateStatusDataModel
import com.arduia.expense.model.Result
import com.arduia.expense.model.getDataOrError
import com.arduia.expense.model.onSuccess
import com.arduia.expense.ui.about.AboutUpdateUiModel
import com.arduia.expense.ui.base.BaseViewModel
import com.arduia.expense.ui.component.dialog.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class SettingsUiState(
    val currentCurrency: String = "",
    val currentLanguage: String = "",
    val currentTheme: AppTheme = AppTheme.System,
    val appVersion: String = "",
    val isNewVersionAvailable: Boolean = false
)

sealed class SettingsUiEffect {
    data class OpenThemeChooser(val mode: Int) : SettingsUiEffect()
    object ThemeChanged : SettingsUiEffect()
    data class ShowAboutUpdate(val info: AboutUpdateUiModel) : SettingsUiEffect()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val currencyRepo: CurrencyRepository,
    private val aboutUpdateUiDataMapper: Mapper<AboutUpdateDataModel, AboutUpdateUiModel>
) : BaseViewModel<SettingsUiState, SettingsUiEffect>(SettingsUiState()) {

    init {
        observeSelectedLanguage()
        observeSelectedCurrency()
        observeVersionStatus()
    }

    private fun observeVersionStatus() {
        settingsRepository.getUpdateStatus()
            .flowOn(Dispatchers.IO)
            .onSuccess { status ->
                Timber.d("version status $status")
                val isAvailable = when (status) {
                    UpdateStatusDataModel.STATUS_NO_UPDATE -> false
                    UpdateStatusDataModel.STATUS_NORMAL_UPDATE -> true
                    UpdateStatusDataModel.STATUS_FORCE_UPGRADE -> true
                    else -> false
                }
                setState { copy(isNewVersionAvailable = isAvailable) }
            }
            .launchIn(viewModelScope)
    }

    fun onOpenNewUpdateInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.value.isNewVersionAvailable.not()) return@launch
            val info = settingsRepository.getAboutUpdateSync().getDataOrError()
            val infoVo = aboutUpdateUiDataMapper.map(info)
            sendEffect(SettingsUiEffect.ShowAboutUpdate(infoVo))
        }
    }

    private fun observeSelectedLanguage() {
        currencyRepo.getSelectedCacheCurrency()
            .flowOn(Dispatchers.IO)
            .onEach {
                if (it !is Result.Success) return@onEach
                setState { copy(currentCurrency = it.data.symbol) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeSelectedCurrency() {
        settingsRepository.getSelectedLanguage()
            .flowOn(Dispatchers.IO)
            .onEach {
                if (it !is Result.Success) return@onEach
                setState { copy(currentLanguage = it.data) }
            }
            .launchIn(viewModelScope)
    }

    fun chooseTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            val mode = settingsRepository.getSelectedThemeModeSync().getDataOrError()
            sendEffect(SettingsUiEffect.OpenThemeChooser(mode))
        }
    }

    fun setThemeMode(mode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.setSelectedThemeMode(mode)
            sendEffect(SettingsUiEffect.ThemeChanged)
        }
    }

    fun onThemeClick() {}
}
