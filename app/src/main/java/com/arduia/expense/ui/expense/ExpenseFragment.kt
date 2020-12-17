package com.arduia.expense.ui.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.ItemTouchHelper
import com.arduia.core.extension.px
import com.arduia.expense.R
import com.arduia.expense.databinding.FragExpenseLogsBinding
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.common.filter.DateRangeSortingEnt
import com.arduia.expense.ui.common.filter.DateRangeSortingFilterDialog
import com.arduia.expense.ui.common.filter.Sorting
import com.arduia.expense.ui.expense.swipe.SwipeItemCallback
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*

@AndroidEntryPoint
class ExpenseFragment : NavBaseFragment() {

    private var _binding: FragExpenseLogsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ExpenseViewModel>()

    private var filterDialog: DateRangeSortingFilterDialog? = null

    private var adapter: ExpenseLogAdapter? = null

    private val itemNumberFormat = DecimalFormat()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragExpenseLogsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        clean()
        super.onDestroyView()
    }

    private fun clean() {
        filterDialog?.setOnFilterApplyListener(null)
        filterDialog = null
        lifecycle.removeObserver(viewModel)
        binding.tbExpense.setOnMenuItemClickListener(null)
        binding.rvExpense.adapter = null
        adapter = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupExpenseLogRecyclerview()
        setupViewModel()
        PageKeyedDataSource.LoadInitialParams<Int>(1, false)
    }

    private fun setupToolbar() {
        binding.tbExpense.setOnMenuItemClickListener listener@{
            when (it.itemId) {
                R.id.filter -> openFilterDialog()
                R.id.delete -> viewModel.deleteSelectedItems()
            }
            return@listener true
        }
    }

    private fun openFilterDialog() {
        //Remove Old Dialog if exit
        with(filterDialog) {
            this?.setOnFilterApplyListener(null)
            this?.dismiss()
        }

        //Create New Dialog
        filterDialog = DateRangeSortingFilterDialog().apply {
            setOnFilterApplyListener { filter ->
                Timber.d("filter applie! $filter")
            }
        }

        filterDialog?.show(
            childFragmentManager,
            DateRangeSortingEnt(Date().time, Date().time, Sorting.DESC)
        )
    }

    private fun setupExpenseLogRecyclerview() {
        adapter = ExpenseLogAdapter(layoutInflater).apply {
            setOnStateChangeListener { holder, _ ->
                viewModel.storeState(holder)
            }
        }
        val rvTouchHelper = ItemTouchHelper(SwipeItemCallback())
        rvTouchHelper.attachToRecyclerView(binding.rvExpense)
        binding.rvExpense.addItemDecoration(
            MarginItemDecoration(
                spaceSide = 0,
                spaceHeight = requireContext().px(0.5f).toInt()
            )
        )
        binding.rvExpense.adapter = adapter
    }

    private fun setupViewModel() {
        lifecycle.addObserver(viewModel)
        viewModel.expenseList.observe(viewLifecycleOwner) {
            adapter?.submitList(it)
        }
        viewModel.onRestoreSwipeState.observe(viewLifecycleOwner, EventObserver {
            adapter?.restoreState(it)
            adapter?.notifyDataSetChanged()
        })

        viewModel.expenseLogMode.observe(viewLifecycleOwner) {
            when (it) {
                ExpenseMode.NORMAL -> changeUiDefault()
                ExpenseMode.SELECTION -> changeUiSelection()
                else -> Unit
            }
        }

        viewModel.selectedCount.observe(viewLifecycleOwner) {
            binding.tbExpense.title = "${itemNumberFormat.format(it)} ${
                if (it <= 1) getString(R.string.single_item_suffix) else getString(R.string.multi_item_suffix)
            }"
        }

    }

    private fun changeUiDefault() {
        val appBarElevation = binding.appBar.elevation
        with(binding.tbExpense) {
            menu.findItem(R.id.delete)?.isVisible = false
            menu.findItem(R.id.filter)?.isVisible = true
            title = getString(R.string.expense_logs)
            setNavigationIcon(R.drawable.ic_menu)
            setNavigationOnClickListener(::openNavDrawer)
        }
        binding.appBar.elevation = appBarElevation
    }

    private fun changeUiSelection() {
        val appBarElevation = binding.appBar.elevation
        with(binding.tbExpense) {
            menu.findItem(R.id.delete)?.isVisible = true
            menu.findItem(R.id.filter)?.isVisible = false
            title = ""
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener(::clearSelectedItems)
        }
        binding.appBar.elevation = appBarElevation
    }

    private fun openNavDrawer(v: View) {
        navigationDrawer.openDrawer()
    }

    private fun clearSelectedItems(v: View) {
        viewModel.clearState()
    }


}
