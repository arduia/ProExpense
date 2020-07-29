package com.arduia.expense.ui.backup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.databinding.ItemBackupBinding
import com.arduia.expense.ui.vto.BackupVto

class BackupListAdapter(private val layoutInflater: LayoutInflater):
    ListAdapter<BackupVto,BackupListAdapter.VH>(DIFF_UTIL){

    private var itemClickListener = {_: BackupVto -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewBinding = ItemBackupBinding.inflate(layoutInflater)

        return VH(viewBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        with(holder.viewBinding){
            val item = getItem(position)

            tvBackupName.text = item.name
            tvDate.text = item.date
            tvItems.text = item.items
            pbStatus.visibility = if(item.onProgress) View.VISIBLE else View.INVISIBLE
            imvStatus.visibility = if(item.onProgress) View.INVISIBLE else View.VISIBLE

            cdBackup.setOnClickListener(holder)
        }
    }

    inner class VH(val viewBinding: ItemBackupBinding):
        RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener{

        override fun onClick(v: View?) {
            val item = getItem(adapterPosition)

            itemClickListener.invoke(item)
        }

    }

    fun setItemClickListener(listener: (BackupVto) -> Unit ){
        this.itemClickListener = listener
    }
}

private val DIFF_UTIL get() = object : DiffUtil.ItemCallback<BackupVto>(){
    override fun areItemsTheSame(oldItem: BackupVto, newItem: BackupVto): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: BackupVto, newItem: BackupVto): Boolean {
        return (oldItem.date == newItem.date) &&
                (oldItem.id == newItem.id) &&
                (oldItem.name == newItem.name) &&
                (oldItem.onProgress == newItem.onProgress) &&
                (oldItem.items == newItem.items)
    }
}