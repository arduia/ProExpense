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

class RecentListAdapter constructor( private val layoutInflater: LayoutInflater):
    ListAdapter<ExpenseVto, RecentListAdapter.VH>(DIFF_CALLBACK){

    private var newItemInsertionListener: ((Unit) -> Unit)? = null

    private var onItemClickListener: (ExpenseVto) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val itemView = layoutInflater.inflate(R.layout.item_expense, parent, false)

        return VH( ItemExpenseBinding.bind(itemView ), onItemClickListener)
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
        previousList: MutableList<ExpenseVto>,
        currentList: MutableList<ExpenseVto>
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

    fun setOnItemClickListener(listener: (ExpenseVto) -> Unit){
        this.onItemClickListener = listener
    }

    inner class VH(val binding: ItemExpenseBinding, private val listener: (ExpenseVto) -> Unit): RecyclerView.ViewHolder(binding.root), View.OnClickListener{

        init {
            binding.cdTransaction.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            listener(getItem(adapterPosition))
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
