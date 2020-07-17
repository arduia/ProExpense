package com.arduia.expense.ui.onboarding

import android.app.Application
import androidx.lifecycle.*
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.SettingsRepositoryImpl
import com.arduia.expense.ui.common.BaseLiveData
import com.arduia.expense.ui.vto.LanguageVto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LanguageViewModel(app:Application): AndroidViewModel(app), LifecycleObserver{

    private val _selectedLanguage = BaseLiveData<String>()
    val selectedLanguage = _selectedLanguage.asLiveData()

    private val settingsRepository by lazy {
        SettingsRepositoryImpl(app, viewModelScope)
    }



    @ExperimentalCoroutinesApi
    @FlowPreview
    fun selectLanguage(id: String){
        settingsRepository.setSelectedLanguage(id)
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){
        viewModelScope.launch(Dispatchers.IO){
            settingsRepository.getSelectedLanguage().collect {
                _selectedLanguage.postValue(it)
            }
        }
    }
}