package com.arduia.expense

import android.view.View

interface MainHost{

    val defaultSnackBarDuration: Int

    fun showAddButton()

    fun hideAddButton()

    fun setAddButtonClickListener(listener: ()-> Unit)

    fun showSnackMessage(message: String, duration: Int = defaultSnackBarDuration)

}