package com.arduia.expense.ui.about.molecule

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.data.local.UpdateStatusDataModel
import com.arduia.expense.model.Result
import com.arduia.expense.ui.about.AboutUpdateUiModel
import com.arduia.expense.utils.BaseComposeTest
import com.arduia.expense.utils.testMolecule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AboutPresenterTest : BaseComposeTest() {

    private val settingsRepository: SettingsRepository = mockk(relaxed = true)
    private val mapper: Mapper<AboutUpdateDataModel, AboutUpdateUiModel> = mockk(relaxed = true)

    private val events = MutableSharedFlow<AboutEvent>()

    @Test
    fun `initial state defaults to false for new version if NO_UPDATE`() = runTest {
        // Given
        every { settingsRepository.getUpdateStatus() } returns flowOf(
            Result.Success(UpdateStatusDataModel.STATUS_NO_UPDATE)
        )
        val presenter = AboutPresenter(settingsRepository, mapper)

        // When & Then
        testMolecule(
            presenter = { presenter.present(events) },
            validate = {
                val initialState = awaitItem()
                assertFalse(initialState.isNewVersionAvailable)
                assertNull(initialState.updateInfo)
                cancelAndIgnoreRemainingEvents()
            }
        )
    }

    @Test
    fun `when update status is NORMAL_UPDATE isNewVersionAvailable is true`() = runTest {
        // Given
        every { settingsRepository.getUpdateStatus() } returns flowOf(
            Result.Success(UpdateStatusDataModel.STATUS_NORMAL_UPDATE)
        )
        val presenter = AboutPresenter(settingsRepository, mapper)

        // When & Then
        testMolecule(
            presenter = { presenter.present(events) },
            validate = {
                skipItems(1) // Skip initial false emission
                val updatedState = awaitItem()
                assertTrue(updatedState.isNewVersionAvailable)
                cancelAndIgnoreRemainingEvents()
            }
        )
    }

    @Test
    fun `when OpenUpdateInfo event received and new version available, updateInfo populates`() = runTest {
        // Given
        val updateData = AboutUpdateDataModel("v2.0", 20L, "Bug fixes")
        val updateUi = AboutUpdateUiModel("v2.0", "20", "Bug fixes")

        every { settingsRepository.getUpdateStatus() } returns flowOf(
            Result.Success(UpdateStatusDataModel.STATUS_FORCE_UPGRADE)
        )
        coEvery { settingsRepository.getAboutUpdateSync() } returns Result.Success(updateData)
        every { mapper.map(updateData) } returns updateUi

        val presenter = AboutPresenter(settingsRepository, mapper)

        // When & Then
        testMolecule(
            presenter = { presenter.present(events) },
            validate = {
                skipItems(1) // Skip initial
                val initialState = awaitItem()
                assertTrue(initialState.isNewVersionAvailable) // Init effect makes it true

                events.emit(AboutEvent.OpenUpdateInfo)

                val infoState = awaitItem()
                assertEquals(updateUi, infoState.updateInfo)
                cancelAndIgnoreRemainingEvents()
            }
        )
    }

    @Test
    fun `when ClearUpdateInfo event received, updateInfo becomes null`() = runTest {
        // Given
        val updateData = AboutUpdateDataModel("v2.0", 20L, "Bug fixes")
        val updateUi = AboutUpdateUiModel("v2.0", "20", "Bug fixes")

        every { settingsRepository.getUpdateStatus() } returns flowOf(
            Result.Success(UpdateStatusDataModel.STATUS_NORMAL_UPDATE)
        )
        coEvery { settingsRepository.getAboutUpdateSync() } returns Result.Success(updateData)
        every { mapper.map(updateData) } returns updateUi

        val presenter = AboutPresenter(settingsRepository, mapper)

        // When & Then
        testMolecule(
            presenter = { presenter.present(events) },
            validate = {
                skipItems(2) // Skip initial and the LoadUpdateStatus update

                events.emit(AboutEvent.OpenUpdateInfo)
                val infoState = awaitItem()
                assertEquals(updateUi, infoState.updateInfo)

                events.emit(AboutEvent.ClearUpdateInfo)
                val clearedState = awaitItem()
                assertNull(clearedState.updateInfo)
                
                cancelAndIgnoreRemainingEvents()
            }
        )
    }
}
