package com.arduia.expense.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.arduia.expense.R
import com.arduia.expense.databinding.FragOnboardBinding
import com.arduia.expense.ui.NavigationDrawer

class OnboardingFragment : Fragment(){

    private lateinit var viewBinding: FragOnboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragOnboardBinding.inflate(layoutInflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lockDrawer()
        setupView()
    }

    private fun setupView(){

        viewBinding.vpOnboard.adapter =  OnBoardingPagerAdapter(layoutInflater)

        viewBinding.diDotIndicator.setViewPager2(viewBinding.vpOnboard)

        viewBinding.btnSkip.setOnClickListener {
            viewBinding.vpOnboard.currentItem = 1
        }

        viewBinding.vpOnboard.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position  ){
                    0 -> {
                        viewBinding.btnNext.text = getString(R.string.label_next)
                        viewBinding.btnSkip.visibility = View.VISIBLE
                        viewBinding.btnNext.setOnClickListener {
                            viewBinding.vpOnboard.currentItem += 1
                        }
                    }

                    else    -> {
                        viewBinding.btnNext.text = getString(R.string.label_start_now)
                        viewBinding.btnSkip.visibility = View.INVISIBLE
                        viewBinding.btnNext.setOnClickListener {
                            findNavController().popBackStack()
                            findNavController().navigate(R.id.dest_home)
                        }
                    }
                }
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        unlockDrawer()
    }
    private fun lockDrawer(){
        (requireActivity() as? NavigationDrawer)?.lockDrawer(true)
    }

    private fun unlockDrawer(){
        (requireActivity() as? NavigationDrawer)?.lockDrawer(false)
    }

}