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
import com.arduia.expense.di.ServiceLoader
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.EventObserver
import com.arduia.expense.ui.common.ExpenseCategoryProviderImpl
import com.arduia.expense.ui.common.ExpenseDetailDialog
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.mapping.ExpenseMapper
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import java.text.DecimalFormat


class HomeFragment : NavBaseFragment() {

    private lateinit var viewBinding: FragHomeBinding

    private val viewModel by viewModels<HomeViewModel>{
        val serviceLoader = ServiceLoader.getInstance(requireContext())
        HomeViewModelFactory(
            serviceLoader.getExpenseMapper(),
            serviceLoader.getAccountingRepository(),
            serviceLoader.getExpenseRateCalculator()
        )
    }

    private val entryNavOption by lazy {
        createEntryNavOptions()
    }

    private val moreRecentNavOption by lazy {
        createRecentMoreNavOptions()
    }

    private val recentAdapter: RecentListAdapter by lazy {
        RecentListAdapter(layoutInflater)
    }

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

    private lateinit var totalCostFormat: DecimalFormat

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

        totalCostFormat = DecimalFormat("#,##0.0")
        lifecycle.addObserver(viewModel)
        setupView()
        setupViewModel()
    }

    override fun onResume() {
        super.onResume()
        mainHost.showAddButton()
    }

    override fun onStop() {
        super.onStop()
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
        viewBinding.imgGraph.dayNameProvider = ExpenseDayNameProvider(requireContext())

        recentAdapter.setItemInsertionListener {
            //Item inserted
            viewBinding.rvRecent.smoothScrollToPosition(0)
        }

        recentAdapter.setOnItemClickListener {
            viewModel.selectItemForDetail(it)
        }

        viewBinding.btnMoreExpenses.visibility = View.INVISIBLE

        //Auto Hide when scroll
        viewBinding.rvRecent.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            private var currentScrollPhrase = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentScrollPhrase += dy
                when(currentScrollPhrase> 10){
                    true -> mainHost.hideAddButton()
                    false -> mainHost.showAddButton(showInstantly = true)
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    //Scrolling is over
                    currentScrollPhrase = 0
                }
            }
        })
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
        findNavController().navigate(action, createEntryNavOptions())
    }

    private fun createEntryNavOptions() =
        NavOptions.Builder()
            //For Entry Fragment
            .setEnterAnim(R.anim.pop_down_up)
            .setPopExitAnim(R.anim.pop_up_down)
            //For Home Fragment
            .setExitAnim(android.R.anim.fade_out)
            .setPopEnterAnim(R.anim.nav_default_enter_anim)
            .setLaunchSingleTop(true)
            .build()

    private fun createRecentMoreNavOptions() =
        NavOptions.Builder()
            //For Transaction Fragment
            .setEnterAnim(R.anim.expense_enter_left)
            .setPopExitAnim(R.anim.expense_exit_right)
            //For Home Fragment
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_enter_anim)
            .setLaunchSingleTop(true)
            .build()



    companion object {
        private const val TAG = "MY_HomeFragment"
    }

}
