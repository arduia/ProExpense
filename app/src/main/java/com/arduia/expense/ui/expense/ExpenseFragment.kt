package com.arduia.expense.ui.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.core.extension.px
import com.arduia.expense.ui.MainHost
import com.arduia.expense.R
import com.arduia.expense.databinding.FragExpenseLogsBinding
import com.arduia.expense.di.TopDropNavOption
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.common.ExpenseDetailDialog
import com.arduia.expense.ui.tmp.SwipeItemCallback
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseFragment : NavBaseFragment() {

    private lateinit var viewBinding: FragExpenseLogsBinding

    @Inject
    lateinit var expenseListAdapter: ExpenseListAdapter

    private val viewModel by viewModels<ExpenseViewModel>()

    @Inject
    @TopDropNavOption
    lateinit var topDropNavOption: NavOptions

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
        viewBinding = FragExpenseLogsBinding.inflate(layoutInflater, null, false)
        return viewBinding.root
    }


    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    @ExperimentalCoroutinesApi
    private fun setupView() {
        setupExpenseListAdapter()
        setupNavigateBackButton()
        setupRestoreButton()
        showLoading()
    }

    private fun setupViewModel() {
        observeIsSelectedMode()
        observeDetailDataSelectEvent()
        observeDeleteEvent()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        waitAnimationAndObserveExpenseList()
    }


    private fun setupExpenseListAdapter() {
        //Setup Transaction Recycler View
        viewBinding.rvExpense.adapter = expenseListAdapter
        viewBinding.rvExpense.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.rvExpense.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        expenseListAdapter.setOnItemClickListener {
            viewModel.selectItemForDetail(it)
        }
        val itemHelper = ItemTouchHelper(SwipeItemCallback())
        itemHelper.attachToRecyclerView(viewBinding.rvExpense)
    }

    private fun setupRestoreButton(){
//        viewBinding.btnRestoreDeletion.setOnClickListener {
//            viewModel.restoreDeletion()
//        }
    }

    private fun setupNavigateBackButton() {
        viewBinding.tbExpense.setNavigationOnClickListener {
            navigationDrawer?.openDrawer()
        }
    }

    private fun observeDeleteEvent() {
        viewModel.itemDeletedEvent.observe(viewLifecycleOwner, EventObserver {
            val message = when {
                it > 0 -> "$it ${getString(R.string.item_deleted)}"
                else -> "$it ${getString(R.string.multi_item_deleted)}"
            }
            mainHost.showSnackMessage(message)
        })
    }

    private fun showNoDataLogs(){
        viewBinding.tvNoData.visibility = View.VISIBLE
    }

    private fun observeDetailDataSelectEvent() {
        viewModel.detailDataChanged.observe(viewLifecycleOwner, EventObserver { expenseDetail ->
            //Remove Old Dialog, If Exist
            detailDialog?.dismiss()
            //Show selected Data
            detailDialog = ExpenseDetailDialog()
            detailDialog?.setOnEditClickListener { openEntryFragment(expenseDetail.id) }
            detailDialog?.setOnDeleteClickListener(::onItemDeleted)
            detailDialog?.showDetail(parentFragmentManager, expenseDetail)

        })
    }

    private fun onItemDeleted(item: ExpenseDetailsVto){
        viewModel.deleteItemById(item.id)
        detailDialog?.dismiss()
    }

    private fun openEntryFragment(id: Int){
        val action = ExpenseFragmentDirections
            .actionDestExpenseToDestExpenseEntry(id)
        findNavController().navigate(action, topDropNavOption)
    }

    private fun observeIsSelectedMode() {
        viewModel.isDeleteMode.observe(viewLifecycleOwner, Observer {
//            when (it) {
//                true -> viewBinding.btnRestoreDeletion.visibility = View.VISIBLE
//                false -> viewBinding.btnRestoreDeletion.visibility = View.INVISIBLE
//            }
        })
    }

    private fun showLoading(){
//        viewBinding.pbLoading.visibility = View.VISIBLE
    }

    private fun hideLoading(){
//        viewBinding.pbLoading.visibility = View.INVISIBLE
    }

    private fun waitAnimationAndObserveExpenseList(){
        lifecycleScope.launch(Dispatchers.Main) {
            val animationDuration = getAnimationDuration().toLong()
            delay(animationDuration)
            observeExpenseList()
        }
    }

    private fun observeExpenseList(){
        viewModel.getExpenseLiveData().observe(viewLifecycleOwner, Observer {
            expenseListAdapter.submitList(it)
            hideLoading()
            if(it.isEmpty()){
                showNoDataLogs()
            }else{
                hideNoDataLogs()
            }
        })
    }

    private fun hideNoDataLogs(){
        viewBinding.tvNoData.visibility = View.GONE
    }

    private fun getAnimationDuration() =
        resources.getInteger(R.integer.duration_left_animation)


}
