package com.arduia.expense.ui.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.arduia.expense.R
import com.arduia.expense.databinding.FragLanguageBinding
import com.arduia.expense.ui.common.LanguageProviderImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import timber.log.Timber

class LanguageFragment: Fragment(){

    private lateinit var viewBinding: FragLanguageBinding

    private lateinit var languageListAdapter: LanguageListAdapter

    private val viewModel by viewModels<LanguageViewModel>()

    private val langProvider by lazy {
        LanguageProviderImpl()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragLanguageBinding.inflate(layoutInflater, container, false)

        return viewBinding.root
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.addObserver(viewModel)
        setupView()
        setupViewModel()
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun setupView(){

        languageListAdapter =
            LanguageListAdapter(layoutInflater)

        viewBinding.rvLanguages.adapter = languageListAdapter

        languageListAdapter.languageLists = langProvider.getAvailableLanguages()

        languageListAdapter.setOnItemClickListener {
            viewModel.selectLanguage(it.id)
        }

        viewBinding.btnContinue.setOnClickListener {
            viewModel.continueHome()
        }

    }

    private fun setupViewModel(){

        viewModel.selectedLanguage.observe(viewLifecycleOwner, Observer {
            val selectedLang = langProvider.getLanguageVtoByID(it)
            languageListAdapter.selectedLanguage = selectedLang
            Timber.d("selected Language -> $it")
        })

        viewModel.continueEvent.observe(viewLifecycleOwner, Observer {
            requireActivity().recreate()
            findNavController().popBackStack()
            findNavController().navigate(R.id.dest_home)
        })
    }

}