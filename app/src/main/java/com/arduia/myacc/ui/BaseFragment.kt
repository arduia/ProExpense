package com.arduia.myacc.ui

import androidx.fragment.app.Fragment
import com.arduia.myacc.NavigationDrawer

abstract class BaseFragment :Fragment(){

    private val navDrawer:NavigationDrawer? by lazy {  requireActivity() as? NavigationDrawer }

    protected fun openDrawer(){
        navDrawer?.openDrawer()
    }
}
