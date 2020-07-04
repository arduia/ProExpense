package com.arduia.myacc.ui.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arduia.myacc.databinding.FragSettingsBinding
import com.arduia.myacc.ui.NavBaseFragment
import com.arduia.myacc.ui.NavigationDrawer

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
