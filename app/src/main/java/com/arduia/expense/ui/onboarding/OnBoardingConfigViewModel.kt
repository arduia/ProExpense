package com.arduia.expense.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arduia.expense.data.SettingsRepository
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.EventUnit
import com.arduia.mvvm.post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingConfigViewModel @Inject constructor(private val settingRepo: SettingsRepository) :
    ViewModel(){

    private val _onRestart = EventLiveData<Unit>()
    val onRestart get() = _onRestart.asLiveData()

    private val _onContinued = EventLiveData<Unit>()
    val onContinued get() = _onContinued.asLiveData()

    fun finishedConfig(){
        viewModelScope.launch(Dispatchers.IO){
            settingRepo.setFirstUser(false)
            _onRestart post EventUnit
        }
    }

    fun continued(){
        _onContinued post EventUnit
    }
}