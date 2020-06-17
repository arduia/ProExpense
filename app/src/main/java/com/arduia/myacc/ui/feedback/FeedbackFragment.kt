package com.arduia.myacc.ui.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arduia.myacc.databinding.FragFeedbackBinding
import com.arduia.myacc.ui.BaseFragment

class FeedbackFragment :BaseFragment(){

    private val viewBinding by lazy { FragFeedbackBinding.inflate(layoutInflater)}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =  viewBinding.root

}
