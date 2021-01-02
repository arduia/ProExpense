package com.arduia.expense.ui.expenselogs.swipe

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeItemCallback :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (viewHolder !is SwipeListenerVH) return
        viewHolder.onSwipeItemChanged()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (viewHolder !is SwipeListenerVH) return
        viewHolder.onSwipe(isCurrentlyActive, dX)
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return Float.MAX_VALUE
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return Float.MAX_VALUE
    }

}