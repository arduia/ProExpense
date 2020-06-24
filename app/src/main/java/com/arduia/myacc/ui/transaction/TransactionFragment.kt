package com.arduia.myacc.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.myacc.NavigationDrawer
import com.arduia.myacc.databinding.FragTransactionBinding
import com.arduia.myacc.ui.adapter.CostAdapter
import com.arduia.myacc.ui.mock.costList
import com.arduia.myacc.ui.vto.CostVto

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