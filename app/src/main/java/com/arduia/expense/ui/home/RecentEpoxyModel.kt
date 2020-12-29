package com.arduia.expense.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import com.airbnb.epoxy.*
import com.arduia.expense.R
import com.arduia.expense.databinding.RecentListBinding
import com.arduia.expense.ui.vto.ExpenseVto
import timber.log.Timber

data class RecentUiModel(val list: List<ExpenseVto>)

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.recent_list)
abstract class RecentEpoxyModel : EpoxyModelWithHolder<RecentEpoxyModel.VH>() {

    @EpoxyAttribute
    lateinit var moreClickListener: View.OnClickListener

    @EpoxyAttribute
    lateinit var onItemClickListener: (ExpenseVto) -> Unit

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
        lateinit var binding: RecentListBinding
        var adapter: RecentListAdapter? = null

        override fun bindView(itemView: View) {
            binding = RecentListBinding.bind(itemView)
            adapter = RecentListAdapter(LayoutInflater.from(itemView.context)).apply {
                setOnItemClickListener(onItemClickListener)
            }
            binding.rvRecentLists.adapter = adapter
            binding.btnMoreLogs.setOnClickListener(moreClickListener)

        }
    }

}