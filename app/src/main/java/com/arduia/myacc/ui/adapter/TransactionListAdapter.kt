package com.arduia.myacc.ui.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
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

    private var itemClickListener: (TransactionVto) -> Unit = {}

    private val selectedDrawable by lazy {
        return@lazy if(Build.VERSION.SDK_INT <23){
            ColorDrawable(context.resources.getColor(R.color.primaryColor))
        }else{
            ColorDrawable(context.resources.getColor(R.color.primaryColor, null))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {

        val itemView = layoutInflater.inflate(R.layout.item_transaction, parent, false)

        return TransactionVH(ItemTransactionBinding.bind(itemView), itemClickListener)
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

            if(item.isSelected){
                imvSelect.setImageDrawable(selectedDrawable)
            }
            cdTransaction.setOnClickListener(holder)
        }
    }

    inner class TransactionVH(val binding: ItemTransactionBinding,
                              private val listener: (TransactionVto) -> Unit):
        RecyclerView.ViewHolder(binding.root), View.OnClickListener{

        override fun onClick(v: View?) {
            listener(getItem(absoluteAdapterPosition)!!)
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
            oldItem.cateogry == newItem.cateogry &&
            oldItem.cost == newItem.cost &&
            oldItem.date == newItem.date &&
            oldItem.finance == newItem.finance
    }

}

