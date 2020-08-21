package com.arduia.expense.ui

import androidx.fragment.app.Fragment

abstract class NavBaseFragment : Fragment(){

    protected val navigationDrawer: NavigationDrawer? by lazy {  requireActivity() as? NavigationDrawer }
}
