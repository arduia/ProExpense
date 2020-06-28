package com.arduia.myacc.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.myacc.ui.NavigationDrawer
import com.arduia.myacc.R
import com.arduia.myacc.databinding.FragTransactionBinding
import com.arduia.myacc.ui.adapter.RecentListAdapter
import com.arduia.myacc.ui.adapter.MarginItemDecoration
import com.arduia.myacc.ui.adapter.TransactionListAdapter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TransactionFragment : Fragment(){

    private val binding by lazy { FragTransactionBinding.inflate(layoutInflater) }

    private val transactionAdapter by lazy { TransactionListAdapter(layoutInflater) }

    private val viewModel by viewModels<TransactionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = binding.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lifecycle.addObserver(viewModel)
        lockNavigation()
        setupView()
        setupViewModel()
    }

    private fun setupView(){

        //Setup Transaction Recycler View
        binding.rvTransactions.adapter = transactionAdapter
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.addItemDecoration(MarginItemDecoration(
            resources.getDimension(R.dimen.spacing_list_item).toInt(),
            resources.getDimension(R.dimen.margin_list_item).toInt()
        ))

        //Close the page
        binding.btnPopBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setupViewModel(){

        viewModel.transactions.observe(viewLifecycleOwner, Observer {
            MainScope().launch {
                transactionAdapter.submitData(it)
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            when(it){
                true -> binding.pbLoading.visibility = View.VISIBLE
                false -> binding.pbLoading.visibility = View.GONE
            }
        })

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
