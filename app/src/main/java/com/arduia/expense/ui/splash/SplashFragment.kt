package com.arduia.expense.ui.splash

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.arduia.core.extension.px
import com.arduia.expense.R
import com.arduia.expense.databinding.FragSplashBinding
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val viewModel by viewModels<SplashViewModel>()

    private lateinit var binding: FragSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragSplashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.blue_light_500)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.firstTimeEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
            findNavController().navigate(R.id.dest_language)
        })
        viewModel.normalUserEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
            findNavController().navigate(R.id.dest_home)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.statusBarColor = Color.TRANSPARENT
    }

}
