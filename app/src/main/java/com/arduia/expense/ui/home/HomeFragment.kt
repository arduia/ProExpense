package com.arduia.expense.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.core.extension.px
import com.arduia.core.view.asGone
import com.arduia.core.view.asVisible
import com.arduia.expense.ui.MainHost
import com.arduia.expense.R
import com.arduia.expense.databinding.FragHomeBinding
import com.arduia.expense.di.FloatingDecimal
import com.arduia.expense.di.LefSideNavOption
import com.arduia.expense.di.TopDropNavOption
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.DeleteConfirmFragment
import com.arduia.expense.ui.common.DeleteInfoVo
import com.arduia.expense.ui.common.ExpenseDetailDialog
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.graph.DayNameProvider
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : NavBaseFragment() {

    private var _binding: FragHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    @Inject
    @TopDropNavOption
    lateinit var entryNavOption: NavOptions

    @Inject
    @LefSideNavOption
    lateinit var moreRecentNavOption: NavOptions

    @Inject
    @FloatingDecimal
    lateinit var totalCostFormat: DecimalFormat

    @Inject
    lateinit var dayNameProvider: DayNameProvider

    private var recentAdapter: RecentListAdapter? = null

    private var detailDialog: ExpenseDetailDialog? = null

    private val mainHost by lazy { requireActivity() as MainHost }

    private val detailDismissListener by lazy {
        return@lazy { mainHost.showAddButton() }
    }

    private var graphAdapter: ExpenseGraphAdapter? = null

    private var deleteConfirmDialog: DeleteConfirmFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragHomeBinding.inflate(layoutInflater, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        graphAdapter = null
        recentAdapter = null
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        mainHost.showAddButton()
    }

    override fun onPause() {
        super.onPause()
        mainHost.hideAddButton()
    }

    //    Setup View
    private fun setupView() {
        setupRecentUi()
        setupCommonUi()
        setupGraphUi()
    }

    private fun setupViewModel() {
        setupIncomeOutcomeViewModel()
        setupRecentViewModel()
        setupCommonViewModel()
        setupGraphViewModel()
    }

    private fun setupCommonUi() {
        mainHost.setAddButtonClickListener {
            findNavController().navigate(R.id.dest_expense_entry, null, entryNavOption)
        }
        binding.toolbar.setNavigationOnClickListener { navigationDrawer.openDrawer() }
    }

    private fun setupGraphUi() {
        graphAdapter = ExpenseGraphAdapter()

        binding.cvGraph.expenseGraph.adapter = graphAdapter

        binding.cvGraph.expenseGraph.dayNameProvider = dayNameProvider

    }

    private fun setupRecentUi() {
        recentAdapter = RecentListAdapter(layoutInflater)
        binding.cvExpenseList.rvRecentLists.adapter = recentAdapter
        binding.cvExpenseList.rvRecentLists.layoutManager =
            LinearLayoutManager(requireContext())

        binding.cvExpenseList.rvRecentLists.addItemDecoration(
            MarginItemDecoration(
                spaceHeight = requireContext().px(4),
            )
        )
        recentAdapter?.setItemInsertionListener {
            binding.cvExpenseList.rvRecentLists.smoothScrollToPosition(0)
        }

        recentAdapter?.setOnItemClickListener {
            viewModel.selectItemForDetail(it)
        }

        binding.cvExpenseList.rvRecentLists.isNestedScrollingEnabled = false
        binding.cvExpenseList.rvRecentLists.hasFixedSize()
        binding.cvExpenseList.btnMoreLogs.setOnClickListener {
            navigateToExpenseLogs()
        }
    }

    private fun setupIncomeOutcomeViewModel() {
        viewModel.weekIncome.observe(viewLifecycleOwner) {
            binding.cvExpenseInOut.tvIncomeValue.text = it
        }

        viewModel.weekOutcome.observe(viewLifecycleOwner) {
            binding.cvExpenseInOut.tvOutcomeValue.text = it
        }

    }


    private fun setupRecentViewModel() {
        viewModel.recentData.observe(viewLifecycleOwner, Observer {
//            viewBinding.cvExpenseList.root.visibility =
//                if (it.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE
            recentAdapter?.submitList(it)
        })
        viewModel.isEmptyRecent.observe(viewLifecycleOwner) { isEmptyRecent ->
            if (isEmptyRecent) {
                binding.cvExpenseList.root.asGone()
            } else {
                binding.cvExpenseList.root.asVisible()
            }
        }

    }

    private fun setupCommonViewModel() {
        viewModel.currencySymbol.observe(viewLifecycleOwner) {
            binding.cvExpenseInOut.tvIncomeSymobol.text = it
            binding.cvExpenseInOut.tvOutcomeSymbol.text = it
        }

        viewModel.onError.observe(viewLifecycleOwner, EventObserver {
            mainHost.showSnackMessage("Error")
        })

        viewModel.currentWeekDateRange.observe(viewLifecycleOwner) {
            binding.cvExpenseInOut.tvDateRange.text = it
            binding.cvGraph.tvDateRange.text = it
        }
        viewModel.detailData.observe(viewLifecycleOwner, EventObserver { expenseDetail ->
            //Remove Old Dialog if double clicked
            detailDialog?.dismiss()
            //Show Selected Dialog
            detailDialog = ExpenseDetailDialog()
            detailDialog?.setDismissListener(detailDismissListener)
            detailDialog?.setOnDeleteClickListener {
                viewModel.onDeletePrepared(it.id)
            }
            detailDialog?.setOnEditClickListener {
                navigateEntryFragment(expenseDetail.id)
            }
            detailDialog?.showDetail(parentFragmentManager, expenseDetail, isDeleteEnabled = true)
            mainHost.hideAddButton()
        })

        viewModel.onExpenseItemDeleted.observe(viewLifecycleOwner, EventObserver {
            mainHost.showSnackMessage(getString(R.string.item_deleted))
        })

        viewModel.onDeleteConfirm.observe(viewLifecycleOwner, EventObserver {
            detailDialog?.dismiss()
            showDeleteConfirmDialog(info = it)
        })

    }

    private fun setupGraphViewModel() {
        viewModel.costRate.observe(viewLifecycleOwner) {
            graphAdapter?.expenseMap = it
        }
    }


    private fun showDeleteConfirmDialog(info: DeleteInfoVo) {
        deleteConfirmDialog?.dismiss()
        deleteConfirmDialog = DeleteConfirmFragment()
        deleteConfirmDialog?.setOnConfirmListener {
            viewModel.onDeleteConfirmed()
        }
        deleteConfirmDialog?.show(childFragmentManager, info)

    }

    private fun navigateToExpenseLogs() {
        findNavController().navigate(R.id.dest_expense_logs)
    }

    private fun navigateEntryFragment(id: Int) {
        val action = HomeFragmentDirections
            .actionDestHomeToDestExpenseEntry(expenseId = id)
        findNavController().navigate(action, entryNavOption)
    }

    companion object {
        private const val TAG = "MY_HomeFragment"
        private const val KEY_SCROLL_POSITION = "scroll_position"
    }

}
