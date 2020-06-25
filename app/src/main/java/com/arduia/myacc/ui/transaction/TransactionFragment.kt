package com.arduia.myacc.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.myacc.ui.NavigationDrawer
import com.arduia.myacc.R
import com.arduia.myacc.databinding.FragTransactionBinding
import com.arduia.myacc.ui.adapter.CostAdapter
import com.arduia.myacc.ui.adapter.MarginItemDecoration
import com.arduia.myacc.ui.mock.costList

class TransactionFragment : Fragment(){

    private val binding by lazy { FragTransactionBinding.inflate(layoutInflater) }

    private val costAdapter by lazy { CostAdapter(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lockNavigation()
        setupView()
    }

    private fun setupView(){

        //Setup Transaction Recycler View
        binding.rvTransactions.adapter = costAdapter
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.addItemDecoration(MarginItemDecoration(
            resources.getDimension(R.dimen.spacing_list_item).toInt(),
            resources.getDimension(R.dimen.margin_list_item).toInt()
        ))

        //Initialize list
        costAdapter.submitList(costList())

        //Close the page
        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
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

}