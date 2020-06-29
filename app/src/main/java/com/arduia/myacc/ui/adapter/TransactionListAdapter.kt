package com.arduia.myacc.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arduia.myacc.R
import com.arduia.myacc.databinding.ItemTransactionBinding
import com.arduia.myacc.ui.vto.TransactionVto
import java.lang.Exception

class TransactionListAdapter constructor(private val context: Context,
                                         private val categoryProvider: CategoryProvider):
    PagingDataAdapter<TransactionVto, TransactionListAdapter.TransactionVH>(DIFF_CALLBACK){

    var isSelectionMode = false
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    private val layoutInflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {

        val itemView = layoutInflater.inflate(R.layout.item_transaction, parent, false)

        return TransactionVH(ItemTransactionBinding.bind(itemView))
    }

    override fun onBindViewHolder(holder: TransactionVH, position: Int) {

        val item = getItem(position) ?: throw Exception("getItem not found at $position")

        with(holder.binding){

            //For Selection Mode, show select bubble
            imvSelect.visibility = when(isSelectionMode){
                true -> View.VISIBLE
                false -> View.GONE
            }

            tvName.text = item.name
            tvDate.text = item.date
            tvFinanceType.text = item.finance

            tvAmount.text = item.cost
            val imgRes = categoryProvider.getDrawableCategory(item.cateogry)
            imvCategory.setImageResource(imgRes)

        }

    }

    class TransactionVH(val binding: ItemTransactionBinding): RecyclerView.ViewHolder(binding.root)

}

private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<TransactionVto>(){
    override fun areItemsTheSame(oldItem: TransactionVto, newItem: TransactionVto): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: TransactionVto, newItem: TransactionVto): Boolean {
        return  oldItem.name == newItem.name &&
            oldItem.cateogry == newItem.cateogry &&
            oldItem.cost == newItem.cost &&
            oldItem.date == newItem.date &&
            oldItem.finance == newItem.finance
    }
}
