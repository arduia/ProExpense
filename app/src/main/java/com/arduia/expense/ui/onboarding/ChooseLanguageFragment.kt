package com.arduia.expense.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.arduia.core.extension.px
import com.arduia.expense.R
import com.arduia.expense.databinding.FragChooseLanguageBinding
import com.arduia.expense.ui.common.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseLanguageFragment : Fragment() {

    private lateinit var binding: FragChooseLanguageBinding

    private val viewModel: ChooseLanguageViewModel by viewModels()

    private lateinit var adapter: LangListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragChooseLanguageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        adapter = LangListAdapter(layoutInflater)
        binding.rvLanguages.adapter = adapter
        binding.rvLanguages.itemAnimator = null
        binding.searchBox.setOnSearchTextChangeListener {
            viewModel.searchLang(it)
        }
        binding.rvLanguages. addItemDecoration(
            MarginItemDecoration(
                spaceSide = resources.getDimension(R.dimen.grid_3).toInt(),
                spaceHeight = requireContext().px(4)
            )
        )
        adapter.setOnItemClickListener(viewModel::selectLang)
    }

    private fun setupViewModel() {
        viewModel.language.observe(viewLifecycleOwner, adapter::submitList)
    }

}