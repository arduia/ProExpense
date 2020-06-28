package com.arduia.myacc.ui.transaction

import android.graphics.Color
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
import com.arduia.myacc.ui.adapter.MarginItemDecoration
import com.arduia.myacc.ui.adapter.TransactionListAdapter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TransactionFragment : Fragment(){

    private val viewBinding by lazy { FragTransactionBinding.inflate(layoutInflater) }

    private val transactionAdapter by lazy { TransactionListAdapter(layoutInflater) }

    private val viewModel by viewModels<TransactionViewModel>()

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
        viewBinding.rvTransactions.addItemDecoration(MarginItemDecoration(
            resources.getDimension(R.dimen.spacing_list_item).toInt(),
            resources.getDimension(R.dimen.margin_list_item).toInt()
        ))

        //Close the page
        viewBinding.btnPopBack.setOnClickListener {
            findNavController().popBackStack()
        }
        viewBinding.fbDelete.setColorFilter(Color.WHITE)
        viewBinding.btnEdit.setOnClickListener {
            viewBinding.fbDelete.show()
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
                true -> viewBinding.pbLoading.visibility = View.VISIBLE
                false -> viewBinding.pbLoading.visibility = View.GONE
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
