package com.arduia.expense.ui.expenselogs.swipe

interface SwipeListenerVH {

    fun onSwipe(isOnTouch: Boolean, dx: Float)

    fun onSwipeItemChanged()

}