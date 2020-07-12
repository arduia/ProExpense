package com.arduia.expense.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arduia.expense.databinding.FragAboutBinding
import com.arduia.expense.ui.NavBaseFragment

class AboutFragment : NavBaseFragment(){

    private lateinit var viewBinding: FragAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragAboutBinding.inflate(layoutInflater)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView(){

        viewBinding.btnDrawerOpen.setOnClickListener {
            openDrawer()
        }



    }

}
