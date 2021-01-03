package com.arduia.expense.ui.onboarding

import android.app.ActivityOptions
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.arduia.expense.R
import com.arduia.expense.databinding.FragOnboardConfigBinding
import com.arduia.expense.ui.common.language.LanguageProvider
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingConfigFragment : Fragment() {

    private lateinit var binding: FragOnboardConfigBinding

    @Inject
    lateinit var languageProvider: LanguageProvider

    private val viewModel: OnBoardingConfigViewModel by viewModels()

    private lateinit var adapter: OnBoardingStateAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragOnboardConfigBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
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

    private fun setupViewModel(){
        viewModel.onRestart.observe(viewLifecycleOwner, EventObserver{
            restartActivity()
        })
        viewModel.onContinued.observe(viewLifecycleOwner,EventObserver{
            onChooseCurrency()
        })
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

}
