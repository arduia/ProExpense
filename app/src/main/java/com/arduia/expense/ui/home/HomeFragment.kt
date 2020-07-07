package com.arduia.expense.ui.home

import android.graphics.Color
import android.os.Bundle
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
import kotlin.random.Random
import kotlin.random.nextInt


class HomeFragment : NavBaseFragment(){

    private val viewBinding by lazy {  createViewBinding() }

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

    private val linearLayoutManager by lazy {
        LinearLayoutManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = viewBinding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(viewModel)
        setupView()
        setupViewModel()
    }

    //Setup View
    private fun setupView(){

        viewBinding.fbAdd.setColorFilter(Color.WHITE)

        viewBinding.fbAdd.setOnClickListener {
            findNavController().navigate(R.id.dest_expense_entry, null, entryNavOption )
        }

        viewBinding.btnMenuOpen.setOnClickListener { openDrawer() }

        viewBinding.btnMoreTransaction.setOnClickListener {
            findNavController().navigate(R.id.dest_expense, null, moreRecentNavOption)
        }

        viewBinding.imgGraph.spendPoints = getSamplePoints()

        recentAdapter.setItemInsertionListener {
            //Item inserted
            viewBinding.rvRecent.smoothScrollToPosition(0)
        }

        recentAdapter.setOnItemClickListener {
            viewModel.selectItemForDetail(it)
        }
    }

    private fun setupViewModel(){
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
    }

    private fun getSamplePoints() =
        mutableListOf<SpendPoint>().apply {
        add(SpendPoint(1, randomRate()))
        add(SpendPoint(2, randomRate()))
        add(SpendPoint(3, randomRate()))
        add(SpendPoint(4, randomRate()))
//        add(SpendPoint(5, randomRate()))
//        add(SpendPoint(6, randomRate()))
//        add(SpendPoint(7, randomRate()))
    }

    private fun randomRate() = (Random.nextInt(0..100).toFloat() / 100)

    private fun createViewBinding() =
        FragHomeBinding.inflate(layoutInflater).apply {
            //Once Configuration
        rvRecent.adapter = recentAdapter
        rvRecent.layoutManager = linearLayoutManager
        rvRecent.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.space_between_items).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            ))
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

    companion object{
        private const val TAG = "MY_HomeFragment"
    }

}
