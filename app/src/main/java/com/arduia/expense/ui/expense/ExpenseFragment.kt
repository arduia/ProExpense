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
        setupRestoreButton()
    }

    private fun setupViewModel() {
        addLifecycleObserver()
        observeIsLoadingEvent()
        observeIsSelectedMode()
        observeDetailDataSelectEvent()
        observeDeleteEvent()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        waitAnimationAndObserveExpenseList()
    }

    override fun onDestroy() {
        super.onDestroy()
        unlockNavDrawer()
    }

    private fun addLifecycleObserver(){
        lifecycle.addObserver(viewModel)
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
            viewModel.deleteItem(item)
        }
        expenseListAdapter.setOnItemClickListener {
            viewModel.selectItemForDetail(it)
        }
    }

    private fun setupRestoreButton(){
        viewBinding.btnRestoreDeletion.setOnClickListener {
            viewModel.restoreDeletion()
        }
    }

    private fun setupNavigateBackButton() {
        viewBinding.btnPopBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun observeDeleteEvent() {
        viewModel.itemDeletedEvent.observe(viewLifecycleOwner, EventObserver {
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
        viewModel.isDeleteMode.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> viewBinding.btnRestoreDeletion.visibility = View.VISIBLE
                false -> viewBinding.btnRestoreDeletion.visibility = View.INVISIBLE
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

    private fun waitAnimationAndObserveExpenseList(){
        MainScope().launch(Dispatchers.Main) {
            val animationDuration = getAnimationDuration().toLong()
            delay(animationDuration)
            observeExpenseList()
        }
    }

    private suspend fun observeExpenseList(){
        viewModel.getExpenseLiveData().observe(viewLifecycleOwner, Observer {
            expenseListAdapter.submitList(it)
        })
    }

    private fun getAnimationDuration() =
        resources.getInteger(R.integer.expense_anim_left_duration)

    private fun initializeViewBinding() {
        viewBinding = FragExpenseBinding.inflate(layoutInflater, null, false)
    }

}
