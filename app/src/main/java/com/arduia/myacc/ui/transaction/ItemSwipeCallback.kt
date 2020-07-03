package com.arduia.myacc.ui.transaction

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemSwipeCallback :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END){

    private var swipeListener : (Int) -> Unit = {}

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
       swipeListener(viewHolder.adapterPosition)
    }

    fun setSwipeListener(listener: (Int) -> Unit){
        swipeListener = listener
    }
}