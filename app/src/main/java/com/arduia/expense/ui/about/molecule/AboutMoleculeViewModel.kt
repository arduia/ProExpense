package com.arduia.expense.ui.about.molecule

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.ui.about.AboutUpdateUiModel
import com.arduia.expense.ui.common.molecule.MoleculeViewModel
import com.arduia.expense.ui.common.molecule.Presenter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutMoleculeViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    updateUiDataMapper: Mapper<AboutUpdateDataModel, AboutUpdateUiModel>
) : MoleculeViewModel<AboutEvent, AboutState>() {

    override val presenter: Presenter<AboutEvent, AboutState> = AboutPresenter(settingsRepository, updateUiDataMapper)
}
