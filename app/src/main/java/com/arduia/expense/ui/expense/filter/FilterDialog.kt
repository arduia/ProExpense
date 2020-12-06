package com.arduia.expense.ui.expense.filter

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.arduia.expense.R
import com.arduia.expense.databinding.FilterExpenseBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class FilterDialog : BottomSheetDialogFragment() {

    private lateinit var binding: FilterExpenseBinding

    private lateinit var data: ExpenseLogFilterVo

    private val filterDateFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)

    private var filterApplyListener: OnFilterApplyListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FilterExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            when (data.sorting) {
                Sorting.ASC -> rgOrder.check(R.id.rb_asc)
                Sorting.DESC -> rgOrder.check(R.id.rb_desc)
            }

            tvStartDate.text = filterDateFormat.format(data.startTime)
            tvEndDate.text = filterDateFormat.format(data.endTime)

            btnApplyFilter.setOnClickListener {
                filterApplyListener?.onApply(data)
                dismiss()
            }

        }
    }

    fun show(fm: FragmentManager, data: ExpenseLogFilterVo) {
        this.data = data
        show(fm, "FilterDialog")
    }

    fun setOnFilterApplyListener(listener: OnFilterApplyListener) {
        this.filterApplyListener = listener
    }

    fun interface OnFilterApplyListener {
        fun onApply(result: ExpenseLogFilterVo)
    }

}