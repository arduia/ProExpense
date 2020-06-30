package com.arduia.myacc.ui.owe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.myacc.R
import com.arduia.myacc.databinding.ItemOweBinding
import com.arduia.myacc.ui.vto.OweLogVto

class OweLogAdapter(private val layoutInflater: LayoutInflater):
    ListAdapter<OweLogVto, OweLogAdapter.VH>(
        DIFF_CALLBACK
    ){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val view = layoutInflater.inflate(R.layout.item_owe, parent, false)

        return VH(ItemOweBinding.bind(view))
    }

    override fun onBindViewHolder(holder: VH, position: Int){

        val item = getItem(position)

        with(holder.binding){
            tvOweDate.text = item.date
            tvOweName.text = item.name
            tvOweValue.text = item.value
        }

    }

    class VH(val binding:ItemOweBinding): RecyclerView.ViewHolder(binding.root)
}

private val  DIFF_CALLBACK
    get() = object: DiffUtil.ItemCallback<OweLogVto>(){
    override fun areItemsTheSame(oldItem: OweLogVto, newItem: OweLogVto) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: OweLogVto, newItem: OweLogVto) =
                (oldItem.id == newItem.id) &&
                (oldItem.date == oldItem.date) &&
                (oldItem.name == newItem.name) &&
                (oldItem.value == newItem.value)

}