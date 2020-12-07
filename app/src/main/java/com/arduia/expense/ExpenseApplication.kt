package com.arduia.expense

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import com.arduia.core.lang.updateResource
import com.arduia.expense.data.SettingsRepositoryImpl
import com.arduia.expense.data.local.PreferenceStorageDao
import com.arduia.expense.data.local.PreferenceStorageDaoImpl
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.data
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import leakcanary.LeakCanary
import timber.log.Timber
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltAndroidApp
class ExpenseApplication : Application(), androidx.work.Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val appJob = Job()

    override fun onCreate() {
        super.onCreate()
        setupLogging()
    }

    private fun setupLogging() {
        when (BuildConfig.DEBUG) {
            true -> Timber.plant(Timber.DebugTree())
            false -> Unit
        }
    }

    override fun getWorkManagerConfiguration(): androidx.work.Configuration =
        androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun attachBaseContext(base: Context?) {
        runBlocking {
            if (base == null) return@runBlocking
            val prefDao: PreferenceStorageDao = PreferenceStorageDaoImpl(base, this)
            val selectedLanguage: String = SettingsRepositoryImpl(prefDao).getSelectedLanguage().awaitValueOrError()
            val localedContext = base.updateResource(selectedLanguage)
            super.attachBaseContext(localedContext)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appJob.cancel()
    }
}
