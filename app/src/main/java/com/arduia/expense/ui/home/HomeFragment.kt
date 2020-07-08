package com.arduia.expense.ui.home

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.expense.MainHost
import com.arduia.graph.SpendPoint
import com.arduia.expense.R
import com.arduia.expense.databinding.FragHomeBinding
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.EventObserver
import com.arduia.expense.ui.common.ExpenseDetailDialog
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.entry.CategoryListAdapter
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import kotlin.random.Random
import kotlin.random.nextInt


class HomeFragment : NavBaseFragment() {

    private val viewBinding by lazy { createViewBinding() }

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


    private val mainHost by lazy {
        requireActivity() as MainHost
    }

    private val detailEditListener by lazy {
        createDetailEditClickListener()
    }

    private val detailDismissListener by lazy {
        return@lazy { mainHost.showAddButton() }
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

    override fun onResume() {
        super.onResume()
        mainHost.showAddButton()
    }

    override fun onStop() {
        super.onStop()
        mainHost.hideAddButton()
    }

    private fun FragHomeBinding.initSetupView() {
        rvRecent.adapter = recentAdapter
        rvRecent.layoutManager = LinearLayoutManager(requireContext())
        rvRecent.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.space_between_items).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            )
        )

    }

    //Setup View
    private fun setupView() {

        mainHost.setAddButtonClickListener {
            findNavController().navigate(R.id.dest_expense_entry, null, entryNavOption)
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

    private fun setupViewModel() {
        viewModel.recentData.observe(viewLifecycleOwner, Observer {
            recentAdapter.submitList(it)
        })

        viewModel.detailData.observe(viewLifecycleOwner, EventObserver {

            ExpenseDetailDialog().apply {
                setEditClickListener(detailEditListener)
                setDismissListener(detailDismissListener)
            }.showDetail(parentFragmentManager, it)
            mainHost.hideAddButton()
        })
    }

    private fun createDetailEditClickListener() = { expense: ExpenseDetailsVto ->
        val action = HomeFragmentDirections
            .actionDestHomeToDestExpenseEntry(expenseId = expense.id)
        findNavController().navigate(action, createEntryNavOptions())
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
        FragHomeBinding.inflate(layoutInflater, null, false).apply {
            //Once Setup
            initSetupView()
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
