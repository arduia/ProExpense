package com.arduia.expense.ui.settings

import androidx.lifecycle.*
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.data.local.UpdateStatusDataModel
import com.arduia.expense.model.Result
import com.arduia.expense.model.getDataOrError
import com.arduia.expense.model.onSuccess
import com.arduia.expense.ui.about.AboutUpdateUiModel
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val currencyRepo: CurrencyRepository,
    private val aboutUpdateUiDataMapper: Mapper<AboutUpdateDataModel, AboutUpdateUiModel>
) : ViewModel() {

    private val _selectedLanguage = BaseLiveData<String>()
    val selectedLanguage get() = _selectedLanguage.asLiveData()

    private val _currencyValue = BaseLiveData<String>()
    val currencyValue get() = _currencyValue.asLiveData()

    private val _onThemeOpenToChange = EventLiveData<Int>()
    val onThemeOpenToChange get() = _onThemeOpenToChange.asLiveData()

    private val _onThemeChanged = EventLiveData<Unit>()
    val onThemeChanged get() = _onThemeChanged.asLiveData()

    private val _isNewVersionAvailable = BaseLiveData<Boolean>(initValue = false)
    val isNewVersionAvailable get() = _isNewVersionAvailable.asLiveData()

    private val _onShowAboutUpdate = EventLiveData<AboutUpdateUiModel>()
    val onShowAboutUpdate get() = _onShowAboutUpdate.asLiveData()

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
                when (status) {
                    UpdateStatusDataModel.STATUS_NO_UPDATE -> {
                        _isNewVersionAvailable post false
                    }

                    UpdateStatusDataModel.STATUS_NORMAL_UPDATE -> {
                        _isNewVersionAvailable post true
                    }

                    UpdateStatusDataModel.STATUS_CRITICAL_UPDATE -> {
                        _isNewVersionAvailable post true
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onOpenNewUpdateInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            if(isNewVersionAvailable.value == null)return@launch
            val info = settingsRepository.getAboutUpdateSync().getDataOrError()
            val infoVo = aboutUpdateUiDataMapper.map(info)
            _onShowAboutUpdate post event(infoVo)
        }
    }

    private fun observeSelectedLanguage() {
        currencyRepo.getSelectedCacheCurrency()
            .flowOn(Dispatchers.IO)
            .onEach {
                if (it !is Result.Success) return@onEach
                _currencyValue post it.data.symbol
            }
            .launchIn(viewModelScope)
    }

    private fun observeSelectedCurrency() {
        settingsRepository.getSelectedLanguage()
            .flowOn(Dispatchers.IO)
            .onEach {
                if (it !is Result.Success) return@onEach
                _selectedLanguage post it.data
            }
            .launchIn(viewModelScope)
    }

    fun chooseTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            val mode = settingsRepository.getSelectedThemeModeSync().getDataOrError()
            _onThemeOpenToChange post event(mode)
        }
    }

    fun setThemeMode(mode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.setSelectedThemeMode(mode)
            _onThemeChanged post EventUnit
        }
    }

}
