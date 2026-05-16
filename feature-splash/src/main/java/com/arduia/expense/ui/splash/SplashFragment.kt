package com.arduia.expense.ui.splash

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.arduia.expense.feature.splash.R
import com.arduia.expense.feature.splash.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val viewModel by viewModels<SplashViewModel>()

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private var normalStatusBarColor = Color.WHITE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeSplashStatusBarColor()
        collectEffects()
    }

    private fun changeSplashStatusBarColor() {
        val window = requireActivity().window
        normalStatusBarColor = window.statusBarColor
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue_light_500)
    }

    private fun restoreNormalStatusBarColor() {
        requireActivity().window.statusBarColor = normalStatusBarColor
    }

    private fun collectEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffect.collect { effect ->
                    when (effect) {
                        is SplashUiEffect.NavigateToOnboarding -> {
                            findNavController().popBackStack()
                            findNavController().navigate(R.id.dest_language)
                        }
                        is SplashUiEffect.NavigateToHome -> {
                            findNavController().popBackStack()
                            findNavController().navigate(R.id.dest_home)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        restoreNormalStatusBarColor()
    }
}
