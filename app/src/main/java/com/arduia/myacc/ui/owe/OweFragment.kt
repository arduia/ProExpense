package com.arduia.myacc.ui.owe

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arduia.myacc.R
import com.arduia.myacc.databinding.FragOweBinding
import com.arduia.myacc.ui.BaseFragment
import com.arduia.myacc.ui.adapter.OwePagerAdapter
import com.arduia.myacc.ui.vto.OweLogVto
import com.arduia.myacc.ui.vto.OwePeopleVto
import com.google.android.material.tabs.TabLayoutMediator

class OweFragment: BaseFragment(){

    private val viewBinding by lazy { FragOweBinding.inflate(layoutInflater) }
    private val owePagerAdapter by lazy { OwePagerAdapter(layoutInflater) }

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

        viewBinding.fbAdd.setColorFilter(Color.WHITE)

        viewBinding.btnOpen.setOnClickListener {
            openDrawer()
        }

        viewBinding.vpOwe.adapter = owePagerAdapter

        owePagerAdapter.oweLogs = getSampleOweLog()
        owePagerAdapter.peopleList = getSampleOwePeople()

        TabLayoutMediator(viewBinding.tabLayout,viewBinding.vpOwe){tab, position ->
            tab.text = getString(
                when(position){
                0       -> R.string.label_owe_log
                else    -> R.string.label_owe_people
            })
        }.attach()

    }

    private fun getSampleOweLog() = mutableListOf<OweLogVto>().apply {
        add(OweLogVto("1","Kyaw Win Htun","20/2/2020","-60,000"))
        add(OweLogVto("2","Win Naing Soe","11/2/2020","+5,000"))
    }

    private fun getSampleOwePeople() = mutableListOf<OwePeopleVto>().apply {
        add(OwePeopleVto("1","Win Naing Soe","Win Naing Soe","-5000"))
        add(OwePeopleVto("2","Kyaw Win Htun","Kyaw Win Htun","+60000"))
    }

}
