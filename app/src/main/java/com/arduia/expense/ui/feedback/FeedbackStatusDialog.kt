package com.arduia.expense.ui.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.arduia.expense.R
import com.arduia.expense.databinding.FragFeedbackStatusDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FeedbackStatusDialog : BottomSheetDialogFragment() {

    private var _binding: FragFeedbackStatusDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragFeedbackStatusDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        setupGoHomeButton()
    }

    private fun setupGoHomeButton() {
        binding.goHome.setOnClickListener {
            popBackHome()
            dismiss()
        }

        binding.btnDrop.setOnClickListener {
            dismiss()
        }
    }

    private fun popBackHome() {
        findNavController().popBackStack(R.id.dest_home, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
