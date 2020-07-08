package com.arduia.expense.ui.entry

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.databinding.ItemCategoryBinding

class CategoryListAdapter(private val layoutInflater: LayoutInflater):
    ListAdapter<ExpenseCategoryVto, CategoryListAdapter.VH>( DIFF_CALLBACK)
{
    private var itemClickListener: (ExpenseCategoryVto)-> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH{

        val viewBinding = ItemCategoryBinding.inflate(layoutInflater,parent, false)

        return VH(viewBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        with(holder.viewBinding){
            val item = getItem(position)
            tvName.text = item.name
            imvCategory.visibility = when(item.isSelected){
                true -> View.VISIBLE
                else -> View.GONE
            }
        }

    }

    private fun setOnItemClickListener(listener: (ExpenseCategoryVto) -> Unit){
        this.itemClickListener = listener
    }

    inner class VH(val viewBinding: ItemCategoryBinding):
        RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener{

        init {
            viewBinding.cdCategory.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemClickListener.invoke(getItem(adapterPosition))
            viewBinding.cdCategory.isChecked = true
        }
    }
}

private val DIFF_CALLBACK get() = object : DiffUtil.ItemCallback<ExpenseCategoryVto>(){
    override fun areItemsTheSame(
        oldItem: ExpenseCategoryVto,
        newItem: ExpenseCategoryVto
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ExpenseCategoryVto,
        newItem: ExpenseCategoryVto
    ): Boolean {
        return (oldItem.name == newItem.name) &&
                (oldItem.isSelected == newItem.isSelected)
    }
}