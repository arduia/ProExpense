package com.arduia.expense.testing

import com.arduia.expense.di.AppGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.robolectric.RuntimeEnvironment

fun testAppGraph(): AppGraph {
    val context = RuntimeEnvironment.getApplication()
    return AppGraph(CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate), context)
}
