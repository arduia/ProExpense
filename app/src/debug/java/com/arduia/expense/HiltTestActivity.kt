package com.arduia.expense

import androidx.appcompat.app.AppCompatActivity
import com.arduia.expense.ui.NavigationDrawer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HiltTestActivity : AppCompatActivity(), NavigationDrawer {
    override fun openDrawer() {}
    override fun closeDrawer() {}
    override fun lockDrawer() {}
    override fun unlockDrawer() {}
}
