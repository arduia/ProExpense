package com.arduia.myacc.ui.transaction

import android.graphics.Color
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.core.view.asGone
import com.arduia.core.view.asInvisible
import com.arduia.core.view.asVisible
import com.arduia.myacc.ui.NavigationDrawer
import com.arduia.myacc.R
import com.arduia.myacc.databinding.FragTransactionBinding
import com.arduia.myacc.ui.common.MarginItemDecoration
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TransactionFragment : Fragment(){

    private val viewBinding by lazy { createViewBinding()}

    private val transactionAdapter by lazy {
        TransactionListAdapter(
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lifecycle.addObserver(viewModel)
        lockNavigation()
        setupView()
        setupViewModel()
    }

    private fun setupView(){

        //Setup Transaction Recycler View
        viewBinding.rvTransactions.adapter = transactionAdapter
        viewBinding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(itemSwipeCallback).attachToRecyclerView(viewBinding.rvTransactions)

        itemSwipeCallback.setSwipeListener {
            val item = transactionAdapter.getItemFromPosition(it)
            viewModel.onItemSelect(item)
            transactionAdapter.refresh()
        }

        transactionAdapter.setItemClickListener {
            d("Transaction Fragment", "$it")
        }

        //Close the page
        viewBinding.btnPopBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewBinding.btnDoneDelete.setOnClickListener {
            viewModel.deleteConfirm()
        }

        viewBinding.btnCancelDelete.setOnClickListener {
            viewModel.cancelDelete()
        }
    }

    private fun setupViewModel(){

        viewModel.transactions.observe(viewLifecycleOwner, Observer {
            MainScope().launch {
                transactionAdapter.submitData(it)
                d("TransactionFragment", "transaction changed $it")
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            when(it){
                true -> viewBinding.pbLoading.visibility = View.VISIBLE
                false -> viewBinding.pbLoading.visibility = View.GONE
            }
        })

        viewModel.notifyMessage.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })

        viewModel.isSelectedMode.observe(viewLifecycleOwner, Observer {
            when(it){
                false -> {
                    viewBinding.btnPopBack.visibility = View.VISIBLE
                    viewBinding.btnCancelDelete.visibility = View.GONE
                    viewBinding.tvConfirmDelete.visibility = View.GONE
                    viewBinding.btnDoneDelete.visibility = View.GONE
                    viewBinding.tvTransactions.visibility = View.VISIBLE
                    transactionAdapter.refresh()
                }

                true -> {
                    viewBinding.btnPopBack.visibility = View.GONE
                    viewBinding.btnCancelDelete.visibility =  View.VISIBLE
                    viewBinding.tvConfirmDelete.visibility = View.VISIBLE
                    viewBinding.btnDoneDelete.visibility = View.VISIBLE
                    viewBinding.tvTransactions.visibility = View.INVISIBLE
                }
            }

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
