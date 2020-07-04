package com.arduia.expense.ui.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arduia.expense.databinding.FragSettingsBinding
import com.arduia.expense.ui.NavBaseFragment

class SettingsFragment: NavBaseFragment(){

    private val binding by lazy { FragSettingsBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupView()
    }

    private fun setupView(){

        binding.btnMenu.setOnClickListener{
            openDrawer()
        }

    }

}
