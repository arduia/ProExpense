package com.arduia.expense.ui.statistics

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.arduia.expense.R
import com.arduia.expense.databinding.FragStatisticBinding
import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.helper.MarginItemDecoration
import com.arduia.expense.ui.common.filter.ExpenseFilterDialogFragment
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : NavBaseFragment() {

    private var _binding: FragStatisticBinding? = null
    private val binding get() = _binding!!

    private var categoryAdapter: CategoryStatisticListAdapter? = null

    private val viewModel by viewModels<StatisticsViewModel>()
    private var filterDialog: ExpenseFilterDialogFragment? = null

    private val dateRangeListener by lazy { Observer<String>(binding.tbStatistic::setSubtitle) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragStatisticBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        categoryAdapter = CategoryStatisticListAdapter(layoutInflater)
        binding.tbStatistic.setNavigationOnClickListener {
            navigationDrawer.openDrawer()
        }
        binding.rvCategoryStatistics.adapter = categoryAdapter
        binding.rvCategoryStatistics.itemAnimator = null
        binding.rvCategoryStatistics.addItemDecoration(
            MarginItemDecoration(
                spaceHeight = resources.getDimension(R.dimen.grid_3).toInt()
            )
        )
        setupCalendarMenu()
    }

    private fun setupCalendarMenu() {
        with(binding.tbStatistic.menu.add(0, 1, 0, "Calendar")) {
            setIcon(R.drawable.ic_filter)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener listener@{
                viewModel.onFilterSelected()
                return@listener true
            }
        }
    }

    private fun shoeDateRangeDialog(filterInfo: ExpenseLogFilterInfo) {
        filterDialog?.dismiss()
        filterDialog = ExpenseFilterDialogFragment(isSortingEnabled = false)
        filterDialog?.setOnFilterApplyListener(viewModel::setFilter)
        filterDialog?.show(childFragmentManager, filterInfo)
    }


    private fun setupViewModel() {
        viewModel.categoryStatisticList.observe(viewLifecycleOwner) {
            categoryAdapter?.submitList(it)
            if (it.isEmpty()) showNoData() else hideNoData()
        }

        viewModel.onFilterShow.observe(viewLifecycleOwner, EventObserver {
            shoeDateRangeDialog(it)
        })

        viewModel.isEmptyExpenseData.observe(viewLifecycleOwner) { isEmptyData ->
            if (isEmptyData) {
                disableMenuActions()
                hideFilterDate()
            } else {
                enableMenuActions()
                showFilterDate()
            }
        }
    }

    private fun hideFilterDate() {
        binding.tbStatistic.subtitle = ""
        unregisterDateRangeObserver()
    }

    private fun showFilterDate() {
        binding.tbStatistic.subtitle = ""
        registerDateRangeObserver()
    }

    private fun registerDateRangeObserver() {
        viewModel.dateRange.observe(viewLifecycleOwner, dateRangeListener)
    }

    private fun unregisterDateRangeObserver() {
        viewModel.dateRange.removeObserver(dateRangeListener)
    }

    private fun disableMenuActions() {
        binding.tbStatistic.menu.forEach {
            it.isEnabled = false
        }
    }

    private fun enableMenuActions() {
        binding.tbStatistic.menu.forEach {
            it.isEnabled = true
        }
    }

    private fun showNoData() {
        binding.tvNoData.visibility = View.VISIBLE
    }

    private fun hideNoData() {
        binding.tvNoData.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        filterDialog?.dismiss()
        filterDialog = null
        binding.rvCategoryStatistics.adapter = null
        categoryAdapter = null
        _binding = null

    }

}