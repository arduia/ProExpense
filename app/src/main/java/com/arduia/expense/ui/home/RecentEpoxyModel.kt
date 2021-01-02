package com.arduia.expense.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import com.airbnb.epoxy.*
import com.arduia.expense.R
import com.arduia.expense.databinding.LayoutRecentListsBinding
import com.arduia.expense.ui.expenselogs.ExpenseUiModel

data class RecentUiModel(val list: List<ExpenseUiModel>)

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.layout_recent_lists)
abstract class RecentEpoxyModel : EpoxyModelWithHolder<RecentEpoxyModel.VH>() {

    @EpoxyAttribute
    lateinit var moreClickListener: View.OnClickListener

    @EpoxyAttribute
    lateinit var onItemClickListener: (ExpenseUiModel) -> Unit

    @EpoxyAttribute
    lateinit var recentData: RecentUiModel

    override fun unbind(holder: VH) {
        super.unbind(holder)
        holder.binding.btnMoreLogs.setOnClickListener(null)
        holder.adapter = null
    }


    override fun bind(holder: VH) {
        super.bind(holder)
        if(recentData.list.isEmpty()){
            holder.binding.root.visibility = View.GONE
        }else{
            holder.binding.root.visibility = View.VISIBLE
        }
        holder.adapter?.submitList(recentData.list)
    }

    inner class VH : EpoxyHolder() {
        lateinit var binding: LayoutRecentListsBinding
        var adapter: RecentListAdapter? = null

        override fun bindView(itemView: View) {
            binding = LayoutRecentListsBinding.bind(itemView)
            adapter = RecentListAdapter(LayoutInflater.from(itemView.context)).apply {
                setOnItemClickListener(onItemClickListener)
            }
            binding.rvRecentLists.adapter = adapter
            binding.btnMoreLogs.setOnClickListener(moreClickListener)

        }
    }

}