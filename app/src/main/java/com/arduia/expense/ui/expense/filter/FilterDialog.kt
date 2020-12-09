package com.arduia.expense.ui.expense.filter

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.arduia.expense.R
import com.arduia.expense.databinding.FilterExpenseBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class FilterDialog : BottomSheetDialogFragment() {

    private var _binding: FilterExpenseBinding? = null
    private val binding get() = _binding!!

    private val filterDateFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)

    private var filterApplyListener: OnFilterApplyListener? = null

    private var startTime = 0L
    private var endTime = 0L
    private var datePickerDialog: DatePickerDialog? = null
    private var sorting: Sorting = Sorting.ASC

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilterExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            when (sorting) {
                Sorting.ASC     -> rgOrder.check(R.id.rb_asc)
                Sorting.DESC    -> rgOrder.check(R.id.rb_desc)
            }
            tvStartDate.text = filterDateFormat.format(startTime)
            tvEndDate.text = filterDateFormat.format(endTime)

            btnApplyFilter.setOnClickListener {
                filterApplyListener?.onApply(ExpenseLogFilterEnt(startTime, endTime, sorting))
                dismiss()
            }

            cvStartDate.setOnClickListener {
                openStartTimePicker()
            }

            cvEndDate.setOnClickListener {
                openEndTimePicker()
            }

            rgOrder.setOnCheckedChangeListener { _, id ->
                sorting = when(id){
                    R.id.rb_asc -> Sorting.ASC
                    R.id.rb_desc -> Sorting.DESC
                    else -> throw Exception("Order must be asc or desc radio buttons")
                }
            }
        }
    }

    private fun createDatePicker(time: Long, onSelect: (Long)-> Unit): DatePickerDialog{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onSelect.invoke(calendar.timeInMillis)
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
    }

    private fun openStartTimePicker(){
        datePickerDialog?.dismiss()
        datePickerDialog = createDatePicker(startTime){
            changeStartTime(it)
        }
        datePickerDialog?.show()
    }

    private fun openEndTimePicker(){
        datePickerDialog?.dismiss()
        datePickerDialog = createDatePicker(endTime){
            changeEndTime(it)
        }
        datePickerDialog?.show()
    }

    private fun changeStartTime(time: Long){
        this.startTime = time
        binding.tvStartDate.text = filterDateFormat.format(time)
    }

    private fun changeEndTime(time: Long){
        this.endTime = time
        binding.tvEndDate.text = filterDateFormat.format(time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun show(fm: FragmentManager, data: ExpenseLogFilterEnt) {
        val (start, end, sorting) = data
        this.startTime = start
        this.endTime = end
        this.sorting = sorting
        show(fm, "FilterDialog")
    }

    fun setOnFilterApplyListener(listener: OnFilterApplyListener?) {
        this.filterApplyListener = listener
    }

    fun interface OnFilterApplyListener {
        fun onApply(result: ExpenseLogFilterEnt)
    }

}