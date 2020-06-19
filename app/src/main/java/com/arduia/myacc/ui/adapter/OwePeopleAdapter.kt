package com.arduia.myacc.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.myacc.R
import com.arduia.myacc.databinding.ItemOweBinding
import com.arduia.myacc.databinding.ItemPeopleBinding
import com.arduia.myacc.ui.vto.OwePeopleVto

class OwePeopleAdapter(private val layoutInflater: LayoutInflater):
    ListAdapter<OwePeopleVto, OwePeopleAdapter.VH>( DIFF_CALLBACK){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = layoutInflater.inflate(R.layout.item_people,parent,false)
        return VH(ItemPeopleBinding.bind(view))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.binding){
            tvName.text = item.name
            tvPeopleValue.text = item.owe_value
        }
    }

    class VH(val binding:ItemPeopleBinding): RecyclerView.ViewHolder(binding.root)
}

private val DIFF_CALLBACK
get() = object :DiffUtil.ItemCallback<OwePeopleVto>(){
    override fun areItemsTheSame(oldItem: OwePeopleVto, newItem: OwePeopleVto)
    = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: OwePeopleVto, newItem: OwePeopleVto) =
        (oldItem.id == newItem.id) &&
        (oldItem.img_url == newItem.img_url) &&
        (oldItem.owe_value == newItem.owe_value)
}