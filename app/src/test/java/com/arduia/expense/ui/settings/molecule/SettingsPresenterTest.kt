package com.arduia.expense.ui.settings.molecule

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.data.local.UpdateStatusDataModel
import com.arduia.expense.model.Result
import com.arduia.expense.ui.about.AboutUpdateUiModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsPresenterTest {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var currencyRepo: CurrencyRepository
    private lateinit var updateMapper: Mapper<AboutUpdateDataModel, AboutUpdateUiModel>

    @Before
    fun setup() {
        settingsRepository = mockk(relaxed = true)
        currencyRepo = mockk(relaxed = true)
        updateMapper = mockk(relaxed = true)

        coEvery { settingsRepository.getSelectedLanguage() } returns flowOf(Result.Success("en"))
        coEvery { currencyRepo.getSelectedCacheCurrency() } returns flowOf(Result.Success(CurrencyDto(rank = 1, name = "US Dollar", code = "USD", symbol = "$", number = "840")))
        coEvery { settingsRepository.getUpdateStatus() } returns flowOf(Result.Success(UpdateStatusDataModel.STATUS_NO_UPDATE))
    }

    @Test
    fun `initial state is correct`() = runTest {
        val presenter = SettingsPresenter(settingsRepository, currencyRepo, updateMapper)
        val events = MutableSharedFlow<SettingsEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            val initialState = awaitItem()
            // It might bounce once due to coroutines launched effect updates
            // but eventually it lands on correct data
            val finalState = awaitItem()
            
            assertEquals("en", finalState.selectedLanguageId)
            assertEquals("$", finalState.currencyValue)
            assertEquals(false, finalState.isNewVersionAvailable)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `theme dialog changes state correctly`() = runTest {
        coEvery { settingsRepository.getSelectedThemeModeSync() } returns Result.Success(1)

        val presenter = SettingsPresenter(settingsRepository, currencyRepo, updateMapper)
        val events = MutableSharedFlow<SettingsEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            skipItems(2) // Skip initial rendering
            
            events.emit(SettingsEvent.ChooseThemeClicked)
            val stateWithDialog = awaitItem()
            assertEquals(1, stateWithDialog.showThemeDialogForMode)

            events.emit(SettingsEvent.ThemeSelected(2))
            val stateAfterSelect = awaitItem()
            assertEquals(null, stateAfterSelect.showThemeDialogForMode)
            assertEquals(true, stateAfterSelect.restartRequired)
            
            coVerify { settingsRepository.setSelectedThemeMode(2) }

            events.emit(SettingsEvent.RestartHandled)
            val stateRestartHandled = awaitItem()
            assertEquals(false, stateRestartHandled.restartRequired)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
