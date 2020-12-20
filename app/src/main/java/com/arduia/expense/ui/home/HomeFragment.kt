package com.arduia.expense.ui.home

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.core.extension.px
import com.arduia.core.view.asGone
import com.arduia.core.view.asInvisible
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
import com.arduia.expense.ui.vto.ExpenseDetailsVto
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

    init {
        Timber.d("LIFE Home Init")
    }

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
        restoreViewState(savedInstanceState)
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
        recentAdapter = RecentListAdapter(layoutInflater)
        binding.cvExpenseList.rvRecentLists.adapter = recentAdapter
        binding.cvExpenseList.rvRecentLists.layoutManager =
            LinearLayoutManager(requireContext())

        binding.cvExpenseList.rvRecentLists.addItemDecoration(
            MarginItemDecoration(
                spaceHeight = requireContext().px(4),
            )
        )

        binding.scrollHome.isEnabled = false
        binding.cvExpenseList.rvRecentLists.isNestedScrollingEnabled = false
        binding.cvExpenseList.rvRecentLists.hasFixedSize()


        mainHost.setAddButtonClickListener {
            findNavController().navigate(R.id.dest_expense_entry, null, entryNavOption)
        }

        binding.toolbar.setNavigationOnClickListener { navigationDrawer.openDrawer() }

        binding.cvExpenseList.btnMoreLogs.setOnClickListener {
            navigateToExpenseLogs()
        }

        graphAdapter = ExpenseGraphAdapter()

        binding.cvGraph.expenseGraph.adapter = graphAdapter

        binding.cvGraph.expenseGraph.dayNameProvider = dayNameProvider

        recentAdapter?.setItemInsertionListener {
            binding.cvExpenseList.rvRecentLists.smoothScrollToPosition(0)
        }

        recentAdapter?.setOnItemClickListener {
            viewModel.selectItemForDetail(it)
        }
    }

    private fun setupViewModel() {

        viewModel.recentData.observe(viewLifecycleOwner, Observer {
//            viewBinding.cvExpenseList.root.visibility =
//                if (it.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE
            recentAdapter?.submitList(it)
        })

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
            detailDialog?.showDetail(parentFragmentManager, expenseDetail, isDeleteEnabled = false)
            mainHost.hideAddButton()
        })

        viewModel.costRate.observe(viewLifecycleOwner) {
            graphAdapter?.expenseMap = it
        }

        viewModel.onExpenseItemDeleted.observe(viewLifecycleOwner, EventObserver {
            mainHost.showSnackMessage(getString(R.string.item_deleted))
        })

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

        viewModel.weekIncome.observe(viewLifecycleOwner) {
            binding.cvExpenseInOut.tvIncomeValue.text = it
        }

        viewModel.weekOutcome.observe(viewLifecycleOwner) {
            binding.cvExpenseInOut.tvOutcomeValue.text = it
            }

            viewModel.onDeleteConfirm.observe(viewLifecycleOwner, EventObserver {
                detailDialog?.dismiss()
                showDeleteConfirmDialog(info = it)
            })

            viewModel.isEmptyRecent.observe(viewLifecycleOwner) { isEmptyRecent ->
                if (isEmptyRecent) {
                    binding.cvExpenseList.root.asGone()
                } else {
                binding.cvExpenseList.root.asVisible()
            }
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

    private fun restoreViewState(instanceState: Bundle?) {
        val scrollPosition = instanceState?.getInt(KEY_SCROLL_POSITION) ?: return
        restoreScrollPosition(scrollPosition)
    }

    private fun restoreScrollPosition(positionY: Int) {
        val height = binding.scrollHome.height
        if (height <= positionY) {
            binding.scrollHome.scrollY = positionY
        }
    }

    companion object {
        private const val TAG = "MY_HomeFragment"
        private const val KEY_SCROLL_POSITION = "scroll_position"
    }

}
