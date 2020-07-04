package com.arduia.myacc.ui.transaction

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arduia.myacc.R
import com.arduia.myacc.databinding.ItemExpenseBinding
import com.arduia.myacc.ui.vto.ExpenseVto
import java.lang.Exception

class ExpenseListAdapter constructor(private val context: Context):
    PagedListAdapter<ExpenseVto, ExpenseListAdapter.TransactionVH>(
        DIFF_CALLBACK
    ){

    private val layoutInflater by lazy { LayoutInflater.from(context) }

    private var itemClickListener: (ExpenseVto) -> Unit = {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {

        val itemView = layoutInflater.inflate(R.layout.item_expense, parent, false)

        return TransactionVH(ItemExpenseBinding.bind(itemView), itemClickListener)
    }

    override fun onBindViewHolder(holder: TransactionVH, position: Int) {

            val item = getItem(position) ?: throw Exception("getItem not found at $position")

            with(holder.binding){
                tvName.text = item.name
                tvDate.text = item.date
                tvFinanceType.text = item.finance
                tvAmount.text = item.amount
                imvCategory.setImageResource(item.category)
        }
    }

    fun getItemFromPosition(position: Int):ExpenseVto
            = getItem(position) ?: throw Exception("Item Not Found Exception")


    inner class TransactionVH(val binding: ItemExpenseBinding,
                              private val listener: (ExpenseVto) -> Unit):
        RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        init {
            binding.cdTransaction.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener(getItem(adapterPosition)!!)
        }
    }

    fun setItemClickListener(listener: (ExpenseVto) -> Unit){
        itemClickListener = listener
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

