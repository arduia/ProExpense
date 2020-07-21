package com.arduia.expense.ui.language

import android.app.ActivityOptions
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.arduia.expense.R
import com.arduia.expense.databinding.FragLanguageBinding
import com.arduia.expense.ui.common.LanguageProvider
import com.arduia.expense.ui.common.LanguageProviderImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LanguageFragment: Fragment(){

    private lateinit var viewBinding: FragLanguageBinding

    private lateinit var languageListAdapter: LanguageListAdapter

    private val viewModel by viewModels<LanguageViewModel>()

    @Inject
    lateinit var languageProvider: LanguageProvider

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

        languageListAdapter.languageLists = languageProvider.getAvailableLanguages()

        languageListAdapter.setOnItemClickListener {
            viewModel.selectLanguage(it.id)
        }

        viewBinding.btnContinue.setOnClickListener {
            viewModel.continueHome()
        }

    }

    private fun setupViewModel(){

        viewModel.selectedLanguage.observe(viewLifecycleOwner, Observer {
            val selectedLang = languageProvider.getLanguageVtoByID(it)
            languageListAdapter.selectedLanguage = selectedLang
        })

        viewModel.continueEvent.observe(viewLifecycleOwner, Observer {
             restartActivity()
        })

    }
    private fun restartActivity(){
        val currentActivity = requireActivity()
        val intent = currentActivity.intent
        currentActivity.finish()
        val animationBundle =
            ActivityOptions.makeCustomAnimation(requireContext(),
                R.anim.expense_enter_left, android.R.anim.fade_out).toBundle()
        startActivity(intent, animationBundle)
    }

}
