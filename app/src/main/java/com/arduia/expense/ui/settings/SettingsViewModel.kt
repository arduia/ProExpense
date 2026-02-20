package com.arduia.expense.ui.settings

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.ui.about.AboutUpdateUiModel
import com.arduia.expense.ui.common.molecule.MoleculeViewModel
import com.arduia.expense.ui.common.molecule.Presenter
import com.arduia.expense.ui.settings.molecule.SettingsEvent
import com.arduia.expense.ui.settings.molecule.SettingsPresenter
import com.arduia.expense.ui.settings.molecule.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    currencyRepo: CurrencyRepository,
    aboutUpdateUiDataMapper: Mapper<AboutUpdateDataModel, AboutUpdateUiModel>
) : MoleculeViewModel<SettingsEvent, SettingsState>() {

    override val presenter: Presenter<SettingsEvent, SettingsState> =
        SettingsPresenter(settingsRepository, currencyRepo, aboutUpdateUiDataMapper)
}
