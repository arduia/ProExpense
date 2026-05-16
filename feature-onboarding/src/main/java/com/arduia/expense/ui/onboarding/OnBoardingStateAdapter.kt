package com.arduia.expense.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnBoardingStateAdapter(parent: Fragment): FragmentStateAdapter(parent){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ChooseLanguageFragment()
            else -> ChooseCurrencyFragment()
        }
    }
}