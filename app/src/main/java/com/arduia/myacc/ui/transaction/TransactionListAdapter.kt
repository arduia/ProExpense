package com.arduia.myacc.ui.transaction

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arduia.myacc.R
import com.arduia.myacc.databinding.ItemTransactionBinding
import com.arduia.myacc.ui.vto.TransactionVto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class TransactionListAdapter constructor(private val context: Context):
    PagedListAdapter<TransactionVto, TransactionListAdapter.TransactionVH>(
        DIFF_CALLBACK
    ){

    private val layoutInflater by lazy { LayoutInflater.from(context) }

    private var itemClickListener: (TransactionVto) -> Unit = {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {

        val itemView = layoutInflater.inflate(R.layout.item_transaction, parent, false)

        return TransactionVH(ItemTransactionBinding.bind(itemView), itemClickListener)
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

    fun getItemFromPosition(position: Int):TransactionVto
            = getItem(position) ?: throw Exception("Item Not Found Exception")


    inner class TransactionVH(val binding: ItemTransactionBinding,
                              private val listener: (TransactionVto) -> Unit):
        RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        init {
            binding.cdTransaction.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener(getItem(adapterPosition)!!)
        }
    }

    fun setItemClickListener(listener: (TransactionVto) -> Unit){
        itemClickListener = listener
    }

}

private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<TransactionVto>(){

    override fun areItemsTheSame(oldItem: TransactionVto, newItem: TransactionVto): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: TransactionVto, newItem: TransactionVto): Boolean {
        return  oldItem.name == newItem.name &&
            oldItem.category == newItem.category &&
            oldItem.amount == newItem.amount &&
            oldItem.date == newItem.date &&
            oldItem.finance == newItem.finance
    }

}

