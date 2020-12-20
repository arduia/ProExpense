package com.arduia.expense.ui.common.filter

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.arduia.expense.R
import com.arduia.expense.databinding.FilterExpenseBinding
import com.arduia.expense.ui.common.ext.setDayAsEnd
import com.arduia.expense.ui.common.ext.setDayAsStart
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateRangeSortingFilterDialog(private val isSortingEnabled: Boolean = true) :
    BottomSheetDialogFragment() {

    private var _binding: FilterExpenseBinding? = null
    private val binding get() = _binding!!

    private val filterDateFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)

    private var filterApplyListener: OnFilterApplyListener? = null

    private var startTimeSelected = 0L
    private var endTimeSelected = 0L

    private var minTime = 0L
    private var maxTime = 0L


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

            tvStartDate.text = filterDateFormat.format(startTimeSelected)
            tvEndDate.text = filterDateFormat.format(endTimeSelected)

            btnApplyFilter.setOnClickListener {
                filterApplyListener?.onApply(
                    DateRangeSortingEnt(
                        startTimeSelected,
                        endTimeSelected,
                        sorting
                    )
                )
                dismiss()
            }

            cvStartDate.setOnClickListener {
                openStartTimePicker()
            }

            cvEndDate.setOnClickListener {
                openEndTimePicker()
            }

            if (isSortingEnabled.not()) {
                tvSortedBy.visibility = View.GONE
                rgOrder.visibility = View.GONE
                return@with
            }
            when (sorting) {
                Sorting.ASC -> rgOrder.check(R.id.rb_asc)
                Sorting.DESC -> rgOrder.check(R.id.rb_desc)
            }

            rgOrder.setOnCheckedChangeListener { _, id ->
                sorting = when (id) {
                    R.id.rb_asc -> Sorting.ASC
                    R.id.rb_desc -> Sorting.DESC
                    else -> throw Exception("Order must be asc or desc radio buttons")
                }
            }
        }
    }

    fun show(fm: FragmentManager, filter: DateRangeSortingEnt, limit: DateRangeSortingEnt? = null) {
        storeData(filter, limit)
        show(fm, "FilterDialog")
    }

    private fun storeData(
        selectedRange: DateRangeSortingEnt,
        limitRange: DateRangeSortingEnt? = null
    ) {
        val (start, end, sorting) = selectedRange

        if (limitRange != null) {
            this.minTime = limitRange.start
            this.maxTime = limitRange.end
        }

        this.startTimeSelected = start
        this.endTimeSelected = end
        this.sorting = sorting
    }

    private fun createDatePicker(time: Long, onSelect: (Long) -> Unit): DatePickerDialog {
        val calendar = Calendar.getInstance().also {
            it.timeInMillis = time
        }
        return DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onSelect.invoke(calendar.timeInMillis)
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        ).apply {
            this.datePicker.minDate = minTime
            this.datePicker.maxDate = maxTime
        }
    }

    private fun openStartTimePicker() {
        datePickerDialog?.dismiss()
        datePickerDialog = createDatePicker(startTimeSelected) {
            setStartTime(it)
        }
        datePickerDialog?.show()
    }

    private fun openEndTimePicker() {
        datePickerDialog?.dismiss()
        datePickerDialog = createDatePicker(endTimeSelected) {
            setEndTime(it)
        }
        datePickerDialog?.show()
    }

    private fun setStartTime(time: Long) {
        val selectedTime = Calendar.getInstance().apply {
            timeInMillis = time
        }
        //At the start of the day
        selectedTime.setDayAsStart()
        this.startTimeSelected = selectedTime.timeInMillis

        //setEndTime as Start time if less than
        if (endTimeSelected < startTimeSelected) {
            this.endTimeSelected = startTimeSelected
            showSelectedEndTime()
        }
        showSelectedStartTime()
    }

    private fun setEndTime(time: Long) {

        val selectedTime = Calendar.getInstance().apply {
            timeInMillis = time
        }

        selectedTime.setDayAsEnd()
        this.endTimeSelected = selectedTime.timeInMillis

        //end Time should be greater than or equal start time
        //if end is lass than start, set start value from end
        if (endTimeSelected < startTimeSelected) {
            this.startTimeSelected = this.endTimeSelected
            showSelectedStartTime()
        }
        showSelectedEndTime()
    }

    private fun showSelectedEndTime() {
        binding.tvEndDate.text = filterDateFormat.format(this.endTimeSelected)
    }

    private fun showSelectedStartTime() {
        binding.tvStartDate.text = filterDateFormat.format(this.startTimeSelected)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setOnFilterApplyListener(listener: OnFilterApplyListener?) {
        this.filterApplyListener = listener
    }

    fun interface OnFilterApplyListener {
        fun onApply(result: DateRangeSortingEnt)
    }

}