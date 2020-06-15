package com.arduia.myacc.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arduia.myacc.R
import com.arduia.myacc.databinding.ItemCostBinding
import com.arduia.myacc.ui.vto.CostCategory
import com.arduia.myacc.ui.vto.CostVto
import javax.inject.Inject

class CostAdapter @Inject constructor(private val layoutInflater: LayoutInflater):
    RecyclerView.Adapter<CostAdapter.CostViewHolder>(){

    var listItem = listOf<CostVto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostViewHolder {
        val itemView = layoutInflater.inflate(R.layout.item_cost,parent,false)
        return CostViewHolder(ItemCostBinding.bind(itemView))
    }

    override fun getItemCount() = listItem.size

    override fun onBindViewHolder(holder: CostViewHolder, position: Int) {
        val item = listItem[position]

        with(holder.binding){

           tvName.text = item.name
           tvDate.text = item.date
           tvFiance.text = item.finance

           val imgRes = when(item.cateogry){
               CostCategory.CLOTHES -> R.drawable.ic_clothes
               CostCategory.HOUSEHOLD -> R.drawable.ic_household
               CostCategory.TRANSPORTATION -> R.drawable.ic_transportation
               CostCategory.FOOD -> R.drawable.ic_food
               CostCategory.UTILITIES -> R.drawable.ic_utities
               CostCategory.HEARTHCARE -> R.drawable.ic_healthcare
               CostCategory.SOCIAL -> R.drawable.ic_social
               CostCategory.EDUCATION -> R.drawable.ic_education
               CostCategory.DONATIONS -> R.drawable.ic_donations
               CostCategory.ENTERTAINMENT -> R.drawable.ic_entertainment
               CostCategory.INCOME ->{
                   R.drawable.ic_borrow
               }
           }

//           if(item.cateogry != CostCategory.INCOME){
//               tvValue.setTextColor(Color.RED)
//           }

           tvValue.text = item.cost
           imgType.setImageResource(imgRes)

       }
    }

    class CostViewHolder(val binding:ItemCostBinding):RecyclerView.ViewHolder(binding.root)

}
