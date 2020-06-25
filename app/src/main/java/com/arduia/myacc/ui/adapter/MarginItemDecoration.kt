package com.arduia.myacc.ui.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration( private val spaceHeight: Int = 10,
                            private val spaceSide: Int? = null): RecyclerView.ItemDecoration(){

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

         with(outRect){
//             Fist tem should has top height
             if(parent.getChildAdapterPosition(view) == 0){
                 top = spaceHeight
             }
             bottom = spaceHeight

             spaceSide?.let {
                 left = it
                 right = it
             }
         }

    }

}