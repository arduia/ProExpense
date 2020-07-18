package com.arduia.expense.ui

import android.view.View

interface MainHost{

    val defaultSnackBarDuration: Int

    fun showAddButton(showInstantly: Boolean = false)

    fun hideAddButton()

    fun setAddButtonClickListener(listener: ()-> Unit)

    fun showSnackMessage(message: String, duration: Int = defaultSnackBarDuration)

}
