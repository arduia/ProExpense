package com.arduia.expense.ui.statistics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.databinding.ItemCategoryStatisticBinding

class CategoryStatisticListAdapter(private val layoutInflater: LayoutInflater) :
    ListAdapter<CategoryStatisticVo, CategoryStatisticListAdapter.VH>(
        DIFFER
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCategoryStatisticBinding.inflate(layoutInflater, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvCategoryName.text = item.name
            tvProgress.text = item.progressText
            pvProgress.progress = item.progress
        }
    }

    inner class VH(val binding: ItemCategoryStatisticBinding) :
        RecyclerView.ViewHolder(binding.root)

}

private val DIFFER
    get() = object : DiffUtil.ItemCallback<CategoryStatisticVo>() {
        override fun areItemsTheSame(
            oldItem: CategoryStatisticVo,
            newItem: CategoryStatisticVo
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CategoryStatisticVo,
            newItem: CategoryStatisticVo
        ): Boolean {
            return (oldItem.name == newItem.name) and
                    (oldItem.progress == newItem.progress) and
                    (oldItem.progressText == newItem.progressText)
        }
    }