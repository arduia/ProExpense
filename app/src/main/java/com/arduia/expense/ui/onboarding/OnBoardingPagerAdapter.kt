package com.arduia.expense.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.lang.IllegalStateException

class OnBoardingPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fm, lifecycle){

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
        when(position){

            0 -> WelcomeFragment()

            1 -> ConfigFragment()

            else -> throw IllegalStateException("No Fragment Found at position $position")
        }

}