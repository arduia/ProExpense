package com.arduia.expense.ui.expense.swipe

interface SwipeListenerVH {

    fun onSwipe(isOnTouch: Boolean, dx: Float)

    fun onSwipeItemChanged()

}