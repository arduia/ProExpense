package com.arduia.expense.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.databinding.ItemCurrencyBinding

class CurrencyListAdapter(private val layoutInflater: LayoutInflater) :
    ListAdapter<CurrencyUiModel, CurrencyListAdapter.VH>(
        DIFF_CALLBACK
    ) {

    private var onItemClickListener: (CurrencyUiModel) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCurrencyBinding.inflate(layoutInflater, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        with(holder.binding) {
            val item = getItem(position)
            tvCurrency.text = item.name
            tvSymbol.text = item.symbol
            imvSelected.visibility = item.isSelectionVisible
        }
    }

    fun setOnItemClickListener(listener: (CurrencyUiModel) -> Unit) {
        this.onItemClickListener = listener
    }

    inner class VH(val binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cvCurrency.setOnClickListener {
                onItemClickListener.invoke(getItem(adapterPosition))
            }
        }
    }
}

private val DIFF_CALLBACK
    get() = object : DiffUtil.ItemCallback<CurrencyUiModel>() {
        override fun areItemsTheSame(oldItem: CurrencyUiModel, newItem: CurrencyUiModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CurrencyUiModel, newItem: CurrencyUiModel): Boolean {
            return (oldItem.name == newItem.name) and
                    (oldItem.number == newItem.number) and
                    (oldItem.symbol == newItem.symbol) and
                    (oldItem.isSelectionVisible == newItem.isSelectionVisible)
        }
    }