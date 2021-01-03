package com.arduia.expense.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.lang.Exception

/**
 * Base class for Fragment which use drawer navigation
 */
abstract class NavBaseFragment : Fragment() {

    private var _navDrawer: NavigationDrawer? = null
    protected val navigationDrawer get() = _navDrawer!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _navDrawer = (requireActivity() as? NavigationDrawer)
            ?: throw Exception("Fragment's host Activity must implement NavigationDrawer interface!")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _navDrawer = null
    }
}
