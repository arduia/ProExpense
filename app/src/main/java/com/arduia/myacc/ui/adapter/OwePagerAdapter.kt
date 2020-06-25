package com.arduia.myacc.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arduia.myacc.R
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

//            Top Padding between List View top and first item
            clipToPadding = false
            setPadding(
                paddingLeft,
                resources.getDimension(R.dimen.material_margin).toInt(),
                paddingRight,
                paddingBottom)
            overScrollMode = ScrollView.OVER_SCROLL_NEVER
        }

        return VH(recyclerView)
    }

    override fun getItemCount() = 2

    override fun onBindViewHolder(holder: VH, position: Int) {

        with(holder.view){

            //Recycler view's Adapter Assignment
            adapter = when(position){

                0 -> {
                    //First Adapter Use Item Decoration
                    val marginDecorator = MarginItemDecoration(
                        resources.getDimension(R.dimen.spacing_list_item).toInt(),
                        resources.getDimension(R.dimen.margin_list_item).toInt())

                    //Add Decorator to view
                    addItemDecoration(marginDecorator)

                    //Assign Adapter Class
                    oweLogAdapter
                }

                1 -> peopleAdapter

                //No more type so return
                else -> return
            }
        }

    }

    class VH(val view:RecyclerView): RecyclerView.ViewHolder(view)

}