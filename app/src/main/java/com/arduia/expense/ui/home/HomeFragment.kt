package com.arduia.expense.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.expense.ui.MainHost
import com.arduia.expense.R
import com.arduia.expense.databinding.FragHomeBinding
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.EventObserver
import com.arduia.expense.ui.common.ExpenseDetailDialog
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import java.text.DecimalFormat


class HomeFragment : NavBaseFragment() {

    private lateinit var viewBinding: FragHomeBinding

    private val viewModel by viewModels<HomeViewModel>()

    private val entryNavOption by lazy {
        createEntryNavOptions()
    }

    private val moreRecentNavOption by lazy {
        createRecentMoreNavOptions()
    }

    private val recentAdapter by lazy {
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

    private val graphAdapter by lazy {
        ExpenseGraphAdapter()
    }

    private val totalCostFormat = DecimalFormat("#,##0.0")

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

        viewBinding.imgGraph.adapter = graphAdapter

        recentAdapter.setItemInsertionListener {
            //Item inserted
            viewBinding.rvRecent.smoothScrollToPosition(0)
        }

        recentAdapter.setOnItemClickListener {
            viewModel.selectItemForDetail(it)
        }


    }

    private fun setupViewModel() {

        viewModel.recentData.observe(viewLifecycleOwner, Observer {
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
