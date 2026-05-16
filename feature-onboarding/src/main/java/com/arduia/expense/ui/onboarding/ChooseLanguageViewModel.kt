package com.arduia.expense.ui.onboarding

import android.view.View
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.model.data
import com.arduia.expense.ui.base.BaseViewModel
import com.arduia.expense.ui.common.language.LanguageProvider
import com.arduia.expense.ui.common.language.LanguageUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class ChooseLanguageUiState(
    val languages: List<LanguageUiModel> = emptyList(),
    val isRestartEnabled: Boolean = true
)

sealed class ChooseLanguageUiEffect {
    object Dismiss : ChooseLanguageUiEffect()
    object RestartAndDismiss : ChooseLanguageUiEffect()
}

@HiltViewModel
class ChooseLanguageViewModel @Inject constructor(
    private val langRep: LanguageProvider,
    private val settingRepo: SettingsRepository
) : BaseViewModel<ChooseLanguageUiState, ChooseLanguageUiEffect>(ChooseLanguageUiState()) {

    private val selectedId = MutableStateFlow<String?>(null)
    private val searchKey = MutableStateFlow("")
    private var initialSelectedId: String? = null

    init {
        observeSelectedLanguage()
        observeAvailableLanguages()
    }

    fun searchLang(key: String) {
        searchKey.value = key
    }

    fun selectLang(lang: LanguageUiModel) {
        viewModelScope.launch {
            settingRepo.setSelectedLanguage(lang.id)
        }
    }

    private fun observeAvailableLanguages() {
        selectedId
            .filterNotNull()
            .flowOn(Dispatchers.IO)
            .onEach { id ->
                setState { copy(isRestartEnabled = id != initialSelectedId) }
            }.combine(searchKey) { id, search ->
                langRep.getAvailableLanguages()
                    .filter {
                        if (search.isEmpty()) return@filter true
                        it.name.lowercase(Locale.ROOT).contains(search.lowercase(Locale.ROOT))
                    }
                    .map {
                        if (id == it.id) LanguageUiModel(id = it.id, name = it.name, flag = it.flag, isSelectedVisible = View.VISIBLE)
                        else it
                    }
            }
            .onEach { languages -> setState { copy(languages = languages) } }
            .launchIn(viewModelScope)
    }

    private fun observeSelectedLanguage() {
        settingRepo.getSelectedLanguage()
            .flowOn(Dispatchers.IO)
            .onEach {
                val id = it.data ?: return@onEach
                setInitialIdIfNotExist(id)
                selectedId.value = id
            }
            .launchIn(viewModelScope)
    }

    fun onExit() {
        sendEffect(ChooseLanguageUiEffect.Dismiss)
    }

    fun onRestart() {
        val currentSelectedId = selectedId.value
        viewModelScope.launch(Dispatchers.IO) {
            if (initialSelectedId != currentSelectedId && currentSelectedId != null) {
                settingRepo.setSelectedLanguage(currentSelectedId)
            }
            sendEffect(ChooseLanguageUiEffect.RestartAndDismiss)
        }
    }

    private fun setInitialIdIfNotExist(id: String) {
        if (initialSelectedId == null) {
            initialSelectedId = id
        }
    }
}