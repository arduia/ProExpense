package com.arduia.expense.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
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

    private var detailDialog: ExpenseDetailDialog? = null

    private val mainHost by lazy { requireActivity() as MainHost }

    private val detailDismissListener by lazy {
        return@lazy { mainHost.showAddButton() }
    }

    private var deleteConfirmDialog: DeleteConfirmFragment? = null
    private val homeEpoxyController: HomeEpoxyController by lazy {
        HomeEpoxyController(
            onRecentItemClick = {
                viewModel.selectItemForDetail(it)
            },
            onMoreItemClick = {
                navigateToExpenseLogs()
            }
        )
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainHost.setAddButtonClickListener(null)
        binding.rvHome.adapter = null

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

    private fun setupView() {
        setupCommonUi()
        binding.rvHome.adapter = homeEpoxyController.adapter
        binding.rvHome.addItemDecoration(
            MarginItemDecoration(
                spaceHeight = requireContext().resources.getDimension(R.dimen.grid_2).toInt(),
                spaceSide = requireContext().resources.getDimension(R.dimen.grid_2).toInt()
            )
        )
        homeEpoxyController.requestModelBuild()
    }

    private fun setupViewModel() {
        setupRecentViewModel()
        setupCommonViewModel()
    }

    private fun setupCommonUi() {
        mainHost.setAddButtonClickListener {
            findNavController().navigate(R.id.dest_expense_entry, null, entryNavOption)
        }
        binding.toolbar.setNavigationOnClickListener { navigationDrawer.openDrawer() }
    }

    private fun setupRecentViewModel() {
        viewModel.recentData.observe(viewLifecycleOwner, Observer {
            homeEpoxyController.updateRecent(RecentUiModel(it))
        })
        viewModel.graphUiModel.observe(viewLifecycleOwner, Observer {
            homeEpoxyController.updateGraphRate(it)
        })

        viewModel.incomeOutcomeData.observe(viewLifecycleOwner, Observer {
            homeEpoxyController.updateIncomeOutcome(it)
        })

    }

    private fun setupCommonViewModel() {
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
