package com.arduia.expense

import android.app.Application
import com.arduia.expense.data.AppContainer

class ExpenseApplication : Application() {
    // Lazy: the encrypted DB only opens when a real screen first needs data, never in tests.
    val container: AppContainer by lazy { AppContainer(this) }
}
