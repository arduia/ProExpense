package com.arduia.expense.ui.settings.molecule

import androidx.compose.runtime.*
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.data.local.UpdateStatusDataModel
import com.arduia.expense.model.Result
import com.arduia.expense.model.getDataOrError
import com.arduia.expense.ui.about.AboutUpdateUiModel
import com.arduia.expense.ui.common.molecule.Presenter
import kotlinx.coroutines.flow.collectLatest

class SettingsPresenter(
    private val settingsRepository: SettingsRepository,
    private val currencyRepo: CurrencyRepository,
    private val aboutUpdateUiDataMapper: Mapper<AboutUpdateDataModel, AboutUpdateUiModel>
) : Presenter<SettingsEvent, SettingsState> {

    @Composable
    override fun present(events: kotlinx.coroutines.flow.Flow<SettingsEvent>): SettingsState {
        var selectedLanguageId by remember { mutableStateOf("") }
        var currencyValue by remember { mutableStateOf("") }
        var isNewVersionAvailable by remember { mutableStateOf(false) }
        var showThemeDialogForMode by remember { mutableStateOf<Int?>(null) }
        var restartRequired by remember { mutableStateOf(false) }
        var showAboutUpdate by remember { mutableStateOf<AboutUpdateUiModel?>(null) }

        LaunchedEffect(Unit) {
            settingsRepository.getSelectedLanguage().collectLatest { result ->
                if (result is Result.Success) {
                    selectedLanguageId = result.data
                }
            }
        }

        LaunchedEffect(Unit) {
            currencyRepo.getSelectedCacheCurrency().collectLatest { result ->
                if (result is Result.Success) {
                    currencyValue = result.data.symbol
                }
            }
        }

        LaunchedEffect(Unit) {
            settingsRepository.getUpdateStatus().collectLatest { result ->
                if (result is Result.Success) {
                    isNewVersionAvailable = when (result.data) {
                        UpdateStatusDataModel.STATUS_NORMAL_UPDATE,
                        UpdateStatusDataModel.STATUS_FORCE_UPGRADE -> true
                        else -> false
                    }
                }
            }
        }

        LaunchedEffect(events) {
            events.collect { event ->
                when (event) {
                    is SettingsEvent.ChooseThemeClicked -> {
                        val mode = settingsRepository.getSelectedThemeModeSync().getDataOrError()
                        showThemeDialogForMode = mode
                    }
                    is SettingsEvent.ThemeSelected -> {
                        settingsRepository.setSelectedThemeMode(event.mode)
                        showThemeDialogForMode = null
                        restartRequired = true
                    }
                    is SettingsEvent.ThemeDialogDismissed -> {
                        showThemeDialogForMode = null
                    }
                    is SettingsEvent.OpenNewUpdateInfoClicked -> {
                        if (isNewVersionAvailable) {
                            val info = settingsRepository.getAboutUpdateSync().getDataOrError()
                            showAboutUpdate = aboutUpdateUiDataMapper.map(info)
                        }
                    }
                    is SettingsEvent.AboutUpdateDismissed -> {
                        showAboutUpdate = null
                    }
                    is SettingsEvent.RestartHandled -> {
                        restartRequired = false
                    }
                }
            }
        }

        return SettingsState(
            selectedLanguageId = selectedLanguageId,
            currencyValue = currencyValue,
            isNewVersionAvailable = isNewVersionAvailable,
            showThemeDialogForMode = showThemeDialogForMode,
            restartRequired = restartRequired,
            showAboutUpdate = showAboutUpdate
        )
    }
}
