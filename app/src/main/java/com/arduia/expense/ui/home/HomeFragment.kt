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
import com.arduia.expense.ui.MainHost
import com.arduia.expense.R
import com.arduia.expense.databinding.FragHomeBinding
import com.arduia.expense.di.FloatingDecimal
import com.arduia.expense.di.LefSideNavOption
import com.arduia.expense.di.TopDropNavOption
import com.arduia.expense.ui.NavBaseFragment
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

    private lateinit var viewBinding: FragHomeBinding

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

    @Inject
    lateinit var recentAdapter: RecentListAdapter

    private var detailDialog: ExpenseDetailDialog? = null

    private val mainHost by lazy {
        requireActivity() as MainHost
    }

    private val detailDismissListener by lazy {
        return@lazy { mainHost.showAddButton() }
    }

    private lateinit var graphAdapter: ExpenseGraphAdapter

    init {
        Timber.d("LIFE Home Init")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewBinding = FragHomeBinding.inflate(layoutInflater, null, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
        restoreViewState(savedInstanceState)
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

        viewBinding.cvExpenseList.rvRecentLists.adapter = recentAdapter
        viewBinding.cvExpenseList.rvRecentLists.layoutManager =
            LinearLayoutManager(requireContext())
        viewBinding.cvExpenseList.rvRecentLists.addItemDecoration(
            MarginItemDecoration(
                spaceHeight = requireContext().px(4),
            )
        )
        viewBinding.scrollHome.isEnabled = false

        mainHost.setAddButtonClickListener {
            findNavController().navigate(R.id.dest_expense_entry, null, entryNavOption)
        }

        viewBinding.toolbar.setNavigationOnClickListener { navigationDrawer?.openDrawer() }

//        viewBinding.btnMoreExpenses.setOnClickListener {
//            findNavController().navigate(R.id.dest_expense, null, moreRecentNavOption)
//        }
        graphAdapter = ExpenseGraphAdapter()
        viewBinding.cvGraph.expenseGraph.adapter = graphAdapter

        viewBinding.cvGraph.expenseGraph.dayNameProvider = dayNameProvider

        recentAdapter.setItemInsertionListener {
            //Item inserted
//            viewBinding.rvRecent.smoothScrollToPosition(0)
        }

        recentAdapter.setOnItemClickListener {
            viewModel.selectItemForDetail(it)
        }

//        viewBinding.btnMoreExpenses.visibility = View.INVISIBLE

    }

    private fun setupViewModel() {

        viewModel.recentData.observe(viewLifecycleOwner, Observer {
            viewBinding.cvExpenseList.root.visibility =
                if (it.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            recentAdapter.submitList(it)
        })

        viewModel.detailData.observe(viewLifecycleOwner, EventObserver { expenseDetail ->
            //Remove Old Dialog if double clicked
            detailDialog?.dismiss()
            //Show Selected Dialog
            detailDialog = ExpenseDetailDialog()
            detailDialog?.setDismissListener(detailDismissListener)
            detailDialog?.setOnDeleteClickListener(::onDeleteExpense)
            detailDialog?.setOnEditClickListener {
                navigateEntryFragment(expenseDetail.id)
            }
            detailDialog?.showDetail(parentFragmentManager, expenseDetail)
            mainHost.hideAddButton()
        })

        viewModel.totalCost.observe(viewLifecycleOwner, Observer {
//            viewBinding.tvTotalValue.text = totalCostFormat.format(it)
        })

        viewModel.costRate.observe(viewLifecycleOwner) {
            graphAdapter.expenseMap = it
        }

        viewModel.onExpenseItemDeleted.observe(viewLifecycleOwner, EventObserver {
            mainHost.showSnackMessage(getString(R.string.item_deleted))
        })

        viewModel.currencySymbol.observe(viewLifecycleOwner) {
//            viewBinding.tvCurrency.text = it
        }

        viewModel.onError.observe(viewLifecycleOwner, EventObserver {
            mainHost.showSnackMessage("Error")
        })
    }

    private fun onDeleteExpense(item: ExpenseDetailsVto) {
        detailDialog?.dismiss()
        viewModel.deleteExpense(item.id)
    }

    private fun navigateEntryFragment(id: Int) {
        val action = HomeFragmentDirections
            .actionDestHomeToDestExpenseEntry(expenseId = id)
        findNavController().navigate(action, entryNavOption)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SCROLL_POSITION, viewBinding.scrollHome.scrollY)
    }

    private fun restoreViewState(instanceState: Bundle?) {
        val scrollPosition = instanceState?.getInt(KEY_SCROLL_POSITION) ?: return
        restoreScrollPosition(scrollPosition)
    }

    private fun restoreScrollPosition(positionY: Int) {
        val height = viewBinding.scrollHome.height
        if (height <= positionY) {
            viewBinding.scrollHome.scrollY = positionY
        }
    }

    companion object {
        private const val TAG = "MY_HomeFragment"
        private const val KEY_SCROLL_POSITION = "scroll_position"
    }

}
