package com.arduia.myacc.ui.transaction

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
import com.arduia.myacc.ui.NavigationDrawer
import com.arduia.myacc.R
import com.arduia.myacc.databinding.FragExpenseBinding
import com.arduia.myacc.ui.common.EventObserver
import com.arduia.myacc.ui.common.MarginItemDecoration
import com.arduia.myacc.ui.common.TransactionDetailDialog
import com.arduia.myacc.ui.common.event
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class TransactionFragment : Fragment(){

    lateinit var  viewBinding: FragExpenseBinding

    private val transactionAdapter by lazy {
        TransactionListAdapter( requireContext() )
    }

    private val viewModel by viewModels<TransactionViewModel>()

    private val itemSwipeCallback  by lazy { ItemSwipeCallback() }


    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        viewBinding = FragExpenseBinding.inflate(layoutInflater)
        lockNavigation()
        setupView()
        setupViewModel()
        return viewBinding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    @ExperimentalCoroutinesApi
    private fun setupView(){

        //Setup Transaction Recycler View
        viewBinding.rvTransactions.adapter = transactionAdapter
        viewBinding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.rvTransactions.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.spacing_list_item).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            ))

        ItemTouchHelper(itemSwipeCallback).attachToRecyclerView(viewBinding.rvTransactions)

        itemSwipeCallback.setSwipeListener {
            val item = transactionAdapter.getItemFromPosition(it)
            viewModel.onItemSelect(item)
        }

        transactionAdapter.setItemClickListener {
            viewModel.showDetailData(it)
        }

        //Close the page
        viewBinding.btnPopBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewBinding.btnCancelDelete.setOnClickListener {
            viewModel.cancelDelete()
        }

        viewBinding.btnDoneDelete.setOnClickListener {
            viewModel.deleteConfirm()
        }
    }

    @ExperimentalCoroutinesApi
    private fun setupViewModel(){

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            when(it){
                true -> viewBinding.pbLoading.visibility = View.VISIBLE
                false -> viewBinding.pbLoading.visibility = View.GONE
            }
        })

        //Expense item Selected, show Confirm
        viewModel.isSelectedMode.observe(viewLifecycleOwner, Observer {

            when(it){
                //Show Confirm dialog after an item is selected
                true -> viewBinding.flConfirmDelete.visibility = View.VISIBLE

                //Hide when none item is selected
                false ->{
                    viewBinding.flConfirmDelete.visibility = View.GONE
                }
            }
        })

        viewModel.itemSelectionChangeEvent.observe(viewLifecycleOwner, Observer {
            transactionAdapter.notifyDataSetChanged()
        })

        viewModel.detailDataChanged.observe(viewLifecycleOwner, EventObserver {
            TransactionDetailDialog().apply {
                setEditClickListener {expense ->
                    val action = TransactionFragmentDirections
                        .actionDestTransactionToDestExpenseEntry(expenseId = expense.id)
                    findNavController().navigate(action,createEntryNavOptions())
                }
            }.showDetail(parentFragmentManager,it)
        })

        viewModel.deleteEvent.observe( viewLifecycleOwner, EventObserver{
            Snackbar.make(viewBinding.root, when{
                it > 0 ->  "$it Items Deleted!"
                else -> "$it Item Deleted!"
            }, 500).show()
        })
    }

    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        MainScope().launch(Dispatchers.Main) {
            val animationDuration = resources.getInteger(R.integer.expense_anim_left_duration)
            delay(animationDuration.toLong())
            viewModel.getExpenseLiveData().observe( viewLifecycleOwner, Observer {
                transactionAdapter.submitList(it)
            })
        }
    }

    private fun lockNavigation(){
        //Lock Navigation Drawer Left
        (requireActivity() as? NavigationDrawer)?.lockDrawer(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        //Unlock the Navigation Drawer Left
        (requireActivity() as? NavigationDrawer)?.lockDrawer(false)
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
}
