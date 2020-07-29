package com.arduia.expense

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import androidx.hilt.work.HiltWorkerFactory
import com.arduia.core.lang.updateResource
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.SettingsRepositoryImpl
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltAndroidApp
class ExpenseApplication: Application(), androidx.work.Configuration.Provider{

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        when(BuildConfig.DEBUG){
            true -> Timber.plant(Timber.DebugTree())
            false ->  Unit
        }
    }

    override fun getWorkManagerConfiguration(): androidx.work.Configuration  =
        androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun attachBaseContext(base: Context?) {
        runBlocking {
            base?.let {
                val settings = SettingsRepositoryImpl(base, GlobalScope)
                val selectedLanguage = settings.getSelectedLanguage().first()
                val localedContext = base.updateResource(selectedLanguage)
                super.attachBaseContext(localedContext)
            }
        }
    }

}
