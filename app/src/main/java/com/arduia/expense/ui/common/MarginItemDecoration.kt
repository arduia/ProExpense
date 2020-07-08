package com.arduia.expense.ui.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration( private val spaceHeight: Int = 0,
                            private val spaceSide: Int? = null,
                            private val isHorizontal: Boolean = false): RecyclerView.ItemDecoration(){

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

         with(outRect){
//             Fist tem should has top height
             if(parent.getChildAdapterPosition(view) == 0){
                when(isHorizontal){
                    false ->  top = spaceHeight
                }
             }

             bottom = spaceHeight

             spaceSide?.let {
                 left = it
                 right = it
             }
         }

    }

}
