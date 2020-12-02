package com.arduia.expense.ui.expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.R
import com.arduia.expense.databinding.ItemExpenseDateHeaderBinding
import com.arduia.expense.databinding.ItemExpenseLogBinding
import com.arduia.expense.ui.expense.swipe.SwipeFrameLayout
import com.arduia.expense.ui.expense.swipe.SwipeListenerVH
import timber.log.Timber
import java.lang.Exception

class ExpenseLogAdapter constructor(private val layoutInflater: LayoutInflater) :
    ListAdapter<ExpenseLogVo, RecyclerView.ViewHolder>(DIFF_CALLBACK){

    private var onItemClickListener: (ExpenseLogVo.Log) -> Unit = {}

    private var onItemDeleteListener: (ExpenseLogVo.Log) -> Unit = {}

    companion object {
        private const val TYPE_LOG = 0
        private const val TYPE_HEADER = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItemFromPosition(position)) {
            is ExpenseLogVo.Log -> TYPE_LOG
            is ExpenseLogVo.Header -> TYPE_HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemExpenseDateHeaderBinding.inflate(layoutInflater, parent, false)
                HeaderVH(binding)
            }
            TYPE_LOG -> {
                val binding = ItemExpenseLogBinding.inflate(layoutInflater, parent, false)
                LogVH(binding)
            }
            else -> throw Exception("Invalid ViewType($viewType)")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItemFromPosition(position)
        when {
            (holder is LogVH) && (item is ExpenseLogVo.Log) -> {
                bindLogVH(holder.binding, item)
            }
            (holder is HeaderVH) && (item is ExpenseLogVo.Header) -> {
                bindHeaderVH(holder.binding, item)
            }
        }
    }

    private fun bindLogVH(binding: ItemExpenseLogBinding, data: ExpenseLogVo.Log) {
        val item = data.expenseLog
        with(binding) {
            tvName.text = item.name
            tvDate.text = item.date
            tvCurrencySymbol.text = item.currencySymbol
            tvAmount.text = item.amount
        }
    }

    private fun bindHeaderVH(binding: ItemExpenseDateHeaderBinding, data: ExpenseLogVo.Header) {
        binding.tvDate.text = data.date
    }

    private fun getItemFromPosition(position: Int) = getItem(position)!!

    inner class HeaderVH(val binding: ItemExpenseDateHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class LogVH(val binding: ItemExpenseLogBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, SwipeListenerVH,
        SwipeFrameLayout.OnSelectedChangedListener {

        init {
            binding.cdExpense.setOnClickListener(this)
            binding.root.setOnSelectedChangedListener(this)
            binding.imvDeleteIcon.setOnClickListener(this)
        }

        override fun onSelectedChanged(isSelected: Boolean) {
            Timber.d("${adapterPosition}onSelected $isSelected")
        }

        override fun onSwipe(isOnTouch: Boolean, dx: Float) {
            binding.root.onSwipe(isOnTouch, dx)
        }

        override fun onClick(v: View?) {
            if (v == null) return
            val item = getItemFromPosition(adapterPosition)
            if (item !is ExpenseLogVo.Log) return
            when (v.id) {
                binding.cdExpense.id -> {
                    onItemClickListener(item)
                }
                binding.imvDeleteIcon.id -> {
                    onItemDeleteListener.invoke(item)
                }
            }
        }

        override fun onSwipeItemChanged() {
            binding.root.onStartSwipe()
        }
    }

    fun setOnItemClickListener(listener: (ExpenseLogVo.Log) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnItemDeleteListener(listener: (ExpenseLogVo.Log) -> Unit) {
        onItemDeleteListener = listener
    }

}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ExpenseLogVo>() {

    override fun areItemsTheSame(oldItem: ExpenseLogVo, newItem: ExpenseLogVo): Boolean {
        return (oldItem is ExpenseLogVo.Header && newItem is ExpenseLogVo.Header) ||
                (oldItem is ExpenseLogVo.Log && newItem is ExpenseLogVo.Log)
    }

    override fun areContentsTheSame(oldItem: ExpenseLogVo, newItem: ExpenseLogVo): Boolean {
        return when (oldItem) {
            is ExpenseLogVo.Log -> if (newItem is ExpenseLogVo.Log) oldItem.expenseLog == newItem.expenseLog else false
            is ExpenseLogVo.Header -> if (newItem is ExpenseLogVo.Header) oldItem.date == newItem.date else false
        }
    }
}

