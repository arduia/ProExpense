package com.arduia.myacc.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arduia.graph.SpendPoint
import com.arduia.myacc.NavigationDrawer
import com.arduia.myacc.databinding.FragHomeBinding
import com.arduia.myacc.ui.BaseFragment
import com.arduia.myacc.ui.adapter.CostAdapter
import com.arduia.myacc.ui.mock.costList
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextInt


class HomeFragment : BaseFragment(){

    private val viewBinding by lazy { FragHomeBinding.inflate(layoutInflater) }

    private val costAdapter: CostAdapter by lazy { CostAdapter(layoutInflater) }

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

        viewBinding.btnClose.setOnClickListener {  openDrawer()  }

        viewBinding.rvRecent.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        viewBinding.rvRecent.adapter = costAdapter

        costAdapter.listItem = costList()

        viewBinding.imgGraph.spendPoints = getSamplePoints()
    }

    private fun getSamplePoints() = mutableListOf<SpendPoint>().apply {
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
