package com.arduia.expense.ui.expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.databinding.ItemExpenseBinding
import com.arduia.expense.ui.vto.ExpenseVto
import timber.log.Timber
import java.lang.Exception

class ExpenseListAdapter constructor(private val layoutInflater: LayoutInflater):
    PagedListAdapter<ExpenseVto, ExpenseListAdapter.ExpenseVH>( DIFF_CALLBACK ){

    private var onItemClickListener: (ExpenseVto) -> Unit = {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseVH {

        val viewBinding = ItemExpenseBinding.inflate(layoutInflater, parent, false)

        return ExpenseVH(viewBinding)
    }

    override fun onBindViewHolder(holder: ExpenseVH, position: Int) {

            val item = getItem(position) ?: throw Exception("getItem not found at $position")

            with(holder.binding){

                tvName.text = item.name
                tvDate.text = item.date
                tvAmount.text = item.amount
                imvCategory.setImageResource(item.category)
        }
    }

    fun getItemFromPosition(position: Int): ExpenseVto
            = getItem(position) ?: throw Exception("Item Not Found Exception")


    inner class ExpenseVH(val binding: ItemExpenseBinding):
        RecyclerView.ViewHolder(binding.root), View.OnClickListener{

        init { binding.cdExpense.setOnClickListener(this)  }

        override fun onClick(v: View?) {
            onItemClickListener(getItem(adapterPosition)!!)
        }
    }

    fun setOnItemClickListener(listener: ( ExpenseVto) -> Unit){
        onItemClickListener = listener
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

