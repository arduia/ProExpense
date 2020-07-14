package com.arduia.expense.ui.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.arduia.expense.databinding.FragFreeBinding
import com.arduia.expense.databinding.FragRecordBinding
import java.lang.IllegalStateException

class OnBoardingPagerAdapter(private val layoutInflater: LayoutInflater): RecyclerView.Adapter<OnBoardingPagerAdapter.VH>(){

    companion object{
        private const val RECORD_VIEW  = 1
        private const val FREE_VIEW = 2
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val viewBinding:ViewBinding = when(viewType){
            RECORD_VIEW -> FragRecordBinding.inflate(layoutInflater, parent, false)
            FREE_VIEW -> FragFreeBinding.inflate(layoutInflater, parent, false)
            else -> throw IllegalStateException("This type of view doesn't exist.")
        }

        return VH(viewBinding.root)
    }

    override fun getItemViewType(position: Int): Int =

        when(position){
            0 -> RECORD_VIEW
            1 -> FREE_VIEW
            else -> throw IllegalStateException("Item Type at $position not found")
        }

    override fun getItemCount() = 2

    override fun onBindViewHolder(holder: VH, position: Int) {    }

    inner class VH(val view:View): RecyclerView.ViewHolder(view)
}
