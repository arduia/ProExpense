package com.arduia.myacc.ui.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arduia.myacc.databinding.FragFeedbackBinding
import com.arduia.myacc.ui.NavBaseFragment
import com.arduia.myacc.ui.NavigationDrawer

class FeedbackFragment : NavBaseFragment(){

    private val viewBinding by lazy {
        FragFeedbackBinding.inflate(layoutInflater).apply {
            setupView()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =  viewBinding.root

    private fun FragFeedbackBinding.setupView(){
        btnMenu.setOnClickListener{
            (requireActivity() as? NavigationDrawer)?.openDrawer()
        }
    }

}
