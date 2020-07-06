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
import com.arduia.expense.ui.NavigationDrawer
import com.arduia.expense.R
import com.arduia.expense.databinding.FragExpenseBinding
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.EventObserver
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.common.ExpenseDetailDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class ExpenseFragment : NavBaseFragment(){

    @ExperimentalCoroutinesApi
    private val viewBinding by lazy {
        FragExpenseBinding.inflate(layoutInflater).apply {
            setupView()
            setupViewModel()
        }
    }

    private val expenseListAdapter by lazy {
        ExpenseListAdapter( requireContext() )
    }

    private val viewModel by viewModels<ExpenseViewModel>()

    private val itemSwipeCallback  by lazy { ItemSwipeCallback() }

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =  viewBinding.root


    @ExperimentalCoroutinesApi
    private fun FragExpenseBinding.setupView(){

        //Setup Transaction Recycler View
        rvExpenses.adapter = expenseListAdapter
        rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        rvExpenses.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.spacing_list_item).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            ))

        ItemTouchHelper(itemSwipeCallback).attachToRecyclerView(rvExpenses)

        itemSwipeCallback.setSwipeListener {
            val item = expenseListAdapter.getItemFromPosition(it)
            viewModel.onItemSelect(item)
        }

        expenseListAdapter.setItemClickListener {
            viewModel.selectItemForDetail(it)
        }

        //Close the page
        btnPopBack.setOnClickListener {
            findNavController().popBackStack()
        }

         btnCancelDelete.setOnClickListener {
            viewModel.cancelDelete()
        }

         btnDoneDelete.setOnClickListener {
            viewModel.deleteConfirm()
        }

    }

    @ExperimentalCoroutinesApi
    private fun FragExpenseBinding.setupViewModel(){

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            when(it){
                true ->  pbLoading.visibility = View.VISIBLE
                false ->  pbLoading.visibility = View.GONE
            }
        })

        //Expense item Selected, show Confirm
        viewModel.isSelectedMode.observe(viewLifecycleOwner, Observer {

            when(it){
                //Show Confirm dialog after an item is selected
                true ->  flConfirmDelete.visibility = View.VISIBLE

                //Hide when none item is selected
                false ->{
                     flConfirmDelete.visibility = View.GONE
                }
            }
        })

        viewModel.detailDataChanged.observe(viewLifecycleOwner, EventObserver {
            ExpenseDetailDialog().apply {
                setEditClickListener {expense ->
                    val action = ExpenseFragmentDirections
                        .actionDestExpenseToDestExpenseEntry(expenseId = expense.id)
                    findNavController().navigate(action,createEntryNavOptions())
                }
            }.showDetail(parentFragmentManager,it)
        })

        viewModel.deleteEvent.observe( viewLifecycleOwner, EventObserver{
            Snackbar.make(root, when{
                it > 0 ->  "$it Items Deleted!"
                else -> "$it Item Deleted!"
            }, 500).show()
        })
    }

    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.addObserver(viewModel)
        lockNavigation()

        MainScope().launch(Dispatchers.Main) {
            val animationDuration = resources.getInteger(R.integer.expense_anim_left_duration)
            delay(animationDuration.toLong())
            viewModel.getExpenseLiveData().observe(viewLifecycleOwner, Observer {
                expenseListAdapter.submitList(it)
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
