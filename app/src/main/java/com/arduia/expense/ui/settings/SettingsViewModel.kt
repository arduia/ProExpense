package com.arduia.expense.ui.settings

import android.app.Application
import androidx.lifecycle.*
import com.arduia.expense.data.SettingsRepositoryImpl
import com.arduia.expense.ui.common.BaseLiveData
import com.arduia.expense.ui.vto.LanguageVto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application): AndroidViewModel(app), LifecycleObserver{

    private val _selectedLanguage = BaseLiveData<String>()
    val selectedLanguage get() = _selectedLanguage.asLiveData()

    private val settingsRepo by lazy {
        SettingsRepositoryImpl(app, viewModelScope)
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){
        viewModelScope.launch (Dispatchers.IO){
            settingsRepo.getSelectedLanguage().collect {
                _selectedLanguage.postValue(it)
            }
        }
    }
}
