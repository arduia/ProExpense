package com.arduia.myacc.ui

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(){

    private val navDrawer: NavigationDrawer? by lazy {  requireActivity() as? NavigationDrawer }

    protected fun openDrawer(){
        navDrawer?.openDrawer()
    }

}
