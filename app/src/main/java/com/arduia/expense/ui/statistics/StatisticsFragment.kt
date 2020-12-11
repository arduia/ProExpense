package com.arduia.expense.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.arduia.core.extension.px
import com.arduia.expense.R
import com.arduia.expense.databinding.FragStatisticBinding
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : NavBaseFragment() {

    private var _binding: FragStatisticBinding? = null
    private val binding get() = _binding!!

    private var categoryAdapter: CategoryStatisticListAdapter? = null

    private val viewModel by viewModels<StatisticsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragStatisticBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        categoryAdapter = CategoryStatisticListAdapter(layoutInflater)
        binding.tbStatistic.setNavigationOnClickListener {
            navigationDrawer.openDrawer()
        }
        binding.rvCategoryStatistics.adapter = categoryAdapter
        binding.rvCategoryStatistics.addItemDecoration(MarginItemDecoration(
            spaceHeight = resources.getDimension(R.dimen.grid_3).toInt()
        ))
    }

    private fun setupViewModel() {
        viewModel.categoryStatisticList.observe(viewLifecycleOwner) {
            categoryAdapter?.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvCategoryStatistics.adapter = null
        categoryAdapter = null
        _binding = null

    }

}