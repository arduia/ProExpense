package com.arduia.expense.ui.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.arduia.expense.R
import com.arduia.expense.databinding.FragFeedbackStatusDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FeedbackStatusDialog : BottomSheetDialogFragment(){
    private lateinit var viewBinding: FragFeedbackStatusDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewBinding(parent = container)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }
    private fun setupView(){
     setupGoHomeButton()
    }

    private fun setupGoHomeButton(){
        viewBinding.goHome.setOnClickListener {
            popBackHome()
            dismiss()
        }

        viewBinding.btnDrop.setOnClickListener {
            dismiss()
        }
    }

    private fun popBackHome(){
        findNavController().popBackStack(R.id.dest_home, false)
    }

    private fun initViewBinding(parent: ViewGroup?){
        viewBinding = FragFeedbackStatusDialogBinding.inflate(layoutInflater, parent, false)
    }
}
