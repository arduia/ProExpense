package com.arduia.expense.ui.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.expense.ui.MainHost
import com.arduia.expense.ui.NavigationDrawer
import com.arduia.expense.R
import com.arduia.expense.databinding.FragExpenseBinding
import com.arduia.expense.di.TopDropNavOption
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.common.ExpenseDetailDialog
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseFragment : Fragment() {

    private lateinit var viewBinding: FragExpenseBinding

    @Inject
    lateinit var expenseListAdapter: ExpenseListAdapter

    private val viewModel by viewModels<ExpenseViewModel>()

    @Inject
    @TopDropNavOption
    lateinit var topDropNavOption: NavOptions

    private val itemSwipeCallback by lazy { ItemSwipeCallback() }

    private val mainHost by lazy {
        requireActivity() as MainHost
    }

    private var detailDialog: ExpenseDetailDialog? = null

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initializeViewBinding()
        return viewBinding.root
    }


    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lockNavDrawer()
        setupView()
        setupViewModel()
    }

    @ExperimentalCoroutinesApi
    private fun setupView() {
        setupExpenseListAdapter()
        setupNavigateBackButton()
        setupCancelButton()
        setupDeleteButton()
    }

    private fun setupViewModel() {
        observeIsLoadingEvent()
        observeIsSelectedMode()
        observeDetailDataSelectEvent()
        observeDeleteEvent()
        observeSelectedItemChangeEvent()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeExpenseListAfterAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        unlockNavDrawer()
    }

    private fun unlockNavDrawer(){
        (requireActivity() as? NavigationDrawer)?.unlockDrawer()
    }

    private fun lockNavDrawer() {
        (requireActivity() as? NavigationDrawer)?.lockDrawer()
    }

    private fun setupExpenseListAdapter() {
        //Setup Transaction Recycler View
        viewBinding.rvExpense.adapter = expenseListAdapter
        viewBinding.rvExpense.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.rvExpense.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.space_between_items).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            )
        )
        ItemTouchHelper(itemSwipeCallback).attachToRecyclerView(viewBinding.rvExpense)
        itemSwipeCallback.setSwipeListener {
            val item = expenseListAdapter.getItemFromPosition(it)
            viewModel.onItemSelect(item)
        }
        expenseListAdapter.setOnItemClickListener {
            viewModel.selectItemForDetail(it)
        }
    }

    private fun setupDeleteButton(){
        viewBinding.btnDoneDelete.setOnClickListener {
            viewModel.deleteConfirm()
        }
    }

    private fun setupCancelButton(){
        viewBinding.btnCancelDelete.setOnClickListener {
            viewModel.cancelDeletion()
        }
    }

    private fun setupNavigateBackButton() {
        viewBinding.btnPopBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun observeSelectedItemChangeEvent() {
        viewModel.selectedItemChangeEvent.observe(viewLifecycleOwner, EventObserver {
            expenseListAdapter.notifyDataSetChanged()
        })
    }

    private fun observeDeleteEvent() {
        viewModel.deleteEvent.observe(viewLifecycleOwner, EventObserver {
            val message = when {
                it > 0 -> "$it ${getString(R.string.label_single_item_deleted)}"
                else -> "$it ${getString(R.string.label_multi_item_deleted)}"
            }
            mainHost.showSnackMessage(message)
        })
    }

    private fun observeDetailDataSelectEvent() {
        viewModel.detailDataChanged.observe(viewLifecycleOwner, EventObserver {
            //Remove Old Dialog, If Exist
            detailDialog?.dismiss()
            //Show selected Data
            detailDialog = ExpenseDetailDialog().apply {
                setEditClickListener { expense ->
                    val action = ExpenseFragmentDirections
                        .actionDestExpenseToDestExpenseEntry(expenseId = expense.id)
                    findNavController().navigate(action, topDropNavOption)
                }
            }
            detailDialog?.showDetail(parentFragmentManager, it)
        })
    }

    private fun observeIsSelectedMode() {
        viewModel.isSelectedMode.observe(viewLifecycleOwner, Observer {
            when (it) {
                //Show Confirm dialog after an item is selected
                true -> viewBinding.flConfirmDelete.visibility = View.VISIBLE

                //Hide when none item is selected
                false -> {
                    viewBinding.flConfirmDelete.visibility = View.GONE
                }
            }
        })
    }

    private fun observeIsLoadingEvent() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> viewBinding.pbLoading.visibility = View.VISIBLE
                false -> viewBinding.pbLoading.visibility = View.GONE
            }
        })
    }

    private fun observeExpenseListAfterAnimation(){
        MainScope().launch(Dispatchers.Main) {
            val animationDuration = resources.getInteger(R.integer.expense_anim_left_duration)
            delay(animationDuration.toLong())
            viewModel.getExpenseLiveData().observe(viewLifecycleOwner, Observer {
                expenseListAdapter.submitList(it)
            })
        }
    }

    private fun initializeViewBinding() {
        viewBinding = FragExpenseBinding.inflate(layoutInflater, null, false)
    }

}
