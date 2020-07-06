package com.arduia.expense.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.graph.SpendPoint
import com.arduia.expense.R
import com.arduia.expense.databinding.FragHomeBinding
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.EventObserver
import com.arduia.expense.ui.common.ExpenseDetailDialog
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.expense.ExpenseFragmentDirections
import com.arduia.expense.ui.expense.ExpenseListAdapter
import timber.log.Timber
import kotlin.random.Random
import kotlin.random.nextInt


class HomeFragment : NavBaseFragment(){

    private val viewBinding by lazy {
        FragHomeBinding.inflate(layoutInflater).apply {
            setupView()
            setupViewModel()
        }

    }

    private val viewModel by viewModels<HomeViewModel>()

    private val entryNavOption by lazy {
        createEntryNavOptions()
    }

    private val moreRecentNavOption by lazy {
        createRecentMoreNavOptions()
    }

    private val recentAdapter by lazy {
        RecentListAdapter( layoutInflater )
    }

    private val expenseGraphAdapter by lazy {
        ExpenseGraphAdapter()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{

        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.addObserver(viewModel)
    }

    //Setup View
    private fun FragHomeBinding.setupView(){

        rvRecent.adapter = recentAdapter
        rvRecent.layoutManager = LinearLayoutManager(requireContext())
        rvRecent.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.spacing_list_item).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            ))

        fbAdd.setColorFilter(Color.WHITE)

        fbAdd.setOnClickListener {
            findNavController().navigate(R.id.dest_expense_entry, null, entryNavOption )
        }

        btnMenuOpen.setOnClickListener { openDrawer() }

        btnMoreExpenses.setOnClickListener {
            findNavController().navigate(R.id.dest_expense, null, moreRecentNavOption)
        }

        imgGraph.adapter = expenseGraphAdapter

        recentAdapter.setItemInsertionListener {
            //Item inserted
            rvRecent.smoothScrollToPosition(0)
        }

        recentAdapter.setOnItemClickListener {
            viewModel.selectItemForDetail(it)
        }
    }

    private fun FragHomeBinding.setupViewModel(){
        viewModel.recentData.observe(viewLifecycleOwner, Observer {
            recentAdapter.submitList(it)
        })

        viewModel.detailData.observe(viewLifecycleOwner, EventObserver {
            ExpenseDetailDialog().apply {
                setEditClickListener {expense ->
                    val action = HomeFragmentDirections
                        .actionDestHomeToDestExpenseEntry(expenseId = expense.id)
                    findNavController().navigate(action,createEntryNavOptions())
                }
            }.showDetail(parentFragmentManager,it)
        })

        viewModel.weeklyCost.observe( viewLifecycleOwner, Observer{
            tvTotalValue.text = it
        })

        viewModel.graphPoints.observe( viewLifecycleOwner, Observer {
            expenseGraphAdapter.points = it
        })
    }

    private fun createEntryNavOptions() =
        NavOptions.Builder()
                //For Entry Fragment
            .setEnterAnim(R.anim.pop_down_up)
            .setPopExitAnim(R.anim.pop_up_down)
                //For Home Fragment
            .setExitAnim(android.R.anim.fade_out)
            .setPopEnterAnim(R.anim.nav_default_enter_anim)

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
