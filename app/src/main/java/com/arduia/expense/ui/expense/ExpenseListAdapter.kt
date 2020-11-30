package com.arduia.expense.ui.expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.databinding.ItemExpenseLogBinding
import com.arduia.expense.ui.expense.swipe.SwipeFrameLayout
import com.arduia.expense.ui.expense.swipe.SwipeListenerVH
import com.arduia.expense.ui.vto.ExpenseVto
import timber.log.Timber
import java.lang.Exception

class ExpenseListAdapter constructor(private val layoutInflater: LayoutInflater) :
    PagedListAdapter<ExpenseVto, ExpenseListAdapter.ExpenseVH>(DIFF_CALLBACK) {

    private var onItemClickListener: (ExpenseVto) -> Unit = {}

    private var onItemDeleteListener: (ExpenseVto)-> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseVH {

        val viewBinding = ItemExpenseLogBinding.inflate(layoutInflater, parent, false)

        return ExpenseVH(viewBinding)
    }

    override fun onBindViewHolder(holder: ExpenseVH, position: Int) {

        val item = getItem(position) ?: throw Exception("getItem not found at $position")
        holder.binding.root.bindData(item)
    }

    fun getItemFromPosition(position: Int): ExpenseVto =
        getItem(position) ?: throw Exception("Item Not Found Exception")

    inner class ExpenseVH(val binding: ItemExpenseLogBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, SwipeListenerVH,
        SwipeFrameLayout.OnSelectedChangedListener {

        init {
            binding.cdExpense.setOnClickListener(this)
            binding.root.setOnSelectedChangedListener(this)
            binding.imvDeleteIcon.setOnClickListener(this)
        }

        override fun onSelectedChanged(isSelected: Boolean) {
            Timber.d("${adapterPosition }onSelected $isSelected")
        }

        override fun onSwipe(isOnTouch: Boolean, dx: Float) {
            binding.root.onSwipe(isOnTouch, dx)
        }

        override fun onClick(v: View?) {
            if(v == null) return
            when(v.id){
                binding.cdExpense.id -> onItemClickListener(getItemFromPosition(adapterPosition))
                binding.imvDeleteIcon.id -> onItemDeleteListener.invoke(getItemFromPosition(adapterPosition))
            }
        }

        override fun onSwipeItemChanged() {
            binding.root.onStartSwipe()
        }

    }

    fun setOnItemClickListener(listener: (ExpenseVto) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnItemDeleteListener(listener: (ExpenseVto) -> Unit){
        onItemDeleteListener = listener
    }

}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ExpenseVto>() {

    override fun areItemsTheSame(oldItem: ExpenseVto, newItem: ExpenseVto): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ExpenseVto, newItem: ExpenseVto): Boolean {
        return oldItem.name == newItem.name &&
                oldItem.category == newItem.category &&
                oldItem.amount == newItem.amount &&
                oldItem.date == newItem.date &&
                oldItem.finance == newItem.finance
    }

}

