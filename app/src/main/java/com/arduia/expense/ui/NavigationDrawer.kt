package com.arduia.expense.ui

/**
 * Interface between host Activity and child fragments to make Host Activity's drawer actions.
 */
interface NavigationDrawer {

    fun openDrawer()

    fun closeDrawer()

    fun lockDrawer()

    fun unlockDrawer()

}
