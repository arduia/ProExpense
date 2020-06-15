package com.arduia.myacc.ui.owe

import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arduia.myacc.R
import com.arduia.myacc.databinding.FragOweBinding
import com.arduia.myacc.ui.BaseFragment

class OweFragment: BaseFragment(){

    private val viewBinding by lazy { FragOweBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = viewBinding.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
    }

    private fun setupView(){
        viewBinding.fabAdd.setColorFilter(Color.WHITE)
        viewBinding.btnOpen.setOnClickListener {
            openDrawer()
        }
    }

}
