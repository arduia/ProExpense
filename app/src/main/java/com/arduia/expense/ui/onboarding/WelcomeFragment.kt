package com.arduia.expense.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arduia.expense.databinding.FragWelcomeBinding


class WelcomeFragment: Fragment(){

    private lateinit var viewBinding: FragWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewBinding = FragWelcomeBinding.inflate(layoutInflater, container, false)

        return viewBinding.root

    }


}