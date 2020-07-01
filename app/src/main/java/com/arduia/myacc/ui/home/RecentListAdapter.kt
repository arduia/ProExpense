package com.arduia.myacc.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.myacc.R
import com.arduia.myacc.databinding.ItemTransactionBinding
import com.arduia.myacc.ui.vto.TransactionVto
import java.lang.Exception

class RecentListAdapter constructor(private val layoutInflater: LayoutInflater):
    ListAdapter<TransactionVto, RecentListAdapter.VH>(DIFF_CALLBACK){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val itemView = layoutInflater.inflate(R.layout.item_transaction, parent, false)

        return VH( ItemTransactionBinding.bind(itemView ) )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val item = getItem(position)

        with(holder.binding){
            tvName.text = item.name
            tvDate.text = item.date
            tvFinanceType.text = item.finance
            imvCategory.setImageResource(item.category)
            tvAmount.text = item.cost
       }
    }

    class VH(val binding: ItemTransactionBinding): RecyclerView.ViewHolder(binding.root)

}

private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<TransactionVto>(){

    override fun areItemsTheSame(oldItem: TransactionVto, newItem: TransactionVto): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: TransactionVto, newItem: TransactionVto): Boolean {
        return  oldItem.name == newItem.name &&
            oldItem.category == newItem.category &&
            oldItem.cost == newItem.cost &&
            oldItem.date == newItem.date &&
            oldItem.finance == newItem.finance
    }

}
