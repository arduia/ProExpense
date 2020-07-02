package com.arduia.myacc.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.myacc.ui.NavigationDrawer
import com.arduia.myacc.R
import com.arduia.myacc.databinding.FragTransactionBinding
import com.arduia.myacc.ui.common.MarginItemDecoration
import com.arduia.myacc.ui.common.TransactionDetailDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TransactionFragment : Fragment(){

    private val viewBinding by lazy { createViewBinding()}

    private val transactionAdapter by lazy {
        TransactionListAdapter(
            MainScope(),
            requireContext()
        )
    }


    private val viewModel by viewModels<TransactionViewModel>()

    private val itemSwipeCallback  by lazy { ItemSwipeCallback() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = viewBinding.root


    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.addObserver(viewModel)
        lockNavigation()
        setupView()
        setupViewModel()
    }

    @ExperimentalCoroutinesApi
    private fun setupView(){

        //Setup Transaction Recycler View
        viewBinding.rvTransactions.adapter = transactionAdapter
        viewBinding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())

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

    private fun setupViewModel(){

        viewModel.transactions.observe(viewLifecycleOwner, Observer {
            MainScope().launch(Dispatchers.IO){
                transactionAdapter.submitData(it)
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            when(it){
                true -> viewBinding.pbLoading.visibility = View.VISIBLE
                false -> viewBinding.pbLoading.visibility = View.GONE
            }
        })

        viewModel.notifyMessage.observe(viewLifecycleOwner, Observer {
            Snackbar.make(viewBinding.root, it, Snackbar.LENGTH_SHORT).show()
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
            transactionAdapter.refresh()
        })

        viewModel.detailDataChanged.observe(viewLifecycleOwner, Observer {
            TransactionDetailDialog().apply {
                //
            }.showDetail(parentFragmentManager, it)
        })


    }

    private fun createViewBinding():FragTransactionBinding{
        val binding = FragTransactionBinding.inflate(layoutInflater)
        //One Time Operation
        binding.rvTransactions.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.spacing_list_item).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            ))
        return binding

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

}
