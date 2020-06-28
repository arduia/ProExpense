package com.arduia.myacc.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arduia.myacc.R
import com.arduia.myacc.databinding.ItemTransactionSelectBinding
import com.arduia.myacc.ui.vto.CostCategory
import com.arduia.myacc.ui.vto.TransactionVto
import java.lang.Exception

class TransactionListAdapter constructor(private val layoutInflater: LayoutInflater):
    PagingDataAdapter<TransactionVto, TransactionListAdapter.VH>(DIFF_CALLBACK){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val itemView = layoutInflater.inflate(R.layout.item_transaction_select, parent, false)

        return VH(ItemTransactionSelectBinding.bind(itemView))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val item = getItem(position) ?: throw Exception("getItem not found at $position")

        with(holder.binding){

            tvName.text = item.name
            tvDate.text = item.date
            tvFiance.text = item.finance

            val imgRes = when(item.cateogry){

                CostCategory.CLOTHES         -> R.drawable.ic_clothes
                CostCategory.HOUSEHOLD       -> R.drawable.ic_household
                CostCategory.TRANSPORTATION  -> R.drawable.ic_transportation
                CostCategory.FOOD            -> R.drawable.ic_food
                CostCategory.UTILITIES       -> R.drawable.ic_utities
                CostCategory.HEARTHCARE      -> R.drawable.ic_healthcare
                CostCategory.SOCIAL          -> R.drawable.ic_social
                CostCategory.EDUCATION       -> R.drawable.ic_education
                CostCategory.DONATIONS       -> R.drawable.ic_donations
                CostCategory.ENTERTAINMENT   -> R.drawable.ic_entertainment
                CostCategory.INCOME          -> R.drawable.ic_borrow

            }

            tvValue.text = item.cost
            imgType.setImageResource(imgRes)
        }

    }

    class VH(val binding: ItemTransactionSelectBinding): RecyclerView.ViewHolder(binding.root)

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
