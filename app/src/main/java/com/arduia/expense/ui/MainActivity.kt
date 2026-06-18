package com.arduia.expense.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.arduia.expense.feature.history.InMemoryHistoryRepository
import com.arduia.expense.feature.logging.InMemoryLoggingRepository
import com.arduia.expense.storage.InMemoryFinanceStore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val financeStore = remember { InMemoryFinanceStore() }
            val loggingRepository = remember { InMemoryLoggingRepository(financeStore) }
            val historyRepository = remember { InMemoryHistoryRepository(financeStore) }

            ExpenseApp(
                loggingRepository = loggingRepository,
                historyRepository = historyRepository,
            )
        }
    }
}
