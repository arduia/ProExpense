package com.arduia.expense.ui.home

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.arduia.expense.R
import com.arduia.expense.databinding.ExpenseGraphBinding

data class WeeklyGraphUiModel(val dateRange: String, val rate: Map<Int, Int>)


@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.expense_graph)
abstract class WeeklyGraphEpoxyModel : EpoxyModelWithHolder<WeeklyGraphEpoxyModel.VH>() {

    @EpoxyAttribute
    lateinit var data: WeeklyGraphUiModel

    inner class VH : EpoxyHolder() {
        lateinit var binding: ExpenseGraphBinding
        lateinit var adapter: ExpenseGraphAdapter
        override fun bindView(itemView: View) {
            binding = ExpenseGraphBinding.bind(itemView)
            adapter = ExpenseGraphAdapter()
            binding.expenseGraph.adapter = adapter
        }
    }

    override fun bind(holder: VH) {
        with(holder) {
            adapter.expenseMap = data.rate
            binding.tvDateRange.text = data.dateRange
        }
    }

    override fun unbind(holder: VH) {
        holder.binding.expenseGraph.adapter = null
    }
}