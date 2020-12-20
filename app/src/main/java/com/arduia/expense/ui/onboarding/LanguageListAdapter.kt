package com.arduia.expense.ui.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.databinding.ItemLanguageBinding
import com.arduia.expense.ui.vto.LanguageVto

class LanguageListAdapter(private val layoutInflater: LayoutInflater) :
    RecyclerView.Adapter<LanguageListAdapter.VH>() {

    var languageLists = listOf<LanguageVto>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedLanguage: LanguageVto? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var itemClickListener: (LanguageVto) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val viewBinding = ItemLanguageBinding.inflate(layoutInflater, parent, false)

        return VH(viewBinding)
    }

    override fun getItemCount(): Int = languageLists.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        with(holder.viewBinding) {
            val item = languageLists[position]

            imvFlag.setImageResource(item.flag)
            tvLanguageName.text = item.name

            selectedLanguage?.let {
                imvChecked.visibility =
                    when (it.id == item.id) {
                        true -> View.VISIBLE
                        false -> View.INVISIBLE
                    }
            }
        }
    }

    fun setOnItemClickListener(listener: (LanguageVto) -> Unit) {
        this.itemClickListener = listener
    }

    inner class VH(val viewBinding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener {
        init {
            viewBinding.cdLanguage.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemClickListener.invoke(languageLists[adapterPosition])
        }
    }
}