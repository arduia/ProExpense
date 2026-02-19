package com.arduia.expense.ui.about.molecule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.data.local.UpdateStatusDataModel
import com.arduia.expense.model.getDataOrError
import com.arduia.expense.model.onSuccess
import com.arduia.expense.ui.about.AboutUpdateUiModel
import com.arduia.expense.ui.common.molecule.Presenter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

// Events
sealed interface AboutEvent {
    data object LoadUpdateStatus : AboutEvent
    data object OpenUpdateInfo : AboutEvent
    data object ClearUpdateInfo : AboutEvent
}

// State
data class AboutState(
    val isNewVersionAvailable: Boolean = false,
    val updateInfo: AboutUpdateUiModel? = null
)

// Presenter
class AboutPresenter(
    private val settingsRepository: SettingsRepository,
    private val mapper: Mapper<AboutUpdateDataModel, AboutUpdateUiModel>
) : Presenter<AboutEvent, AboutState> {

    @Composable
    override fun present(events: Flow<AboutEvent>): AboutState {
        var isNewVersionAvailable by remember { mutableStateOf(false) }
        var updateInfo by remember { mutableStateOf<AboutUpdateUiModel?>(null) }

        LaunchedEffect(Unit) {
            settingsRepository.getUpdateStatus()
                .onSuccess { status ->
                    isNewVersionAvailable = when (status) {
                        UpdateStatusDataModel.STATUS_NORMAL_UPDATE,
                        UpdateStatusDataModel.STATUS_FORCE_UPGRADE -> true
                        else -> false
                    }
                }
                .collect { }
        }

        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    AboutEvent.LoadUpdateStatus -> {
                        // Already handled in init, but could be refreshed
                    }
                    AboutEvent.OpenUpdateInfo -> {
                        if (isNewVersionAvailable) {
                            try {
                                val info = settingsRepository.getAboutUpdateSync().getDataOrError()
                                updateInfo = mapper.map(info)
                            } catch (e: Exception) {
                                // Handle error or ignore
                            }
                        }
                    }
                    AboutEvent.ClearUpdateInfo -> {
                        updateInfo = null
                    }
                }
            }
        }

        return AboutState(
            isNewVersionAvailable = isNewVersionAvailable,
            updateInfo = updateInfo
        )
    }
}
