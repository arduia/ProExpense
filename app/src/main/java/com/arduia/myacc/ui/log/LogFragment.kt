package com.arduia.myacc.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arduia.myacc.databinding.FragLogBinding
import com.arduia.myacc.databinding.LayoutFeatureDetailBinding
import com.arduia.myacc.databinding.SheetEntryBinding
import com.arduia.myacc.ui.BaseFragment

class LogFragment : BaseFragment(){

    private val viewBinding by lazy { LayoutFeatureDetailBinding.inflate(layoutInflater)  }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  = viewBinding.root
}
