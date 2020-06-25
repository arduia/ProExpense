package com.arduia.myacc.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.myacc.R
import com.arduia.myacc.databinding.ItemCostBinding
import com.arduia.myacc.ui.vto.CostCategory
import com.arduia.myacc.ui.vto.CostVto
import javax.inject.Inject

class CostAdapter @Inject constructor(private val layoutInflater: LayoutInflater):
    ListAdapter<CostVto, CostAdapter.VH>(DIFF_CALLBACK){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val itemView = layoutInflater.inflate(R.layout.item_cost, parent, false)

        return VH(ItemCostBinding.bind(itemView))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val item = getItem(position)

        with(holder.binding){

           tvName.text = item.name
           tvDate.text = item.date
           tvFiance.text = item.finance

           val imgRes = when(item.cateogry){

               CostCategory.CLOTHES         -> R.drawable.ic_clothes
               CostCategory.HOUSEHOLD       -> R.drawable.ic_household
               CostCategory.TRANSPORTATION  -> R.drawable.ic_transportation
               CostCategory.FOOD            -> R.drawable.ic_food
               CostCategory.UTILITIES       -> R.drawable.ic_utities
               CostCategory.HEARTHCARE      -> R.drawable.ic_healthcare
               CostCategory.SOCIAL          -> R.drawable.ic_social
               CostCategory.EDUCATION       -> R.drawable.ic_education
               CostCategory.DONATIONS       -> R.drawable.ic_donations
               CostCategory.ENTERTAINMENT   -> R.drawable.ic_entertainment
               CostCategory.INCOME          -> R.drawable.ic_borrow

           }

           tvValue.text = item.cost

           imgType.setImageResource(imgRes)
       }

    }

    class VH(val binding: ItemCostBinding): RecyclerView.ViewHolder(binding.root)

}

private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<CostVto>(){
    override fun areItemsTheSame(oldItem: CostVto, newItem: CostVto): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CostVto, newItem: CostVto): Boolean {
        return  oldItem.name == newItem.name &&
                oldItem.cateogry == newItem.cateogry &&
                oldItem.cost == newItem.cost &&
                oldItem.date == newItem.date &&
                oldItem.finance == newItem.finance
    }
}
