package com.arduia.expense.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.R
import com.arduia.expense.databinding.ItemExpenseBinding
import com.arduia.expense.ui.vto.ExpenseVto

class RecentListAdapter constructor(private val layoutInflater: LayoutInflater):
    ListAdapter<ExpenseVto, RecentListAdapter.VH>(DIFF_CALLBACK){

    // Mostly unregister, so should be able to be claimed.
    private var onSingleItemInsertListener: ((Unit) -> Unit)? = null

    private var onItemClickListener: (ExpenseVto) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val viewBinding = ItemExpenseBinding.inflate(layoutInflater, parent, false)

        return VH(viewBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val item = getItem(position)

        with(holder.binding){
            tvName.text = item.name
            tvDate.text = item.date
            imvCategory.setImageResource(item.category)
            tvAmount.text = item.amount
       }

    }

    override fun onCurrentListChanged(previousList: MutableList<ExpenseVto>,
                                      currentList: MutableList<ExpenseVto>){

        super.onCurrentListChanged(previousList, currentList)

        //New Insertion should be ignored
        if( currentList.size < 2 || previousList.isEmpty() ) return

        val oldItemAtFirst = previousList.first()
        val newItemAtSecond = currentList[1]

        if(oldItemAtFirst.id == newItemAtSecond.id ){
            //There is new Item at First Place
            onSingleItemInsertListener?.invoke(Unit)
        }
    }

    fun setItemInsertionListener(listener: (Unit) -> Unit){
        onSingleItemInsertListener = listener
    }

    fun setOnItemClickListener(listener: (ExpenseVto) -> Unit){
        this.onItemClickListener = listener
    }

    inner class VH(val binding: ItemExpenseBinding):
        RecyclerView.ViewHolder(binding.root), View.OnClickListener{

        init { binding.cdExpense.setOnClickListener(this) }

        override fun onClick(v: View?) {
            onItemClickListener(getItem(adapterPosition))
        }
    }

}

private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<ExpenseVto>(){

    override fun areItemsTheSame(oldItem: ExpenseVto, newItem: ExpenseVto): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ExpenseVto, newItem: ExpenseVto): Boolean {
        return  oldItem.name == newItem.name &&
            oldItem.category == newItem.category &&
            oldItem.amount == newItem.amount &&
            oldItem.date == newItem.date &&
            oldItem.finance == newItem.finance
    }

}
