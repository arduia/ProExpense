package com.arduia.expense.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.update.CheckAboutUpdateWorker
import com.arduia.expense.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(
    private val settingRepo: SettingsRepository,
    private val currencyRepo: CurrencyRepository,
    private val workManager: WorkManager
) : ViewModel(), LifecycleObserver {

    init {
        observeAndCacheSelectedCurrency()
    }

    private fun observeAndCacheSelectedCurrency() {
        settingRepo.getSelectedCurrencyNumber()
            .flowOn(Dispatchers.IO)
            .onEach {
                if (it is Result.Success) {
                    currencyRepo.setSelectedCacheCurrency(it.data)
                }
            }
            .launchIn(viewModelScope)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun startCheckAboutUpdateWork() {
        val checkVersionRequest = OneTimeWorkRequestBuilder<CheckAboutUpdateWorker>()
            .build()
        workManager.enqueue(checkVersionRequest)
        Timber.d("startCheckUpdate")
    }
}