package com.arduia.expense.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arduia.expense.ui.MainHost
import com.arduia.expense.feature.home.R
import com.arduia.expense.feature.home.databinding.FragmentHomeBinding
import com.arduia.expense.di.FloatingDecimal
import com.arduia.expense.di.LefSideNavOption
import com.arduia.expense.di.TopDropNavOption
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.delete.DeleteConfirmFragment
import com.arduia.expense.ui.common.uimodel.DeleteInfoUiModel
import com.arduia.expense.ui.common.expense.ExpenseDetailDialog
import com.arduia.expense.ui.common.helper.MarginItemDecoration
import com.arduia.graph.DayNameProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : NavBaseFragment() {

    private var _binding: FragmentHomeBinding? = null // keep as is, layout is still frag_home.xml
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

    private var deleteConfirmDialog: DeleteConfirmFragment? = null

    private var recentListAdapter: RecentListAdapter? = null
    private var expenseGraphAdapter: ExpenseGraphAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateRecentData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        collectState()
        collectEffects()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView() {
        setupCommonUi()
        recentListAdapter = RecentListAdapter(layoutInflater)
        recentListAdapter?.setOnItemClickListener {
            viewModel.selectItemForDetail(it)
        }
        binding.layoutRecentLists.rvRecentLists.adapter = recentListAdapter

        expenseGraphAdapter = ExpenseGraphAdapter()
        binding.layoutExpenseGraph.expenseGraph.adapter = expenseGraphAdapter

        binding.layoutRecentLists.btnMoreLogs.setOnClickListener {
            navigateToExpenseLogs()
        }
    }

    private fun setupCommonUi() {
        binding.toolbar.setNavigationOnClickListener { navigationDrawer.openDrawer() }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    recentListAdapter?.submitList(state.recentExpenses)
                    binding.layoutRecentLists.root.visibility =
                        if (state.recentExpenses.isEmpty()) View.GONE else View.VISIBLE

                    expenseGraphAdapter?.expenseMap = state.weeklyGraphData.mapValues { it.value.toDouble() }
                    binding.layoutExpenseGraph.tvDateRange.text = state.dateRange

                    with(binding.layoutInOut) {
                        tvIncomeValue.text = state.totalIncome
                        tvOutcomeValue.text = state.totalExpense
                        tvOutcomeSymbol.text = state.currencySymbol
                        tvIncomeSymobol.text = state.currencySymbol
                        tvDateRange.text = state.dateRange
                        Timber.d("Home: $state")
                    }
                }
            }
        }
    }

    private fun collectEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffect.collect { effect ->
                    when (effect) {
                        is HomeUiEffect.ShowDetail -> {
                            detailDialog?.dismiss()
                            detailDialog = ExpenseDetailDialog()
                            detailDialog?.setOnDeleteClickListener {
                                viewModel.onDeletePrepared(it.id)
                            }
                            detailDialog?.setOnEditClickListener {
                                navigateEntryFragment(effect.detail.id)
                            }
                            detailDialog?.setDismissListener {
                                showAddButtonIfCurrentIsHomeDestination()
                            }
                            detailDialog?.showDetail(parentFragmentManager, effect.detail, isDeleteEnabled = true)
                            mainHost.hideAddButton()
                        }
                        is HomeUiEffect.ShowItemDeleted -> {
                            mainHost.showSnackMessage(getString(R.string.item_deleted))
                        }
                        is HomeUiEffect.ShowDeleteConfirm -> {
                            detailDialog?.dismiss()
                            showDeleteConfirmDialog(info = effect.info)
                        }
                        is HomeUiEffect.ShowError -> Unit
                    }
                }
            }
        }
    }

    private fun showAddButtonIfCurrentIsHomeDestination() {
        if (findNavController().currentDestination?.id == R.id.dest_home) {
            mainHost.showAddButtonInstantly()
        }
    }

    private fun showDeleteConfirmDialog(info: DeleteInfoUiModel) {
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
        findNavController().navigate(
            R.id.dest_expense_entry,
            android.os.Bundle().apply { putInt("expenseId", id) },
            entryNavOption
        )
    }

    companion object {
        private const val TAG = "MY_HomeFragment"
        private const val KEY_SCROLL_POSITION = "scroll_position"
    }

}
