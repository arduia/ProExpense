package com.arduia.expense

import android.app.Application
import com.arduia.expense.di.AppGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ExpenseApplication : Application() {
    lateinit var appGraph: AppGraph
        private set

    override fun onCreate() {
        super.onCreate()
        val applicationScope = CoroutineScope(SupervisorJob())
        appGraph = AppGraph(applicationScope)
    }
}
