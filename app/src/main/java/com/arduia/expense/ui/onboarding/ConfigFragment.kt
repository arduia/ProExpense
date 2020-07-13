package com.arduia.expense.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arduia.expense.databinding.FragConfigureBinding

class ConfigFragment: Fragment(){

    private lateinit var viewBinding: FragConfigureBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewBinding = FragConfigureBinding.inflate(layoutInflater)

        return viewBinding.root

    }


}