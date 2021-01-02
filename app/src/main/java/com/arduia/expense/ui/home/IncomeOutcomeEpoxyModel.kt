package com.arduia.expense.ui.home

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.*
import com.arduia.expense.R
import com.arduia.expense.databinding.LayoutExpenseInOutBinding

data class IncomeOutcomeUiModel(
    val incomeValue: String,
    val outComeValue: String,
    val currencySymbol: String,
    val dateRange: String
)

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.layout_expense_in_out)
abstract class IncomeOutcomeEpoxyModel : EpoxyModelWithHolder<IncomeOutcomeEpoxyModel.VH>() {

    @EpoxyAttribute
    lateinit var data: IncomeOutcomeUiModel

    override fun bind(holder: VH) {
        with(holder.binding) {
            tvIncomeValue.text = data.incomeValue
            tvOutcomeValue.text = data.outComeValue
            tvOutcomeSymbol.text = data.currencySymbol
            tvIncomeSymobol.text = data.currencySymbol
            tvDateRange.text = data.dateRange
        }
    }

    inner class VH : EpoxyHolder() {
        lateinit var binding: LayoutExpenseInOutBinding
        override fun bindView(itemView: View) {
            binding = LayoutExpenseInOutBinding.bind(itemView)
        }
    }
}