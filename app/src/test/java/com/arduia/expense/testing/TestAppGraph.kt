package com.arduia.expense.testing

import com.arduia.expense.di.AppGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

fun testAppGraph(): AppGraph = AppGraph(CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate))
