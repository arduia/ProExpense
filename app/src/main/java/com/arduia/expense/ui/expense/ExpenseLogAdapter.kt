package com.arduia.expense.ui.expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.databinding.ItemExpenseDateHeaderBinding
import com.arduia.expense.databinding.ItemExpenseLogBinding
import com.arduia.expense.ui.expense.swipe.SwipeFrameLayout
import com.arduia.expense.ui.expense.swipe.SwipeItemState
import com.arduia.expense.ui.expense.swipe.SwipeListenerVH
import com.arduia.expense.ui.expense.swipe.SwipeStateHolder
import timber.log.Timber
import java.lang.Exception

class ExpenseLogAdapter constructor(private val layoutInflater: LayoutInflater) :
    PagedListAdapter<ExpenseLogVo, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: (ExpenseLogVo.Log) -> Unit = {}

    private var onItemDeleteListener: (ExpenseLogVo.Log) -> Unit = {}

    private var swipeState = SwipeStateHolder()

    private var onStateChangeListener: (holder: SwipeStateHolder, item: ExpenseLogVo.Log?)
    -> Unit = { _, _ -> }

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

    fun restoreState(state: SwipeStateHolder) {
        this.swipeState = state
    }

//    fun selectAllItems() {
//        val list = currentList ?: return
//        if (list.isEmpty()) return
//        swipeState.clear()
//        val stateLockStart = SwipeItemState.STATE_LOCK_START
//        list.forEach {
//            if (it is ExpenseLogVo.Log) {
//                swipeState.updateState(it.expenseLog.id, stateLockStart)
//            }
//        }
//        onStateChangeListener.invoke(swipeState, null)
//        notifyDataSetChanged()
//    }

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
        binding.root.bindData(data, state = swipeState.getStateOrNull(data.expenseLog.id))
    }

    private fun bindHeaderVH(binding: ItemExpenseDateHeaderBinding, data: ExpenseLogVo.Header) {
        binding.tvDate.text = data.date
    }

    private fun getItemFromPosition(position: Int) = getItem(position)!!

    inner class HeaderVH(val binding: ItemExpenseDateHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class LogVH(val binding: ItemExpenseLogBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, SwipeListenerVH,
        SwipeFrameLayout.OnSelectedChangedListener, SwipeFrameLayout.OnPrepareChangedListener {

        init {
            binding.viewBg.setOnClickListener(this)
            binding.root.setOnSelectedChangedListener(this)
            binding.root.setOnPrepareChangedListener(this)
            binding.imvDeleteIcon.setOnClickListener(this)
        }

        override fun onSelectedChanged(isSelected: Boolean) {
            onStateChanged(if (isSelected) SwipeItemState.STATE_LOCK_START else SwipeItemState.STATE_IDLE)
        }

        override fun onPreparedChanged(isPrepared: Boolean) {
            onStateChanged(if (isPrepared) SwipeItemState.STATE_LOCK_END else SwipeItemState.STATE_IDLE)
        }

        private fun onStateChanged(@SwipeItemState.SwipeState state: Int) {

            if (adapterPosition == -1) return
            val item = getItem(adapterPosition) as? ExpenseLogVo.Log ?: return

            if (state == SwipeItemState.STATE_LOCK_END) {
                swipeState.clear()
                swipeState.updateState(item.expenseLog.id, state)
                onStateChangeListener.invoke(swipeState, item)
                notifyDataSetChanged()
                return
            }
            swipeState.updateState(item.expenseLog.id, state)
            onStateChangeListener.invoke(swipeState, item)
        }

        override fun onSwipe(isOnTouch: Boolean, dx: Float) {
            binding.root.onSwipe(isOnTouch, dx)
        }

        override fun onClick(v: View?) {
            if (v == null) return
            if(adapterPosition == -1) return
            val item = getItemFromPosition(adapterPosition)

            if (item !is ExpenseLogVo.Log) return

            when (v.id) {
                binding.viewBg.id -> {
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

    fun setOnClickListener(listener: (ExpenseLogVo.Log) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnStateChangeListener(listener: (holder: SwipeStateHolder, item: ExpenseLogVo.Log?) -> Unit) {
        this.onStateChangeListener = listener
    }

    fun setOnDeleteListener(listener: (ExpenseLogVo.Log) -> Unit) {
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

