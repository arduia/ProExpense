package com.arduia.myacc.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arduia.myacc.databinding.ItemFeaturePreviewBinding
import com.arduia.myacc.ui.BaseFragment

class LogFragment : BaseFragment(){

    private val viewBinding by lazy { ItemFeaturePreviewBinding.inflate(layoutInflater)  }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  = viewBinding.root

}
