package com.arduia.expense.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arduia.core.extension.px
import com.arduia.expense.feature.onboarding.R
import com.arduia.expense.feature.onboarding.databinding.FragmentChooseCurrencyBinding
import com.arduia.expense.ui.common.helper.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChooseCurrencyFragment : Fragment() {

    private var _binding: FragmentChooseCurrencyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChooseCurrencyViewModel by viewModels()

    private lateinit var adapter: CurrencyListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        collectState()
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

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.currencies)
                    binding.tvNoItem.visibility = if (state.currencies.isEmpty()) View.VISIBLE else View.INVISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
