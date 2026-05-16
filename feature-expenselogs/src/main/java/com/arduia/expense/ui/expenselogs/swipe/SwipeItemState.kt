package com.arduia.expense.ui.expenselogs.swipe

import androidx.annotation.IntDef

class SwipeItemState{

    companion object{
        const val STATE_IDLE = -1
        const val STATE_LOCK_START = 1
        const val STATE_LOCK_END = 2
    }

    @IntDef(value =[STATE_IDLE, STATE_LOCK_START, STATE_LOCK_END])
    @MustBeDocumented
    annotation class SwipeState
}
