package com.arduia.myacc.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.myacc.R
import com.arduia.myacc.databinding.ItemTransactionBinding
import com.arduia.myacc.ui.vto.TransactionVto
import kotlinx.coroutines.CoroutineScope

class RecentListAdapter constructor( private val layoutInflater: LayoutInflater):
    ListAdapter<TransactionVto, RecentListAdapter.VH>(DIFF_CALLBACK){

    private var newItemInsertionListener: ((Unit) -> Unit)? = null

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
            tvAmount.text = item.amount
       }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<TransactionVto>,
        currentList: MutableList<TransactionVto>
    ) {
        super.onCurrentListChanged(previousList, currentList)

        if( currentList.size < 2 || previousList.isEmpty() ) return

        val oldItemAtFirst = previousList.first()
        val newItemAtSecond = currentList[1]

        if(oldItemAtFirst.id == newItemAtSecond.id ){
            //There is new Item at First
            newItemInsertionListener?.invoke(Unit)
        }
    }

    fun setItemInsertionListener( listener: (Unit)-> Unit){
        newItemInsertionListener = listener
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
            oldItem.amount == newItem.amount &&
            oldItem.date == newItem.date &&
            oldItem.finance == newItem.finance
    }

}
