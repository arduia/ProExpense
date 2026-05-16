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
import com.arduia.expense.feature.onboarding.databinding.FragmentChooseLanguageBinding
import com.arduia.expense.ui.common.helper.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChooseLanguageFragment : Fragment() {

    private var _binding: FragmentChooseLanguageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChooseLanguageViewModel by viewModels()

    private lateinit var adapter: LangListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseLanguageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        collectState()
    }

    private fun setupView() {
        adapter = LangListAdapter(layoutInflater)
        binding.rvLanguages.adapter = adapter
        binding.rvLanguages.itemAnimator = null
        binding.searchBox.setOnSearchTextChangeListener {
            viewModel.searchLang(it)
        }
        binding.rvLanguages.addItemDecoration(
            MarginItemDecoration(
                spaceSide = resources.getDimension(R.dimen.grid_3).toInt(),
                spaceHeight = requireContext().px(4)
            )
        )
        adapter.setOnItemClickListener(viewModel::selectLang)
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.languages)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
