package com.arduia.expense.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.mvvm.BaseLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsViewModel @ViewModelInject constructor(
    private val expenseRepository: ExpenseRepository,
    private val settingsRepository: SettingsRepository): ViewModel(), LifecycleObserver{

    private val _selectedLanguage = BaseLiveData<String>()
    val selectedLanguage get() = _selectedLanguage.asLiveData()


    @ExperimentalCoroutinesApi
    @FlowPreview
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){
        viewModelScope.launch (Dispatchers.IO){
            settingsRepository.getSelectedLanguage().collect {
                _selectedLanguage.postValue(it)
            }
        }

        viewModelScope.launch(Dispatchers.IO){
            expenseRepository.getVersionStatus().collect {
                Timber.d("VersionStatus ->  $it")
            }
        }
    }
}
