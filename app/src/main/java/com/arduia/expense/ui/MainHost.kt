package com.arduia.expense.ui

import androidx.fragment.app.Fragment


interface MainHost{

    val defaultSnackBarDuration: Int

    fun showAddButton()

    fun showAddButtonInstantly()

    fun hideAddButton()

    fun setAddButtonClickListener(listener: ()-> Unit)

    fun showSnackMessage(message: String, duration: Int = defaultSnackBarDuration)

}

val Fragment.mainHost
get() = requireActivity() as MainHost