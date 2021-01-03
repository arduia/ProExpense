package com.arduia.expense.ui

import androidx.fragment.app.Fragment

/**
 * Communication interface between host activity and
 * it's child view such as Fragments.
 *
 * Received Add button onClickListener, Toast message
 * to not overlap Floating Action Button and Toast.
 */

interface MainHost{

    val defaultSnackBarDuration: Int

    fun showAddButton()

    fun showAddButtonInstantly()

    fun hideAddButton()

    fun setAddButtonClickListener(listener: (()-> Unit)?)

    fun showSnackMessage(message: String, duration: Int = defaultSnackBarDuration)

}

val Fragment.mainHost
get() = requireActivity() as MainHost