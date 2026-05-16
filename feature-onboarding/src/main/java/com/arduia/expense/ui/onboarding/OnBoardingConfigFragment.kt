package com.arduia.expense.ui.onboarding

import android.app.ActivityOptions
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arduia.expense.feature.onboarding.R
import com.arduia.expense.feature.onboarding.databinding.FragmentOnboardConfigBinding
import com.arduia.expense.ui.common.language.LanguageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingConfigFragment : Fragment() {

    private var _binding: FragmentOnboardConfigBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var languageProvider: LanguageProvider

    private val viewModel: OnBoardingConfigViewModel by viewModels()

    private lateinit var adapter: OnBoardingStateAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardConfigBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        collectEffects()
    }

    private fun setupView(){
        adapter = OnBoardingStateAdapter(this)
        binding.vpConfig.adapter = adapter
        binding.vpConfig.isUserInputEnabled = false
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        binding.btnContinue.setOnClickListener {
           viewModel.continued()
        }
    }

    private fun collectEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffect.collect { effect ->
                    when (effect) {
                        is OnBoardingConfigUiEffect.Restart -> restartActivity()
                        is OnBoardingConfigUiEffect.Continue -> onChooseCurrency()
                    }
                }
            }
        }
    }

    private fun onChooseCurrency(){
        with(binding){
            vpConfig.currentItem = 1
            btnContinue.text = getString(R.string.continue_home)
            btnContinue.setOnClickListener {
                viewModel.finishedConfig()
            }
        }
    }

    private fun restartActivity() {
        val currentActivity = requireActivity()
        val intent = currentActivity.intent
        currentActivity.finish()
        val animationBundle =
            ActivityOptions.makeCustomAnimation(
                requireContext(),
                R.anim.expense_enter_left, android.R.anim.fade_out
            ).toBundle()
        startActivity(intent, animationBundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
