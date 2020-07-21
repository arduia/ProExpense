package com.arduia.expense.ui.language

import android.app.Application
import android.icu.util.LocaleData
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.SettingsRepositoryImpl
import com.arduia.expense.ui.common.BaseLiveData
import com.arduia.expense.ui.common.EventLiveData
import com.arduia.expense.ui.common.EventUnit
import com.arduia.expense.ui.vto.LanguageVto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class LanguageViewModel @ViewModelInject
    constructor(private val settingsRepository: SettingsRepository): ViewModel(), LifecycleObserver{

    private val _selectedLanguage = BaseLiveData<String>()
    val selectedLanguage get() =  _selectedLanguage.asLiveData()

    private val _detectedLanguage = BaseLiveData<String>()
    val detectedLanguage get() = _detectedLanguage.asLiveData()

    private val _continueEvent = EventLiveData<Unit>()
    val continueEvent get() =  _continueEvent.asLiveData()


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

    fun continueHome(){
            settingsRepository.setFirstUser(false)
            _continueEvent.postValue(EventUnit)
    }

}
