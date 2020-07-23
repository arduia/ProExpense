package com.arduia.expense.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.ui.MainHost
import com.arduia.expense.R
import com.arduia.expense.data.AccRepositoryImpl
import com.arduia.expense.databinding.FragHomeBinding
import com.arduia.expense.di.FloatingDecimal
import com.arduia.expense.di.LefSideNavOption
import com.arduia.expense.di.ServiceLoader
import com.arduia.expense.di.TopDropNavOption
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.ExpenseCategoryProviderImpl
import com.arduia.expense.ui.common.ExpenseDetailDialog
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.mapping.ExpenseMapper
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

    private val detailEditListener by lazy {
        createDetailEditClickListener()
    }

    private val detailDismissListener by lazy {
        return@lazy { mainHost.showAddButton() }
    }

    private lateinit var graphAdapter: ExpenseGraphAdapter

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

        lifecycle.addObserver(viewModel)
        setupView()
        setupViewModel()
    }

    override fun onResume() {
        super.onResume()
        mainHost.showAddButton()
    }

    override fun onPause() {
        super.onPause()
        mainHost.hideAddButton()
    }

    //Setup View
    private fun setupView() {

        viewBinding.rvRecent.adapter = recentAdapter
        viewBinding.rvRecent.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.rvRecent.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.space_between_items).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            )
        )
        mainHost.setAddButtonClickListener {
            findNavController().navigate(R.id.dest_expense_entry, null, entryNavOption)
        }

        viewBinding.btnMenuOpen.setOnClickListener { openDrawer() }

        viewBinding.btnMoreExpenses.setOnClickListener {
            findNavController().navigate(R.id.dest_expense, null, moreRecentNavOption)
        }
        graphAdapter = ExpenseGraphAdapter()
        viewBinding.imgGraph.adapter = graphAdapter

        viewBinding.imgGraph.dayNameProvider = dayNameProvider

        recentAdapter.setItemInsertionListener {
            //Item inserted
            viewBinding.rvRecent.smoothScrollToPosition(0)
        }

        recentAdapter.setOnItemClickListener {
            viewModel.selectItemForDetail(it)
        }

        viewBinding.btnMoreExpenses.visibility = View.INVISIBLE

    }

    private fun setupViewModel() {

        viewModel.recentData.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                viewBinding.btnMoreExpenses.visibility = View.VISIBLE
            }
            recentAdapter.submitList(it)
        })

        viewModel.detailData.observe(viewLifecycleOwner, EventObserver {
            //Remove Old Dialog if double clicked
            detailDialog?.dismiss()
            //Show Selected Dialog
            detailDialog = ExpenseDetailDialog().apply {
                setEditClickListener(detailEditListener)
                setDismissListener(detailDismissListener)
            }
            detailDialog?.showDetail(parentFragmentManager, it)
            mainHost.hideAddButton()
        })

        viewModel.totalCost.observe(viewLifecycleOwner, Observer {
            viewBinding.tvTotalValue.text = totalCostFormat.format(it)
        })

        viewModel.costRate.observe(viewLifecycleOwner){
            graphAdapter.expenseMap = it
        }
    }

    private fun createDetailEditClickListener() = { expense: ExpenseDetailsVto ->
        val action = HomeFragmentDirections
            .actionDestHomeToDestExpenseEntry(expenseId = expense.id)
        findNavController().navigate(action, entryNavOption)
    }

    companion object {
        private const val TAG = "MY_HomeFragment"
    }

}
