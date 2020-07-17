package com.arduia.expense.ui.entry

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class CategoryItemAnimator: DefaultItemAnimator(){
    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder?,
        newHolder: RecyclerView.ViewHolder?,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        return false
    }

    override fun onChangeFinished(item: RecyclerView.ViewHolder?, oldItem: Boolean) {

    }
}