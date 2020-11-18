package com.arduia.expense.ui.onboarding


import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.SettingsRepository
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.EventUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
