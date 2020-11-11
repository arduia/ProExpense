package com.arduia.expense

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import com.arduia.core.lang.updateResource
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.SettingsRepositoryImpl
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ExpenseApplication : Application(), androidx.work.Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


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
        if (base == null) return
        val updatedLocale = updateToLanguageContext(baseContext = base)
        super.attachBaseContext(updatedLocale)
    }

    private fun updateToLanguageContext(baseContext: Context): Context =
        runBlocking {
            val selectedLanguage = SettingsRepositoryImpl(baseContext, this).getSelectedLanguage().first()
            return@runBlocking baseContext.updateResource(selectedLanguage)
        }

}
