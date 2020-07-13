package com.arduia.expense.ui.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arduia.expense.databinding.FragFeedbackBinding
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.NavigationDrawer

class FeedbackFragment : NavBaseFragment(){

    private lateinit var viewBinding: FragFeedbackBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        viewBinding = FragFeedbackBinding.inflate(layoutInflater)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }
    private fun  setupView(){
        viewBinding.btnDrawerOpen.setOnClickListener{
            (requireActivity() as? NavigationDrawer)?.openDrawer()
        }
    }

}
