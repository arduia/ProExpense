package com.arduia.myacc.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arduia.myacc.NavigationDrawer
import com.arduia.myacc.databinding.FragHomeBinding
import com.arduia.myacc.ui.adapter.CostAdapter
import com.arduia.myacc.ui.mock.costList
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


class HomeFragment : Fragment(){

    private val viewBinding by lazy { FragHomeBinding.inflate(layoutInflater) }
    private val navDrawer:NavigationDrawer? by lazy { requireActivity() as? NavigationDrawer  }

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
        viewBinding.btnClose.setOnClickListener { navDrawer?.openDrawer()  }
        viewBinding.rvRecent.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        viewBinding.rvRecent.adapter = costAdapter
        costAdapter.listItem = costList()
    }

}
