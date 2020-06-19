package com.arduia.myacc.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arduia.myacc.ui.vto.OweLogVto
import com.arduia.myacc.ui.vto.OwePeopleVto

class OwePagerAdapter(private val layoutInflater: LayoutInflater):
    RecyclerView.Adapter<OwePagerAdapter.VH>(){

    private val oweLogAdapter by lazy { OweLogAdapter(layoutInflater) }
    private val peopleAdapter by lazy { OwePeopleAdapter(layoutInflater) }

    var oweLogs: MutableList<OweLogVto> = oweLogAdapter.currentList
    set(value) { oweLogAdapter.submitList(value) }

    var peopleList: MutableList<OwePeopleVto> = peopleAdapter.currentList
    set(value) { peopleAdapter.submitList(value) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val recyclerView = RecyclerView(parent.context).apply {
            layoutManager = LinearLayoutManager(parent.context, RecyclerView.VERTICAL, false)
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        return VH(recyclerView)
    }

    override fun getItemCount() = 2

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.view.adapter = when(position){
            0       -> oweLogAdapter
            1       -> peopleAdapter
            else    -> return
        }
    }

    class VH(val view:RecyclerView): RecyclerView.ViewHolder(view)

}