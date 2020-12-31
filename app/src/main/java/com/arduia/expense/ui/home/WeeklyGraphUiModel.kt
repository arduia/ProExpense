package com.arduia.expense.ui.home

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.arduia.expense.R
import com.arduia.expense.databinding.LayoutExpenseGraphBinding

data class WeeklyGraphUiModel(val dateRange: String, val rate: Map<Int, Int>)


@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.layout_expense_graph)
abstract class WeeklyGraphEpoxyModel : EpoxyModelWithHolder<WeeklyGraphEpoxyModel.VH>() {

    @EpoxyAttribute
    lateinit var data: WeeklyGraphUiModel

    inner class VH : EpoxyHolder() {
        lateinit var binding: LayoutExpenseGraphBinding
        lateinit var adapter: ExpenseGraphAdapter
        override fun bindView(itemView: View) {
            binding = LayoutExpenseGraphBinding.bind(itemView)
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