package com.arduia.myacc.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arduia.core.performance.printDurationMilli
import com.arduia.core.performance.printDurationNano
import com.arduia.graph.SpendPoint
import com.arduia.myacc.NavigationDrawer
import com.arduia.myacc.R
import com.arduia.myacc.databinding.FragHomeBinding
import com.arduia.myacc.ui.BaseFragment
import com.arduia.myacc.ui.MainActivity
import com.arduia.myacc.ui.adapter.CostAdapter
import com.arduia.myacc.ui.mock.costList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextInt


class HomeFragment : BaseFragment(){

    private val viewBinding by lazy {
        printDurationMilli("HomeFragment", "Home Binding"){
            FragHomeBinding.inflate(layoutInflater).apply {
                rvRecent.adapter = costAdapter
                rvRecent.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
                d("HomeFragment","Home instance is ${this.root.toString()}")
            }
        }
    }

    private val costAdapter by lazy { CostAdapter(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = viewBinding.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
    }

    private fun setupView(){

        viewBinding.fbAdd.setColorFilter(Color.WHITE)

        viewBinding.btnClose.setOnClickListener { openDrawer() }

        viewBinding.btnRecentMore.setOnClickListener {
            findNavController().navigate(R.id.dest_transaction)
        }

    }

    override fun onResume() {
        super.onResume()

        costAdapter.submitList(costList())
        viewBinding.imgGraph.spendPoints = getSamplePoints()
    }

    private fun getSamplePoints() =
        mutableListOf<SpendPoint>().apply {
        add(SpendPoint(1, randomRate()))
        add(SpendPoint(2, randomRate()))
        add(SpendPoint(3, randomRate()))
        add(SpendPoint(4, randomRate()))
        add(SpendPoint(5, randomRate()))
        add(SpendPoint(6, randomRate()))
        add(SpendPoint(7, randomRate()))
    }

    private fun randomRate() = (Random.nextInt(0..100).toFloat() / 100)

}
