package com.arduia.myacc.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arduia.graph.SpendPoint
import com.arduia.myacc.R
import com.arduia.myacc.databinding.FragHomeBinding
import com.arduia.myacc.ui.BaseFragment
import com.arduia.myacc.ui.adapter.CostAdapter
import com.arduia.myacc.ui.adapter.MarginItemDecoration
import com.arduia.myacc.ui.mock.costList
import kotlin.random.Random
import kotlin.random.nextInt


class HomeFragment : BaseFragment(){

    private val viewBinding by lazy {
            FragHomeBinding.inflate(layoutInflater).apply { setupView() }
    }

    private val costAdapter by lazy { CostAdapter(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = viewBinding.root

    //Setup View
    private fun FragHomeBinding.setupView(){

        fbAdd.setColorFilter(Color.WHITE)

        btnMenuOpen.setOnClickListener { openDrawer() }

        tvMore.setOnClickListener {
            findNavController().navigate(R.id.dest_transaction)
        }

        rvRecent.adapter = costAdapter
        rvRecent.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        rvRecent.addItemDecoration(
            MarginItemDecoration( resources.getDimension(R.dimen.spacing_list_item).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            ))

        rvRecent.addOnScrollListener(object: RecyclerView.OnScrollListener(){

            private var totalScrollTop = 0
            private var currentScrollShift = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalScrollTop += dy
                currentScrollShift += dy

                when(totalScrollTop > 0){
                    //It is scrolling...
                    true -> dvRecent.visibility = View.VISIBLE
                    //If it is on start point hide it
                    false -> dvRecent.visibility = View.INVISIBLE
                }

                when(currentScrollShift > 10){
                    true -> fbAdd.hide()
                    false -> fbAdd.show()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    //Scrolling is over
                    currentScrollShift = 0
                }
            }
        })

        with(sheetEntry){
            btnEntryClose.setOnClickListener {

                mlHome.transitionToEnd()
            }

        }

        d("Home Fragment", "setupView")

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
