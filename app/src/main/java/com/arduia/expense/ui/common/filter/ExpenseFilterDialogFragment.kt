package com.arduia.expense.ui.common.filter

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.arduia.expense.R
import com.arduia.expense.databinding.FilterExpenseBinding
import com.arduia.expense.domain.filter.DateRange
import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.mvvm.EventObserver
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ExpenseFilterDialogFragment(private val isSortingEnabled: Boolean = true) :
    BottomSheetDialogFragment() {

    private var _binding: FilterExpenseBinding? = null
    private val binding get() = _binding!!

    private var datePickerDialog: DatePickerDialog? = null
    private var timePicker: TimePickerDialog? = null

    private val viewModel by viewModels<ExpenseFilterViewModel>()

    private var filterApplyListener: OnFilterApplyListener? = null

    private lateinit var filterInfo: ExpenseLogFilterInfo


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
            btnApplyFilter.setOnClickListener {
                viewModel.applyFilter()
            }

            cvStartDate.setOnClickListener {
                viewModel.onStartDateSelect()
            }

            cvEndDate.setOnClickListener {
                viewModel.onEndDateSelect()
            }

            if (isSortingEnabled.not()) {
                tvSortedBy.visibility = View.GONE
                tgDateOrder.visibility = View.GONE
                return@with
            }

            tgDateOrder.addOnButtonCheckedListener { _, checkedId, isChecked ->

                if (isChecked.not()) return@addOnButtonCheckedListener

                val sorting = when (checkedId) {
                    R.id.btn_date_order_asc -> Sorting.ASC
                    R.id.btn_date_order_desc -> Sorting.DESC
                    else -> throw Exception("Order must be asc or desc radio buttons")
                }
                viewModel.setSorting(sorting)
            }
        }
        setupViewModel()
    }

    private fun setupViewModel() {


        viewModel.onStartDateFilterShow.observe(viewLifecycleOwner, EventObserver {
            showDatePicker(
                selectedTime = it.dateRangeSelected.start,
                dateRageLimit = it.dateRangeLimit,
                onSelect = viewModel::setStartDate
            )
        })

        viewModel.onEndDateFilterShow.observe(viewLifecycleOwner, EventObserver {
            showDatePicker(
                selectedTime = it.dateRangeSelected.end,
                dateRageLimit = it.dateRangeLimit,
                onSelect = viewModel::setEndDate
            )
        })

        viewModel.startDate.observe(viewLifecycleOwner) {
            val dateFormat = SimpleDateFormat("MMM", Locale.ENGLISH)
            with(binding) {
                tvStartMonth.text = dateFormat.format(it)
                dateFormat.applyPattern("d")
                tvStartDay.text = dateFormat.format(it)
                dateFormat.applyPattern("yyyy")
                tvStartYear.text = dateFormat.format(it)
            }
        }

        viewModel.endDate.observe(viewLifecycleOwner) {
            val dateFormat = SimpleDateFormat("MMM", Locale.ENGLISH)
            with(binding) {
                tvEndMonth.text = dateFormat.format(it)
                dateFormat.applyPattern("d")
                tvEndDay.text = dateFormat.format(it)
                dateFormat.applyPattern("yyyy")
                tvEndYear.text = dateFormat.format(it)
            }
        }

        viewModel.onResult.observe(viewLifecycleOwner, EventObserver {
            filterApplyListener?.onApply(it)
            dismiss()
        })

        viewModel.sortingType.observe(viewLifecycleOwner) { sorting ->
            when (sorting) {
                Sorting.DESC -> {
                    val isChecked = binding.tgDateOrder.checkedButtonId == R.id.btn_date_order_desc
                    Timber.d("isChecked desc $isChecked")
                    if (isChecked) return@observe //Remove Recursive Checks
                    else
                        binding.tgDateOrder.check(R.id.btn_date_order_desc)

                }
                Sorting.ASC -> {
                    val isChecked = binding.tgDateOrder.checkedButtonId == R.id.btn_date_order_asc
                    Timber.d("isChecked asc $isChecked")
                    if (isChecked) return@observe //Remove Recursive Checks
                    else
                        binding.tgDateOrder.check(R.id.btn_date_order_asc)
                }
                else -> Unit
            }
        }

        viewModel.onEndChangedAsStart.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), "End Date Changed to Start Date!", Toast.LENGTH_SHORT)
                .show()
        })

        viewModel.onStartChangedAsEnd.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), "Start Date Changed to End Date!", Toast.LENGTH_SHORT)
                .show()
        })
        viewModel.setFilterInfo(filterInfo)
    }

    private fun showDatePicker(
        selectedTime: Long,
        dateRageLimit: DateRange,
        onSelect: (Int, Int, Int) -> Unit
    ) {
        val initialTime = Calendar.getInstance()
            .apply {
                timeInMillis = selectedTime
            }
        datePickerDialog?.dismiss()
        datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day -> onSelect.invoke(year, month, day) },
            initialTime[Calendar.YEAR],
            initialTime[Calendar.MONTH], initialTime[Calendar.DAY_OF_MONTH]
        ).apply {
            datePicker.minDate = dateRageLimit.start
            datePicker.maxDate = dateRageLimit.end
        }
        datePickerDialog?.show()
    }

    fun show(fm: FragmentManager, info: ExpenseLogFilterInfo) {
        this.filterInfo = info
        show(fm, "FilterDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setOnFilterApplyListener(listener: OnFilterApplyListener?) {
        this.filterApplyListener = listener
    }

    fun interface OnFilterApplyListener {
        fun onApply(result: ExpenseLogFilterInfo)
    }

}