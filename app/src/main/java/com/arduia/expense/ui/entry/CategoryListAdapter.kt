package com.arduia.expense.ui.entry

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.databinding.ItemCategoryBinding
import com.arduia.expense.ui.common.ExpenseCategory

class CategoryListAdapter(private val layoutInflater: LayoutInflater) :
    ListAdapter<ExpenseCategory, CategoryListAdapter.VH>(DIFF_CALLBACK) {

    private var itemClickListener: (ExpenseCategory) -> Unit = {}

    var selectedItem: ExpenseCategory? = null
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val viewBinding = ItemCategoryBinding.inflate(layoutInflater, parent, false)

        return VH(viewBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        with(holder.viewBinding) {
            val item = getItem(position)
            tvName.text = item.name
            imvCategory.visibility = when (item.id == selectedItem?.id) {
                true ->{
                    cdCategory.isChecked = true
                    View.VISIBLE
                }
                else -> {
                    cdCategory.isChecked = false
                    View.GONE
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (ExpenseCategory) -> Unit) {
        this.itemClickListener = listener
    }

    inner class VH(val viewBinding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener {

        init {
            viewBinding.cdCategory.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemClickListener.invoke(getItem(adapterPosition))
            viewBinding.cdCategory.isChecked = true
        }
    }
}

private val DIFF_CALLBACK
    get() = object : DiffUtil.ItemCallback<ExpenseCategory>() {
        override fun areItemsTheSame(
            oldItem: ExpenseCategory,
            newItem: ExpenseCategory
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ExpenseCategory,
            newItem: ExpenseCategory
        ): Boolean {
            return (oldItem.name == newItem.name) &&
                    (oldItem.id == newItem.id) &&
                    (oldItem.img == newItem.img)
        }
    }