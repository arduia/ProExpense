package com.arduia.expense.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.arduia.core.extension.px
import com.arduia.expense.R
import com.arduia.expense.databinding.FragChooseCurrencyBinding
import com.arduia.expense.ui.common.helper.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseCurrencyFragment : Fragment() {

    private lateinit var binding: FragChooseCurrencyBinding

    private val viewModel: ChooseCurrencyViewModel by viewModels()

    private lateinit var adapter: CurrencyListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragChooseCurrencyBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        adapter = CurrencyListAdapter(layoutInflater)
            .apply {
                setOnItemClickListener(viewModel::selectCurrency)
            }
        val spaceDeco = MarginItemDecoration(
            spaceHeight = requireContext().px(4),
            spaceSide = requireContext().px(resources.getDimension(R.dimen.grid_2).toInt())
        )
        binding.rvCurrencies.adapter = adapter
        binding.rvCurrencies.addItemDecoration(spaceDeco)
        binding.rvCurrencies.itemAnimator = null
        binding.searchBox.setOnSearchTextChangeListener {
            viewModel.searchCurrency(it)
        }
    }

    private fun setupViewModel() {
        viewModel.currencies.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.tvNoItem.visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
        }
    }

}