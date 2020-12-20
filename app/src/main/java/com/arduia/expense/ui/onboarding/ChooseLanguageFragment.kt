package com.arduia.expense.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.arduia.expense.databinding.FragChooseLanguageBinding
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
        adapter.setOnItemClickListener(viewModel::selectLang)
    }

    private fun setupViewModel() {
        viewModel.language.observe(viewLifecycleOwner, adapter::submitList)
    }

}